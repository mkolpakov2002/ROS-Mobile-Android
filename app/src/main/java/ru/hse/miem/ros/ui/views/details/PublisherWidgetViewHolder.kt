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
abstract class PublisherWidgetViewHolder() : DetailViewHolder() {
    private val widgetViewHolder: WidgetViewHolder = WidgetViewHolder(this)
    private val publisherViewHolder: PublisherViewHolder = PublisherViewHolder(this)

    init {
        publisherViewHolder.topicTypes = getTopicTypes()
    }

    abstract fun getTopicTypes(): List<String>

    override var viewModel: DetailsViewModel? = null
        set(value) {
            field = value
            publisherViewHolder.viewModel = value
        }

    public override fun baseInitView(view: View) {
        widgetViewHolder.baseInitView(view)
        publisherViewHolder.baseInitView(view)
    }

    public override fun baseBindEntity(entity: BaseEntity) {
        widgetViewHolder.baseBindEntity(entity)
        publisherViewHolder.baseBindEntity(entity)
    }

    public override fun baseUpdateEntity(entity: BaseEntity) {
        widgetViewHolder.baseUpdateEntity(entity)
        publisherViewHolder.baseUpdateEntity(entity)
    }
}
