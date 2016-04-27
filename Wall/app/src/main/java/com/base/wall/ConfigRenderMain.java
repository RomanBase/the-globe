package com.base.wall;

import com.base.lib.engine.BaseRender;
import com.base.lib.engine.builders.RenderConfig;
import com.base.wall.config.ui.ConfigLayer;
import com.base.wall.config.ui.SplashLoader;

public class ConfigRenderMain extends BaseRender {

    public ConfigRenderMain(RenderConfig config) {
        super(config);
    }

    @Override
    protected void onCreate() {

        Shaders.init(base.factory);

        final SplashLoader loader = new SplashLoader(base) {
            @Override
            public void onReady() {
                setUiLayer(new ConfigLayer(base), true);
            }
        };

        addDrawable(loader);
    }
}
