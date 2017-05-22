package org.hpi.bpt.eventbuffer.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

public class MySQLHelper {
	private final static Logger LOGGER = Logger.getLogger("MYSQL-HELPER");

	private final static String MYSQL_JDBC_ADDRESS = "jdbc:mysql://localhost/eventbuffering";
	private final static String MYSQL_JDBC_DRIVER = "org.gjt.mm.mysql.Driver";

	public static void writeToMySQL(String eventMessage, String processId, String messageName) {
		try {
			// create a mysql database connection
			Class.forName(MYSQL_JDBC_DRIVER);
			Connection conn = DriverManager.getConnection(MYSQL_JDBC_ADDRESS, "root", "");

			// the mysql insert statement
			String query = " insert into events (eventMessage, eventOccurrenceTime, messageName, processDefinitionId)"
					+ " values (?, ?, ?, ?)";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			int i = 1;
			preparedStmt.setString(i++, eventMessage);
			preparedStmt.setLong(i++, System.currentTimeMillis());
			preparedStmt.setString(i++, messageName);
			preparedStmt.setString(i++, processId);

			// execute the preparedstatement
			preparedStmt.execute();

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("Exception while writing to MySQL: " + e.getMessage());
		}
	}

	public static String loadEventMessageFromMySQL(String eventMessageName, String eventProcessDefId) {
		String result = null;
		try {
			// create our mysql database connection
			Class.forName(MYSQL_JDBC_DRIVER);
			Connection conn = DriverManager.getConnection(MYSQL_JDBC_ADDRESS, "root", "");
			Statement st = conn.createStatement();

			// define query
			String query = "SELECT eventMessage FROM events WHERE processDefinitionId = \"" + eventProcessDefId + "\""
					+ " AND messageName = \"" + eventMessageName + "\"" + " ORDER BY eventOccurrenceTime DESC LIMIT 1";

			// execute the query, and get a java resultset
			LOGGER.info("running query: " + query);
			ResultSet rs = st.executeQuery(query);

			// read first line of result
			if (rs.next()) {
				result = rs.getString(1);
			}
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error while retrieving buffered events from mysql: " + e.getMessage());
		}

		return result;
	}

	public static void removeEventMessages(String eventMessageName, String eventProcessDefId) {
		try {
			// create our mysql database connection
			Class.forName(MYSQL_JDBC_DRIVER);
			Connection conn = DriverManager.getConnection(MYSQL_JDBC_ADDRESS, "root", "");
			Statement st = conn.createStatement();

			// define query
			String sqlStatement = "DELETE FROM events WHERE processDefinitionId = \"" + eventProcessDefId + "\""
					+ " AND messageName = \"" + eventMessageName + "\"";

			// execute the query, and get a java resultset
			LOGGER.info("running statement: " + sqlStatement);
			st.execute(sqlStatement);

			st.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error while removing events from mysql: " + e.getMessage());
		}
	}
}
