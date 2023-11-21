package ru.hse.miem.ros.widgets.logger

import ru.hse.miem.ros.data.model.entities.widgets.SubscriberWidgetEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic

/**
 * TODO: Description
 *
 * @author Dragos Circa
 * @version 1.0.0
 * @created on 02.11.2020
 * @updated on 18.11.2020
 * @modified by Tanya Rykova
 */
class LoggerEntity() : SubscriberWidgetEntity() {
    var text: String
    var rotation: Int

    init {
        width = 3
        height = 1
        topic = Topic("log", std_msgs.String._TYPE)
        text = "A logger"
        rotation = 0
    }
}
