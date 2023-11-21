package ru.hse.miem.ros.ui.general

import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.1
 * @created on 17.03.20
 * @updated on 27.10.2020
 * @modified by Maxim Kolpakov
 */
interface WidgetChangeListener {
    fun onWidgetDetailsChanged(widgetEntity: BaseEntity)
}
