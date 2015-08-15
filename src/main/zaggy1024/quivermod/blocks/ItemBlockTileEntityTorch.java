package zaggy1024.quivermod.blocks;

import zaggy1024.quivermod.QuiverMod;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockTileEntityTorch extends ItemBlock {

	public ItemBlockTileEntityTorch(int id) {
		super(id);
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int side,
			float hitX, float hitY, float hitZ)
	{
		int noOffX = x;
		int noOffY = y;
		int noOffZ = z;
		
		switch (side)
		{
		case 0:
			y--;
			break;
		case 1:
			y++;
			break;
		case 2:
			z--;
			break;
		case 3:
			z++;
			break;
		case 4:
			x--;
			break;
		case 5:
			x++;
			break;
		}
	    
		if (player.canPlayerEdit(x, y, z, side, stack))
		{
    		if (!world.isRemote)
    		{
    			QuiverMod.tileEntityTorchBlock.tryPlace(world, x, y, z, noOffX + hitX, noOffY + hitY, noOffZ + hitZ, side);
    			
    			if (!player.capabilities.isCreativeMode)
    				--stack.stackSize;
    		}
    		
    		return true;
    		
			/*int blockID = world.getBlockId(x, y, z);
	    	
	    	if (blockID == 0)
	    	{
	    		int metadata = Block.torchWood.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, 0);
	            ItemBlock itemBlock = (ItemBlock)Item.itemsList[Block.torchWood.blockID];
	            
	            if (world.setBlock(x, y, z, Block.torchWood.blockID, metadata, 7))
	            {
	            	world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, Block.torchWood.stepSound.getPlaceSound(), (Block.torchWood.stepSound.getVolume() + 1) / 2, Block.torchWood.stepSound.getPitch() * 0.8F);
	        		
	            	if (!player.capabilities.isCreativeMode)
	            		player.inventory.consumeInventoryItem(Block.torchWood.blockID);
	            	
	            	return true;
	            }
	    	}*/
	    }

		return false;
	}
	
    public boolean canPlaceItemBlockOnSide(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer, ItemStack par7ItemStack)
    {
        return true;
    }
	
}
