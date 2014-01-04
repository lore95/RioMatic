package it.unibz.mngeng.java.Handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.Commons.Utility;

public class DataStructures 
{
	static protected final int TYPE_MOISTURE = 0;
	static protected final int TYPE_WATERING_TIME = 1;
	static protected final int ITEM_LEN = 6;
	static protected final String DATA_ITEM_FORMAT = "%0" + ITEM_LEN + ".2f";
	static protected int RECORD_LEN = 0;

	private double[] moisture;
	private boolean[] valveStatus;
	private int[] wateringTimeElapsed;
	private Parameters parms;
	private RandomAccessFile persistedData;
	
	public DataStructures(Parameters parms) throws IOException
	{
		this.parms = parms;
		RECORD_LEN = ITEM_LEN * parms.getNumberOfSensors() + 1;
		
		persistedData = new RandomAccessFile(new File(parms.getPersistFilePath()), "rwd");
		this.moisture = new double[parms.getNumberOfSensors()];
		this.wateringTimeElapsed = new int[parms.getNumberOfSensors()];
		this.valveStatus = new boolean[parms.getNumberOfSensors()];
		for(int i = 0; i < parms.getNumberOfSensors(); i++)
		{
			valveStatus[i] = false;
		}
		byte[] buffer = new byte[6];
		for(int i = 0; i < parms.getNumberOfSensors(); i++)
		{
			persistedData.seek(i * ITEM_LEN);
			persistedData.read(buffer, 0, ITEM_LEN);
			moisture[i] = Double.valueOf(Utility.byteToString(buffer)).doubleValue();
		}
		for(int i = 0; i < parms.getNumberOfSensors(); i++)
		{
			persistedData.seek(RECORD_LEN + i * ITEM_LEN);
			persistedData.read(buffer, 0, ITEM_LEN);
			wateringTimeElapsed[i] = Double.valueOf(Utility.byteToString(buffer)).intValue();
		}
	}
	
	protected void finalize() throws Throwable 
	{
		try 
		{
			persistedData.close();        // close open files
		}
		finally 
		{
			super.finalize();
		}
	}
	
	protected void persistData(int type, int pos, double value) throws IOException
	{
		long seekAt = 0;

		seekAt = pos * 6;
		switch(type)
		{
		case TYPE_MOISTURE:
			break;
		
		case TYPE_WATERING_TIME:
			seekAt += RECORD_LEN;
			break;
		}

		persistedData.seek(seekAt);
		persistedData.write(String.format(DATA_ITEM_FORMAT, value).getBytes());
	}
	
	
	public double[] getMoisture() 
	{
		return moisture;
	}
	
	public double getMoisture(int i)
	{
		return moisture[i];
	}
	
	public void setMoisture(double[] moisture, boolean persist) throws IOException 
	{
		this.moisture = moisture;
		if (persist)
			for(int i = 0; i < parms.getNumberOfSensors(); i++)
			{
				persistData(TYPE_MOISTURE, i, moisture[i]);
			}
	}
	
	public void setMoisture(int pos, double moisture, boolean persist) throws IOException
	{
		this.moisture[pos] = moisture;
		if (persist)
			persistData(TYPE_MOISTURE, pos, moisture);
	}

	public int[] getWateringTimeElapsed() 
	{
		return wateringTimeElapsed;
	}
	
	public int getWateringTimeElapsed(int pos) 
	{
		return wateringTimeElapsed[pos];
	}
	
	public void setWateringTimeElapsed(int[] wateringTimeElapsed, boolean persist) throws IOException 
	{
		this.wateringTimeElapsed = wateringTimeElapsed;
		if (persist)
			for(int i = 0; i < parms.getNumberOfSensors(); i++)
			{
				persistData(TYPE_WATERING_TIME, i, wateringTimeElapsed[i]);
			}
	}

	public void setWateringTimeElapsed(int pos, int wateringTimeElapsed, boolean persist) throws IOException 
	{
		this.wateringTimeElapsed[pos] = wateringTimeElapsed;
		if (persist)
			persistData(TYPE_WATERING_TIME, pos, wateringTimeElapsed);
	}

	public boolean[] getValveStatus() 
	{
		return valveStatus;
	}

	public boolean getValveStatus(int pos) 
	{
		return valveStatus[pos];
	}

	public void setValveStatus(boolean[] valveStatus) 
	{
		this.valveStatus = valveStatus;
	}

	public void setValveStatus(int pos, boolean valveStatus) 
	{
		this.valveStatus[pos] = valveStatus;
	}
}