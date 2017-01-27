package com.base.lib.engine.common.gl;

import android.opengl.GLES20;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseDrawable;
import com.base.lib.engine.BaseGL;
import com.base.lib.engine.BaseTexture;
import com.base.lib.engine.common.DrawableData;

/**
 *
 */
public class RenderToTexture extends BaseDrawable { //todo split

    private int frameBuffer;
    private int renderBuffer;

    private int texW;
    private int texH;

    public RenderToTexture(final int textureWidth, final int textureHeight) {
        super(DrawableData.RECTANGLE(textureWidth, textureHeight, 0.5f, 0.5f, 0.5f, -0.5f));

        this.texW = textureWidth;
        this.texH = textureHeight;
        if (base.gl.isOnGLThread()) {
            generate(textureWidth, textureHeight);
        } else {
            base.render.glQueueEvent(new Runnable() {
                @Override
                public void run() {
                    generate(textureWidth, textureHeight);
                }
            });
        }
    }

    public void setDrawableDimension(float x, float y) {

        getBuffer().getVerticeBuffer().position(0);
        getBuffer().putVertices(DrawableData.rectangleVertices(x * 0.5f, y * 0.5f));
    }

    protected void generate(int texW, int texH) {
        BaseGL.glError(); // clear 'show' prev errors

        int[] temp = new int[1];

        // generate the texture
        texture = new BaseTexture(base.gl);
        texture.setName("RTT_" + texW + "x" + texH + "_" + hashCode());
        GLES20.glGenTextures(1, temp, 0);
        texture.glid = temp[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.glid);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texW, texH, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        BaseGL.glError("RTT");

        // generate frame buffer
        GLES20.glGenFramebuffers(1, temp, 0);
        frameBuffer = temp[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        BaseGL.glError("RTT");

        // create render buffer and bind 16-bit depth buffer
        GLES20.glGenRenderbuffers(1, temp, 0);
        renderBuffer = temp[0];
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBuffer);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, texW, texH);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture.glid, 0);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderBuffer);
        BaseGL.glError("RTT");


        // check status
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Base.logE("RTT", "Framebuffer uncompleted");
            BaseGL.glError("RTT");
            if (!base.gl.isOnGLThread()) {
                throw new RuntimeException("Monkeys can't generate Framebuffer outside of GL Thread.");
            }
        } else {
            if (texture.glid == 0 || frameBuffer == 0 || renderBuffer == 0) {
                BaseGL.glError("RTT");
                throw new RuntimeException("GL Buffers Error - NULL");
            }
            Base.logI("RTT", "Framebuffer ok");
        }

        // unbind
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        BaseGL.glError("RTT");
    }

    public void bind() {

        // Bind the framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        GLES20.glViewport(0, 0, texW, texH);
        BaseGL.glClear();
    }

    public void unbind() {

        // Bind the default framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, (int) base.screen.width, (int) base.screen.height);
        BaseGL.glClear();
    }

    public int getFrameBuffer() {
        return frameBuffer;
    }

    public void setFrameBuffer(int frameBufferId) {
        this.frameBuffer = frameBufferId;
    }

    public int getRenderBuffer() {
        return renderBuffer;
    }

    public void setRenderBuffer(int renderBufferId) {
        this.renderBuffer = renderBufferId;
    }

    @Override
    public void destroy() {

        if (renderBuffer > 0) {
            GLES20.glDeleteRenderbuffers(1, new int[]{renderBuffer}, 0);
            renderBuffer = 0;
        }
        if (frameBuffer > 0) {
            GLES20.glDeleteFramebuffers(1, new int[]{frameBuffer}, 0);
            frameBuffer = 0;
        }
    }
}
