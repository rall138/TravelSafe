package com.rldevel.helpers;

import android.util.Log;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.UUID;

/**
 * Created by root on 28/12/17.
 */

public class CustomMarker{

    public static final String TAG = "MARCRUT";

    private UUID identificador;
    private int enumerador;
    private Marker marcador;

    public CustomMarker(Marker marcador, int enumeradorAnterior){

        this.identificador = UUID.randomUUID();
        this.marcador = marcador;
        this.enumerador = enumeradorAnterior + 1 ;

    }

    //region Getter and Setters

    public Marker getMarcador() {
        Marker marcador_auxiliar = this.marcador;
        return marcador_auxiliar;
    }


    public UUID getIdentificador() {
        return identificador;
    }

    public int getEnumerador() {
        return enumerador;
    }

    //endregion

    public void toLog(){
        String string = "==== CUSTOM DEBUG ==="+System.getProperty("line.separator");
        string += "Identificador: "+this.getIdentificador()+System.getProperty("line.separator");
        string += ", enumerador: "+this.getEnumerador()+System.getProperty("line.separator");
        Log.d(TAG, string);
    }

}
