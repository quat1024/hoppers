package agency.highlysuspect.hopper;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
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
		int targetDir = ForgeDirection.OPPOSITES[hitDirection];
		
		//Not yet!
		if(targetDir == ForgeDirection.UP.ordinal()) targetDir = ForgeDirection.DOWN.ordinal();
		
		return targetDir;
	}
}
