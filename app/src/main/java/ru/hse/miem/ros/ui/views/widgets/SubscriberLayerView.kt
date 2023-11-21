package ru.hse.miem.ros.ui.views.widgets

import android.content.Context
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import org.ros.internal.message.Message
import javax.microedition.khronos.opengles.GL10

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.03.21
 */
abstract class SubscriberLayerView(context: Context?) : LayerView(context), ISubscriberView {
    public override fun onNewMessage(message: Message) {
        return
    }

    public override fun draw(view: VisualizationView, gl: GL10) {}
}
