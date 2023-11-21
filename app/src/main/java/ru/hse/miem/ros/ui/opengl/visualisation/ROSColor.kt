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

import com.google.common.base.Preconditions
import javax.microedition.khronos.opengles.GL10

/**
 * Defines a color based on RGBA values in the range [0, 1].
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
class ROSColor(var red: Float, var green: Float, var blue: Float, var alpha: Float) {

    fun interpolate(other: ROSColor, fraction: Float): ROSColor {
        return ROSColor(
            (other.red - red) * fraction + red,
            (other.green - green) * fraction + green,
            (other.blue - blue) * fraction + blue,
            (other.alpha - alpha) * fraction + alpha
        )
    }

    fun toInt(): Int {
        var A: Int = (255 * alpha).toInt()
        var R: Int = (255 * red).toInt()
        var G: Int = (255 * green).toInt()
        var B: Int = (255 * blue).toInt()
        A = (A shl 24) and -0x1000000
        R = (R shl 16) and 0x00FF0000
        G = (G shl 8) and 0x0000FF00
        B = B and 0x000000FF
        return A or R or G or B
    }

    public override fun toString(): String {
        return "Color = R:$red B:$blue G:$green A:$alpha"
    }

    fun apply(gl: GL10) {
        gl.glColor4f(red, green, blue, alpha)
    }

    companion object {
        fun copyOf(color: ROSColor): ROSColor {
            return ROSColor(color.red, color.green, color.blue, color.alpha)
        }

        fun fromInt(color: Int): ROSColor {
            val a: Float = ((color shr 24) and 0xFF) / 255f
            val r: Float = ((color shr 16) and 0xFF) / 255f
            val g: Float = ((color shr 8) and 0xFF) / 255f
            val b: Float = (color and 0xFF) / 255f
            return ROSColor(r, g, b, a)
        }

        fun fromHex(hex: String): ROSColor {
            val length: Int = hex.length
            return when (length) {
                6 -> {
                    val red: Float = hex.substring(0, 2).toInt(16) / 255.0f
                    val green: Float = hex.substring(2, 4).toInt(16) / 255.0f
                    val blue: Float = hex.substring(4).toInt(16) / 255.0f
                    ROSColor(red, green, blue, 1f)
                }
                8 -> {
                    val alpha: Float = hex.substring(0, 2).toInt(16) / 255.0f
                    val red: Float = hex.substring(2, 4).toInt(16) / 255.0f
                    val green: Float = hex.substring(4, 6).toInt(16) / 255.0f
                    val blue: Float = hex.substring(6).toInt(16) / 255.0f
                    ROSColor(red, green, blue, alpha)
                }
                else -> {
                    ROSColor(0f, 0f, 0f, 0f)
                }
            }
        }

        fun fromHexAndAlpha(hex: String, alpha: Float): ROSColor {
            Preconditions.checkArgument(hex.length == 6)
            val red: Float = hex.substring(0, 2).toInt(16) / 255.0f
            val green: Float = hex.substring(2, 4).toInt(16) / 255.0f
            val blue: Float = hex.substring(4).toInt(16) / 255.0f
            return ROSColor(red, green, blue, alpha)
        }
    }
}
