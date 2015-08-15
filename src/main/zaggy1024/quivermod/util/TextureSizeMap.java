package zaggy1024.quivermod.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.Resource;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;
import zaggy1024.quivermod.QuiverMod;

public class TextureSizeMap {
	
	public static class TextureSize
	{
		public int width;
		public int height;
		
		public TextureSize(int width, int height)
		{
			this.width = width;
			this.height = height;
		}
	}
	
	private static HashMap<ResourceLocation, TextureSize> sizesMap = new HashMap();
	
	public static void clear()
	{
		sizesMap.clear();
	}
	
	public static TextureSize getSize(ResourceLocation textureLocation, int defaultW, int defaultH)
	{
		TextureSize size = sizesMap.get(textureLocation);
		
		if (size == null)
		{
			InputStream inputstream = null;
			
			try
			{
				ResourceManager resMan = QuiverMod.proxy.mc.getResourceManager();
	            Resource resource = resMan.getResource(textureLocation);
	            inputstream = resource.getInputStream();
	
	            if (inputstream != null)
	            {
	                BufferedImage bufferedImage = ImageIO.read(inputstream);
	                inputstream.close();
	                size = new TextureSize(bufferedImage.getWidth(), bufferedImage.getHeight());
	                sizesMap.put(textureLocation, size);
	            }
			}
			catch (Exception e) {
				QuiverMod.log("An error occurred getting the size of a texture. Please post this log on the Better Bows thread on the Minecraft forums.");
				e.printStackTrace();
				size = null;
			}
		}
		
		if (size != null)
			return size;
		else
			return new TextureSize(defaultW, defaultH);
	}
	
}
