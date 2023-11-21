package ru.hse.miem.ros.data.model.repositories.rosRepo.message

/**
 * ROS topic class for subscriber/publisher nodes.
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 15.09.2020
 */
data class Topic(
    /**
     * Topic name e.g. '/map'
     */
    var name: String = "",

    /**
     * Type of the topic e.g. 'nav_msgs.OccupancyGrid'
     */
    var type: String = ""
) {
    constructor(other: Topic?) : this(other?.name ?: "", other?.type ?: "")
}
