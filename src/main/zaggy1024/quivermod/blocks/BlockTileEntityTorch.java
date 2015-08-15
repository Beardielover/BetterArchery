package zaggy1024.quivermod.blocks;

import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.WEST;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Callable;

import zaggy1024.quivermod.QuiverMod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTileEntityTorch extends Block implements ITileEntityProvider {
	
    public static float w = 0.2F;
    public static float w2 = w / 2F;
    public static float h = 0.6F;

	public BlockTileEntityTorch(int id, Material material) {
		super(id, material);
		
		setUnlocalizedName("tileEntityTorch");
		setLightValue(0.9375F);
		setBlockBounds(0, 0, 0, 0, 0, 0);
	}
	
	public TileEntityTorch trySetBlock(World world, int intX, int intY, int intZ)
	{
		int hitBlockID = world.getBlockId(intX, intY, intZ);
		Material hitBlockMat = world.getBlockMaterial(intX, intY, intZ);
		
		int hitBlockMetadata = world.getBlockMetadata(intX, intY, intZ);
		boolean hitTorch = hitBlockID == Block.torchWood.blockID;
		boolean replace = (hitBlockMat.isReplaceable() && !hitBlockMat.isLiquid()) || hitTorch;
		
		if (replace)
		{
			world.setBlock(intX, intY, intZ, QuiverMod.tileEntityTorchBlock.blockID);
			hitBlockID = QuiverMod.tileEntityTorchBlock.blockID;
		}
		
		if (replace || hitBlockID == QuiverMod.tileEntityTorchBlock.blockID)
		{
			TileEntityTorch torchTileEnt = (TileEntityTorch)world.getBlockTileEntity(intX, intY, intZ);
			
			if (torchTileEnt == null)
				torchTileEnt = new TileEntityTorch();
			
			if (hitTorch)
			{
				int torchSide = 6 - hitBlockMetadata;
				
		        double horizOff = 1;
		        double vertOff = 0.2;
				double offX = 0;
				double offY = vertOff;
				double offZ = 0;
				
				switch (hitBlockMetadata)
				{
				case 1:
					offX -= horizOff;
					break;
				case 2:
					offX += horizOff;
					break;
				case 3:
					offZ -= horizOff;
					break;
				case 4:
					offZ += horizOff;
					break;
				default:
					offY = 0;
					break;
				}
				
				TorchPosition torch = new TorchPosition((float)(0.5 + offX), (float)(offY), (float)(0.5 + offZ), torchSide);
				torch.checkBounds();
				torch.isNormalTorch = true;
				torchTileEnt.addTorch(torch);
			}
			
			world.setBlockTileEntity(intX, intY, intZ, torchTileEnt);
	        
			return torchTileEnt;
		}
		
		return null;
	}
	
	public boolean tryPlace(World world, int intX, int intY, int intZ, double hitX, double hitY, double hitZ, int side)
	{
		TileEntityTorch torchTileEnt = trySetBlock(world, intX, intY, intZ);
		
		if (torchTileEnt != null)
		{
			TorchPosition torchPos = torchTileEnt.addTorch(hitX - intX, hitY - intY, hitZ - intZ, side);
			
			world.setBlockTileEntity(intX, intY, intZ, torchTileEnt);
			
			StepSound sound = QuiverMod.tileEntityTorchBlock.stepSound;
	        world.playSoundEffect(hitX, hitY, hitZ, sound.getPlaceSound(), (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
	        
			return true;
		}
		else
		{
			TileEntityTorch.dropTorch(world, hitX, hitY, hitZ);
		}
		
		return false;
	}
	
	@Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
    {
	    TileEntityTorch torch = (TileEntityTorch)world.getBlockTileEntity(x, y, z);
        ArrayList<ItemStack> out = new ArrayList<ItemStack>();
        
        ArrayList<ItemStack> torchDrops;
        
        for (TorchPosition torchPos : torch.getTorches())
        {
        	torchDrops = Block.torchWood.getBlockDropped(world, x, y, z, torchPos.getTorchBlockMetadata(), 0);
        	out.addAll(torchDrops);
        }
        
        return out;
    }
    
	@Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        return Block.torchWood.getPickBlock(target, world, x, y, z);
    }

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
	
    public boolean canRenderInPass(int pass)
    {
        return false;
    }
    
	@Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityTorch();
    }
    
    private void setBounds(AxisAlignedBB bb)
    {
        minX = bb.minX;
        minY = bb.minY;
        minZ = bb.minZ;
        maxX = bb.maxX;
        maxY = bb.maxY;
        maxZ = bb.maxZ;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 startVec3, Vec3 endVec3)
    {
	    TileEntityTorch torch = (TileEntityTorch)world.getBlockTileEntity(x, y, z);
	    
	    int i = 0;
	    ArrayList<MovingObjectPosition> mops = new ArrayList();
	    ArrayList<TorchPosition> hitTorches = new ArrayList();
	    
	    for (TorchPosition torchPos : torch.getTorches())
	    {
	    	AxisAlignedBB bb = torchPos.getBounds();
	    	
	    	if (bb != null)
	    	{
		        setBounds(bb);
		        MovingObjectPosition mop = super.collisionRayTrace(world, x, y, z, startVec3, endVec3);
		        
		        if (mop != null)
		        {
			        mop.subHit = i;
		        	mops.add(mop);
		        	hitTorches.add(torchPos);
		        }
	    	}
	    	
	    	i++;
	    }
	    
	    i = 0;
	    int closestIndex = -1;
	    double distance = -1;

	    double curDistance;
	    
	    for (MovingObjectPosition mop : mops)
	    {
	    	curDistance = mop.hitVec.squareDistanceTo(startVec3);
	    	
	    	if (distance == -1 || curDistance < distance)
	    	{
	    		distance = curDistance;
	    		closestIndex = i;
	    	}
	    	
	    	i++;
	    }
    	
	    if (closestIndex != -1)
	    {
	    	setBounds(hitTorches.get(closestIndex).getBounds());
	    	return mops.get(closestIndex);
	    }
	    else
	    {
	    	return null;
	    }
    }
    
    @Override
    public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
    {
		TileEntityTorch torch = (TileEntityTorch)world.getBlockTileEntity(x, y, z);
		
    	if (world.isRemote)
    	{
    		Vec3 playerPos = player.getPosition(1);
    		playerPos = playerPos.addVector(ActiveRenderInfo.objectX, ActiveRenderInfo.objectY, ActiveRenderInfo.objectZ);
	    	Vec3 lookVec = player.getLookVec();
	    	lookVec = playerPos.addVector(lookVec.xCoord * 10, lookVec.yCoord * 10, lookVec.zCoord * 10);
	    	
	    	MovingObjectPosition mop = collisionRayTrace(world, x, y, z, playerPos, lookVec);
	    	
	    	if (mop != null)
	    	{
	    		torch.breakTorch(mop.subHit, false);
	    		
	            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
	            
	            try {
	            	DataOutputStream outputStream = new DataOutputStream(byteOutputStream);
	                outputStream.writeInt(x);
	                outputStream.writeInt(y);
	                outputStream.writeInt(z);
	                outputStream.writeInt(mop.subHit);
	            }
	            catch (Exception ex) {
	                ex.printStackTrace();
	            }
		    	
		    	Packet250CustomPayload packet = new Packet250CustomPayload();
		    	packet.channel = "BreakTorch";
		    	packet.data = byteOutputStream.toByteArray();
		    	packet.length = byteOutputStream.size();
		    	
		    	PacketDispatcher.sendPacketToServer(packet);
	    	}
    	}
    	
    	return false;
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID)
    {
    	TileEntityTorch torch = (TileEntityTorch)world.getBlockTileEntity(x, y, z);
    	torch.checkTorches();
    }
    
    public boolean canPlaceTorchOn(World world, int x, int y, int z)
    {
        if (world.doesBlockHaveSolidTopSurface(x, y, z))
        {
            return true;
        }
        else
        {
            int id = world.getBlockId(x, y, z);
            return (Block.blocksList[id] != null && Block.blocksList[id].canPlaceTorchOnTop(world, x, y, z));
        }
    }
    
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
    	blockIcon = iconRegister.registerIcon("quivermod:blank");
    }
	
}
