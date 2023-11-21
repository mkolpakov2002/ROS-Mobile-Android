package ru.hse.miem.ros.ui.views.widgets

import android.content.Context
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import javax.microedition.khronos.opengles.GL10

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.03.21
 */
abstract class PublisherLayerView(context: Context?) : LayerView(context), IPublisherView {
    public override fun publishViewData(data: BaseData) {
        if (dataListener == null) return
        dataListener?.let {
            data.topic = (widgetEntity?.topic)
            it.onNewWidgetData(data)
        }
    }

    public override fun draw(view: VisualizationView, gl: GL10) {}
}
