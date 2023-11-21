package ru.hse.miem.ros.ui.views.widgets

import android.content.Context
import android.util.AttributeSet
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import ru.hse.miem.ros.ui.general.DataListener

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.03.21
 */
abstract class PublisherWidgetView : WidgetView, IPublisherView {
    override var dataListener: DataListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    public override fun publishViewData(data: BaseData) {
        if (dataListener == null) return
        dataListener?.let{
            data.topic = (widgetEntity?.topic)
            it.onNewWidgetData(data)
        }
    }

    companion object {
        var TAG: String = PublisherWidgetView::class.java.simpleName
    }
}
