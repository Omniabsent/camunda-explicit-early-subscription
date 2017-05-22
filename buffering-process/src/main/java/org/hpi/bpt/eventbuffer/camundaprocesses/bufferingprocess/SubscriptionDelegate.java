package org.hpi.bpt.eventbuffer.camundaprocesses.bufferingprocess;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hpi.bpt.eventbuffer.util.HTTPHelper;

/**
 * To be used as a JavaDelegate class in a Camunda Service Task. Read
 * subscription information (message-name and correlation process ID) from a
 * process variable and subscribe to that message via HTTP post to a fixed
 * external source. Receive a subscription ID as response, store it in a process
 * variable.
 * 
 * @author Dennis
 *
 */

public class SubscriptionDelegate implements JavaDelegate {

	private final static Logger LOGGER = Logger.getLogger("EVENT-BUFFERING");

	public void execute(DelegateExecution execution) throws Exception {
		LOGGER.info("Subscribing to events on localhost.");

		String buffProcessID = execution.getProcessInstanceId();
		String messageName = "event-to-buffer";
		LOGGER.info("Process ID: " + buffProcessID + "; Message Name: " + messageName);

		// we must set the processInstanceId as a Process Variable to allow
		// correlation
		execution.setVariable("correlation-piid", buffProcessID);

		String response = HTTPHelper.doPost(messageName, buffProcessID);

		LOGGER.info("Subscription-ID: " + response);
	}

}