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
package ru.hse.miem.ros.ui.opengl.layer

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
interface CameraControlListener {
    fun onTranslate(distanceX: Float, distanceY: Float)
    fun onRotate(focusX: Float, focusY: Float, deltaAngle: Double)
    fun onZoom(focusX: Float, focusY: Float, factor: Float)
    fun onDoubleTap(x: Float, y: Float)
}