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

import ru.hse.miem.ros.data.model.repositories.rosRepo.TransformProvider
import org.ros.math.RosMath
import org.ros.namespace.GraphName
import org.ros.rosjava_geometry.FrameTransform
import org.ros.rosjava_geometry.FrameTransformTree
import org.ros.rosjava_geometry.Transform
import org.ros.rosjava_geometry.Vector3
import javax.microedition.khronos.opengles.GL10

/**
 * @author damonkohler@google.com (Damon Kohler)
 * @author moesenle@google.com (Lorenz Moesenlechner)
 */
class XYOrthographicCamera() {
    private val frameTransformTree: FrameTransformTree = TransformProvider.getInstance().tree
    private val mutex: Any = Any()
    private lateinit var viewport: Viewport
    // Transforms are immutable. No need for a defensive copy.
    /**
     * Transforms from camera frame (our data frame) to the ROS frame (our target
     * frame). See [.ROS_TO_SCREEN_TRANSFORM].
     */
    private lateinit var cameraToRosTransform: Transform

    /**
     * The frame in which to render everything. The default value is /map which
     * indicates that everything is rendered in map. If this is changed to, for
     * instance, base_link, the view follows the robot and the robot itself is in
     * the origin.
     */
    var frame: GraphName? = null
        private set

    init {
        resetTransform()
    }

    private fun resetTransform() {
        cameraToRosTransform = Transform.identity()
    }

    fun apply(gl: GL10) {
        synchronized(mutex) {
            OpenGlTransform.apply(gl, ROS_TO_SCREEN_TRANSFORM)
            OpenGlTransform.apply(gl, cameraToRosTransform)
        }
    }

    fun applyFrameTransform(gl: GL10, frame: GraphName?): Boolean {
        if (this.frame == null) return false
        val frameTransform: FrameTransform = frameTransformTree.transform(frame, this.frame)
            ?: return false
        OpenGlTransform.apply(gl, frameTransform.transform)
        return true
    }

    /**
     * Translates the camera.
     *
     * @param deltaX distance to move in x in pixels
     * @param deltaY distance to move in y in pixels
     */
    fun translate(deltaX: Double, deltaY: Double) {
        synchronized(mutex) {
            cameraToRosTransform = ROS_TO_SCREEN_TRANSFORM.invert()
                .multiply(Transform.translation(deltaX, deltaY, 0.0))
                .multiply(cameraToScreenTransform)
        }
    }

    private val cameraToScreenTransform: Transform
        get() {
            return ROS_TO_SCREEN_TRANSFORM.multiply(cameraToRosTransform)
        }

    fun getScreenTransform(targetFrame: GraphName?): Transform {
        val frameTransform: FrameTransform = frameTransformTree.transform(frame, targetFrame)
        return frameTransform.transform.multiply(cameraToScreenTransform.invert())
    }

    /**
     * Rotates the camera round the specified coordinates.
     *
     * @param focusX     the x coordinate to focus on
     * @param focusY     the y coordinate to focus on
     * @param deltaAngle the camera will be rotated by `deltaAngle` radians
     */
    fun rotate(focusX: Double, focusY: Double, deltaAngle: Double) {
        synchronized(mutex) {
            val focus: Transform =
                Transform.translation(toCameraFrame(focusX.toInt(), focusY.toInt()))
            cameraToRosTransform =
                cameraToRosTransform.multiply(focus).multiply(Transform.zRotation(deltaAngle))
                    .multiply(focus.invert())
        }
    }

    /**
     * Zooms the camera around the specified focus coordinates.
     *
     * @param focusX the x coordinate to focus on
     * @param focusY the y coordinate to focus on
     * @param factor the zoom will be scaled by this factor
     */
    fun zoom(focusX: Double, focusY: Double, factor: Double) {
        synchronized(mutex) {
            val focus: Transform =
                Transform.translation(toCameraFrame(focusX.toInt(), focusY.toInt()))
            val scale: Double = cameraToRosTransform.scale
            val zoom: Double = RosMath.clamp(
                scale * factor,
                MINIMUM_ZOOM_FACTOR.toDouble(),
                MAXIMUM_ZOOM_FACTOR.toDouble()
            ) / scale
            cameraToRosTransform =
                cameraToRosTransform.multiply(focus).scale(zoom).multiply(focus.invert())
        }
    }

    val zoom: Double
        /**
         * @return the current zoom level in pixels per meter
         */
        get() {
            return cameraToRosTransform.scale * PIXELS_PER_METER
        }

    /**
     * @return the provided pixel coordinates (where the origin is the top left
     * corner of the view) in the camera [.frame]
     */
    private fun toCameraFrame(pixelX: Int, pixelY: Int): Vector3 {
        val centeredX: Double = pixelX - viewport.width / 2.0
        val centeredY: Double = viewport.height / 2.0 - pixelY
        return cameraToScreenTransform.invert().apply(Vector3(centeredX, centeredY, 0.0))
    }

    /**
     * @param pixelX the x coordinate on the screen (origin top left) in pixels
     * @param pixelY the y coordinate on the screen (origin top left) in pixels
     * @param frame  the frame to transform the coordinates into (e.g. "map")
     * @return the pixel coordinate in the specified frame
     */
    fun toFrame(pixelX: Int, pixelY: Int, frame: GraphName?): Transform? {
        val translation: Transform = Transform.translation(toCameraFrame(pixelX, pixelY))
        val cameraToFrame: FrameTransform = frameTransformTree.transform(this.frame, frame)
            ?: return null
        return cameraToFrame.transform.multiply(translation)
    }

    fun toFrame(x: Float, y: Float): Transform? {
        return toFrame(x.toInt(), y.toInt(), frame)
    }

    /**
     * Changes the camera frame to the specified frame.
     *
     *
     * If possible, the camera will avoid jumping on the next frame.
     *
     * @param frame the new camera frame
     */
    private fun setFrame(frame: GraphName) {
        synchronized(mutex) {
            if (this.frame != null && this.frame !== frame) {
                val frameTransform: FrameTransform? =
                    frameTransformTree.transform(frame, this.frame)
                if (frameTransform != null) {
                    // Best effort to prevent the camera from jumping. If we don't have
                    // the transform yet, we can't help matters.
                    cameraToRosTransform =
                        cameraToRosTransform.multiply(frameTransform.transform)
                }
            }
            this.frame = frame
        }
    }

    /**
     * @see .setFrame
     */
    fun setFrame(frame: String?) {
        setFrame(GraphName.of(frame))
    }

    /**
     * Changes the camera frame to the specified frame and aligns the camera with
     * the new frame.
     *
     * @param frame the new camera frame
     */
    private fun jumpToFrame(frame: GraphName?) {
        synchronized(mutex) {
            this.frame = frame
            val scale: Double = cameraToRosTransform.scale
            resetTransform()
            cameraToRosTransform =
                cameraToRosTransform.scale(scale / cameraToRosTransform.scale)
        }
    }

    /**
     * @see .jumpToFrame
     */
    fun jumpToFrame(frame: String?) {
        jumpToFrame(GraphName.of(frame))
    }

    fun getViewport(): Viewport {
        return viewport
    }

    fun setViewport(viewport: Viewport) {
        this.viewport = viewport
    }

    companion object {
        val TAG: String = XYOrthographicCamera::class.java.simpleName

        /**
         * Pixels per meter in the world. If zoom is set to the number of pixels per
         * meter (the display density) then 1 cm in the world will be displayed as 1
         * cm on the display.
         */
        private val PIXELS_PER_METER: Double = 100.0

        /**
         * Transforms from our ROS frame (the data frame) to the screen frame (our
         * target frame) by rotating the coordinate system so that x is forward and y
         * is left. See [REP 103](http://www.ru.hse.miem.ros.org/reps/rep-0103.html).
         */
        private val ROS_TO_SCREEN_TRANSFORM: Transform = Transform.zRotation(Math.PI / 2).scale(
            PIXELS_PER_METER
        )

        /**
         * Most the user can zoom in.
         */
        private val MINIMUM_ZOOM_FACTOR: Float = 0.1f

        /**
         * Most the user can zoom out.
         */
        private val MAXIMUM_ZOOM_FACTOR: Float = 5f
    }
}
