package agency.highlysuspect.hopper.render;

import agency.highlysuspect.hopper.BlockHopper;
import agency.highlysuspect.hopper.HopperMod;
import agency.highlysuspect.hopper.render.HmmRenderBlocks;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

import java.util.Arrays;

public class RenderHopper implements ISimpleBlockRenderingHandler {
	//Texture index IDs into gfx/hopper/atlas.png
	private static final int HOPPER_INSIDE = 16;
	private static final int HOPPER_OUTSIDE = 17;
	private static final int HOPPER_TOP = 18;
	
	private static final int[] HOPPER_OUTSIDE_WITH_HOPPER_INSIDE_ON_TOP = differentOnTop(HOPPER_OUTSIDE, HOPPER_INSIDE);
	private static final int[] HOPPER_OUTSIDE_WITH_HOPPER_INSIDE_ON_BOTTOM = flip(HOPPER_OUTSIDE_WITH_HOPPER_INSIDE_ON_TOP);
	private static final int[] HOPPER_OUTSIDE_WITH_HOPPER_TOP_ON_TOP = differentOnTop(HOPPER_OUTSIDE, HOPPER_TOP);
	private static final int[] HOPPER_OUTSIDE_WITH_HOPPER_TOP_ON_BOTTOM = flip(HOPPER_OUTSIDE_WITH_HOPPER_TOP_ON_TOP);
	private static final int[] HOPPER_OUTSIDE_ON_ALL_FACES = allFaces(HOPPER_OUTSIDE);
	
	//Wrapper for overrideBlockBounds where I can read the parameters right off of a model imported into Blockbench. Lol.
	private static void bounds(RenderBlocks renderBlocks, boolean flipVertically, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
		if(flipVertically) y = 16 - y - sizeY;
		renderBlocks.overrideBlockBounds(x / 16d, y / 16d, z / 16d, (x + sizeX) / 16d, (y + sizeY) / 16d, (z + sizeZ) / 16d);
	}
	
	@Override
	public void renderInventoryBlock(Block block, int x, int y, RenderBlocks renderBlocks) {
		//The item uses its own renderer, and doesn't take this code path.
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderBlocks) {
		if(!(block instanceof BlockHopper)) return false;
		BlockHopper hopper = (BlockHopper) block; 
		
		//The model FORTUNATELY has some scheme where the UV always matches the world coordinates or something.
		//Like if you're drawing on the left side of the block the UV will be on the left side of the texture.
		//This plays really well with how overrideBlockBounds works.
		
		ForgeDirection dir = hopper.directionFromMeta(world.getBlockMetadata(x, y, z));
		boolean flipVertically = dir == ForgeDirection.UP;
		
		//Bowl base
		int[] faces = flipVertically ? HOPPER_OUTSIDE_WITH_HOPPER_INSIDE_ON_BOTTOM : HOPPER_OUTSIDE_WITH_HOPPER_INSIDE_ON_TOP;
		bounds(renderBlocks, flipVertically, 0, 10, 0, 16, 1, 16);
		HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, faces);
		
		//Bowl walls
		faces = flipVertically ? HOPPER_OUTSIDE_WITH_HOPPER_TOP_ON_BOTTOM : HOPPER_OUTSIDE_WITH_HOPPER_TOP_ON_TOP;
		bounds(renderBlocks, flipVertically, 0, 11, 0, 2, 5, 16);
		HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, faces);
		bounds(renderBlocks, flipVertically, 14, 11, 0, 2, 5, 16);
		HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, faces);
		bounds(renderBlocks, flipVertically, 2, 11, 0, 12, 5, 2);
		HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, faces);
		bounds(renderBlocks, flipVertically, 2, 11, 14, 12, 5, 2);
		HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, faces);
		
		//Chute A
		bounds(renderBlocks, flipVertically, 4, 4, 4, 8, 6, 8);
		HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, HOPPER_OUTSIDE_ON_ALL_FACES);
		
		//Chute B (moves depending on metadata)
		switch(dir) {
			case DOWN: case UP: bounds(renderBlocks, flipVertically, 6, 0, 6, 4, 4, 4); break;
			case NORTH: bounds(renderBlocks, false, 6, 4, 0, 4, 4, 4); break;
			case SOUTH: bounds(renderBlocks, false, 6, 4, 12, 4, 4, 4); break;
			case WEST: bounds(renderBlocks, false, 0, 4, 6, 4, 4, 4); break;
			case EAST: bounds(renderBlocks, false, 12, 4, 6, 4, 4, 4); break;
		}
		HmmRenderBlocks.renderStandardBlockWithTextureOverride(renderBlocks, block, x, y, z, HOPPER_OUTSIDE_ON_ALL_FACES);
		
		renderBlocks.unlockBlockBounds();
		return true;
	}
	
	@Override
	public boolean shouldRender3DInInventory() {
		return false;
	}
	
	@Override
	public int getRenderId() {
		//Could take this through the ctor, but it has to be a global variable anyways, might as well go all in
		return HopperMod.proxy.hopperBlockRenderType;
	}
	
	private static int[] allFaces(int i) {
		int[] array = new int[6];
		Arrays.fill(array, i);
		return array;
	}
	
	@SuppressWarnings("SameParameterValue")
	private static int[] differentOnTop(int sides, int top) {
		int[] array = allFaces(sides);
		array[ForgeDirection.UP.ordinal()] = top;
		return array;
	}
	
	private static int[] flip(int[] in) {
		int[] out = new int[in.length];
		System.arraycopy(in, 0, out, 0, in.length);
		out[ForgeDirection.DOWN.ordinal()] = in[ForgeDirection.UP.ordinal()  ];
		out[ForgeDirection.UP.ordinal()  ] = in[ForgeDirection.DOWN.ordinal()];
		return out;
	}
}
