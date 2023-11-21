package ru.hse.miem.ros.widgets.laserscan

import android.content.Context
import org.ros.internal.message.Message
import org.ros.namespace.GraphName
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.opengl.visualisation.ROSColor
import ru.hse.miem.ros.ui.opengl.visualisation.Vertices
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import ru.hse.miem.ros.ui.views.widgets.SubscriberLayerView
import sensor_msgs.LaserScan
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 14.05.21
 */
class LaserScanView(context: Context?) : SubscriberLayerView(context) {
    private val mutex: Any = Any()
    private lateinit var vertexFrontBuffer: FloatBuffer
    private lateinit var vertexBackBuffer: FloatBuffer
    private var occupiedSpaceColor: ROSColor? = null
    private var freeSpaceColor: ROSColor? = null
    private var pointSize: Float = 0f
    private var showFreeSpace: Boolean = false
    override var widgetEntity: BaseEntity? = null
        get() = super.widgetEntity
        set(value) {
            field = value
            val entity: LaserScanEntity = value as LaserScanEntity
            occupiedSpaceColor = ROSColor.fromInt(entity.pointsColor)
            freeSpaceColor = ROSColor.fromInt(entity.areaColor)
            pointSize = entity.pointSize.toFloat()
            showFreeSpace = entity.showFreeSpace
        }

    public override fun draw(view: VisualizationView, gl: GL10) {
        if (!this::vertexFrontBuffer.isInitialized) return
        synchronized(mutex) {
            if (showFreeSpace) Vertices.drawTriangleFan(gl, vertexFrontBuffer, freeSpaceColor)
            val pointVertices: FloatBuffer = vertexFrontBuffer.duplicate()
            pointVertices.position(3)
            Vertices.drawPoints(gl, pointVertices, occupiedSpaceColor, pointSize)
        }
    }

    public override fun onNewMessage(message: Message) {
        val scan: LaserScan = message as LaserScan
        frame = GraphName.of(scan.header.frameId)
        updateVertexBuffer2(scan)
    }

    private fun updateVertexBuffer2(laserScan: LaserScan) {
        var vertexCount: Int = 0
        val ranges: FloatArray = laserScan.ranges
        val size: Int = (ranges.size + 2) * 3
        if (!this::vertexBackBuffer.isInitialized || vertexBackBuffer.capacity() < size) {
            vertexBackBuffer = Vertices.allocateBuffer(size)
        }

        // Clear vertices and fill in first vertex
        vertexBackBuffer.clear()
        for (i in 0..2) {
            vertexBackBuffer.put(0f)
        }
        vertexCount++
        val minimumRange: Float = laserScan.rangeMin
        val maximumRange: Float = laserScan.rangeMax
        var angle: Float = laserScan.angleMin
        val angleIncrement: Float = laserScan.angleIncrement

        // Calculate coordinates of laser range values
        for (range: Float in ranges) {
            if (minimumRange < range && range < maximumRange) {
                vertexBackBuffer.put((range * cos(angle.toDouble())).toFloat())
                vertexBackBuffer.put((range * sin(angle.toDouble())).toFloat())
                vertexBackBuffer.put(0f)
                vertexCount++
            }
            angle += angleIncrement
        }
        vertexBackBuffer.position(0)
        vertexBackBuffer.limit(vertexCount * 3)
        synchronized(mutex) {
            val tmp: FloatBuffer = vertexFrontBuffer
            vertexFrontBuffer = vertexBackBuffer
            vertexBackBuffer = tmp
        }
    }

    companion object {
        val TAG: String = LaserScanView::class.java.simpleName
    }
}