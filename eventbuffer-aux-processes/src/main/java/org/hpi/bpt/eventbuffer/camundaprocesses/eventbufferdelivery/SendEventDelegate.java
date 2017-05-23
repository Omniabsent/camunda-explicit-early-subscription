package org.hpi.bpt.eventbuffer.camundaprocesses.eventbufferdelivery;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SendEventDelegate implements JavaDelegate {
	private final static Logger LOGGER = Logger.getLogger("EVENT-DELIVERY");

	public void execute(DelegateExecution execution) throws Exception {
		LOGGER.info("Delivering event to target process");

		String processInstanceId = (String) execution.getVariable("ref_processInstanceId");
		String messageName = (String) execution.getVariable("event_messageName");
		String eventMessage = (String) execution.getVariable("eventMessage");

		// send the event
		execution.getProcessEngineServices().getRuntimeService().createMessageCorrelation(messageName)
				.processInstanceId(processInstanceId).setVariable("eventMessage", eventMessage).correlateWithResult();
	}
}
