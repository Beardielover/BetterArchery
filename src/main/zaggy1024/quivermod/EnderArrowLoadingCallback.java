package zaggy1024.quivermod;

import java.util.List;

import zaggy1024.quivermod.entities.EntityQuiverModArrow;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class EnderArrowLoadingCallback implements LoadingCallback {

	public EnderArrowLoadingCallback() {
	}

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) {
		for (Ticket ticket : tickets)
		{
			Entity ent = ticket.getEntity();
			
			if (ent != null && ent instanceof EntityQuiverModArrow)
			{
				EntityQuiverModArrow arrowEnt = (EntityQuiverModArrow)ent;
				arrowEnt.chunkLoader = ticket;
			}
		}
	}

}
