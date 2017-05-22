package org.hpi.bpt.eventbuffer.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilTest {

	@Test
	public void mysqlHelperTest() {
		String test_identifier = System.currentTimeMillis() + "";
		String eventMsg = "junit-test-event_" + test_identifier;
		String pDefId = "junit-test-process_" + test_identifier;
		String msgName = "junit-test-messageName_" + test_identifier;

		MySQLHelper.writeToMySQL(eventMsg, pDefId, msgName);
		assertTrue(MySQLHelper.loadEventMessageFromMySQL(msgName, pDefId).equals(eventMsg));

		MySQLHelper.writeToMySQL(eventMsg + "_2", pDefId, msgName);
		assertTrue(MySQLHelper.loadEventMessageFromMySQL(msgName, pDefId).equals(eventMsg + "_2"));

		MySQLHelper.removeEventMessages(msgName, pDefId);
		assertTrue(MySQLHelper.loadEventMessageFromMySQL(msgName, pDefId) == null);
	}

	@Test
	public void httpHelperTest() {
		String response = HTTPHelper.doPost("junit-test-message", "junit-test-instance");
		assertTrue(response != null);
	}

}
