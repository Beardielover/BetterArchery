package zaggy1024.quivermod.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import zaggy1024.quivermod.QuiverMod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;

public class PacketHelper {
	
	public static Packet250CustomPayload makePacket(String channel, Object... contents)
	{
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
	    
	    try {
	    	DataOutputStream outputStream = new DataOutputStream(byteOutputStream);
	    	
	    	for (Object obj : contents)
	    	{
	    		if (obj instanceof Integer)
	    		{
	    			outputStream.writeInt((Integer)obj);
	    		}
	    		else if (obj instanceof Float)
	    		{
	    			outputStream.writeFloat((Float)obj);
	    		}
	    		else if (obj instanceof Double)
	    		{
	    			outputStream.writeDouble((Double)obj);
	    		}
	    		else if (obj instanceof String)
	    		{
	    			outputStream.writeUTF((String)obj);
	    		}
	    		else if (obj instanceof Boolean)
	    		{
	    			outputStream.writeBoolean((Boolean)obj);
	    		}
	    	}
	    	
	    	return new Packet250CustomPayload(channel, byteOutputStream.toByteArray());
	    }
	    catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    
		return null;
	}
	
}
