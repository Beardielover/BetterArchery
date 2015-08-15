package zaggy1024.quivermod.recipes;

import zaggy1024.quivermod.QuiverMod;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipesPotionArrow implements IRecipe
{
	private ItemStack dummyOutput = null;
	
	private ItemStack getPotionStack(InventoryCrafting inventoryCrafting)
	{
    	int arrowIndex = -1;
    	int stackCount = 0;
    	
    	for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
    	{
    		ItemStack stack = inventoryCrafting.getStackInSlot(i);
    		
    		if (stack != null)
    		{
	    		if (stack.itemID == Item.arrow.itemID)
	    		{
	    			arrowIndex = i;
	    		}
	    		
	    		stackCount++;
    		}
    	}
    	
    	if (stackCount == 3 && arrowIndex >= 3)
    	{
    		ItemStack arrow2 = inventoryCrafting.getStackInSlot(arrowIndex - 1);
    		
    		if (arrow2 != null)
    		{
	    		ItemStack potionStack = inventoryCrafting.getStackInSlot(arrowIndex - 3);
	    		
	    		if (potionStack == null && arrowIndex >= 4)
	    		{
	    			potionStack = inventoryCrafting.getStackInSlot(arrowIndex - 4);
	    		}
	    		
	    		if (potionStack != null && potionStack.itemID == Item.potion.itemID)
	    		{
	    			return potionStack;
	    		}
    		}
    	}
    	
    	return null;
	}
	
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inventoryCrafting, World par2World)
    {
        return getPotionStack(inventoryCrafting) != null;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting)
    {
    	ItemStack potionStack = getPotionStack(inventoryCrafting);
    	ItemStack arrowStack = new ItemStack(QuiverMod.potionArrow.itemID, 2, QuiverMod.potionArrow.getItemDamageForArrowCount(potionStack.getItemDamage(), 0));
    	
    	if (potionStack.stackTagCompound != null && potionStack.stackTagCompound.hasKey("CustomPotionEffects"))
    	{
    		if (arrowStack.stackTagCompound == null)
    			arrowStack.stackTagCompound = new NBTTagCompound();
    		
    		arrowStack.stackTagCompound.setTag("CustomPotionEffects", potionStack.stackTagCompound.getTag("CustomPotionEffects"));
    	}
    	
    	return arrowStack;
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return 2;
    }

    public ItemStack getRecipeOutput()
    {
    	if (dummyOutput == null)
    		dummyOutput = new ItemStack(QuiverMod.potionArrow.itemID, 2, 0);
    	
        return dummyOutput;
    }
}