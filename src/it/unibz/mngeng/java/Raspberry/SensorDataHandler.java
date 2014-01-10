package it.unibz.mngeng.java.Raspberry;

import java.io.IOException;
import java.text.Format;
import java.util.Random;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.Handlers.DataStructures;

public class SensorDataHandler extends Thread 
{
	private static final int[] BYTE_BITS = new int[] {0x1, 0x2, 0x4, 0x8, 0x10, 0x20, 0x40, 0x80};	

	private DataStructures appData;
	private boolean shutDown;
	private byte buffer = 0;
	private double moistureValue = 0.0;
	private Random rn ;
	private boolean execAsTest = false;
	private I2CBus bus;
	private I2CDevice device;
	private Parameters parms;
	
	public SensorDataHandler(DataStructures appData, Parameters parms, boolean shutDown) throws IOException
	{
		rn = new Random();		
		this.appData = appData;
		this.shutDown = shutDown;
		this.parms = parms;
        // create I2C communications bus instance
		bus = I2CFactory.getInstance(I2CBus.BUS_1);
        // create I2C device instance
        device = bus.getDevice(parms.getADCAddress());
	}

	public SensorDataHandler(DataStructures appData, Parameters parms, boolean shutDown, boolean isTest) throws IOException
	{
		rn = new Random();		
		this.appData = appData;
		this.shutDown = shutDown;
		this.execAsTest = true;
		this.parms = parms;
	}

	private void readFromI2C(int sensorId)
	{
		if (execAsTest)
		{
			buffer = 0;
			for(int i = 0; i < 8; i++)
			{
				if (rn.nextBoolean())
					buffer |= BYTE_BITS[i];
			}
		}
		else
		{
			try 
			{
				device.write((byte) (0x40 | (sensorId & 3)));
				device.read();
				buffer = (byte) device.read();
			}
			catch (IOException e) 
			{
				// TODO error handling
				return;
			}
		}
		
		double multiplier = 100 / (parms.getSensorRange(sensorId)[1] - parms.getSensorRange(sensorId)[0]);
		moistureValue = 100 - buffer * multiplier;
		return;
	}
	
	@Override
	public void run() 
	{
		try 
		{
			while(!shutDown)
			{
				for(int i = 0; i < parms.getNumberOfSensors(); i++)
				{
					readFromI2C(i);
					System.out.println("Sensor " + i + " - moistureLevel " + String.format("%4.2f", moistureValue));
					try
					{
						appData.setMoisture(i, moistureValue, true);
					}
					catch (IOException e) 
					{
						System.out.println("Exception in setMoisture: " + e.getMessage());
						// TODO handle error
					}
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
