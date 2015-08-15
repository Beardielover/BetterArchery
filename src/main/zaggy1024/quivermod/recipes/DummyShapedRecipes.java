package zaggy1024.quivermod.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

public class DummyShapedRecipes extends ShapedRecipes {

	public DummyShapedRecipes(int width, int height, ItemStack[] recipeStacks, ItemStack outputStack) {
		super(width, height, recipeStacks, outputStack);
	}
	
    public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World)
    {
        return false;
    }

}
