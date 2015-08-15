package zaggy1024.quivermod.entities;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.network.PacketDispatcher;
import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.config.QuiverModConfigManager;
import zaggy1024.quivermod.items.bows.QuiverBow;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITaskEntry;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public class EntityQuiverModSkeleton extends EntitySkeleton {

    protected static final UUID skeletonRangeModifier = UUID.fromString("51220482-1632-4096-1024-137438953472");
    
    public AttributeModifier rangeModifier;
    
	public EntityAINearestAttackableTarget aiNearestAttackableTarget = new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true);
    public EntityAIArrowAttack aiArrowAttack;
    public EntityAIAttackOnCollide aiAttackOnCollide;
    
    public ItemStack arrowStack = new ItemStack(Item.arrow);
    
    protected int useLeft = 0;
    protected int startUseLeft = 0;

	public EntityQuiverModSkeleton(World world) {
		super(world);
		
		Iterator<EntityAITaskEntry> iter = ((List<EntityAITaskEntry>)targetTasks.taskEntries).iterator();
		
		while (iter.hasNext())
		{
			EntityAITaskEntry entry = iter.next();
			
			if (entry.action instanceof EntityAINearestAttackableTarget)
			{
				iter.remove();
			}
		}
		
        targetTasks.addTask(2, aiNearestAttackableTarget);

        rangeModifier = new AttributeModifier(skeletonRangeModifier, "skeletonRangeModifier", QuiverMod.getLargestRangeMult(), 2);
        getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(rangeModifier);
	}
	
	@Override
    public String getEntityName()
    {
        return StatCollector.translateToLocal("entity.Skeleton.name");
    }

    /*@Override
    protected int func_96121_ay()
    {
        return (int)(16 * QuiverMod.getLargestRangeMult());
    }*/

    @Override
    protected void entityInit()
    {
        super.entityInit();
        
        dataWatcher.addObject(19, Item.arrow.itemID);
        dataWatcher.addObject(20, 0);
    }
    
    public void initCreature()
    {
        tasks.addTask(4, aiArrowAttack);
        addRandomArmor();
        enchantEquipment();

        setCanPickUpLoot(rand.nextFloat() < 0.55F * worldObj.getLocationTensionFactor(posX, posY, posZ));

        if (getCurrentItemOrArmor(4) == null)
        {
            Calendar calendar = this.worldObj.getCurrentDate();

            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.rand.nextFloat() < 0.25F)
            {
                setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Block.pumpkinLantern : Block.pumpkin));
                equipmentDropChances[4] = 0.0F;
            }
        }
    }
    
    @Override
    protected void dropFewItems(boolean player, int looting)
    {
        int itemCount = rand.nextInt(3 + looting);
        
        for (int i = 0; i < itemCount; ++i)
        {
            entityDropItem(arrowStack, 0);
        }
        
        itemCount = rand.nextInt(3 + looting);

        for (int i = 0; i < itemCount; ++i)
        {
            dropItem(Item.bone.itemID, 1);
        }
    }
    
    public void setUsingArrowStack(ItemStack newArrowStack)
    {
    	if (newArrowStack != null)
    	{
	    	dataWatcher.updateObject(19, newArrowStack.itemID);
	        dataWatcher.updateObject(20, newArrowStack.getItemDamage());
	        arrowStack = newArrowStack;
    	}
    }
    
    public ItemStack getUsingArrowStack()
    {
    	if (arrowStack != null)
    		return arrowStack;
    	
    	return new ItemStack(dataWatcher.getWatchableObjectInt(19), 1, dataWatcher.getWatchableObjectInt(20));
    }
    
    @Override
    protected void addRandomArmor()
    {
        super.addRandomArmor();
        
        int randomNum = worldObj.rand.nextInt(20);

        if (randomNum <= 4)
        	setCurrentItemOrArmor(0, new ItemStack(QuiverMod.longbow));
        else if (randomNum <= 10)
        	setCurrentItemOrArmor(0, new ItemStack(QuiverMod.bowComposite));
        else
        	setCurrentItemOrArmor(0, new ItemStack(QuiverMod.bow));
        
        ItemStack randomArrowStack = null;
        randomNum = worldObj.rand.nextInt(50);
        int i = 0;

        if (randomNum <= (i + 1) && QuiverModConfigManager.allowSkeletonGriefing && worldObj.difficultySetting >= 3)
        {
        	randomArrowStack = new ItemStack(QuiverMod.impactExplosiveArrow);
        }
        else if (randomNum <= (i += 1) && QuiverModConfigManager.allowSkeletonPotionArrows)
        {
        	randomArrowStack = new ItemStack(QuiverMod.potionArrow, 1, 8196);	// Poison
        }
        else if (randomNum <= (i += 7) && QuiverModConfigManager.allowSkeletonPotionArrows)
        {
        	randomArrowStack = new ItemStack(QuiverMod.potionArrow, 1, 8200);	// Weakness
        }
        else if (randomNum <= (i += 14) && QuiverModConfigManager.allowSkeletonGriefing)
        {
        	randomArrowStack = new ItemStack(QuiverMod.fireArrow);
        }
        else
        {
        	randomArrowStack = new ItemStack(Item.arrow);
        }
        
        setUsingArrowStack(randomArrowStack);
        
        setCombatTask();
    }
    
    private void createArrowTask()
    {
		float pullBackMult = 1;
    	float rangeMult = 1;
		
    	ItemStack heldItemStack = getHeldItem();
		
		if (heldItemStack != null)
		{
			Item heldItem = heldItemStack.getItem();
			
			if (heldItem instanceof QuiverBow)
			{
				QuiverBow bow = (QuiverBow)heldItem;
				pullBackMult = bow.getPullBackMult();
				rangeMult = bow.getRangeMult();
			}
		}
		
		float range = 15 * rangeMult;
		
		aiArrowAttack = new EntityAIArrowAttack(this, 0.25F, (int)(20 * pullBackMult), (int)(60 * pullBackMult), range);
		
		range += rangeMult;
		
        targetTasks.removeTask(aiNearestAttackableTarget);
        aiNearestAttackableTarget = new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true);
        targetTasks.addTask(2, aiNearestAttackableTarget);
        
        tasks.addTask(4, aiArrowAttack);
    }

    @Override
    public void setCombatTask()
    {
        tasks.removeTask(aiAttackOnCollide);
        tasks.removeTask(aiArrowAttack);
        ItemStack itemStack = getHeldItem();

        if (itemStack != null && (itemStack.itemID == Item.bow.itemID || itemStack.getItem() instanceof QuiverBow))
        {
        	createArrowTask();
        }
        else
        {
        	aiAttackOnCollide = new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.31F, false);
            tasks.addTask(4, aiAttackOnCollide);
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float damage)
    {
    	ItemStack heldStack = getHeldItem();
    	Item heldItem = heldStack.getItem();
    	float speedMult = 1;
    	float damageMult = 1;
    	float rangeMult = 1;
    	
    	if (heldItem instanceof QuiverBow)
    	{
    		QuiverBow bow = (QuiverBow)heldItem;
    		speedMult = bow.getArrowSpeedMult();
    		damageMult = bow.getDamageMult();
			rangeMult = bow.getRangeMult();
    	}
    	
        EntityQuiverModArrow entityArrow = new EntityQuiverModArrow(worldObj, this, target, 1.6F * speedMult, (float)(14 - worldObj.difficultySetting * 4) / rangeMult);
        
        int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, heldStack);
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, heldStack);
        entityArrow.setDamage((damage * 2 + rand.nextGaussian() * 0.25D + worldObj.difficultySetting * 0.11F) * damageMult);

        if (power > 0)
        {
        	entityArrow.setDamage(entityArrow.getDamage() + (double)power * 0.5D + 0.5D);
        }

        if (punch > 0)
        {
        	entityArrow.setKnockbackStrength(punch);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, getHeldItem()) > 0 || getSkeletonType() == 1)
        {
        	entityArrow.setFire(100);
        }
        
        entityArrow.setValuesForStack(getUsingArrowStack());

        playSound("random.bow", 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
        worldObj.spawnEntityInWorld(entityArrow);
    }
    
    public void readEntityFromNBT(NBTTagCompound tags)
    {
        super.readEntityFromNBT(tags);
        
        if (tags.hasKey("arrowStack"))
        {
        	ItemStack arrowStack = ItemStack.loadItemStackFromNBT((NBTTagCompound)tags.getTag("arrowStack"));
        	setUsingArrowStack(arrowStack);
        }

        setCombatTask();
    }

    public void writeEntityToNBT(NBTTagCompound tags)
    {
        super.writeEntityToNBT(tags);
        
        if (arrowStack != null)
        	tags.setTag("arrowStack", arrowStack.writeToNBT(new NBTTagCompound()));
    }
    
    public int getStartUseLeft()
    {
    	return startUseLeft;
    }
    
    public int getUseLeft()
    {
    	return useLeft;
    }
    
    protected int getAIUseLeft()
    {
		return ObfuscationReflectionHelper.getPrivateValue(EntityAIArrowAttack.class,
				aiArrowAttack, "rangedAttackTime", "field_75320_d");
    }
    
    public void setUseLeft(int newUseLeft)
    {
    	useLeft = newUseLeft;
    	startUseLeft = newUseLeft;
    }
    
    public void onUpdate()
    {
        super.onUpdate();
        
        if (useLeft > 0)
        {
        	useLeft--;
        	
        	renderYawOffset += (rotationYawHead - renderYawOffset) * (0.5F - Math.min((float)useLeft / 20, 1) * 0.5F);
        }

        if (!worldObj.isRemote && aiArrowAttack != null)
        {
			int actualUsingLeft = getAIUseLeft();
			
	        if (useLeft < actualUsingLeft)
	        {
	        	setUseLeft(actualUsingLeft);
	        	
	        	ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
	    		DataOutputStream inputStream = new DataOutputStream(byteOutput);
	    		
	    		try
	    		{
	    			inputStream.writeInt(entityId);
	    			inputStream.writeInt(actualUsingLeft);
	    		}
	    		catch (IOException e)
	    		{
	    			QuiverMod.log("Exception while writing skeleton use resetting packet: " + e.getMessage());
	    			e.printStackTrace();
	    		}
	        	
	            Packet250CustomPayload packet = new Packet250CustomPayload("SkeletonUse", byteOutput.toByteArray());
	            PacketDispatcher.sendPacketToAllAround(posX, posY, posZ, 50, dimension, packet);
	        }
        }
    }
    
}
