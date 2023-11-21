package ru.hse.miem.ros.ui.views.widgets

import android.content.Context
import android.util.AttributeSet
import org.ros.internal.message.Message

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.03.21
 */
abstract class SubscriberWidgetView : WidgetView, ISubscriberView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    public override fun onNewMessage(message: Message) {
        return
    }
}
