package agency.highlysuspect.hopper.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class CommonProxy {
	public int hopperBlockRenderType = -1;
	public final String ATLAS = "gfx/hopper/atlas.png";
	
	public void clientInit() {
		
	}
	
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
}
