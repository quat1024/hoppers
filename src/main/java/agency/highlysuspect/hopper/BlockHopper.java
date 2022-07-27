package agency.highlysuspect.hopper;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
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
	public int getRenderType() {
		return HopperMod.proxy.hopperBlockRenderType;
	}
	
	@Override
	public int getBlockTextureFromSide(int par1) {
		//This is used for block particles
		return 1;
	}
	
	// Placing, breaking
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileHopper();
	}
}
