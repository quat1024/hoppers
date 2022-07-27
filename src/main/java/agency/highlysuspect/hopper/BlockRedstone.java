package agency.highlysuspect.hopper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockRedstone extends Block {
	public BlockRedstone(int id) {
		super(id, 0, Material.iron);
		setResistance(6);
		setHardness(5);
		setTextureFile(HopperMod.proxy.ATLAS);
	}
	
	//Placing, breaking
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		//Lazy impl, double-updates a bunch of blocks but if redstone torches can do it I can too
		world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
		world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID);
		world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID);
		world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID);
		world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID);
		world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		super.breakBlock(world, x, y, z, par5, par6);
		world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
		world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID);
		world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID);
		world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID);
		world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID);
		world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID);
	}
	
	//Powering
	
	@Override
	public boolean canProvidePower() {
		return true;
	}
	
	@Override
	public boolean isProvidingStrongPower(IBlockAccess access, int x, int y, int z, int side) {
		return false;
	}
	
	@Override
	public boolean isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int side) {
		return true;
	}
	
	//yooo.... the forge extension is really this old!!!!!!
	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}
	
	//Weirdness
	
	@Override
	public boolean isBlockNormalCube(World world, int x, int y, int z) {
		//This version of the game has a peculiarity with redstone. There are no solid-block power sources!
		//The only power sources are things like levers, buttons, repeaters, etc. So the only way a normal
		//cube can provide power is if something else is powering it! isBlockIndirectlyProvidingPowerTo
		//first checks isBlockNormalCube, and if it returns "true" moves directly on to looking for power
		//sources adjacent to it instead. Clever...?
		return false;
	}
	
	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		//So I just said the block isn't a normal cube (for redstone code), but it kinda still is.
		//I want you to be able to place torches on the side and stuff like that.
		return true;
	}
}
