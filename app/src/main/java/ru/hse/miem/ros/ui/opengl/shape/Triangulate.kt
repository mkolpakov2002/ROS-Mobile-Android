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

/**
 * Triangulates a contour for rendering as a triangle fan.
 *
 * @author damonkohler@google.com (Damon Kohler)
 * @see [Efficient Polygon Triangulation](http://www.flipcode.com/archives/Efficient_Polygon_Triangulation
.shtml)
 */
object Triangulate {
    private val EPSILON: Float = 1e-9f

    /**
     * Triangulate a contour/polygon.
     *
     * @param contour the vertices of the polygon
     * @param result  the result series of triangles
     * @return true on success
     */
    fun process(contour: Array<Point>, result: MutableList<Point>): Boolean {
        // Allocate and initialize list of Vertices in polygon.
        val n: Int = contour.size
        if (n < 3) {
            return false
        }
        val V = IntArray(n)

        // We want a counter-clockwise polygon in V.
        if (0.0f < area(contour)) {
            for (v in 0 until n) {
                V[v] = v
            }
        } else {
            for (v in 0 until n) {
                V[v] = (n - 1) - v
            }
        }
        var nv: Int = n

        // Remove nv-2 Vertices, creating 1 triangle every time.
        var count: Int = 2 * nv // error detection
        var m: Int = 0
        var v: Int = nv - 1
        while (nv > 2) {

            // If we loop, it is probably a non-simple polygon.
            if (0 >= (count--)) {
                // Triangulate: ERROR - probable bad polygon!
                return false
            }

            // Three consecutive vertices in current polygon, <u,v,w>
            var u: Int = v
            if (nv <= u) {
                u = 0 // previous
            }
            v = u + 1
            if (nv <= v) {
                v = 0 // new v
            }
            var w: Int = v + 1
            if (nv <= w) {
                w = 0 // next
            }
            if (snip(contour, u, v, w, nv, V)) {
                // True names of the vertices.
                val a: Int = V[u]
                val b: Int = V[v]
                val c: Int = V[w]

                // Output Triangle
                result.add(contour[a])
                result.add(contour[b])
                result.add(contour[c])
                m++

                // Remove v from remaining polygon.
                var s: Int = v
                var t: Int = v + 1
                while (t < nv) {
                    V[s] = V[t]
                    s++
                    t++
                }
                nv--

                // Reset error detection counter.
                count = 2 * nv
            }
        }
        return true
    }

    /**
     * Compute area of a contour/polygon.
     *
     * @param contour the contour to measure the area of
     * @return the area defined by the contour
     */
    private fun area(contour: Array<Point>): Float {
        val n: Int = contour.size
        var A = 0.0f
        var p: Int = n - 1
        var q = 0
        while (q < n) {
            A += contour[p].x() * contour[q].y() - contour[q]
                .x() * contour[p].y()
            p = q++
        }
        return A * 0.5f
    }

    /**
     * Decide if point (Px, Py) is inside triangle defined by
     * ((Ax,Ay), (Bx,By), (Cx,Cy)).
     *
     * @return true if the test point lies inside the triangle
     */
    private fun isInsideTriangle(
        Ax: Float, Ay: Float,
        Bx: Float, By: Float,
        Cx: Float, Cy: Float,
        Px: Float, Py: Float
    ): Boolean {
        val ax: Float = Cx - Bx
        val ay: Float = Cy - By
        val bx: Float = Ax - Cx
        val by: Float = Ay - Cy
        val cx: Float = Bx - Ax
        val cy: Float = By - Ay
        val apx: Float = Px - Ax
        val apy: Float = Py - Ay
        val bpx: Float = Px - Bx
        val bpy: Float = Py - By
        val cpx: Float = Px - Cx
        val cpy: Float = Py - Cy
        val aCROSSbp: Float = ax * bpy - ay * bpx
        val cCROSSap: Float = cx * apy - cy * apx
        val bCROSScp: Float = bx * cpy - by * cpx
        return ((aCROSSbp >= 0.0f) && (bCROSScp >= 0.0f) && (cCROSSap >= 0.0f))
    }

    private fun snip(contour: Array<Point>, u: Int, v: Int, w: Int, n: Int, V: IntArray): Boolean {
        val Ax: Float = contour[V[u]].x()
        val Ay: Float = contour[V[u]].y()
        val Bx: Float = contour[V[v]].x()
        val By: Float = contour[V[v]].y()
        val Cx: Float = contour[V[w]].x()
        val Cy: Float = contour[V[w]].y()
        if (EPSILON > (((Bx - Ax) * (Cy - Ay)) - ((By - Ay) * (Cx - Ax)))) {
            return false
        }
        for (p in 0 until n) {
            if ((p == u) || (p == v) || (p == w)) {
                continue
            }
            val Px: Float = contour[V[p]].x()
            val Py: Float = contour[V[p]].y()
            if (isInsideTriangle(Ax, Ay, Bx, By, Cx, Cy, Px, Py)) {
                return false
            }
        }
        return true
    }

    /**
     * Defines a point in 2D space.
     */
    class Point(private val x: Float, private val y: Float) {
        fun x(): Float {
            return x
        }

        fun y(): Float {
            return y
        }
    }
}
