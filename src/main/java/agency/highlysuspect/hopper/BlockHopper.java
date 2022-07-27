package agency.highlysuspect.hopper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ForgeDirection;

public class BlockHopper extends Block {
	public BlockHopper(int id) {
		super(id, Material.iron);
	}
	
	public ForgeDirection getDirectionFromMeta(int meta) {
		switch(meta) {
			case 0: default: return ForgeDirection.DOWN;
			case 1: return ForgeDirection.NORTH;
			case 2: return ForgeDirection.SOUTH;
			case 3: return ForgeDirection.EAST;
			case 4: return ForgeDirection.WEST;
		}
	}
	
	public int getMetaFromDirection(ForgeDirection d) {
		switch(d) {
			case DOWN: default: return 0;
			case NORTH: return 1;
			case SOUTH: return 2;
			case EAST: return 3;
			case WEST: return 4;
		}
	}
}
