package com.base.wall.globe;

import android.os.SystemClock;

import com.base.lib.interfaces.BaseTouchListener;
import com.base.wall.listener.WeatherUpdateRequested;

public class ForceUpdater implements BaseTouchListener {

    private int currentTouch;
    private long lastTouchTime;

    private WeatherUpdateRequested forceUpdateListener;

    public ForceUpdater(WeatherUpdateRequested forceUpdateListener) {
        this.forceUpdateListener = forceUpdateListener;
    }

    @Override
    public void onTouchDown(int id, float x, float y) {

        if (SystemClock.uptimeMillis() - lastTouchTime > 200) {
            currentTouch = 0;
        }

        lastTouchTime = SystemClock.uptimeMillis();
    }

    @Override
    public void onTouchUp(int id, float x, float y) {

        currentTouch++;

        if (currentTouch == 3) {
            currentTouch = 0;
            lastTouchTime = 0;
            if (forceUpdateListener != null) {
                forceUpdateListener.requestWeatherUpdate(true);
            }
        }
    }

    @Override
    public void onTouchMove(int id, float x, float y) {

    }
}
