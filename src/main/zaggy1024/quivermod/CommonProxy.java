package zaggy1024.quivermod;

import java.util.ArrayList;
import java.util.HashMap;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

import zaggy1024.quivermod.client.ClientProxy;
import zaggy1024.quivermod.client.renderers.QuiverModTickHandler;
import zaggy1024.quivermod.util.IParticleSpawning;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CommonProxy implements IParticleSpawning {
	
		public static Minecraft mc = null;
		public static QuiverModTickHandler overlayHandler;
       
        public void register() { }

		public void preInitRegister() { }
        
        public void onBowUse(EntityPlayer player, float frameTime) {}
    	
    	public void resetSavedFOV() {}

    	public void spawnDiggingFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ, Block block, int side, int metadata) { }
        
}