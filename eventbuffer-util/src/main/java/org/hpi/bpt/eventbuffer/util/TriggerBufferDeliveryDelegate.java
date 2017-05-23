package org.hpi.bpt.eventbuffer.util;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * Triggers the delivery of buffered events by sending a start event message
 * "buffering-event-delivery-task" which instantiates a buffer-delivery-process
 * 
 * @author Dennis
 *
 */

public class TriggerBufferDeliveryDelegate implements JavaDelegate {

	private final static Logger LOGGER = Logger.getLogger("TRIGGER-BUFFER-DELIVERY");

	private Expression exp_messageName;

	public void execute(DelegateExecution execution) throws Exception {
		// sets ref_processInstanceId, event_messageName,
		// event_processDefinitionId
		String processInstanceId = execution.getProcessInstanceId();
		String processDefinitionId = execution.getProcessDefinitionId();
		String messageName = (String) exp_messageName.getValue(execution);

		LOGGER.info("Delegate triggers event delivery for Process Definition " + processDefinitionId + ", messageName "
				+ messageName);

		// send start-event "buffering-task"
		execution.getProcessEngineServices().getRuntimeService()
				.createMessageCorrelation("buffering-event-delivery-task")
				.setVariable("event_processDefinitionId", processDefinitionId)
				.setVariable("event_messageName", messageName).setVariable("ref_processInstanceId", processInstanceId)
				.correlateStartMessage();
		LOGGER.info("Finished trigger buffer delivery.");
	}

}