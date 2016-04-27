package com.base.wall.listener;

public interface CredinalsChangedListener {

    void onModelChanged(String filePath);

    void onTextureChanged(String filePath);
}
