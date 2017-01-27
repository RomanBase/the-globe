package com.base.wall.globe;

import android.opengl.GLES20;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseGL;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.BaseTexture;
import com.base.lib.engine.Type;
import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.Buffers;
import com.base.lib.engine.common.Colorf;
import com.base.lib.engine.common.file.BeoParser;
import com.base.lib.engine.common.gl.BaseGLBuffer;
import com.base.wall.Shaders;
import com.base.wall.common.MathHelper;
import com.base.wall.config.ConfigCredinals;
import com.base.wall.listener.ColorChangedListener;
import com.base.wall.listener.ConfigChangedListener;
import com.base.wall.listener.CredinalsChangedListener;
import com.base.wall.listener.WeatherChangedListener;
import com.base.wall.weather.WeatherData;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class TheGlobe extends BaseRenderable implements ColorChangedListener, WeatherChangedListener, ConfigChangedListener, CredinalsChangedListener {

    float ONE_MINUTE_SPEED = 2.25f;
    float TINT_SPEED = 0.0075f;

    private final FloatBuffer verts;
    private final FloatBuffer texts;
    private final FloatBuffer norms;
    private final ShortBuffer faces;

    private final Colorf color;
    private final BaseTexture texture;
    private final BaseTexture textureTint;
    private final float[] matrix;
    private final float[] matrixMV;
    private final float[] matrixMVP;

    private float r;
    private float tr;

    public TheGlobe(Base base) {
        this(base, new ConfigCredinals("globes/globe.beo", null));
    }

    public TheGlobe(Base base, ConfigCredinals config) {
        super(base);

        shader = base.factory.getShader(Shaders.LIGHTING); //TODO

        BaseDrawableData data = new BeoParser(config.getBeoFilePath()).getBaseDrawableData();

        verts = Buffers.floatBuffer(data.vertices);
        texts = Buffers.floatBuffer(data.textures);
        norms = Buffers.floatBuffer(data.normals);
        faces = Buffers.shortBuffer(data.faceOrder);

        color = new Colorf(1.0f, 1.0f, 1.0f, 1.0f);
        matrix = BaseMatrix.newMatrix();
        matrixMV = BaseMatrix.newMatrix();
        matrixMVP = BaseMatrix.newMatrix();

        texture = new BaseTexture(base.gl);
        texture.setName("TheGlobeTexture");

        textureTint = base.factory.getTexture("globe_tint.png");
    }

    @Override
    public void onConfigChanged(ConfigCredinals credinals) {

        onModelChanged(credinals.getBeoFilePath());
        onTextureChanged(credinals.getTextureFilePath());
    }

    @Override
    public void onModelChanged(String filePath) {

        BaseDrawableData data = new BeoParser(filePath).getBaseDrawableData();

        verts.position(0);
        verts.put(data.vertices);

        texts.position(0);
        texts.put(data.textures);

        norms.position(0);
        norms.put(data.normals);

        faces.position(0);
        faces.put(data.faceOrder);
    }

    @Override
    public void onTextureChanged(String filePath) {

        texture.load(filePath, Type.STORAGE_ASSETS);
    }

    @Override
    public void onColorChanged(Colorf color) {

        final float brightness = 2.25f;

        this.color.r = color.r * brightness;
        this.color.g = color.g * brightness;
        this.color.b = color.b * brightness;
    }

    @Override
    public void onWeatherChanged(WeatherData data) {

        TINT_SPEED = MathHelper.interpolate(0.0035f, 0.0125f, data.getWindSpeed() / 75.0);
    }

    @Override
    public void draw() {

        BaseGL.bindTexture(texture.glid, 0, shader.handle[9]);
        BaseGL.bindTexture(textureTint.glid, 1, shader.handle[10]);

        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, matrixMVP, 0);
        GLES20.glUniformMatrix4fv(shader.handle[1], 1, false, matrixMV, 0);

        BaseGLBuffer.glPutArray(verts, shader.handle[2], 3);
        BaseGLBuffer.glPutArray(norms, shader.handle[3], 3);
        BaseGLBuffer.glPutArray(texts, shader.handle[4], 2);

        GLES20.glUniform3f(shader.handle[5], 0.0f, 0.0f, 21.5f);
        GLES20.glUniform2f(shader.handle[6], tr, 0.0f);
        GLES20.glUniform4f(shader.handle[7], color.r, color.g, color.b, 1.0f);
        GLES20.glUniform1f(shader.handle[8], 0.175f);

        BaseGLBuffer.glDrawElements(faces);
    }

    @Override
    public void update() {

        BaseMatrix.setIdentity(matrix);
        BaseMatrix.setIdentity(matrixMV);
        BaseMatrix.setIdentity(matrixMVP);

        BaseMatrix.rotateY(matrix, r += ONE_MINUTE_SPEED * base.time.delta);
        BaseMatrix.multiplyMM(matrixMV, camera.VPMatrix[0], matrix);
        BaseMatrix.multiplyMM(matrixMVP, camera.mVPMatrix, matrix);

        if (r > 360.0f) {
            r -= 360.0f;
        }

        tr += TINT_SPEED * base.time.delta;
        if (tr > 1.0f) {
            tr -= 1.0f;
        }
    }

    @Override
    public void destroy() {

    }
}
