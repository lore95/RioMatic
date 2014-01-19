package it.unibz.mngeng.java.Raspberry;

import java.io.IOException;

import org.apache.log4j.Logger;

import it.unibz.mngeng.java.Commons.Errors;
import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.Handlers.DataStructures;

public class SensorDataHandler extends Thread 
{
	private DataStructures appData;
	private boolean shutDown;
	private byte buffer = 0;
	private double moistureValue = 0.0;
	private Parameters parms;
	
	static Logger logger = Logger.getLogger(SensorDataHandler.class);
	
	public SensorDataHandler(DataStructures appData, Parameters parms, boolean shutDown) throws IOException
	{
		this.appData = appData;
		this.shutDown = shutDown;
		this.parms = parms;
	}

	protected boolean readFromI2C(int sensorId)
	{
		short unsignedValue = 0;
		try 
		{
			appData.getDevice().write((byte) (0x40 | (sensorId & 3)));
			appData.getDevice().read();
			buffer = (byte) appData.getDevice().read();
			unsignedValue = (short) ((short) 0x00FF & buffer);
		}
		catch (IOException e) 
		{
			appData.setErrorCode(appData.getErrorCode() | Errors.READ_SENSOR_ERROR);
			logger.error("IOEXception " + e.getMessage() + " reading sensor " + sensorId);
			return false;
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
							shutDown = true;
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
	}
}
