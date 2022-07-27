package agency.highlysuspect.hopper.proxy;

import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
	@Override
	public void clientInit() {
		MinecraftForgeClient.preloadTexture("gfx/hopper/blocks.png");
	}
}
