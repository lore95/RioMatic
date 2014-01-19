package it.unibz.mngeng.java.Raspberry;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import it.unibz.mngeng.java.Commons.Errors;
import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.DBUtility.Areas;
import it.unibz.mngeng.java.DBUtility.History;
import it.unibz.mngeng.java.Exceptions.RMException;
import it.unibz.mngeng.java.Handlers.DataStructures;

public class ArchiveData extends Thread
{
	private DataStructures appData;
	private boolean shutDown;
	private ArrayList<Areas> areas;
	private Parameters parms;
	static Logger logger = Logger.getLogger(ArchiveData.class);


	@SuppressWarnings("unchecked")
	public ArchiveData(DataStructures appData, Parameters parms, boolean shutDown) throws RMException
	{
		this.appData = appData;
		this.shutDown = shutDown;
		this.parms = parms;
		areas = (ArrayList<Areas>) Areas.populateCollection("SELECT * FROM Areas " +
															"WHERE fieldId = " + parms.getFieldId() + " " +
															"ORDER BY id", Areas.class);
	}
	
	@Override
	public void run() 
	{
		logger.debug("THread started. Loggin every " + parms.getArchivePeriod() + " seconds");
		while(!shutDown)
		{
			for(int i = 0; i < parms.getNumberOfSensors(); i++)
			{
				try
				{
					History hist = new History();
					hist.setAreaId(areas.get(i).getId());
					hist.setMositure(appData.getMoisture(i));
					hist.setTimestamp(new Date());
					hist.insert("id", hist);
				}
				catch (RMException e) 
				{
					appData.setErrorCode(appData.getErrorCode() | Errors.DB_CONNECTION_ERROR);
					logger.debug("Got error on insert " + e.getErrorCode() + " - " + e.getErrorDescription());
					shutDown = true;
				}
			}
			try 
			{
				Thread.sleep(1000 * parms.getArchivePeriod());
			}
			catch (InterruptedException e)
			{
				;
			}
		}
	}
}
