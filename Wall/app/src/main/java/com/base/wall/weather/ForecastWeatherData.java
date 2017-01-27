package com.base.wall.weather;

import java.util.ArrayList;
import java.util.List;

public class ForecastWeatherData {

    private List<WeatherData> data;

    public ForecastWeatherData() {

        data = new ArrayList<>();
    }

    public void add(WeatherData weatherData) {

        data.add(weatherData);
    }

    public WeatherData getCurrent() {

        long now = System.currentTimeMillis();
        long delay = Math.abs(now - data.get(0).getTime());

        int index = 0;
        int count = data.size();
        for (int i = 1; i < count; i++) {
            long nextDelay = Math.abs(now - data.get(i).getTime());
            if (nextDelay < delay) {
                delay = nextDelay;
                index = i;
            }
        }

        return data.get(index);
    }

    public List<WeatherData> getData() {

        return data;
    }
}
