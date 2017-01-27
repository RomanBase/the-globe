package com.base.wall.common;

public class MathHelper {

    public static float interpolate(double min, double max, double t) {

        return (float) (min + (max - min) * t);
    }

    public static float progress(double min, double max, double value) {

        return (float) ((value - min) / (max - min));
    }
}
