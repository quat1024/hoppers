package agency.highlysuspect.hopper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class RenderHopper implements ISimpleBlockRenderingHandler {
	@Override
	public void renderInventoryBlock(Block block, int x, int y, RenderBlocks renderBlocks) {
		//The item has its own renderer.
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderBlocks) {
		renderBlocks.overrideBlockBounds(0, 0.5, 0, 1, 1, 1);
		renderBlocks.renderStandardBlock(block, x, y, z);
		renderBlocks.renderStandardBlock(block, x, y + 1, z); //Lol
		renderBlocks.unlockBlockBounds();
		
		return true;
	}
	
	@Override
	public boolean shouldRender3DInInventory() {
		return false;
	}
	
	@Override
	public int getRenderId() {
		//Could take this through the ctor but it has to be a global variable anyways, might as well go all in
		return HopperMod.proxy.hopperBlockRenderType;
	}
}
