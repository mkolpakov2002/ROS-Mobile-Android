package ru.hse.miem.ros.ui.views.widgets

import android.content.Context
import android.util.AttributeSet
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.RosData
import org.ros.internal.message.Message

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.03.21
 */
abstract class WidgetGroupView : WidgetView, ISubscriberView, IPublisherView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    abstract fun addLayer(layer: LayerView)
    abstract fun onNewData(data: RosData)
    public override fun onNewMessage(message: Message) {
        return
    }
}
