package zaggy1024.quivermod.util;

import zaggy1024.quivermod.items.arrows.ISplittingArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;

public class TextHelper {
	
	public static String getLocalizedItemName(int itemID)
	{
		Item item = Item.itemsList[itemID];
		
		return StatCollector.translateToLocal(item.getUnlocalizedName()).trim();
	}

}
