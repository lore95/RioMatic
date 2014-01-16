package it.unibz.mngeng.java.Raspberry;

import java.io.IOException;
import java.util.Random;

import org.apache.log4j.Logger;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import it.unibz.mngeng.java.Commons.Errors;
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
	
	static Logger logger = Logger.getLogger(SensorDataHandler.class);
	
	public SensorDataHandler(DataStructures appData, Parameters parms, boolean shutDown) throws IOException
	{
		this.appData = appData;
		this.shutDown = shutDown;
		this.parms = parms;
		logger.debug("create I2C communications bus instance on bus " + parms.getADCBus());
		bus = I2CFactory.getInstance(parms.getADCBus());
		
		logger.debug("create I2C device instance at address " + parms.getADCAddress());
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

	protected boolean readFromI2C(int sensorId)
	{
		short unsignedValue = 0;
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
				unsignedValue = (short) ((short) 0x00FF & buffer);
			}
			catch (IOException e) 
			{
				appData.setErrorCode(appData.getErrorCode() | Errors.READ_SENSOR_ERROR);
				logger.error("IOEXception " + e.getMessage() + " reading sensor " + sensorId);
				return false;
			}
		}
		moistureValue = 100 * (unsignedValue - parms.getSensorRange(sensorId)[0]) / 
							  (parms.getSensorRange(sensorId)[1] - parms.getSensorRange(sensorId)[0]);
		return true;
	}
	
	protected double getMoisture()
	{
		return moistureValue;
	}
	
	@Override
	public void run() 
	{
		logger.debug("Sensor Handler thread started");
		try 
		{
			while(!shutDown)
			{
				for(int i = 0; i < parms.getNumberOfSensors(); i++)
				{
					if (readFromI2C(i))
					{
						logger.trace("Sensor " + i + " - moistureLevel " + String.format("%4.2f", moistureValue));
						try
						{
							appData.setMoisture(i, moistureValue, true);
						}
						catch (IOException e) 
						{
							appData.setErrorCode(appData.getErrorCode() | Errors.DATA_FILE_WRITE_ERROR);
							logger.fatal("Exception in setMoisture: " + e.getMessage());
							System.exit(-1);
						}
					}
				}
				Thread.sleep(1000);
			}
		}
		catch (InterruptedException e) 
		{
			;
		}
		System.exit(0);
	}
}
