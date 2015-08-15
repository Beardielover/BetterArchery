package zaggy1024.quivermod.blocks;

import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TorchPosition {

	public static float w = TileEntityTorch.w;
	public static float w2 = TileEntityTorch.w2;
	public static float w4 = w2 / 2;
	public static float h = TileEntityTorch.h;
	
	private static int currentID = -1;
	
	public int id;

	public float offX;
	public float offY;
	public float offZ;
	public int side;
	
	public boolean isNormalTorch = false;

	public int clonedTorch = -1;
	public int clonedX;
	public int clonedY;
	public int clonedZ;
	public boolean cloneOriginal = true;
	
	private AxisAlignedBB bounds = null;
	
	public boolean particleOffsetSet = false;
	public float particleOffX = 0;
	public float particleOffZ = 0;
	
	protected void setPosition(float x, float y, float z, int hitSide)
	{
		offX = x;
		offY = y;
		side = hitSide;
		
		offX = x;
		offY = y;
		offZ = z;
	}
	
	private void setID(int newID)
	{
		id = newID;
		
		if (currentID < newID)
			currentID = newID;
	}
	
	private void setID()
	{
		setID(currentID + 1);
	}
	
	public TorchPosition(float x, float y, float z, int hitSide, boolean setID)
	{
		setPosition(x, y, z, hitSide);
		
		if (setID)
		{
			setID();
		}
	}
	
	public TorchPosition(float x, float y, float z, int hitSide)
	{
		setPosition(x, y, z, hitSide);
		setID();
	}
	
	public void checkBounds()
	{
		if (offX - w2 < 0)
		{
			offX = w2;
		}
		else if (offX + w2 > 1)
		{
			offX = 1 - w2;
		}
		
		if (offZ - w2 < 0)
		{
			offZ = w2;
		}
		else if (offZ + w2 > 1)
		{
			offZ = 1 - w2;
		}
	}
	
	public boolean equals(TorchPosition torch)
	{
		return torch.id == id;
	}
	
	public void onBroken(World world, int x, int y, int z)
	{
		if (clonedTorch != -1)
		{
			TileEntityTorch torchTile = (TileEntityTorch)world.getBlockTileEntity(clonedX, clonedY, clonedZ);
			
			if (torchTile != null)
			{
				Iterator<TorchPosition> iter = torchTile.getTorches().iterator();
				
				while (iter.hasNext())
				{
					TorchPosition clonedTorch = iter.next();
					
					if (clonedTorch.equals(this))
					{
						iter.remove();
						torchTile.updateBlock();
						break;
					}
				}
			}
		}
	}
	
	public TorchPosition copy()
	{
		TorchPosition out = new TorchPosition(offX, offY, offZ, side);
		out.id = id;
		
		out.isNormalTorch = isNormalTorch;

		out.clonedTorch = clonedTorch;
		out.clonedX = clonedX;
		out.clonedY = clonedY;
		out.clonedZ = clonedZ;
		out.cloneOriginal = false;
		
		out.particleOffsetSet = particleOffsetSet;
		out.particleOffX = particleOffX;
		out.particleOffZ = particleOffZ;
		
		return out;
	}

	public AxisAlignedBB getBounds() {
		if (bounds == null)
		{
			bounds = AxisAlignedBB.getBoundingBox(offX - w2, offY, offZ - w2, offX + w2, offY + h, offZ + w2);

			if (side > 3)
			{
				bounds.minZ -= w4;
				bounds.maxZ += w4;
			}
			else if (side > 1)
			{
				bounds.minX -= w4;
				bounds.maxX += w4;
			}
			
    	    if (side == 4)
    	    {
    	    	bounds.minX -= BlockTileEntityTorch.w2;
    	    }
    	    else if (side == 5)
    	    {
    	    	bounds.maxX += BlockTileEntityTorch.w2;
    	    }
    		else if (side == 2)
    	    {
    			bounds.minZ -= BlockTileEntityTorch.w2;
    	    }
    	    else if (side == 3)
    	    {
    	    	bounds.maxZ += BlockTileEntityTorch.w2;
    	    }
        }
		
		return bounds;
	}
	
	public void setBounds(AxisAlignedBB bb)
	{
		bounds = bb;
	}

	public int getTorchBlockMetadata()
	{
		return 6 - side;
	}

	public static TorchPosition readFromNBT(NBTTagCompound compound)
	{
		TorchPosition out = new TorchPosition(compound.getFloat("offX"), compound.getFloat("offY"), compound.getFloat("offZ"), compound.getInteger("side"), false);
		out.setID(compound.getInteger("id"));

		if (compound.hasKey("clonedTorch"))
		{
			out.clonedTorch = compound.getInteger("clonedTorch");
			out.clonedX = compound.getInteger("clonedX");
			out.clonedY = compound.getInteger("clonedY");
			out.clonedZ = compound.getInteger("clonedZ");
			out.cloneOriginal = compound.getBoolean("cloneOriginal");
		}
		
		out.isNormalTorch = compound.getBoolean("normalTorch");
		
		if (compound.hasKey("currentID"))
		{
			int newCurrentID = compound.getInteger("currentID");
			
			if (newCurrentID > currentID)
				currentID = newCurrentID;
		}
		
		return out;
	}
	
	public void writeToNBT(NBTTagCompound compound)
	{
        compound.setInteger("id", id);
		compound.setFloat("offX", offX);
        compound.setFloat("offY", offY);
        compound.setFloat("offZ", offZ);
        compound.setInteger("side", side);
        
        if (clonedTorch != -1)
        {
        	compound.setInteger("clonedTorch", clonedTorch);
        	compound.setInteger("clonedX", clonedX);
        	compound.setInteger("clonedY", clonedY);
        	compound.setInteger("clonedZ", clonedZ);
        	compound.setBoolean("cloneOriginal", cloneOriginal);
        }
        
        compound.setBoolean("normalTorch", isNormalTorch);
        
        compound.setInteger("currentID", currentID);
	}
	
}
