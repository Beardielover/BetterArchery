package zaggy1024.quivermod.client.renderers;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import thehippomaster.AnimatedPlayer.AnimatedPlayer;
import thehippomaster.AnimatedPlayer.client.ModelPlayer;

import api.player.render.RenderPlayerAPI;
import api.player.render.RenderPlayerBase;

import cpw.mods.fml.relauncher.ReflectionHelper;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.client.ClientProxy;
import zaggy1024.quivermod.config.QuiverModConfigManager;
import zaggy1024.quivermod.util.ColorHelper;
import zaggy1024.quivermod.util.InventorySlot;
import zaggy1024.quivermod.util.InventorySlots;
import zaggy1024.quivermod.util.TextureSizeMap;
import zaggy1024.quivermod.util.TextureSizeMap.TextureSize;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;

public class QuiverRenderPlayerBase extends RenderPlayerBase {
	
	protected static boolean animatedPlayer = false;
	
	public class PlayerValues {
		float quiverRotation = 0;
		float quiverRotationSpeed = 0;
		
		public PlayerValues() { }
	}

	private static final ResourceLocation backQuiverTexture = new ResourceLocation("quivermod:textures/wornquiver/backquiver.png");
	private static final ResourceLocation beltQuiverTexture = new ResourceLocation("quivermod:textures/wornquiver/beltquiver.png");
	private static final float px = 1 / 16F;
	private static final HashMap<Integer, PlayerValues> playerValuesMap = new HashMap();
	private static final float maxSpeedChange = 1;
	
	public static int renderQuiverIn3DDistanceSqr = -1;


	public QuiverRenderPlayerBase(RenderPlayerAPI api) {
		super(api);
		
		try
		{
			animatedPlayer = AnimatedPlayer.instance != null;
		}
		catch (LinkageError e)
		{
			
		}
	}

	private PlayerValues getPlayerValues(int entityId) {
		PlayerValues playerValues = playerValuesMap.get(entityId);
		
		if (playerValues == null)
		{
			playerValues = new PlayerValues();
			playerValuesMap.put(entityId, playerValues);
		}
		
		return playerValues;
	}
	
	private void renderQuiver(EntityLivingBase entity)
	{
		String playerName = entity.getEntityName();
		ItemStack quiver = QuiverMod.playerValueManager.getUsingQuiverStack(playerName);
		
    	if (quiver != null)
    	{
    		int distanceSqr = (int)entity.getDistanceSqToEntity(ClientProxy.mc.renderViewEntity);
    		boolean fancy = false;
    		
    		if (renderQuiverIn3DDistanceSqr == -1)
    			renderQuiverIn3DDistanceSqr = QuiverModConfigManager.renderQuiverIn3DDistance * QuiverModConfigManager.renderQuiverIn3DDistance;
    		
    		fancy = distanceSqr < renderQuiverIn3DDistanceSqr;
    		
			float partialTick = ClientProxy.overlayHandler.partialTick;
			
			int wornType = QuiverMod.playerValueManager.getWornQuiverType(playerName);
			ResourceLocation texture = wornType == 0 ? beltQuiverTexture : backQuiverTexture;
			
	    	GL11.glPushMatrix();
	    	
			if (animatedPlayer)
	    	{
				ModelPlayer modelBiped = ((thehippomaster.AnimatedPlayer.client.RenderPlayer)renderPlayer).playerModel;
		    	modelBiped.postRender(0.0625F, modelBiped.chest);
				GL11.glTranslatef(0, -0.75F, 0);
	    	}
	    	else
	    	{
				ModelBiped modelBiped = renderPlayer.getModelBipedMainField();
		    	modelBiped.bipedBody.postRender(0.0625F);
	    	}
			
			if (wornType == 0)
			{
				GL11.glTranslatef(0, 0.25F, 0);
			}
			
	    	GL11.glTranslatef(-0.5625F, 0.8125F, 0.1875F);
	    	GL11.glScalef(1, -1, 1);
	    	
	    	QuiverMod.proxy.mc.renderEngine.bindTexture(texture);
	    	TextureSize size = TextureSizeMap.getSize(texture, 32, 32);
	    	
	    	float expand = 0;
	    	
	    	if (wornType != 0)
	    	{
	    		if (entity.getCurrentItemOrArmor(2) != null)
	    		{
	    			expand = px;
	    		}
	    	}
	    	else
	    	{
	    		if (entity.getCurrentItemOrArmor(2) != null)
	    			expand = -0.0005F;
	    		
	    		if (entity.getCurrentItemOrArmor(1) != null)
	    			expand = 0.03125F;
	    	}
	    	
	    	GL11.glPushMatrix();
	    	
	    	GL11.glTranslatef(0, 0, expand);
	    	
    		Tex2DRender3D.render(fancy, 0.5F, 0, 1, 0.5F, size.width, size.height, px);
	    	
	    	int damage = quiver.getItemDamage();
	    	int passes = QuiverMod.quiver.getRenderPasses(damage);

	    	QuiverMod.proxy.mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
	    	
	    	GL11.glTranslatef(0, px, 0.03125F);
	    	
	    	GL11.glPushMatrix();
	    	
	    	if (wornType == 0)
	    	{
		    	GL11.glTranslatef(expand, 0, -expand);
		    	
	    		GL11.glRotatef(-90, 0, 1, 0);
	    		GL11.glTranslatef(-0.71875F, -0.125F, -0.8125F);
	    	}
	    	
	    	GL11.glTranslatef(0.55F, 0.4F, 0);
	    	
	    	float targetRot = 0;
	    	
	    	if (entity.onGround)
	    	{
	    		float motionX = (float)(entity.posX - entity.prevPosX);
	    		float motionZ = (float)(entity.posZ - entity.prevPosZ);
	    		
		    	float entityMotion = (float)Math.sqrt(motionX * motionX + motionZ * motionZ);
		    	
		    	float calcMotion = entityMotion * 5;
		    	calcMotion *= 100;
		    	calcMotion *= calcMotion;
		    	calcMotion /= 3500;
		    	calcMotion = Math.min(1.5F, calcMotion);
		    	
		    	targetRot += (float)Math.cos((entity.ticksExisted + partialTick) * 0.6662F) * calcMotion * 5;
	    	}
	    	
    		float motionY = (float)(entity.posY - entity.prevPosY);

    		if (entity.isInsideOfMaterial(Material.water))
    			;//motionY -= 0.02F * partialTick;
    		else if (entity.isInsideOfMaterial(Material.lava))
    			;//motionY -= 0.02F * partialTick;
    		else if (!entity.onGround && !entity.isRiding() && (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).capabilities.isFlying))
    			motionY -= 0.08F * 0.9800000190734863F * partialTick;
    		
    		float yTargetRot = motionY * 25;
    		
    		if (yTargetRot > 25)
    			yTargetRot = 25;
    		else if (yTargetRot < -25)
    			yTargetRot = -25;
    		
			targetRot += yTargetRot;
	    	
	    	PlayerValues playerValues = getPlayerValues(entity.entityId);
	    	
	    	float targetSpeed = targetRot - playerValues.quiverRotation;
	    	float diff = MathHelper.clamp_float(targetSpeed - playerValues.quiverRotationSpeed, -maxSpeedChange, maxSpeedChange);
	    	
	    	playerValues.quiverRotationSpeed = playerValues.quiverRotationSpeed + diff;
    		playerValues.quiverRotationSpeed *= 0.95F;
	    	
	    	playerValues.quiverRotation += playerValues.quiverRotationSpeed;
	    	
	    	GL11.glRotatef(playerValues.quiverRotation, 0, 0, 1);
	    	
	    	GL11.glTranslatef(-0.55F, -0.4F, 0);
	    	
	    	for (int pass = 0; pass < passes - 1; pass++)
	    	{
	    		Icon icon = QuiverMod.quiver.getIcon(quiver, pass);
	    		ColorHelper.glSetColor(QuiverMod.quiver.getColorFromItemStack(quiver, pass));
	    		Tex2DRender3D.render(fancy, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.09375F);
	    	}
	    	
	    	GL11.glPopMatrix();
	    	
	    	GL11.glPopMatrix();
	    	
	    	QuiverMod.proxy.mc.renderEngine.bindTexture(texture);
	    	GL11.glColor4f(1, 1, 1, 1);
	    	
	    	GL11.glPushMatrix();
	    	GL11.glTranslatef(0, 0, -0.3125F);
	    	
	    	GL11.glPushMatrix();
	    	
	    	GL11.glTranslatef(0, 0, -expand);
	    	
	    	Tex2DRender3D.render(fancy, 0.5F, 0.5F, 1, 1, size.width, size.height, px);
	    	
	    	GL11.glPopMatrix();
	    	
	    	if (wornType == 0)
	    	{
		    	GL11.glRotatef(90, 0, 1, 0);
		    	GL11.glTranslatef(-0.9375F, 0, 0.875F);
		    	
		    	GL11.glPushMatrix();
	
		    	GL11.glTranslatef(0, 0, expand);
		    	
		    	Tex2DRender3D.render(fancy, 0, 0, 0.5F, 0.5F, size.width, size.height, px);
		    	
		    	GL11.glPopMatrix();
	    	}
	    	else
	    	{
		    	GL11.glRotatef(90, 1, 0, 0);
		    	GL11.glTranslatef(0.25F, -0.6875F, -0.8125F);
		    	
		    	GL11.glPushMatrix();
	
		    	GL11.glTranslatef(0, 0, -expand);
	
		    	Tex2DRender3D.render(fancy, 0, 0, 0.5F, 0.5F, size.width, size.height, px);
		    	
		    	GL11.glPopMatrix();
	    	}
	    	
	    	GL11.glPopMatrix();
	    	
	    	GL11.glPushMatrix();
	    	
	    	GL11.glRotatef(90, 0, 1, 0);
	    	//GL11.glScalef(0.375F, 1, 1);
	    	GL11.glTranslatef(-0.625F, 0, 0.3125F);

	    	GL11.glTranslatef(0, 0, -expand);

	    	Tex2DRender3D.render(fancy, 0, 0.5F, 0.5F, 1, size.width, size.height, px);
	    	
	    	GL11.glPopMatrix();
	    	
	    	GL11.glPopMatrix();
    	}
	}

	@Override
    public void afterRenderModel(EntityLivingBase entity, float limbSwing, float limbYaw, float existedTicksPartial, float headYawOffset, float pitch, float scale)
    {
		renderQuiver(entity);
    }

}
