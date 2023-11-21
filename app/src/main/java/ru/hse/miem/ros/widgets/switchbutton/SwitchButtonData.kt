package ru.hse.miem.ros.widgets.switchbutton

import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import org.ros.internal.message.Message
import org.ros.node.topic.Publisher
import std_msgs.Bool

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.05.2022
 */
class SwitchButtonData(private var pressed: Boolean) : BaseData() {
    public override fun toRosMessage(publisher: Publisher<Message>, widget: BaseEntity): Message {
        val message: Bool = publisher.newMessage() as Bool
        message.data = pressed
        return message
    }
}
