package agency.highlysuspect.hopper.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;

public class HmmRenderBlocks {
	//Copy paste of RenderBlocks.renderStandardBlock that takes int[] parameter, 012345 -> bottom top north south west east, texture indexes on the atlas.
	public static boolean renderStandardBlockWithTextureOverride(RenderBlocks thi$, Block par1Block, int x, int y, int z, int[] textureOverrides) {
		int packedColorMultiplier = par1Block.colorMultiplier(thi$.blockAccess, x, y, z);
		float red = (float)(packedColorMultiplier >> 16 & 255) / 255.0F;
		float green = (float)(packedColorMultiplier >> 8 & 255) / 255.0F;
		float blue = (float)(packedColorMultiplier & 255) / 255.0F;
		if (EntityRenderer.anaglyphEnable) {
			float anaglyphRed = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
			float anaglyphGreen = (red * 30.0F + green * 70.0F) / 100.0F;
			float anaglyphBlue = (red * 30.0F + blue * 70.0F) / 100.0F;
			red = anaglyphRed;
			green = anaglyphGreen;
			blue = anaglyphBlue;
		}
		
		//Rendering with AO removed because it looked bad on the hopper, this is just for the hopper model anyways.
		return renderStandardBlockWithColorMultiplierAndTextureOverrides(thi$, par1Block, x, y, z, red, green, blue, textureOverrides);
	}
	
	//Copypasta of renderStandardBlockWithColorMultiplier but looks up texture indices in the array instead of delegating to the block.
	//I cleaned the code up a bit and tried to name the parameters.
	public static boolean renderStandardBlockWithColorMultiplierAndTextureOverrides(RenderBlocks thi$, Block par1Block, int x, int y, int z, float red, float green, float blue, int[] textureOverrides) {
		thi$.enableAO = false;
		Tessellator tess = Tessellator.instance;
		boolean renderAnyFaces = false;
		float var10 = 0.5F; //Yeah idk what this does, i think it has to do with the game's directional lighting
		float var11 = 1.0F;
		float var12 = 0.8F;
		float var13 = 0.6F;
		float oneTimesRed = var11 * red;
		float oneTimesGreen = var11 * green;
		float oneTimesBlue = var11 * blue;
		float halfTimesRed = var10 * red;
		float pointEightTimesRed = var12 * red;
		float pointSixTimesRed = var13 * red;
		float halfTimesGreen = var10 * green;
		float pointEightTimesGreen = var12 * green;
		float pointSixTimesGreen = var13 * green;
		float halfTimesBlue = var10 * blue;
		float pointEightTimesBlue = var12 * blue;
		float pointSixTimesBlue = var13 * blue;
		
		int mixedBrightness = par1Block.getMixedBrightnessForBlock(thi$.blockAccess, x, y, z);
		if (thi$.renderAllFaces || par1Block.shouldSideBeRendered(thi$.blockAccess, x, y - 1, z, 0)) {
			tess.setBrightness(thi$.renderMinY > 0.0D ? mixedBrightness : par1Block.getMixedBrightnessForBlock(thi$.blockAccess, x, y - 1, z));
			tess.setColorOpaque_F(halfTimesRed, halfTimesGreen, halfTimesBlue);
			thi$.renderBottomFace(par1Block, x, y, z, textureOverrides[0]);
			renderAnyFaces = true;
		}
		
		if (thi$.renderAllFaces || par1Block.shouldSideBeRendered(thi$.blockAccess, x, y + 1, z, 1)) {
			tess.setBrightness(thi$.renderMaxY < 1.0D ? mixedBrightness : par1Block.getMixedBrightnessForBlock(thi$.blockAccess, x, y + 1, z));
			tess.setColorOpaque_F(oneTimesRed, oneTimesGreen, oneTimesBlue);
			thi$.renderTopFace(par1Block, x, y, z, textureOverrides[1]);
			renderAnyFaces = true;
		}
		
		if (thi$.renderAllFaces || par1Block.shouldSideBeRendered(thi$.blockAccess, x, y, z - 1, 2)) {
			tess.setBrightness(thi$.renderMinZ > 0.0D ? mixedBrightness : par1Block.getMixedBrightnessForBlock(thi$.blockAccess, x, y, z - 1));
			tess.setColorOpaque_F(pointEightTimesRed, pointEightTimesGreen, pointEightTimesBlue);
			thi$.renderEastFace(par1Block, x, y, z, textureOverrides[2]);
			renderAnyFaces = true;
		}
		
		if (thi$.renderAllFaces || par1Block.shouldSideBeRendered(thi$.blockAccess, x, y, z + 1, 3)) {
			tess.setBrightness(thi$.renderMaxZ < 1.0D ? mixedBrightness : par1Block.getMixedBrightnessForBlock(thi$.blockAccess, x, y, z + 1));
			tess.setColorOpaque_F(pointEightTimesRed, pointEightTimesGreen, pointEightTimesBlue);
			thi$.renderWestFace(par1Block, x, y, z, textureOverrides[3]);
			renderAnyFaces = true;
		}
		
		if (thi$.renderAllFaces || par1Block.shouldSideBeRendered(thi$.blockAccess, x - 1, y, z, 4)) {
			tess.setBrightness(thi$.renderMinX > 0.0D ? mixedBrightness : par1Block.getMixedBrightnessForBlock(thi$.blockAccess, x - 1, y, z));
			tess.setColorOpaque_F(pointSixTimesRed, pointSixTimesGreen, pointSixTimesBlue);
			thi$.renderNorthFace(par1Block, x, y, z, textureOverrides[4]);
			renderAnyFaces = true;
		}
		
		if (thi$.renderAllFaces || par1Block.shouldSideBeRendered(thi$.blockAccess, x + 1, y, z, 5)) {
			tess.setBrightness(thi$.renderMaxX < 1.0D ? mixedBrightness : par1Block.getMixedBrightnessForBlock(thi$.blockAccess, x + 1, y, z));
			tess.setColorOpaque_F(pointSixTimesRed, pointSixTimesGreen, pointSixTimesBlue);
			thi$.renderSouthFace(par1Block, x, y, z, textureOverrides[5]);
			renderAnyFaces = true;
		}
		
		return renderAnyFaces;
	}
	
}
