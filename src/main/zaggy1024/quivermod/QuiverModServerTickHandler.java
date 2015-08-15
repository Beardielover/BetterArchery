package zaggy1024.quivermod;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import zaggy1024.quivermod.entities.EntityQuiverModSkeleton;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.World;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class QuiverModServerTickHandler implements ITickHandler {
	
	public QuiverModServerTickHandler() {
	}
	
	@Override
	public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.WORLD);
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		if (type.contains(TickType.WORLD))
		{
			World world = (World)tickData[0];
			
			ArrayList<EntitySkeleton> skeletonsToCopy = new ArrayList();
			
			for (Entity entity : (List<Entity>)world.loadedEntityList)
			{
				if (entity.getClass() == EntitySkeleton.class)
				{
					EntitySkeleton skeleton = (EntitySkeleton)entity;
					
					if (skeleton.getSkeletonType() == 0)
						skeletonsToCopy.add((EntitySkeleton)entity);
				}
			}
			
			for (EntitySkeleton skeleton : skeletonsToCopy)
			{
				EntityQuiverModSkeleton modSkeleton = new EntityQuiverModSkeleton(world);
				modSkeleton.posX = skeleton.posX;
				modSkeleton.posY = skeleton.posY;
				modSkeleton.posZ = skeleton.posZ;
				
				modSkeleton.rotationYaw = skeleton.rotationYaw;
				modSkeleton.rotationPitch = skeleton.rotationPitch;
				
				modSkeleton.setLocationAndAngles(modSkeleton.posX, modSkeleton.posY, modSkeleton.posZ,
						modSkeleton.rotationYaw, modSkeleton.rotationPitch);
	
				modSkeleton.motionX = skeleton.motionX;
				modSkeleton.motionY = skeleton.motionY;
				modSkeleton.motionZ = skeleton.motionZ;
				
				modSkeleton.initCreature();

				skeleton.setDead();
				world.spawnEntityInWorld(modSkeleton);
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
	}

	@Override
	public String getLabel() {
		return "QuiverModServerTickHandler";
	}

}
