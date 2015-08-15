package zaggy1024.quivermod.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemQuiverMod extends Item {

	public ItemQuiverMod(int index) {
		super(index);
	}
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		String mod = "quivermod:";
		String name = mod + getUnlocalizedName().split("\\.")[1];
		itemIcon = iconRegister.registerIcon(name);
	}

}
