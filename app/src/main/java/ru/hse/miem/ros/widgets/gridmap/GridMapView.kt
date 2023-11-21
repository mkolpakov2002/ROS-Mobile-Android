package ru.hse.miem.ros.widgets.gridmap

import android.content.Context
import ru.hse.miem.ros.ui.opengl.visualisation.ROSColor
import ru.hse.miem.ros.ui.opengl.visualisation.TextureBitmap
import ru.hse.miem.ros.ui.opengl.visualisation.Tile
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import ru.hse.miem.ros.ui.views.widgets.SubscriberLayerView
import nav_msgs.OccupancyGrid
import org.jboss.netty.buffer.ChannelBuffer
import org.ros.internal.message.Message
import org.ros.namespace.GraphName
import org.ros.rosjava_geometry.Quaternion
import org.ros.rosjava_geometry.Transform
import org.ros.rosjava_geometry.Vector3
import javax.microedition.khronos.opengles.GL10
import kotlin.math.ceil

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 08.03.21
 */
class GridMapView(context: Context?) : SubscriberLayerView(context) {
    private val tiles: MutableList<Tile>
    private var previousGl: GL10? = null
    private lateinit var colorMap: IntArray

    init {
        tiles = ArrayList()
        createColorMap()
    }

    private fun createColorMap() {
        val colorLow = ROSColor(1f, 1f, 1f, 1f)
        val colorHigh = ROSColor(0f, 0f, 0f, 1f)
        colorMap = IntArray(101)
        for (i in 0..100) {
            val fraction: Float = i.toFloat() / 101
            colorMap[i] = colorLow.interpolate(colorHigh, fraction).toInt()
        }
    }

    public override fun draw(view: VisualizationView, gl: GL10) {
        if (previousGl !== gl) {
            for (tile: Tile in tiles) {
                tile.clearHandle()
            }
            previousGl = gl
        }
        for (tile: Tile in tiles) {
            tile.draw(view, gl)
        }
    }

    public override fun onNewMessage(message: Message) {
        val grid: OccupancyGrid = message as OccupancyGrid
        val resolution: Float = grid.info.resolution
        val width: Int = grid.info.width
        val height: Int = grid.info.height
        val numTilesWide: Int = ceil((width / TextureBitmap.STRIDE.toFloat()).toDouble())
            .toInt()
        val numTilesHigh: Int = ceil((height / TextureBitmap.STRIDE.toFloat()).toDouble())
            .toInt()
        val numTiles: Int = numTilesWide * numTilesHigh
        val origin: Transform = Transform.fromPoseMessage(grid.info.origin)
        while (tiles.size < numTiles) {
            tiles.add(Tile(resolution))
        }
        for (y in 0 until numTilesHigh) {
            for (x in 0 until numTilesWide) {
                val tileIndex: Int = y * numTilesWide + x
                tiles[tileIndex].setOrigin(
                    origin.multiply(
                        Transform(
                            Vector3(
                                (
                                        x * resolution * TextureBitmap.STRIDE).toDouble(),
                                (
                                        y * resolution * TextureBitmap.HEIGHT).toDouble(),
                                0.0
                            ),
                            Quaternion.identity()
                        )
                    )
                )
                val isLastColumn: Boolean = (x == numTilesWide - 1)
                val stride: Int = if (isLastColumn) {
                    width % TextureBitmap.STRIDE
                } else {
                    TextureBitmap.STRIDE
                }
                tiles[tileIndex].setStride(stride)
                val isLastRow: Boolean = (y == numTilesHigh - 1)
                val tileHeight: Int = if (isLastRow) {
                    height % TextureBitmap.HEIGHT
                } else {
                    TextureBitmap.HEIGHT
                }
                tiles[tileIndex].setHeight(tileHeight)
            }
        }
        var x = 0
        var y = 0
        val buffer: ChannelBuffer = grid.data
        while (buffer.readable()) {
            val tileIndex: Int =
                (y / TextureBitmap.STRIDE) * numTilesWide + x / TextureBitmap.STRIDE
            val pixel: Byte = buffer.readByte()
            if (pixel.toInt() == -1) {
                tiles[tileIndex].writeInt(COLOR_UNKNOWN)
            } else {
                tiles[tileIndex].writeInt(colorMap[pixel.toInt()])
            }
            ++x
            if (x == width) {
                x = 0
                ++y
            }
        }
        for (tile: Tile in tiles) {
            tile.update()
        }
        frame = GraphName.of(grid.header.frameId)
    }

    companion object {
        val TAG: String = GridMapView::class.java.simpleName
        private val COLOR_UNKNOWN: Int = -0x777778
    }
}