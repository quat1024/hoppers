package agency.highlysuspect.hopper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileHopper extends TileEntity implements IInventory {
	InventoryBasicExt inv = new InventoryBasicExt("container.hopper", 5) {
		@Override
		public void onInventoryChanged() {
			TileHopper.this.onInventoryChanged();
		}
	};
	
	@Override
	public boolean shouldRefresh(int oldID, int newID, int oldMeta, int newMeta, World world, int x, int y, int z) {
		//Changes to metadata don't invalidate the block.
		return oldID != newID;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("Items", inv.writeToNBT());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inv.readFromNBT(tag.getTagList("Items"));
	}
	
	//Auto-generated delegations
	@Override
	public ItemStack getStackInSlot(int i) {return inv.getStackInSlot(i);}
	
	@Override
	public ItemStack decrStackSize(int i, int j) {return inv.decrStackSize(i, j);}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i) {return inv.getStackInSlotOnClosing(i);}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {inv.setInventorySlotContents(i, itemStack);}
	
	@Override
	public int getSizeInventory() {return inv.getSizeInventory();}
	
	@Override
	public String getInvName() {return inv.getInvName();}
	
	@Override
	public int getInventoryStackLimit() {return inv.getInventoryStackLimit();}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityPlayer) {return inv.isUseableByPlayer(entityPlayer);}
	
	@Override
	public void openChest() {inv.openChest();}
	
	@Override
	public void closeChest() {inv.closeChest();}
}
