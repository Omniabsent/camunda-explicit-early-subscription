package org.camunda.bpm.getstarted.earlysubscription.earlysubscriptionprocess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SubscriptionDelegate implements JavaDelegate {

	private final static Logger LOGGER = Logger.getLogger("LOAN-REQUESTS");
	private Expression exp_msgname;

	public void execute(DelegateExecution execution) throws Exception {
		LOGGER.info("Subscribing to events on localhost.");

		String messageName = (String) exp_msgname.getValue(execution);
		String correlationVarName = "correlation_" + Math.round(Math.random() * 10000);

		String response = doPost(messageName, correlationVarName);
		execution.setVariable(correlationVarName, response);
		LOGGER.info("Correlating via: " + response);
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