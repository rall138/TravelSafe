package rldevl.com.travelsafe;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by root on 17/09/17.
 */

public class LocationListenerImpl implements LocationListener {

    public static String TAG = "LS/LocationListener";
    private LatLng currentLatLng;

    @Override
    public void onLocationChanged(Location location) {
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        GeneralHelper.generateMessage(TAG, location.getLatitude()+" "+location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }
}
