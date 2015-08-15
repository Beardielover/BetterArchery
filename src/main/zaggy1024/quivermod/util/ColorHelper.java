package zaggy1024.quivermod.util;

import org.lwjgl.opengl.GL11;

public class ColorHelper {

	public static int getRed(int color) {
        return BitwiseHelper.getInteger(color, 16, 23);
	}
	
	public static int getGreen(int color) {
        return BitwiseHelper.getInteger(color, 8, 15);
	}
	
	public static int getBlue(int color) {
        return BitwiseHelper.getInteger(color, 0, 7);
	}

	public static float getFloatRed(int color) {
        return getRed(color) / 255F;
	}
	
	public static float getFloatGreen(int color) {
        return getGreen(color) / 255F;
	}
	
	public static float getFloatBlue(int color) {
        return getBlue(color) / 255F;
	}

	public static void glSetColor(int color) {
		GL11.glColor4f(getFloatRed(color), getFloatGreen(color), getFloatBlue(color), 1);
	}

	public static int getColor(int red, int green, int blue) {
		int out = BitwiseHelper.setInteger(0, 16, 23, red);
		out = BitwiseHelper.setInteger(out, 8, 15, green);
		out = BitwiseHelper.setInteger(out, 0, 7, blue);
		return out;
	}

}
