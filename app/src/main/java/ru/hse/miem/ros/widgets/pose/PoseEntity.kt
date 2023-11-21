package ru.hse.miem.ros.widgets.pose

import ru.hse.miem.ros.data.model.entities.widgets.SubscriberLayerEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import geometry_msgs.PoseWithCovarianceStamped

/**
 * Pose entity represents a widget which subscribes
 * to a topic with message type "geometry_msgs.PoseStamped".
 * Usable in 2D widgets.
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.03.21
 */
class PoseEntity() : SubscriberLayerEntity() {
    init {
        topic = Topic("/amcl_pose", PoseWithCovarianceStamped._TYPE)
    }
}
