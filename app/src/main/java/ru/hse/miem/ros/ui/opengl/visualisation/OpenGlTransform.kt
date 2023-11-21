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

import org.ros.rosjava_geometry.Transform
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

/**
 * An adapter for applying [Transform]s in an OpenGL context.
 *
 * @author damonkohler@google.com (Damon Kohler)
 * @author moesenle@google.com (Lorenz Moesenlechner)
 */
object OpenGlTransform {
    private val buffer: ThreadLocal<FloatBuffer> = object : ThreadLocal<FloatBuffer>() {
        override fun initialValue(): FloatBuffer {
            return FloatBuffer.allocate(16)
        }

        public override fun get(): FloatBuffer {
            val buffer: FloatBuffer = super.get()!!
            buffer.clear()
            return buffer
        }
    }

    /**
     * Applies a [Transform] to an OpenGL context.
     *
     * @param gl        the context
     * @param transform the [Transform] to apply
     */
    fun apply(gl: GL10, transform: Transform?) {
        val matrix: FloatBuffer = buffer.get()!!
        for (value: Double in transform!!.toMatrix()) {
            matrix.put(value.toFloat())
        }
        matrix.position(0)
        gl.glMultMatrixf(matrix)
    }
}
