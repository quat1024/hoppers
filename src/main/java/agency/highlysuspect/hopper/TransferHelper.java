package agency.highlysuspect.hopper;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

import javax.annotation.Nullable;

public class TransferHelper {
	/**
	 * Returns true if the ItemStacks are mergeable with each other in the intuitive sense.
	 */
	public static boolean canStack(@Nullable ItemStack a, @Nullable ItemStack b) {
		//Well you can stack anything onto an empty slot.
		if(a == null || b == null) return true;
		//Otherwise the items, damages, and tags must be equal.
		return a.itemID == b.itemID && a.getItemDamage() == b.getItemDamage() && objectsEquals(a.getTagCompound(), b.getTagCompound());
	}
	
	/**
	 * Inserts the ItemStack into the IInventory by iterating over its slots and putting the item in the first slots where it fits.
	 * Returns how many items were inserted into the IInventory. Mutates the the IInventory, but does not mutate `stack`.
	 * An additional "maxCount" parameter is provided for convenience; this limits the amount of items that can be transferred.
	 */
	public static int insert(ItemStack stack, int maxCount, IInventory inventory, int slotStart, int slotEndExclusive) {
		int availableToInsertStart = Math.min(maxCount, stack.stackSize);
		int availableToInsert = availableToInsertStart;
		
		for(int slot = slotStart; slot < slotEndExclusive; slot++) {
			@Nullable ItemStack stackHere = inventory.getStackInSlot(slot);
			if(stackHere != null && stackHere.stackSize == 0) stackHere = null; //I miss ItemStack.EMPTY.
			
			//How many items can potentially go into this slot.
			int slotInsertCapacity;
			if(stackHere == null) {
				//Empty stack in the container
				slotInsertCapacity = Math.min(availableToInsert, inventory.getInventoryStackLimit());
			} else if(canStack(stack, stackHere)){
				//Matching stack in the container
				slotInsertCapacity = Math.min(Math.min(availableToInsert, inventory.getInventoryStackLimit()), stackHere.getMaxStackSize() - stackHere.stackSize);
			} else {
				//Nonmatching stack in the container
				slotInsertCapacity = 0;
			}
			
			if(slotInsertCapacity <= 0) continue;
			
			if(stackHere == null) {
				//Empty stack in the container, fill it with a new itemstack.
				ItemStack newStack = stack.copy();
				newStack.stackSize = slotInsertCapacity;
				inventory.setInventorySlotContents(slot, newStack);
			} else {
				//If we got this far the itemstack has to match, so it should be safe to increase the size of the stack directly.
				stackHere.stackSize += slotInsertCapacity;
				inventory.setInventorySlotContents(slot, stackHere); //to fire change listeners, etc
			}
			
			availableToInsert -= slotInsertCapacity;
		}
		
		return availableToInsertStart - availableToInsert;
	}
	
	public static int insert(ItemStack stack, int maxCount, IInventory inventory) {
		return insert(stack, maxCount, inventory, 0, inventory.getSizeInventory());
	}
	
	public static int insert(ItemStack stack, int maxCount, IInventory maybeSidedInventory, ForgeDirection side) {
		//If the inventory is sided, make sure to only allow insertion into the appropriate window of the inventory
		if(maybeSidedInventory instanceof ISidedInventory) {
			ISidedInventory sided = (ISidedInventory) maybeSidedInventory;
			int start = sided.getStartInventorySide(side);
			return insert(stack, maxCount, maybeSidedInventory, start, start + sided.getSizeInventorySide(side));
		} else return insert(stack, maxCount, maybeSidedInventory);
	}
	
	public static boolean hopperPushInto(IInventory depositInventory, ForgeDirection depositSide, IInventory hopperInventory) {
		for(int i = 0; i < hopperInventory.getSizeInventory(); i++) {
			@Nullable ItemStack movingStack = hopperInventory.getStackInSlot(i);
			if(movingStack != null && movingStack.stackSize == 0) movingStack = null;
			if(movingStack == null) continue;
			
			//Try to transfer one of the item stack into the deposit inventory.
			int howManyMoved = insert(movingStack, 1, depositInventory, depositSide);
			if(howManyMoved != 0) {
				hopperInventory.decrStackSize(i, howManyMoved);
				
				//TODO: Is this the right place to put this special consideration for hoppers
				if(hopperInventory instanceof TileHopper) {
					((TileHopper) hopperInventory).transferCooldown = 9;
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean hopperPullFrom(IInventory collectionInventory, ForgeDirection collectionSide, IInventory hopperInventory) {
		int collectionStart = 0, collectionEnd = collectionInventory.getSizeInventory();
		if(collectionInventory instanceof ISidedInventory) {
			collectionStart = ((ISidedInventory) collectionInventory).getStartInventorySide(collectionSide);
			collectionEnd = collectionStart + ((ISidedInventory) collectionInventory).getSizeInventorySide(collectionSide);
			if(collectionEnd - collectionStart == 0) return false;
		}
		
		for(int i = collectionStart; i < collectionEnd; i++) {
			@Nullable ItemStack movingStack = collectionInventory.getStackInSlot(i);
			if(movingStack != null && movingStack.stackSize == 0) movingStack = null;
			if(movingStack == null) continue;
			
			//Try to transfer one of the itemstacks into the hopper.
			int howManyMoved = insert(movingStack, 1, hopperInventory);
			if(howManyMoved != 0) {
				collectionInventory.decrStackSize(i, howManyMoved);
				return true;
			}
		}
		
		return false;
	}
	
	//Funny name is because this is a backport of Objects.equals() from Java 8. The future is now!
	@SuppressWarnings("ConstantConditions") //simplifying breaks the symmetry!
	public static boolean objectsEquals(Object a, Object b) {
		if(a == null) return b == null;
		if(b == null) return a == null;
		else return a.equals(b);
	}
}
