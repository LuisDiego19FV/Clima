package com.example.luisd.clima;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class Activity_Weather extends AppCompatActivity {

    private NetworkUtils network;
    private String b;
    private JSONObject res;
    private JSONObject main;

    private TextView txtTemp;
    private TextView txtTempMin;
    private TextView txtTempMax;
    private TextView txtCloudy;
    private TextView txtWindy;
    private ImageView iwindy;
    private ImageView icloudy;
    private ImageView isunny;

    private double temp;
    private double tempMax;
    private double tempMin;
    private double windVel;
    private double cloudPer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__weather);

        txtTemp = (TextView) findViewById(R.id.txtTemp);
        txtTempMin = (TextView) findViewById(R.id.txtMin);
        txtTempMax = (TextView) findViewById(R.id.txtMax);
        txtCloudy = (TextView) findViewById(R.id.txtCloudy);
        txtWindy = (TextView) findViewById(R.id.txtWind);
        icloudy = (ImageView) findViewById(R.id.imageClouds);
        isunny = (ImageView) findViewById(R.id.imageSunny);
        iwindy = (ImageView) findViewById(R.id.imageWind);

        network = new NetworkUtils();

        String countryCode = getUserCountry(getBaseContext());
        countryCode = countryCode.toUpperCase();
        Locale loc = new Locale("",countryCode);

        URL SearchUrl = network.buildUrl(loc.getDisplayCountry());
        new QueryTask().execute(SearchUrl);

    }

    public class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {

            String resultado = "";
            try {
                resultado = network.getResponseFromHttpUrl(params[0]);
            }catch (Exception e){
                e.printStackTrace();
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")){
                b = s;
                try {

                    res = new JSONObject(b);
                    main = res.getJSONObject("main");
                    JSONObject clouds = res.getJSONObject("clouds");
                    JSONObject wind = res.getJSONObject("wind");


                    temp = Double.parseDouble(main.getString("temp"));
                    tempMax = Double.parseDouble(main.getString("temp_max"));
                    tempMin = Double.parseDouble(main.getString("temp_min"));
                    cloudPer = Double.parseDouble(clouds.getString("all"));
                    windVel = Double.parseDouble(wind.getString("speed"));



                    txtTemp.setText(Double.toString(temp - 273.15).substring(0,4) + " C");
                    txtTempMin.setText("Min : " + Double.toString(tempMin - 275.15).substring(0,4) + " C");
                    txtTempMax.setText("Max : " + Double.toString(tempMax - 271.15).substring(0,4) + " C");
                    txtCloudy.setText("Clouds : " + cloudPer + "%");
                    txtWindy.setText("Winds : " + windVel);

                    if (cloudPer > 50){
                        icloudy.setVisibility(View.VISIBLE);
                        iwindy.setVisibility(View.INVISIBLE);
                        isunny.setVisibility(View.INVISIBLE);
                    }

                    else if (windVel > 15) {
                        iwindy.setVisibility(View.VISIBLE);
                        icloudy.setVisibility(View.INVISIBLE);
                        isunny.setVisibility(View.INVISIBLE);
                    }

                    else{
                        isunny.setVisibility(View.VISIBLE);
                        iwindy.setVisibility(View.INVISIBLE);
                        icloudy.setVisibility(View.INVISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            super.onPostExecute(s);
        }
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return null;
    }

}
