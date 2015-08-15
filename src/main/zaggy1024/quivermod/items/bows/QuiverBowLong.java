package zaggy1024.quivermod.items.bows;

import org.lwjgl.opengl.GL11;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.config.QuiverModConfigManager;
import zaggy1024.quivermod.entities.EntityQuiverModArrow;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class QuiverBowLong extends QuiverBow {
	
	float firstPersonScale = 2;

	public QuiverBowLong(int index) {
		super(index);
        
        setMaxDamage(384);
        setUnlocalizedName("quiverBowLong");
		
		pullBackMult = 1.5F;
		damageMult = 1F;
		arrowSpeedMult = 2F;
		zoomMult = 1.75F;
		rangeMult = 2F;

		animIconCount = 7;
		iconSize = 2;
		scale = 2 * 0.8F;
		scaleOffX = 17;
		scaleOffY = 17;
		arrowStep = 1;
	}
    
    public String getBowArrowIconName()
    {
    	return "longbow";
    }
    
    public float getScale(boolean thirdPerson)
    {
    	if (thirdPerson)
    		return scale;
    	else
    		return firstPersonScale;
    }
	
	public String getSmallBowIconName()
	{
		return "quiverbowlongicon";
	}
	
	public static void registerIcons(QuiverBow instance, IconRegister iconRegister)
	{
		QuiverBow.registerIcons(instance, iconRegister);
	}
	
	public void registerIconsHelper(QuiverBow instance, IconRegister iconRegister)
	{
		registerIcons(instance, iconRegister);
	}
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		registerIcons(this, iconRegister);
	}
    
    public int getBrokenBowID()
    {
		return QuiverMod.brokenLongbow.itemID;
    }

    @Override
    public void doPreTransforms(boolean thirdPerson, boolean ifp, float px, float partialTick)
    {
    	if (!ifp)
    	{
	    	if (thirdPerson)
	    	{
	        	GL11.glRotatef(15, 1, 1, 0);
	    		//GL11.glScalef(0.8F, 0.8F, 1);
	    		//GL11.glTranslatef(-0.5F, -0.5F, 0);
	    	}
	    	else
	    	{
	        	GL11.glTranslatef(0, 0.1F, 0.2F);
	    	}
    	}
    }

    protected void setExtraArrowSettings(EntityQuiverModArrow arrow, ItemStack stack, World world, EntityPlayer player, int itemInUseCount, ItemStack arrowStack)
    {
    	//arrow.breakGlass = 2;
    	//arrow.breakGlassVel = 4;
    	
    	arrow.arrowLength = 1.25F;
    }

}
