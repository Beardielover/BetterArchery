package zaggy1024.quivermod.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Items64Stacks extends ItemMultiItems {

	public ItemStack sinew = new ItemStack(itemID, 1, 0);

	public Items64Stacks(int index) {
		super(index);
		
		names = new String[] {"sinew"};
		
		GameRegistry.addShapelessRecipe(sinew, Item.rottenFlesh);
	}

}
