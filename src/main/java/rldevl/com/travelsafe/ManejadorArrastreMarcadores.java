package rldevl.com.travelsafe;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.rldevel.helpers.CustomMarker;
import com.rldevel.helpers.RouteDefinition;

/**
 * Created by root on 06/02/18.
 */

public class ManejadorArrastreMarcadores implements GoogleMap.OnMarkerDragListener {

    public static final String TAG = "MARRM";
    private RouteDefinition routeDefinition;

    public ManejadorArrastreMarcadores(RouteDefinition routeDefinition){

        this.routeDefinition = routeDefinition;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        GeneralHelper.generateMessage(TAG, "Marcador: "+marker.getId()+",cantidad de elementos: "+ routeDefinition.getColleccionMarcadores().size());


        boolean itemEncontrado = false;
        int index = 0;
        while (index < routeDefinition.getColleccionMarcadores().size() && !itemEncontrado) {

            if (marker.getId() == routeDefinition.getColleccionMarcadores().get(index)){
              itemEncontrado = true;
            }else{
                index++;
            }
        }

        if (itemEncontrado)
            routeDefinition.getPolyLineHelper()
                    .removerPolilineaEntreMarcadorPivot((CustomMarker) routeDefinition.getColleccionMarcadores().get(index));

    }
}
