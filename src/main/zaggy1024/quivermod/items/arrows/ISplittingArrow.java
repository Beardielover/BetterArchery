package zaggy1024.quivermod.items.arrows;

import net.minecraft.item.ItemStack;

public interface ISplittingArrow {

	public int getSplittingArrowCount(int damage);
	
	public boolean isSplittingArrow(int damage);
	
	public int getItemDamageForArrowCount(int damage, int count);

	public boolean canCraftSplittingArrow(int damage);
	
}
