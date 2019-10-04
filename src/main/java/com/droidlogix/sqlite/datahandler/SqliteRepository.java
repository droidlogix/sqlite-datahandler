package com.droidlogix.sqlite.datahandler;

import com.droidlogix.sqlite.datahandler.exceptions.SqliteDriverNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by John Pili on 11/02/17.
 */

public class SqliteRepository implements ISqliteRepository
{
	private static final Logger logger = LoggerFactory.getLogger(SqliteRepository.class);

	private SqliteConfig sqliteConfig;

	public SqliteRepository(SqliteConfig sqliteConfig)
	{
		this.sqliteConfig = sqliteConfig;
	}

	public SqliteRepository(String dbLocation, String dbUsername, String dbPassword)
	{
		this.sqliteConfig = new SqliteConfig(dbLocation, dbUsername, dbPassword);
	}

	@Override
	public Connection getConnection() throws SQLException, SqliteDriverNotFoundException
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection(sqliteConfig.getDbLocation());
			try (PreparedStatement preparedStatement = connection.prepareStatement("PRAGMA foreign_keys=ON"))
			{
				preparedStatement.execute();
			}
			return connection;
		}
		catch (ClassNotFoundException classNotFoundException)
		{
			throw new SqliteDriverNotFoundException("Cannot load org.sqlite.JDBC driver");
		}
	}

	@Override
	public int insert(String sql, Map<Integer, Object> parameters) throws SQLException, SqliteDriverNotFoundException
	{
		try (Connection connection = getConnection())
		{
			try (PreparedStatement preparedStatement = connection.prepareStatement(sanitizeSqlString(sql)))
			{
				if (parameters != null)
				{
					injectParameterToPreparedStatement(parameters, preparedStatement);
				}
				preparedStatement.executeUpdate();
				return getLastInsertedId(connection);
			}
		}
	}

	@Override
	public int update(String sql, Map<Integer, Object> parameters) throws SQLException, SqliteDriverNotFoundException
	{
		return execute(sql, parameters);
	}

	@Override
	public int delete(String sql, Map<Integer, Object> parameters) throws SQLException, SqliteDriverNotFoundException
	{
		return execute(sql, parameters);
	}

	@Override
	public <T> T getSingle(String sql, ISqliteObjectAssembler sqliteObjectAssembler) throws SQLException, SqliteDriverNotFoundException
	{
		try (Connection connection = getConnection())
		{
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
			{
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet != null)
				{
					return (T) sqliteObjectAssembler.assemble(resultSet);
				}
			}
		}
		return null;
	}

	@Override
	public <T> T getSingle(String sql, Map<Integer, Object> parameters, ISqliteObjectAssembler sqliteObjectAssembler)
			throws SQLException, SqliteDriverNotFoundException
	{
		try (Connection connection = getConnection())
		{
			try (PreparedStatement preparedStatement = connection.prepareStatement(sanitizeSqlString(sql)))
			{
				if (parameters != null)
				{
					injectParameterToPreparedStatement(parameters, preparedStatement);
				}
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet != null)
				{
					return (T) sqliteObjectAssembler.assemble(resultSet);
				}
			}
		}
		return null;
	}

	@Override
	public <T> List<T> getList(String sql, ISqliteObjectAssembler sqliteObjectAssembler) throws SQLException, SqliteDriverNotFoundException
	{
		try (Connection connection = getConnection())
		{
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
			{
				ResultSet resultSet = preparedStatement.executeQuery();
				return executeListQuery(resultSet, sqliteObjectAssembler);
			}
		}
	}

	@Override
	public <T> List<T> getList(String sql, Map<Integer, Object> parameters,
	                           ISqliteObjectAssembler sqliteObjectAssembler) throws SQLException, SqliteDriverNotFoundException
	{
		try (Connection connection = getConnection())
		{
			try (PreparedStatement preparedStatement = connection.prepareStatement(sanitizeSqlString(sql)))
			{
				if (parameters != null)
				{
					injectParameterToPreparedStatement(parameters, preparedStatement);
				}
				ResultSet resultSet = preparedStatement.executeQuery();
				return executeListQuery(resultSet, sqliteObjectAssembler);
			}
		}
	}

	@Override
	public void createTable(String sql) throws SQLException, SqliteDriverNotFoundException
	{
		tableSqlExecutor(sql);
	}

	@Override
	public void alterTable(String sql) throws SQLException, SqliteDriverNotFoundException
	{
		tableSqlExecutor(sql);
	}

	@Override
	public void dropTable(String sql) throws SQLException, SqliteDriverNotFoundException
	{
		tableSqlExecutor(sql);
	}

	/**
	 * This method handles table query manipulation
	 *
	 * @param sql
	 * @throws SQLException
	 */
	private void tableSqlExecutor(String sql) throws SQLException, SqliteDriverNotFoundException
	{
		try (Connection connection = getConnection())
		{
			Statement statement = connection.createStatement();
			statement.execute(sql);
		}
	}

	/**
	 * This injects hash map into the prepared statement
	 *
	 * @param parameters
	 * @param preparedStatement
	 * @throws SQLException
	 */
	private void injectParameterToPreparedStatement(Map<Integer, Object> parameters, PreparedStatement preparedStatement) throws SQLException
	{
		if (parameters != null && preparedStatement != null)
		{
			for (Map.Entry entry : parameters.entrySet())
			{
				preparedStatement.setObject((int) entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * A little bit of rainbow and sugar string sanitation
	 *
	 * @param sql
	 * @return
	 */
	private String sanitizeSqlString(String sql)
	{
		if (sql != null)
		{
			return sql.trim();
		}
		return "";
	}

	/**
	 * This will retreive the late generated ID from SQLite
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	private int getLastInsertedId(Connection connection) throws SQLException
	{
		if (connection != null)
		{
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT last_insert_rowid()");
			if (resultSet != null)
			{
				return resultSet.getInt(1);
			}
		}
		return -1;
	}

	/**
	 * This method handles the execution of prepared statements
	 * returns the number of affected records
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws SQLException
	 * @throws SqliteDriverNotFoundException
	 */
	private int execute(String sql, Map<Integer, Object> parameters) throws SQLException, SqliteDriverNotFoundException
	{
		try (Connection connection = getConnection())
		{
			try (PreparedStatement preparedStatement = connection.prepareStatement(sanitizeSqlString(sql)))
			{
				if (parameters != null)
				{
					injectParameterToPreparedStatement(parameters, preparedStatement);
				}
				return preparedStatement.executeUpdate();
			}
		}
	}

	/**
	 * This method handles the execution of prepared statements
	 * returns the result as a list
	 * @param resultSet
	 * @param sqliteObjectAssembler
	 * @return
	 * @throws SQLException
	 */
	private <T> List<T> executeListQuery(ResultSet resultSet, ISqliteObjectAssembler sqliteObjectAssembler) throws SQLException
	{
		List<T> results = new ArrayList<>();
		while (resultSet.next())
		{
			results.add((T) sqliteObjectAssembler.assemble(resultSet));
		}
		return results;
	}

}
