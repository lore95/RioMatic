package it.unibz.mngeng.java.DBUtilty;

import it.unibz.mngeng.java.Exceptions.RMException;

public class History extends DBInterface
{
	private static final long serialVersionUID = -7844508274722236575L;
	
	protected int id;
	protected String name;
	protected String location ;
	
	public History() throws RMException
	{
		tableName = "History";
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}
