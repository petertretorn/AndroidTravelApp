// Solution by Peter Bruijn Larsen

package dk.itu.mmad.travelapp.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.JsonReader;
import android.util.JsonToken;

public class SightsServiceImplementaion implements SightsService {

	private String urlString;

	public SightsServiceImplementaion(String url) {
		this.urlString = url;
	}

	@Override
	public String addSight(String body) {

		InputStream inputStream = null;
		OutputStream outputStream = null;
		BufferedReader reader = null;

		try {
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();

			((HttpURLConnection) connection).setRequestMethod("POST");

			outputStream = connection.getOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);
			writer.write(body);
			writer.flush();
			writer.close();

			inputStream = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String result = reader.readLine();

			// check if submission returned JSONobject indicating success
			result = new JSONObject(result).getString("sights");

			if (result.startsWith("Successfully", 0))
				return "Sight was submitted succesfully!";
			else
				return "Submission failed!";

		} catch (JSONException e2) {
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (Exception ignore) {

			}
		}
		return "An error occured while submitting";

	}

	@Override
	public List<String> getSights(String location) {

		InputStream inputStream = null;
		JsonReader jsonReader = null;

		try {
			List<String> entries = new ArrayList<String>();

			URL url = new URL(urlString + "?location=" + location);
			URLConnection connection = url.openConnection();

			inputStream = connection.getInputStream();
			jsonReader = new JsonReader(new BufferedReader(
					new InputStreamReader(inputStream)));

			jsonReader.beginObject();
			jsonReader.skipValue();
			jsonReader.beginArray();
			while (jsonReader.peek() != JsonToken.END_ARRAY) {

				jsonReader.beginObject();

				// skip the id key
				jsonReader.skipValue();
				// skip the id value
				jsonReader.skipValue();

				// skip the name key
				jsonReader.skipValue();
				// get name value
				String name = saveValueFetch(jsonReader);

				// get the description
				jsonReader.skipValue();
				String description = saveValueFetch(jsonReader);

				jsonReader.skipValue();
				String uploader = saveValueFetch(jsonReader);

				jsonReader.endObject();

				// concatenate the string values and add them to the list
				String itemText = name + ": " + description + "\n(uploaded by "
						+ uploader + ")";
				entries.add(itemText);
			}
			jsonReader.endArray();

			return entries;
		} catch (IOException e) {
			List<String> entries = new ArrayList<String>();
			entries.add("An error has occured!");
			return entries;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}

				if (jsonReader != null) {
					jsonReader.close();
				}
			} catch (Exception e) {

			}
		}

	}

	/*
	 * helper method that extracts JSON values savely from the JSONReader object
	 * in case of embedded null values in the JSON broadcasted by service. The
	 * empty String, "", poses no problem.
	 */
	private String saveValueFetch(JsonReader jReader) {

		String value = "";
		try {
			value = jReader.nextString();
		} catch (Exception e) {
			try {
				jReader.nextNull();
				return "";
			} catch (IOException noWorries) {
				return "";
			}
		}
		return value;
	}

}
