package ru.hse.miem.ros.data.model.repositories

import androidx.lifecycle.LiveData
import ru.hse.miem.ros.data.model.entities.ConfigEntity
import ru.hse.miem.ros.data.model.entities.MasterEntity
import ru.hse.miem.ros.data.model.entities.SSHEntity
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity


/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.2
 * @created on 26.01.20
 * @updated on 11.04.20
 * @modified by Tanya Rykova
 * @updated on 04.06.20
 * @modified by Tanya Rykova
 * @updated on 27.07.20
 * @modified by Tanya Rykova
 */
interface ConfigRepository {
    fun chooseConfig(configId: Long)
    suspend fun createConfig(configName: String?)
    suspend fun removeConfig(configId: Long)
    suspend fun updateConfig(config: ConfigEntity)
    val allConfigs: LiveData<List<ConfigEntity>>
    val currentConfigId: LiveData<Long>
    fun getConfig(id: Long): LiveData<ConfigEntity>
    val currentConfig: LiveData<ConfigEntity>
    suspend fun updateMaster(master: MasterEntity)
    fun getMaster(configId: Long): LiveData<MasterEntity>
    suspend fun addWidget(parentId: Long, widget: BaseEntity)
    suspend fun createWidget(parentId: Long, widgetType: String)
    suspend fun deleteWidget(parentId: Long, widget: BaseEntity)
    suspend fun updateWidget(parentId: Long?, widget: BaseEntity)
    fun findWidget(widgetId: Long): LiveData<BaseEntity>
    fun getWidgets(id: Long): LiveData<List<BaseEntity>>
    suspend fun updateSSH(ssh: SSHEntity)
    fun setSSH(ssh: SSHEntity, configId: String)
    fun getSSH(configId: Long): LiveData<SSHEntity>
}
