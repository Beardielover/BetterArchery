package zaggy1024.quivermod.items;

import java.util.List;

import zaggy1024.quivermod.QuiverMod;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ItemMultiItems extends ItemQuiverMod {
	
    public String[] names;
    public Icon[] icons;

	public ItemMultiItems(int index) {
		super(index);
		
        setHasSubtypes(true);
        setMaxDamage(0);
        setCreativeTab(CreativeTabs.tabMaterials);
	}
	
	@Override
	public int getItemStackLimit()
	{
		return 64;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if (stack.getItemDamage() >= names.length)
		{
			return "";
		}
		
		return "item." + names[stack.getItemDamage()];
	}
    
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        icons = new Icon[names.length];
        
        for (int i = 0; i < icons.length; i++)
        {
        	icons[i] = iconRegister.registerIcon("quivermod:" + names[i].toLowerCase());
        }
    }
	
	@Override
    public Icon getIconFromDamage(int damage)
    {
		if (damage >= icons.length)
		{
			return icons[0];
		}
		
        return icons[damage];
    }

	@Override
    public void getSubItems(int itemID, CreativeTabs tabs, List list)
    {
        for (int i = 0; i < names.length; i++)
        {
            list.add(new ItemStack(itemID, 1, i));
        }
    }

}
