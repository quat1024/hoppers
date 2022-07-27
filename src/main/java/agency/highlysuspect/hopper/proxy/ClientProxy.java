package agency.highlysuspect.hopper.proxy;

import agency.highlysuspect.hopper.RenderHopper;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
	@Override
	public void clientInit() {
		MinecraftForgeClient.preloadTexture(ATLAS);
		
		hopperBlockRenderType = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new RenderHopper());
	}
}
