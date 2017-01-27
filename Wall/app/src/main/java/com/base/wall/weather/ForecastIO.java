package com.base.wall.weather;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class ForecastIO {

    public enum Icon {
        CLEAR, RAIN, SNOW, CLOUDY, PARTLY_CLOUDY, WIND, FOG, SLEET
    }

    private static final long API_TIME_OFFSET = 1000L; //forecast.io returns 9-digit time, java returns 13-digit time (for next couple centuries)

    private static final String FORECAST_IO_API = "https://api.forecast.io/forecast";
    private static final String FIO_CURRENTLY = "currently";
    private static final String FIO_HOURLY = "hourly";
    private static final String FIO_DATA = "data";

    //clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night
    private static final String CLEAR_DAY = "clear-day";
    private static final String CLEAR_NIGHT = "clear-night";
    private static final String RAIN = "rain";
    private static final String SNOW = "snow";
    private static final String CLOUDY = "cloudy";
    private static final String PARTLY_CLOUDY_DAY = "partly-cloudy-day";
    private static final String PARTLY_CLOUDY_NIGHT = "partly-cloudy-night";
    private static final String WIND = "wind";
    private static final String FOG = "fog";
    private static final String SLEET = "sleet";

    private final String apiKey;

    private WeatherDataListener weatherListener;
    private String location;

    public ForecastIO(@NonNull String apiKey) {
        this.apiKey = apiKey;
        location = "0,0";
    }

    public void setWeatherListener(WeatherDataListener listener) {
        this.weatherListener = listener;
    }

    public void setLocation(String lat, String lon) {

        if (lat == null || lon == null) {
            return;
        }

        location = String.format(Locale.US, "%s,%s", lat, lon);
    }

    public String getApiUrl() {

        return String.format(Locale.US, "%s/%s/%s", FORECAST_IO_API, apiKey, location);
    }

    public void requestUpdate() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                ForecastWeatherData forecastData = null;

                byte[] response = downloadFile(getApiUrl());
                if (response != null) {
                    String json = new String(response);

                    try {
                        forecastData = new ForecastWeatherData();
                        JSONObject data = new JSONObject(json);
                        if (data.has(FIO_CURRENTLY)) {
                            JSONObject currently = data.getJSONObject(FIO_CURRENTLY);
                            forecastData.add(parseData(currently));
                        }

                        if (data.has(FIO_HOURLY)) {
                            JSONObject hourlyData = data.getJSONObject(FIO_HOURLY);
                            if (hourlyData.has(FIO_DATA)) {
                                JSONArray hourly = hourlyData.getJSONArray(FIO_DATA);
                                int count = hourly.length();
                                for (int i = 0; i < count; i++) {
                                    forecastData.add(parseData(hourly.getJSONObject(i)));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (weatherListener != null) {
                    weatherListener.onWeatherChanged(forecastData);
                }

                return null;
            }
        }.execute();
    }

    private WeatherData parseData(JSONObject json) throws JSONException {

        WeatherData data = new WeatherData();

        data.setTime(json.getLong("time") * API_TIME_OFFSET);
        data.setTemperature(json.getDouble("temperature"));
        data.setIcon(getIconEnum(json.getString("icon")));
        data.setWindSpeed(json.getDouble("windSpeed"));

        return data;
    }

    private Icon getIconEnum(String icon) {

        switch (icon) {
            case CLEAR_DAY:
            case CLEAR_NIGHT:
                return Icon.CLEAR;
            case PARTLY_CLOUDY_DAY:
            case PARTLY_CLOUDY_NIGHT:
                return Icon.PARTLY_CLOUDY;
            case CLOUDY:
                return Icon.CLOUDY;
            case RAIN:
                return Icon.RAIN;
            case SNOW:
                return Icon.SNOW;
            case WIND:
                return Icon.WIND;
            case FOG:
                return Icon.FOG;
            case SLEET:
                return Icon.SLEET;
            default:
                return Icon.CLEAR;
        }
    }

    private byte[] downloadFile(String fileUrl) {

        byte[] result = null;

        try {
            URL url = new URL(fileUrl);

            BufferedInputStream is = new BufferedInputStream(url.openConnection().getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int b;
            byte[] bar = new byte[4096];
            while ((b = is.read(bar)) != -1) {
                bos.write(bar, 0, b);
            }

            result = bos.toByteArray();

            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static interface WeatherDataListener {

        void onWeatherChanged(ForecastWeatherData data);
    }
}
