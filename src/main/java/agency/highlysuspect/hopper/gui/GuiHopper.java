package agency.highlysuspect.hopper.gui;

import agency.highlysuspect.hopper.TileEntityHopper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StringTranslate;
import org.lwjgl.opengl.GL11;

public class GuiHopper extends GuiContainer {
	public GuiHopper(InventoryPlayer playerInventory, TileEntityHopper hopper) {
		super(new ContainerHopper(playerInventory, hopper));
		
		this.ySize = 133;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		fontRenderer.drawString(StringTranslate.getInstance().translateKey("container.hopper-hopperBlock"), 8, 6, 0x404040);
		fontRenderer.drawString(StringTranslate.getInstance().translateKey("container.inventory"), 8, 39, 0x404040);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float idk, int probablyMouseX, int probablyMouseY) {
		int textureHandle = this.mc.renderEngine.getTexture("/gfx/hopper/hopper-gui.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(textureHandle);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		//I believe this is x, y, u start, v start, width, height
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}
}
