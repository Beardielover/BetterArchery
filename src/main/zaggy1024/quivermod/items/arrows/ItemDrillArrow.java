package zaggy1024.quivermod.items.arrows;

import java.util.List;

import zaggy1024.quivermod.util.BitwiseHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;

public class ItemDrillArrow extends ItemQuiverModArrow {

	public Icon drillHeadIcon;

	public ItemDrillArrow(int index) {
		super(index);
	}
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		super.registerIcons(iconRegister);
		
		drillHeadIcon = iconRegister.registerIcon("quivermod:drillarrowhead");
	}
	
	public static final int BROKEN = 4;
	
	public boolean isBroken(int damage)
	{
		return BitwiseHelper.getBoolean(damage, BROKEN);
	}
	
	public int setBroken(int damage, boolean broken)
	{
		return BitwiseHelper.setBoolean(damage, BROKEN, broken);
	}
	
    public String getItemDisplayName(ItemStack stack)
    {
        return (isBroken(stack.getItemDamage()) ? StatCollector.translateToLocal("quiverModArrow.prefix.broken") + " " : "") + super.getItemDisplayName(stack);
    }
	
	public boolean canCraftSplittingArrow(int damage)
	{
		return !isBroken(damage);
	}

    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamageForRenderPass(int damage, int pass)
    {
    	if (pass == 0)
    		return itemIcon;
    	
    	if (pass == 1 && !isBroken(damage))
    		return drillHeadIcon;
    	else
    		return splittingArrowIcon;
    }
    
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
    
    public int getRenderPasses(int damage)
    {
        return super.getRenderPasses(damage) + (isBroken(damage) ? 0 : 1);
    }

}
