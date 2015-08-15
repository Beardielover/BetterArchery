package zaggy1024.quivermod.items.arrows;

import java.util.HashMap;

import cpw.mods.fml.common.registry.GameRegistry;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.recipes.DummyShapedRecipes;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ArrowheadTypeManager {
	
	public static class ArrowheadTypeStats {
		public int id;
		public String name;
		public int craftingItem;
		public float damage;
		public float mass;
		
		public ArrowheadTypeStats(int id, String name, Item craftingItem, float damage, float mass)
		{
			this.id = id;
			this.name = name;
			this.craftingItem = craftingItem.itemID;
			this.damage = damage;
			this.mass = mass;
		}
		
		public ArrowheadTypeStats(int id, String name, Block craftingItem, float damage, float mass)
		{
			this.id = id;
			this.name = name;
			this.craftingItem = craftingItem.blockID;
			this.damage = damage;
			this.mass = mass;
		}
	}
	
	public static final HashMap<Integer, ArrowheadTypeStats> arrowheadTypeMap = new HashMap();
	
	public static boolean hasArrowHead(int arrowheadID)
	{
		return arrowheadTypeMap.containsKey(arrowheadID);
	}
	
	public static void registerTypes()
	{
		int id = 0;
		
		arrowheadTypeMap.put(id++, new ArrowheadTypeStats(id, "flint", Item.flint, 1, 1));
		arrowheadTypeMap.put(id++, new ArrowheadTypeStats(id, "stone", Block.stone, 0.75F, 0.9F));
		arrowheadTypeMap.put(id++, new ArrowheadTypeStats(id, "iron", Item.ingotIron, 1.35F, 1.25F));
		arrowheadTypeMap.put(id++, new ArrowheadTypeStats(id, "obsidian", Item.ingotGold, 1.35F, 0.7F));
		arrowheadTypeMap.put(id++, new ArrowheadTypeStats(id, "diamond", Item.diamond, 1.4F, 1.4F));
		
		for (ArrowheadTypeStats stats : arrowheadTypeMap.values())
		{
			GameRegistry.addRecipe(new ItemStack(QuiverMod.arrowhead, 4, stats.id),
					"h",
					"s",
					"f",
					'h', new ItemStack(stats.id, 1, 0),
					's', Item.silk,
					'f', Item.reed);
		}
	}
	
}
