package dk.itu.mmad.travelapp.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import dk.itu.mmad.travelapp.R;

public class DBHelper extends SQLiteOpenHelper {
	
	public static final int DB_VERSION = 6;
	public static final String DB_NAME = "travel";
	
	public static final String APP_NAME = "TravelApp";
	
	Context context;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
			runSQL(db, R.raw.dbcreate);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		runSQL(db, R.raw.dbupgrade);
		onCreate(db);
	}
	
	private void runSQL(SQLiteDatabase db, int resource) {
		InputStream in = context.getResources().openRawResource(resource);
		InputStreamReader reader = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(reader);
		String sql;
		try {
			while((sql = br.readLine()) != null) {
				db.execSQL(sql);
			}
		} catch (Exception e) {
			Log.e(APP_NAME, e.getMessage());
		}
	}

}
