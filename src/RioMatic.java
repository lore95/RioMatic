import java.io.IOException;

import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.Exceptions.RMException;
import it.unibz.mngeng.java.Handlers.DataStructures;
import it.unibz.mngeng.java.Raspberry.SerialHandler;
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
		Parameters parms = new Parameters();
		
		// Creare la classe DataStructure
		DataStructures appData = new DataStructures(parms);
		
		// Spawnare thread seriale
		SerialHandler sh = new SerialHandler(appData, shutDown);
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
	}

}
