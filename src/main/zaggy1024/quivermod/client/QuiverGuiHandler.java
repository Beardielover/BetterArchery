package zaggy1024.quivermod.client;

import zaggy1024.quivermod.config.*;
import zaggy1024.quivermod.items.quiver.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class QuiverGuiHandler implements IGuiHandler {

	private QuiverInventory getQuiverInventory(EntityPlayer player)
	{
		QuiverInventory inv = new QuiverInventory(player.inventory, player.inventory.currentItem);
		
		return inv;
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch (id)
		{
		case 0:
			return new QuiverContainer(player.inventory, getQuiverInventory(player));
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch (id)
		{
		case 0:
			return new QuiverGuiContainer(player.inventory, getQuiverInventory(player));
		}
		
		return null;
	}

}
