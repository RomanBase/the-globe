package com.base.wall;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseActivity;
import com.base.lib.engine.BaseCamera;
import com.base.lib.engine.BaseRender;
import com.base.lib.engine.builders.BaseBuilder;
import com.base.lib.engine.builders.CameraBuilder;
import com.base.lib.engine.builders.RenderConfig;
import com.base.lib.googleservices.BasePermission;

public class ActivityMain extends BaseActivity {

    @Override
    protected Base onCreate(BaseBuilder builder) {
        Base.debug = true;
        builder.setFullScreen(true);
        builder.setPreventSleep(true);
        BasePermission permission = new BasePermission(this, new String[]{"android.permission.ACCESS_COARSE_LOCATION"});
        permission.requestRequiredPermissions();

        // finish();

        return builder.build();
    }

    @Override
    protected BaseCamera onCreateCamera(CameraBuilder builder) {
        builder.setSizeOrientationLandscape(!base.screen.isLandscpateOriented());
        return builder.build();
    }

    @Override
    protected BaseRender onCreateRender(RenderConfig config) {
        config.setFps(30.0f);

        /*return new BaseRender(config) {

            @Override
            protected void onCreate() {

                GlobeAloc globe = new GlobeAloc(base);
                globe.init();
            }

            @Override
            public void onResume() {


            }
        };*/

        return new ConfigRenderMain(config);
    }
}
