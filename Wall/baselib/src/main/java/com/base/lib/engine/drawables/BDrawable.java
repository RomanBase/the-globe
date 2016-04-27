package com.base.lib.engine.drawables;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.Buffers;
import com.base.lib.engine.common.gl.BaseGLBuffer;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class BDrawable extends BaseRenderable {

    protected final FloatBuffer verts;
    protected final FloatBuffer texts;
    protected final ShortBuffer faces;

    protected final float[] matrix;

    public BDrawable(Base base, FloatBuffer verts, FloatBuffer texts, ShortBuffer faces) {
        super(base);

        this.verts = verts;
        this.texts = texts;
        this.faces = faces;

        matrix = BaseMatrix.newMatrix();
    }

    public BDrawable(Base base, float[] verts, float[] texts, short[] faces) {
        this(base, Buffers.floatBuffer(verts), Buffers.floatBuffer(texts), Buffers.shortBuffer(faces));
    }

    public BDrawable(Base base, BaseDrawableData data) {
        this(base, data.vertices, data.textures, data.faceOrder);
    }

    @Override
    public void draw() {

        BaseMatrix.glPutMatrix(matrix, shader.handle[0]);
        BaseGLBuffer.glPutArray(verts, shader.handle[1], 3);
        BaseGLBuffer.glPutArray(texts, shader.handle[2], 2);
        BaseGLBuffer.glDrawElements(faces);
    }

    @Override
    public void update() {

        BaseMatrix.setIdentity(matrix);
        BaseMatrix.multiplyMC(matrix, base.camera);
    }
}
