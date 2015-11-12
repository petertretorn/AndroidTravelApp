// Solution by Peter Bruijn Larsen

package dk.itu.mmad.travelapp.external;

import java.util.List;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

public class FetchSightsTask extends AsyncTask<String, Void, List<String>> {

	private ListActivity activity;
	private String location;
	private SightsService sightsService;

	/*
	 * instance of sightsService passed in via DI. The class is not relying on a
	 * concrete implementation so a mock instance can be passed in for testing
	 * purposes
	 */
	public FetchSightsTask(ListActivity activity, String location,
			SightsService ss) {
		this.activity = activity;
		this.location = location;
		this.sightsService = ss;
	}

	@Override
	protected List<String> doInBackground(String... resource) {

		return sightsService.getSights(location);

	}

	@Override
	protected void onPostExecute(List<String> result) {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
				android.R.layout.simple_list_item_1, result);

		activity.setListAdapter(adapter);
	}

}
