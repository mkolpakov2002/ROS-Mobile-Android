package ru.hse.miem.ros.data.model.repositories.rosRepo.node

import org.ros.internal.message.Message
import org.ros.node.topic.Publisher
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic

abstract class BaseData {
    var topic: Topic? = null
    open fun toRosMessage(publisher: Publisher<Message>, widget: BaseEntity): Message? {
        return null
    }
}
