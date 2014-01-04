package it.unibz.mngeng.java.Raspberry;

import java.io.IOException;
import java.util.Random;

import it.unibz.mngeng.java.Commons.Utility;
import it.unibz.mngeng.java.Handlers.DataStructures;

public class SerialHandler implements Runnable
{
	private static final int[] BYTE_BITS = new int[] {0x1, 0x2, 0x4, 0x8, 0x10, 0x20, 0x40, 0x80};
	private static final int MOISTURE_MASK = 0x1 | 0x2 | 0x4 | 0x8;
	private static final int SENSOR_ADDR_MASK = 0x10 | 0x20 | 0x40;
	private static final double ADDA_MOISTURE_CONVERSION_FACTOR = 6.667;
	

	private DataStructures appData;
	private boolean shutDown;
	private byte buffer = 0;
	private Random rn ;
	
	public SerialHandler(DataStructures appData, boolean shutDown)
	{
		rn = new Random();		
		this.appData = appData;
		this.shutDown = shutDown;
	}

	private int readFromSerial()
	{
		buffer = 0;
		for(int i = 0; i < 8; i++)
		{
			if (rn.nextBoolean())
				buffer |= BYTE_BITS[i];
		}
		return(0);
	}
	
	@Override
	public void run() 
	{
		System.out.println("THread Serial started");
		System.out.println("Moisture at 3 = " + appData.getMoisture(3));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try 
		{
			while(!shutDown)
			{
				readFromSerial();
				double moistureValue = (buffer & MOISTURE_MASK) * ADDA_MOISTURE_CONVERSION_FACTOR;
				int sensorId = (buffer & SENSOR_ADDR_MASK) >> 4;
				System.out.println("Buffer '" + Utility.byteToBits(buffer) + " - Loggin moisture " + moistureValue + " @ " + sensorId);
				try
				{
					appData.setMoisture(sensorId, moistureValue, true);
				}
				catch (IOException e) 
				{
					System.out.println("Exception in setMoisture: " + e.getMessage());
				}
				Thread.sleep(1000);
			}
		}
		catch (InterruptedException e) 
		{
			System.out.println("Exception in SerialHandler: " + e.getMessage());
			System.exit(1);
		}
		System.exit(0);
	}
}
