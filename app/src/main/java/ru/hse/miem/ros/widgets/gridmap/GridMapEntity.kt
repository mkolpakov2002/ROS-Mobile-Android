package ru.hse.miem.ros.widgets.gridmap

import ru.hse.miem.ros.data.model.entities.widgets.SubscriberLayerEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import nav_msgs.OccupancyGrid

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 08.03.21
 */
class GridMapEntity() : SubscriberLayerEntity() {
    init {
        topic = Topic("/move_base/local_costmap/costmap", OccupancyGrid._TYPE)
    }
}
