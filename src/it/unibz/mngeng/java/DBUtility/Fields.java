package it.unibz.mngeng.java.DBUtility;

import it.unibz.mngeng.java.Exceptions.RMException;

public class Fields extends DBInterface
{
	private static final long serialVersionUID = 6914484723211078634L;
	
	protected int id;
	protected String name;
	protected String location ;

	public Fields() throws RMException
	{
		tableName = "Fields";
	}

	public Fields(int id) throws RMException
	{
    	tableName = "Areas";
		String sql = "SELECT * " +
					 "FROM " + tableName + " " +
					 "WHERE id = " + id;
		this.populateObject(sql, this);
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
