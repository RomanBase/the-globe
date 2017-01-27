package com.ankhrom.wall.globe;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseActivity;
import com.base.lib.engine.BaseCamera;
import com.base.lib.engine.BaseGLView;
import com.base.lib.engine.BaseRender;
import com.base.lib.engine.builders.BaseBuilder;
import com.base.lib.engine.builders.CameraBuilder;
import com.base.lib.engine.builders.RenderConfig;
import com.base.lib.googleservices.BasePermission;
import com.base.wall.ConfigRenderMain;

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

        return new ConfigRenderMain(config);
    }

    @Override
    protected void initContentView(BaseGLView glView) {

        setContentView(R.layout.activity_layout);

        FrameLayout container = (FrameLayout) findViewById(R.id.glViewContainer);

        findViewById(R.id.setAsWallapaperView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                if (Build.VERSION.SDK_INT > 15) {
                    intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(getPackageName(), WallServiceMain.class.getCanonicalName()));
                } else {
                    intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                }

                startActivity(intent);
            }
        });

        findViewById(R.id.closeView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        container.addView(glView);
    }
}
