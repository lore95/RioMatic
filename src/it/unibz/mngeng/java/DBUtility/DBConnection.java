package it.unibz.mngeng.java.DBUtility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import it.unibz.mngeng.java.Exceptions.RMException;

public class DBConnection 
{
    private DataSource ds = null;
    private ResultSet rs = null;
    private ResultSetMetaData rsm = null;
    private Statement st = null;
	private Connection conn = null;
	
	private static DBConnection singletonInstance = new DBConnection();


	public void getConnection() throws RMException  
	{
		if (conn != null)
		{
			try
			{
				st.execute("SELECT 1");
				return;
			} 
			catch (SQLException e) 
			{
				;
			}
			try 
			{
				finalize();
			}
			catch (Throwable e) 
			{
				;
			}
		}
		
    	String retVal = null;
 		Exception e1 = null;
		try
		{
			conn = DriverManager.getConnection("jdbc:mysql://" + System.getProperty("DBHOST") + "/RioMatic","RioMatic","RioMatic");;
			st = conn.createStatement();
		}
		catch (SQLException e) 
		{
			retVal = "Error on database connection (" + e.getMessage() + ")";
			e1 = e;
		}
		if (retVal != null)
		{
			try 
			{
				finalize();
			} 
			catch (Throwable e) 
			{
				// No action required
				;
			}
			throw new RMException(e1);
		}
	}
    
	protected void finalize() throws Throwable 
	{
		try 
		{
			ds = null;
			rsm = null;
			if (rs != null)
			{
				rs.close();
				rs = null;
			}
			if (st != null)
			{
				st.close();
				st = null;
			}
			if(conn != null)
			{
				conn.close();
				conn = null;
			}
		}
		catch(Exception e)
		{
			;
		}
	}
	
	public void executeQuery(String sql) throws RMException
	{
		StringTokenizer stok = new StringTokenizer(sql);
		String queryType = "";
		if (stok != null)
		{
			queryType = stok.nextToken().toUpperCase();
		}

		if (st == null)
		{
			try 
			{
				finalize();
				getConnection();
			} 
			catch(RMException e)
			{
				throw e;
			}
			catch (Throwable e) 
			{
				// No action required
				throw new RMException(e);
			}
		}
		try
		{
			if ((queryType.compareTo("INSERT") == 0) ||
				(queryType.compareTo("DELETE") == 0) ||
				(queryType.compareTo("UPDATE") == 0))
			{
				st.execute(sql);
			}
			else
			{
				rs = st.executeQuery(sql);
				rsm = rs.getMetaData();
			}
		}
		catch(Exception e)
		{
			throw new RMException(e);
		}
	}

	public ResultSetMetaData getRsm() {
		return rsm;
	}

	public ResultSet getRs() {
		return rs;
	}

	public Statement getSt()
	{
		return st;
	}
	
	private DBConnection()
	{
		return;
	}
	
	public static DBConnection getInstance() throws RMException
	{
		if ((singletonInstance.conn == null) || 
			(singletonInstance.ds == null) ||
			(singletonInstance.st == null))
		{
			try 
			{
				singletonInstance.finalize();
			}
			catch (Throwable e) 
			{
				// no actions
				;
			}
			singletonInstance = new DBConnection();
		}
		
		singletonInstance.getConnection();
		return singletonInstance;
	}
}
