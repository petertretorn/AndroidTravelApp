package dk.itu.mmad.travelapp.location;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.EditText;

public class LocationController {
	private Geocoder geocoder;
	private LocationManager lm;
	
	public LocationController(Context context) {
		if (Geocoder.isPresent()) {
			geocoder = new Geocoder(context);
		} else {
			Log.i("TravelApp", "Geocoding is not supported on this device.");
		}
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public Location getPoint(String station) {
		if (geocoder != null) {
			try {
				List<Address> addresses = geocoder.getFromLocationName(station, 1);
				if (addresses.size() > 0) {
					Address a = addresses.get(0);
					Location l = new Location(a.getAddressLine(0));
					l.setLatitude(a.getLatitude());
					l.setLongitude(a.getLongitude());
					return l;
				}
			} catch (IOException ioe) {
				Log.e("TravelApp", "Could not access geocoder.", ioe);
			}
		} 
		return null;
	}
	
	public String getStation(Location location) {
		if(geocoder != null) {
			try {
				Log.i("TravelApp", "Getting station for location " + location.toString());
				List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				if (addresses.size() > 0) {
					Address a = addresses.get(0);
					return a.getLocality();
				}
			} catch (IOException ioe) {
				Log.e("TravelApp", "Could not access geocoder.", ioe);
			}
		}
		return null;
	}
	
	public void findLocation(EditText editText) {
		String provider = lm.getBestProvider(new Criteria(), true);
		Location lastKnownLocation = lm.getLastKnownLocation(provider);
		if(lastKnownLocation == null) {
			lm.requestSingleUpdate(provider, new TravelLocationListener(editText, this), null);
		} else {
			Log.i("dsd", "last known location: " + lastKnownLocation.toString());
			editText.setText(getStation(lastKnownLocation));
		}
		
	}
}
