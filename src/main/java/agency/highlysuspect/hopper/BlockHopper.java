package agency.highlysuspect.hopper;

import agency.highlysuspect.hopper.gui.GuiIds;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockHopper extends BlockContainer {
	public BlockHopper(int id) {
		super(id, Material.iron);
		setHardness(2);
		setTextureFile(HopperMod.proxy.ATLAS);
	}
	
	// Util
	
	private static final int DIRECTION_MASK = 7; //0b0111
	private static final int POWER_MASK = 8; //0b1000
	public ForgeDirection directionFromMeta(int meta) {
		return ForgeDirection.getOrientation(meta & DIRECTION_MASK);
	}
	
	public int withDirection(int meta, ForgeDirection direction) {
		return (meta & ~DIRECTION_MASK) | direction.ordinal();
	}
	
	public boolean poweredFromMeta(int meta) {
		return (meta & POWER_MASK) != 0;
	}
	
	public int withPowered(int meta, boolean power) {
		return (meta & ~POWER_MASK) | (power ? POWER_MASK : 0);
	}
	
	//Upward-pointing hoppers collect from the top face of the block under them.
	//All other direction-pointing hoppers collect from the bottom face of the block above them.
	//(And if you want to extract from the side, use a buildcraft pipe lol, that's always how hoppers have been.)
	public AnachronisticBlockPos collectionPosFromDirection(int x, int y, int z, ForgeDirection dir) {
		if(dir == ForgeDirection.UP) return new AnachronisticBlockPos(x, y - 1, z);
		else return new AnachronisticBlockPos(x, y + 1, z);
	}
	
	public ForgeDirection collectionDirectionFromDirection(ForgeDirection dir) {
		if(dir == ForgeDirection.UP) return ForgeDirection.UP;
		else return ForgeDirection.DOWN;
	}
	
	public AnachronisticBlockPos depositPosFromDirection(int x, int y, int z, ForgeDirection dir) {
		return new AnachronisticBlockPos(x, y, z).mutateOffset(dir);
	}
	
	public ForgeDirection depositDirectionFromDirection(ForgeDirection dir) {
		return dir.getOpposite();
	}
	
	public boolean calculatePoweredFromWorld(World world, int x, int y, int z) {
		return world.isBlockIndirectlyGettingPowered(x, y, z);
	}
	
	// Appearance
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		return true;
	}
	
	@Override
	public int getRenderType() {
		return HopperMod.proxy.hopperBlockRenderType;
	}
	
	@Override
	public int getBlockTextureFromSide(int par1) {
		//Outside of block rendering, used for particles.
		return 1;
	}
	
	// Placing, breaking
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityHopper();
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int hitDirection, float rayHitX, float rayHitY, float rayHitZ, int meta) {
		meta = 0;
		meta = withDirection(meta, ForgeDirection.getOrientation(ForgeDirection.OPPOSITES[hitDirection]));
		meta = withPowered(meta, calculatePoweredFromWorld(world, x, y, z));
		return meta;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		//LOOOOOLL there's no utility function for this in this version of Minecraft!! Haha
		//I should probably split this out into one (but this mod only adds this one block entity, idk)
		TileEntityHopper hopper = (TileEntityHopper) world.getBlockTileEntity(x, y, z); 
		if(hopper != null) {
			for(int i = 0; i < hopper.getSizeInventory(); i++) {
				ItemStack stack = hopper.getStackInSlot(i);
				if(stack == null) continue;
				
				float xd = world.rand.nextFloat() * 0.8f + 0.1f;
				float yd = world.rand.nextFloat() * 0.8f + 0.1f;
				float zd = world.rand.nextFloat() * 0.8f + 0.1f;
				
				//Mainly copied from the dispenser code, but the chest is similar.
				//Yo it's so weird that vanilla drops stacks in random quantities like this.
				//It's kinda neat actually. You can see the items go in different directions.
				while(stack.stackSize > 0) {
					int take = Math.min(stack.stackSize, world.rand.nextInt(21) + 10);
					stack.stackSize -= take;
					
					ItemStack drop = stack.copy();
					drop.stackSize = take;
					EntityItem ent = new EntityItem(world, x + xd, y + yd, z + zd, drop);
					ent.motionX = world.rand.nextGaussian() * 0.05;
					ent.motionY = world.rand.nextGaussian() * 0.05 + 0.2;
					ent.motionZ = world.rand.nextGaussian() * 0.05;
					world.spawnEntityInWorld(ent);
				}
			}
		}
		
		super.breakBlock(world, x, y, z, par5, par6);
	}
	
	// Interaction
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		super.onBlockActivated(world, x, y, z, player, par6, par7, par8, par9);
		
		//Skipping block interactions on shift is manual in this version!
		if(player.isSneaking()) return false;
		
		if(!world.isRemote) player.openGui(HopperMod.instance, GuiIds.HOPPER, world, x, y, z);
		return true;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int directionIThink) {
		int meta = world.getBlockMetadata(x, y, z);
		boolean isPowered = poweredFromMeta(meta);
		boolean shouldPower = calculatePoweredFromWorld(world, x, y, z);
		
		if(isPowered != shouldPower) {
			world.setBlockMetadataWithNotify(x, y, z, withPowered(meta, shouldPower));
			
			TileEntity entHere = world.getBlockTileEntity(x, y, z);
			if(entHere != null) entHere.updateContainingBlockInfo();
		}
		
		super.onNeighborBlockChange(world, x, y, z, directionIThink);
	}
}
