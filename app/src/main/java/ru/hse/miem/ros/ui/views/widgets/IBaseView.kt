package ru.hse.miem.ros.ui.views.widgets

import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.03.21
 */
interface IBaseView {
    var widgetEntity: BaseEntity?
    fun sameWidgetEntity(other: BaseEntity): Boolean
}
