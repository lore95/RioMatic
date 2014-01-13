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
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.impl.PinImpl;

import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.DBUtility.Areas;
import it.unibz.mngeng.java.Exceptions.RMException;
import it.unibz.mngeng.java.Handlers.DataStructures;

public class PumpHandler extends Thread
{
	final GpioController gpio = GpioFactory.getInstance();
	private GpioPinDigitalOutput pin;

	private DataStructures appData;
	private Parameters parms;
	private boolean shutDown;
	private String pinName;

	public PumpHandler(DataStructures appData, boolean shutDown, Parameters parms) throws RMException
	{
		this.appData = appData;
		this.shutDown = shutDown;
		this.parms = parms;
		
		pinName = "Pump";
		Pin pinToUse = RaspiPin.GPIO_01;
		pin = gpio.provisionDigitalOutputPin(pinToUse, pinName, PinState.HIGH);
	}

	@Override
	public void run() 
	{
		while(!shutDown)
		{
			int i = 0;
			for(i = 0; i < parms.getNumberOfSensors(); i++)
			{
				if (appData.getValveStatus(i))
				{
					pin.low();
					break;
				}
			}
			if (i >= parms.getNumberOfSensors())
			{
				pin.high();
			}
			
			try 
			{
				Thread.sleep(200);
			} 
			catch (InterruptedException e) 
			{
				;
			}
		}
	}
}
