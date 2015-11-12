package dk.itu.mmad.travelapp.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.EditText;

public class TravelLocationListener implements LocationListener {
	private EditText editText;
	private LocationController locationController;

	public TravelLocationListener(EditText editText, LocationController locationController) {
		this.editText = editText;
		this.locationController = locationController;
	}

	@Override
	public void onLocationChanged(Location location) {
		String station = locationController.getStation(location);
		if (station != null) {
			editText.setText(station);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}