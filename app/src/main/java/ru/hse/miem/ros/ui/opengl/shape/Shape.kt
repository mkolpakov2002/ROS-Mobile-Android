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

import ru.hse.miem.ros.ui.opengl.visualisation.OpenGlDrawable
import ru.hse.miem.ros.ui.opengl.visualisation.ROSColor
import org.ros.rosjava_geometry.Transform

/**
 * A [Shape] is a [OpenGlDrawable] that has a [ROSColor] and a
 * [Transform] that is applied to the OpenGL matrix stack before drawing.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
interface Shape : OpenGlDrawable {
    /**
     * @return the [ROSColor] of this [Shape]
     */
    /**
     * @param color the [ROSColor] of this [Shape]
     */
    var color: ROSColor
    /**
     * @return the [Transform] that will be applied to this [Shape]
     * before it is drawn
     */
    /**
     * @param transform the [Transform] that will be applied to this [Shape]
     * before it is drawn
     */
    var transform: Transform
}
