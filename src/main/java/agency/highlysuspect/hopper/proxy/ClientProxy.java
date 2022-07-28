package agency.highlysuspect.hopper.proxy;

import agency.highlysuspect.hopper.TileEntityHopper;
import agency.highlysuspect.hopper.gui.GuiIds;
import agency.highlysuspect.hopper.gui.GuiHopper;
import agency.highlysuspect.hopper.render.RenderHopper;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
	@Override
	public void clientInit() {
		MinecraftForgeClient.preloadTexture(ATLAS);
		
		hopperBlockRenderType = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new RenderHopper());
	}
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if(!world.blockExists(x, y, z)) return null;
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te == null) return null;
		
		if(id == GuiIds.HOPPER && te instanceof TileEntityHopper) {
			return new GuiHopper(player.inventory, (TileEntityHopper) te);
		}
		
		return null;
	}
}
