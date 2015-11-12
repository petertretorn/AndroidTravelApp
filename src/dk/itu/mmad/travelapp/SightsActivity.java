// Solution by Peter Bruijn Larsen

package dk.itu.mmad.travelapp;

import java.util.HashMap;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import dk.itu.mmad.travelapp.external.*;

public class SightsActivity extends ListActivity {

	private EditText nameEditText;
	private EditText descriptionEditText;
	private EditText uploaderEditText;

	private Button addSightButton;

	private TextView locationView;

	public static final String LOCATION_TAG = "location";

	private String location;

	// interface type, gets instantiated with either a proper implementation or a stub
	SightsService sightsService;

	public final static String TEST_URL = "http://itu.dk/people/jacok/MMAD/services/sights/test/";
	public final static String PRODUCTION_URL = "http://itu.dk/people/jacok/MMAD/services/sights/";

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.sights);

		// instantiating the sightService with a proper instance, outcomment
		// this for test scenario
		sightsService = new SightsServiceImplementaion(PRODUCTION_URL);

		// instantiating the sightService with a stub for testing
		// purposes
		// sightsService = new SightsServiceStub();

		// fetch the location passed with the intent
		Bundle extras = getIntent().getExtras();
		location = extras.getString(LOCATION_TAG);

		// populate content of TextView
		locationView = (TextView) findViewById(R.id.location);
		locationView.setText(location);

		// fetch list of sights at location from webservice asynchronously
		new FetchSightsTask(SightsActivity.this, location, sightsService)
				.execute(TEST_URL);

		nameEditText = (EditText) findViewById(R.id.sightname);
		descriptionEditText = (EditText) findViewById(R.id.sightdescription);
		uploaderEditText = (EditText) findViewById(R.id.sightuploader);

		addSightButton = (Button) findViewById(R.id.addsight);
		addSightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// create a Map to store parameters used in body of post request
				Map<String, String> params = new HashMap<String, String>();

				// populate the map with values provided by user
				params.put("name", nameEditText.getText().toString());
				params.put("description", descriptionEditText.getText()
						.toString());
				params.put("uploader", uploaderEditText.getText().toString());
				params.put("location", location);

				// add a new sight asynchronously, so the main UI thread in not
				// blocked by network operation
				new AddSightTask(SightsActivity.this, params, PRODUCTION_URL,
						sightsService).execute();
			}
		});
	}

	public void clearFields() {

		nameEditText.setText("");
		descriptionEditText.setText("");
		uploaderEditText.setText("");

		nameEditText.requestFocus();
	}
}
