package ru.hse.miem.ros.widgets.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import org.jboss.netty.buffer.ChannelBuffer
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import sensor_msgs.CompressedImage
import sensor_msgs.Image

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.1
 * @created on 27.04.2020
 * @updated on 13.05.2020
 * @modified by Maxim Kolpakov
 * @updated on 10.09.2020
 * @modified by Maxim Kolpakov
 */
class CameraData : BaseData {
    var map: Bitmap?

    constructor(image: CompressedImage) {
        map = this.convert(image)
    }

    constructor(image: Image) {
        map = this.convert(image)
    }

    private fun convert(image: CompressedImage): Bitmap {
        val buffer: ChannelBuffer = image.data
        return BitmapFactory.decodeByteArray(
            buffer.array(),
            buffer.arrayOffset(),
            buffer.readableBytes()
        )
    }

    private fun convert(image: Image): Bitmap? {
        var config: Bitmap.Config? = null

        // Get the data
        val data: ByteArray = image.data.array()
        val height: Int = image.height
        val width: Int = image.width
        val step: Int = image.step

        // Get the starting point of the data
        val dataStart: Int = data.size - (height * step)
        val pixelBytesNum: Int = step / width

        // Encode Byte and transform to image
        var iStep: Int
        var iWidth: Int
        var dataStep: Int
        var iColor: Int
        var lColor: Long

        // Storage capacities
        val intArray = IntArray(height * width)
        val longArray = LongArray(height * width)
        var iR: Int
        var iG: Int
        var iB: Int
        var iA: Int
        var iM: Int
        var lR: Long
        var lG: Long
        var lB: Long
        var lA: Long
        var lM: Long

        // Init data extraction steps
        val monoX0: Int
        val monoX1: Int
        val rx0: Int
        val rx1: Int
        val gx0: Int
        val gx1: Int
        val bx0: Int
        val bx1: Int
        val ax0: Int
        val ax1: Int
        when (image.encoding) {
            "rgb8" -> {
                var i: Int = 0
                while (i < height) {
                    iStep = i * step
                    iWidth = i * width
                    var j: Int = 0
                    while (j < width) {
                        dataStep = dataStart + iStep + (j * pixelBytesNum)
                        iR = data.get(dataStep).toInt()
                        iG = data.get(dataStep + 1).toInt()
                        iB = data.get(dataStep + 2).toInt()
                        iColor =
                            -16777216 or ((iR and 0xff) shl 16) or ((iG and 0xff) shl 8) or (iB and 0xff)
                        intArray[iWidth + j] = iColor
                        j++
                    }
                    i++
                }
                config = Bitmap.Config.ARGB_8888
            }

            "rgba8" -> {
                var i: Int = 0
                while (i < height) {
                    iStep = i * step
                    iWidth = i * width
                    var j: Int = 0
                    while (j < width) {
                        dataStep = dataStart + iStep + (j * pixelBytesNum)
                        iR = data[dataStep].toInt()
                        iG = data[dataStep + 1].toInt()
                        iB = data[dataStep + 2].toInt()
                        iA = data[dataStep + 3].toInt()
                        iColor =
                            (((iA and 0xff) shl 24) or ((iR and 0xff) shl 16) or ((iG and 0xff) shl 8) or (iB and 0xff))
                        intArray[iWidth + j] = iColor
                        j++
                    }
                    i++
                }
                config = Bitmap.Config.ARGB_8888
            }

            "bgr8" -> {
                iA = 255
                var i: Int = 0
                while (i < height) {
                    iStep = i * step
                    iWidth = i * width
                    var j: Int = 0
                    while (j < width) {
                        dataStep = dataStart + iStep + (j * pixelBytesNum)
                        iB = data[dataStep].toInt()
                        iG = data[dataStep + 1].toInt()
                        iR = data[dataStep + 2].toInt()
                        iColor =
                            (((iA and 0xff) shl 24) or ((iR and 0xff) shl 16) or ((iG and 0xff) shl 8) or (iB and 0xff))
                        intArray[iWidth + j] = iColor
                        j++
                    }
                    i++
                }
                config = Bitmap.Config.ARGB_8888
            }

            "bgra8" -> {
                var i: Int = 0
                while (i < height) {
                    iStep = i * step
                    iWidth = i * width
                    var j: Int = 0
                    while (j < width) {
                        dataStep = dataStart + iStep + (j * pixelBytesNum)
                        iB = data[dataStep].toInt()
                        iG = data[dataStep + 1].toInt()
                        iR = data[dataStep + 2].toInt()
                        iA = data[dataStep + 3].toInt()
                        iColor =
                            (((iA and 0xff) shl 24) or ((iR and 0xff) shl 16) or ((iG and 0xff) shl 8) or (iB and 0xff))
                        intArray[iWidth + j] = iColor
                        j++
                    }
                    i++
                }
                config = Bitmap.Config.ARGB_8888
            }

            "mono8" -> {
                iA = 255
                var i: Int = 0
                while (i < height) {
                    iStep = i * step
                    iWidth = i * width
                    var j: Int = 0
                    while (j < width) {
                        dataStep = dataStart + iStep + (j * pixelBytesNum)
                        iM = data[dataStep].toInt()
                        iColor =
                            (((iA and 0xff) shl 24) or ((iM and 0xff) shl 16) or ((iM and 0xff) shl 8) or (iM and 0xff))
                        intArray[iWidth + j] = iColor
                        j++
                    }
                    i++
                }
                config = Bitmap.Config.ARGB_8888
            }

            "rgb16" -> {
                lA = 65535
                if (image.isBigendian.toInt() == 0) {
                    rx0 = 0
                    rx1 = 1
                    gx0 = 2
                    gx1 = 3
                    bx0 = 4
                    bx1 = 5
                } else {
                    rx0 = 1
                    rx1 = 0
                    gx0 = 3
                    gx1 = 2
                    bx0 = 5
                    bx1 = 4
                }
                var i: Int = 0
                while (i < height) {
                    iStep = i * step
                    iWidth = i * width
                    var j: Int = 0
                    while (j < width) {
                        dataStep = dataStart + iStep + (j * pixelBytesNum)
                        lR = (data[dataStep + rx0].toInt() or (data.get(dataStep + rx1)
                            .toInt() shl 8)).toLong()
                        lG = (data[dataStep + gx0].toInt() or (data.get(dataStep + gx1)
                            .toInt() shl 8)).toLong()
                        lB = (data[dataStep + bx0].toInt() or (data.get(dataStep + bx1)
                            .toInt() shl 8)).toLong()
                        lColor =
                            (((lR and 0xffffL) shl 48) or ((lG and 0xffffL) shl 32) or ((lB and 0xffffL) shl 16) or (lA and 0xffffL))
                        longArray[iWidth + j] = lColor
                        j++
                    }
                    i++
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    config = Bitmap.Config.RGBA_F16
                }
            }

            "rgba16" -> {
                if (image.isBigendian.toInt() == 0) {
                    rx0 = 0
                    rx1 = 1
                    gx0 = 2
                    gx1 = 3
                    bx0 = 4
                    bx1 = 5
                    ax0 = 6
                    ax1 = 7
                } else {
                    rx0 = 1
                    rx1 = 0
                    gx0 = 3
                    gx1 = 2
                    bx0 = 5
                    bx1 = 4
                    ax0 = 7
                    ax1 = 6
                }
                var i: Int = 0
                while (i < height) {
                    iStep = i * step
                    iWidth = i * width
                    var j: Int = 0
                    while (j < width) {
                        dataStep = dataStart + iStep + (j * pixelBytesNum)
                        val R: Long = (data[dataStep + rx0].toInt() or (data.get(dataStep + rx1)
                            .toInt() shl 8)).toLong()
                        val G: Long = (data[dataStep + gx0].toInt() or (data.get(dataStep + gx1)
                            .toInt() shl 8)).toLong()
                        val B: Long = (data[dataStep + bx0].toInt() or (data.get(dataStep + bx1)
                            .toInt() shl 8)).toLong()
                        val A: Long = (data[dataStep + ax0].toInt() or (data.get(dataStep + ax1)
                            .toInt() shl 8)).toLong()
                        lColor =
                            (((R and 0xffffL) shl 48) or ((G and 0xffffL) shl 32) or ((B and 0xffffL) shl 16) or (A and 0xffffL))
                        longArray[iWidth + j] = lColor
                        j++
                    }
                    i++
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    config = Bitmap.Config.RGBA_F16
                }
            }

            "bgr16" -> {
                lA = 65535
                if (image.isBigendian.toInt() == 0) {
                    bx0 = 0
                    bx1 = 1
                    gx0 = 2
                    gx1 = 3
                    rx0 = 4
                    rx1 = 5
                } else {
                    bx0 = 1
                    bx1 = 0
                    gx0 = 3
                    gx1 = 2
                    rx0 = 5
                    rx1 = 4
                }
                var i: Int = 0
                while (i < height) {
                    iStep = i * step
                    iWidth = i * width
                    var j: Int = 0
                    while (j < width) {
                        dataStep = dataStart + iStep + (j * pixelBytesNum)
                        lB = (data[dataStep + bx0].toInt() or (data.get(dataStep + bx1)
                            .toInt() shl 8)).toLong()
                        lG = (data[dataStep + gx0].toInt() or (data.get(dataStep + gx1)
                            .toInt() shl 8)).toLong()
                        lR = (data[dataStep + rx0].toInt() or (data.get(dataStep + rx1)
                            .toInt() shl 8)).toLong()
                        lColor =
                            (((lR and 0xffffL) shl 48) or ((lG and 0xffffL) shl 32) or ((lB and 0xffffL) shl 16) or (lA and 0xffffL))
                        longArray[iWidth + j] = lColor
                        j++
                    }
                    i++
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    config = Bitmap.Config.RGBA_F16
                }
            }

            "bgra16" -> {
                if (image.isBigendian.toInt() == 0) {
                    bx0 = 0
                    bx1 = 1
                    gx0 = 2
                    gx1 = 3
                    rx0 = 4
                    rx1 = 5
                    ax0 = 6
                    ax1 = 7
                } else {
                    bx0 = 1
                    bx1 = 0
                    gx0 = 3
                    gx1 = 2
                    rx0 = 5
                    rx1 = 4
                    ax0 = 7
                    ax1 = 6
                }
                var i: Int = 0
                while (i < height) {
                    iStep = i * step
                    iWidth = i * width
                    var j: Int = 0
                    while (j < width) {
                        dataStep = dataStart + iStep + (j * pixelBytesNum)
                        lB = (data.get(dataStep + bx0).toInt() or (data.get(dataStep + bx1)
                            .toInt() shl 8)).toLong()
                        lG = (data.get(dataStep + gx0).toInt() or (data.get(dataStep + gx1)
                            .toInt() shl 8)).toLong()
                        lR = (data.get(dataStep + rx0).toInt() or (data.get(dataStep + rx1)
                            .toInt() shl 8)).toLong()
                        lA = (data.get(dataStep + ax0).toInt() or (data.get(dataStep + ax1)
                            .toInt() shl 8)).toLong()
                        lColor =
                            (((lR and 0xffffL) shl 48) or ((lG and 0xffffL) shl 32) or ((lB and 0xffffL) shl 16) or (lA and 0xffffL))
                        longArray[iWidth + j] = lColor
                        j++
                    }
                    i++
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    config = Bitmap.Config.RGBA_F16
                }
            }

            "mono16" -> {
                val A: Long = 65535
                if (image.isBigendian.toInt() == 0) {
                    monoX0 = 0
                    monoX1 = 1
                } else {
                    monoX0 = 1
                    monoX1 = 0
                }
                var i: Int = 0
                while (i < height) {
                    iStep = i * step
                    iWidth = i * width
                    var j: Int = 0
                    while (j < width) {
                        dataStep = dataStart + iStep + (j * pixelBytesNum)
                        lM = (data.get(dataStep + monoX0).toInt() or (data.get(dataStep + monoX1)
                            .toInt() shl 8)).toLong()
                        lColor =
                            (((lM and 0xffffL) shl 48) or ((lM and 0xffffL) shl 32) or ((lM and 0xffffL) shl 16) or (A and 0xffffL))
                        longArray[iWidth + j] = lColor
                        j++
                    }
                    i++
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    config = Bitmap.Config.RGBA_F16
                }
            }

            else -> Log.i(TAG, "No compatible encoding!")
        }

        // Create the bitmap if config is set and image is creatable
        return if (config != null) {
            Bitmap.createBitmap(intArray, width, height, config)
        } else {
            null
        }
    }

    companion object {
        val TAG: String = "CameraData"
    }
}
