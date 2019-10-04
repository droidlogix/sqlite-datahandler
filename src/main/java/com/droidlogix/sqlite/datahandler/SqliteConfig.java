package com.droidlogix.sqlite.datahandler;

public class SqliteConfig
{
	private String dbLocation;
	private String dbUsername;
	private String dbPassword;

	public SqliteConfig()
	{
	}

	public SqliteConfig(String dbLocation, String dbUsername, String dbPassword)
	{
		this.dbLocation = dbLocation;
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
	}

	public String getDbLocation()
	{
		return dbLocation;
	}

	public void setDbLocation(String dbLocation)
	{
		this.dbLocation = dbLocation;
	}

	public String getDbUsername()
	{
		return dbUsername;
	}

	public void setDbUsername(String dbUsername)
	{
		this.dbUsername = dbUsername;
	}

	public String getDbPassword()
	{
		return dbPassword;
	}

	public void setDbPassword(String dbPassword)
	{
		this.dbPassword = dbPassword;
	}
}
