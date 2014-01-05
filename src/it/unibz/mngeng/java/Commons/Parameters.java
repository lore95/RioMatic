package it.unibz.mngeng.java.Commons;

public class Parameters 
{
	private int NUMBER_OF_SENSORS = 8;
	private int FIELD_ID = 1;
	private String PERSIST_FILE_PATH = "/var/appData/RioMatic/runData.txt";
	
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
}
