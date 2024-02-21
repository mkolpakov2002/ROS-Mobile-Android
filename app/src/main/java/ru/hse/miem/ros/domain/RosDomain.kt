package ru.hse.miem.ros.domain

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import ru.hse.miem.ros.data.model.entities.MasterEntity
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.repositories.ConfigRepository
import ru.hse.miem.ros.data.model.repositories.ConfigRepositoryImpl
import ru.hse.miem.ros.data.model.repositories.rosRepo.RosRepository
import ru.hse.miem.ros.data.model.repositories.rosRepo.connection.ConnectionType
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.RosData
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.AbstractNode
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.3
 * @created on 07.04.20
 * @updated on 15.04.20
 * @modified by Maxim Kolpakov
 * @updated on 15.05.20
 * @modified by Maxim Kolpakov
 * @updated on 27.07.20
 * @modified by Tanya Rykova
 * @updated on 16.11.20
 * @modified by Tanya Rykova
 * @updated on 10.03.21
 * @modified by Maxim Kolpakov
 */
class RosDomain private constructor(application: Application) {
    // Repositories
    private val configRepository: ConfigRepository = ConfigRepositoryImpl.getInstance(application)
    private val rosRepo: RosRepository = RosRepository.getInstance(application)

    // Data objects

    // React on config change and get the new data
    val currentWidgets: LiveData<List<BaseEntity>> = configRepository.currentConfigId
        .switchMap { id: Long -> configRepository.getWidgets(id) }
    val currentMaster: LiveData<MasterEntity?> =
        configRepository.currentConfigId.switchMap { configId: Long ->
                configRepository.getMaster(configId)
            }

    init {
        currentWidgets.observeForever { newWidgets: List<BaseEntity> ->
            rosRepo.updateWidgets(
                newWidgets
            )
        }
        currentMaster.observeForever { master: MasterEntity? ->
            rosRepo.updateMaster(master)
        }
    }

    fun publishData(data: BaseData) {
        rosRepo.publishData(data)
    }

    suspend fun createWidget(parentId: Long?, widgetType: String) {
        configRepository.createWidget(parentId, widgetType)
    }

    suspend fun addWidget(parentId: Long?, widget: BaseEntity) {
        configRepository.addWidget(parentId, widget)
    }

    suspend fun updateWidget(parentId: Long?, widget: BaseEntity) {
        configRepository.updateWidget(parentId, widget)
    }

    suspend fun deleteWidget(parentId: Long?, widget: BaseEntity) {
        configRepository.deleteWidget(parentId, widget)
    }

    fun findWidget(widgetId: Long): LiveData<BaseEntity> {
        return configRepository.findWidget(widgetId)
    }

    val data: LiveData<RosData>
        get() = rosRepo.data

    suspend fun updateMaster(master: MasterEntity) {
        configRepository.updateMaster(master)
    }

    fun setMasterDeviceIp(deviceIp: String) {
        rosRepo.setMasterDeviceIp(deviceIp)
    }

    fun connectToMaster() {
        rosRepo.connectToMaster()
    }

    fun disconnectFromMaster() {
        rosRepo.disconnectFromMaster()
    }

    val rosConnection: LiveData<ConnectionType>
        get() = rosRepo.rosConnectionStatus
    val topicList: List<Topic>
        get() = rosRepo.topicList
    val lastRosData: HashMap<Topic?, AbstractNode>
        get() = rosRepo.lastRosData

    companion object {
        private val TAG = RosDomain::class.java.simpleName

        // Singleton instance
        private lateinit var mInstance: RosDomain
        fun getInstance(application: Application): RosDomain {
            if (!this::mInstance.isInitialized) {
                mInstance = RosDomain(application)
            }
            return mInstance
        }
    }
}
