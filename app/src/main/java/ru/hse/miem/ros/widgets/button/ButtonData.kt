package ru.hse.miem.ros.widgets.button

import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import org.ros.internal.message.Message
import org.ros.node.topic.Publisher
import std_msgs.Bool

/**
 * TODO: Description
 *
 * @author Dragos Circa
 * @version 1.0.0
 * @created on 02.11.2020
 * @updated on 18.11.2020
 * @modified by Tanya Rykova
 */
class ButtonData(var pressed: Boolean) : BaseData() {
    public override fun toRosMessage(publisher: Publisher<Message>, widget: BaseEntity): Message {
        val message: Bool = publisher.newMessage() as Bool
        message.data = pressed
        return message
    }
}
