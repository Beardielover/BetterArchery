package zaggy1024.quivermod.blocks;

import static net.minecraftforge.common.ForgeDirection.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import zaggy1024.quivermod.QuiverMod;
import zaggy1024.quivermod.util.PartialBlockSurfaces;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityTorch extends TileEntity {
	
	private ArrayList<TorchPosition> torches = new ArrayList();
	
	public boolean disableTorchChecks = false;

	private boolean clone = false;
	private int cloneX;
	private int cloneY;
	private int cloneZ;
	
	private boolean cloned = false;
	private int clonedX;
	private int clonedY;
	private int clonedZ;
	
	private static float horizOff = 0.14F;
    public static float w = 0.2F;
    public static float w2 = w / 2F;
    public static float h = 0.6F;
    
    private Random rand = new Random();
    
    public void updateBlock()
    {
		if (!worldObj.isRemote)
		{
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			onInventoryChanged();
		}
    }
    
    public ArrayList<TorchPosition> getTorches()
    {
    	return torches;
    }
    
    public boolean hasTorch(int index)
    {
    	return index >= 0 && index < torchCount();
    }
    
    public TorchPosition getTorch(int index)
    {
    	if (hasTorch(index))
    		return torches.get(index);
    	else
    		return null;
    }
    
    public void setTorch(int index, TorchPosition value)
    {
    	torches.set(index, value);
    }
    
    public TorchPosition addTorch(TorchPosition torchPos)
    {
    	torches.add(torchPos);
    	return torchPos; 
    }
    
    public boolean removeTorch(int index)
    {
		if (hasTorch(index))
		{
	    	TorchPosition removingTorch = getTorch(index);
	    	removingTorch.onBroken(worldObj, xCoord, yCoord, zCoord);
	    	torches.remove(index);
	    	updateBlock();
	    	return true;
		}
		
		return false;
    }
    
    public int torchCount()
    {
    	return torches.size();
    }
	
    /*
     * Adds a torch at the specified position, checking whether to add a clone torch above to complete bounds.
     */
	public TorchPosition addTorch(double x, double y, double z, int hitSide)
	{
		TorchPosition torchPos = new TorchPosition((float)x, (float)y, (float)z, hitSide);
		
		AxisAlignedBB torchBounds = torchPos.getBounds();
		
		torchPos = addTorch(torchPos);
		boolean broken = checkTorches();
		
		if (!broken)
		{
			torchPos.checkBounds();
			broken = checkTorches();
			
			disableTorchChecks = true;
			
			if (!broken && torchBounds.maxY > 1)
			{
				int cloneX = xCoord;
				int cloneY = yCoord + 1;
				int cloneZ = zCoord;

				TileEntityTorch cloningTorchTileEnt = QuiverMod.tileEntityTorchBlock.trySetBlock(worldObj, cloneX, cloneY, cloneZ);
				
				if (cloningTorchTileEnt != null)
				{
					cloningTorchTileEnt.disableTorchChecks = true;
					
					torchPos.clonedTorch = torchPos.id;
					torchPos.clonedX = cloneX;
					torchPos.clonedY = cloneY;
					torchPos.clonedZ = cloneZ;
					
					TorchPosition clone = torchPos.copy();
					clone.offY -= 1;
					clone.clonedX = xCoord;
					clone.clonedY = yCoord;
					clone.clonedZ = zCoord;
					
					cloningTorchTileEnt.addTorch(clone);
					
					worldObj.setBlockTileEntity(cloneX, cloneY, cloneZ, cloningTorchTileEnt);
					cloningTorchTileEnt.disableTorchChecks = false;
					
					cloningTorchTileEnt.updateAndCheckTorches();
				}
				else
				{
					torchPos.offY = 1 - h;
				}
			}

			disableTorchChecks = false;
		}
		
		updateBlock();
		
		return torchPos;
	}

	public boolean breakTorch(int torchIndex, boolean drop)
	{
	    TorchPosition torchPos = getTorch(torchIndex);
	    
	    if (removeTorch(torchIndex))
	    {
			dropTorch(torchPos, drop);
	    }
		
	    return updateAndCheckTorches();
	}

	public void dropTorch(TorchPosition torchPos, boolean drop)
	{
        if (!worldObj.isRemote)
        {
        	if (drop)
        	{
	        	dropTorch(worldObj, xCoord + torchPos.offX, yCoord + torchPos.offY, zCoord + torchPos.offZ);
        	}
        }
        else
        {
	        AxisAlignedBB torchBounds = torchPos.getBounds();
	        
	        double particleX = xCoord + torchBounds.minX;
	        double particleY = yCoord + torchBounds.minY;
	        double particleZ = zCoord + torchBounds.minZ;
	        
	        double sizeX = torchBounds.maxX - torchBounds.minX;
	        double sizeY = torchBounds.maxY - torchBounds.minY;
	        double sizeZ = torchBounds.maxZ - torchBounds.minZ;
	        
	        for (int i = 0; i < 64; i++)
	        {
	        	double offX = rand.nextDouble() * sizeX;
	        	double offY = rand.nextDouble() * sizeY;
	        	double offZ = rand.nextDouble() * sizeZ;
	        	
	        	QuiverMod.proxy.spawnDiggingFX(worldObj, particleX + offX, particleY + offY, particleZ + offZ, 0, 0, 0, Block.torchWood, 2, 0);
	        }
        }
	}
	
	public static void dropTorch(World world, double x, double y, double z)
	{
        ArrayList<ItemStack> items = Block.torchWood.getBlockDropped(world, (int)x, (int)y, (int)z, 0, 0);
        
        for (ItemStack item : items)
        {
            if (world.getGameRules().getGameRuleBooleanValue("doTileDrops"))
            {
                EntityItem dropItem = new EntityItem(world, x, y, z, item);
                dropItem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(dropItem);
            }
        }
	}
	
	public boolean checkTorches()
	{
		if (!disableTorchChecks)
		{
	    	Iterator<TorchPosition> iter = getTorches().iterator();
	    	boolean removedTorch = false;
	    	
	    	while (iter.hasNext())
	    	{
	    		TorchPosition torchPos = iter.next();
	    		AxisAlignedBB torchBounds = torchPos.getBounds();
		        boolean removeTorch = false;
		        
		        if (torchBounds.maxY < 0)
		        {
		        	removeTorch = true;
		        }
		        else if (torchBounds.minY > 1)
		        {
		        	removeTorch = true;
		        }
		        else if (torchPos.clonedTorch == -1 || torchPos.cloneOriginal)
		        {
		        	ForgeDirection side = null;
		        	int checkX = xCoord;
		        	int checkY = yCoord;
		        	int checkZ = zCoord;
		        	
			        switch (torchPos.side)
			        {
			        case 5:
			        	side = EAST;
			        	checkX--;
			        	break;
			        case 4:
			        	side = WEST;
			        	checkX++;
			        	break;
			        case 3:
			        	side = SOUTH;
			        	checkZ--;
			        	break;
			        case 2:
			        	side = NORTH;
			        	checkZ++;
			        	break;
			        case 1:
				        side = UP;
				        checkY--;
			        	break;
			        default:
			        	removeTorch = true;
			        	break;
			        }
			        
			        if (!removeTorch)
			        {
	        			int blockID = worldObj.getBlockId(checkX, checkY, checkZ);
	        			int metadata = worldObj.getBlockMetadata(checkX, checkY, checkZ);
	        			int partialBlockCheck = PartialBlockSurfaces.checkPosition(torchPos.offX, torchPos.offY, torchPos.offZ, side, blockID, metadata, w2);
	        			
	        			if (partialBlockCheck == 0)
		        		{
		        			removeTorch = true;
		        		}
	        			else if (partialBlockCheck == -1)
			        	{
	        				switch (side)
	        				{
	        				case UP:
	    				        if (!QuiverMod.tileEntityTorchBlock.canPlaceTorchOn(worldObj, xCoord, yCoord - 1, zCoord))
	    				        {
	    				            removeTorch = true;
	    				            break;
	    				        }
					        default:
						        if (!worldObj.isBlockSolidOnSide(checkX, checkY, checkZ, side, true))
						            removeTorch = true;
					        	break;
	        				}
			        	}
			        }
		        }
		        
		        if (removeTorch)
		        {
		            dropTorch(torchPos, true);
		            torchPos.onBroken(worldObj, xCoord, yCoord, zCoord);
		            iter.remove();
		            removedTorch = true;
		        }
	    	}
	    	
			int count = torchCount();
			
			if (count < 1)
			{
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			}
	    	
			if (removedTorch)
			{
	            updateBlock();
	            return true;
			}
		}
		
	    return false;
	}
	
	public boolean updateAndCheckTorches()
	{
		boolean check = checkTorches();
		updateBlock();
		return check;
	}
	
	public AxisAlignedBB getBounds(int torch)
	{
		TorchPosition torchPos = getTorch(torch);
		AxisAlignedBB torchBounds = torchPos.getBounds();
		setTorch(torch, torchPos);
        
        return torchBounds;
	}
	
	private void checkForNormalTorches()
	{
		if (torchCount() == 1)
	    {
	    	TorchPosition lastTorch = torches.get(0);
	    	
	    	if (lastTorch.isNormalTorch)
	    	{
	    		worldObj.setBlock(xCoord, yCoord, zCoord, Block.torchWood.blockID);
	    		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, lastTorch.getTorchBlockMetadata(), 0);
	    	}
	    }
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		checkForNormalTorches();
		
		for (int i = 0; i < torches.size(); i++)
		{
			TorchPosition torchPos = torches.get(i);
			
			if (rand.nextInt(8) == 0)
			{
		        double x = xCoord + torchPos.offX;
		        double y = yCoord + torchPos.offY + 0.7;
		        double z = zCoord + torchPos.offZ;

		        float particleOffX = torchPos.particleOffX;
		        float particleOffZ = torchPos.particleOffZ;
		        
		        if (!torchPos.particleOffsetSet)
		        {
			        switch (torchPos.side)
			        {
			        case 5:
			        	particleOffX = horizOff;
			        	break;
			        case 4:
			        	particleOffX = -horizOff;
			        	break;
			        case 3:
			        	particleOffZ = horizOff;
			        	break;
			        case 2:
			        	particleOffZ = -horizOff;
			        	break;
			        }
			        
			        torchPos.particleOffsetSet = true;
			        torchPos.particleOffX = particleOffX;
			        torchPos.particleOffZ = particleOffZ;
			        setTorch(i, torchPos);
		        }
		        
		        worldObj.spawnParticle("smoke", x + particleOffX, y, z + particleOffZ, 0.0D, 0.0D, 0.0D);
		        worldObj.spawnParticle("flame", x + particleOffX, y, z + particleOffZ, 0.0D, 0.0D, 0.0D);
			}
		}
	}
    
    public Packet getDescriptionPacket() {
    	NBTTagCompound comp = new NBTTagCompound();
    	writeToNBT(comp);
    	Packet132TileEntityData packet = new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, comp);
    	
    	return packet;
    }
    
    public void onDataPacket(INetworkManager net, Packet132TileEntityData packet)
    {
    	readFromNBT(packet.data);
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        
        torches = new ArrayList();
        NBTTagList torchList = compound.getTagList("torches");

        for (int i = 0; i < torchList.tagCount(); i++)
        {
	        NBTTagCompound torchComp = (NBTTagCompound)torchList.tagAt(i);
	        addTorch(TorchPosition.readFromNBT(torchComp));
        }
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        NBTTagList torchList = new NBTTagList();
        
        for (TorchPosition torchPos : torches)
        {
        	NBTTagCompound torchComp = new NBTTagCompound();
        	torchPos.writeToNBT(torchComp);
	        torchList.appendTag(torchComp);
        }
        
        compound.setTag("torches", torchList);
    }
	
}
