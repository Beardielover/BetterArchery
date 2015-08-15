package zaggy1024.quivermod.items.bows;

import net.minecraft.client.renderer.texture.IconRegister;

import org.lwjgl.opengl.GL11;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.config.QuiverModConfigManager;

public class QuiverBowYumi extends QuiverBowLong {

	public QuiverBowYumi(int index) {
		super(index);
        
        setMaxDamage(384);
        setUnlocalizedName("quiverBowYumi");
		
		pullBackMult = 1.15F;
		damageMult = 1F;
		arrowSpeedMult = 1.5F;
		zoomMult = 1.45F;
		rangeMult = 1.5F;
		
		animIconCount = 5;
		iconSize = 2;
		scale = 2;
		firstPersonScale = 2;
		scaleOffX = 17;
		scaleOffY = 17;
	}
	
	public String getSmallBowIconName()
	{
		return "quiverbowyumiicon";
	}
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		registerIcons(this, iconRegister);
	}
	
	public void registerIconsHelper(QuiverBow instance, IconRegister iconRegister)
	{
		registerIcons(instance, iconRegister);
	}
    
    public int getBrokenBowID()
    {
		return QuiverMod.brokenYumi.itemID;
    }

    @Override
    public void doPreTransforms(boolean thirdPerson, boolean ifp, float px, float partialTick)
    {
    	GL11.glTranslatef(1 * px, 1 * px, 0);
    	
    	if (!ifp)
    	{
	    	if (thirdPerson)
	    	{
	        	GL11.glTranslatef(-2 * px, 2 * px, 0);
	        	//GL11.glRotatef(-15, 1, 1, 0);
	        	//GL11.glRotatef(-8, 0, 0, 1);
	    	}
	    	else
	    	{
	        	//GL11.glTranslatef(0, 0.1F, 0.2F);
	    	}
    	}
    }

    @Override
    public void doBowTransforms(boolean thirdPerson, boolean ifp, float px, float partialTick)
    {
    	GL11.glTranslatef(-1 * px, -1 * px, 0);
    	
    	GL11.glTranslatef(-4 * px, 5 * px, 0);
    	
    	if (!ifp)
    	{
	    	if (thirdPerson)
	    	{
	        	//GL11.glRotatef(-15, 0, 0, 1);
	    	}
	    	else
	    	{
	        	//GL11.glTranslatef(0, 0.1F, 0.2F);
	    	}
    	}
    }

}
