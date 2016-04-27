package com.base.wall.weather;

import java.util.Locale;

public class WeatherData {

    private ForecastIO.Icon icon;
    private long time;
    private double temperature;
    private double windSpeed;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getTemperatureC() {
        return (temperature - 32.0) * 5.0 / 9.0;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public ForecastIO.Icon getIcon() {
        return icon;
    }

    public void setIcon(ForecastIO.Icon icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "time: %s\ntemperature: %s\nwind: %s\nclouds: %s", time, getTemperatureC(), windSpeed, icon);
    }
}
