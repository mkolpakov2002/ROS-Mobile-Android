package ru.hse.miem.ros.widgets.logger

import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData


/**
 * TODO: Description
 *
 * @author Dragos Circa
 * @version 1.0.0
 * @created on 02.11.2020
 * @updated on 18.11.2020
 * @modified by Tanya Rykova
 */
class LoggerData(message: std_msgs.String) : BaseData() {
    var data: String

    init {
        data = message.data
    }
}
