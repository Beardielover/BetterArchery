package zaggy1024.quivermod.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import zaggy1024.quivermod.*;
import zaggy1024.quivermod.items.bows.QuiverBow;
import zaggy1024.quivermod.items.quiver.QuiverInventory;
import zaggy1024.quivermod.util.PlayerValueManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.TickType;

public class QuiverKeyHandler extends KeyHandler {

	static KeyBinding next = new KeyBinding("Next Arrow Slot", Keyboard.KEY_EQUALS);
	static KeyBinding prev = new KeyBinding("Previous Arrow Slot", Keyboard.KEY_MINUS);
	static KeyBinding wornQuiverType = new KeyBinding("Cycle Quiver Types", Keyboard.KEY_BACK);

	public QuiverKeyHandler() {
		super(new KeyBinding[]{next, prev, wornQuiverType},
				new boolean[]{false, false, false});
	}

	@Override
	public String getLabel() {
		return "QuiverModBindings";
	}
	
	private void clearBowUse(EntityPlayer player)
	{
		ItemStack heldItem = player.inventory.getCurrentItem();
		
		if (heldItem != null && heldItem.getItem() instanceof QuiverBow)
		{
			player.clearItemInUse();
		}
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
	{
		Minecraft mc = QuiverMod.proxy.mc;
		
		if (tickEnd && mc.currentScreen == null)
		{
			PlayerValueManager valMan = QuiverMod.playerValueManager;
			EntityPlayer player = mc.thePlayer;
			String playerName = player.getEntityName();

			boolean updatedPlayerValues = false;
			
			boolean isNext = kb.keyCode == next.keyCode;
			boolean isPrev = kb.keyCode == prev.keyCode;
			
			if (isNext || isPrev)
			{
				int selectedArrowItem = valMan.getSelectedArrowItem(playerName);
				
				if (isNext)
				{
					selectedArrowItem++;
				}
				else
				{
					selectedArrowItem--;
				}
				
				int arrowItemCount = valMan.getArrowTypesHeld(player).size();
				
				if (selectedArrowItem >= arrowItemCount)
					selectedArrowItem = 0;
				else if (selectedArrowItem < 0)
					selectedArrowItem = arrowItemCount - 1;
				
				valMan.setSelectedArrowItem(playerName, selectedArrowItem);
				
				int[] indexes = valMan.getIndexesToUse(player);
				int quiverIndex = indexes[0];
				int arrowIndex = indexes[1];
				
				if (quiverIndex >= 0 && arrowIndex >= 0)
				{
					ItemStack quiver = player.inventory.getStackInSlot(quiverIndex);
					ItemStack arrow = new QuiverInventory(quiver).getStackInSlot(arrowIndex);
					
					if (arrow != null)
					{
						String infoText = arrow.getDisplayName();
						//String extraText = arrow.getItem().getPotionEffect();
						
						QuiverMod.proxy.overlayHandler.setInfoText(infoText);
					}
				}

				updatedPlayerValues = true;
			}
			
			if (kb.keyCode == wornQuiverType.keyCode)
			{
				int value = valMan.getWornQuiverType(playerName) + 1;
				
				if (value >= PlayerValueManager.wornQuiverTypes)
					value = 0;
				
				valMan.setWornQuiverType(playerName, value);
				valMan.sendValuesToServer(player);
			}
			
			if (updatedPlayerValues)
			{
				clearBowUse(player);
				QuiverMod.proxy.overlayHandler.resetSelectionAnimation();
				valMan.updateUsingArrow(player);
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
	}

	@Override
	public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT);
	}

}
