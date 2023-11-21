package ru.hse.miem.ros.ui.views.details

import android.view.View
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 01.04.21
 */
abstract class SilentWidgetViewHolder() : DetailViewHolder() {
    private val widgetViewHolder: WidgetViewHolder = WidgetViewHolder(this)

    public override fun baseInitView(view: View) {
        widgetViewHolder.baseInitView(view)
    }

    public override fun baseBindEntity(entity: BaseEntity) {
        widgetViewHolder.baseBindEntity(entity)
    }

    public override fun baseUpdateEntity(entity: BaseEntity) {
        widgetViewHolder.baseUpdateEntity(entity)
    }
}
