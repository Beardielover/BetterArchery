package zaggy1024.quivermod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import zaggy1024.quivermod.items.arrows.ISplittingArrow;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

public class CommandGetSplittingArrowMetadata extends CommandBase {
	
	private static String[] arrowNames = null;

	@Override
	public String getCommandName() {
		return "splitarrow";
	}
	
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "splitarrow <(optional, defaults to held item) arrow item name> <arrow count> <(optional) initial metadata for potions> <yes/no whether to give a stack to the player>";
    }

	@Override
	public void processCommand(ICommandSender commandSender, String[] stringArray) {
		if (stringArray.length >= 1)
		{
			EntityPlayer player = (EntityPlayer)commandSender;
			
			int index = 1;
			ItemStack arrowStack = null;
			Item arrow = QuiverMod.arrowNamesMap.get(stringArray[0]);
			int origDamage = -1;
			
			if (arrow == null)
			{
				arrowStack = player.getHeldItem();
				
				if (arrowStack != null)
				{
					arrow = arrowStack.getItem();
					origDamage = arrowStack.getItemDamage();
					index = 0;
				}
			}
			
			if (arrow != null && arrow instanceof ISplittingArrow)
			{
				ISplittingArrow splittingArrow = (ISplittingArrow)arrow;
				
				int count = Integer.parseInt(stringArray[index]);
				
				boolean give = false;
				
				for (int i = 0; i < 2; i++)
				{
					if (index + 1 < stringArray.length)
					{
						String next = stringArray[index + 1];
						
						if (next.toLowerCase().matches("true|yes|y"))
						{
							give = true;
						}
						else if (origDamage == -1 && next == Integer.toString(Integer.parseInt(next)))
						{
							origDamage = Integer.parseInt(next);
						}
						
						index++;
					}
				}
				
				int splitDamage = splittingArrow.getItemDamageForArrowCount(origDamage, count);
				
				int actualCount = splittingArrow.getSplittingArrowCount(splitDamage);
				
				if (actualCount != 0 && actualCount != count)
				{
					commandSender.sendChatToPlayer(ChatMessageComponent.createFromText("This type of arrow must be limited to an arrow count of less than or equal to " + actualCount + "."));
				}
				
				String message = "";
				
				String displayName = (actualCount <= 1 ? "normal " : "") + new ItemStack(arrow, 1, splitDamage).getDisplayName().toLowerCase();
				message += "The damage for " + aOrAn(actualCount > 0 ? toEnglish(actualCount) : displayName) + " " + displayName + " is: ";
				
				message += Integer.toString(splitDamage);
				
				commandSender.sendChatToPlayer(ChatMessageComponent.createFromText(message));
				
				if (give)
				{
					if (arrowStack != null)
					{
						arrowStack.setItemDamage(splitDamage);
						commandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Changed metadata of " + player.getEntityName() + "'s held item to the corresponding value."));
					}
					else
					{
						ItemStack newStack  = new ItemStack(arrow.itemID, 64, splitDamage);
						player.inventory.addItemStackToInventory(newStack);
						commandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Gave " + player.getEntityName() + " a full stack of " + arrow.getItemDisplayName(newStack) + "."));
					}
					
					player.inventory.onInventoryChanged();
				}
			}
		}
		else
		{
			throw new WrongUsageException(getCommandUsage(commandSender));
		}
	}
	
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] stringArray)
    {
		if (stringArray.length == 1)
		{
			if (arrowNames == null)
			{
				ArrayList<String> listOfNames = new ArrayList<String>();
				
				for (Object strObj : QuiverMod.arrowNamesMap.keySet())
				{
					if (strObj instanceof String)
						listOfNames.add((String)strObj);
				}
				
				String[] nameArray = new String[listOfNames.size()];
				int i = 0;
				
				for (String string : listOfNames)
				{
					nameArray[i] = string;
					i++;
				}
				
				arrowNames = nameArray;
			}
			
			return getListOfStringsMatchingLastWord(stringArray, arrowNames);
		}
		
        return null;
    }
	
	private static final Character[] vowels = new Character[]{
		'a', 'e', 'i', 'o', 'u'
	};
    
    public static String aOrAn(String str)
    {
    	str = str.toLowerCase();
    	
    	for (Character vowel : vowels)
    	{
    		if (str.startsWith(vowel.toString()))
    		{
    			return "an";
    		}
    	}
    	
    	return "a";
    }
	
	private static final String[] initialNumbers = new String[]{
		"zero",
		"one",
		"two",
		"three",
		"four",
		"five",
		"six",
		"seven",
		"eight",
		"nine",
		"ten",
		"eleven",
		"twelve",
		"thirteen",
		"fourteen",
		"fifteen",
		"sixteen",
		"seventeen",
		"eighteen",
		"nineteen"
	};
	private static final HashMap<Integer, String> bigNumbers = new HashMap(){{
		put(90, "ninety");
		put(80, "eighty");
		put(70, "seventy");
		put(60, "sixty");
		put(50, "fifty");
		put(40, "forty");
		put(30, "thirty");
		put(20, "twenty");
	}};
	private static List<Integer> bigNumberList = null;
	private static final HashMap<Integer, String> biggerNumbers = new HashMap(){{
		put(100, "hundred");
		put(1000, "thousand");
		put(1000000, "million");
		put(1000000000, "billion");
	}};
	private static List<Integer> biggerNumberList = null;
	
	private static Comparator<Integer> reverseSorting = new Comparator<Integer>() {
	    public int compare(Integer int1, Integer int2) {
	        return (int1 > int2 ? -1 : (int1 == int2 ? 0 : 1));
	    }
	};
	
	public static String toEnglish(int i)
	{
		if (bigNumberList == null)
		{
			bigNumberList = new ArrayList(bigNumbers.keySet());
			Collections.sort(bigNumberList, reverseSorting);
			
			biggerNumberList = new ArrayList(biggerNumbers.keySet());
			Collections.sort(biggerNumberList, reverseSorting);
		}
		
		String out = "";
		
		if (i < 0)
		{
			out += "negative ";
			i *= -1;
		}
		
		if (i > 0)
		{
			for (int biggerNumber : biggerNumberList)
			{
				if (i >= biggerNumber)
				{
					int numBigNumbers = i / biggerNumber;
					out += toEnglish(numBigNumbers) + " " + biggerNumbers.get(biggerNumber) + " ";
					i -= numBigNumbers * biggerNumber;
				}
				
				if (i == 0)
					break;
			}
		}
		
		if (i > 0)
		{
			for (int bigNumber : bigNumberList)
			{
				if (i >= bigNumber)
				{
					out += bigNumbers.get(bigNumber) + " ";
					i -= bigNumber;
				}
				
				if (i == 0)
					break;
			}
		}
		
		if (i > 0 && i < initialNumbers.length)
		{
			out += initialNumbers[i] + " ";
			i = 0;
		}
		
		return out.trim();
	}

}
