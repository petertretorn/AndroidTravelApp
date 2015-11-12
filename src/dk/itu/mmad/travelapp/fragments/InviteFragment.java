package dk.itu.mmad.travelapp.fragments;

import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class InviteFragment extends ListFragment {

	public static final String INVITE_MESSAGE = "Hey, please download this great new app, The Travel App!";
	
	ContentResolver contentResolver;
	SimpleCursorAdapter cursorAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		contentResolver = getActivity().getContentResolver();

		// The code below can be used for a blocking call, but the CursorLoader is encouraged.
		
		// Cursor cursor = contentResolver.query(
		// ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
		// null, ContactsContract.Contacts.DISPLAY_NAME);
		
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				ContactsContract.Contacts.CONTENT_URI, null, null,
				null, ContactsContract.Contacts.DISPLAY_NAME);
		Cursor cursor = cursorLoader.loadInBackground();
		cursorAdapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_1, cursor,
				new String[] { ContactsContract.Contacts.DISPLAY_NAME },
				new int[] { android.R.id.text1 }, 0);

		setListAdapter(cursorAdapter);

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Cursor cursor = cursorAdapter.getCursor();
		String hasPhone = cursor
				.getString(cursor
						.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
		if (Integer.parseInt(hasPhone) == 1) {
			String personId = cursor.getString(cursor
					.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
			Cursor personCursor = contentResolver.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + personId,
					null, null);
			while (personCursor.moveToNext()) {
				String phoneNo = personCursor
						.getString(personCursor
								.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(phoneNo, null,
						INVITE_MESSAGE, null, null);
				Toast.makeText(getActivity(), "SMS sent to " + phoneNo,
						Toast.LENGTH_SHORT).show();
			}
			personCursor.close();

		}
	}

}
