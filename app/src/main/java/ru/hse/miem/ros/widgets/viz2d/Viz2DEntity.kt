package ru.hse.miem.ros.widgets.viz2d

import ru.hse.miem.ros.data.model.entities.widgets.GroupEntity

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 08.03.21
 */
class Viz2DEntity() : GroupEntity() {
    var frame: String

    init {
        width = 8
        height = 8
        frame = "map"
    }
}
