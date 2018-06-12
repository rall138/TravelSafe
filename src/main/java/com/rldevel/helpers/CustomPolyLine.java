package com.rldevel.helpers;

import com.google.android.gms.maps.model.Polyline;

/**
 * Created by root on 29/12/17.
 */

public class CustomPolyLine {

    private Polyline polyline;
    private CustomArrayList<CustomMarker> colleccionMarcadores = new CustomArrayList<>();
    private String respuestaServicio;

    public CustomPolyLine(Polyline polyline){

        this.polyline = polyline;
    }

    //region Getters and Setters

    public String getRespuestaServicio() {
        return respuestaServicio;
    }

    public void setRespuestaServicio(String respuestaServicio) {
        this.respuestaServicio = respuestaServicio;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public CustomArrayList<CustomMarker> getColleccionMarcadores() {
        return colleccionMarcadores;
    }

    //endregion

}
