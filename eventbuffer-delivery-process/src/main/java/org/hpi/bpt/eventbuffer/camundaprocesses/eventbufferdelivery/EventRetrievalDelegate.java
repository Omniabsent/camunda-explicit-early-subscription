package org.hpi.bpt.eventbuffer.camundaprocesses.eventbufferdelivery;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hpi.bpt.eventbuffer.util.MySQLHelper;

public class EventRetrievalDelegate implements JavaDelegate {
	private final static Logger LOGGER = Logger.getLogger("EVENT-RETRIEVAL");

	public void execute(DelegateExecution execution) throws Exception {
		LOGGER.info("Retrieving events from mysql event buffer.");

		String eventMessageName = (String) execution.getVariable("event_messageName");
		String eventProcessDefId = (String) execution.getVariable("event_processDefinitionId");

		String eventMessage = MySQLHelper.loadEventMessageFromMySQL(eventMessageName, eventProcessDefId);

		execution.setVariable("eventMessage", eventMessage);
	}

}
