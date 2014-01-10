package it.unibz.mngeng.java.Commons;

public class Parameters 
{
	private int NUMBER_OF_SENSORS = 8;
	private int FIELD_ID = 1;
	private String PERSIST_FILE_PATH = "/var/appData/RioMatic/runData.txt";
	private int ARCHIVE_PERIOD = 30;
	private int[][] SENSORS_RANGE = {
			{0, 255},
			{36, 243}
	};
	private int ADC_ADDRESS = 0x40;
	
	
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
}
