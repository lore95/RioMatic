package it.unibz.mngeng.java.Commons;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class Parameters 
{
	private int NUMBER_OF_SENSORS;
	private int FIELD_ID;
	private String PERSIST_FILE_PATH;
	private int ARCHIVE_PERIOD;
	private int[] SENSORS_ID;
	private int[] LED_ERROR_GPIO = new int[3];
	private int[][] SENSORS_RANGE;
	private int ADC_ADDRESS = 0x40;
	private int PUMP_GPIO;
	private String DBHOST;
	
	public Parameters(String filePath) throws InvalidFileFormatException, IOException
	{
		Ini ini = new Ini(new File(filePath));
		NUMBER_OF_SENSORS = Integer.parseInt(ini.get("sensors", "NUMBER_OF_SENSORS"));
		FIELD_ID = Integer.parseInt(ini.get("generic", "FIELD_ID"));
		PERSIST_FILE_PATH = ini.get("persistance", "PERSIST_FILE_PATH");
		ARCHIVE_PERIOD = Integer.parseInt(ini.get("persistance", "ARCHIVE_PERIOD"));
		SENSORS_RANGE = new int [NUMBER_OF_SENSORS][2];
		SENSORS_ID = new int [NUMBER_OF_SENSORS];
		ADC_ADDRESS = Integer.decode(ini.get("sensors", "ADC_ADDRESS"));
		DBHOST = ini.get("persistance", "DBHOST");
		PUMP_GPIO = Integer.decode(ini.get("sensors", "PUMP_GPIO"));
		for (int i = 0; i < NUMBER_OF_SENSORS; i++)
		{
			String entryName = "SENSOR_RANGE_" + i;
			String range = ini.get("sensors", entryName);
			StringTokenizer st = new StringTokenizer(range);
			SENSORS_RANGE[i][0] = Integer.parseInt(st.nextToken());
			SENSORS_RANGE[i][1] = Integer.parseInt(st.nextToken());
			entryName = "SENSOR_ID_" + i;
			SENSORS_ID[i] = Integer.parseInt(ini.get("sensors", entryName));
		}
		for(int i = 0; i < LED_ERROR_GPIO.length; i++)
		{
			String entryName = "LED_ERROR_GPIO_" + i;
			LED_ERROR_GPIO[i] = Integer.parseInt(ini.get("generic", entryName));
		}
	}

	public int getNumberOfSensors()
	{
		return NUMBER_OF_SENSORS;
	}
	
	public int getFieldId()
	{
		return FIELD_ID;
	}
	
	public String getPersistFilePath()
	{
		return PERSIST_FILE_PATH;
	}
	
	public void setFilePath(String path)
	{
		PERSIST_FILE_PATH = path;
	}
	
	public int getArchivePeriod()
	{
		return ARCHIVE_PERIOD;
	}
	
	public int[] getSensorRange(int instance)
	{
		return SENSORS_RANGE[instance];
	}
	
	public int getADCAddress()
	{
		return ADC_ADDRESS;
	}

	public String getDBHost()
	{
		return DBHOST;
	}
	
	public int getPumpGPIOPin()
	{
		return PUMP_GPIO;
	}
	
	public int getSensorId(int instance)
	{
		return SENSORS_ID[instance];
	}
	
	public int getLedGPIOPin(int instance)
	{
		return LED_ERROR_GPIO[instance];
	}
}
