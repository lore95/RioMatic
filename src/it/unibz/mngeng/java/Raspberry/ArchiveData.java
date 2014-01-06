package it.unibz.mngeng.java.Raspberry;

import java.util.ArrayList;
import java.util.Date;

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

	@SuppressWarnings("unchecked")
	public ArchiveData(DataStructures appData, boolean shutDown, Parameters parms) throws RMException
	{
		this.appData = appData;
		this.shutDown = shutDown;
		this.parms = parms;
		areas = (ArrayList<Areas>) Areas.populateCollection("SELECT * FROM Areas " +
															"WHERE fieldId = " + parms.getFieldId() + " " +
															"ORDER BY sensorId", Areas.class);
	}
	
	@Override
	public void run() 
	{
		while(!shutDown)
		{
			for(int i = 0; i < appData.getMoisture().length; i++)
			{
				try
				{
					int id = 0;
					History hist = new History();
					hist.setAreaId(areas.get(i).getId());
					hist.setMositure(appData.getMoisture(i));
					hist.setTimestamp(new Date());
					hist.insert("id", hist, id);
				}
				catch (RMException e) 
				{
					// TODO Auto-generated catch block
				}
			}
			try 
			{
				Thread.sleep(parms.getArchivePeriod());
			}
			catch (InterruptedException e)
			{
			}
		}
	}
}
