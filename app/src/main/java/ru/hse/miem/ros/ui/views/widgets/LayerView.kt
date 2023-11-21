package ru.hse.miem.ros.ui.views.widgets

import android.content.Context
import android.view.MotionEvent
import org.ros.namespace.GraphName
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 8.03.21
 */
abstract class LayerView(context: Context?) : IBaseView {
    override var widgetEntity: BaseEntity? = null
    var frame: GraphName? = null
    lateinit var parentView: VisualizationView
    abstract fun draw(view: VisualizationView, gl: GL10)

    public override fun sameWidgetEntity(other: BaseEntity): Boolean {
        return other.id == (widgetEntity?.id ?: false)
    }

    fun onSurfaceChanged(view: VisualizationView?, gl: GL10?, width: Int, height: Int) {}
    fun onSurfaceCreated(view: VisualizationView?, gl: GL10?, config: EGLConfig?) {}
    open fun onTouchEvent(visualizationView: VisualizationView?, event: MotionEvent): Boolean {
        return false
    }

    companion object {
        var TAG: String = LayerView::class.java.simpleName
    }
}
