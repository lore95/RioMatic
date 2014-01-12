package it.unibz.mngeng.java.Handlers;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.Commons.Utility;

import org.ini4j.InvalidFileFormatException;
import org.junit.Test;

public class DataStructureTest {
	private RandomAccessFile raf;
	private DataStructures data;
	private Parameters parms;
	private int[] wateringTime = new int[] {10, 20, 30, 40, 50, 60, 70, 80};
	private double[] moisture = new double[] {1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7, 8.8};
	
	public DataStructureTest()
	{
		try 
		{
			parms = new Parameters(" ");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			data = new DataStructures(parms);
		} 
		catch (FileNotFoundException e) 
		{
			fail("File not found");
		} 
		catch (IOException e) 
		{
			fail("IOException");
		}		
	}
	
	protected void finalize()
	{
		data = null;
		try 
		{
			raf.close();
		}
		catch (IOException e) 
		{
			;
		}
	}

	@Test
	public void properInitValueOnDataStructureCreation()
	{
		assertArrayEquals(moisture, data.getMoisture(), 0);
		assertArrayEquals(wateringTime, data.getWateringTimeElapsed());
	}

	@Test
	public void testDataStructurePersistenceMethod()
	{
		try
		{
			// Writing a double in the 3rd slot and check its value
			double exp = 12.34;
			data.persistData(DataStructures.TYPE_MOISTURE, 2, exp);
			raf.seek(2 * DataStructures.ITEM_LEN);
			byte[] buffer = new byte[DataStructures.ITEM_LEN];
			raf.read(buffer, 0, DataStructures.ITEM_LEN);
			double actual = Double.valueOf(Utility.byteToString(buffer)).doubleValue();
			assertEquals(exp, actual, 0);
		}
		catch(Exception e)
		{
			fail("Exception thrown");
		}
	}
}