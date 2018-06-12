package rldevel.com.parsers;

import android.content.Context;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by root on 21/11/17.
 */

class DirectionHelper extends AsyncTask<String, String, String>{

    public static final String TAG = "DIRECTIONHELPER";

    @Override
    protected String doInBackground(String... params) {

        BufferedReader reader;
        StringBuffer buffer = null;

        try {

            HttpsURLConnection httpsURLConnection;

            String parameters = "";
            URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?" + params[0]);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.connect();

            InputStream inputStream = httpsURLConnection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            buffer = new StringBuffer();
            String line;
            while(((line = reader.readLine()) != null)){
                buffer.append(line+"\n");
            }

            return buffer.toString();

        }catch (IOException ex){

            Log.e(TAG, ex.getMessage());
        }

        return null;
    }

}
