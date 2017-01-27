package com.ankhrom.wall.globe;

import android.view.View;

import com.base.lib.engine.BaseCamera;
import com.base.lib.engine.builders.CameraBuilder;
import com.base.lib.engine.builders.RenderConfig;
import com.base.lib.wall.BasePaperRender;
import com.base.lib.wall.BasePaperService;
import com.base.wall.WallRenderMain;

public class WallServiceMain extends BasePaperService {

    @Override
    protected BasePaperRender onCreate(RenderConfig config) {
        config.setFps(30.0f);

        return new WallRenderMain(config);
    }

    @Override
    protected BaseCamera onCreateCamera(CameraBuilder builder) {

        builder.setSizeOrientationLandscape(!base.screen.isLandscpateOriented());
        return builder.build();
    }

    @Override
    protected View onCreatePreview() {

        return null;
    }
}
