package zaggy1024.quivermod;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import zaggy1024.quivermod.blocks.BlockTileEntityTorch;
import zaggy1024.quivermod.blocks.ItemBlockTileEntityTorch;
import zaggy1024.quivermod.blocks.TileEntityTorch;
import zaggy1024.quivermod.client.*;
import zaggy1024.quivermod.config.QuiverModConfigManager;
import zaggy1024.quivermod.entities.EntityQuiverModArrow;
import zaggy1024.quivermod.entities.EntityQuiverModSkeleton;
import zaggy1024.quivermod.items.Items1Stacks;
import zaggy1024.quivermod.items.Items64Stacks;
import zaggy1024.quivermod.items.arrows.ISplittingArrow;
import zaggy1024.quivermod.items.arrows.ItemArrow;
import zaggy1024.quivermod.items.arrows.ItemDrillArrow;
import zaggy1024.quivermod.items.arrows.ItemPotionArrow;
import zaggy1024.quivermod.items.arrows.ItemQuiverModArrow;
import zaggy1024.quivermod.items.bows.BrokenBow;
import zaggy1024.quivermod.items.bows.QuiverBow;
import zaggy1024.quivermod.items.bows.QuiverBowComposite;
import zaggy1024.quivermod.items.bows.QuiverBowLong;
import zaggy1024.quivermod.items.bows.QuiverBowRecurve;
import zaggy1024.quivermod.items.bows.QuiverBowYumi;
import zaggy1024.quivermod.items.quiver.Quiver;
import zaggy1024.quivermod.recipes.DummyShapedRecipes;
import zaggy1024.quivermod.recipes.DummyShapelessRecipes;
import zaggy1024.quivermod.recipes.RecipesLongbow;
import zaggy1024.quivermod.recipes.RecipesPotionArrow;
import zaggy1024.quivermod.recipes.RecipesRecurveBow;
import zaggy1024.quivermod.recipes.RecipesSplittingArrow;
import zaggy1024.quivermod.util.PlayerValueManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid=QuiverMod.ID, name="Better Archery", version="1.6.4")
@NetworkMod(clientSideRequired=true, serverSideRequired=true,
		channels={"QuivSelServer", "QuivSelClient", "RemoveAllArrows",
		"BreakTorch", "SkeletonUse", "QuiverModConf",
		"QMArGetSpecial", "QMArImpaledItems"},
		packetHandler=QuiverModPacketHandler.class)
public class QuiverMod
{
	
	public static final String ID = "QuiverMod";
	
	public static boolean finishedLoading = false;
	
	public static int tileEntityTorchBlockID = 2012;
	public static BlockTileEntityTorch tileEntityTorchBlock = null;
	
	public static int quiverID = 7052;
	public static Quiver quiver = null;
	
	public static ArrayList<Item> arrowItemList = new ArrayList<Item>(){{
	}};
	
	public static HashMap<String, Item> arrowNamesMap = new HashMap();
	
	public static ArrayList<QuiverBow> bowItemList = new ArrayList();
	
	public static int bowID = 7053;
	public static QuiverBow bow = null;
	public static int brokenBowID = 7054;
	public static BrokenBow brokenBow = null;
	
	public static int bowRecurveID = 7055;
	public static QuiverBow bowRecurve = null;
	public static int brokenBowRecurveID = 7056;
	public static BrokenBow brokenBowRecurve = null;
	
	public static int yumiID = 7065;
	public static QuiverBow yumi = null;
	public static int brokenYumiID = 7066;
	public static BrokenBow brokenYumi = null;
	
	public static int longbowID = 7057;
	public static QuiverBow longbow = null;
	public static int brokenLongbowID = 7058;
	public static BrokenBow brokenLongbow = null;
	
	public static int bowCompositeID = 7068;
	public static QuiverBow bowComposite = null;
	public static int brokenBowCompositeID = 7069;
	public static BrokenBow brokenBowComposite = null;
	
	public static ItemArrow arrow = null;

	public static int fireArrowID = 7059;
	public static ItemQuiverModArrow fireArrow = null;
	
	public static int impactExplosiveArrowID = 7060;
	public static ItemQuiverModArrow impactExplosiveArrow = null;
	
	public static int timedExplosiveArrowID = 7061;
	public static ItemQuiverModArrow timedExplosiveArrow = null;
	
	public static int enderArrowID = 7062;
	public static ItemQuiverModArrow enderArrow = null;
	
	public static int torchArrowID = 7063;
	public static ItemQuiverModArrow torchArrow = null;
	
	public static int drillArrowID = 7067;
	public static ItemDrillArrow drillArrow = null;
	
	public static int potionArrowID = 7064;
	public static ItemPotionArrow potionArrow = null;
	
	public static int items1StacksID = 7070;
	public static Items1Stacks items1Stacks = null;
	
	//public static int items16StacksID = 7071;
	//public static Items16Stacks items16Stacks = null;
	
	public static int items64StacksID = 7072;
	public static Items64Stacks items64Stacks = null;
	
	public static int arrowheadID = 7073;
	public static ItemArrowHead arrowhead = null;
	
	public static CreativeTabs tabBows = new CreativeTabs("tabBows")
	{
	    public int getTabIconItemIndex() {
	    	return bowRecurve.itemID;
	    }
	};
	
	public static CreativeTabs tabArrows = new CreativeTabs("tabArrows")
	{
	    public int getTabIconItemIndex() {
	    	return Item.arrow.itemID;
	    }
	};
	
	public static PlayerValueManager playerValueManager = new PlayerValueManager();

	// The instance of your mod that Forge uses.
	@Instance("QuiverMod")
	public static QuiverMod instance;

	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide="zaggy1024.quivermod.client.ClientProxy", serverSide="zaggy1024.quivermod.CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInitRegister();
		
		QuiverModConfigManager.loadSettings(event);
		
		quiver = new Quiver(quiverID);

		bow = new QuiverBow(bowID);
		brokenBow = (BrokenBow)new BrokenBow(brokenBowID).
				setOriginalBowID(bow.itemID).
				setUnlocalizedName("quiverBowBroken");
		
		bowRecurve = new QuiverBowRecurve(bowRecurveID);
		brokenBowRecurve = (BrokenBow)new BrokenBow(brokenBowRecurveID).
				setOriginalBowID(bowRecurve.itemID).
				setUnlocalizedName("quiverBowRecurveBroken");
		
		yumi = new QuiverBowYumi(yumiID);
		brokenYumi = (BrokenBow)new BrokenBow(brokenYumiID).
				setOriginalBowID(yumi.itemID).
				setUnlocalizedName("quiverBowYumiBroken");
		
		longbow = new QuiverBowLong(longbowID);
		brokenLongbow = (BrokenBow)new BrokenBow(brokenLongbowID).
				setOriginalBowID(longbow.itemID).
				setUnlocalizedName("quiverBowLongBroken");
		
		bowComposite = new QuiverBowComposite(bowCompositeID);
		brokenBowComposite = (BrokenBow)new BrokenBow(brokenBowCompositeID).
				setOriginalBowID(bowComposite.itemID).
				setUnlocalizedName("quiverBowCompositeBroken");
		
		log("A message will follow about item slot 6 already being occupied, ignore it.");
		log("This is necessary to make normal arrows able to be split.");
		Item.arrow = arrow = (ItemArrow)new ItemArrow(6).setCreativeTab(QuiverMod.tabArrows);
		
		fireArrow = (ItemQuiverModArrow)new ItemQuiverModArrow(fireArrowID)
				.setInfinityChance(1)
				.setFullBright(200)
				.setFrameCount(4)
				.setUnlocalizedName("fireArrow");
		impactExplosiveArrow = (ItemQuiverModArrow)new ItemQuiverModArrow(impactExplosiveArrowID)
				.setInfinityChance(3)
				.setUnlocalizedName("impactArrow");
		timedExplosiveArrow = (ItemQuiverModArrow)new ItemQuiverModArrow(timedExplosiveArrowID)
				.setInfinityChance(3)
				.setUnlocalizedName("timedArrow");
		enderArrow = (ItemQuiverModArrow)new ItemQuiverModArrow(enderArrowID)
				.setInfinityChance(3)
				.setUnlocalizedName("enderArrow");
		torchArrow = (ItemQuiverModArrow)new ItemQuiverModArrow(torchArrowID)
				.setInfinityChance(1)
				.setFullBright(-180)
				.setUnlocalizedName("torchArrow");
		drillArrow = (ItemDrillArrow)new ItemDrillArrow(drillArrowID)
				.setInfinityChance(3)
				.setUnlocalizedName("drillArrow");
		
		potionArrow = new ItemPotionArrow(potionArrowID);

		items1Stacks = new Items1Stacks(items1StacksID);
		//items16Stacks = new Items16Stacks(items16StacksID);
		items64Stacks = new Items64Stacks(items64StacksID);
		
		tileEntityTorchBlock = (BlockTileEntityTorch)new BlockTileEntityTorch(tileEntityTorchBlockID, Material.circuits).setStepSound(Block.soundWoodFootstep);
	}
	
	public static boolean isArrow(Item item)
	{
		return item.itemID == Item.arrow.itemID || item instanceof ItemQuiverModArrow || item instanceof ItemPotionArrow;
	}
	
	public static boolean isArrow(ItemStack itemStack)
	{
		return isArrow(itemStack.getItem());
	}
	
	public static boolean arrayItemStackContains(ArrayList<ItemStack> list, ItemStack findStack)
	{
		for (ItemStack stack : list)
		{
			if (stack.isItemEqual(findStack))
			{
				return true;
			}
		}
		
		return false;
	}

	@EventHandler
	public void init(FMLInitializationEvent event) throws Exception
	{
		boolean client = event.getSide() == Side.CLIENT;
		
		List recipes = CraftingManager.getInstance().getRecipeList();

		GameRegistry.addRecipe(new ItemStack(bow),
				"sw ",
				"slw",
				"sw ",
				'l', Item.leather,
				's', Item.silk,
				'w', Item.stick);
		GameRegistry.addRecipe(new ItemStack(Block.dispenser),
				"ccc",
				"cbc",
				"crc",
				'c', Block.cobblestone,
				'b', bow,
				'r', Item.redstone);
		
		recipes.add(new RecipesRecurveBow());
		recipes.add(new DummyShapelessRecipes(new ItemStack(bowRecurve), new ArrayList<ItemStack>(){{
					add(new ItemStack(bow));
				}}));
		
		recipes.add(new RecipesLongbow());
		recipes.add(new DummyShapedRecipes(2, 3,
				new ItemStack[]{
					new ItemStack(Item.stick), null,
					new ItemStack(Item.silk), new ItemStack(bow),
					new ItemStack(Item.stick), null,
				},
				new ItemStack(longbow)));
		
		GameRegistry.addRecipe(new ItemStack(yumi),
				"scc",
				"slc",
				"sc ",
				'l', Item.leather,
				's', Item.silk,
				'c', Item.reed);
		
		GameRegistry.addRecipe(new ItemStack(bowComposite),
				"sb ",
				"slw",
				"sgt",
				'l', Item.leather,
				'b', Item.bone,
				's', Item.silk,
				'w', Item.stick,
				'g', items1Stacks.hideGlue,
				't', items64Stacks.sinew);
		
		if (QuiverModConfigManager.replaceVanillaTorchPlacing)
		{
			log("Replacing torch placing item to place tile entity torches.");
			log("Change \"replaceVanillaTorchPlacing\" in the config to false if you think this is a mistake.");
			int replaceID = Block.torchWood.blockID - 256;
			new ItemBlockTileEntityTorch(replaceID);
		}
		
		proxy.register();
		
		ForgeChunkManager.setForcedChunkLoadingCallback(instance, new EnderArrowLoadingCallback());
		
		if (QuiverModConfigManager.removeVanillaBowCrafting);
		{
			Iterator iter = recipes.iterator();
			
			while (iter.hasNext())
			{
				Object recipeObj = iter.next();
				
				if (recipeObj instanceof IRecipe)
				{
					ItemStack output = ((IRecipe)recipeObj).getRecipeOutput();
					
					if (output != null && output.itemID == Item.bow.itemID)
					{
						log("Removing bow crafting recipe");
						iter.remove();
					}
				}
			}
		}
		
		GameRegistry.addRecipe(new ItemStack(fireArrow),
				"c",
				"a",
				'c', new ItemStack(Item.coal, 1, 0),
				'a', Item.arrow);
		GameRegistry.addRecipe(new ItemStack(fireArrow),
				"c",
				"a",
				'c', new ItemStack(Item.coal, 1, 1),
				'a', Item.arrow);
		GameRegistry.addRecipe(new ItemStack(fireArrow, 2),
				"b ",
				"aa",
				'b', new ItemStack(Item.blazePowder),
				'a', Item.arrow);
		GameRegistry.addRecipe(new ItemStack(fireArrow, 2),
				" b",
				"aa",
				'b', new ItemStack(Item.blazePowder),
				'a', Item.arrow);
		
		GameRegistry.addRecipe(new ItemStack(impactExplosiveArrow),
				"gr",
				"a ",
				'g', Item.gunpowder,
				'r', Block.torchRedstoneActive,
				'a', Item.arrow);
		
		GameRegistry.addRecipe(new ItemStack(timedExplosiveArrow),
				"gg",
				"as",
				'g', Item.gunpowder,
				's', Item.silk,
				'a', Item.arrow);

		GameRegistry.addRecipe(new ItemStack(enderArrow),
				"p",
				"a",
				'p', Item.enderPearl,
				'a', Item.arrow);

		GameRegistry.addRecipe(new ItemStack(torchArrow),
				"t",
				"a",
				't', Block.torchWood,
				'a', Item.arrow);

		GameRegistry.addRecipe(new ItemStack(drillArrow, 3, 0),
				"fff",
				" pt",
				"aaa",
				'p', Block.pistonStickyBase,
				'f', Item.flint,
				't', Block.torchRedstoneActive,
				'a', Item.arrow);
		GameRegistry.addRecipe(new ItemStack(drillArrow, 1, 0),
				"f",
				"a",
				'f', Item.flint,
				'a', new ItemStack(drillArrow, 1, drillArrow.setBroken(0, true)));
		
		recipes.add(new RecipesPotionArrow());

		recipes.add(new RecipesSplittingArrow());
		
		if (client)
		{
			ArrayList<ItemStack> subItems = new ArrayList();
			potionArrow.getSubItems(arrow.itemID, tabArrows, subItems);
			
			for (ItemStack arrowStack : subItems)
			{
				int damage = arrowStack.getItemDamage();
				
				if (!potionArrow.isSplittingArrow(damage) && potionArrow.canCraftSplittingArrow(damage))
				{
					recipes.add(new DummyShapedRecipes(2, 2,
							new ItemStack[]{
								new ItemStack(Item.potion.itemID, 1, damage), null,
								new ItemStack(arrow), new ItemStack(arrow)
							},
							new ItemStack(potionArrow.itemID, 1, damage)));
				}
			}
		
			ItemStack stringStack = new ItemStack(Item.silk);
			
			for (Item arrow : arrowItemList)
			{
				if (arrow instanceof ISplittingArrow)
				{
					ISplittingArrow splittingArrow = (ISplittingArrow)arrow;
					subItems = new ArrayList();
					arrow.getSubItems(arrow.itemID, tabArrows, subItems);
					
					for (ItemStack arrowStack : subItems)
					{
						int damage = arrowStack.getItemDamage();
						
						if (!splittingArrow.isSplittingArrow(damage) && splittingArrow.canCraftSplittingArrow(damage))
						{
							ItemStack normalArrowStack = new ItemStack(arrow.itemID, 1, splittingArrow.getItemDamageForArrowCount(damage, 1));
							
							ArrayList<ItemStack> craftingItemList = new ArrayList();
							
							for (int i = 0; i < 4; i++)
							{
								craftingItemList.add(normalArrowStack);
							}
							
							for (int i = 0; i < 2; i++)
							{
								craftingItemList.add(stringStack);
							}
	
							recipes.add(new DummyShapelessRecipes(new ItemStack(arrow.itemID, 1, splittingArrow.getItemDamageForArrowCount(damage, 4)), craftingItemList));
						}
					}
				}
			}
		}
		
	    NetworkRegistry.instance().registerGuiHandler(this, new QuiverGuiHandler());

	    EntityRegistry.registerModEntity(EntityQuiverModArrow.class, "QuiverModArrow",
	    		0, this, 64, 20, false);

	    EntityRegistry.registerModEntity(EntityQuiverModSkeleton.class, "QuiverModSkeleton",
	    		1, this, 256, 1, true);
	    
        GameRegistry.registerTileEntity(TileEntityTorch.class, QuiverModConfigManager.tileEntityTorchID);
        
        saveArrowNamesMap();

		TickRegistry.registerTickHandler(new QuiverModServerTickHandler(), Side.SERVER);
	}

	private void saveArrowNamesMap()
	{
		for (Item arrow : arrowItemList)
		{
			String arrowUnlocName = arrow.getUnlocalizedName();
			String[] split = arrowUnlocName.split("\\.");
			arrowUnlocName = split[split.length - 1];
			arrowNamesMap.put(arrowUnlocName, arrow);
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		finishedLoading = true;
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		ICommandManager commandManager = server.getCommandManager();
		ServerCommandManager serverCommandManager = ((ServerCommandManager)commandManager);
		addCommands(serverCommandManager);
		
        DispenserBehaviorQuiverModArrow modDispBehavior = new DispenserBehaviorQuiverModArrow();
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.arrow, modDispBehavior);
        BlockDispenser.dispenseBehaviorRegistry.putObject(fireArrow, modDispBehavior);
        BlockDispenser.dispenseBehaviorRegistry.putObject(impactExplosiveArrow, modDispBehavior);
        BlockDispenser.dispenseBehaviorRegistry.putObject(timedExplosiveArrow, modDispBehavior);
        BlockDispenser.dispenseBehaviorRegistry.putObject(enderArrow, modDispBehavior);
        BlockDispenser.dispenseBehaviorRegistry.putObject(torchArrow, modDispBehavior);
        BlockDispenser.dispenseBehaviorRegistry.putObject(drillArrow, modDispBehavior);
        BlockDispenser.dispenseBehaviorRegistry.putObject(potionArrow, modDispBehavior);
	}
	
	public void addCommands(ServerCommandManager cmdManager)
	{
		cmdManager.registerCommand(new CommandGetSplittingArrowMetadata());
	}
	
	public static void log(Object obj)
	{
        /*FMLLog.log(FMLCommonHandler.instance().findContainerFor(instance).getModId(),
        		Level.INFO, obj == null ? "null" : obj.toString());*/
		if (logger == null)
		{
			logger = Logger.getLogger(ID);
		}

		if (obj == null)
			obj = "null";
		
		logger.info("[" + FMLCommonHandler.instance().getEffectiveSide() + "] " + obj.toString());
	}

	public static float getLargestRangeMult()
	{
		float out = 0;
		
		for (QuiverBow bow : bowItemList)
		{
			float range = bow.getRangeMult();
			
			if (range > out)
				out = range;
		}
		
		return out;
	}
	
}