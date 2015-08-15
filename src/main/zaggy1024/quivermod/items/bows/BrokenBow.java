package zaggy1024.quivermod.items.bows;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.util.InventorySlot;
import zaggy1024.quivermod.util.InventorySlots;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class BrokenBow extends QuiverBow
{

	private String bowName = null;
	private int stringCost = 3;
	
	private int originalBowID = -1; 
	private QuiverBow originalBow = null;

	public BrokenBow(int par1)
	{
		super(par1);
		
		zoomMult = 0;
	}
	
	@Override
	public String getItemDisplayName(ItemStack stack)
	{
		return StatCollector.translateToLocal("quiverModBow.prefix.broken") + " " + originalBow.getItemDisplayName(stack);
	}
	
	public BrokenBow setBowName(String name)
	{
		bowName = name;
		return this;
	}
	
	public String getBowName()
	{
		if (bowName == null)
			bowName = getBowName(((QuiverBow)Item.itemsList[originalBowID]).getUnlocalizedName());
		
		return bowName;
	}
	
	public String getSmallBowIconName()
	{
		return originalBow.getSmallBowIconName();
	}
	
	public void setValuesFor(QuiverBow bow)
	{
		damageMult = bow.damageMult;
		arrowSpeedMult = bow.arrowSpeedMult;
		zoomMult *= bow.zoomMult;
		
		animIconCount = bow.animIconCount;
		
		iconSize = bow.iconSize;
		scale = bow.scale;
		scaleOffX = bow.scaleOffX;
		scaleOffY = bow.scaleOffY;
	}

	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		originalBow.registerIconsHelper(this, iconRegister);
		defaultStringIcon = null;
	}
    
	@Override
    public int getIconOffset(float power)
    {
    	return originalBow.getIconOffset(power);
    }
	
	public BrokenBow setOriginalBowID(int id)
	{
		originalBowID = id;
		originalBow = (QuiverBow)Item.itemsList[id];
		setValuesFor(originalBow);
		return this;
	}
    
    public Icon getStringIconForPlayer(EntityPlayer player, ItemStack stack)
    {
    	return null;
    }
    
    public Icon getArrowIconForPlayer(EntityPlayer player, ItemStack stack)
    {
    	return null;
    }

    @Override
    public void doPreTransforms(boolean thirdPerson, boolean ifp, float px, float partialTick)
    {
    	originalBow.doPreTransforms(thirdPerson, ifp, px, partialTick);
    }

    @Override
    public void doBowTransforms(boolean thirdPerson, boolean ifp, float px, float partialTick)
    {
    	originalBow.doBowTransforms(thirdPerson, ifp, px, partialTick);
    }
    
    private InventorySlots getStringSlots(EntityPlayer player)
    {
    	InventorySlots stringSlots = new InventorySlots();
        int stringCount = 0;
        int stringI = 0;
        int i = 0;
        
        for (ItemStack iterStack : player.inventory.mainInventory)
        {
        	if (iterStack != null && iterStack.itemID == Item.silk.itemID)
        	{
        		if (stringSlots == null)
        			stringSlots = new InventorySlots();
        		
        		InventorySlot stringSlot = new InventorySlot(iterStack, i);
        		stringSlots.set(stringI, stringSlot);
        		
        		stringCount += iterStack.stackSize;
        		stringI++;
        	}
        	
        	i++;
        }
    	
        if (stringCount >= stringCost)
        {
        	return stringSlots;
        }
        
        return null;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int itemInUseCount)
    {
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player)
    {
    	InventorySlots stringSlots = getStringSlots(player);
    	boolean string = stringSlots != null;
        
        if (player.capabilities.isCreativeMode || string)
        {
        	if (!player.capabilities.isCreativeMode && string)
        	{
	        	int stringIndex;
	        	ItemStack stringStack;
	        	int remainingStringCost = stringCost;
	        	
	        	for (Object key : stringSlots.getSlots().keySet())
	        	{
	        		InventorySlot slot = stringSlots.get(key);
			        stringStack = slot.stack;
			    	stringIndex = slot.index;
			    	
			    	if (stringStack.stackSize > remainingStringCost)
			    	{
			    		stringStack.stackSize -= remainingStringCost;
			    		remainingStringCost = 0;
			    		break;
			    	}
			    	else
			    	{
			    		remainingStringCost -= stringStack.stackSize;
			    		stringStack.stackSize = 0;
		        		player.inventory.setInventorySlotContents(stringIndex, null);
		        		
		        		if (remainingStringCost <= 0)
		        			break;
			    	}
	        	}
        	}
        	
        	ItemStack newStack = stack.copy();
        	newStack.itemID = originalBowID;
        	player.inventory.setInventorySlotContents(player.inventory.currentItem, newStack);
        }
        
        return stack;
    }
    
    public float getPullBackMult()
    {
    	return originalBow.getPullBackMult();
    }
    
    public int getMaxItemUseDuration(ItemStack stack)
    {
    	return (int)(getPullBackMult() * 20 * 2);
    }

	/*public float getUsePower(EntityPlayer player)
	{
		int useCount = player.getItemInUseCount();
		float power = ((getPullBackMult() * useCount) / 20F);
		power *= power;
		
		return power;
	}*/

	/**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
    	InventorySlots stringSlot = getStringSlots(player);
    	
    	if (player.capabilities.isCreativeMode || stringSlot != null)
    	{
	        player.setItemInUse(itemStack, getMaxItemUseDuration(itemStack));
    	}

        return itemStack;
    }
    
    public boolean requiresMultipleRenderPasses()
    {
        return false;
    }

}
