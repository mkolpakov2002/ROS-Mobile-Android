package ru.hse.miem.ros.ui.views.details

import android.view.View
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 17.03.21
 */
internal interface IBaseViewHolder {
    fun baseInitView(view: View)
    fun baseBindEntity(entity: BaseEntity)
    fun baseUpdateEntity(entity: BaseEntity)
}
