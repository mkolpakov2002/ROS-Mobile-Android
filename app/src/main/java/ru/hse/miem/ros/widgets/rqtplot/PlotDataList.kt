package ru.hse.miem.ros.widgets.rqtplot

import org.ros.message.Duration
import org.ros.message.Time
import ru.hse.miem.ros.widgets.rqtplot.PlotDataList.PlotData
import kotlin.math.max
import kotlin.math.min

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 31.05.21
 */
class PlotDataList() : ArrayList<PlotData>() {
    var maxTime: Float = 10f
        set(value) {
            field = value
            cleanUp()
        }
    var maxNum: Int = 1000
    var minValue: Double = Double.MAX_VALUE
    var maxValue: Double = -Double.MAX_VALUE
    var latestTime: Time? = null
    fun add(value: Double, time: Time?) {
        this.add(PlotData(value, time))
        updateMinMax(value)
        cleanUp()
    }

    private fun cleanUp() {
        while (!this.isEmpty()) {
            if (size > maxNum || this[0].secsToLatest() > maxTime) {
                removeAt(0)
            } else {
                break
            }
        }
    }

    private fun updateMinMax(value: Double) {
        minValue = min(value, minValue)
        maxValue = max(value, maxValue)
        if (!isEmpty()) {
            latestTime = this[size - 1].time
        }
    }

    inner class PlotData(var value: Double, var time: Time?) {
        fun secsToLatest(): Float {
            val diff: Duration = latestTime!!.subtract(time)
            return diff.secs + diff.nsecs / 1000000000f
        }
    }
}
