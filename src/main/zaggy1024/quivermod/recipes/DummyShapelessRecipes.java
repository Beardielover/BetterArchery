package zaggy1024.quivermod.recipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

public class DummyShapelessRecipes extends ShapelessRecipes {

	public DummyShapelessRecipes(ItemStack outputStack, List<ItemStack> craftingStackList) {
		super(outputStack, craftingStackList);
	}
	
    public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World)
    {
        return false;
    }

}
