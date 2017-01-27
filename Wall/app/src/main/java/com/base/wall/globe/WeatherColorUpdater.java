package com.base.wall.globe;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseUpdateable;
import com.base.lib.engine.common.Colorf;
import com.base.wall.common.MathHelper;
import com.base.wall.listener.ColorChangedListener;

import java.util.List;

public class WeatherColorUpdater extends BaseUpdateable {

    private final List<ColorChangedListener> colorChangedListeners;

    private final Colorf oldColor;
    private final Colorf newColor;
    private final Colorf currentColor;

    private float t;

    public WeatherColorUpdater(Base base, List<ColorChangedListener> colorChangedListeners, double temperature, Colorf currentColor) {
        super(base);

        this.colorChangedListeners = colorChangedListeners;

        this.newColor = getTemperatureColor(temperature);
        this.oldColor = currentColor.copy();
        this.currentColor = currentColor;
    }

    public Colorf getNewWeatherColor(){

        return newColor;
    }

    @Override
    public void update() {

        t += base.time.delta;

        if (t > 1.0f) {
            t = 1.0f;
            inUse = false;
        }

        currentColor.setf(
                MathHelper.interpolate(oldColor.r, newColor.r, t),
                MathHelper.interpolate(oldColor.g, newColor.g, t),
                MathHelper.interpolate(oldColor.b, newColor.b, t),
                1.0f
        );

        for (ColorChangedListener listener : colorChangedListeners) {
            listener.onColorChanged(currentColor);
        }
    }

    public Colorf getTemperatureColor(double temperature) {

        //-25 = 0 0 1
        //-10 = 0 0.35 1
        //0   = 0 1 1
        //+25 = 1 0.35 0
        //+40 = 1 1 0

        Colorf color = new Colorf();

        if (temperature > 0.0) {
            if (temperature < 25.0) {
                double t = temperature / 25.0;
                color.r = MathHelper.interpolate(0.0, 1.0, t);
                color.g = MathHelper.interpolate(1.0, 0.35, t);
                color.b = MathHelper.interpolate(1.0, 0.0, t);
                color.a = 1.0f;
            } else if (temperature < 40.0) {
                double t = MathHelper.progress(25.0, 40.0, temperature);
                color.r = 1.0f;
                color.g = MathHelper.interpolate(0.35, 1.0, t);
                color.b = 0.0f;
                color.a = 1.0f;
            } else {
                double t = MathHelper.progress(40.0, 75.0, temperature);
                color.r = 1.0f;
                color.g = 1.0f;
                color.b = 0.0f;
                color.a = MathHelper.interpolate(1.0, 3.0, t);
            }
        } else {
            if (temperature < -25.0) {
                double t = MathHelper.progress(-25.0, -50.0, temperature);
                color.r = 0.0f;
                color.g = 0.0f;
                color.b = 1.0f;
                color.a = MathHelper.interpolate(1.0f, 3.0f, t);
            } else if (temperature < -10.0) {
                double t = MathHelper.progress(-10.0, -25.0, temperature);
                color.r = 0.0f;
                color.g = MathHelper.interpolate(0.35f, 0.0f, t);
                color.b = 1.0f;
                color.a = 1.0f;
            } else {
                double t = -temperature / 10.0f;
                color.r = 0.0f;
                color.g = MathHelper.interpolate(1.0, 0.35, t);
                color.b = 1.0f;
                color.a = 1.0f;
            }
        }

        return color;
    }
}
