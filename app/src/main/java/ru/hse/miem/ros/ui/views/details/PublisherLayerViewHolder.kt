package ru.hse.miem.ros.ui.views.details

import android.view.View
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.viewmodel.DetailsViewModel

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 26.05.2021
 */
abstract class PublisherLayerViewHolder() : DetailViewHolder() {
    private val layerViewHolder: LayerViewHolder = LayerViewHolder(this)
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
        layerViewHolder.baseInitView(view)
        publisherViewHolder.baseInitView(view)
    }

    public override fun baseBindEntity(entity: BaseEntity) {
        layerViewHolder.baseBindEntity(entity)
        publisherViewHolder.baseBindEntity(entity)
    }

    public override fun baseUpdateEntity(entity: BaseEntity) {
        layerViewHolder.baseUpdateEntity(entity)
        publisherViewHolder.baseUpdateEntity(entity)
    }
}
