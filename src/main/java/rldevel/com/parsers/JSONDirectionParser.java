package rldevel.com.parsers;

import android.graphics.Color;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rldevel.helpers.CustomArrayList;
import com.rldevel.helpers.CustomPolyLine;
import com.rldevel.helpers.RouteDefinition;
import com.rldevel.helpers.CustomMarker;
import com.rldevel.helpers.PolyLineHelper;

/**
 * Created by root on 21/11/17.
 */

public class JSONDirectionParser {

    public static final String TAG = "JSONDIRECTIONPARSER";
    private static final String GOOGLE_PROYECT_KEY = "AIzaSyBTkEVP8KBnkXbQxPAvrNoXk78KZLqQj2s";

    private RouteDefinition routeDefinition;
    private CustomMarker marcadorPivot;


    public JSONDirectionParser(RouteDefinition routeDefinition) {

        this.routeDefinition = routeDefinition;
    }


    public JSONDirectionParser(RouteDefinition routeDefinition, CustomMarker marcadorPivot) {

        this.routeDefinition = routeDefinition;
        this.marcadorPivot = marcadorPivot;

    }

    public PolyLineHelper obtenerRespuestaDeServicioGoogle() {

        return obtenerRespuestaDeServicioGoogle(false);

    }

    public PolyLineHelper obtenerRespuestaDeServicioGoogle(boolean isDistanciaAPrimerPunto) {

        PolyLineHelper polyLineHelper = null;
        CustomArrayList<CustomMarker> marcadores = routeDefinition.getColleccionMarcadores();

        CustomMarker marcador1 = null, marcador2 = null;
        String response = null;
        int index = -1;
        if (marcadorPivot != null) {

            index = marcadores.indexOf(marcadorPivot);
            if (index > 0) {

                marcador1 = (CustomMarker) marcadores.get(index - 1);
                marcador2 = marcadorPivot;
                try {

                    response = new DirectionHelper().execute(generarStringConsulta(marcador1, marcador2)).get();
                    polyLineHelper = parseoDeRespuesta(response, marcador1, marcador2, isDistanciaAPrimerPunto);

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            if (index < marcadores.size()) {

                marcador1 = marcadorPivot;
                marcador2 = (CustomMarker) marcadores.get(index + 1);
                try {

                    response = new DirectionHelper().execute(generarStringConsulta(marcador1, marcador2)).get();
                    polyLineHelper = parseoDeRespuesta(response, marcador1, marcador2, isDistanciaAPrimerPunto);

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

            }

        }else{

            marcador1 = (CustomMarker) routeDefinition.getColleccionMarcadores().get(routeDefinition.getColleccionMarcadores().getLastIndex()-1);
            marcador2 = routeDefinition.getColleccionMarcadores().getLastItem();

            try {

                response = new DirectionHelper().execute(generarStringConsulta(marcador1, marcador2)).get();
                polyLineHelper = parseoDeRespuesta(response, marcador1, marcador2, isDistanciaAPrimerPunto);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }

        return polyLineHelper;
    }


    private String generarStringConsulta(CustomMarker marcador1, CustomMarker marcador2) {

        StringBuffer buffer = new StringBuffer();

        buffer.append("origin=" + marcador1.getMarcador().getPosition().latitude + "," + marcador1.getMarcador().getPosition().longitude);
        buffer.append("&destination=" + marcador2.getMarcador().getPosition().latitude + "," + marcador2.getMarcador().getPosition().longitude);
        buffer.append("&key=" + GOOGLE_PROYECT_KEY);

        return buffer.toString();

    }

    private PolyLineHelper parseoDeRespuesta(String response, CustomMarker marcador1, CustomMarker marcador2,  boolean isPosicionDistanciaAprimerPunto) {

        PolyLineHelper polyLineHelper = routeDefinition.getPolyLineHelper();

        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray routes = jsonObject.getJSONArray("routes");
            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
            JSONArray steps;
            JSONObject end_location, start_location;

            for (int index = 0; index < legs.length(); index++) {
                JSONObject leg_item = legs.getJSONObject(index);
                steps = leg_item.getJSONArray("steps");

                for (int jindex = 0; jindex < steps.length(); jindex++) {
                    end_location = steps.getJSONObject(jindex).getJSONObject("end_location");
                    start_location = steps.getJSONObject(jindex).getJSONObject("start_location");

                    Polyline polyline = routeDefinition.getGoogleMap().addPolyline(new PolylineOptions()
                            .add(new LatLng(start_location.getDouble("lat"), start_location.getDouble("lng")),
                                    new LatLng(end_location.getDouble("lat"), end_location.getDouble("lng"))));

                    if (isPosicionDistanciaAprimerPunto) {

                        polyline.setColor(Color.GREEN);
                        polyline.setWidth(6.0f);

                    }

                    // Referencia cruzada, las polilineas nos dicen cuales son sus marcadores de inicio y fin
                    CustomPolyLine customPolyLine = new CustomPolyLine(polyline);
                    customPolyLine.getColleccionMarcadores().add(marcador1);
                    customPolyLine.getColleccionMarcadores().add(marcador2);
                    customPolyLine.setRespuestaServicio(response);

                    polyLineHelper.getPolilineas().add(customPolyLine);

                }
            }


        } catch (JSONException ex) {

            Log.e(TAG, ex.getMessage());
        }

        return polyLineHelper;

    }

}
