package com.base.wall.config;

import android.opengl.GLES20;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseGL;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.BaseTexture;
import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.Buffers;
import com.base.lib.engine.common.Colorf;
import com.base.lib.engine.common.file.BeoParser;
import com.base.lib.engine.common.gl.BaseGLBuffer;
import com.base.wall.Shaders;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class ConfigGlobeItem extends BaseRenderable {

    float ONE_MINUTE_SPEED = 2.25f;
    float TINT_SPEED = 0.0075f;

    private final FloatBuffer verts;
    private final FloatBuffer texts;
    private final FloatBuffer norms;
    private final ShortBuffer faces;

    private final BaseTexture texture;
    private final BaseTexture textureTint;

    private final float[] matrix;
    private final float[] matrixMV;
    private final float[] matrixMVP;

    private float r;
    private float tr;

    private final Colorf color;

    private float yPos;

    public ConfigGlobeItem(Base base, ConfigCredinals credinals) {
        super(base);

        shader = base.factory.getShader(Shaders.LIGHTING);

        BaseDrawableData data = new BeoParser(credinals.getBeoFilePath()).getBaseDrawableData();

        verts = Buffers.floatBuffer(data.vertices);
        texts = Buffers.floatBuffer(data.textures);
        norms = Buffers.floatBuffer(data.normals);
        faces = Buffers.shortBuffer(data.faceOrder);

        texture = base.factory.getTexture(credinals.getTextureFilePath());
        textureTint = base.factory.getTexture("globe_tint.png");

        matrix = BaseMatrix.newMatrix();
        matrixMV = BaseMatrix.newMatrix();
        matrixMVP = BaseMatrix.newMatrix();

        color = new Colorf();
    }

    public void setColor(float r, float g, float b) {

        color.setf(r, g, b, 1.0f);
    }

    public void setY(float yPos) {

        this.yPos = yPos;
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

        BaseMatrix.translate(matrix, 0.0f, yPos, 0.0f);
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
}
