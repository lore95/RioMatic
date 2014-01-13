package it.unibz.mngeng.java.Raspberry;

import static org.junit.Assert.*;

import java.io.IOException;

import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.Handlers.DataStructures;

import org.ini4j.InvalidFileFormatException;
import org.junit.Test;

public class SensorDataHandlerTest {

	@Test
	public void readDataFromSensorsWhenReallyConnectedToRPI() throws InvalidFileFormatException, IOException 
	{
		boolean shutdown = false;
		Parameters parms = new Parameters("Data/RioMatic.ini");
		DataStructures appData = new DataStructures(parms);
		SensorDataHandler sh = new SensorDataHandler(appData, parms, shutdown);
		int count = 0;
		while(count++ < 60)
		{
			sh.readFromI2C(0);
			System.out.println("Moisture sensor 1: " + sh.getMoisture());
			sh.readFromI2C(1);
			System.out.println("Moisture sensor 2: " + sh.getMoisture());
			try 
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
