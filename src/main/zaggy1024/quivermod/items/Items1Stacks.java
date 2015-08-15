package zaggy1024.quivermod.items;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class Items1Stacks extends ItemMultiItems {

	private ArrayList<Integer> bottledItems = new ArrayList(){{
    	add(0);
    	add(1);
    }};
    
	public ItemStack bottledRottenFlesh = new ItemStack(itemID, 1, 0);
	public ItemStack hideGlue = new ItemStack(itemID, 1, 1);

	public Items1Stacks(int index) {
		super(index);
		
        setHasSubtypes(true);
        setMaxDamage(0);
        setCreativeTab(CreativeTabs.tabMaterials);
		
		names = new String[] {"bottledRottenFlesh", "hideGlue"};

		GameRegistry.addShapedRecipe(bottledRottenFlesh,
				"f",
				"b",
				'f', Item.rottenFlesh,
				'b', new ItemStack(Item.potion));
    	FurnaceRecipes.smelting().addSmelting(itemID, 0, hideGlue, 0.2F);
	}
	
	@Override
	public int getItemStackLimit()
	{
		return 1;
	}
    
    @Override
    public boolean requiresMultipleRenderPasses()
    {
    	return true;
    }
    
    @Override
    public int getRenderPasses(int damage)
    {
    	if (bottledItems.contains(damage))
    		return 2;
    	
    	return 1;
    }
    
    @Override
    public Icon getIconFromDamageForRenderPass(int damage, int pass)
    {
    	if (bottledItems.contains(damage) && pass == 1)
    	{
    		return Item.glassBottle.getIconFromDamage(0);
    	}
    	
    	return super.getIconFromDamageForRenderPass(damage, pass);
    }

}
