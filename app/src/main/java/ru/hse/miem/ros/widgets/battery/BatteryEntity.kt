package ru.hse.miem.ros.widgets.battery

import ru.hse.miem.ros.data.model.entities.widgets.SubscriberWidgetEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import sensor_msgs.BatteryState

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 13.05.2021
 */
class BatteryEntity() : SubscriberWidgetEntity() {
    var displayVoltage: Boolean

    init {
        width = 1
        height = 2
        topic = Topic("battery", BatteryState._TYPE)
        displayVoltage = false
    }
}
