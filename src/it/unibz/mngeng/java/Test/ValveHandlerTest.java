package it.unibz.mngeng.java.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.DBUtility.Areas;
import it.unibz.mngeng.java.Exceptions.RMException;
import it.unibz.mngeng.java.Handlers.DataStructures;
import it.unibz.mngeng.java.Raspberry.ValveHandler;

import org.junit.Test;

public class ValveHandlerTest {
	private RandomAccessFile raf;
	private Parameters parms = new Parameters();
	private DataStructures appData;
	private Areas areaData;
	
	private int[] wateringTime = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
	private double[] moisture = new double[] {0, 20.5, 0, 0, 0, 0, 0, 0};
	
	public ValveHandlerTest()
	{
		parms = new Parameters();
		parms.setFilePath("/var/appData/RioMatic/testData.txt");
		try 
		{
			// Reset the data test file to the expected values
			raf = new RandomAccessFile(new File(parms.getPersistFilePath()), "rwd");
			for(double d : moisture)
			{
				raf.write(String.format(DataStructures.DATA_ITEM_FORMAT, d).getBytes());
			}
			raf.write(("\n").getBytes());
			for(int i : wateringTime)
			{
				raf.write(String.format(DataStructures.DATA_ITEM_FORMAT, new Integer(i).doubleValue()).getBytes());
			}
			raf.close();
			
			raf = new RandomAccessFile(new File(parms.getPersistFilePath()), "r");
			appData = new DataStructures(parms);
			areaData = new Areas();
			areaData.setId(1);
			areaData.setMoistureMin(18);
			areaData.setWateringTime(1);
		} 
		catch (FileNotFoundException e) 
		{
			fail("File not found");
		} 
		catch (IOException e) 
		{
			fail("IOException");
		} 
		catch (RMException e) 
		{
			fail("RMException");
		}		
	}

	@Test
	public void checkValveGetOpenedWhenMoistureGetUnderExpectedLevel() 
	{
		boolean shutDown = false;
		try 
		{
			ValveHandler vh = new ValveHandler(appData, 1, shutDown, parms, areaData);
			vh.run();
			Thread.sleep(3000);
			appData.setMoisture(1, 20, true);
			assertFalse(appData.getValveStatus(1));
			Thread.sleep(1500);
			assertFalse(appData.getValveStatus(1));
			appData.setMoisture(1, 19, true);
			Thread.sleep(1500);
			assertFalse(appData.getValveStatus(1));
			appData.setMoisture(1, 18, true);
			Thread.sleep(1500);
			assertFalse(appData.getValveStatus(1));
			appData.setMoisture(1, 17, true);
			Thread.sleep(1000);
			assertTrue(appData.getValveStatus(1));
			appData.setMoisture(1, 20, true);
			Thread.sleep(50000);
			assertTrue(appData.getValveStatus(1));
			Thread.sleep(11000);
			assertFalse(appData.getValveStatus(1));
			assertEquals(0, appData.getWateringTimeElapsed(1));
		}
		catch (RMException e) 
		{
			fail("RMException");
		}
		catch (InterruptedException e) 
		{
			fail("Timer exception");
		} 
		catch (IOException e) 
		{
			fail("IOException");
		}
		assertTrue(true);
	}

}
