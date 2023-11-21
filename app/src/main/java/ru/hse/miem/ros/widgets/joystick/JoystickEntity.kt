package ru.hse.miem.ros.widgets.joystick

import ru.hse.miem.ros.data.model.entities.widgets.PublisherWidgetEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import geometry_msgs.Twist

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.1.1
 * @created on 31.01.20
 * @updated on 10.05.20
 * @modified by Maxim Kolpakov
 */
class JoystickEntity() : PublisherWidgetEntity() {
    var xAxisMapping: String
    var yAxisMapping: String
    var xScaleLeft: Float
    var xScaleRight: Float
    var yScaleLeft: Float
    var yScaleRight: Float
    var rectangularLimits: Boolean

    init {
        width = 4
        height = 4
        topic = Topic("cmd_vel", Twist._TYPE)
        immediatePublish = false
        publishRate = 20f
        xAxisMapping = "Angular/Z"
        yAxisMapping = "Linear/X"
        xScaleLeft = 1f
        xScaleRight = -1f
        yScaleLeft = -1f
        yScaleRight = 1f
        rectangularLimits = false
    }
}
