package zaggy1024.quivermod.items.bows;

import zaggy1024.quivermod.QuiverMod;
import net.minecraft.creativetab.CreativeTabs;

public class QuiverBowRecurve extends QuiverBow {

	public QuiverBowRecurve(int index) {
		super(index);
        
        setMaxDamage(256);
        setUnlocalizedName("quiverBowRecurve");
		
		pullBackMult = 0.9F;
		damageMult = 1.1F;
		arrowSpeedMult = 1.25F;
		zoomMult = 1.35F;
		rangeMult = 1.25F;
	}
    
    public int getBrokenBowID()
    {
        return QuiverMod.brokenBowRecurve.itemID;
    }

}
