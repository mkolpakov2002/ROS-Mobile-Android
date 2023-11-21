package ru.hse.miem.ros.ui.opengl.visualisation

import nav_msgs.OccupancyGrid
import org.jboss.netty.buffer.ChannelBuffer
import org.ros.internal.message.MessageBuffers
import org.ros.rosjava_geometry.Transform
import javax.microedition.khronos.opengles.GL10

/**
 * In order to draw maps with a size outside the maximum size of a texture,
 * we split the map into multiple tiles and draw one texture per tile.
 *
 * @author moesenle@google.com (Lorenz Moesenlechner)
 * @version 2.0
 * @updated on 08.03.21
 */
class Tile(
    /**
     * Resolution of the [OccupancyGrid].
     */
    private val resolution: Float
) {
    private val pixelBuffer: ChannelBuffer = MessageBuffers.dynamicBuffer()
    private val textureBitmap: TextureBitmap = TextureBitmap()

    /**
     * Points to the top left of the [Tile].
     */
    private lateinit var origin: Transform

    /**
     * Width of the [Tile].
     */
    private var stride: Int = 0

    /**
     * Height of the [Tile].
     */
    private var height: Int = 0

    /**
     * `true` when the [Tile] is ready to be drawn.
     */
    private var ready: Boolean = false
    fun draw(view: VisualizationView, gl: GL10) {
        if (ready) {
            textureBitmap.draw(view, gl)
        }
    }

    fun clearHandle() {
        textureBitmap.clearHandle()
    }

    fun writeInt(value: Int) {
        pixelBuffer.writeInt(value)
    }

    fun update() {
        textureBitmap.updateFromPixelBuffer(
            pixelBuffer,
            stride,
            height,
            resolution,
            origin,
            COLOR_TRANSPARENT
        )
        pixelBuffer.clear()
        ready = true
    }

    fun setOrigin(origin: Transform) {
        this.origin = origin
    }

    fun setStride(stride: Int) {
        this.stride = stride
    }

    fun setHeight(height: Int) {
        this.height = height
    }

    companion object {
        /**
         * Color of transparent cells in the map.
         */
        private val COLOR_TRANSPARENT: Int = 0x00000000
    }
}
