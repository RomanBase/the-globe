package com.base.wall.config.ui;

import android.opengl.GLES20;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseShader;
import com.base.lib.engine.controls.BaseUILayer;
import com.base.wall.Shaders;
import com.base.wall.config.ConfigCredinals;
import com.base.wall.config.ConfigParser;
import com.base.wall.config.ConfigPrefs;
import com.base.wall.listener.ConfigChangedListener;

import java.util.ArrayList;
import java.util.List;

public class ConfigLayer extends BaseUILayer implements ConfigChangedListener {

    private BaseShader secondaryShader;
    private List<ConfigLayerItem> configItems;
    private ConfigPrefs prefs;

    public ConfigLayer(Base base) {
        super(base, base.camera);

        shader = base.factory.getShader(Shaders.LIGHTING);
        secondaryShader = base.factory.getShader(BaseShader.TEXTURE_COLOR);

        prefs = new ConfigPrefs(base.context);

        setClassicSelectableProperty();
        enableStrictMode();

        init(new ConfigParser("config.cfg").getGlobeCredinals());
    }

    private void init(List<ConfigCredinals> credinals) {

        ConfigCredinals selected = prefs.getCredinals();

        configItems = new ArrayList<>(credinals.size());
        for (ConfigCredinals config : credinals) {
            ConfigLayerItem item = new ConfigLayerItem(base, config, base.camera.getSemiWidth(), this);
            if (config.getBeoFilePath().equals(selected.getBeoFilePath()) && config.getTextureFilePath().equals(selected.getTextureFilePath())) {
                item.selectColor();
            } else {
                item.clearColor();
            }
            configItems.add(item);
            addUnder(item, 0.25f);
        }
    }

    @Override
    public void update() {

        int count = items.size();
        if (count > 0) {
            adjustVerticalPositions(count - 1, 0, 0, 0);
        }
    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public void secondaryDrawPass() {

        GLES20.glUseProgram(shader.glid);
        super.secondaryDrawPass();
    }

    @Override
    public void onConfigChanged(ConfigCredinals credinals) {

        prefs.setCredinals(credinals);

        for (ConfigLayerItem item : configItems) {
            item.clearColor();
        }
    }
}
