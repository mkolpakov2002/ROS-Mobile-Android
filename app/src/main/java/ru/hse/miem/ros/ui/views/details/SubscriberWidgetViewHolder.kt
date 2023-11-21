package ru.hse.miem.ros.ui.views.details

import android.view.View
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.viewmodel.DetailsViewModel

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 17.03.21
 */
abstract class SubscriberWidgetViewHolder() : DetailViewHolder() {
    private val widgetViewHolder: WidgetViewHolder = WidgetViewHolder(this)
    private val subscriberViewHolder: SubscriberViewHolder = SubscriberViewHolder(this)

    init {
        subscriberViewHolder.topicTypes = getTopicTypes()
    }

    abstract fun getTopicTypes(): List<String>

    override var viewModel: DetailsViewModel? = null
        set(value) {
            field = value
            subscriberViewHolder.viewModel = viewModel
        }

    public override fun baseInitView(view: View) {
        widgetViewHolder.baseInitView(view)
        subscriberViewHolder.baseInitView(view)
    }

    public override fun baseBindEntity(entity: BaseEntity) {
        widgetViewHolder.baseBindEntity(entity)
        subscriberViewHolder.baseBindEntity(entity)
    }

    public override fun baseUpdateEntity(entity: BaseEntity) {
        widgetViewHolder.baseUpdateEntity(entity)
        subscriberViewHolder.baseUpdateEntity(entity)
    }
}
