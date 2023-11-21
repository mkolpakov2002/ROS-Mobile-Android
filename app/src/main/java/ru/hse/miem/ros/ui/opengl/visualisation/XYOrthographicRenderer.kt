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

import android.graphics.Color
import android.opengl.GLSurfaceView
import ru.hse.miem.ros.ui.views.widgets.LayerView
import org.ros.namespace.GraphName
import ru.hse.miem.ros.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Renders all layers of a navigation view.
 *
 * @author damonkohler@google.com (Damon Kohler)
 * @author moesenle@google.com (Lorenz Moesenlechner)
 */
class XYOrthographicRenderer(private val view: VisualizationView) : GLSurfaceView.Renderer {
    private val bgR: Float
    private val bgG: Float
    private val bgB: Float

    init {
        val bgColor: Int = view.context.resources.getColor(R.color.bgColor)
        bgR = Color.red(bgColor) / 255f
        bgG = Color.green(bgColor) / 255f
        bgB = Color.blue(bgColor) / 255f
    }

    public override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        val viewport = Viewport(width, height)
        viewport.apply(gl)
        view.camera.setViewport(viewport)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glEnable(GL10.GL_BLEND)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        gl.glDisable(GL10.GL_DEPTH_TEST)
        gl.glClearColor(bgR, bgG, bgB, 1f)
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
        for (layer: LayerView in view.getLayers()) {
            layer.onSurfaceChanged(view, gl, width, height)
        }
    }

    public override fun onDrawFrame(gl: GL10) {
        gl.glClearColor(bgR, bgG, bgB, 1f)
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
        gl.glLoadIdentity()
        view.camera.apply(gl)
        drawLayers(gl)
    }

    private fun drawLayers(gl: GL10) {
        for (layer: LayerView in view.getLayers()) {
            gl.glPushMatrix()
            val layerFrame: GraphName? = layer.frame
            if (layerFrame != null && view.camera.applyFrameTransform(gl, layerFrame)) {
                layer.draw(view, gl)
            }
            gl.glPopMatrix()
        }
    }

    public override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        for (layer: LayerView in view.getLayers()) {
            layer.onSurfaceCreated(view, gl, config)
        }
    }

    companion object {
        var TAG: String = XYOrthographicRenderer::class.java.simpleName
    }
}
