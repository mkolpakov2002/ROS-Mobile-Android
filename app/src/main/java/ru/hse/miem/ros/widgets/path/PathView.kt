package ru.hse.miem.ros.widgets.path

import android.content.Context
import geometry_msgs.PoseStamped
import nav_msgs.Path
import org.ros.internal.message.Message
import org.ros.namespace.GraphName
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.opengl.visualisation.ROSColor
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import ru.hse.miem.ros.ui.views.widgets.SubscriberLayerView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 08.03.21
 */
class PathView(context: Context?) : SubscriberLayerView(context) {
    private lateinit var lineColor: ROSColor
    private var lineWidth: Float = 0f
    private var numPoints: Int = 0
    private lateinit var vertexBuffer: FloatBuffer
    override var widgetEntity: BaseEntity? = null
        get() = super.widgetEntity
        set(value) {
            field = value
            val entity: PathEntity = value as PathEntity
            lineColor = ROSColor.fromHex(entity.lineColor)
            lineWidth = entity.lineWidth
        }

    public override fun draw(view: VisualizationView, gl: GL10) {
        if (numPoints < 2) return
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        lineColor.apply(gl)
        gl.glLineWidth(lineWidth)
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, numPoints)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
    }

    public override fun onNewMessage(message: Message) {
        val path: Path = message as Path
        val pufferBuffer: ByteBuffer =
            ByteBuffer.allocateDirect(path.poses.size * 3 * java.lang.Float.SIZE)
        pufferBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = pufferBuffer.asFloatBuffer()
        var i = 0
        if (path.poses.size > 0) {
            frame = GraphName.of(path.poses[0].header.frameId)
            for (pose: PoseStamped in path.poses) {
                vertexBuffer.put(pose.pose.position.x.toFloat())
                vertexBuffer.put(pose.pose.position.y.toFloat())
                vertexBuffer.put(pose.pose.position.z.toFloat())
                i++
            }
        }
        vertexBuffer.position(0)
        numPoints = i
    }

    companion object {
        val TAG: String = PathView::class.java.simpleName
    }
}