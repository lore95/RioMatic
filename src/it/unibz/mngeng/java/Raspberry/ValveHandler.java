package it.unibz.mngeng.java.Raspberry;

import java.io.IOException;
import java.util.EnumSet;

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

	public ValveHandler(DataStructures appData, int instance, boolean shutDown, Parameters parms) throws RMException
	{
		this.appData = appData;
		this.shutDown = shutDown;
		this.instance = instance;
		this.parms = parms;
		
		try 
		{
			this.areaData = new Areas(parms.getSensorId(instance));
		}
		catch (RMException e) 
		{
			appData.setErrorCode(appData.getErrorCode() | Errors.DB_CONNECTION_ERROR);
			throw(e);
		}
		pinName = "Area_" + this.parms.getFieldId() + "." + areaData.getGPIOId();
		pinDescr = new PinImpl(RaspiGpioProvider.NAME, areaData.getGPIOId(), pinName, 
				                   EnumSet.of(PinMode.DIGITAL_OUTPUT),
				                   PinPullResistance.all()); 
		pin = gpio.provisionDigitalOutputPin(pinDescr, pinName, PinState.HIGH);
	}

	public ValveHandler(DataStructures appData, int instance, boolean shutDown, Parameters parms, Areas areaData) 
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
		while(!shutDown)
		{
			if ((appData.getMoisture(instance) < areaData.getMoistureMin()) &&
				!appData.getValveStatus(instance))
			{
				try
				{
					appData.setWateringTimeElapsed(0, instance, true);
					appData.setValveStatus(instance, true);
					secondsElapsed = 0;
				}
				catch (IOException e) 
				{
					appData.setErrorCode(appData.getErrorCode() | Errors.DATA_FILE_WRITE_ERROR);
					System.out.println("ValveHandler instance " + instance + ": got IOException " + e.getMessage());
					System.exit(-1);
				}
			}

			if (appData.getValveStatus(instance))
			{
				pin.low();
				secondsElapsed++;
				if (secondsElapsed % 60 == 0)
				{
					try
					{
						int i = appData.getWateringTimeElapsed(instance);
						appData.setWateringTimeElapsed(++i, instance, true);
						if (i >= areaData.getWateringTime())
						{
							appData.setWateringTimeElapsed(instance, 0, true);
							appData.setValveStatus(instance, false);
							secondsElapsed = 0;
						}
					}
					catch (IOException e) 
					{
						appData.setErrorCode(appData.getErrorCode() | Errors.DATA_FILE_WRITE_ERROR);
						System.out.println("ValveHandler instance " + instance + ": got IOException " + e.getMessage());
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
