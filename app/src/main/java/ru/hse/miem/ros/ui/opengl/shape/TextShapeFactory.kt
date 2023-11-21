package ru.hse.miem.ros.ui.opengl.shape

import android.graphics.Typeface
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import ru.hse.miem.ros.ui.opengl.shape.texample.GLText
import javax.microedition.khronos.opengles.GL10

class TextShapeFactory(view: VisualizationView, gl: GL10) {
    private val glText: GLText

    init {
        glText = GLText(gl, view.context.assets)
    }

    fun loadFont(typeface: Typeface?, size: Int, padX: Int, padY: Int) {
        glText.load(typeface, size, padX, padY)
    }

    fun loadFont(file: String?, size: Int, padX: Int, padY: Int) {
        glText.load(file, size, padX, padY)
    }

    fun newTextShape(text: String): TextShape {
        return TextShape(glText, text)
    }
}
