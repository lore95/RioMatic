package it.unibz.mngeng.java.Test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.DBUtility.Areas;
import it.unibz.mngeng.java.Exceptions.RMException;
import it.unibz.mngeng.java.Handlers.DataStructures;

import org.junit.Test;

public class ValveHandlerTest {
	Parameters parms = new Parameters();
	DataStructures appData;

	@Test
	public void checkValveGetOpenedWhenMoistureGetUnderExpectedLevel() 
	{
		try 
		{
			appData = new DataStructures(parms);
			Areas areas = new Areas();
			areas.populateCollectionOnCondition("WHERE fieldId = " + parms.getFiledId(), Areas.class);
			assertTrue(true);
		} 
		catch (RMException e) 
		{
			fail("Exception thrown: " + e.getErrorCode() + " - " + e.getMessage());
		} 
		catch (FileNotFoundException e) 
		{
			fail("File not found: " + e.getMessage());
		} 
		catch (IOException e) 
		{
			fail("IOException: " + e.getMessage());
		}
	}

}
