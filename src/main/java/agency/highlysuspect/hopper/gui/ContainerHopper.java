package agency.highlysuspect.hopper.gui;

import agency.highlysuspect.hopper.TileEntityHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerHopper extends Container {
	public ContainerHopper(InventoryPlayer playerInventory, TileEntityHopper hopper) {
		this.hopper = hopper;
		
		//hopper slots
		hopperSlotCount = hopper.getSizeInventory();
		int rowWidth = hopperSlotCount * 18;
		int rowStart = 89 - (rowWidth / 2);
		for(int i = 0; i < hopper.getSizeInventory(); i++) {
			addSlotToContainer(new Slot(hopper, i, rowStart + 18 * i, 20));
		}
		
		//player inventory (lotsa magic numbers borrowed from ContainerDispenser)
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 9; col++) {
				addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
			}
		}
		for(int hotbarCol = 0; hotbarCol < 9; hotbarCol++) {
			addSlotToContainer(new Slot(playerInventory, hotbarCol, 8 + hotbarCol * 18, 109));
		}
		
		totalSlotCount = inventorySlots.size();
	}
	
	private final TileEntityHopper hopper;
	private final int hopperSlotCount;
	private final int totalSlotCount;
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return hopper.isUseableByPlayer(player);
	}
	
	@SuppressWarnings("UnnecessaryLocalVariable") //gotta label stuff.
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int shiftedSlotId) {
		ItemStack result = null;
		Slot shiftedSlot = (Slot) inventorySlots.get(shiftedSlotId); //Lol no generics
		if(shiftedSlot != null && shiftedSlot.getHasStack()) {
			ItemStack slotContents = shiftedSlot.getStack();
			result = slotContents.copy();
			
			if(shiftedSlotId < hopperSlotCount) {
				int start = hopperSlotCount;
				int end = totalSlotCount;
				if(!mergeItemStack(slotContents, start, end, true)) return null;
			} else {
				int start = 0;
				int end = hopperSlotCount;
				if(!mergeItemStack(slotContents, start, end, false)) return null;
			}
			
			//Copied, again, out of the dispenser code. Not sure exactly what it does.
			//Containers! Fun.
			if(slotContents.stackSize == 0) shiftedSlot.putStack(null);
			else shiftedSlot.onSlotChanged();
			
			if(slotContents.stackSize == result.stackSize) return null;
			
			shiftedSlot.onPickupFromSlot(player, slotContents);
		}
		
		return result;
	}
}
