package zaggy1024.quivermod.util;

public class QuiverModMath {
	
	public static float clamp(float value, float min, float max)
	{
		if (value < min)
		{
			value = min;
		}
		else if (value > max)
		{
			value = max;
		}
		
		return value;
	}
	
}
