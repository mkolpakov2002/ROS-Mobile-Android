package ru.hse.miem.ros.widgets.camera

import ru.hse.miem.ros.data.model.entities.widgets.SubscriberWidgetEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import sensor_msgs.Image

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 27.04.2020
 * @updated on 27.10.2020
 * @modified by Maxim Kolpakov
 */
class CameraEntity() : SubscriberWidgetEntity() {
    var colorScheme: Int = 0
    var drawBehind: Boolean = false
    var useTimeStamp: Boolean = false

    init {
        width = 8
        height = 6
        topic = Topic("camera/image_raw", Image._TYPE)
    }
}
