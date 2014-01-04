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
}
