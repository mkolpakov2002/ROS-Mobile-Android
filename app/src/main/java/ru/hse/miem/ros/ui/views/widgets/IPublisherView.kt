package ru.hse.miem.ros.ui.views.widgets

import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import ru.hse.miem.ros.ui.general.DataListener

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.03.21
 */
interface IPublisherView {
    fun publishViewData(data: BaseData)
    var dataListener: DataListener?
}
