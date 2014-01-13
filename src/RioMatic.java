import java.io.IOException;
import java.util.Properties;

import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.Exceptions.RMException;
import it.unibz.mngeng.java.Handlers.DataStructures;
import it.unibz.mngeng.java.Raspberry.SensorDataHandler;
import it.unibz.mngeng.java.Raspberry.ValveHandler;

public class RioMatic 
{
	/**
	 * @param args
	 * @throws IOException 
	 * @throws RMException 
	 */
	public static void main(String[] args) throws IOException, RMException
	{
		boolean shutDown = false;
		// Leggere i Parametri con cui gira questa istanza dell'applicazione
		Parameters parms = new Parameters(args[0]);
		Properties p = new Properties(System.getProperties());
		p.setProperty("DBHOST", parms.getDBHost());
		
		// Creare la classe DataStructure
		DataStructures appData = new DataStructures(parms);
		
		// Spawnare thread seriale
		SensorDataHandler sh = new SensorDataHandler(appData, parms, shutDown);
		sh.start();
		
		// Spawnare thread attivazione valvole
		
		// Spawnare threads di irrigazione
		ValveHandler[] vh = new ValveHandler[parms.getNumberOfSensors()];
		for(int i = 0; i < parms.getNumberOfSensors(); i++)
		{
			vh[i] = new ValveHandler(appData, i, shutDown, parms);
			vh[i].start();
		}
		
		// Spawnare thread di archiviazione
		
		while(true)
		{
			if (!sh.isAlive())
			{
				// Set error condition - try to respone
			}
			else
			{
				// clean error condition
			}
			
			for(int i = 0; i < parms.getNumberOfSensors(); i++)
			{
				if (!vh[i].isAlive())
				{
					// Set error condition
				}
				else
				{
					// clean error condition
				}
			}
			try 
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e) 
			{
				System.out.println("Exception " + e.getMessage() + " in main - Thread.sleep");
			}
		}
	}

}
