package agency.highlysuspect.hopper;

import agency.highlysuspect.hopper.gui.GuiIds;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
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
	public static ForgeDirection directionFromMeta(int meta) {
		return ForgeDirection.getOrientation(meta & DIRECTION_MASK);
	}
	
	public static int withDirection(int meta, ForgeDirection direction) {
		return (meta & ~DIRECTION_MASK) | direction.ordinal();
	}
	
	public static boolean poweredFromMeta(int meta) {
		return (meta & POWER_MASK) != 0;
	}
	
	public static int withPowered(int meta, boolean power) {
		return (meta & ~POWER_MASK) | (power ? POWER_MASK : 0);
	}
	
	public static boolean poweredFromWorld(World world, int x, int y, int z) {
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
		return new TileHopper();
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int hitDirection, float rayHitX, float rayHitY, float rayHitZ, int meta) {
		meta = 0;
		meta = withDirection(meta, ForgeDirection.getOrientation(ForgeDirection.OPPOSITES[hitDirection]));
		meta = withPowered(meta, poweredFromWorld(world, x, y, z));
		return meta;
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
}
