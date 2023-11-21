/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.hse.miem.ros.ui.opengl.visualisation

import android.graphics.Bitmap
import android.opengl.GLUtils
import com.google.common.base.Preconditions
import org.jboss.netty.buffer.ChannelBuffer
import org.ros.rosjava_geometry.Transform
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

/**
 * Renders a texture.
 *
 * @author moesenle@google.com (Lorenz Moesenlechner)
 * @author damonkohler@google.com (Damon Kohler)
 */
class TextureBitmap() : OpenGlDrawable {
    private val pixels: IntArray = IntArray(HEIGHT * STRIDE)
    private val surfaceVertices: FloatBuffer?
    private val textureVertices: FloatBuffer?
    private val mutex: Any
    private var bitmapFront: Bitmap
    private var bitmapBack: Bitmap
    private var handle: IntArray? = null
    private var origin: Transform? = null
    private var scaledWidth: Double = 0.0
    private var scaledHeight: Double = 0.0
    private var reload: Boolean

    init {
        surfaceVertices = Vertices.toFloatBuffer(
            floatArrayOf( // Triangle strip
                0.0f, 0.0f, 0.0f,  // Bottom left
                1.0f, 0.0f, 0.0f,  // Bottom right
                0.0f, 1.0f, 0.0f,  // Top left
                1.0f, 1.0f, 0.0f
            )
        )
        textureVertices = Vertices.toFloatBuffer(
            floatArrayOf( // Triangle strip
                0.0f, 0.0f,  // Bottom left
                1.0f, 0.0f,  // Bottom right
                0.0f, 1.0f,  // Top left
                1.0f, 1.0f
            )
        )
        bitmapFront = Bitmap.createBitmap(STRIDE, HEIGHT, Bitmap.Config.ARGB_8888)
        bitmapBack = Bitmap.createBitmap(STRIDE, HEIGHT, Bitmap.Config.ARGB_8888)
        mutex = Any()
        reload = true
    }

    fun updateFromPixelArray(
        pixels: IntArray, stride: Int, resolution: Float, origin: Transform?,
        fillColor: Int
    ) {
        Preconditions.checkArgument(pixels.size % stride == 0)
        val height: Int = pixels.size / stride
        for (y in 0 until HEIGHT) {
            for (x in 0 until STRIDE) {
                // If the pixel is within the bounds of the specified pixel array then
                // we copy the specified value. Otherwise, we use the specified fill
                // color.
                val sourceIndex: Int = y * stride + x
                val targetIndex: Int = y * STRIDE + x
                if (x < stride && y < height) {
                    this.pixels[targetIndex] = pixels[sourceIndex]
                } else {
                    this.pixels[targetIndex] = fillColor
                }
            }
        }
        update(origin, stride, resolution, fillColor)
    }

    fun updateFromPixelBuffer(
        pixels: ChannelBuffer, stride: Int, height: Int, resolution: Float,
        origin: Transform, fillColor: Int
    ) {
        var y: Int = 0
        var i: Int = 0
        while (y < HEIGHT) {
            var x: Int = 0
            while (x < STRIDE) {


                // If the pixel is within the bounds of the specified pixel array then
                // we copy the specified value. Otherwise, we use the specified fill
                // color.
                if ((x < stride) && (y < height) && pixels.readable()) {
                    this.pixels[i] = pixels.readInt()
                } else {
                    this.pixels[i] = fillColor
                }
                x++
                i++
            }
            y++
        }
        update(origin, stride, resolution, fillColor)
    }

    fun clearHandle() {
        handle = null
    }

    private fun update(origin: Transform?, stride: Int, resolution: Float, fillColor: Int) {
        this.origin = origin
        scaledWidth = (STRIDE * resolution).toDouble()
        scaledHeight = (HEIGHT * resolution).toDouble()
        bitmapBack.setPixels(pixels, 0, STRIDE, 0, 0, STRIDE, HEIGHT)
        synchronized(mutex) {
            val tmp: Bitmap = bitmapFront
            bitmapFront = bitmapBack
            bitmapBack = tmp
            reload = true
        }
    }

    private fun bind(gl: GL10) {
        if (handle == null) {
            handle = IntArray(1)
            gl.glGenTextures(1, handle, 0)
            reload = true
        }
        gl.glBindTexture(GL10.GL_TEXTURE_2D, handle!!.get(0))
        gl.glTexParameterf(
            GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_MIN_FILTER,
            GL10.GL_NEAREST.toFloat()
        )
        gl.glTexParameterf(
            GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_MAG_FILTER,
            GL10.GL_NEAREST.toFloat()
        )
        synchronized(mutex) {
            if (reload) {
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapFront, 0)
                reload = false
            }
        }
    }

    public override fun draw(view: VisualizationView, gl: GL10) {
        gl.glEnable(GL10.GL_TEXTURE_2D)
        bind(gl)
        gl.glPushMatrix()
        OpenGlTransform.apply(gl, origin)
        gl.glScalef(scaledWidth.toFloat(), scaledHeight.toFloat(), 1.0f)
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, surfaceVertices)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureVertices)
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glPopMatrix()
        gl.glBindTexture(GL10.GL_TEXTURE_2D, 0)
        gl.glDisable(GL10.GL_TEXTURE_2D)
    }

    companion object {
        /**
         * The maximum height of a texture.
         */
        val HEIGHT: Int = 512

        /**
         * The maximum width of a texture.
         */
        val STRIDE: Int = 512
    }
}
