package ru.hse.miem.ros.utility

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.min

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.09.20
 * @updated on
 * @modified by
 */
class TiledBitmap(options: Options) {
    private val mArray // array where chunks is stored
            : Array<Array<Bitmap?>>
    val width // original (full) width of source image
            : Int
    val height // original (full) height of source image
            : Int
    private val mChunkWidth // default width of a chunk
            : Int
    private val mChunkHeight // default height of a chunk
            : Int

    constructor(src: Bitmap) : this(Options(src, 100, 100))

    init {
        mArray = divideBitmap(options)
        width = options.source.width
        height = options.source.height
        mChunkWidth = options.chunkWidth
        mChunkHeight = options.chunkHeight
    }

    fun getChunk(x: Int, y: Int): Bitmap? {
        if ((mArray.size < x) && (x > 0) && (mArray[x].size < y) && (y > 0)) {
            return mArray[x][y]
        }
        return null
    }

    /**
     * x, y are viewport coords on the image itself;
     * w, h are viewport's width and height.
     */
    fun draw(canvas: Canvas, x: Int, y: Int, w: Int, h: Int, paint: Paint?) {
        if ((x >= width) || (y >= height) || (x + w <= 0) || (y + h <= 0)) return
        val i1: Int = x / mChunkWidth // i1 and j1 are indices of visible chunk that is
        val j1: Int = y / mChunkHeight // on the top-left corner of the screen
        var i2: Int = (x + w) / mChunkWidth // i2 and j2 are indices of visible chunk that is
        var j2: Int = (y + h) / mChunkHeight // on the right-bottom corner of the screen
        i2 = if (i2 >= mArray.size) mArray.size - 1 else i2
        j2 = if (j2 >= mArray[i2].size) mArray[i2].size - 1 else j2
        val offsetX: Int = x - i1 * mChunkWidth
        val offsetY: Int = y - j1 * mChunkHeight
        for (i in i1..i2) {
            for (j in j1..j2) {
                canvas.drawBitmap(
                    (mArray[i][j])!!,
                    (
                            (i - i1) * mChunkWidth - offsetX).toFloat(),
                    (
                            (j - j1) * mChunkHeight - offsetY).toFloat(),
                    paint
                )
            }
        }
    }

    class Options {
        val chunkWidth: Int
        val chunkHeight: Int
        val xCount: Int
        val yCount: Int
        val source: Bitmap

        constructor(src: Bitmap, chunkW: Int, chunkH: Int) {
            chunkWidth = chunkW
            chunkHeight = chunkH
            xCount = ((src.width - 1) / chunkW) + 1
            yCount = ((src.height - 1) / chunkH) + 1
            source = src
        }

        constructor(xc: Int, yc: Int, src: Bitmap) {
            xCount = xc
            yCount = yc
            chunkWidth = src.width / xCount
            chunkHeight = src.height / yCount
            source = src
        }
    }

    companion object {
        fun divideBitmap(bitmap: Bitmap): Array<Array<Bitmap?>> {
            return divideBitmap(Options(bitmap, 100, 100))
        }

        fun divideBitmap(options: Options): Array<Array<Bitmap?>> {
            val arr: Array<Array<Bitmap?>> = Array(options.xCount) { arrayOfNulls(options.yCount) }
            for (x in 0 until options.xCount) {
                for (y in 0 until options.yCount) {
                    val w: Int = min(
                        options.chunkWidth.toDouble(),
                        (options.source.width - (x * options.chunkWidth)).toDouble()
                    ).toInt()
                    val h: Int = min(
                        options.chunkHeight.toDouble(),
                        (options.source.height - (y * options.chunkHeight)).toDouble()
                    ).toInt()
                    arr[x][y] = Bitmap.createBitmap(
                        options.source,
                        x * options.chunkWidth,
                        y * options.chunkHeight,
                        w,
                        h
                    )
                }
            }
            return arr
        }
    }
}