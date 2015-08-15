package zaggy1024.quivermod.client.renderers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.ReadableColor;

import zaggy1024.quivermod.*;
import zaggy1024.quivermod.client.ClientProxy;
import zaggy1024.quivermod.config.QuiverModConfigManager;
import zaggy1024.quivermod.entities.EntityQuiverModSkeleton;
import zaggy1024.quivermod.items.bows.QuiverBow;
import zaggy1024.quivermod.items.quiver.QuiverInventory;
import zaggy1024.quivermod.util.ColorHelper;
import zaggy1024.quivermod.util.InventorySlot;
import zaggy1024.quivermod.util.InventorySlots;
import zaggy1024.quivermod.util.PlayerValueManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraft.world.World;
import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;

public class QuiverModTickHandler extends Gui implements ITickHandler {
	
	public static final ResourceLocation hotbarTexture = new ResourceLocation("quivermod:textures/gui/quiverhotbar.png");
	
	public static float partialTick = 0;
	public static int frames = 0;
	
	static Tessellator tessellator = Tessellator.instance;
    static RenderItem itemRenderer = new RenderItem();
	int zLevel = -90;

	public ItemStack prevHeldStack;
	public int prevQuiverUniqueID = -1;
	public int prevQuiverDamage = -1;
	
	int fadeTimer = 0;
	int fadeTime = 80;
	int noFadeTime = 40;
	
	String infoText = "";
	
	int switchAnimation = 0;
	int switchAnimationLength = 30;
	
	int offset = 0;
	
	float animLength = 100;
	float animTickStart = animLength / 2;
	float animTick = animTickStart;
	float halfAnimLength = animLength / 2F;
	
	float lastFrameTime = 0;
	long lastFrame = QuiverMod.proxy.mc.getSystemTime();
	
	public QuiverModTickHandler() {
	}

	@Override
	public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.RENDER, TickType.PLAYER);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
	    EntityPlayerSP thePlayer = QuiverMod.proxy.mc.thePlayer;
	    
		if (type.contains(TickType.RENDER))
		{
	        partialTick = (Float)tickData[0];
	        frames++;
	        
			if (thePlayer != null)
			{
		        if (thePlayer.isUsingItem())
		        {
		        	ItemStack usingStack = thePlayer.getHeldItem();
		        	
		        	if (usingStack != null && usingStack.getItem() instanceof QuiverBow)
		        	{
		        		QuiverMod.proxy.onBowUse(thePlayer, lastFrameTime);
		        		
		        	}
		        }
		        else
		        {
		        	QuiverMod.proxy.resetSavedFOV();
		        }
			}
		}
		
		if (type.contains(TickType.PLAYER))
		{
			EntityPlayer tickPlayer = (EntityPlayer)tickData[0];
			
			if (thePlayer != null && tickPlayer == thePlayer)
			{
				if (thePlayer.isUsingItem())
				{
		        	ItemStack usingStack = thePlayer.getHeldItem();
		        	Item usingItem = null;
		        	
		        	if (usingStack != null && (usingItem = usingStack.getItem()) instanceof QuiverBow)
		        	{
		        		QuiverBow usingBow = (QuiverBow)usingItem;
		        		float slow = usingBow.getSlow();
		        		
		        		if (slow > 0)
		        		{
		        			//thePlayer.getAttributeMap().getAttributeInstanceByName(par1Str)
				            //thePlayer.landMovementFactor /= 0.2F;
				            //thePlayer.landMovementFactor *= slow;
		        		}
		        	}
				}
				
				ItemStack quiver = null;
				
				InventorySlots slots = QuiverMod.playerValueManager.getArrowQuiverSlot(tickPlayer);
				
				if (slots != null)
				{
					InventorySlot slot = slots.get("quiver");
					
					if (slot != null)
					{
						quiver = slot.stack;
					}
				}
				
				ItemStack heldStack = tickPlayer.getHeldItem();
				
				if (heldStack != null && heldStack.itemID == QuiverMod.quiver.itemID && heldStack.equals(quiver))
				{
					int heldQuiverID = QuiverMod.quiver.getUniqueID(heldStack);
					
					if (QuiverMod.quiver.getUniqueID(quiver) == heldQuiverID)
					{
						quiver = null;
					}
				}
				
				int uniqueID = -1;
				int damage = -1;
				
				if (quiver != null)
				{
					uniqueID = QuiverMod.quiver.getUniqueID(quiver);
					damage = quiver.getItemDamage();
				}
				
				if (prevHeldStack != heldStack || uniqueID != prevQuiverUniqueID || damage != prevQuiverDamage)
				{
					QuiverMod.playerValueManager.setUsingQuiver(tickPlayer.getEntityName(), damage);
					QuiverMod.playerValueManager.sendValuesToServer(tickPlayer);
				}
				
				prevQuiverUniqueID = uniqueID;
				prevQuiverDamage = damage;
				prevHeldStack = heldStack;
			}
		}
	}
	
	public void setInfoText(String newText)
	{
		infoText = newText;
		fadeTimer = fadeTime + noFadeTime;
	}
	
	public void resetSelectionAnimation()
	{
		if (QuiverModConfigManager.animationStyle == 0)
			animTick = animTickStart;
		else
			switchAnimation = switchAnimationLength;
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
	}

	@Override
	public String getLabel() {
		return "QuiverModTickHandler";
	}

}
