package it.unibz.mngeng.java.Raspberry;

import java.io.IOException;
import java.util.EnumSet;

import org.apache.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.impl.PinImpl;

import it.unibz.mngeng.java.Commons.Errors;
import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.DBUtility.Areas;
import it.unibz.mngeng.java.Exceptions.RMException;
import it.unibz.mngeng.java.Handlers.DataStructures;

public class ValveHandler extends Thread
{
	final GpioController gpio = GpioFactory.getInstance();
	private GpioPinDigitalOutput pin;
	private Pin pinDescr;

	private DataStructures appData;
	private Parameters parms;
	private boolean shutDown;
	private Areas areaData;
	private String pinName;
	private int secondsElapsed= 0;
	private int instance;

	static Logger logger = Logger.getLogger(ValveHandler.class);
	
	public ValveHandler(DataStructures appData, int instance, Parameters parms, boolean shutDown) throws RMException
	{
		this.appData = appData;
		this.shutDown = shutDown;
		this.instance = instance;
		this.parms = parms;
		
		logger.debug("Getting area data for Aread id " + parms.getSensorId(instance));
		try 
		{
			this.areaData = new Areas(parms.getSensorId(parms.getSensorId(instance)));
		}
		catch (RMException e) 
		{
			appData.setErrorCode(appData.getErrorCode() | Errors.DB_CONNECTION_ERROR);
			throw(e);
		}
		logger.debug("Creating pin " + areaData.getGPIOId());
		pinName = "Area_" + this.parms.getFieldId() + "." + areaData.getGPIOId();
		pinDescr = new PinImpl(RaspiGpioProvider.NAME, areaData.getGPIOId(), pinName, 
				                   EnumSet.of(PinMode.DIGITAL_OUTPUT),
				                   PinPullResistance.all()); 
		pin = gpio.provisionDigitalOutputPin(pinDescr, pinName, PinState.HIGH);
	}

	public ValveHandler(DataStructures appData, int instance, Parameters parms, Areas areaData, boolean shutDown) 
	{
		this.appData = appData;
		this.shutDown = shutDown;
		this.instance = instance;
		this.parms = parms;
		
		this.areaData = areaData;
		
		pinName = "Area_" + this.parms.getFieldId() + "." + areaData.getGPIOId();
		pinDescr = new PinImpl(RaspiGpioProvider.NAME, areaData.getGPIOId(), pinName, 
				                   EnumSet.of(PinMode.DIGITAL_OUTPUT),
				                   PinPullResistance.all()); 
		pin = gpio.provisionDigitalOutputPin(pinDescr, pinName, PinState.HIGH);
	}

	@Override
	public void run() 
	{
		logger.debug("Valve Handler " + instance + " started");
		while(!shutDown)
		{
			if ((appData.getMoisture(instance) < areaData.getMoistureMin()) &&
				!appData.getValveStatus(instance))
			{
				logger.debug("Moisture for area " + instance + " = " + appData.getMoisture(instance) + 
							 " lower than required value (" + areaData.getMoistureMin() + ")");
				try
				{
					logger.debug("Resetting watering time to 0"); 
					appData.setWateringTimeElapsed(0, instance, true);
					logger.debug("Set valve status to true in order to open it"); 
					appData.setValveStatus(instance, true);
					logger.debug("Set time elapsed to 0"); 
					secondsElapsed = 0;
				}
				catch (IOException e) 
				{
					appData.setErrorCode(appData.getErrorCode() | Errors.DATA_FILE_WRITE_ERROR);
					logger.fatal("ValveHandler instance " + instance + ": got IOException " + e.getMessage());
					System.exit(-1);
				}
			}

			if (appData.getValveStatus(instance))
			{
				logger.debug("Valve " + pin.getName() + " required to open"); 
				pin.low();
				secondsElapsed++;
				if (secondsElapsed % 10 == 0)
				{
					logger.debug("logging watering time on persistence data file");
					try
					{
						appData.setWateringTimeElapsed(instance, secondsElapsed, true);
						if (secondsElapsed >= areaData.getWateringTime())
						{
							logger.debug("Watering time is over. Reset data on persistence file");
							appData.setWateringTimeElapsed(instance, 0, true);
							logger.debug("Set the valve status to off to close it");
							appData.setValveStatus(instance, false);
							secondsElapsed = 0;
						}
					}
					catch (IOException e) 
					{
						appData.setErrorCode(appData.getErrorCode() | Errors.DATA_FILE_WRITE_ERROR);
						logger.fatal("ValveHandler instance " + instance + ": got IOException " + e.getMessage());
						System.exit(-1);
					}
				}
			}
			else
			{
				pin.high();
			}
			
			try 
			{
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) 
			{
				;
			}
		}
	}
}
