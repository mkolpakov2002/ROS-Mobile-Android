package ru.hse.miem.ros.widgets.path

import ru.hse.miem.ros.data.model.entities.widgets.SubscriberLayerEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import nav_msgs.Path

/**
 * Path entity represents a widget which subscribes
 * to a topic with message type "nav_msgs.Path".
 * Usable in 2D widgets.
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 08.03.21
 */
class PathEntity() : SubscriberLayerEntity() {
    var lineWidth: Float
    var lineColor: String

    init {
        topic = Topic("/move_base/TebLocalPlannerROS/local_plan", Path._TYPE)
        lineWidth = 4f
        lineColor = "ff0000ff"
    }
}
