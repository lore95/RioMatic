import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import it.unibz.mngeng.java.Commons.Errors;
import it.unibz.mngeng.java.Commons.Parameters;
import it.unibz.mngeng.java.Exceptions.RMException;
import it.unibz.mngeng.java.Handlers.DataStructures;
import it.unibz.mngeng.java.Raspberry.ArchiveData;
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
	public static void main(String[] args) 
	{
		final Logger logger = Logger.getLogger(RioMatic.class);
		boolean shutDown = false;

	    // Inizializza la libreria di log
		PropertyConfigurator.configure(args[1]);

	    // Leggere i Parametri con cui gira questa istanza dell'applicazione
		Parameters parms = null;
		try 
		{
			parms = new Parameters(args[0]);
		}
		catch (IOException e) 
		{
			logger.fatal("Exception " + e.getMessage() + " getting parameters (" + args[0] + ")");
			System.exit(-1);
		}
		Properties p = new Properties(System.getProperties());
		p.setProperty("DBHOST", parms.getDBHost());
		logger.debug("Got parms from configuration file. DBHOST is " + parms.getDBHost());
		
		// Creare la classe DataStructure
		DataStructures appData = null;
		try 
		{
			appData = new DataStructures(parms);
		}
		catch (IOException e) 
		{
			logger.fatal("Exception " + e.getMessage() + " creating data structures");
			System.exit(-1);
		}
		logger.debug("Data structures created");
		
		// Error Handler spawn
		appData.setErrorCode(0);
		ErrorsHandler eh = null;
		try 
		{
			logger.debug("Starting Error Handler thread");
			eh = new ErrorsHandler(appData, parms, shutDown);
			eh.start();
		}
		catch(IOException e)
		{
			logger.error("Exception " + e.getMessage() + " creating ErrorHandler thread");
		}
		
		try 
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e) 
		{
			;
		}		
		
		// ADC Handler spawn
		SensorDataHandler sh = null;
		try 
		{
			logger.debug("Starting Sensor Handler thread");
			sh = new SensorDataHandler(appData, parms, shutDown);
			sh.start();
		}
		catch (IOException e)
		{
			logger.error("Exception " + e.getMessage() + " creating SensorHandler thread");
		}
		
		try 
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e) 
		{
			;
		}		
		
		// Valve handler spawn
		ValveHandler[] vh = new ValveHandler[parms.getNumberOfSensors()];
		for(int i = 0; i < parms.getNumberOfSensors(); i++)
		{
			try 
			{
				logger.debug("Starting Valve " + i + " Handler thread");
				vh[i] = new ValveHandler(appData, i, parms, shutDown);
				vh[i].start();
				try 
				{
					Thread.sleep(250);
				}
				catch (InterruptedException e) 
				{
					;
				}		
			}
			catch (RMException e) 
			{
				logger.error("Exception " + e.getMessage() + " creating Valve Handler " + i + " thread");
			}
		}
		
		try 
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e) 
		{
			;
		}		

		// Valve handler spawn
		logger.debug("Starting Pump Handler thread");
		PumpHandler ph = new PumpHandler(appData, parms, shutDown);
		ph.start();
		
		try 
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e) 
		{
			;
		}		

		// Data archive spawn
		ArchiveData ad = null;
		try 
		{
			logger.debug("Starting Archive Data thread");
			ad = new ArchiveData(appData, parms, shutDown);
			ad.start();
		}
		catch (RMException e) 
		{
			logger.error("Exception " + e.getMessage() + " creating ArchiveData thread");
		}
		
		System.out.print("RioMatic> ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		while(!shutDown)
		{
			if (input.toLowerCase().compareTo("reset") == 0)
			{
				logger.debug("Reset command pressed. Setting error code to 0");
				appData.setErrorCode(0);
			}

			if ((eh == null) || !eh.isAlive())
			{
				logger.debug("Errors Handler thread is dead");
				if (input.toLowerCase().compareTo("reset") == 0)
				{
					logger.debug("Restarting Errors Handler thread on RESET");
					eh = null;
					try 
					{
						eh = new ErrorsHandler(appData, parms, shutDown);
						eh.start();
					}
					catch(IOException e)
					{
						logger.error("Exception " + e.getMessage() + " creating ErrorHandler thread");
					}
				}
			}

			if ((ad == null) || !ad.isAlive())
			{
				logger.debug("Archive Data thread is dead");
				if (input.toLowerCase().compareTo("reset") == 0)
				{
					logger.debug("Restarting Archive Datqa thread on RESET");
					ad = null;
					try 
					{
						ad = new ArchiveData(appData, parms, shutDown);
						ad.start();
					}
					catch (RMException e) 
					{
						logger.error("Exception " + e.getMessage() + " creating ArchiveData thread");
					}
				}
				else
				{
					if ((appData.getErrorCode() & Errors.ARCHIVE_HANDLE_ERROR) == 0)
					{
						logger.debug("Setting error for LED ERROR report");
						appData.setErrorCode(appData.getErrorCode() | Errors.ARCHIVE_HANDLE_ERROR);
					}
				}
			}

			if ((sh == null) || !sh.isAlive())
			{
				logger.debug("Sensor Handler thread is dead");
				if (input.toLowerCase().compareTo("reset") == 0)
				{
					logger.debug("Restarting Sensor Handler thread on RESET");
					sh = null;
					try 
					{
						sh = new SensorDataHandler(appData, parms, shutDown);
						sh.start();
					}
					catch (IOException e)
					{
						logger.error("Exception " + e.getMessage() + " creating SensorHandler thread");
					}
				}
				else
				{
					if ((appData.getErrorCode() & Errors.SENSOR_HANDLE_ERROR) == 0)
					{
						logger.debug("Setting error for LED ERROR report");
						appData.setErrorCode(appData.getErrorCode() | Errors.SENSOR_HANDLE_ERROR);
					}
				}
			}
			
			if ((ph == null) || !ph.isAlive())
			{
				logger.debug("Pump Handler thread is dead");
				if (input.toLowerCase().compareTo("reset") == 0)
				{
					logger.debug("Restarting Pump Handler thread on RESET");
					ph = new PumpHandler(appData, parms, shutDown);
					ph.start();
				}
				else
				{
					if ((appData.getErrorCode() & Errors.PUMP_HANDLE_ERROR) == 0)
					{
						logger.debug("Setting error for LED ERROR report");
						appData.setErrorCode(appData.getErrorCode() | Errors.PUMP_HANDLE_ERROR);
					}
				}
			}

			for(int i = 0; i < parms.getNumberOfSensors(); i++)
			{
				if ((vh[i] == null) || !vh[i].isAlive())
				{
					logger.debug("Valve Handler " + i + " thread is dead");
					if (input.toLowerCase().compareTo("reset") == 0)
					{
						logger.debug("Restarting Valve Handler " + i + " thread on RESET");
						vh[i] = null;
						try 
						{
							vh[i] = new ValveHandler(appData, i, parms, shutDown);
							logger.debug("Starting Valve " + i + " Handler thread");
							vh[i].start();
						}
						catch (RMException e) 
						{
							logger.error("Exception " + e.getMessage() + " creating Valve Handler " + i + " thread");
						}
					}
					else
					{
						if ((appData.getErrorCode() & Errors.VALVE_HANDLE_ERROR) == 0)
						{
							logger.debug("Setting error for LED ERROR report");
							appData.setErrorCode(appData.getErrorCode() | Errors.VALVE_HANDLE_ERROR);
						}
					}
				}
			}
			
			try 
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e) 
			{
				logger.debug("Exception " + e.getMessage() + " in main - Thread.sleep");
			}
			try 
			{
				if(br.ready()) 
				{
					input = br.readLine();
					logger.debug("Read '" + input + "' from command line");
					if (input.toLowerCase().compareTo("shutdown") == 0)
					{
						shutDown = true;
					}
					System.out.print("RioMatic> ");
				}
			}
			catch (IOException e) 
			{
				logger.fatal("RioMatic console could not be started. Shutting down");
				shutDown = true;
			}
		}
		logger.debug("Waiting for thread shutdown");
		boolean done = false;
		while(!done)
		{
			done = true;
			if (sh.isAlive())
				done = false;
			if (ph.isAlive())
				done = false;
			for(int i = 0; i < parms.getNumberOfSensors(); i++)
			{
				if (vh[i].isAlive())
					done = false;
			}
			if (eh.isAlive())
				done = false;
			if (ad.isAlive())
				done = false;
		}
		logger.debug("Threads ended. Exit program");
	}

}
