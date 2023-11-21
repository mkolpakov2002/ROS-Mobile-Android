package ru.hse.miem.ros.widgets.joystick

import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import geometry_msgs.Twist
import geometry_msgs.Vector3
import org.ros.internal.message.Message
import org.ros.node.topic.Publisher

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 17.03.20
 * @updated on 17.03.20
 * @modified by
 */
class JoystickData(var x: Float, var y: Float) : BaseData() {
    public override fun toRosMessage(publisher: Publisher<Message>, widget: BaseEntity): Message {
        val joyWidget: JoystickEntity = widget as JoystickEntity
        val xAxisValue: Float =
            joyWidget.xScaleLeft + (joyWidget.xScaleRight - joyWidget.xScaleLeft) * ((x + 1) / 2f)
        val yAxisValue: Float =
            joyWidget.yScaleLeft + (joyWidget.yScaleRight - joyWidget.yScaleLeft) * ((y + 1) / 2f)
        val message: Twist = publisher.newMessage() as Twist
        for (i in 0..1) {
            val splitMapping: Array<String> =
                (if (i == 0) joyWidget.xAxisMapping else joyWidget.yAxisMapping).split("/".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()
            val value: Float = if (i == 0) xAxisValue else yAxisValue
            val dirVector: Vector3 = if ((splitMapping[0] == "Linear")) {
                message.linear
            } else {
                message.angular
            }
            when (splitMapping[1]) {
                "X" -> dirVector.x = value.toDouble()
                "Y" -> dirVector.y = value.toDouble()
                "Z" -> dirVector.z = value.toDouble()
            }
        }
        return message
    }

    companion object {
        val TAG: String = JoystickData::class.java.simpleName
    }
}
