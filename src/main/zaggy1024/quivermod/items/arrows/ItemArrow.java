package zaggy1024.quivermod.items.arrows;

import java.util.List;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.util.TextHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemArrow extends ItemQuiverModArrow {

	public ItemArrow(int itemID) {
		super(itemID);
		
		setUnlocalizedName("arrow");
		setCreativeTab(CreativeTabs.tabCombat);
		
		QuiverMod.arrowItemList.add(this);
		setFull3D();
		setHasSubtypes(true);
		setCreativeTab(QuiverMod.tabArrows);
		setTextureName("arrow");
		
		setInfinityChance(1);
	}
	
	@Override
    public void registerIcons(IconRegister register)
    {
        itemIcon = register.registerIcon(getIconString());
    }

}
