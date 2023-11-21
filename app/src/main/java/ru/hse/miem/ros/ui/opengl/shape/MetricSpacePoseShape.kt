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

/**
 * Represents a pose.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
open class MetricSpacePoseShape() : TriangleFanShape(VERTICES, COLOR) {
    companion object {
        private val COLOR: ROSColor = ROSColor.fromHexAndAlpha("377dfa", 1.0f)
        private val VERTICES: FloatArray = floatArrayOf(
            0.2f, 0f, 0f,
            -0.2f, -0.15f, 0f,
            -0.05f, 0f, 0f,
            -0.2f, 0.15f, 0f,
            0.2f, 0f, 0f
        )
    }
}
