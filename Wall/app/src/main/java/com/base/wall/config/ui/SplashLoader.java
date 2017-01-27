package com.base.wall.config.ui;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.BaseTexture;
import com.base.lib.engine.common.DrawableData;
import com.base.lib.engine.drawables.BDrawable;

public abstract class SplashLoader extends BaseRenderable {

    private int drawCount;

    private BDrawable splash;
    private BaseTexture texture;

    public SplashLoader(Base base) {
        super(base);

        float size = base.camera.width;
        texture = base.factory.getTexture("ic_launcher_512");
        splash = new BDrawable(base, DrawableData.RECTANGLE(size, size).convertTo3D());
    }

    @Override
    public void draw() {

        texture.bind();
        splash.draw();

        if (drawCount++ == 3) {
            onReady();
            unUse();
        }
    }

    @Override
    public void update() {

        splash.update();
    }

    public abstract void onReady();
}
