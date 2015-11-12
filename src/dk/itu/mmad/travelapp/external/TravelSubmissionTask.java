package dk.itu.mmad.travelapp.external;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import dk.itu.mmad.travelapp.SightsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.JsonReader;


public class TravelSubmissionTask extends AsyncTask<Void, Void, Integer> {

	public static final String URL = "http://www.itu.dk/people/jacok/MMAD/services/registertravel/";

	private Activity activity;
	private String startStation;
	private String endStation;
	
	private int passengers;
	
	private Throwable error;

	public TravelSubmissionTask(Activity activity, String startStation, String endStation, int passengers) {
		this.activity = activity;
		this.startStation = startStation;
		this.endStation = endStation;
		this.passengers = passengers;
	}

	@Override
	protected Integer doInBackground(Void... voids) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		JsonReader jsonReader = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
			connection.setRequestMethod("POST");
			outputStream = connection.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(outputStream);
			writer.write("from=" + startStation + "&to=" + endStation);
			writer.flush();
			writer.close();

			inputStream = connection.getInputStream();
			jsonReader = new JsonReader(new BufferedReader(new InputStreamReader(inputStream)));
			jsonReader.beginObject();
			String type = jsonReader.nextName();
			if ("error".equals(type)) {
				throw new Exception(jsonReader.nextString());
			} else {
				int price = jsonReader.nextInt();
				return price;
			}

		} catch (Exception e) {
			error = e;
			return 0;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
				if (jsonReader != null) {
					jsonReader.close();
				}
			} catch (Exception e) {
				
			}
		}
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		
		Builder builder = new AlertDialog.Builder(activity);
		
		if(error != null) {
			builder.setTitle("An error occured");
			builder.setMessage(error.getMessage());
		} else {
			builder.setTitle("Travel completed");

			//calculate total price based on number of passengers, passed into constructor
			int totalPrice = result * passengers;
			
			builder.setMessage("Price: " + totalPrice);
		}
		
		builder.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		builder.setNeutralButton("See sights", new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(activity, SightsActivity.class);
				intent.putExtra(SightsActivity.LOCATION_TAG, endStation);
				activity.startActivity(intent);
			}
		});
		
		builder.show();
	}
	
	

}
