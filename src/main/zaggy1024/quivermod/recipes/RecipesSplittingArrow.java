package zaggy1024.quivermod.recipes;

import java.util.ArrayList;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.items.arrows.ISplittingArrow;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesSplittingArrow implements IRecipe {

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
		
		ItemStack firstArrowStack = null;
		ArrayList<ItemStack> arrowStacks = new ArrayList();
		
		ItemStack firstSplittingStack = null;
		int extraArrowsZeroDamage = -1;
		int extraArrows = 0;
		
		ArrayList<ItemStack> stringStacks = new ArrayList();
		
		for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
		{
			ItemStack stack = inventoryCrafting.getStackInSlot(i);
			
			if (stack != null)
			{
				Item item = stack.getItem();
				boolean rightItem = false;
				
				if ((firstArrowStack == null || stack.itemID == firstArrowStack.itemID) && item instanceof ISplittingArrow)
				{
					ISplittingArrow splittingArrow = (ISplittingArrow)item;
					int damage = stack.getItemDamage();
					
					if (!splittingArrow.isSplittingArrow(damage))
					{
						if (firstArrowStack == null)
							firstArrowStack = stack;
						
						arrowStacks.add(stack);
						rightItem = true;
					}
					else
					{
						int stackZeroCountDamage = splittingArrow.getItemDamageForArrowCount(damage, 0);
						
						if (extraArrowsZeroDamage == -1 || stackZeroCountDamage == extraArrowsZeroDamage)
						{
							if (extraArrowsZeroDamage == -1)
							{
								firstSplittingStack = stack;
								extraArrowsZeroDamage = stackZeroCountDamage;
							}
							
							extraArrows += splittingArrow.getSplittingArrowCount(damage);
							rightItem = true;
						}
					}
				}
				else if (stack.itemID == Item.silk.itemID)
				{
					stringStacks.add(stack);
					rightItem = true;
				}
				
				if (!rightItem)
				{
					return null;
				}
				
				stackCount++;
			}
		}
		
		if (firstArrowStack == null && firstSplittingStack != null)
		{
			ISplittingArrow splittingArrow = (ISplittingArrow)firstSplittingStack.getItem();
			
			if (splittingArrow.getSplittingArrowCount(firstSplittingStack.getItemDamage()) < extraArrows)
			{
				firstArrowStack = firstSplittingStack;
			}
		}

		if (firstArrowStack != null)
		{
			ISplittingArrow splittingArrow = (ISplittingArrow)firstArrowStack.getItem();
			int arrowStackDamage = firstArrowStack.getItemDamage();
			
			if (extraArrows == 0 || (splittingArrow.getItemDamageForArrowCount(arrowStackDamage, 0) == extraArrowsZeroDamage))
			{
				int arrowCount = arrowStacks.size();
				int fullArrowCount = arrowCount + extraArrows;
				int damageForCount = splittingArrow.getItemDamageForArrowCount(arrowStackDamage, fullArrowCount);
				
				if (splittingArrow.getSplittingArrowCount(damageForCount) == fullArrowCount)
				{
					int stringCount = stringStacks.size();
					int stringNeeded = (fullArrowCount / 2) - (extraArrows / 2);
					
					if (stringCount == stringNeeded)
					{
						ItemStack output = firstArrowStack.copy();
						output.stackSize = 1;
						output.setItemDamage(damageForCount);
						return output;
					}
				}
			}
		}
		
		return null;
	}

	@Override
	public int getRecipeSize()
	{
		return 4;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		if (dummyOutput == null)
			dummyOutput = new ItemStack(QuiverMod.arrow.itemID, 1, QuiverMod.arrow.getItemDamageForArrowCount(0, 4));
		
		return dummyOutput;
	}

}
