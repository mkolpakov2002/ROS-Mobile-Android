package ru.hse.miem.ros.widgets.gps

import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import sensor_msgs.NavSatFix

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 05.05.20
 * @updated on 05.05.20
 * @modified by
 */
class GpsData(private val navSatFix: NavSatFix) : BaseData() {
    val lat: Double
        get() {
            return navSatFix.latitude
        }
    val lon: Double
        get() {
            return navSatFix.longitude
        }
}
