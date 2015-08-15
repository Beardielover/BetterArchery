package zaggy1024.quivermod.util;

import net.minecraft.block.*;
import net.minecraftforge.common.ForgeDirection;

public class PartialBlockSurfaces {
	
	private static boolean approxEqual(double i, double j)
	{
		return i < j + 0.01 && i > j - 0.01;
	}
	
	private static boolean atOuterEdge(double i, float leeway)
	{
		return (i <= leeway && i >= -leeway) || (i >= 1 - leeway && i <= 1 + leeway);
	}
	
	private static boolean atOuterEdge(double x, double y, double z, ForgeDirection side, float leeway)
	{
		switch (side)
		{
		case WEST:
		case EAST:
			return atOuterEdge(x, leeway);
		case NORTH:
		case SOUTH:
			return atOuterEdge(z, leeway);
		case UP:
		case DOWN:
			return atOuterEdge(y, leeway);
		default:
			break;
		}
		
		return false;
	}

	public static int checkPosition(double x, double y, double z, ForgeDirection side, int blockID, int metadata, float leeway)
	{
		Block block = Block.blocksList[blockID];
		
		if (block instanceof BlockStairs || block instanceof BlockHalfSlab)
		{
            if (atOuterEdge(x, y, z, side, leeway))
            	return 1;
		}
		
		return -1;
	}

}
