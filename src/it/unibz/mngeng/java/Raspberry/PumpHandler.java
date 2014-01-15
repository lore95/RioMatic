package it.unibz.mngeng.java.Raspberry;

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

import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.Handlers.DataStructures;

public class PumpHandler extends Thread
{
	final GpioController gpio = GpioFactory.getInstance();
	private GpioPinDigitalOutput pin;
	private Pin pinDescr;

	private DataStructures appData;
	private Parameters parms;
	private boolean shutDown;
	private String pinName;
	
	static Logger logger = Logger.getLogger(PumpHandler.class);

	public PumpHandler(DataStructures appData, Parameters parms, boolean shutDown)
	{
		this.appData = appData;
		this.shutDown = shutDown;
		this.parms = parms;
		
		logger.debug("Setting pin for pump handler. GPIO " + parms.getPumpGPIOPin());
		pinName = "Pump";
		pinDescr = new PinImpl(RaspiGpioProvider.NAME, parms.getPumpGPIOPin(), pinName, 
					               EnumSet.of(PinMode.DIGITAL_OUTPUT),
					               PinPullResistance.all()); 

		pin = gpio.provisionDigitalOutputPin(pinDescr, pinName, PinState.HIGH);
	}

	@Override
	public void run() 
	{
		logger.debug("Pump Handler thread started");
		while(!shutDown)
		{
			int i = 0;
			for(i = 0; i < parms.getNumberOfSensors(); i++)
			{
				if (appData.getValveStatus(i))
				{
					logger.debug("Valve " + i + " is open.");
					break;
				}
			}
			if (i >= parms.getNumberOfSensors())
			{
				logger.debug("No valve opened. Stopping the pump");
				pin.high();
			}
			else
			{
				logger.debug("At least one valve is opene. Starting the pump");
				pin.low();
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
