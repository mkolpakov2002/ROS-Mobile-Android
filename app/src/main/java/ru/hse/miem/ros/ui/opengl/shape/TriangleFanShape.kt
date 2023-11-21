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
package ru.hse.miem.ros.ui.opengl.shape

import ru.hse.miem.ros.ui.opengl.visualisation.ROSColor
import ru.hse.miem.ros.ui.opengl.visualisation.Vertices
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

/**
 * A [org.ros.android.view.visualization.shape.Shape] defined by vertices using OpenGl's GL_TRIANGLE_FAN method.
 *
 *
 * Note that this class is intended to be wrapped. No transformations are
 * performed in the [.draw] method.
 *
 * @author moesenle@google.com (Lorenz Moesenlechner)
 * @author damonkohler@google.com (Damon Kohler)
 */
open class TriangleFanShape(vertices: FloatArray, color: ROSColor) : BaseShape() {
    private val vertices: FloatBuffer?

    /**
     * @param vertices an array of vertices as defined by OpenGL's GL_TRIANGLE_FAN method
     * @param color    the [ROSColor] of the [Shape]
     */
    init {
        this.vertices = Vertices.toFloatBuffer(vertices)
        this.color = (color)
    }

    public override fun drawShape(view: VisualizationView?, gl: GL10) {
        Vertices.drawTriangleFan(gl, vertices, color)
    }
}
