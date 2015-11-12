package dk.itu.mmad.travelapp;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import dk.itu.mmad.travelapp.db.DBAdapter;

public class SelectStationActivity extends ListActivity {

	// String[] stations = {"Copenhagen", "Aarhus", "Odense",
	// "End of the World"};

	DBAdapter dbAdapter;
	SimpleCursorAdapter cursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dbAdapter = new DBAdapter(this);
		dbAdapter.open();
		Cursor stations = dbAdapter.getStations();
		startManagingCursor(stations);
		cursorAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, stations,
				new String[] { "station" }, new int[] { android.R.id.text1 });

		setListAdapter(cursorAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Cursor cursor = (Cursor) l.getItemAtPosition(position);
		Intent intent = new Intent().putExtra(
				TravelActivity.SELECTED_STATION_NAME,
				cursor.getString(cursor.getColumnIndexOrThrow("station")));
		setResult(RESULT_OK, intent);
		cursor.close();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbAdapter.close();
	}
}
