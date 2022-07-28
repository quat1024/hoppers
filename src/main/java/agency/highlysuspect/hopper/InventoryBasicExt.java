package agency.highlysuspect.hopper;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryBasicExt extends InventoryBasic {
	public InventoryBasicExt(String name, int slotCount) {
		super(name, slotCount);
	}
	
	public NBTTagList writeToNBT() {
		NBTTagList list = new NBTTagList();
		for(int i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
			if(stack == null) continue;
			
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("Slot", i);
			stack.writeToNBT(tag);
			list.appendTag(tag);
		}
		
		return list;
	}
	
	public void readFromNBT(NBTTagList list) {
		for(int i = 0; i < getSizeInventory(); i++) setInventorySlotContents(i, null); //Clear existing contents
		
		for(int tagId = 0; tagId < list.tagCount(); tagId++) {
			NBTBase baseTag = list.tagAt(tagId);
			if(baseTag instanceof NBTTagCompound) {
				NBTTagCompound tag = (NBTTagCompound) baseTag;
				setInventorySlotContents(tag.getInteger("Slot"), ItemStack.loadItemStackFromNBT(tag));
			}
		}
	}
}
