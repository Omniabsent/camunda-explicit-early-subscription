package org.hpi.bpt.eventbuffer.camundaprocesses.bufferingprocess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class WriteToMySQLDelegate implements JavaDelegate {
	private final static Logger LOGGER = Logger.getLogger("WRITE-TO-MYSQL");

	private final String MYSQL_ADDRESS = "//localhost/eventbuffering";

	public void execute(DelegateExecution execution) throws Exception {
		LOGGER.info("Writing event data to MySQL. " + MYSQL_ADDRESS);

		String processId = (String) execution.getVariable("bufferedMessage_piid");
		String messageName = (String) execution.getVariable("bufferedMessage_name");
		String eventBody = (String) execution.getVariable("eventBody");
		LOGGER.info("Process ID: " + processId + "; Message Name: " + messageName + "; eventBody: " + eventBody);

		String dbKey = processId + "-" + messageName;

		writeToMySQL(dbKey, eventBody, processId, messageName);
	}

	public void writeToMySQL(String dbkey, String eventBody, String processId, String messageName) {
		try {
			// create a mysql database connection
			String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = "jdbc:mysql:" + MYSQL_ADDRESS;
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root", "");

			// the mysql insert statement
			String query = " insert into events (pkey, eventBody, eventOccurrenceTime, messageName, processInstanceId)"
					+ " values (?, ?, ?, ?, ?)";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, dbkey);
			preparedStmt.setString(2, eventBody);
			preparedStmt.setLong(3, System.currentTimeMillis());
			preparedStmt.setString(4, messageName);
			preparedStmt.setString(5, processId);

			// execute the preparedstatement
			preparedStmt.execute();

			conn.close();
		} catch (Exception e) {
			LOGGER.info("Exception while writing to MySQL: " + e.getMessage());
		}
	}

}
