package dk.itu.mmad.travelapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
	DBHelper helper;
	SQLiteDatabase db;
	Context context;
	SharedPreferences preferences;

	public final static String TRAVELS_TABLE = "travels";
	public final static String STATIONS_TABLE = "stations";

	public DBAdapter(Context context) {
		helper = new DBHelper(context);
		this.context = context;
		preferences = context.getSharedPreferences(context.getPackageName()
				+ "_preferences", Context.MODE_PRIVATE);
	}

	public void open() {
		db = helper.getWritableDatabase();
	}

	public void close() {
		db.close();
	}

	public Cursor getTravels() {
		String limit = preferences.getString("historylength", "10");
		Cursor query = db.query(TRAVELS_TABLE, new String[] { "_id", "start",
				"destination", "start_time", "end_time", "duration", "distance" }, null,
				null, null, null, "_id DESC", limit);
		return query;
	}

	public void saveTravel(String start, String destination, float distance,
			String startTime, String endTime, int duration) {
		ContentValues values = new ContentValues();
		values.put("start", start);
		values.put("destination", destination);
		values.put("distance", distance);
		values.put("start_time", startTime);
		values.put("end_time", endTime);
		values.put("duration", duration);
		db.insert(TRAVELS_TABLE, null, values);
	}

	public void clearTravels() {
		db.delete(TRAVELS_TABLE, null, null);
	}

	public Cursor getStations() {
		Cursor query = db.query(STATIONS_TABLE,
				new String[] { "_id", "station" }, null, null, null, null,
				"station");
		return query;
	}

	public void saveStation(String station) {
		boolean save = preferences.getBoolean("savestations", true);
		if (save) {
			Cursor query = db.query(STATIONS_TABLE, new String[] { "_id" },
					"station = ?", new String[] { station }, null, null, null);
			if (query.getCount() == 0) {

				ContentValues values = new ContentValues();
				values.put("station", station);
				db.insert(STATIONS_TABLE, null, values);
			}
			query.close();
		}
	}

	public void clearStations() {
		db.delete(STATIONS_TABLE, null, null);
	}
}
