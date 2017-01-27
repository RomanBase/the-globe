package com.base.wall.config.ui;

import com.base.lib.engine.Base;
import com.base.lib.engine.controls.BaseUIItem;
import com.base.wall.config.ConfigCredinals;
import com.base.wall.config.ConfigGlobeItem;
import com.base.wall.config.ConfigRingItem;
import com.base.wall.listener.ConfigChangedListener;

public class ConfigLayerItem extends BaseUIItem {

    private ConfigChangedListener listener;
    private ConfigCredinals credinals;

    private ConfigGlobeItem globe;
    private ConfigRingItem ring;

    public ConfigLayerItem(Base base, ConfigCredinals credinals, float hSize, ConfigChangedListener listener) {

        setItemInfo(0, 0, hSize, hSize);

        globe = new ConfigGlobeItem(base, credinals);
        ring = new ConfigRingItem(base);

        this.credinals = credinals;
        this.listener = listener;
    }

    public void clearColor() {

        globe.setColor(0.25f, 1.5f, 0.25f);
        ring.setColor(0.125f, 1.0f, 0.125f);
    }

    public void selectColor() {

        globe.setColor(1.5f, 0.0f, 1.5f);
        ring.setColor(1.0f, 0.0f, 1.0f);
    }

    @Override
    public void onGoDown() {

    }

    @Override
    public boolean onGoUp(boolean above) {

        if (above) {
            listener.onConfigChanged(credinals);
            selectColor();
        }

        return above;
    }

    @Override
    public boolean isTouched(float x, float y) {
        return checkRectHit(x, y);
    }

    @Override
    public void updatePosition(float xmove, float ymove) {
        super.updatePosition(xmove, ymove);
        globe.setY(getY());
        ring.setY(getY());
    }

    @Override
    public void update() {
        globe.update();
        ring.update();
    }

    @Override
    public void draw() {
        globe.draw();
    }

    @Override
    public void secondaryDrawPass() {
        ring.draw();
    }
}
