package it.unibz.mngeng.java.Raspberry;

import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.Handlers.DataStructures;

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

public class ErrorsHandler extends Thread
{
	private GpioController gpio;
	private GpioPinDigitalOutput pinLed1;
	private GpioPinDigitalOutput pinLed2;
	private GpioPinDigitalOutput pinLed3;
	private Pin pinDescr;

	private DataStructures appData;
	private boolean shutDown;
	private String pinName;

	static Logger logger = Logger.getLogger(ErrorsHandler.class);

	public ErrorsHandler(DataStructures appData, Parameters parms, boolean shutDown) throws IOException
	{
		gpio = GpioFactory.getInstance();
		this.appData = appData;
		this.shutDown = shutDown;
		
		logger.debug("Setting pins for error out");
		
		logger.debug("LED 1 - pin " + parms.getLedGPIOPin(0));
		logger.debug("LED 2 - pin " + parms.getLedGPIOPin(1));
		logger.debug("LED 3 - pin " + parms.getLedGPIOPin(2));

		pinName = "LED_";
		pinDescr = new PinImpl(RaspiGpioProvider.NAME, parms.getLedGPIOPin(0), pinName + "0", 
					               EnumSet.of(PinMode.DIGITAL_OUTPUT),
					               PinPullResistance.all()); 
		pinLed1 = gpio.provisionDigitalOutputPin(pinDescr, pinName, PinState.LOW);
		pinDescr = new PinImpl(RaspiGpioProvider.NAME, parms.getLedGPIOPin(1), pinName + "1", 
					               EnumSet.of(PinMode.DIGITAL_OUTPUT),
					               PinPullResistance.all()); 
		pinLed2 = gpio.provisionDigitalOutputPin(pinDescr, pinName, PinState.LOW);
		pinDescr = new PinImpl(RaspiGpioProvider.NAME, parms.getLedGPIOPin(2), pinName + "2", 
					               EnumSet.of(PinMode.DIGITAL_OUTPUT),
					               PinPullResistance.all()); 
		pinLed3 = gpio.provisionDigitalOutputPin(pinDescr, pinName, PinState.LOW);
	}

	@Override
	public void run() 
	{
		logger.debug("Errors Handler thread started");
		byte[] countFlash = {-1, -1, -1};
		byte[] countOff = {-1, -1, -1};
		while(!shutDown)
		{
			if (appData.getErrorCode() == 0)
			{
				pinLed1.low();
				pinLed2.low();
				pinLed3.low();
				countFlash[0] = -1;
				countFlash[1] = -1;
				countFlash[2] = -1;
			}
			else
			{
				if (countFlash[0] <= 0)
				{
					countFlash[0] = (byte) (appData.getErrorCode() & 0x000000FF);
					countOff[0] = 4;
					pinLed1.low();
					logger.debug("Error 1 set to " + (appData.getErrorCode() & 0x000000FF));
				}
				else
				{
					if (countOff[0] > 0)
					{
						countOff[0]--;
					}
					else
					{
						countFlash[0]--;
						pinLed1.toggle();
						logger.trace("LED 1 is " + pinLed1.getState().getName());
					}
				}
				if (countFlash[1] <= 0)
				{
					countFlash[1] = (byte) ((appData.getErrorCode() & 0x0000FF00) >> 8);
					countOff[1] = 4;
					pinLed2.low();
					logger.debug("Error 2 set to " + ((appData.getErrorCode() & 0x0000FF00) >> 8));
				}
				else
				{
					if (countOff[1] > 0)
					{
						countOff[1]--;
					}
					else
					{
						countFlash[1]--;
						pinLed2.toggle();
						logger.trace("LED 2 is " + pinLed2.getState().getName());
					}
				}
				if (countFlash[2] <= 0)
				{
					countFlash[2] = (byte) ((appData.getErrorCode() & 0x00FF0000) >> 16);
					countOff[1] = 4;
					pinLed3.low();
					logger.debug("Error 3 set to " + ((appData.getErrorCode() & 0x00FF0000) >> 16));
				}
				else
				{
					if (countOff[2] > 0)
					{
						countOff[2]--;
						countOff[2] = 4;
					}
					else
					{
						countFlash[2]--;
						pinLed3.toggle();
						logger.trace("LED 3 is " + pinLed3.getState().getName());
					}
				}
				try 
				{
					Thread.sleep(250);
				} 
				catch (InterruptedException e) 
				{
					;
				}
			}
		}
		logger.debug("Shutting down");
	}
}
