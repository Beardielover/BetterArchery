package zaggy1024.quivermod.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

//import sharose.mods.guiapi.ModSettingScreen;
//import sharose.mods.guiapi.ModSettings;

import zaggy1024.quivermod.*;
import zaggy1024.quivermod.blocks.TileEntityTorch;
import zaggy1024.quivermod.blocks.TileEntityTorchRenderer;
import zaggy1024.quivermod.client.renderers.QuiverModOverlayRenderer;
import zaggy1024.quivermod.client.renderers.QuiverModTickHandler;
import zaggy1024.quivermod.client.renderers.QuiverRenderPlayerBase;
//import zaggy1024.quivermod.config.ContainModSettings;
//import zaggy1024.quivermod.config.QuiverModSettings;
import zaggy1024.quivermod.entities.EntityQuiverModArrow;
import zaggy1024.quivermod.entities.RenderQuiverModArrow;
import zaggy1024.quivermod.items.bows.QuiverBow;
import zaggy1024.quivermod.items.bows.QuiverBowRenderer;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import api.player.render.RenderPlayerAPI;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

	public static final String LOCALIZATION_PATH = "assets/quivermod/lang/";

	public float fovModifierHand = -1;
	
	//public ContainModSettings modSettingsContainer;

	public ClientProxy()
	{
	}
	
	@Override
	public void preInitRegister()
	{
		try
		{
			RenderPlayerAPI.register("quivermod", QuiverRenderPlayerBase.class);
		}
		catch (NoClassDefFoundError e)
		{
			QuiverMod.log("RenderPlayer API not found. Quivers will not be rendered on players.");
		}
		
		makeSettingsGui();
		
		MinecraftForge.EVENT_BUS.register(new QuiverModOverlayRenderer());
	}

	/**
	 * List directory contents for a resource folder. Not recursive.
	 * This is basically a brute-force implementation.
	 * Works for regular files and also JARs.
	 * 
	 * @author Greg Briggs
	 * @param clazz Any java class that lives in the same place as the resources you want.
	 * @param path Should end with "/", but not start with one.
	 * @return Just the name of each member item, not the full paths.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
		URL dirURL = clazz.getClassLoader().getResource(path);
		
		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			return new File(dirURL.toURI()).list();
		}

		if (dirURL == null) {
			String me = clazz.getName().replace(".", "/")+".class";
			dirURL = clazz.getClassLoader().getResource(me);
		}

		if (dirURL.getProtocol().equals("jar")) {
			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
			
			while(entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				
				if (name.startsWith(path)) { //filter according to the path
					String entry = name.substring(path.length());
					int checkSubdir = entry.indexOf("/");
					
					if (checkSubdir >= 0) {
						// if it is a subdirectory, we just return the directory name
						entry = entry.substring(0, checkSubdir);
					}
					
					result.add(entry);
				}
			}
			
			jar.close();
			return result.toArray(new String[result.size()]);
		}

		throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
	}

	@Override
	public void register() {
		mc = Minecraft.getMinecraft();
		
		overlayHandler = new QuiverModTickHandler();
		TickRegistry.registerTickHandler(overlayHandler, Side.CLIENT);

		KeyBindingRegistry.registerKeyBinding(new QuiverKeyHandler());

		QuiverBowRenderer bowRenderer = new QuiverBowRenderer();

		for (Item bow : QuiverMod.bowItemList)
		{
			MinecraftForgeClient.registerItemRenderer(bow.itemID, bowRenderer);
		}

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTorch.class, new TileEntityTorchRenderer());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityArrow.class, new RenderQuiverModArrow());
		
		String[] langList = new String[0];

		try
		{
			langList = getResourceListing(getClass(), LOCALIZATION_PATH);
		}
		catch (Exception ex) {
			QuiverMod.log("Failed to get list of language files in \"" + LOCALIZATION_PATH + "\".");
			ex.printStackTrace();
		}

		for (String langFilename : langList)
		{
			try
			{
				String path = "/" + LOCALIZATION_PATH + langFilename;
				QuiverMod.log("Attempting to load language file at \"" + path + "\"");
				int dot = langFilename.lastIndexOf('.');
				
				if (dot != -1)
				{
					String lang = langFilename.substring(0, dot);
					String extension = langFilename.substring(dot + 1);
					boolean xml = extension.equals("xml");
					LanguageRegistry.instance().loadLocalization(path, lang, xml);
					QuiverMod.log("Success!");
				}
				else
				{
					QuiverMod.log("Found but failed to load filename: " + langFilename);
				}
			}
			catch (Exception ex) {
				QuiverMod.log("Language loading failed while attempting to load \"/" + LOCALIZATION_PATH + langFilename + "\"");
				ex.printStackTrace();
			}
		}
	}
	
	public void makeSettingsGui()
	{
		/*try
		{
			ModSettingScreen modSettingScreen = new ModSettingScreen("Better Bows Settings (R: restart, C: client)", "Better Bows");
			QuiverModSettings modSettings = new QuiverModSettings(QuiverMod.ID, modSettingScreen);
			modSettingsContainer = new ContainModSettings(modSettings, modSettingScreen);
		}
		catch (NoClassDefFoundError e)
		{
			
		}*/
	}

	@Override
	public void onBowUse(EntityPlayer player, float frameTime)
	{
		if (fovModifierHand == -1)
			fovModifierHand = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, "fovModifierHand", "field_78507_R");

		ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, fovModifierHand, "fovModifierHandPrev", "field_78506_S");

		float fov = 1.0F;

		if (player.capabilities.isFlying)
		{
			fov *= 1.1F;
		}

		float speedOnGround = 0.1F;

        AttributeInstance attributeinstance = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        fov = (fov * (((float)attributeinstance.getAttributeValue() / player.capabilities.getWalkSpeed() + 1) / 2));

		QuiverBow bow = (QuiverBow)player.getItemInUse().getItem();
		int useDuration = player.getItemInUseDuration();

		if (useDuration < 0)
			useDuration = 0;

		float zoom = useDuration / (20.0F * bow.getPullBackMult());

		if (zoom > 1.0F)
		{
			zoom = 1.0F;
		}
		else
		{
			zoom *= zoom;
		}

		fov *= 1 - zoom * 0.15F * bow.getZoomMultiplier();

		fovModifierHand += (fov - fovModifierHand) * 0.1F * frameTime;

		if (fovModifierHand > 1.5F)
		{
			fovModifierHand = 1.5F;
		}

		if (fovModifierHand < 0.1F)
		{
			fovModifierHand = 0.1F;
		}

		ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, fovModifierHand, "fovModifierHand", "field_78507_R");
	}

	@Override
	public void resetSavedFOV() {
		fovModifierHand = -1;
	}
	
	private void addEffect(EntityFX fx)
	{
		QuiverMod.proxy.mc.effectRenderer.addEffect(fx);
	}

	@Override
	public void spawnDiggingFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ, Block block, int side, int metadata)
	{
		EntityDiggingFX fx = new EntityDiggingFX(world, x, y, z, motionX, motionY, motionZ, block, side, metadata);
		addEffect(fx);
	}

}