package it.unibz.mngeng.java.Raspberry;

import it.unibz.mngeng.java.Handlers.DataStructures;

public class ValveHandler 
{
	private DataStructures appData;
	private boolean shutDown;
	private int instance;

	public ValveHandler(DataStructures appData, int instance, boolean shutDown)
	{
		this.appData = appData;
		this.shutDown = shutDown;
		this.instance = instance;
	}

}
