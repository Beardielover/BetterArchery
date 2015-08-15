package zaggy1024.quivermod.items.arrows;

import java.util.*;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.items.misc.SplittingArrowDefaults;
import zaggy1024.quivermod.util.BitwiseHelper;
import zaggy1024.quivermod.util.TextHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ItemPotionArrow extends ItemPotion implements ISplittingArrow, IArrowIcons, IArrowItem {

	Map effectToSubItemMap = new LinkedHashMap();
	Map effectToSplittingSubItemMap = new LinkedHashMap();
	Icon[] icons = new Icon[4];
	
	public ItemPotionArrow(int par1) {
		super(par1);
		
		setMaxStackSize(64);
		setUnlocalizedName("potionArrow");
		setCreativeTab(QuiverMod.tabArrows);
		setFull3D();
		
		QuiverMod.arrowItemList.add(this);
	}
    
    protected String getName()
    {
    	String unlocName = getUnlocalizedName();
    	return unlocName.substring(unlocName.lastIndexOf('.') + 1);
    }

	public String getSpecialBowIconName(int damage)
	{
		return (isSplash(damage) ? "splash" : "") + getName().toLowerCase();
	}

	public float getFullBright()
	{
		return -1;
	}

	public int getFrameCount()
	{
		return 1;
	}

	public boolean getNeedsTwoPasses()
	{
		return true;
	}

	public String[] getSpecialBowIconNames()
	{
		String name = getName().toLowerCase();
		
		return new String[]{name, "splash" + name};
	}
    
    public int getRenderPasses(int damage)
    {
        return isSplittingArrow(damage) ? 3 : 2;
    }
	
    public Icon getIconFromDamageForRenderPass(int damage, int pass)
    {
    	if (pass > 1 && isSplittingArrow(damage))
    		return ItemQuiverModArrow.splittingArrowIcon;
    	
    	int index = 0;
    	
    	if (isSplash(damage))
    	{
    		index += 2;
    	}
    	
        return pass == 0 ? icons[index + 1] : icons[index];
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int pass)
    {
        return pass != 0 ? 16777215 : getColorFromDamage(par1ItemStack.getItemDamage());
    }
	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		return stack;
	}
	
	public ItemStack onFoodEaten(ItemStack stack, World world, EntityPlayer player)
	{
		return stack;
	}

    public String getItemDisplayName(ItemStack stack)
    {
        //return StringTranslate.getInstance().translateNamedKey(this.getLocalizedName(par1ItemStack)).trim();
    	String out = "";
    	
        if (stack.getItemDamage() == 0)
        {
            return StatCollector.translateToLocal(getUnlocalizedName()).trim();
        }
        else
        {
            String prefix = "";

            if (isSplash(stack.getItemDamage()))
            {
                prefix += StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
            }

            List effects = Item.potion.getEffects(stack);

            if (effects != null && !effects.isEmpty())
            {
                String nameUnloc = ((PotionEffect)effects.get(0)).getEffectName();
                nameUnloc += ".postfix";
                out += prefix + StatCollector.translateToLocal(nameUnloc).trim();
            }
            else
            {
            	String nameUnloc = PotionHelper.func_77905_c(stack.getItemDamage());
                out += StatCollector.translateToLocal(nameUnloc).trim() + " " + TextHelper.getLocalizedItemName(Item.potion.itemID);
            }
        }
        
        int damage = stack.getItemDamage();
        out += " " + ArrowNameHelper.getName(Item.arrow, isSplittingArrow(damage));
        
        out = ArrowNameHelper.getLocalizedSplittingArrowPrefix(this, out, damage);
        
        return out;
    }
	
    public void getSubItems(int itemID, CreativeTabs par2CreativeTabs, List par3List)
    {
        if (effectToSubItemMap.isEmpty())
        {
            for (int i = 0; i <= 32767; ++i)
            {
                List effects = PotionHelper.getPotionEffects(i, false);

                if (effects != null && !effects.isEmpty())
                {
                	if (!effectToSubItemMap.containsKey(effects))
                	{
                		effectToSubItemMap.put(effects, Integer.valueOf(i));
                		effectToSplittingSubItemMap.put(effects, Integer.valueOf(getItemDamageForArrowCount(i, 4)));
                	}
                }
            }
        }

        Iterator iter = effectToSubItemMap.values().iterator();

        while (iter.hasNext())
        {
            int damage = ((Integer)iter.next()).intValue();
            par3List.add(new ItemStack(itemID, 1, damage));
        }

        iter = effectToSplittingSubItemMap.values().iterator();

        while (iter.hasNext())
        {
            int damage = ((Integer)iter.next()).intValue();
            par3List.add(new ItemStack(itemID, 1, damage));
        }
    }
    
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		icons[0] = iconRegister.registerIcon("quivermod:potionarrowover");
		icons[1] = iconRegister.registerIcon("quivermod:potionarrowcolor");
		icons[2] = iconRegister.registerIcon("quivermod:potionarrowsplashover");
		icons[3] = iconRegister.registerIcon("quivermod:potionarrowsplashcolor");
	}

	private static final int POTIONMASK = 16511;
	private static final int SPLITSTART = 7;
	private static final int SPLITEND = 10;
	
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
		damage &= POTIONMASK;
		
		if (count < 2)
			count = 0;
		
		return BitwiseHelper.setInteger(damage, SPLITSTART, SPLITEND, count);
	}
	
	public boolean canCraftSplittingArrow(int damage)
	{
		return true;
	}

	@Override
	public int getInfinityChance() {
		return 2;
	}

	@Override
	public boolean canAddArrowHead(ItemStack stack) {
		return !isSplash(stack.getItemDamage());
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

}
