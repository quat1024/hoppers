package agency.highlysuspect.hopper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import java.util.Arrays;

public class RenderHopper implements ISimpleBlockRenderingHandler {
	private static final int HOPPER_INSIDE = 16;
	private static final int HOPPER_OUTSIDE = 17;
	private static final int HOPPER_TOP = 18;
	
	private static final int[] HOPPER_OUTSIDE_WITH_HOPPER_INSIDE_ON_TOP = differentOnTop(HOPPER_OUTSIDE, HOPPER_INSIDE);
	private static final int[] HOPPER_OUTSIDE_WITH_HOPPER_TOP_ON_TOP = differentOnTop(HOPPER_OUTSIDE, HOPPER_TOP);
	private static final int[] HOPPER_OUTSIDE_ON_ALL_FACES = allFaces(HOPPER_OUTSIDE);
	
	private static int[] allFaces(int i) {
		int[] array = new int[6];
		Arrays.fill(array, i);
		return array;
	}
	
	@SuppressWarnings("SameParameterValue")
	private static int[] differentOnTop(int sides, int top) {
		int[] array = allFaces(sides);
		array[1] = top;
		return array;
	}
	
	//Wrapper for overrideBlockBounds where I can read the parameters right off of a model imported into Blockbench. Lol.
	private static void bounds(RenderBlocks renderBlocks, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
		renderBlocks.overrideBlockBounds(x / 16d, y / 16d, z / 16d, (x + sizeX) / 16d, (y + sizeY) / 16d, (z + sizeZ) / 16d);
	}
	
	@Override
	public void renderInventoryBlock(Block block, int x, int y, RenderBlocks renderBlocks) {
		//The item uses its own renderer, and doesn't take this code path.
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderBlocks) {
		//The model FORTUNATELY has some scheme where the UV always matches the world coordinates or something.
		//Like if you're drawing on the left side of the block the UV will be on the left side of the texture.
		//This plays really well with how overrideBlockBounds works.
		
		//Bowl base
		bounds(renderBlocks, 0, 10, 0, 16, 1, 16);
		HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, HOPPER_OUTSIDE_WITH_HOPPER_INSIDE_ON_TOP);
		
		//Bowl walls
		bounds(renderBlocks, 0, 11, 0, 2, 5, 16);
		HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, HOPPER_OUTSIDE_WITH_HOPPER_TOP_ON_TOP);
		bounds(renderBlocks, 14, 11, 0, 2, 5, 16);
		HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, HOPPER_OUTSIDE_WITH_HOPPER_TOP_ON_TOP);
		bounds(renderBlocks, 2, 11, 0, 12, 5, 2);
		HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, HOPPER_OUTSIDE_WITH_HOPPER_TOP_ON_TOP);
		bounds(renderBlocks, 2, 11, 14, 12, 5, 2);
		HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, HOPPER_OUTSIDE_WITH_HOPPER_TOP_ON_TOP);
		
		//Chute A
		bounds(renderBlocks, 4, 4, 4, 8, 6, 8);
		HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, HOPPER_OUTSIDE_ON_ALL_FACES);
		
		//Chute B (moves depending on metadata)
		switch(world.getBlockMetadata(x, y, z)) {
			case 0: //Down
				bounds(renderBlocks, 6, 0, 6, 4, 4, 4);
				HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, HOPPER_OUTSIDE_ON_ALL_FACES);
				break;
			case 1: default: //Up??? TODO
				break;
			case 2: //North
				bounds(renderBlocks, 6, 4, 0, 4, 4, 4);
				HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, HOPPER_OUTSIDE_ON_ALL_FACES);
				break;
			case 3: //South
				bounds(renderBlocks, 6, 4, 12, 4, 4, 4);
				HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, HOPPER_OUTSIDE_ON_ALL_FACES);
				break;
			case 4: //West
				bounds(renderBlocks, 0, 4, 6, 4, 4, 4);
				HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, HOPPER_OUTSIDE_ON_ALL_FACES);
				break;
			case 5: //East
				bounds(renderBlocks, 12, 4, 6, 4, 4, 4);
				HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, HOPPER_OUTSIDE_ON_ALL_FACES);
				break;
		}
		
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
