package dk.itu.mmad.travelapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
	public SMSReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Object[] pdus = (Object[]) extras.get("pdus");
			SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[0]);
			String sender = message.getOriginatingAddress();
			String origMessage = message.getMessageBody();
			if (origMessage.startsWith("TravelApp")) {
				// Keep the original message from being delivered to the SMS
				// system
				abortBroadcast();

				Toast.makeText(context, "Great, your friend " + sender + " just downloaded the app!", Toast.LENGTH_SHORT).show();
			}

		}
	}
}
