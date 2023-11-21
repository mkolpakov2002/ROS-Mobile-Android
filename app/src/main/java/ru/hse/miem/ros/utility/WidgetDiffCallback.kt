package ru.hse.miem.ros.utility

import androidx.recyclerview.widget.DiffUtil
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.1
 * @created on 05.02.20
 * @updated on 24.09.20
 * @modified by Maxim Kolpakov
 */
class WidgetDiffCallback(var newWidgets: List<BaseEntity>, var oldWidgets: List<BaseEntity>) :
    DiffUtil.Callback() {
    public override fun getOldListSize(): Int {
        return oldWidgets.size
    }

    public override fun getNewListSize(): Int {
        return newWidgets.size
    }

    public override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldWidget: BaseEntity = oldWidgets[oldItemPosition]
        val newWidget: BaseEntity = newWidgets[newItemPosition]
        return oldWidget.id == newWidget.id
    }

    public override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldWidget: BaseEntity = oldWidgets[oldItemPosition]
        val newWidget: BaseEntity = newWidgets[newItemPosition]
        return (oldWidget == newWidget)
    }

    companion object {
        var TAG: String = WidgetDiffCallback::class.java.simpleName
    }
}
