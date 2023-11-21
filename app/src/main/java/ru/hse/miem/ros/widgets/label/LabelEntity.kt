package ru.hse.miem.ros.widgets.label

import ru.hse.miem.ros.data.model.entities.widgets.SilentWidgetEntity

/**
 * TODO: Description
 *
 * @author Dragos Circa
 * @version 1.0.0
 * @created on 02.11.2020
 * @updated on 18.11.2020
 * @modified by Tanya Rykova
 * @updated on 01.04.2021
 * @modified by Maxim Kolpakov
 */
class LabelEntity() : SilentWidgetEntity() {
    var text: String
    var rotation: Int

    init {
        width = 3
        height = 1
        text = "A label"
        rotation = 0
    }
}
