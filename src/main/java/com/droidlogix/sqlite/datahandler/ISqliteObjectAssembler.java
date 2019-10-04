package com.droidlogix.sqlite.datahandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by John Pili on 11/02/17.
 */

public interface ISqliteObjectAssembler<T>
{
	/**
	 * This method is used for converting SQLite result into POJO via dependecy injection
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	T assemble(ResultSet resultSet) throws SQLException;
}
