package ru.hse.miem.ros.widgets.gps

import ru.hse.miem.ros.data.model.entities.widgets.SubscriberWidgetEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import sensor_msgs.NavSatFix

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 05.05.20
 * @updated on 27.10.2020
 * @modified by Maxim Kolpakov
 */
class GpsEntity() : SubscriberWidgetEntity() {
    init {
        width = 8
        height = 8
        topic = Topic("gps", NavSatFix._TYPE)
    }
}
