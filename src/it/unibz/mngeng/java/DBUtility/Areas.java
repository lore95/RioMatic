package it.unibz.mngeng.java.DBUtility;

import it.unibz.mngeng.java.Exceptions.RMException;

public class Areas extends DBInterface
{
	private static final long serialVersionUID = 4084049596659828087L;
	
	protected int id;
	protected int fieldId;
	protected double moistureMin;
	protected int wateringTime;

	public Areas() throws RMException
	{
		tableName = "Areas";
	}

	public Areas(int id) throws RMException
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
	public int getFieldId() {
		return fieldId;
	}
	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}
	public double getMoistureMin() {
		return moistureMin;
	}
	public void setMoistureMin(double moistureMin) {
		this.moistureMin = moistureMin;
	}
	public int getWateringTime() {
		return wateringTime;
	}
	public void setWateringTime(int wateringTime) {
		this.wateringTime = wateringTime;
	}
	
}
