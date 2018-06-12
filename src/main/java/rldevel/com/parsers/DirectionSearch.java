package rldevel.com.parsers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by root on 03/12/17.
 */

public class DirectionSearch {

    public static final String TAG = "DIRECTIONSEARCH";


    public List<Address> searchDirectionByText(String locationName, Context context){

        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(locationName, 4);
        }catch (IOException ex){
            Log.e(TAG, ex.getMessage());
        }
        return addresses;
    }

}