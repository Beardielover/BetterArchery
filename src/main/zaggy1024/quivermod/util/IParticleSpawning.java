package zaggy1024.quivermod.util;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public interface IParticleSpawning {

	public void spawnDiggingFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ, Block block, int side, int metadata);

}
