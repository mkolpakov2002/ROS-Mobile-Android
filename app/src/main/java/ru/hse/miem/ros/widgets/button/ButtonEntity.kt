package ru.hse.miem.ros.widgets.button

import ru.hse.miem.ros.data.model.entities.widgets.PublisherWidgetEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
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
class ButtonEntity() : PublisherWidgetEntity() {
    var text: String
    var rotation: Int

    init {
        width = 2
        height = 1
        topic = Topic("btn_press", Bool._TYPE)
        immediatePublish = true
        text = "A button"
        rotation = 0
    }
}
