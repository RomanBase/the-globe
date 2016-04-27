package com.base.wall;

import com.base.lib.engine.builders.RenderConfig;
import com.base.lib.wall.BasePaperRender;
import com.base.wall.globe.GlobeAloc;

public class WallRenderMain extends BasePaperRender {

    private GlobeAloc globe;

    public WallRenderMain(RenderConfig config) {
        super(config);
    }

    @Override
    protected void onCreate() {

        globe = new GlobeAloc(base);
        globe.init();
    }

    @Override
    public void onResume() {

        if (globe != null) {
            if (base.isConnected()) {
                globe.requestWeatherUpdate(false);
            } else {
                globe.registerConnectionReceiver();
            }
        }
    }

    @Override
    public void onPause() {

        if (globe != null) {
            globe.unregisterConnectionReceiver();
        }
    }

    @Override
    protected void onDestroy() {

        if(globe != null){
            globe.unregisterPrefsListener();
        }

        super.onDestroy();
    }
}
