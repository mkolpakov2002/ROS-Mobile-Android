/*
 * Copyright (C) 2012 Google Inc.
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

import android.view.MotionEvent
import org.ros.math.RosMath
import kotlin.math.atan2

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
class RotateGestureDetector(private val listener: OnRotateGestureListener) {
    private lateinit var previousMotionEvent: MotionEvent
    private fun angle(event: MotionEvent): Double {
        val deltaX: Double = (event.getX(0) - event.getX(1)).toDouble()
        val deltaY: Double = (event.getY(0) - event.getY(1)).toDouble()
        return atan2(deltaY, deltaX)
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount != 2) {
            return false
        }
        if (!this::previousMotionEvent.isInitialized) {
            previousMotionEvent = MotionEvent.obtain(event)
            return false
        }
        val deltaAngle: Double = RosMath.clamp(
            angle(previousMotionEvent) - angle(event),
            -MAX_DELTA_ANGLE,
            MAX_DELTA_ANGLE
        )
        val result: Boolean = listener.onRotate(previousMotionEvent, event, deltaAngle)
        previousMotionEvent.recycle()
        previousMotionEvent = MotionEvent.obtain(event)
        return result
    }

    fun interface OnRotateGestureListener {
        fun onRotate(event1: MotionEvent, event2: MotionEvent, deltaAngle: Double): Boolean
    }

    companion object {
        private val MAX_DELTA_ANGLE: Double = 1e-1
    }
}
