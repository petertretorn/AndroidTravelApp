package dk.itu.mmad.travelapp.fragments;

import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import dk.itu.mmad.travelapp.R;
import dk.itu.mmad.travelapp.db.DBAdapter;

public class HistoryFragment extends ListFragment {

	DBAdapter dbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbAdapter = new DBAdapter(getActivity());
		dbAdapter.open();
		Cursor travels = dbAdapter.getTravels();
		
		// instantiate views and columns used for Cursor Adapter
		int[] views = new int[] { R.id.start, R.id.destination, R.id.starttime, R.id.endtime, R.id.duration, R.id.distance };
		String[] columns = new String[] { "start", "destination", "start_time", "end_time", "duration", "distance" };
		
		getActivity().startManagingCursor(travels);
		
		SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
				getActivity(), R.layout.historyitem, travels, columns, views);
		
		setListAdapter(cursorAdapter);

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		dbAdapter.close();
	}
}
