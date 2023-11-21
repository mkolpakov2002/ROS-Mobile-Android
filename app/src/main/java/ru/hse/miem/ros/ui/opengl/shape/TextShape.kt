package ru.hse.miem.ros.ui.opengl.shape

import ru.hse.miem.ros.ui.opengl.visualisation.Vertices
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import ru.hse.miem.ros.ui.opengl.shape.texample.GLText
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

class TextShape(private val glText: GLText, private val text: String) : BaseShape() {
    private val lines: FloatBuffer?
    private var x: Float = 0f
    private var y: Float = 0f

    init {
        lines = Vertices.allocateBuffer(4 * 3)
    }

    fun setOffset(x: Float, y: Float) {
        this.x = x
        this.y = y
        lines!!.put(0f)
        lines.put(0f)
        lines.put(0f)
        lines.put(x)
        lines.put(y)
        lines.put(0f)
        lines.put(x)
        lines.put(y)
        lines.put(0f)
        lines.put(x + glText.getLength(text))
        lines.put(y)
        lines.put(0f)
        lines.flip()
    }

    override fun scale(view: VisualizationView, gl: GL10) {
        // Counter adjust for the camera zoom.
        gl.glScalef(
            1 / view.camera.zoom.toFloat(), 1 / view.camera.zoom.toFloat(),
            1.0f
        )
    }

    override fun drawShape(view: VisualizationView?, gl: GL10) {
        Vertices.drawLines(gl, lines, color, 3f)
        gl.glEnable(GL10.GL_TEXTURE_2D)
        glText.begin(
            color.red, color.green, color.blue, color.alpha
        )
        glText.draw(text, x, y)
        glText.end()
        gl.glDisable(GL10.GL_TEXTURE_2D)
    }
}
