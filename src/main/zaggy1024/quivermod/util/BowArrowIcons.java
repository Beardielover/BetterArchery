package zaggy1024.quivermod.util;

import java.util.*;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.client.ClientProxy;
import zaggy1024.quivermod.items.arrows.IArrowIcons;

import net.minecraft.util.Icon;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BowArrowIcons {

	public static HashMap<String, HashMap<String, Object>> iconArrayMap = new HashMap();
	public static HashMap<String, Float> fullBrightMap = new HashMap();
	public static boolean registered = false;

	public static void register(String bowName, String specialArrowName, IconRegister iconRegister,
			int frames)
	{
		String name = "quivermod:bowarrowicons/" + bowName;
		
		HashMap<String, Object> specialArrowMap = iconArrayMap.get(bowName);
		
		if (specialArrowMap == null)
			specialArrowMap = new HashMap();
		
		if (frames > 1)
		{
			Icon[] arrowIcons = new Icon[4];
			
			for (int i = 0; i < arrowIcons.length; i++)
			{
				arrowIcons[i] = iconRegister.registerIcon(name + specialArrowName + i);
			}
			
			specialArrowMap.put(specialArrowName, arrowIcons);
		}
		else
		{
			Icon arrowIcon = iconRegister.registerIcon(name + specialArrowName);
			specialArrowMap.put(specialArrowName, arrowIcon);
		}
		
		iconArrayMap.put(bowName, specialArrowMap);
	}

	public static void registerArrowIcons(String bowName, IconRegister iconRegister)
	{
		for (Item arrowItem : QuiverMod.arrowItemList)
		{
			if (arrowItem instanceof IArrowIcons)
			{
				IArrowIcons arrowIcons = (IArrowIcons)arrowItem;
				String[] specialArrowNames = arrowIcons.getSpecialBowIconNames();
				
				for (int i = 0; i < specialArrowNames.length; i++)
				{
					String specialArrowName = specialArrowNames[i];
					
					register(bowName, specialArrowName, iconRegister, arrowIcons.getFrameCount());
					
					if (arrowIcons.getNeedsTwoPasses())
						register(bowName, specialArrowName + "1", iconRegister, arrowIcons.getFrameCount());
					
					fullBrightMap.put(specialArrowName, arrowIcons.getFullBright());
				}
			}
		}
		
		register(bowName, "splittingarrow", iconRegister, 1);
	}
	
	public static Icon getIcon(String bowName, ItemStack arrow, int iconOffset, int pass, boolean splitting)
	{
		ClientProxy.mc.mcProfiler.startSection("getArrowIcon");
		Icon output = null;
		
		if (arrow.getItem() instanceof IArrowIcons)
		{
			IArrowIcons arrowIcons = (IArrowIcons)arrow.getItem();
			
			String specialArrowName = arrowIcons.getSpecialBowIconName(arrow.getItemDamage());
			HashMap<String, Object> bowArrowIconMap = iconArrayMap.get(bowName);
			Object icons = null;
			
			switch (pass)
			{
			case 0:
			case 1:
				icons = bowArrowIconMap.get(specialArrowName + (pass > 0 ? pass : ""));
				break;
			case 2:
				if (splitting)
					icons = bowArrowIconMap.get("splittingarrow");
				break;
			}
			
			if (icons != null)
			{
				if (icons instanceof Icon)
				{
					output = (Icon)icons;
				}
				else if (icons instanceof Icon[])
				{
					Icon[] iconsArray = (Icon[])icons;
					int index = iconOffset >= iconsArray.length ? iconsArray.length - 1 : iconOffset;
					output = iconsArray[index];
				}
			}
		}
		
		ClientProxy.mc.mcProfiler.endSection();
		return output;
	}
	
	public static float getFullBright(String bowName, String itemArrowName)
	{
		if (fullBrightMap.containsKey(itemArrowName))
		{
			return fullBrightMap.get(itemArrowName);
		}
		
		return -1;
	}

}
