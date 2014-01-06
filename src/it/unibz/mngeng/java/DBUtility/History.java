package it.unibz.mngeng.java.DBUtility;

import java.util.Date;

import it.unibz.mngeng.java.Exceptions.RMException;

public class History extends DBInterface
{
	private static final long serialVersionUID = -7844508274722236575L;
	
	protected int areaId;
	protected double mositure;
	protected Date timestamp;
	
	public History() throws RMException
	{
		tableName = "History";
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public double getMositure() {
		return mositure;
	}

	public void setMositure(double mositure) {
		this.mositure = mositure;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
