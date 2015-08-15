package zaggy1024.quivermod.items.arrows;

import java.util.List;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.items.ItemQuiverMod;
import zaggy1024.quivermod.items.misc.SplittingArrowDefaults;
import zaggy1024.quivermod.util.BitwiseHelper;
import zaggy1024.quivermod.util.TextHelper;
import zaggy1024.quivermod.util.TextureSizeMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;

public class ItemQuiverModArrow extends ItemQuiverMod implements ISplittingArrow, IArrowIcons, IArrowItem {
	
	public static Icon emptySlotIcon = null;
	public static Icon splittingArrowIcon = null;

	protected float fullBright = -1;
	protected int frameCount = 1;

	protected int infinityChance = 2;
	protected boolean canAddArrowHead = true;

	public ItemQuiverModArrow(int par1) {
		super(par1);
		
		QuiverMod.arrowItemList.add(this);
		setFull3D();
		setHasSubtypes(true);
		setCreativeTab(QuiverMod.tabArrows);
	}
	
	public ItemQuiverModArrow setInfinityChance(int chance)
	{
		infinityChance = chance;
		return this;
	}
	
	public int getInfinityChance()
	{
		return infinityChance;
	}
	
	public boolean canAddArrowHead(ItemStack stack)
	{
		return canAddArrowHead;
	}

	@Override
	public int getArrowHeadID(ItemStack stack) {
		return stack.stackTagCompound.getInteger("arrowhead");
	}
	
	@Override
	public void setArrowHeadID(ItemStack stack, int id)
	{
		stack.stackTagCompound.setInteger("arrowhead", id);
	}

	public String getSpecialBowIconName(int damage)
	{
		return getName().toLowerCase();
	}

	public String[] getSpecialBowIconNames() {
		return new String[]{getSpecialBowIconName(0)};
	}
	
	public ItemQuiverModArrow setFullBright(float brightness)
	{
		fullBright = brightness;
		return this;
	}

	public float getFullBright()
	{
		return fullBright;
	}
	
	public ItemQuiverModArrow setFrameCount(int count)
	{
		frameCount = count;
		return this;
	}

	public int getFrameCount()
	{
		return frameCount;
	}

	public boolean getNeedsTwoPasses()
	{
		return false;
	}

	public static final int SPLITSTART = 0;
	public static final int SPLITEND = 3;
	
	@Override
	public int getSplittingArrowCount(int damage)
	{
		return BitwiseHelper.getInteger(damage, SPLITSTART, SPLITEND);
	}
	
	@Override
	public boolean isSplittingArrow(int damage)
	{
		return SplittingArrowDefaults.isSplittingArrow(getSplittingArrowCount(damage));
	}
	
	@Override
	public int getItemDamageForArrowCount(int damage, int count)
	{
		if (count < 2)
			count = 0;
		
		return BitwiseHelper.setInteger(damage, SPLITSTART, SPLITEND, count);
	}
	
	public boolean canCraftSplittingArrow(int damage)
	{
		return true;
	}
	
    public String getItemDisplayName(ItemStack stack)
    {
		return ArrowNameHelper.getFullName(stack);
    }
    
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
    
    public int getRenderPasses(int damage)
    {
        return isSplittingArrow(damage) ? 2 : 1;
    }

    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamageForRenderPass(int damage, int pass)
    {
        return pass > 0 && isSplittingArrow(damage) ? ItemQuiverModArrow.splittingArrowIcon : itemIcon;
    }
    
    protected String getName()
    {
    	String unlocName = getUnlocalizedName();
    	return unlocName.substring(unlocName.lastIndexOf('.') + 1);
    }
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		String mod = "quivermod:";
		String name = mod + getName().toLowerCase();
		itemIcon = iconRegister.registerIcon(name);
		
		emptySlotIcon = iconRegister.registerIcon(mod + "emptyarrowslot");
		splittingArrowIcon = iconRegister.registerIcon(mod + "splittingarrowbundle");
		
		TextureSizeMap.clear();
	}

	/*@Override
    public boolean shouldRotateAroundWhenRendering()
    {
        return true;
    }*/
	
    public void getSubItems(int itemID, int metadata, CreativeTabs creativeTabs, List subItemList)
    {
        subItemList.add(new ItemStack(itemID, 1, metadata));
        subItemList.add(new ItemStack(itemID, 1, getItemDamageForArrowCount(metadata, 4)));
    }
	
    public void getSubItems(int itemID, CreativeTabs creativeTabs, List subItemList)
    {
        getSubItems(itemID, 0, creativeTabs, subItemList);
    }

}
