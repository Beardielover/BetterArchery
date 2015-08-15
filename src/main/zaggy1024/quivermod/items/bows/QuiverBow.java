package zaggy1024.quivermod.items.bows;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.items.arrows.IArrowItem;
import zaggy1024.quivermod.items.arrows.ISplittingArrow;
import zaggy1024.quivermod.items.quiver.Quiver;
import zaggy1024.quivermod.util.BowArrowIcons;
import zaggy1024.quivermod.util.InventorySlot;
import zaggy1024.quivermod.util.InventorySlots;
import zaggy1024.quivermod.config.QuiverModConfigManager;
import zaggy1024.quivermod.entities.EntityQuiverModArrow;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class QuiverBow extends ItemBow
{
	
	protected float pullBackMult = 0.75F;
	protected float arrowSpeedMult = 1;
	protected float damageMult = 1;
	protected float zoomMult = 1;
	protected float rangeMult = 1;
	
	protected int animIconCount = 4;
	protected int iconSize = 1;
	protected float scale = 1;
	protected float scaleOffX = 0;
	protected float scaleOffY = 0;
	protected int arrowStep = 1;
	
	protected Icon[] icons;
	protected Icon[] stringIcons;
	public Icon defaultStringIcon;
	public Icon smallIcon;
	public Icon smallStringIcon;
	
	protected int defaultMaxItemUse = 72000;
	protected int notchTime = 5;
	protected int noQuiverUsePenalty = 15;
	
    public QuiverBow(int index)
    {
        super(index);
        
        setMaxDamage(384);
        setCreativeTab(QuiverMod.tabBows);
        setUnlocalizedName("quiverBow");
        
        QuiverMod.bowItemList.add(this);
    }
    
    public static String getBowName(String unlocalizedName)
    {
    	return unlocalizedName.split("\\.")[1].toLowerCase();
    }
    
    public String getBowName()
    {
    	return getBowName(getUnlocalizedName());
    }
	
	public String getSmallBowIconName()
	{
		return null;
	}
    
    public int getBrokenBowID()
    {
        return QuiverMod.brokenBow.itemID;
    }
    
    public QuiverBow setIcon(Icon icon)
    {
    	itemIcon = icon;
    	return this;
    }
    
    public String getBowArrowIconName()
    {
    	return "";
    }
    
    public static void resetIcons()
    {
    	for (QuiverBow bow : QuiverMod.bowItemList)
    	{
    		bow.makeUseLarge(QuiverModConfigManager.highResIcons);
    	}
    }
    
    public void makeUseLarge(boolean useHighRes)
    {
    	if (useHighRes)
    	{
    		itemIcon = icons[0];
    		defaultStringIcon = stringIcons[0];
    	}
    	else
    	{
    		itemIcon = smallIcon;
    		defaultStringIcon = smallStringIcon;
    	}
    }

	public static void registerIcons(QuiverBow instance, IconRegister iconRegister)
	{
		instance.icons = new Icon[instance.animIconCount];
		instance.stringIcons = new Icon[instance.animIconCount];
		
		String mod = "quivermod:";
		String name = mod + instance.getBowName();
		
		for (int i = 0; i < instance.animIconCount; i++)
		{
			instance.icons[i] = iconRegister.registerIcon(name + i);
			instance.stringIcons[i] = iconRegister.registerIcon(name + "string" + i);
		}
		
		instance.setIcon(instance.icons[0]);
		instance.defaultStringIcon = instance.stringIcons[0];
		
		String bowIconName = instance.getSmallBowIconName();
		
		if (bowIconName != null)
		{
			instance.smallIcon = iconRegister.registerIcon("quivermod:" + bowIconName);
			instance.smallStringIcon = iconRegister.registerIcon("quivermod:" + bowIconName + "string");
		}
		else
		{
			instance.smallIcon = instance.itemIcon;
			instance.smallStringIcon = instance.defaultStringIcon;
		}
		
		BowArrowIcons.registerArrowIcons(instance.getBowArrowIconName(), iconRegister);
		
		instance.makeUseLarge(QuiverModConfigManager.highResIcons);
	}
	
	public void registerIconsHelper(QuiverBow instance, IconRegister iconRegister)
	{
		registerIcons(instance, iconRegister);
	}

	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		registerIcons(this, iconRegister);
	}
	
	public float getPullBackMult()
	{
		return pullBackMult;
	}
	
	public float getArrowSpeedMult()
	{
		return arrowSpeedMult;
	}
	
	public float getDamageMult()
	{
		return damageMult;
	}
	
	public int getIconSize()
	{
		return iconSize;
	}
	
	public float getScale(boolean thirdPerson)
	{
		return scale;
	}
	
	public float getScaleOffsetX()
	{
		return scaleOffX;
	}
	
	public float getScaleOffsetY()
	{
		return scaleOffY;
	}
	
	public int getArrowStep()
	{
		return arrowStep;
	}

	public float getSlow()
	{
		return 0;
	}

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.bow;
    }
    
    private int getQuiverIndex(InventoryPlayer inventory)
    {
    	int i = 0;
    	
        for (ItemStack stack : inventory.mainInventory) {
        	if (stack != null && (stack.itemID == QuiverMod.quiver.itemID) && Quiver.getArrowCount(stack) > 0)
        	{
        		return i;
        	}
        	
        	i++;
        }
        
        return -1;
    }
    
    private int getItemUsePenalties(ItemStack stack, EntityPlayer player)
    {
    	int value = notchTime;
    	
    	InventorySlots arrowSlot = QuiverMod.playerValueManager.getArrowQuiverSlot(player);
    	
    	if (arrowSlot == null)
    	{
    		value += noQuiverUsePenalty;
    	}
    	
    	value /= getPullBackMult();
    	
    	return value;
    }
    
    public int getIconOffset(float power)
    {
    	//power = (float)Math.pow(power, 1.1);

        if(power == 1)
        	return animIconCount - 1;
		
		for (float i = animIconCount - 2; i > 1; i--)
		{
			if (power >= i / (animIconCount - 1))
			{
            	return (int)i;
			}
		}
        
        if(power > 0)
        	return 1;
		
    	return 0;
    }
    
    public Icon getBowIconForPlayer(int iconOffset)
    {
    	return icons[iconOffset];
    }
    
    public Icon getStringIcon(int iconOffset)
    {
    	return stringIcons[iconOffset];
    }
    
    public Icon getArrowIcon(int iconOffset, ItemStack arrowStack, int pass)
    {
    	return BowArrowIcons.getIcon(getBowArrowIconName(),
    			arrowStack, iconOffset, pass,
    			((ISplittingArrow)arrowStack.getItem()).isSplittingArrow(arrowStack.getItemDamage()));
    }
    
    public void doPreTransforms(boolean thirdPerson, boolean ifp, float px, float partialTick) {}
    
    public void doBowTransforms(boolean thirdPerson, boolean ifp, float px, float partialTick) {}

    public int getMaxItemUseDuration(ItemStack stack)
    {
        return (int)(defaultMaxItemUse * getPullBackMult()) + notchTime;
    }

    public int getMaxItemUseDuration(int penalties)
    {
        return (int)((defaultMaxItemUse + penalties) * getPullBackMult());
    }

    public int getMaxItemUseDuration(ItemStack stack, EntityPlayer player)
    {
        return getMaxItemUseDuration(getItemUsePenalties(stack, player));
    }
    
    public float getUsePower(float useDuration)
    {
    	useDuration /= getPullBackMult();
		float power = useDuration / 20F;
        power = (power * power + power * 2.0F) / 3.0F;
        
        if (power < 0)
        	return 0;
		
		if (power > 1)
			return 1;
		
		return power;
    }
    
    public float getUsePowerFromUseCount(float playerUseCount)
    {
    	return getUsePower(getMaxItemUseDuration(null) - playerUseCount); 
    }
    
    public float getUsePowerFromUseCount(int playerUseCount)
    {
		return getUsePower(getMaxItemUseDuration(null) - playerUseCount);
    }

	public float getUsePower(EntityPlayer player)
	{
		if (player.isUsingItem())
		{
			int useCount = player.getItemInUseCount();
			return getUsePowerFromUseCount(useCount);
		}
		
		return 0;
	}

    protected void setExtraArrowSettings(EntityQuiverModArrow arrow, ItemStack stack, World world, EntityPlayer player, int itemInUseCount, ItemStack arrowStack) { }

    protected void setExtraArrowSettings(EntityArrow arrow, ItemStack stack, World world, EntityPlayer player, int itemInUseCount, ItemStack arrowStack) { }

    /**
     * called when the player releases the use item button. Args: stack, world, player, itemInUseCount
     */
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int itemInUseCount)
    {
        float useDuration = getMaxItemUseDuration(stack) - itemInUseCount;

        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, (int)useDuration);
        MinecraftForge.EVENT_BUS.post(event);
        
        if (event.isCanceled())
        {
            return;
        }
        
        useDuration = event.charge;	// Essentially the same value as before, ignore this

        boolean useNoArrow = player.capabilities.isCreativeMode;
        boolean infinity = EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;
        
        InventorySlots arrowSlots = QuiverMod.playerValueManager.getArrowSlot(player);
        int quiverIndex = -1;
        int arrowIndex = -1;
        ItemStack quiverStack = null; 
        ItemStack arrowStack = null;
        
        if (arrowSlots != null)
        {
        	InventorySlot quiverSlot = arrowSlots.get("quiver");
        	quiverStack = quiverSlot.stack;
        	quiverIndex = quiverSlot.index;
        	
        	InventorySlot arrowSlot = arrowSlots.get("arrow");
        	arrowStack = arrowSlot.stack;
        	arrowIndex = arrowSlot.index;
        }
        
        boolean canFire = (useNoArrow || arrowStack != null) && useDuration > 0;
        
    	if (arrowStack == null)
    		arrowStack = new ItemStack(Item.arrow, 0);
    	
    	IArrowItem iArrow = (IArrowItem)arrowStack.getItem();

        if (canFire)
        {
            float power = getUsePower(useDuration);

            if (power > 0)
            {
	            EntityQuiverModArrow arrow = new EntityQuiverModArrow(world, player, power * 2.0F * arrowSpeedMult);
	            
	            int bowDamage = player.capabilities.isCreativeMode ? 0 : 1;
	
	            if (power >= 1)
	            {
	                arrow.setIsCritical(true);
	            }
	
	            int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
	
	            if (powerLevel > 0)
	            {
	                arrow.setDamage(arrow.getDamage() + (double)powerLevel * 0.5D + 0.5D);
	            }
	            
	            arrow.setDamage(arrow.getDamage() * damageMult);
	
	            int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
	
	            if (punchLevel > 0)
	            {
	                arrow.setKnockbackStrength(punchLevel);
	            }
	
	            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0)
	            {
	                arrow.setFire(100);
	            }
	            
	            arrow.setValuesForStack(arrowStack);
	            
	            setExtraArrowSettings(arrow, stack, world, player, itemInUseCount, arrowStack);
	
	            boolean broken = stack.attemptDamageItem(bowDamage, player.getRNG());
	            
	            if (broken)
	            {
	            	onBroken(stack, player);
	            }
	            
	            if (!useNoArrow && !world.isRemote)
	            {
	            	if (infinity && (
	            			(QuiverModConfigManager.bowInfinityMode == 0) ||
	            			(QuiverModConfigManager.bowInfinityMode == 1 && world.rand.nextInt(iArrow.getInfinityChance()) == 0) ||
	            			(QuiverModConfigManager.bowInfinityMode == 2 && world.rand.nextInt(3) == 0)))
	            	{
	            		arrow.canBePickedUp = 2;
	            	}
	            	else
	            	{
		            	if (quiverStack != null)
		            	{
		            		Quiver.removeArrow(player, quiverStack, quiverIndex, arrowIndex);
		            	}
		            	else
		            	{
		            		arrowStack.stackSize--;
		            		
		            		if (arrowStack.stackSize < 1)
		            			player.inventory.setInventorySlotContents(arrowIndex, null);
		            	}
	            	}
	            }
	            
	            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + power * 0.5F);
	
	            if (useNoArrow)
	            {
	                arrow.canBePickedUp = 2;
	            }
	
	            if (!world.isRemote)
	            {
	            	world.spawnEntityInWorld(arrow);
	            }
            }
        }
    }

	/**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        ArrowNockEvent event = new ArrowNockEvent(player, itemStack);
        MinecraftForge.EVENT_BUS.post(event);
        
        if (event.isCanceled())
        {
            return event.result;
        }
        
        InventorySlots arrowSlot = QuiverMod.playerValueManager.getArrowSlot(player);
        ItemStack arrowStack = null;
        
        if (arrowSlot != null)
        {
	        arrowStack = arrowSlot.get("arrow").stack;
        }
        
        if (player.capabilities.isCreativeMode || arrowStack != null)
        {
            if (!player.isUsingItem())
            {
            	QuiverMod.playerValueManager.sendValuesToServer(player);
            	QuiverMod.playerValueManager.updateUsingArrow(player);
            }
            
            player.setItemInUse(itemStack, getMaxItemUseDuration(itemStack, player));
        }

        return itemStack;
    }
    
    public void onBroken(ItemStack stack, EntityPlayer player)
    {
    	stack.setItemDamage(0);
    	stack.itemID = getBrokenBowID();
    	
    	if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("ench"))
    		stack.stackTagCompound.removeTag("ench");
    }
    
    public boolean isFull3D()
    {
    	return true;
    }
    
    public boolean requiresMultipleRenderPasses()
    {
        return false;
    }
    
    public Icon getIconFromDamageForRenderPass(int damage, int pass)
    {
        return pass == 0 ? itemIcon : (defaultStringIcon ==  null ? itemIcon : defaultStringIcon);
    }

	public float getZoomMultiplier()
	{
		return zoomMult;
	}

	public float getRangeMult()
	{
		return rangeMult;
	}

	public Icon getDefaultHeldIcon() {
		return icons[0];
	}

	public Icon getDefaultHeldStringIcon() {
		return stringIcons[0];
	}
	
}
