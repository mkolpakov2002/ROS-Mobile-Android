package ru.hse.miem.ros.widgets.rqtplot

import ru.hse.miem.ros.data.model.entities.widgets.SubscriberWidgetEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import org.ros.node.topic.Subscriber

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 29.05.21
 */
class RqtPlotEntity() : SubscriberWidgetEntity() {
    var fieldPath: String

    init {
        width = 8
        height = 6
        topic = Topic("/plot", Subscriber.TOPIC_MESSAGE_TYPE_WILDCARD)
        fieldPath = "/pos/xy"
    }
}
