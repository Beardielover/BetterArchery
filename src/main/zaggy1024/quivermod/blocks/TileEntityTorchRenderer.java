package zaggy1024.quivermod.blocks;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import zaggy1024.quivermod.QuiverMod;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityTorchRenderer extends TileEntitySpecialRenderer {
	
    private static double topOff = 0.4;
    private static double bottomOff = 0.3;
	
    private void drawOutlinedBoundingBox(AxisAlignedBB par1AxisAlignedBB)
    {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(3);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.draw();
        tessellator.startDrawing(3);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.draw();
        tessellator.startDrawing(1);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.draw();

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double offX, double offY, double offZ, float partialTick)
	{
		Minecraft mc = QuiverMod.proxy.mc;
		TileEntityTorch torch = (TileEntityTorch)tileEntity;
		
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		
        QuiverMod.proxy.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		
		Tessellator tess = Tessellator.instance;
		ArrayList<TorchPosition> torches = (ArrayList<TorchPosition>)torch.getTorches().clone();

		tess.startDrawingQuads();
		
		for (TorchPosition torchPos : torches)
		{
			double torchTopX = 0;
			double torchTopZ = 0;
			
			double torchBottomX = 0;
			double torchBottomZ = 0;
			
			switch (torchPos.side)
			{
			case 5:
	            torchTopX += -topOff;
	            torchBottomX += bottomOff;
	            break;
			case 4:
	            torchTopX += topOff;
	            torchBottomX += -bottomOff;
	            break;
			case 3:
	        	torchTopZ += -topOff;
	        	torchBottomZ += bottomOff;
	            break;
			case 2:
	        	torchTopZ += topOff;
	        	torchBottomZ += -bottomOff;
	            break;
			}
			
			mc.renderGlobal.globalRenderBlocks.renderTorchAtAngle(Block.torchWood, offX - 0.5 + torchBottomX + torchPos.offX, offY + torchPos.offY, offZ - 0.5 + torchBottomZ + torchPos.offZ, torchTopX, torchTopZ, 0);
		}
		
        tess.draw();

		GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
	}

}
