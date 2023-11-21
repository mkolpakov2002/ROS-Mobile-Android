package ru.hse.miem.ros.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.RosData
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.AbstractNode
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import ru.hse.miem.ros.domain.RosDomain
import kotlinx.coroutines.launch

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.2
 * @created on 10.01.20
 * @updated on 21.04.20
 * @modified by Tanya Rykova
 */
class VizViewModel(application: Application) : AndroidViewModel(application) {
    private val rosDomain: RosDomain

    init {
        rosDomain = RosDomain.getInstance(application)
    }

    fun updateWidget(widget: BaseEntity) {
        viewModelScope.launch {
            rosDomain.updateWidget(null, widget)
        }
    }

    val currentWidgets: LiveData<List<BaseEntity>>
        get() {
            return rosDomain.currentWidgets
        }
    val data: LiveData<RosData>
        get() {
            return rosDomain.data
        }

    fun publishData(data: BaseData) {
        rosDomain.publishData(data)
    }

    val lastRosData: HashMap<Topic?, AbstractNode>
        get() {
            return rosDomain.lastRosData
        }

    companion object {
        private val TAG: String = VizViewModel::class.java.simpleName
    }
}
