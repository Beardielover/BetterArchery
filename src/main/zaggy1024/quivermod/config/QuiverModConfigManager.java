package zaggy1024.quivermod.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

//import sharose.mods.guiapi.ModSettings;
//import sharose.mods.guiapi.Setting;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.client.ClientProxy;
import zaggy1024.quivermod.client.renderers.QuiverRenderPlayerBase;
import zaggy1024.quivermod.entities.RenderQuiverModArrow;
import zaggy1024.quivermod.items.bows.QuiverBow;
import zaggy1024.quivermod.util.InventorySlot;
import zaggy1024.quivermod.util.InventorySlots;

public class QuiverModConfigManager {
	
	private static HashMap<String, String> comments = new HashMap(){{
		put("entityTorchBlock", "Block IDs");
		
		put("bow", "Item IDs");
		
		put("useChunkLoader", "Whether to use a chunk loader for the ender arrows to keep them moving when leaving loaded chunks. May cause severe lag with multiple arrows in the air.");
		put("arrowSlowMo", "How many ticks it should take for an arrow to complete one tick. 0 = No slowmo, 2 = Half-speed");
		put("offsetArrows", "Whether the arrows should be offset to the right to match the position of the bow in first person.");
		put("allowSkeletonGriefing", "Whether skeletons should be allowed to spawn with fire arrows or impact explosive arrows (in hard mode).");
		put("allowSkeletonPotionArrows", "Whether skeletons should be allowed to spawn with potion arrows.");
		put("arrowKnockbackMode", "0 = Vanilla arrow knockback, 1 = The higher the armor, the less the knockback, 2 = The less the armor, the less the knockback.");
		put("replaceVanillaTorchPlacing", "Whether to replace the vanilla torch item with one that places torches wherever on a block you right-click, like torch arrows. Must be the same on the server and client to join.");
		put("bowInfinityMode", "0 = Vanilla, 1 = Per-arrow chance, 2 = 25% chance.");

		put("highResIcons", "Whether the 32x32 textures for large bows should be used for non-equipped bows.");
		put("animationStyle", "The animation style to use for selected arrows. 0 = bouncing, 1 = bounce once, 2 = move up, stop");
		put("fancyArrowDistance", "The distance at which arrows should stop being rendered in 3D.");
		put("renderQuiverIn3DDistance", "The distance at which quivers on players should stop being rendered with proper sides.");
		put("quiverHotbarVerticalOffset", "The amount (in pixels) to offset the quiver hotbar. Higher = lower.");
	}};
	
	public static boolean settingsLoaded = false;
	
	public static String configFilePath;
	public static Configuration config;
	
	/* General */
	public static boolean useChunkLoader = true;
	public static int slowMoSetting = 0;
	public static boolean offsetArrows = true;
	public static boolean allowSkeletonGriefing = false;
	public static boolean allowSkeletonPotionArrows = true;
	public static int arrowKnockbackMode = 0;
	
	public static boolean replaceVanillaTorchPlacing = false;
	public static boolean removeVanillaBowCrafting = true;
	public static String tileEntityTorchID = "TileEntityTorch";

	public static int bowInfinityMode = 1;

	/* Client stuff */
	public static boolean highResIcons = false;
	public static int animationStyle = 0;
	
	public static int fancyArrowDistance = 50;
	
	public static int renderQuiverIn3DDistance = 50;

	public static int quiverHotbarVerticalOffset = 0;
	
	private static Property getProperty(String category, String key, Object defaultValue)
	{
		Property property = null;
		
		if (defaultValue instanceof String)
			property = config.get(category, key, (String)defaultValue);
		else if (defaultValue instanceof Integer)
			property = config.get(category, key, (Integer)defaultValue);
		else if (defaultValue instanceof Boolean)
			property = config.get(category, key, (Boolean)defaultValue);
		else if (defaultValue instanceof String[])
			property = config.get(category, key, (String[])defaultValue);
		else if (defaultValue instanceof int[])
			property = config.get(category, key, (int[])defaultValue);
		else if (defaultValue instanceof boolean[])
			property = config.get(category, key, (boolean[])defaultValue);
		
		String comment = comments.get(key);
		
		if (comment != null)
			property.comment = comment;
		
		return property;
	}
	
	private static Property getItemProperty(String key, Object defaultValue)
	{
		return getProperty(Configuration.CATEGORY_ITEM, key, defaultValue);
	}
	
	private static Property getBlockProperty(String key, Object defaultValue)
	{
		return getProperty(Configuration.CATEGORY_BLOCK, key, defaultValue);
	}
	
	private static Property getGenProperty(String key, Object defaultValue)
	{
		return getProperty(Configuration.CATEGORY_GENERAL, key, defaultValue);
	}
	
	private static void loadIDs()
	{
        QuiverMod.tileEntityTorchBlockID = getBlockProperty("entityTorchBlock", QuiverMod.tileEntityTorchBlockID).getInt();

    	QuiverMod.quiverID = getItemProperty("quiver", QuiverMod.quiverID).getInt();

    	QuiverMod.bowID = getItemProperty("bow", QuiverMod.bowID).getInt();
    	QuiverMod.brokenBowID = getItemProperty("brokenBow", QuiverMod.brokenBowID).getInt();
    	
    	QuiverMod.bowRecurveID = getItemProperty("bowRecurve", QuiverMod.bowRecurveID).getInt();
    	QuiverMod.brokenBowRecurveID = getItemProperty("brokenBowRecurve", QuiverMod.brokenBowRecurveID).getInt();
    	
    	QuiverMod.longbowID = getItemProperty("longbow", QuiverMod.longbowID).getInt();
    	QuiverMod.brokenLongbowID = getItemProperty("brokenLongbow", QuiverMod.brokenLongbowID).getInt();
    	
    	QuiverMod.yumiID = getItemProperty("yumi", QuiverMod.yumiID).getInt();
    	QuiverMod.brokenYumiID = getItemProperty("brokenYumi", QuiverMod.brokenYumiID).getInt();
    	
    	QuiverMod.bowCompositeID = getItemProperty("bowComposite", QuiverMod.bowCompositeID).getInt();
    	QuiverMod.brokenBowCompositeID = getItemProperty("brokenBowComposite", QuiverMod.brokenBowCompositeID).getInt();

    	QuiverMod.fireArrowID = getItemProperty("fireArrow", QuiverMod.fireArrowID).getInt();
    	QuiverMod.impactExplosiveArrowID = getItemProperty("impactExplosiveArrow", QuiverMod.impactExplosiveArrowID).getInt();
    	QuiverMod.timedExplosiveArrowID = getItemProperty("timedExplosiveArrow", QuiverMod.timedExplosiveArrowID).getInt();
    	QuiverMod.enderArrowID = getItemProperty("enderArrow", QuiverMod.enderArrowID).getInt();
    	QuiverMod.torchArrowID = getItemProperty("torchArrow", QuiverMod.torchArrowID).getInt();
    	QuiverMod.drillArrowID = getItemProperty("drillArrow", QuiverMod.drillArrowID).getInt();

    	QuiverMod.potionArrowID = getItemProperty("potionArrow", QuiverMod.potionArrowID).getInt();

    	QuiverMod.items1StacksID = getItemProperty("items1Stacks", QuiverMod.items1StacksID).getInt();
    	//items16StacksID = getItemProperty("items16Stacks", items16StacksID).getInt();
    	QuiverMod.items64StacksID = getItemProperty("items64Stacks", QuiverMod.items64StacksID).getInt();
	}

	public static void loadSettings(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
    	
    	useChunkLoader = getGenProperty("useChunkLoader", useChunkLoader).getBoolean(useChunkLoader);
    	slowMoSetting = getGenProperty("arrowSlowMo", slowMoSetting).getInt();
    	offsetArrows = getGenProperty("offsetArrows", offsetArrows).getBoolean(offsetArrows);
    	allowSkeletonGriefing = getGenProperty("allowSkeletonGriefing", allowSkeletonGriefing).getBoolean(allowSkeletonGriefing);
    	allowSkeletonPotionArrows = getGenProperty("allowSkeletonPotionArrows", allowSkeletonPotionArrows).getBoolean(allowSkeletonPotionArrows);
    	arrowKnockbackMode = getGenProperty("arrowKnockbackMode", arrowKnockbackMode).getInt();

    	replaceVanillaTorchPlacing = getGenProperty("replaceVanillaTorchPlacing", replaceVanillaTorchPlacing).getBoolean(replaceVanillaTorchPlacing);
    	removeVanillaBowCrafting = getGenProperty("removeVanillaBowCrafting", removeVanillaBowCrafting).getBoolean(removeVanillaBowCrafting);
    	tileEntityTorchID = getGenProperty("tileEntityTorchID", tileEntityTorchID).getString();
    	
    	bowInfinityMode = getGenProperty("bowInfinityMode", bowInfinityMode).getInt();

    	if (QuiverMod.proxy instanceof ClientProxy)
    	{
    		highResIcons = getGenProperty("highResIcons", highResIcons).getBoolean(highResIcons);
    		animationStyle = getGenProperty("animationStyle", 0).getInt();
    		fancyArrowDistance = getGenProperty("fancyArrowDistance", fancyArrowDistance).getInt();
    		renderQuiverIn3DDistance = getGenProperty("renderQuiverIn3DDistance", renderQuiverIn3DDistance).getInt();
    		quiverHotbarVerticalOffset = getGenProperty("quiverHotbarVerticalOffset", quiverHotbarVerticalOffset).getInt();
    		
    		//ContainModSettings settings = ((ClientProxy)QuiverMod.proxy).modSettingsContainer;
    		
    		//if (settings != null)
    		//	settings.modSettings.load();
    	}
    	
    	loadIDs();

        config.save();
        
        settingsLoaded = true;
	}
	
	public static void sendConfigPacketToServer()
	{
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
	    
	    try {
	    	DataOutputStream outputStream = new DataOutputStream(byteOutputStream);
	    	
	    	outputStream.writeBoolean(useChunkLoader);
	    	outputStream.writeInt(slowMoSetting);
	    	outputStream.writeBoolean(offsetArrows);
	    	outputStream.writeBoolean(allowSkeletonGriefing);
	    	outputStream.writeBoolean(allowSkeletonPotionArrows);
	    	outputStream.writeInt(arrowKnockbackMode);
	    	
	    	outputStream.writeBoolean(replaceVanillaTorchPlacing);
	    	outputStream.writeBoolean(removeVanillaBowCrafting);
	    	outputStream.writeUTF(tileEntityTorchID);
	    	
	    	outputStream.writeInt(bowInfinityMode);
	    }
	    catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "QuiverModConf";
		packet.data = byteOutputStream.toByteArray();
		packet.length = byteOutputStream.size();
		
		PacketDispatcher.sendPacketToServer(packet);
	}
	
	public static void readValuesFromPacket(EntityPlayer fromPlayer, Packet250CustomPayload packet, DataInputStream input) throws IOException
	{
		boolean allowChange = false;
		boolean onServer = fromPlayer instanceof EntityPlayerMP;
		
		if (fromPlayer instanceof EntityClientPlayerMP)
		{
			allowChange = true;	// It's from the server (probably), allow it
		}
		else if (onServer &&
				MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(fromPlayer.username))
		{
			allowChange = true;	// It's from a client, verify before allowing
		}
		
		if (allowChange)
		{
			useChunkLoader = input.readBoolean();
			slowMoSetting = input.readInt();
			offsetArrows = input.readBoolean();
			allowSkeletonGriefing = input.readBoolean();
			allowSkeletonPotionArrows = input.readBoolean();
			arrowKnockbackMode = input.readInt();

			replaceVanillaTorchPlacing = input.readBoolean();
			removeVanillaBowCrafting = input.readBoolean();
			tileEntityTorchID = input.readUTF();
			
			bowInfinityMode = input.readInt();
			
			saveSettings(true);
		}
		
		if (onServer)
			PacketDispatcher.sendPacketToAllPlayers(packet);
	}
	
	/*
	 * To be called every time settings are changed in-game.
	 */
	public static void saveSettings(boolean fromPacket) {
		getGenProperty("useChunkLoader", useChunkLoader).set(useChunkLoader);
		getGenProperty("arrowSlowMo", slowMoSetting).set(slowMoSetting);
		getGenProperty("offsetArrows", offsetArrows).set(offsetArrows);
		getGenProperty("allowSkeletonGriefing", allowSkeletonGriefing).set(allowSkeletonGriefing);
		getGenProperty("allowSkeletonPotionArrows", allowSkeletonPotionArrows).set(allowSkeletonPotionArrows);
		getGenProperty("arrowKnockbackMode", arrowKnockbackMode).set(arrowKnockbackMode);

    	getGenProperty("replaceVanillaTorchPlacing", replaceVanillaTorchPlacing).set(replaceVanillaTorchPlacing);
    	getGenProperty("removeVanillaBowCrafting", removeVanillaBowCrafting).set(removeVanillaBowCrafting);
    	getGenProperty("tileEntityTorchID", tileEntityTorchID).set(tileEntityTorchID);
    	
		getGenProperty("bowInfinityMode", bowInfinityMode).set(bowInfinityMode);
		
		getGenProperty("highResIcons", highResIcons).set(highResIcons);
		QuiverBow.resetIcons();
		
		getGenProperty("animationStyle", animationStyle).set(animationStyle);
		getGenProperty("fancyArrowDistance", fancyArrowDistance).set(fancyArrowDistance);
		getGenProperty("renderQuiverIn3DDistance", renderQuiverIn3DDistance).set(renderQuiverIn3DDistance);
		getGenProperty("quiverHotbarVerticalOffset", quiverHotbarVerticalOffset).set(quiverHotbarVerticalOffset);
		
		QuiverRenderPlayerBase.renderQuiverIn3DDistanceSqr = -1;
		RenderQuiverModArrow.distance3DSqr = -1;
		
		config.save();
		
		if (!fromPacket)
			sendConfigPacketToServer();
	}

}
