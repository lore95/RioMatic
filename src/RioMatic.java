import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import it.unibz.mngeng.java.Commons.Errors;
import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.Exceptions.RMException;
import it.unibz.mngeng.java.Handlers.DataStructures;
import it.unibz.mngeng.java.Raspberry.ErrorsHandler;
import it.unibz.mngeng.java.Raspberry.PumpHandler;
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
		
		// Error Handler spawn
		appData.setErrorCode(0);
		ErrorsHandler eh = new ErrorsHandler(appData, parms, shutDown);
		eh.start();
		
		// ADC Handler spawn
		SensorDataHandler sh = new SensorDataHandler(appData, parms, shutDown);
		sh.start();
		
		// Valve handler spawn
		ValveHandler[] vh = new ValveHandler[parms.getNumberOfSensors()];
		for(int i = 0; i < parms.getNumberOfSensors(); i++)
		{
			vh[i] = new ValveHandler(appData, parms.getSensorId(i), shutDown, parms);
			vh[i].start();
		}
		
		// Valve handler spawn
		PumpHandler ph = new PumpHandler(appData, shutDown, parms);
		ph.start();
		
		// Data archive spawn
		
		System.out.print("RioMatic> ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		while(!shutDown)
		{
			if (input.toLowerCase().compareTo("reset") == 0)
			{
				appData.setErrorCode(0);
			}

			if (!sh.isAlive())
			{
				if (input.toLowerCase().compareTo("reset") == 0)
				{
					sh = new SensorDataHandler(appData, parms, shutDown);
					sh.start();
				}
				else
				{
					appData.setErrorCode(appData.getErrorCode() | Errors.SENSOR_HANDLE_ERROR);
				}
			}
			
			if (!ph.isAlive())
			{
				if (input.toLowerCase().compareTo("reset") == 0)
				{
					ph = new PumpHandler(appData, shutDown, parms);
					ph.start();
				}
				else
				{
					appData.setErrorCode(appData.getErrorCode() | Errors.PUMP_HANDLE_ERRO);
				}
			}

			for(int i = 0; i < parms.getNumberOfSensors(); i++)
			{
				if (!vh[i].isAlive())
				{
					if (input.toLowerCase().compareTo("reset") == 0)
					{
						vh[i] = new ValveHandler(appData, parms.getSensorId(i), shutDown, parms);
						vh[i].start();
					}
					else
					{
						appData.setErrorCode(appData.getErrorCode() | Errors.VALVE_HANDLE_ERROR);
					}
				}
			}
			
			try 
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e) 
			{
				System.out.println("Exception " + e.getMessage() + " in main - Thread.sleep");
			}
			if(br.ready()) 
			{
				input = br.readLine();
				if (input.toLowerCase().compareTo("shutdown") == 0)
				{
					shutDown = true;
				}
				System.out.print("RioMatic> ");
			}
		}
	}

}
