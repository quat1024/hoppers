package agency.highlysuspect.hopper;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import javax.annotation.Nullable;
import java.util.List;

public class TileHopper extends TileEntity implements IInventory {
	private final InventoryBasicExt inv = new InventoryBasicExt("container.hopper", 5) {
		@Override
		public void onInventoryChanged() {
			TileHopper.this.onInventoryChanged();
		}
	};
	
	public int transferCooldown = 8;
	
	// Ticking
	
	@Override
	public void updateEntity() {
		if(worldObj == null || worldObj.isRemote) return;
		
		if(!(getBlockType() instanceof BlockHopper)) return;
		BlockHopper blockHopper = (BlockHopper) getBlockType();
		
		if(blockHopper.poweredFromMeta(getBlockMetadata())) {
			transferCooldown = 0;
			return;
		}
		
		boolean allowInventoryInteraction = true;
		boolean performedAnyInventoryInteraction = false;
		if(transferCooldown > 0) {
			transferCooldown--;
			allowInventoryInteraction = false;
		}
		
		ForgeDirection hopperDir = blockHopper.directionFromMeta(getBlockMetadata());
		AnachronisticBlockPos collectionPos = blockHopper.collectionPosFromDirection(xCoord, yCoord, zCoord, hopperDir);
		@Nullable TileEntity collectionTile = collectionPos.getBlockTileEntity(worldObj);
		
		//Only pull from an IInventory if we're not on transfer cooldown.
		if(collectionTile instanceof IInventory) {
			if(allowInventoryInteraction)	{
				performedAnyInventoryInteraction = pullFromIInventory((IInventory) collectionTile, blockHopper.collectionDirectionFromDirection(hopperDir));
			}
		} else {
			pullFromWorld(collectionPos);
		}
		
		//Push into IInventories.
		if(allowInventoryInteraction) {
			AnachronisticBlockPos pushPos = blockHopper.depositPosFromDirection(xCoord, yCoord, zCoord, hopperDir);
			TileEntity pushTile = pushPos.getBlockTileEntity(worldObj);
			if(pushTile instanceof IInventory) {
				performedAnyInventoryInteraction |= pushIntoIInventory((IInventory) pushTile, blockHopper.depositDirectionFromDirection(hopperDir));
			}
		}
		
		if(performedAnyInventoryInteraction) transferCooldown = 8;
	}
	
	private boolean pullFromIInventory(IInventory collectionInventory, ForgeDirection collectionSide) {
		return TransferHelper.hopperPullFrom(collectionInventory, collectionSide, this);
	}
	
	@SuppressWarnings("unchecked")
	private void pullFromWorld(AnachronisticBlockPos pos) {
		for(EntityItem entItem : (List<EntityItem>) worldObj.getEntitiesWithinAABB(EntityItem.class, pos.aabb())) {
			if(entItem.isDead) continue;
			ItemStack stack = entItem.getEntityItem(); //Wrong MCP name, this is "getItemStack"
			if(stack == null || stack.stackSize <= 0) continue;
			
			stack.stackSize -= TransferHelper.insert(stack, stack.stackSize, this.inv);
			if(stack.stackSize <= 0) entItem.setDead(); 
		}
	}
	
	private boolean pushIntoIInventory(IInventory depositInventory, ForgeDirection depositSide) {
		return TransferHelper.hopperPushInto(depositInventory, depositSide, this);
	}
	
	// Yo idk
	
	@Override
	public boolean shouldRefresh(int oldID, int newID, int oldMeta, int newMeta, World world, int x, int y, int z) {
		//Changes to only metadata don't invalidate the tile entity, but I should be aware of them.
		if(oldID != newID) return true;
		
		blockMetadata = newMeta;
		return false;
	}
	
	// Nbt
	
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
