package zaggy1024.quivermod.items.bows;

import org.lwjgl.opengl.GL11;

import zaggy1024.quivermod.QuiverMod;

public class QuiverBowComposite extends QuiverBow
{

	public QuiverBowComposite(int index) {
		super(index);
        
        setMaxDamage(1024);
        setUnlocalizedName("quiverBowComposite");
		
		pullBackMult = 0.5F;
		damageMult = 1.25F;
		arrowSpeedMult = 0.65F;
		zoomMult = 0.8F;
		rangeMult = 1;

		arrowStep = 2;
	}
	
	@Override
	public float getSlow()
	{
		return 0.6F;
	}

    @Override
    public void doPreTransforms(boolean thirdPerson, boolean ifp, float px, float partialTick)
    {
    	GL11.glTranslatef(-2 * px, -2 * px, 0);
    	
    	if (!thirdPerson)
    		GL11.glTranslatef(0, 0, -0.01F);
    }

    @Override
    public void doBowTransforms(boolean thirdPerson, boolean ifp, float px, float partialTick)
    {
    	GL11.glTranslatef(2 * px, 2 * px, 0);
    	
    	if (!thirdPerson)
    		GL11.glTranslatef(0, 0, 0.01F);
    }
    
    public int getBrokenBowID()
    {
		return QuiverMod.brokenBowComposite.itemID;
    }

}
