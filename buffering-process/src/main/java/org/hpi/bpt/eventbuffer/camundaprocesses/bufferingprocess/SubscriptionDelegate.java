package org.hpi.bpt.eventbuffer.camundaprocesses.bufferingprocess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

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

		String response = doPost(messageName, buffProcessID);

		LOGGER.info("Subscription-ID: " + response);
	}

	// HTTP GET request
	private String doPost(String msgName, String processInstanceID) throws Exception {
		String url = "http://localhost:5000/cep/subscription";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");

		String jsonBody = "{\"msgName\":\"" + msgName + "\",\"processInstanceId\":\"" + processInstanceID + "\"}";

		OutputStream os = con.getOutputStream();
		os.write(jsonBody.getBytes());
		os.flush();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		return response.toString();

	}

}