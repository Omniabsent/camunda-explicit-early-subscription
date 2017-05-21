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

		String processID = (String) execution.getVariable("processID");
		String messageName = (String) execution.getVariable("messageName");
		LOGGER.info("Process ID: " + processID + "; Message Name: " + messageName);

		String correlationVarName = "correlation" + System.currentTimeMillis() + Math.round(Math.random() * 10000);

		String response = doPost(messageName, correlationVarName);
		execution.setVariable(correlationVarName, response);
		LOGGER.info("Correlating via subscription-ID: " + response);
	}

	// HTTP GET request
	private String doPost(String msgName, String correlationKey) throws Exception {
		String url = "http://localhost:5000/cep/subscription";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");

		String jsonBody = "{\"msgName\":\"" + msgName + "\",\"correlationKey\":\"" + correlationKey + "\"}";

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