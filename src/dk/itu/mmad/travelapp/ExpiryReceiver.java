package dk.itu.mmad.travelapp;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ExpiryReceiver extends BroadcastReceiver {
	
	public static final String STATUS = "status";
	public static final int NOTICE = 0;
	public static final int EXPIRY = 1;
	
	public static final int NOTICE_MINUTES = 8;
	public static final int EXPIRY_MINUTES = 10;
	
	public ExpiryReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = null;
		switch (intent.getIntExtra(STATUS, 0)) {
		case NOTICE:
			notification = makeNotification(context, "Reminder", "Your ticket is running out!");
			break;
		case EXPIRY:
			notification = makeNotification(context, "Expiry", "Your ticket has expired!");
			break;
		}
		nm.notify(1, notification);
	}
	
	private Notification makeNotification(Context context, String title, String text) {
		Intent intent = new Intent(context, TravelActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		
		Builder builder = new Notification.Builder(context);
		builder.setContentTitle(title);
		builder.setContentText(text);
		builder.setSmallIcon(R.drawable.ic_stat_new_message);
		builder.setContentIntent(pendingIntent);
		builder.setAutoCancel(true);
		// Deprecated, but the replacement method build() will only work on API 16 and above. 
		Notification notification = builder.getNotification();
		return notification;
	}
}
