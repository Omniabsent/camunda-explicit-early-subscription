package org.hpi.bpt.eventbuffer.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class HTTPHelper {
	private final static Logger LOGGER = Logger.getLogger("MYSQL-HELPER");

	public static String doPost(String msgName, String processInstanceID) {
		String result = null;
		try {
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
			result = response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("Error while HTTP Post to CEP: " + e.getMessage());
		}
		return result;

	}
}
