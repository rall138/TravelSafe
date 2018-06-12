package com.rldevel.helpers;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Instancia de singleton para manejo de marcadores.
 * Singleton instance for markers managment.
 *
 * Created by root on 22/11/17.
 */

public class MarkerHelper {

    private static MarkerHelper instance;
    private static ArrayList<MarkerOptions> markerOption_list;
    private static ArrayList<Marker> marker_list;

    private MarkerHelper(){}

    public static MarkerHelper getInstance(){
        if (instance == null){
            instance = new MarkerHelper();
            markerOption_list = new ArrayList<>();
            marker_list = new ArrayList<>();
        }
        return instance;
    }

    public static ArrayList<MarkerOptions> getMarkerOption_list() {
        return markerOption_list;
    }
    public static ArrayList<Marker> getMarker_list() {return marker_list; }

}
