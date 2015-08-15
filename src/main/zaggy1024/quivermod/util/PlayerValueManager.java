package zaggy1024.quivermod.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.items.quiver.QuiverInventory;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

public class PlayerValueManager {

    private HashMap<String,HashMap<String,Integer>> playerData = new HashMap();
    private HashMap<String,Integer> defaultValues = new HashMap<String,Integer>(){{
    	put("selectedArrowItem", 0);
    	put("usingArrowItemID", Item.arrow.itemID);
    	put("usingArrowMeta", 0);
    	put("usingQuiverMetadata", -1);
    	put("wornQuiverType", 0);
    }};
    
    public static int wornQuiverTypes = 2;

	public PlayerValueManager()
	{
		QuiverMod.log("QuiverMod: Initialized a player value manager for " + FMLCommonHandler.instance().getEffectiveSide());
	}
    
    public HashMap<String,Integer> getValues(String playerName)
    {
    	return playerData.get(playerName);
    }
	
	public HashMap<String,Integer> setDefaultValues(String playerName)
	{
		HashMap<String,Integer> defValsClone = (HashMap<String,Integer>)defaultValues.clone();
		playerData.put(playerName, defValsClone);
		return defValsClone;
	}
    
    public int getValue(String playerName, String key)
    {
    	HashMap<String,Integer> hashMap = getValues(playerName);
    	
    	if (hashMap == null)
    	{
    		hashMap = setDefaultValues(playerName);
    	}
    	
    	if (!hashMap.containsKey(key))
    		hashMap.put(key, defaultValues.get(key));
    	
    	return hashMap.get(key);
    }
    
    public void setValue(String playerName, String key, int value)
    {
    	HashMap<String,Integer> newHashMap = playerData.get(playerName);
    	
    	if (newHashMap == null)
    	{
    		newHashMap = setDefaultValues(playerName);
    	}
    	
    	newHashMap.put(key, value);
    	
    	playerData.put(playerName, newHashMap);
    }
    
    public Packet getPlayerValuePacket(EntityPlayer player, boolean toServer)
    {
    	String playerName = player.getEntityName();
    	ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    
	    try {
	    	DataOutputStream outputStream = new DataOutputStream(byteOutputStream);
	        outputStream.writeUTF(playerName);
	    	
	    	outputStream.writeInt(getWornQuiverType(playerName));
	    	
	    	if (toServer)
	    	{
		        outputStream.writeInt(player.inventory.currentItem);
		        outputStream.writeInt(getSelectedArrowItem(playerName));
	    	}
	    	else
	    	{
	    		int arrowItemID = -1;
	    		int arrowDamage = -1;
	    		int quiverDamage = -1;
	    		
	    		InventorySlots slots = getArrowSlot(player);
	    		
	    		if (slots != null)
	    		{
		    		ItemStack usingArrowStack = slots.get("arrow").stack;
		    		
		    		if (usingArrowStack != null)
		    		{
		    			arrowItemID = usingArrowStack.itemID;
		    			arrowDamage = usingArrowStack.getItemDamage();
		    		}
		    		
		    		InventorySlot quiverSlot = slots.get("quiver");
		    		
		    		if (quiverSlot != null)
		    		{
		    			ItemStack quiver = quiverSlot.stack;
		    			
		    			if (quiver != null)
		    			{
		    				quiverDamage = quiver.getItemDamage();
		    				
							ItemStack heldStack = player.getHeldItem();
							
							if (heldStack != null && heldStack.itemID == QuiverMod.quiver.itemID && heldStack.equals(quiver))
							{
								int heldQuiverID = QuiverMod.quiver.getUniqueID(heldStack);
								
								if (QuiverMod.quiver.getUniqueID(quiver) == heldQuiverID)
								{
									quiverDamage = -1;
								}
							}
		    			}
		    		}
	    		}
	    		
    			outputStream.writeInt(arrowItemID);
    			outputStream.writeInt(arrowDamage);
    			outputStream.writeInt(quiverDamage);
	    	}
	    }
	    catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "QuivSel" + (toServer ? "Server" : "Client");
		packet.data = byteOutputStream.toByteArray();
		packet.length = byteOutputStream.size();
		
		return packet;
    }
	
	public void sendValuesToServer(EntityPlayer player)
	{
		if (player.worldObj.isRemote && player == QuiverMod.proxy.mc.thePlayer)
		{
			((EntityClientPlayerMP)player).sendQueue.addToSendQueue(getPlayerValuePacket(player, true));
		}
	}
	
	public void readValues(EntityPlayer player, byte[] data, int dataLength, boolean forServer)
	{
		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		DataInputStream inputStream = new DataInputStream(byteInput);
		String playerName = null;
		
		try {
			playerName = inputStream.readUTF();
			
			setWornQuiverType(playerName, inputStream.readInt());
			
			if (forServer)
			{
				player.inventory.currentItem = inputStream.readInt();
				setSelectedArrowItem(playerName, inputStream.readInt());
			}
			else
			{
				setUsingArrow(playerName, inputStream.readInt(), inputStream.readInt());
				setUsingQuiver(playerName, inputStream.readInt());
			}
		}
		catch (Exception ex) {
            ex.printStackTrace();
		}
		
		if (forServer)
		{
	        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
	        ServerConfigurationManager servMan = server.getConfigurationManager();
	        EntityPlayer forPlayer = servMan.getPlayerForUsername(playerName);
			Packet packet = getPlayerValuePacket(forPlayer, false);
	        
	        if (packet != null)
	        {
	            servMan.sendToAllNearExcept(forPlayer, forPlayer.posX, forPlayer.posY, forPlayer.posZ, 100, forPlayer.dimension, packet);
	        }
		}
	}
    
    public int getSelectedArrowItem(String playerName)
    {
    	return getValue(playerName, "selectedArrowItem");
    }
    
    public void setSelectedArrowItem(String playerName, int value)
    {
    	setValue(playerName, "selectedArrowItem", value);
    }
    
    public int getUsingArrowItemID(String playerName)
    {
    	return getValue(playerName, "usingArrowItemID");
    }
    
    public int getUsingArrowMetadata(String playerName)
    {
    	return getValue(playerName, "usingArrowMeta");
    }
    
    public void setWornQuiverType(String playerName, int value)
    {
    	setValue(playerName, "wornQuiverType", value);
    }
    
    public int getWornQuiverType(String playerName)
    {
    	return getValue(playerName, "wornQuiverType");
    }
    
    public ItemStack getUsingArrowStack(String playerName)
    {
    	int arrowItemID = getUsingArrowItemID(playerName);
    	
    	if (arrowItemID >= 0)
    		return new ItemStack(arrowItemID, 1, getUsingArrowMetadata(playerName));
    	
    	return null;
    }
    
    public void setUsingArrow(String playerName, int itemID, int metadata)
    {
    	setValue(playerName, "usingArrowItemID", itemID);
    	setValue(playerName, "usingArrowMeta", metadata);
    }
    
    public ItemStack getUsingQuiverStack(String playerName)
    {
    	int metadata = getValue(playerName, "usingQuiverMetadata");
    	
    	if (metadata == -1)
    		return null;
    	
    	return new ItemStack(QuiverMod.quiver.itemID, 1, metadata);
    }
    
    public void setUsingQuiver(String playerName, int metadata)
    {
    	setValue(playerName, "usingQuiverMetadata", metadata);
    }
    
    public void updateUsingArrow(EntityPlayer player)
    {
    	String playerName = player.getEntityName();
    	InventorySlots slots = getArrowSlot(player);
    	
    	if (slots != null)
    	{
			ItemStack usingArrowStack = slots.get("arrow").stack;
			
			if (usingArrowStack != null)
			{
				setUsingArrow(playerName, usingArrowStack.itemID, usingArrowStack.getItemDamage());
				return;
			}
    	}
    	
		setUsingArrow(playerName, Item.arrow.itemID, 0);
    }

	public static ArrayList<ItemStack> getQuivers(EntityPlayer player)
	{
		ArrayList<ItemStack> quiverStacks = new ArrayList();
		
		for (ItemStack stack : player.inventory.mainInventory)
		{
			if (stack != null && stack.itemID == QuiverMod.quiver.itemID)
			{
				quiverStacks.add(stack);
			}
		}
		
		return quiverStacks;
	}
    
    public ArrayList<ItemStack> getArrowTypesHeld(EntityPlayer player)
    {
    	ArrayList<ItemStack> quivers = getQuivers(player);
    	
    	ArrayList<ItemStack> arrows = new ArrayList<ItemStack>();
    	
    	for (ItemStack stack : quivers)
    	{
    		if (stack != null)
    		{
    			QuiverInventory quiverInv = new QuiverInventory(stack);
    			
    			for (ItemStack arrowStack : quiverInv.inv)
    			{
    				if (arrowStack != null)
    				{
	    				ItemStack addStack = arrowStack.copy();
	    				addStack.stackSize = 1;
	    				
	    				if (!QuiverMod.arrayItemStackContains(arrows, addStack))
	    				{
	    					arrows.add(addStack);
	    				}
    				}
    			}
    		}
    	}
    	
    	return arrows;
    }
    
    public int[] getIndexesToUse(EntityPlayer player)
    {
    	String playerName = player.getEntityName();
    	int quiverIndex = -1;
    	int arrowIndex = -1;
    	
		ItemStack[] inv = player.inventory.mainInventory;
		ArrayList<ItemStack> arrowTypesHeld = getArrowTypesHeld(player);
		int arrowTypeCount = arrowTypesHeld.size();
		int arrowItem = getSelectedArrowItem(playerName);
		
		if (arrowTypeCount > 0)
		{
			if (arrowItem >= arrowTypeCount)
			{
				arrowItem = arrowTypeCount - 1;
				setSelectedArrowItem(playerName, arrowItem);
			}
			
			ItemStack arrowType = arrowTypesHeld.get(arrowItem);
			int arrowID = arrowType.itemID;
			int arrowDamage = arrowType.getItemDamage();
			
			for (int quiverI = inv.length - 1; quiverI >= 0; quiverI--)
			{
				ItemStack quiverStack = inv[quiverI];
				
				if (quiverStack != null && quiverStack.itemID == QuiverMod.quiver.itemID)
				{
					QuiverInventory quiverInv = new QuiverInventory(quiverStack);
					
					for (int arrowI = quiverInv.size - 1; arrowI >= 0; arrowI--)
					{
						ItemStack arrowStack = quiverInv.getStackInSlot(arrowI);
						
						if (arrowStack != null && arrowStack.itemID == arrowID && arrowStack.getItemDamage() == arrowDamage)
						{
							quiverIndex = quiverI;
							arrowIndex = arrowI;
							break;
						}
					}
				}
				
				if (arrowIndex != -1)
					break;
			}
		}
    	
    	return new int[]{quiverIndex, arrowIndex};
    }

	public int getArrowStackIndexToUse(EntityPlayer player, ArrayList<ItemStack> quiverStacks)
	{
		String playerName = player.getEntityName();

		int arrowID = QuiverMod.arrowItemList.get(getSelectedArrowItem(playerName)).itemID;
		
		for (int i = quiverStacks.size() - 1; i >= 0; i++)
		{
			QuiverInventory inv = new QuiverInventory(quiverStacks.get(i));
			
			for (int arrowI = inv.size - 1; i >= 0; i++)
			{
				ItemStack arrowStack = inv.getStackInSlot(arrowI);
				if (arrowStack != null && arrowStack.itemID == arrowID)
				{
					return arrowI;
				}
			}
		}
		
		return -1;
	}
    
    public InventorySlots getArrowQuiverSlot(EntityPlayer player)
    {
    	InventorySlots out = null;
    	
        int[] indexes = getIndexesToUse(player);
        int quiverIndex = indexes[0];
        int arrowIndex = indexes[1];
        ItemStack quiverStack = null; 
        ItemStack arrowStack = null;
        
        if (quiverIndex >= 0)
        {
        	out = new InventorySlots();
        	quiverStack = player.inventory.getStackInSlot(quiverIndex);
        	InventorySlot quiver = new InventorySlot(quiverStack, quiverIndex);
        	out.set("quiver", quiver);
        	
        	arrowStack = new QuiverInventory(quiverStack).getStackInSlot(arrowIndex);
        	
        	if (arrowStack != null)
        	{
	        	InventorySlot arrow = new InventorySlot(arrowStack, arrowIndex);
	        	out.set("arrow", arrow);
        	}
        }
        
        return out;
    }
    
    public InventorySlots getArrowSlot(EntityPlayer player)
    {
    	InventorySlots out = getArrowQuiverSlot(player);
        
        if (out == null || out.get("arrow") == null)
        {
        	int i = 0;
        	
        	for (ItemStack arrow : player.inventory.mainInventory)
        	{
        		if (arrow != null && QuiverMod.isArrow(arrow))
        		{
        			out = new InventorySlots();
        			out.set("arrow", new InventorySlot(arrow, i));
        			break;
        		}
        		
        		i++;
        	}
        }
        
        return out;
    }

}
