package ru.hse.miem.ros.widgets.touchgoal

import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import ru.hse.miem.ros.ui.opengl.visualisation.XYOrthographicCamera
import geometry_msgs.Point
import geometry_msgs.Pose
import geometry_msgs.PoseStamped
import org.ros.internal.message.Message
import org.ros.namespace.GraphName
import org.ros.node.topic.Publisher
import org.ros.rosjava_geometry.Quaternion
import org.ros.rosjava_geometry.Transform
import org.ros.rosjava_geometry.Vector3
import kotlin.math.atan2

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 26.05.2021
 */
class TouchGoalData(private val camera: XYOrthographicCamera) : BaseData() {
    var startX: Float = 0f
    var startY: Float = 0f
    var endX: Float = 0f
    var endY: Float = 0f
    var start: Transform? = null
    var end: Transform? = null
    var frame: GraphName?  = null

    init {
        frame = camera.frame
    }

    fun setStart(x: Float, y: Float) {
        startX = x
        startY = y
        start = camera.toFrame(x, y)
    }

    fun setEnd(x: Float, y: Float) {
        endX = x
        endY = y
        end = camera.toFrame(x, y)
    }

    val isValid: Boolean
        get() {
            return (start != null) && (end != null) && (frame != null)
        }

    public override fun toRosMessage(publisher: Publisher<Message>, widget: BaseEntity): Message {
        val entity: TouchGoalEntity = widget as TouchGoalEntity

        // Create message
        val message: PoseStamped = publisher.newMessage() as PoseStamped
        message.header.frameId = frame.toString()
        val pose: Pose = message.pose

        // Set position
        val pos: Point = pose.position
        val startTransform = start
        startTransform?.translation?.toPointMessage(pos)

        // Set orientation
        val endTransform = end
        if (startTransform != null && endTransform != null) {
            val diff: Vector3 = endTransform.translation.add(startTransform.translation.invert())
            val angle: Float = atan2(diff.y, diff.x).toFloat()
            val rotation: Quaternion =
                Quaternion.fromAxisAngle(Vector3(0.0, 0.0, 1.0), angle.toDouble())
            val poseRot: geometry_msgs.Quaternion = pose.orientation
            poseRot.w = rotation.w
            poseRot.x = rotation.x
            poseRot.y = rotation.y
            poseRot.z = rotation.z
        }
        return message
    }

    companion object {
        val TAG: String = TouchGoalData::class.java.simpleName
    }
}
