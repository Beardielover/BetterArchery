package zaggy1024.quivermod;

import zaggy1024.quivermod.entities.EntityQuiverModArrow;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DispenserBehaviorQuiverModArrow extends BehaviorProjectileDispense {

	@Override
    protected IProjectile getProjectileEntity(World world, IPosition position)
    {
		return null;
    }

    protected IProjectile getProjectileEntity(World world, ItemStack stack, IPosition position)
    {
		TileEntityDispenser dispenserTileEntity = (TileEntityDispenser)world.getBlockTileEntity((int)Math.floor(position.getX()),
				(int)Math.floor(position.getY()),
				(int)Math.floor(position.getZ()));
        EntityQuiverModArrow arrow = new EntityQuiverModArrow(world, position.getX(), position.getY(), position.getZ());
        arrow.canBePickedUp = 1;
        arrow.setValuesForStack(stack);
        
        return arrow;
    }
	
	@Override
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack)
    {
        World world = blockSource.getWorld();
        
		if (!world.isRemote)
		{
	        IPosition position = BlockDispenser.getIPositionFromBlockSource(blockSource);
	        EnumFacing facing = BlockDispenser.getFacing(blockSource.getBlockMetadata());
	        
	        IProjectile projectile = getProjectileEntity(world, stack, position);
	        projectile.setThrowableHeading((double)facing.getFrontOffsetX(), (double)((float)facing.getFrontOffsetY() + 0.1F), (double)facing.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
	        world.spawnEntityInWorld((Entity)projectile);
		}
        
        stack.splitStack(1);
        return stack;
    }

}
