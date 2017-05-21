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
		LOGGER.info("Writing event data to MySQL.");

		String processID = (String) execution.getVariable("processID");
		String messageName = (String) execution.getVariable("messageName");
		String eventBody = (String) execution.getVariable("eventBody");
		LOGGER.info("Process ID: " + processID + "; Message Name: " + messageName + "; eventBody: " + eventBody);

		String dbKey = processID + "-" + messageName;

		writeToMySQL(dbKey, eventBody);
	}

	public void writeToMySQL(String dbkey, String eventBody) {
		try {
			// create a mysql database connection
			String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = "jdbc:mysql:" + MYSQL_ADDRESS;
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root", "");

			// the mysql insert statement
			String query = " insert into events (correlationKey, eventBody, timestamp)" + " values (?, ?, ?)";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, dbkey);
			preparedStmt.setString(2, eventBody);
			preparedStmt.setLong(3, System.currentTimeMillis());

			// execute the preparedstatement
			preparedStmt.execute();

			conn.close();
		} catch (Exception e) {
			LOGGER.info("Exception while writing to MySQL: " + e.getMessage());
		}
	}

}
