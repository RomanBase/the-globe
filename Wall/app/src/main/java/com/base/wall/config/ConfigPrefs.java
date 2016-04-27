package com.base.wall.config;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigPrefs {

    private static final String CONFIG = "config";
    public static final String BEO = "beo";
    public static final String TEXTURE = "texture";

    private static final String BEO_DEFAULT = "globes/globe.beo";
    private static final String TEXTURE_DEFAULT = "globes/rectangle_1.png";

    private final SharedPreferences prefs;

    public ConfigPrefs(Context context) {

        prefs = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
    }

    public ConfigPrefs(SharedPreferences prefs) {

        this.prefs = prefs;
    }

    public void setCredinals(ConfigCredinals config) {

        prefs.edit()
                .putString(BEO, config.getBeoFilePath())
                .putString(TEXTURE, config.getTextureFilePath())
                .apply();
    }

    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {

        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {

        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public String getModelBeo() {

        return prefs.getString(BEO, BEO_DEFAULT);
    }

    public String getTexture() {

        return prefs.getString(TEXTURE, TEXTURE_DEFAULT);
    }

    public ConfigCredinals getCredinals() {

        return new ConfigCredinals(getModelBeo(), getTexture());
    }
}
