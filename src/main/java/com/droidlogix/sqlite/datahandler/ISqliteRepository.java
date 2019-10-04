package com.droidlogix.sqlite.datahandler;

import com.droidlogix.sqlite.datahandler.exceptions.SqliteDriverNotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by mrprintedwall on 11/02/17.
 */

public interface ISqliteRepository
{
	//region CONNECTION

	/**
	 * This method initialize the SQLite connection and driver check
	 * @return
	 * @throws SQLException
	 * @throws SqliteDriverNotFoundException
	 */
	Connection getConnection() throws SQLException, SqliteDriverNotFoundException;

	//endregion

	//region ROW METHODS

	/**
	 * Insert into SQLite and return the generated primary ID.
	 * Method will return -1 if an error occurs during execution
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws SQLException
	 */
	int insert(String sql, Map<Integer, Object> parameters) throws SQLException, SqliteDriverNotFoundException;

	/**
	 * Update rows in SQLite and return the number of affected rows
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws SQLException
	 */
	int update(String sql, Map<Integer, Object> parameters) throws SQLException, SqliteDriverNotFoundException;

	/**
	 * Delete rows in SQLite and return the number of affected rows
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws SQLException
	 */
	int delete(String sql, Map<Integer, Object> parameters) throws SQLException, SqliteDriverNotFoundException;

	/**
	 * Query a single item using sql. Convert resultset to POJO via dependency injection
	 * @param sql
	 * @param sqliteObjectAssembler
	 * @return
	 * @throws SQLException
	 */
	<T> T getSingle(String sql, ISqliteObjectAssembler sqliteObjectAssembler) throws SQLException, SqliteDriverNotFoundException;

	/**
	 * Query a single item using sql and map of parameters. Convert resultset to POJO via dependency injection
	 * @param sql
	 * @param parameters
	 * @param sqliteObjectAssembler
	 * @return
	 * @throws SQLException
	 */
	<T> T getSingle(String sql, Map<Integer, Object> parameters, ISqliteObjectAssembler sqliteObjectAssembler) throws SQLException, SqliteDriverNotFoundException;

	/**
	 * Query a list items using sql. Convert resultset to POJO via dependency injection
	 * @param sql
	 * @param sqliteObjectAssembler
	 * @param <T>
	 * @return
	 * @throws SQLException
	 */
	<T> List<T> getList(String sql, ISqliteObjectAssembler sqliteObjectAssembler) throws SQLException, SqliteDriverNotFoundException;

	/**
	 * Query a list items using sql and map of parameters. Convert resultset to POJO via dependency injection
	 * @param sql
	 * @param parameters
	 * @param sqliteObjectAssembler
	 * @return
	 * @throws SQLException
	 */
	<T> List<T> getList(String sql, Map<Integer, Object> parameters, ISqliteObjectAssembler sqliteObjectAssembler) throws SQLException, SqliteDriverNotFoundException;

	//endregion

	//region TABLE METHODS

	/**
	 * Method that handles create table
	 * @param sql
	 * @throws SQLException
	 * @throws SqliteDriverNotFoundException
	 */
	void createTable(String sql) throws SQLException, SqliteDriverNotFoundException;

	/**
	 * Method that handles alter table
	 * @param sql
	 * @throws SQLException
	 * @throws SqliteDriverNotFoundException
	 */
	void alterTable(String sql) throws SQLException, SqliteDriverNotFoundException;

	/**
	 * Method that handles drop table
	 * @param sql
	 * @throws SQLException
	 * @throws SqliteDriverNotFoundException
	 */
	void dropTable(String sql) throws SQLException, SqliteDriverNotFoundException;

	//endregion

}
