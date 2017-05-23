package org.hpi.bpt.eventbuffer.util;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * Triggers the delivery of buffered events by sending a start event message
 * "buffering-task" which instantiates a buffering-process
 * 
 * @author Dennis
 *
 */

public class TriggerBufferingProcessDelegate implements JavaDelegate {

	private final static Logger LOGGER = Logger.getLogger("TRIGGER-BUFFERING");
	private Expression exp_messageName;

	public void execute(DelegateExecution execution) throws Exception {
		// should set ref_processDefinitionId and ref_messageName as process
		// Variables
		String processDefinitionId = execution.getProcessDefinitionId();
		String messageName = (String) exp_messageName.getValue(execution);

		LOGGER.info("Delegate triggers event delivery for Process Definition " + processDefinitionId + ", messageName "
				+ messageName);

		// send start-event "buffering-task"
		execution.getProcessEngineServices().getRuntimeService().createMessageCorrelation("buffering-task")
				.setVariable("ref_processDefinitionId", processDefinitionId).setVariable("ref_messageName", messageName)
				.correlateStartMessage();
	}
}