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

public class TriggerBufferDeliveryDelegate implements JavaDelegate {

	private final static Logger LOGGER = Logger.getLogger("TRIGGER-BUFFER-DELIVERY");
	private final static long TRIGGERDELAY_MS = 500L;

	private Expression exp_messageName;

	public void execute(DelegateExecution execution) throws Exception {
		// sets ref_processInstanceId, event_messageName,
		// event_processDefinitionId
		String processInstanceId = execution.getProcessInstanceId();
		String processDefinitionId = execution.getProcessDefinitionId();
		String messageName = (String) exp_messageName.getValue(execution);

		LOGGER.info("Delegate triggers event delivery for Process Definition " + processDefinitionId + ", messageName "
				+ messageName + " with delay ms: " + TRIGGERDELAY_MS);

		// send start-event "buffering-task"
		execution.getProcessEngineServices().getRuntimeService()
				.createMessageCorrelation("buffering-event-delivery-task")
				.setVariable("event_processDefinitionId", processDefinitionId)
				.setVariable("event_messageName", messageName).setVariable("ref_processInstanceId", processInstanceId)
				.correlateStartMessage();
		LOGGER.info("Finished trigger buffer delivery.");

		// TriggerEventDeliveryRunnable triggerRunnable = new
		// TriggerEventDeliveryRunnable();
		// triggerRunnable.execution = execution;
		// triggerRunnable.messageName = messageName;
		// triggerRunnable.processDefinitionId = processDefinitionId;
		// triggerRunnable.processInstanceId = processInstanceId;
		// new Thread(triggerRunnable).start();
	}

	public class TriggerEventDeliveryRunnable implements Runnable {
		public String processInstanceId;
		public String processDefinitionId;
		public String messageName;

		public DelegateExecution execution;

		public void run() {
			try {
				Thread.sleep(TRIGGERDELAY_MS);
			} catch (InterruptedException e) {
				LOGGER.info("Couldn't sleep before triggering event delivery!");
				e.printStackTrace();
			}

			execution.getProcessEngineServices().getRuntimeService()
					.createMessageCorrelation("buffering-event-delivery-task")
					.setVariable("event_processDefinitionId", processDefinitionId)
					.setVariable("event_messageName", messageName)
					.setVariable("ref_processInstanceId", processInstanceId).correlateStartMessage();
			LOGGER.info("Finished trigger buffer delivery.");
		}

	}
}