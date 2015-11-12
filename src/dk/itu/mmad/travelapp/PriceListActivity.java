package dk.itu.mmad.travelapp;

import dk.itu.mmad.travelapp.external.PriceListTask;
import android.app.ListActivity;
import android.os.Bundle;

public class PriceListActivity extends ListActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    new PriceListTask(this).execute();
	    
	}

}
