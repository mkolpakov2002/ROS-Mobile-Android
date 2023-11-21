package ru.hse.miem.ros.widgets.switchbutton

import ru.hse.miem.ros.data.model.entities.widgets.PublisherWidgetEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import std_msgs.Bool

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.05.2022
 */
class SwitchButtonEntity() : PublisherWidgetEntity() {
    var text: String

    init {
        width = 2
        height = 1
        topic = Topic("switch_state", Bool._TYPE)
        immediatePublish = true
        text = "Switch"
    }
}
