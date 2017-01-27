package com.base.wall;

import com.ankhrom.wall.globe.R;
import com.base.lib.engine.BaseGL;
import com.base.lib.engine.BaseShader;
import com.base.lib.engine.builders.BaseFactory;

public class Shaders extends BaseShader {

    public static final String LIGHTING = "lighting";

    public Shaders(BaseGL gl, String name, String... atrs) {
        super(gl, name, atrs);
    }

    public static void init(BaseFactory factory) {

        factory.gen.shaderResource(LIGHTING, R.raw.light_vert, R.raw.light_frag,
                "u_MVPMatrix", "u_MVMatrix", "a_Position", "a_Normal", "a_Texture", "u_LightPos", "u_TextureOffset",
                "u_Color", "u_Ambient", "u_Texture", "u_TextureTint");
    }
}
