package com.base.wall.globe;

import android.content.IntentFilter;
import android.content.SharedPreferences;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseObject;
import com.base.lib.engine.BaseRender;
import com.base.lib.engine.BaseRenderer;
import com.base.lib.engine.common.Colorf;
import com.base.lib.engine.other.dev.FpsBar;
import com.ankhrom.wall.globe.ActivityMain;
import com.base.wall.ConnectionHandler;
import com.base.wall.Shaders;
import com.base.wall.config.ConfigPrefs;
import com.base.wall.listener.ColorChangedListener;
import com.base.wall.listener.CredinalsChangedListener;
import com.base.wall.listener.NetworkConnectedListener;
import com.base.wall.listener.WeatherChangedListener;
import com.base.wall.listener.WeatherUpdateRequested;
import com.base.wall.weather.CoarseLocation;
import com.base.wall.weather.ForecastIO;
import com.base.wall.weather.ForecastWeatherData;
import com.base.wall.weather.WeatherData;

import java.util.ArrayList;
import java.util.List;

public class GlobeAloc extends BaseObject implements ForecastIO.WeatherDataListener, CoarseLocation.LocationObtainedListener, WeatherUpdateRequested, NetworkConnectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String FORECAST_IO_API_KEY = "de9fb13d12571d6dae553e24869a2f6e";

    private final List<ColorChangedListener> colorChangedListeners;
    private final List<WeatherChangedListener> weatherChangedListeners;
    private final ForecastIO forecast;

    private final Colorf currentWeatherColor;

    private ForecastWeatherData forecastData;
    private WeatherData currentWeatherData;
    private long lastWeatherChangeTime;

    private ConnectionHandler connectionReceiver;
    private CredinalsChangedListener credinalsChangedListener;

    public GlobeAloc(Base base) {
        super(base);

        colorChangedListeners = new ArrayList<>();
        weatherChangedListeners = new ArrayList<>();

        currentWeatherColor = new Colorf();

        forecast = new ForecastIO(FORECAST_IO_API_KEY);
        forecast.setWeatherListener(this);

        requestWeatherUpdate(true);
    }

    public GlobeAloc init() {

        Shaders.init(base.factory);
        BaseRenderer render = base.render;

        TheGlobe globe = new TheGlobe(base);
        GlobeRing ring = new GlobeRing(base);
        //RingParticles particles = new RingParticles(base, base.camera);

        globe.onConfigChanged(new ConfigPrefs(base.context).getCredinals());

        colorChangedListeners.add(globe);
        colorChangedListeners.add(ring);
        //colorChangedListeners.add(particles);

        weatherChangedListeners.add(globe);
        //weatherChangedListeners.add(particles);

        credinalsChangedListener = globe;

        render.rebindShaderCollection();
        render.addDrawable(globe);
        render.addDrawable(ring);
        //render.addPostDrawable(particles.getInstance());

        render.setTouchListener(new ForceUpdater(this));

        new ConfigPrefs(base.context).registerListener(this);

        if (base.context instanceof ActivityMain) {
            if (Base.debug) {
                ((BaseRender) render).addPostDrawable(new FpsBar(base));
            }
        }

        return this;
    }

    public void changeWeather(WeatherData data) {

        if (data == null || currentWeatherData != null && data.getTime() == currentWeatherData.getTime()) {
            Base.log("weather not changed");
            return;
        }

        currentWeatherData = data;
        lastWeatherChangeTime = data.getTime();

        Base.log("weather updated", lastWeatherChangeTime, "\n", data.toString());

        base.render.addUpdateable(new WeatherColorUpdater(base, colorChangedListeners, data.getTemperatureC(), currentWeatherColor));

        for (WeatherChangedListener listener : weatherChangedListeners) {
            listener.onWeatherChanged(data);
        }
    }

    public void registerConnectionReceiver() {

        if (connectionReceiver == null) {
            connectionReceiver = new ConnectionHandler(this);
        }

        base.context.registerReceiver(connectionReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void unregisterConnectionReceiver() {

        if (connectionReceiver != null) {
            base.context.unregisterReceiver(connectionReceiver);
            connectionReceiver = null;
        }
    }

    public void unregisterPrefsListener() {

        if (credinalsChangedListener != null) {
            new ConfigPrefs(base.context).unregisterListener(this);
        }
    }

    @Override
    public void requestWeatherUpdate(boolean forceUpdate) {

        if (!forceUpdate) {
            long currentDelay = System.currentTimeMillis() - lastWeatherChangeTime;

            Base.logV(currentDelay);
            //change weather after 30min
            if (currentDelay < 30000) {
                return;
            }

            //download new data after 2 hours
            if (currentDelay < 120000 && forecastData != null) {
                changeWeather(forecastData.getCurrent());
                return;
            }
        }

        Base.logV("request weather update");

        if (forecastData != null && !base.isConnected()) {
            changeWeather(forecastData.getCurrent());
        }

        CoarseLocation loc = new CoarseLocation(base.context);
        loc.setLocationListener(this);
        loc.requestLocation();
    }

    @Override
    public void onLocationObtained(String lat, String lon) {

        forecast.setLocation(lat, lon);
        forecast.requestUpdate();
    }

    @Override
    public void onWeatherChanged(ForecastWeatherData data) {

        if (data != null) {
            forecastData = data;
        }

        if (forecastData != null) {
            changeWeather(forecastData.getCurrent());
        }
    }

    @Override
    public void onConnected() {

        unregisterConnectionReceiver();
        requestWeatherUpdate(false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (credinalsChangedListener == null) {
            return;
        }

        ConfigPrefs prefs = new ConfigPrefs(sharedPreferences);

        switch (key) {
            case ConfigPrefs.BEO:
                credinalsChangedListener.onModelChanged(prefs.getModelBeo());
                break;
            case ConfigPrefs.TEXTURE:
                credinalsChangedListener.onTextureChanged(prefs.getTexture());
                break;
        }
    }
}
