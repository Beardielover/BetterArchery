package zaggy1024.quivermod;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;

import zaggy1024.quivermod.blocks.TileEntityTorch;
import zaggy1024.quivermod.config.QuiverModConfigManager;
import zaggy1024.quivermod.entities.EntityQuiverModArrow;
import zaggy1024.quivermod.entities.EntityQuiverModArrow.ImpaledItem;
import zaggy1024.quivermod.entities.EntityQuiverModSkeleton;
import zaggy1024.quivermod.items.quiver.QuiverContainer;
import zaggy1024.quivermod.util.PlayerValueManager;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class QuiverModPacketHandler implements IPacketHandler {
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		EntityPlayer entPlayer = (EntityPlayer)player;
		
		if (packet.channel.startsWith("QuivSel"))
		{
			PlayerValueManager valMan = QuiverMod.playerValueManager;
			boolean forServer = true;
			
			if (packet.channel.equals("QuivSelClient"))
			{
				forServer = false;
			}
			
			valMan.readValues(entPlayer, packet.data, packet.length, forServer);
		}
		else
		{
			ByteArrayInputStream byteInput = new ByteArrayInputStream(packet.data);
			DataInputStream inputStream = new DataInputStream(byteInput);
			
			try {
				if (packet.channel.equals("RemoveAllArrows") && entPlayer.openContainer instanceof QuiverContainer)
				{
					int index = inputStream.readInt();
					QuiverContainer container = (QuiverContainer)entPlayer.openContainer;
					container.transferAllStacks();
					container.detectAndSendChanges();
				}
				else if (packet.channel.equals("BreakTorch"))
				{
					int x = inputStream.readInt();
					int y = inputStream.readInt();
					int z = inputStream.readInt();
					int subHit = inputStream.readInt();
					
					TileEntityTorch torch = (TileEntityTorch)entPlayer.worldObj.getBlockTileEntity(x, y, z);
					
					if (torch != null)
						torch.breakTorch(subHit, !entPlayer.capabilities.isCreativeMode);
				}
				else if (packet.channel.equals("SkeletonUse"))
				{
					int id = inputStream.readInt();
					EntityQuiverModSkeleton skeleton = (EntityQuiverModSkeleton)entPlayer.worldObj.getEntityByID(id);
					
					if (skeleton != null)
						skeleton.setUseLeft(inputStream.readInt());
				}
				else if (packet.channel.equals("QuiverModConf"))
				{
					QuiverModConfigManager.readValuesFromPacket(entPlayer, packet, inputStream);
				}
				else if (packet.channel.equals("QMArGetSpecial"))
				{
					EntityQuiverModArrow arrow;
					int entityID = inputStream.readInt();
					
					arrow = (EntityQuiverModArrow)entPlayer.worldObj.getEntityByID(entityID);
					
					if (arrow != null)
						arrow.sendImpaledItemsPacket();
				}
				else if (packet.channel.equals("QMArImpaledItems"))
				{
					EntityQuiverModArrow arrow = (EntityQuiverModArrow)entPlayer.worldObj.getEntityByID(inputStream.readInt());
					arrow.impaledItems.clear();
					
					while (true)
					{
						int itemID = inputStream.readInt();
						
						if (itemID == -1)
							break;
						
						int damage = inputStream.readInt();

						float position = inputStream.readFloat();
						float targetPosition = inputStream.readFloat();
						
						arrow.impaledItems.add(new ImpaledItem(entPlayer.worldObj, itemID, damage, position, targetPosition));
					}
				}
			}
			catch (Exception ex) {
	            ex.printStackTrace();
			}
		}
	}

}
