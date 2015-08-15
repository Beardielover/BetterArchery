package zaggy1024.quivermod.client.quiverslots;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.items.arrows.ItemQuiverModArrow;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class SlotArrow extends Slot {

	public SlotArrow(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		
		setBackgroundIcon(ItemQuiverModArrow.emptySlotIcon);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		if (QuiverMod.isArrow(stack))
			return true;
		
		return false;
	}

}
