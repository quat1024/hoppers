package agency.highlysuspect.hopper;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import javax.annotation.Nullable;

//Hey I mean, sometimes it's useful.
public class AnachronisticBlockPos {
	public AnachronisticBlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int x, y, z;
	
	public @Nullable TileEntity getBlockTileEntity(World world) {
		return world.getBlockTileEntity(x, y, z);
	}
	
	public AxisAlignedBB aabb() {
		return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
	}
	
	public AnachronisticBlockPos mutateOffset(ForgeDirection dir) {
		x += dir.offsetX;
		y += dir.offsetY;
		z += dir.offsetZ;
		return this;
	}
}
