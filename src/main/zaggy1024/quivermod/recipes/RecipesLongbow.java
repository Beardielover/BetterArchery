package zaggy1024.quivermod.recipes;

import zaggy1024.quivermod.QuiverMod;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesLongbow implements IRecipe {

	ItemStack dummyOutput = null;

	@Override
	public boolean matches(InventoryCrafting inventoryCrafting, World world)
	{
		return getCraftingResult(inventoryCrafting) != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting)
	{
		for (int x = 0; x < 2; x++)
		{
			ItemStack bow = inventoryCrafting.getStackInRowAndColumn(x + 1, 1);
			
			if (bow != null && bow.itemID == QuiverMod.bow.itemID)
			{
				int bowDamage = bow.getItemDamage();
				
				if (bowDamage < QuiverMod.longbow.getMaxDamage())
				{
					ItemStack string = inventoryCrafting.getStackInRowAndColumn(x + 0, 1);
					
					if (string != null && string.itemID == Item.silk.itemID)
					{
						ItemStack stick0 = inventoryCrafting.getStackInRowAndColumn(x + 0, 0);
						ItemStack stick1 = inventoryCrafting.getStackInRowAndColumn(x + 0, 2);
						
						if (stick0 != null && stick1 != null && stick0.itemID == Item.stick.itemID && stick1.itemID == Item.stick.itemID)
						{
							ItemStack output = bow.copy();
							output.itemID = QuiverMod.longbow.itemID;
							return output;
						}
					}
				}
			}
		}
		
		return null;
	}

	@Override
	public int getRecipeSize()
	{
		return 1;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		if (dummyOutput == null)
			dummyOutput = new ItemStack(QuiverMod.longbow);
		
		return dummyOutput;
	}

}
