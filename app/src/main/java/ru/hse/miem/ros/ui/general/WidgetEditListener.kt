package ru.hse.miem.ros.ui.general

import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity

/**
 * TODO: Description
 *
 * @author Sarthak Mittal
 * @version 1.0.1
 * @created on 01.07.21
 */
fun interface WidgetEditListener {
    fun onWidgetEdited(widgetEntity: BaseEntity, updateConfig: Boolean)
}
