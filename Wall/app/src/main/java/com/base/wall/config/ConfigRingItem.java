package com.base.wall.config;

import android.opengl.GLES20;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseGL;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.BaseShader;
import com.base.lib.engine.BaseTexture;
import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.Buffers;
import com.base.lib.engine.common.Colorf;
import com.base.lib.engine.common.file.BeoParser;
import com.base.lib.engine.common.gl.BaseGLBuffer;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class ConfigRingItem extends BaseRenderable {

    private final FloatBuffer verts;
    private final FloatBuffer texts;
    private final ShortBuffer faces;

    private final BaseTexture texture;
    private final float[] matrixL;
    private final float[] matrixR;

    private final Colorf cl;
    private final Colorf cr;

    private float rl;
    private float rr;
    private float sl;
    private float sr;

    private float slStep = 0.01f;
    private float srStep = 0.01f;
    private float slStepMax = 0.05f;

    private float yPos;

    public ConfigRingItem(Base base) {
        super(base);

        shader = base.factory.getShader(BaseShader.TEXTURE_COLOR);

        BaseDrawableData data = new BeoParser("ring.beo").getBaseDrawableData();

        verts = Buffers.floatBuffer(data.vertices);
        texts = Buffers.floatBuffer(data.textures);
        faces = Buffers.shortBuffer(data.faceOrder);

        matrixL = BaseMatrix.newMatrix();
        matrixR = BaseMatrix.newMatrix();
        texture = base.factory.getTexture("ring.png");

        cl = new Colorf(1.0f, 1.0f, 1.0f, 0.25f);
        cr = new Colorf();
    }

    public void setColor(float r, float g, float b) {

        cr.setf(r, g, b, 1.0f);
    }

    public void setY(float y) {
        this.yPos = y;
    }

    @Override
    public void draw() {

        shader.useProgram();

        BaseGL.bindTexture(texture.glid);
        BaseGLBuffer.glPutArray(verts, shader.handle[1], 3);
        BaseGLBuffer.glPutArray(texts, shader.handle[2], 2);

        GLES20.glUniform4f(shader.handle[3], cr.r, cr.g, cr.b, cr.a);
        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, matrixR, 0);
        BaseGLBuffer.glDrawElements(faces);

        GLES20.glUniform4f(shader.handle[3], cl.r, cl.g, cl.b, cl.a);
        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, matrixL, 0);
        BaseGLBuffer.glDrawElements(faces);
    }

    @Override
    public void update() {

        BaseMatrix.setIdentity(matrixL);
        BaseMatrix.translate(matrixL, 0.0f, yPos, 0.0f);
        BaseMatrix.rotateZ(matrixL, rl += 7.75f * base.time.delta);
        BaseMatrix.scale(matrixL, 1.0f + sl);
        BaseMatrix.multiplyMC(matrixL, camera);

        BaseMatrix.setIdentity(matrixR);
        BaseMatrix.translate(matrixR, 0.0f, yPos, 0.0f);
        BaseMatrix.rotateZ(matrixR, rr -= 5.25f * base.time.delta);
        BaseMatrix.scale(matrixR, 1.075f + sr);
        BaseMatrix.multiplyMC(matrixR, camera);

        if (rl > 360.0f) {
            rl -= 360.0f;
        }

        if (rr < 0.0f) {
            rr += 360.0f;
        }

        sl += slStep * base.time.delta;
        if (slStep > 0.0f) {
            if (sl > slStepMax) {
                slStep = -base.random.nextFloat() * 0.025f - 0.005f;
                slStepMax = -base.random.nextFloat() * 0.05f;
            }
        } else {
            if (sl < slStepMax) {
                slStep = base.random.nextFloat() * 0.025f + 0.005f;
                slStepMax = base.random.nextFloat() * 0.025f + 0.025f;
            }
        }
    }
}