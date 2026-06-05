package com.example.projetdevandroid;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFragment extends Fragment {

    private TextView txtResult;
    private EditText edtCity;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        txtResult = view.findViewById(R.id.txtResult);
        edtCity = view.findViewById(R.id.edtCity);
        Button btnSearch = view.findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> searchWeather());

        return view;
    }

    private void searchWeather() {

        String city = edtCity.getText().toString().trim();

        if (city.isEmpty()) {
            txtResult.setText("Please enter a city");
            return;
        }

        new Thread(() -> {
            try {

                // 1️⃣ GET LAT/LON FROM CITY
                String geoUrlStr =
                        "https://geocoding-api.open-meteo.com/v1/search?name="
                                + city + "&count=1";

                URL geoUrl = new URL(geoUrlStr);
                HttpURLConnection geoConn =
                        (HttpURLConnection) geoUrl.openConnection();

                BufferedReader geoReader =
                        new BufferedReader(new InputStreamReader(geoConn.getInputStream()));

                StringBuilder geoResult = new StringBuilder();
                String line;

                while ((line = geoReader.readLine()) != null) {
                    geoResult.append(line);
                }

                JSONObject geoJson = new JSONObject(geoResult.toString());

                if (!geoJson.has("results")) {
                    requireActivity().runOnUiThread(() ->
                            txtResult.setText("City not found"));
                    return;
                }

                JSONObject first = geoJson
                        .getJSONArray("results")
                        .getJSONObject(0);

                double lat = first.getDouble("latitude");
                double lon = first.getDouble("longitude");

                // 2️⃣ GET WEATHER
                String weatherUrlStr =
                        "https://api.open-meteo.com/v1/forecast?latitude="
                                + lat + "&longitude=" + lon
                                + "&current_weather=true";

                URL weatherUrl = new URL(weatherUrlStr);
                HttpURLConnection weatherConn =
                        (HttpURLConnection) weatherUrl.openConnection();

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(weatherConn.getInputStream()));

                StringBuilder result = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject weatherJson = new JSONObject(result.toString());

                JSONObject current = weatherJson.getJSONObject("current_weather");

                double temp = current.getDouble("temperature");

                requireActivity().runOnUiThread(() ->
                        txtResult.setText(
                                "City: " + city +
                                        "\nTemperature: " + temp + " °C"
                        ));

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        txtResult.setText("Error: " + e.getMessage()));
            }

        }).start();
    }
}