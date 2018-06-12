package com.rldevel.helpers;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

import rldevl.com.travelsafe.GeneralHelper;

/**
 * Created by root on 23/11/17.
 */

public class PolyLineHelper {

    private static final String TAG = "POPH";
    private CustomArrayList<CustomPolyLine> polilineas = new CustomArrayList<>();

    public PolyLineHelper(){}

    public void removerPolilineaEntreMarcadorPivot(CustomMarker marcadorPivot){

        for(int index = 0; index < polilineas.size(); index++){

            if (((CustomPolyLine)polilineas.get(index)).getColleccionMarcadores().getLastItem().getIdentificador().compareTo(marcadorPivot.getIdentificador())==0 ||
                    ((CustomPolyLine)polilineas.get(index)).getColleccionMarcadores().getFirstItem().getIdentificador().compareTo(marcadorPivot.getIdentificador())==0)
                ((CustomPolyLine)polilineas.get(index)).getPolyline().remove();

        }
    }

    public void removeAll(){

        for(Object polyLine: getPolilineas()){

            ((CustomPolyLine)polyLine).getPolyline().remove();

        }

    }

    //region Getter and Setters

    public CustomArrayList<CustomPolyLine> getPolilineas() {
        return polilineas;
    }

    //endregion

}
