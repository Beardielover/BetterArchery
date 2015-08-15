package zaggy1024.quivermod.recipes;

import zaggy1024.quivermod.QuiverMod;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesRecurveBow implements IRecipe {

	ItemStack dummyOutput = null;

	@Override
	public boolean matches(InventoryCrafting inventoryCrafting, World world)
	{
		return getCraftingResult(inventoryCrafting) != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting)
	{
		int stackCount = 0;
		
		ItemStack bow = null;
		
		for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
		{
			ItemStack stack = inventoryCrafting.getStackInSlot(i);
			
			if (stack != null)
			{
				if (stack.itemID == QuiverMod.bow.itemID)
				{
					bow = stack;
				}
				
				stackCount++;
			}
		}
		
		if (bow != null && stackCount == 1 && bow.getItemDamage() < QuiverMod.bowRecurve.getMaxDamage())
		{
			ItemStack output = bow.copy();
			output.itemID = QuiverMod.bowRecurve.itemID;
			
			return output;
		}
		
		return null;
	}

	@Override
	public int getRecipeSize()
	{
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		if (dummyOutput == null)
			dummyOutput = new ItemStack(QuiverMod.bowRecurve);
		
		return dummyOutput;
	}

}
