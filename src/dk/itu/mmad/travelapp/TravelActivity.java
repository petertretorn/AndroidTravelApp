// Solution by Peter Bruijn Larsen


package dk.itu.mmad.travelapp;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import dk.itu.mmad.travelapp.db.DBAdapter;
import dk.itu.mmad.travelapp.external.TravelSubmissionTask;
import dk.itu.mmad.travelapp.fragments.InviteFragment;
import dk.itu.mmad.travelapp.location.LocationController;

public class TravelActivity extends Activity {

	private static final String UNKNOWN_STATION = "Unknown station";
	public final static String LAST_START = "lastStart";
	public final static String LAST_DESTINATION = "lastDestination";

	public final static String CHECKIN_TXT = "checkinTxt";
	public final static String CHECKOUT_TXT = "checkoutTxt";

	public final static String CHECKED_IN = "checkedIn";

	public final static int SELECT_CHECKIN_STATION = 1;
	public final static int SELECT_CHECKOUT_STATION = 2;
	public final static String SELECTED_STATION_NAME = "selectedStationName";

	public final static String EXPIRES = "expires";

	public final static String APP_IN_USE = "app in use";
	
	public final static String CHECK_IN_TIME = "checkInTime";
	public final static String CHECK_OUT_TIME = "checOutTime";
	
	public final static String CHECK_IN_STAMP = "checkInStamp";
	public final static String CHECK_OUT_STAMP = "checkOutStamp";
	
	public final static String PASSENGERS = "passengers";

	// field for receipt message, accessible by getter for UI testing
	private String dialogMessage;

	private EditText checkinTxt;
	private EditText checkoutTxt;
	private Button checkinButton;
	private Button checkoutButton;
	private Button checkinSelect;
	private Button checkoutSelect;
	private ImageView travelAppImage;

	private Button receiptButton;
	private Spinner passengersSpinner;
	private int passengers;

	private Button checkinHere;
	private Button checkoutHere;

	private Button priceButton;

	private boolean checkedIn;

	private String lastStart;
	private String lastDestination;

	private String checkInTimeStamp;
	private String checkOutTimeStamp;
	private Date startTime;
	
	private Date endTime;

	private DBAdapter dbAdapter;

	private LocationController locationController;

	// These won't be saved during a screen orientation change. We'll live with
	// that.
	private Location startLocation;
	private Location destinationLocation;

	private SharedPreferences preferences;
	private AlarmManager alarmManager;
	private PendingIntent noticeIntent;
	private PendingIntent expiryIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel);

		//instantiate start time to avoid null pointer exception at rare edge cases
		startTime = new Date(0);
		
		alarmManager = (AlarmManager) getBaseContext().getSystemService(
				ALARM_SERVICE);
		preferences = getSharedPreferences("travel", MODE_PRIVATE);

		dbAdapter = new DBAdapter(this);
		dbAdapter.open();

		checkinTxt = (EditText) findViewById(R.id.checkintxt);
		checkoutTxt = (EditText) findViewById(R.id.checkouttxt);
		checkinButton = (Button) findViewById(R.id.checkinbutton);
		checkoutButton = (Button) findViewById(R.id.checkoutbutton);

		priceButton = (Button) findViewById(R.id.pricebutton);

		checkinHere = (Button) findViewById(R.id.checkinhere);
		checkoutHere = (Button) findViewById(R.id.checkouthere);
		receiptButton = (Button) findViewById(R.id.receiptbutton);

		// disable receipt button if the app has not been in use previously
		if (preferences.getBoolean(APP_IN_USE, false) == false) {
			receiptButton.setEnabled(false);
			preferences.edit().putBoolean(APP_IN_USE, true).commit();
		}

		// attach a listener to the receipt button utilizing anonymous class  
		receiptButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog(0);
			}
		});
		
		// get a  reference to the spinner view
		passengersSpinner = (Spinner) findViewById(R.id.spinner);

		checkinButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkinTxt.getText().length() > 0) {
					checkin(checkinTxt.getText().toString());
				} else {
					TestableToast.makeText(TravelActivity.this,
							"Enter a check-in station.", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		checkoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkoutTxt.getText().length() > 0) {
					checkout(checkoutTxt.getText().toString());
				} else {
					displayToast("Enter a check-out station.");
				}
			}
		});

		checkinSelect = (Button) findViewById(R.id.checkinselect);
		checkoutSelect = (Button) findViewById(R.id.checkoutselect);
		checkinSelect.setOnClickListener(new SelectButtonListener(
				SELECT_CHECKIN_STATION));
		checkoutSelect.setOnClickListener(new SelectButtonListener(
				SELECT_CHECKOUT_STATION));

		if (checkinHere != null) {
			checkinHere.setOnClickListener(new HereButtonListener(checkinTxt));
		}
		if (checkoutHere != null) {
			checkoutHere
					.setOnClickListener(new HereButtonListener(checkoutTxt));
		}

		if (priceButton != null) {
			priceButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getBaseContext(),
							PriceListActivity.class);
					startActivity(intent);
				}
			});
		}

		travelAppImage = (ImageView) findViewById(R.id.travelappimage);
		if (travelAppImage != null) {
			travelAppImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(TravelActivity.this,
							WebActivity.class);
					startActivity(intent);
				}
			});
		}

	}

	/*
	 * callback method invoked for creating a dialog when showDialog(int num) is
	 * called
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			dialogMessage = "";
			checkedIn = preferences.getBoolean(CHECKED_IN, false);
			if (checkedIn) {
				dialogMessage = "Current travel is from "
						+ checkinTxt.getText().toString()
						+ ", travel started at " + 
						DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format( startTime );
			} else {
				dialogMessage = "Last travel was from " + lastStart + " at "
						+ checkInTimeStamp + " to " + lastDestination + " at "
						+ checkOutTimeStamp;
			}
			return new AlertDialog.Builder(this)
					.setTitle("Receipt")
					.setMessage(dialogMessage)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									removeDialog(0);
								}
							}).create();
		}
		return null;
	}

	private void updateFields() {
		checkinTxt.setEnabled(!checkedIn);
		checkoutTxt.setEnabled(checkedIn);
		checkinButton.setEnabled(!checkedIn);
		checkoutButton.setEnabled(checkedIn);
		checkinSelect.setEnabled(!checkedIn);
		checkoutSelect.setEnabled(checkedIn);
		if (checkinHere != null) {
			checkinHere.setEnabled(!checkedIn);
		}
		if (checkoutHere != null) {
			checkoutHere.setEnabled(checkedIn);
		}
	}

	private void checkin(String station) {
		checkedIn = true;

		startTime = new Date();

		updateFields();

		if (receiptButton.isEnabled() == false) {
			receiptButton.setEnabled(true);
		}

		passengers = Integer.parseInt((String) passengersSpinner
				.getSelectedItem());
		dbAdapter.saveStation(checkinTxt.getText().toString());

		startLocation = locationController.getPoint(station);

		preferences.edit().putBoolean(CHECKED_IN, checkedIn)
				.putString(LAST_START, station).commit();
		setupExpiry();
	}

	private void checkout(String station) {

		endTime = new Date();
		
		// format the date instances as strings, more suitable for rendering and persistence
		checkOutTimeStamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
				.format( endTime );
		checkInTimeStamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
				.format( startTime );
		
		lastStart = preferences.getString(LAST_START, UNKNOWN_STATION);
		lastDestination = station;

		destinationLocation = locationController.getPoint(station);

		checkinTxt.setText("");
		checkoutTxt.setText("");

		checkedIn = false;
		updateFields();

		dbAdapter.saveStation(lastDestination);

		float distance = 0;
		
		if (destinationLocation != null && startLocation != null) {
			distance = destinationLocation.distanceTo(startLocation) / 1000;
		}

		// call helper method to calculate the difference in minutes
		int duration = minutesDifference(startTime, endTime);
	
		dbAdapter.saveTravel(lastStart, lastDestination, distance, checkInTimeStamp, checkOutTimeStamp, duration);

		// utilizing the builder API
		preferences.edit().putBoolean(CHECKED_IN, checkedIn)
				.putString(LAST_START, null)
				.putLong(EXPIRES, Long.MAX_VALUE)
				.commit();

		new TravelSubmissionTask(this, lastStart, lastDestination, passengers)
				.execute();

		passengers = 1;
		
		// reinitialize spinner to default
		passengersSpinner.setSelection(0);
		
		cancelExpiry();
	}

	private void displayToast(String txt) {
		Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
	}
	
	/* helper method to calculate the time difference in minutes 
	 * between two Date object
	 */
	public static int minutesDifference( Date earlier, Date later ) {
		
        if( earlier == null || later == null ) return 0;
        
        return (int)((later.getTime() / (1000*60)) - (earlier.getTime() /(1000*60) ) );
    }
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem item = menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "History");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		item = menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "Settings");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		item = menu.add(Menu.NONE, Menu.FIRST + 2, Menu.NONE, "Invite friends");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		item = menu.add(Menu.NONE, Menu.FIRST + 3, Menu.NONE, "Notify friend");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case Menu.FIRST:
			intent = new Intent(this, HistoryActivity.class);
			startActivity(intent);
			break;
		case Menu.FIRST + 1:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		case Menu.FIRST + 2:
			intent = new Intent(this, InviteActivity.class);
			startActivity(intent);
			break;
		case Menu.FIRST + 3:
			notifyFriend();
		}
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(LAST_START, lastStart);
		outState.putString(LAST_DESTINATION, lastDestination);

		outState.putString(CHECKIN_TXT, checkinTxt.getText().toString());
		outState.putString(CHECKOUT_TXT, checkoutTxt.getText().toString());

		outState.putBoolean(CHECKED_IN, checkedIn);
		
		outState.putInt(PASSENGERS, passengers);
		
		// keep time states at configuration changes, i.e landscape mode
		if (checkedIn) {
			outState.putString(CHECK_IN_TIME, startTime.toString());
		}
		else {
			outState.putString(CHECK_IN_STAMP, checkInTimeStamp);
			outState.putString(CHECK_OUT_STAMP, checkOutTimeStamp);
		}
		
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
		lastStart = savedInstanceState.getString(LAST_START);
		lastDestination = savedInstanceState.getString(LAST_DESTINATION);

		// restore values
		checkinTxt.setText(savedInstanceState.getString(CHECKIN_TXT));
		checkoutTxt.setText(savedInstanceState.getString(CHECKOUT_TXT));

		checkedIn = savedInstanceState.getBoolean(CHECKED_IN);
		
		passengers = savedInstanceState.getInt(PASSENGERS, 1);
		
		passengersSpinner.setSelection(passengers - 1);
		
		// fetch time states at configuration changes, i.e landscape mode
		if (checkedIn) {
			startTime = new Date( savedInstanceState.getString(CHECK_IN_TIME) );
		}
		else {
			checkInTimeStamp = savedInstanceState.getString(CHECK_IN_STAMP);
			checkOutTimeStamp = savedInstanceState.getString(CHECK_OUT_STAMP);
		}
		
		updateFields();
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationController = new LocationController(this);

		if (preferences.getLong(EXPIRES, Long.MAX_VALUE) < System
				.currentTimeMillis()) {
			preferences.edit().putLong(EXPIRES, Long.MAX_VALUE).commit();
			checkout(UNKNOWN_STATION);
			displayToast("Your ticket has expired, so you are no longer checked in.");
		}

		checkedIn = preferences.getBoolean(CHECKED_IN, false);

		updateFields();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String selectedStation = data.getStringExtra(SELECTED_STATION_NAME);
			switch (requestCode) {
			case SELECT_CHECKIN_STATION:
				checkinTxt.setText(selectedStation);
				break;
			case SELECT_CHECKOUT_STATION:
				checkoutTxt.setText(selectedStation);
				break;
			}
		}

	}

	private void notifyFriend() {
		Uri smsUri = Uri.parse("content://sms/inbox");

		Cursor cursor = getContentResolver().query(smsUri, null, "body = ?",
				new String[] { InviteFragment.INVITE_MESSAGE }, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String address = cursor.getString(cursor
					.getColumnIndexOrThrow("address"));
			SmsManager.getDefault().sendTextMessage(address, null,
					"TravelApp downloaded", null, null);
			Toast.makeText(this,
					"Your friend " + address + " has been notified!",
					Toast.LENGTH_SHORT).show();
			cursor.close();
		} else {
			Toast.makeText(this, "Did not find any invite. :(",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void setupExpiry() {
		long now = System.currentTimeMillis();
		long notice = now + (ExpiryReceiver.NOTICE_MINUTES * 60 * 1000);
		long expiry = now + (ExpiryReceiver.EXPIRY_MINUTES * 60 * 1000);

		Intent newIntent = new Intent(getBaseContext(), ExpiryReceiver.class);
		newIntent.putExtra(ExpiryReceiver.STATUS, ExpiryReceiver.NOTICE);
		noticeIntent = PendingIntent.getBroadcast(getBaseContext(), 0,
				newIntent, 0);
		newIntent = new Intent(getBaseContext(), ExpiryReceiver.class);
		newIntent.putExtra(ExpiryReceiver.STATUS, ExpiryReceiver.EXPIRY);
		expiryIntent = PendingIntent.getBroadcast(getBaseContext(), 1,
				newIntent, 0);

		preferences.edit().putLong(EXPIRES, expiry).commit();

		alarmManager.set(AlarmManager.RTC_WAKEUP, notice, noticeIntent);
		alarmManager.set(AlarmManager.RTC_WAKEUP, expiry, expiryIntent);
	}

	private void cancelExpiry() {
		if (noticeIntent != null && expiryIntent != null) {
			alarmManager.cancel(noticeIntent);
			alarmManager.cancel(expiryIntent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbAdapter.close();
	}

	class SelectButtonListener implements View.OnClickListener {

		private int code;

		public SelectButtonListener(int code) {
			this.code = code;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(TravelActivity.this,
					SelectStationActivity.class);
			startActivityForResult(intent, code);
		}

	}

	class HereButtonListener implements OnClickListener {

		private EditText editText;

		public HereButtonListener(EditText editText) {
			this.editText = editText;
		}

		@Override
		public void onClick(View v) {
			locationController.findLocation(editText);
		}
	}

	
	// getter for dialog message used in UI test
	public String getDialogMessage() {
		return dialogMessage;
	}
	
	// getters for time fields used in testing the receipt toasts
	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

}
