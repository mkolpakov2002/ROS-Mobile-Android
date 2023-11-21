package ru.hse.miem.ros.ui.general

import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 15.03.20
 * @updated on 15.03.20
 * @modified by
 */
fun interface DataListener {
    fun onNewWidgetData(data: BaseData)
}
