package dk.itu.mmad.travelapp.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.JsonToken;
import android.widget.ArrayAdapter;

public class PriceListTask extends AsyncTask<Void, Void, List<String>> {

	public static final String URL = "http://www.itu.dk/people/jacok/MMAD/services/prices/";
	
	private ListActivity activity;

	public PriceListTask(ListActivity activity) {
		this.activity = activity;
	}

	@Override
	protected List<String> doInBackground(Void... params) {
		InputStream inputStream = null;
		JsonReader jsonReader = null;
		try {
			List<String> entries = new ArrayList<String>();
			URLConnection connection = new URL(URL).openConnection();
			inputStream = connection.getInputStream();
			jsonReader = new JsonReader(new BufferedReader(new InputStreamReader(inputStream)));
			// "commontravels"
			jsonReader.beginObject();
			jsonReader.skipValue();
			jsonReader.beginArray();
			while (jsonReader.peek() != JsonToken.END_ARRAY) {
				jsonReader.beginObject();
				// "stations"
				jsonReader.skipValue();
				jsonReader.beginArray();
				String station1 = jsonReader.nextString();
				String station2 = jsonReader.nextString();
				jsonReader.endArray();
				jsonReader.skipValue();
				int price = jsonReader.nextInt();
				jsonReader.endObject();
				entries.add(station1 + " - " + station2 + ": " + price);
			}
			jsonReader.endArray();
			// "defaultprice"
			jsonReader.skipValue();
			int defaultPrice = jsonReader.nextInt();
			entries.add("Default price: " + defaultPrice);
			return entries;
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				
				if (jsonReader != null) {
					jsonReader.close();
				}
			} catch (Exception e) {
				// Tough luck
			}
		}
	}
	
	@Override
	protected void onPostExecute(List<String> result) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, result);
		activity.setListAdapter(adapter);
	}
}
