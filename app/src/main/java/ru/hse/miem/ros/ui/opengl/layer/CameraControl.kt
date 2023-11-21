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
package ru.hse.miem.ros.ui.opengl.layer

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.core.view.GestureDetectorCompat
import ru.hse.miem.ros.ui.opengl.visualisation.RotateGestureDetector
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView

/**
 * Provides gesture control of the camera for translate, rotate, and zoom.
 *
 * @author damonkohler@google.com (Damon Kohler)
 * @author moesenle@google.com (Lorenz Moesenlechner)
 * @updated on 14.06.2021
 * @modified by Maxim Kolpakov
 */
class CameraControl(private val vizView: VisualizationView) {
    private var translateGestureDetector: GestureDetectorCompat? = null
    private var rotateGestureDetector: RotateGestureDetector? = null
    private var zoomGestureDetector: ScaleGestureDetector? = null
    fun init(translate: Boolean, rotate: Boolean, scale: Boolean) {
        if (translate) {
            translateGestureDetector =
                GestureDetectorCompat(vizView.context, object : GestureDetector.SimpleOnGestureListener() {
                    public override fun onDown(e: MotionEvent): Boolean {
                        // This must return true in order for onScroll() to trigger.
                        return true
                    }

                    public override fun onScroll(
                        event1: MotionEvent?, event2: MotionEvent,
                        distanceX: Float, distanceY: Float
                    ): Boolean {
                        vizView.camera.translate(-distanceX.toDouble(), distanceY.toDouble())
                        return true
                    }

                    public override fun onDoubleTap(e: MotionEvent): Boolean {
                        return true
                    }
                })
        }
        if (rotate) {
            rotateGestureDetector =
                RotateGestureDetector { event1: MotionEvent, _: MotionEvent, deltaAngle: Double ->
                    val focusX: Float = (event1.getX(0) + event1.getX(1)) / 2
                    val focusY: Float = (event1.getY(0) + event1.getY(1)) / 2
                    vizView.camera.rotate(focusX.toDouble(), focusY.toDouble(), deltaAngle)
                    true
                }
        }
        if (scale) {
            zoomGestureDetector = ScaleGestureDetector(
                vizView.context,
                object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    public override fun onScale(detector: ScaleGestureDetector): Boolean {
                        if (!detector.isInProgress) {
                            return false
                        }
                        val focusX: Float = detector.focusX
                        val focusY: Float = detector.focusY
                        val factor: Float = detector.scaleFactor
                        vizView.camera
                            .zoom(focusX.toDouble(), focusY.toDouble(), factor.toDouble())
                        return true
                    }
                })
        }
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        var handled: Boolean = false
        if (translateGestureDetector != null) handled =
            handled || translateGestureDetector!!.onTouchEvent(event)
        if (rotateGestureDetector != null) handled =
            handled || rotateGestureDetector!!.onTouchEvent(event)
        if (zoomGestureDetector != null) handled =
            handled || zoomGestureDetector!!.onTouchEvent(event)
        return handled
    }

    companion object {
        private val TAG: String = CameraControl::class.java.simpleName
    }
}