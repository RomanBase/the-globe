package com.base.wall.listener;

import com.base.wall.weather.WeatherData;

public interface WeatherChangedListener {

    void onWeatherChanged(WeatherData data);
}
