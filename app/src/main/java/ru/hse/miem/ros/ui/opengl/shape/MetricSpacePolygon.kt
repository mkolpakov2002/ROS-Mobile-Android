/*
 * Copyright (C) 2014 Google Inc.
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

import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import ru.hse.miem.ros.ui.opengl.visualisation.ROSColor
import ru.hse.miem.ros.ui.opengl.visualisation.Vertices
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

/**
 * A polygon with metric space vertices.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
class MetricSpacePolygon(vertices: FloatArray, color: ROSColor) : BaseShape() {
    val vertexBuffer: FloatBuffer?
    val triangles: MutableList<FloatBuffer?>

    init {
        vertexBuffer = Vertices.toFloatBuffer(vertices)
        this.color = (color)
        val points: MutableList<Triangulate.Point> = Lists.newArrayList()
        val contour: Array<Triangulate.Point> = Array(vertices.size / 3) {
            Triangulate.Point(
                0f,
                0f
            )
        }
        for (i in contour.indices) {
            contour[i] = Triangulate.Point(vertices[i * 3], vertices[i * 3 + 1])
        }
        Preconditions.checkState(Triangulate.process(contour, points))
        triangles = Lists.newArrayList()
        for (i in 0 until (points.size / 3)) {
            val triangle: FloatBuffer = Vertices.allocateBuffer(3 * 3)
            for (j in i * 3 until (i * 3 + 3)) {
                triangle.put(points[j].x())
                triangle.put(points[j].y())
                triangle.put(0f)
            }
            triangle.flip()
            triangles.add(triangle)
        }
    }

    public override fun drawShape(view: VisualizationView?, gl: GL10) {
        val translucent: ROSColor = color
        translucent.alpha = (0.3f)
        for (triangle: FloatBuffer? in triangles) {
            Vertices.drawTriangleFan(gl, triangle, translucent)
        }
        val opaque: ROSColor = color
        opaque.alpha = (1f)
        Vertices.drawLineLoop(gl, vertexBuffer, opaque, 3f)
        Vertices.drawPoints(gl, vertexBuffer, opaque, 10f)
    }
}
