package dk.itu.mmad.travelapp.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import dk.itu.mmad.travelapp.R;
import dk.itu.mmad.travelapp.db.DBAdapter;

public class PrefsFragment extends PreferenceFragment {
		
	DBAdapter dbAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		
		dbAdapter = new DBAdapter(getActivity());
		dbAdapter.open();
		
		Preference clearHistory = findPreference("clearhistory");
		clearHistory.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				dbAdapter.clearTravels();
				return true;
			}
		});
		
		Preference clearStations = findPreference("clearstations");
		clearStations.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				dbAdapter.clearStations();
				return true;
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		dbAdapter.close();
	}
}
