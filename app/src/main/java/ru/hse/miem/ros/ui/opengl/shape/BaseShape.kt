package ru.hse.miem.ros.ui.opengl.shape

import ru.hse.miem.ros.ui.opengl.visualisation.OpenGlTransform
import ru.hse.miem.ros.ui.opengl.visualisation.ROSColor
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import org.ros.rosjava_geometry.Transform
import javax.microedition.khronos.opengles.GL10

/**
 * Defines the getters and setters that are required for all [Shape]
 * implementors.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
abstract class BaseShape() : Shape {
    final override lateinit var color: ROSColor
    final override var transform: Transform = Transform.identity()

    public override fun draw(view: VisualizationView, gl: GL10) {
        gl.glPushMatrix()
        OpenGlTransform.apply(gl, transform)
        scale(view, gl)
        drawShape(view, gl)
        gl.glPopMatrix()
    }

    /**
     * To be implemented by children. Draws the shape after the shape's
     * transform and scaling have been applied.
     */
    protected abstract fun drawShape(view: VisualizationView?, gl: GL10)

    /**
     * Scales the coordinate system.
     *
     *
     * This is called after transforming the surface according to
     * [.transform].
     */
    protected open fun scale(view: VisualizationView, gl: GL10) {
        // The default scale is in metric space.
    }
}
