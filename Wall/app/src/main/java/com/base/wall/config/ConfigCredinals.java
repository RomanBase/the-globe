package com.base.wall.config;

public class ConfigCredinals {

    private String beoFilePath;
    private String textureFilePath;

    public ConfigCredinals() {

    }

    public ConfigCredinals(String beoFilePath, String textureFilePath) {
        this.beoFilePath = beoFilePath;
        this.textureFilePath = textureFilePath;
    }

    public String getBeoFilePath() {
        return beoFilePath;
    }

    public void setBeoFilePath(String beoFilePath) {
        this.beoFilePath = beoFilePath;
    }

    public String getTextureFilePath() {
        return textureFilePath;
    }

    public void setTextureFilePath(String textureFilePath) {
        this.textureFilePath = textureFilePath;
    }
}
