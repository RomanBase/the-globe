package com.base.wall.config;

import com.base.lib.engine.common.file.BaseParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigParser extends BaseParser {

    private static final String GLOBE_CREDINALS = "g";
    private static final String GLOBE_ASSET_FILE = "globes/";

    private List<ConfigCredinals> globes;

    public ConfigParser(String file) {
        super(file);
    }

    public ConfigParser(int resourceID) {
        super(resourceID);
    }

    public ConfigParser(byte[] bytes) {
        super(bytes);
    }

    @Override
    protected void parse(BufferedReader br) throws IOException {

        globes = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {

            if (line.startsWith(GLOBE_CREDINALS)) {
                String[] token = line.split(" ");

                String beoFile = token[1];
                String textureFile = token[2];

                ConfigCredinals globe = new ConfigCredinals();
                globe.setBeoFilePath(GLOBE_ASSET_FILE + beoFile);
                globe.setTextureFilePath(GLOBE_ASSET_FILE + textureFile);

                globes.add(globe);
            }
        }
    }

    public List<ConfigCredinals> getGlobeCredinals() {
        return globes;
    }
}
