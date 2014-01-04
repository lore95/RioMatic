package it.unibz.mngeng.java.Commons;

public class Utility 
{
	static public String byteToString(byte[] buffer)
	{
		StringBuilder sBuf = new StringBuilder();
		for(int i = 0; i < 6; i++){
			char c = (char) buffer[i];
			sBuf.append(c);
		}
		return sBuf.toString();
	}
	
	static public String byteToBits(byte buf)
	{
		String retVal = "";
		for(int i = 0; i < 8 ; i++)
		{
			if ((buf & (2^i)) != 0)
			{
				retVal += "1";
			}
			else
			{
				retVal += "0";
			}
		}
		return retVal;
	}
}
