package ru.hse.miem.ros.widgets.laserscan

import ru.hse.miem.ros.data.model.entities.widgets.SubscriberLayerEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import ru.hse.miem.ros.ui.opengl.visualisation.ROSColor
import sensor_msgs.LaserScan

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 14.05.21
 */
class LaserScanEntity() : SubscriberLayerEntity() {
    var pointsColor: Int
    var areaColor: Int
    var pointSize: Int
    var showFreeSpace: Boolean

    init {
        topic = Topic("/scan", LaserScan._TYPE)
        pointsColor = ROSColor.fromHexAndAlpha("377dfa", 0.6f).toInt()
        areaColor = ROSColor.fromHexAndAlpha("377dfa", 0.2f).toInt()
        pointSize = 10
        showFreeSpace = true
    }
}
