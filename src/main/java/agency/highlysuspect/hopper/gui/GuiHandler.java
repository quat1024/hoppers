package agency.highlysuspect.hopper.gui;

import agency.highlysuspect.hopper.HopperMod;
import agency.highlysuspect.hopper.TileEntityHopper;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return HopperMod.proxy.getClientGuiElement(id, player, world, x, y, z);
	}
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if(!world.blockExists(x, y, z)) return null;
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te == null) return null;
		
		if(id == GuiIds.HOPPER && te instanceof TileEntityHopper) {
			return new ContainerHopper(player.inventory, (TileEntityHopper) te);
		}
		
		return null;
	}
}
