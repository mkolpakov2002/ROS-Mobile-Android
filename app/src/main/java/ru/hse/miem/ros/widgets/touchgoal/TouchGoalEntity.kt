package ru.hse.miem.ros.widgets.touchgoal

import ru.hse.miem.ros.data.model.entities.widgets.PublisherLayerEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import geometry_msgs.PoseStamped

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 26.05.2021
 */
class TouchGoalEntity() : PublisherLayerEntity() {
    init {
        topic = Topic("/goal", PoseStamped._TYPE)
        immediatePublish = true
    }
}
