package ru.hse.miem.ros.ui.views.details

import android.view.View
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.general.WidgetChangeListener
import ru.hse.miem.ros.viewmodel.DetailsViewModel

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 17.03.21
 */
abstract class DetailViewHolder : IBaseViewHolder {
    protected lateinit var entity: BaseEntity
    var itemView: View? = null
        set(value) {
            field = value
            value?.let {
                baseInitView(value)
                initView(value)
            }
        }
    abstract var viewModel: DetailsViewModel?

    lateinit var widgetChangeListener: WidgetChangeListener

    protected abstract fun initView(itemView: View)

    protected abstract fun bindEntity(entity: BaseEntity)

    protected abstract fun updateEntity(entity: BaseEntity)

    abstract override fun baseInitView(view: View)

    abstract override fun baseBindEntity(entity: BaseEntity)

    abstract override fun baseUpdateEntity(entity: BaseEntity)

    fun forceWidgetUpdate() {
        baseUpdateEntity(entity)
        updateEntity(entity)
        widgetChangeListener.onWidgetDetailsChanged(entity)
    }

    var widget: BaseEntity
        get() = this.entity
        set(value) {
            entity = value.copy()
            baseBindEntity(entity)
            bindEntity(entity)
        }
}
