package org.hpi.bpt.eventbuffer.camundaprocesses.bufferingprocess;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hpi.bpt.eventbuffer.util.MySQLHelper;

public class WriteToMySQLDelegate implements JavaDelegate {
	private final static Logger LOGGER = Logger.getLogger("WRITE-TO-MYSQL");

	public void execute(DelegateExecution execution) throws Exception {
		LOGGER.info("Delegate execution: Writing event data to MySQL.");

		String processId = (String) execution.getVariable("ref_processDefinitionId");
		String messageName = (String) execution.getVariable("ref_messageName");
		String eventMessage = (String) execution.getVariable("eventBody");
		LOGGER.info("Process ID: " + processId + "; Message Name: " + messageName + "; eventMessage: " + eventMessage);

		MySQLHelper.writeToMySQL(eventMessage, processId, messageName);
	}

}
