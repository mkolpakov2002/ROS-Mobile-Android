package ru.hse.miem.ros.widgets.pose

import android.content.Context
import geometry_msgs.PoseWithCovarianceStamped
import org.ros.internal.message.Message
import org.ros.namespace.GraphName
import org.ros.rosjava_geometry.FrameTransform
import org.ros.rosjava_geometry.Transform
import ru.hse.miem.ros.data.model.repositories.rosRepo.TransformProvider
import ru.hse.miem.ros.ui.opengl.shape.GoalShape
import ru.hse.miem.ros.ui.opengl.shape.Shape
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import ru.hse.miem.ros.ui.views.widgets.SubscriberLayerView
import javax.microedition.khronos.opengles.GL10

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 08.03.21
 */
class PoseView(context: Context?) : SubscriberLayerView(context) {
    private val shape: Shape
    private lateinit var pose: PoseWithCovarianceStamped

    init {
        shape = GoalShape()
    }

    public override fun draw(view: VisualizationView, gl: GL10) {
        if (!this::pose.isInitialized) return
        shape.draw(view, gl)
    }

    public override fun onNewMessage(message: Message) {
        pose = message as PoseWithCovarianceStamped
        val source: GraphName = GraphName.of(pose.header.frameId)
        frame = source
        val frameTransform: FrameTransform =
            TransformProvider.getInstance().tree.transform(source, frame) ?: return
        val poseTransform: Transform = Transform.fromPoseMessage(
            pose.pose.pose
        )
        shape.transform = (frameTransform.transform.multiply(poseTransform))
    }

    companion object {
        val TAG: String = PoseView::class.java.simpleName
    }
}