package ru.hse.miem.ros.widgets.debug

import ru.hse.miem.ros.data.model.entities.widgets.SubscriberWidgetEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import org.ros.node.topic.Subscriber

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 17.08.20
 * @updated on 17.09.20
 * @modified by Tanya Rykova
 */
class DebugEntity() : SubscriberWidgetEntity() {
    var numberMessages: Int

    init {
        width = 4
        height = 3
        topic = Topic("MessageToDebug", Subscriber.TOPIC_MESSAGE_TYPE_WILDCARD)
        numberMessages = 10
    }
}
