package com.rldevel.kotlin

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import rldevl.com.travelsafe.MapActivity
import rldevl.com.travelsafe.R

class MarkerOptionHandler (var googleMap : GoogleMap, var activity: MapActivity) : GoogleMap.OnMarkerClickListener, OnMapReadyCallback{

    init {
        this.declareHandler()
    }

    private fun declareHandler(){

        googleMap.setOnMarkerClickListener {it : Marker

        }

    }

    private fun generateAsociatedOptions(){

        activity.layoutInflater.inflate(R.layout.menu)
    }
}