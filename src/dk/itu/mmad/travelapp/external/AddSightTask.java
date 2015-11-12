// Solution by Peter Bruijn Larsen

package dk.itu.mmad.travelapp.external;

import java.util.Map;
import java.util.Map.Entry;
import android.os.AsyncTask;
import android.widget.Toast;
import dk.itu.mmad.travelapp.SightsActivity;


public class AddSightTask extends AsyncTask<Void, Void, String> {

	private SightsActivity sightsActivity;

	private Map<String, String> params;

	private String URLresource;

	private SightsService sightsService;

	/*
	 * instance of sightsService passed in via DI. This class is not relying on
	 * a concrete implementation of SightService so a mock instance can be
	 * passed in for testing purposes
	 */
	public AddSightTask(SightsActivity sightsActivity,
			Map<String, String> params, String resource, SightsService ss) {
		this.sightsActivity = sightsActivity;
		this.URLresource = resource;
		this.params = params;
		this.sightsService = ss;
	}

	@Override
	protected String doInBackground(Void... arg0) {

		String postBody = extractParameters(params);

		return sightsService.addSight(postBody);
	}

	@Override
	protected void onPostExecute(String result) {

		Toast.makeText(sightsActivity, result, Toast.LENGTH_LONG).show();

		String location = params.get("location");

		// update view of sights with recently added item
		new FetchSightsTask(sightsActivity, location, sightsService)
				.execute(URLresource);
		
		// clear the EditText boxes for additional sight entries
		sightsActivity.clearFields();
	}

	private String extractParameters(Map<String, String> params) {

		StringBuilder text = new StringBuilder();

		boolean first = true;

		for (Entry<String, String> entry : params.entrySet()) {

			if (first) {
				first = false;
			} else {
				text.append("&");
			}
			text.append(entry.getKey() + "=" + entry.getValue());
		}
		return text.toString();
	}
}
