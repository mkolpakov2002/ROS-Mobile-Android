package ru.hse.miem.ros.data.model.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.ConfigEntity
import ru.hse.miem.ros.data.model.entities.MasterEntity
import ru.hse.miem.ros.data.model.entities.SSHEntity
import ru.hse.miem.ros.data.model.entities.WidgetStorageData
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.utility.Constants

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.4
 * @created on 31.01.20
 * @updated on 15.05.20
 * @modified by Maxim Kolpakov
 * @updated on 04.06.20
 * @modified by Tanya Rykova
 * @updated on 27.07.20
 * @modified by Maxim Kolpakov
 * @updated on 27.07.20
 * @modified by Tanya Rykova
 * @updated on 23.09.20
 * @modified by Maxim Kolpakov
 */
@Database(
    entities = [ConfigEntity::class, MasterEntity::class, WidgetStorageData::class, SSHEntity::class],
    version = 6,
    exportSchema = false
)
abstract class DataStorage : RoomDatabase() {
    // DAO Methods ---------------------------------------------------------------------------------
    abstract fun configDao(): ConfigDao
    abstract fun masterDao(): MasterDao
    abstract fun widgetDao(): WidgetDao
    abstract fun sshDao(): SSHDao

    // Config methods ------------------------------------------------------------------------------
    suspend fun addConfig(config: ConfigEntity) {
        configDao().insert(config)
    }

    suspend fun updateConfig(config: ConfigEntity) {
        configDao().update(config)
    }

    suspend fun deleteConfig(config: ConfigEntity) {
        configDao().delete(config)
    }

    suspend fun deleteConfig(id: Long) {
        configDao().removeConfig(id)
        masterDao().delete(id)
        sshDao().delete(id)
        widgetDao().deleteWithConfigId(id)
    }

    fun getConfig(id: Long): LiveData<ConfigEntity> {
        return configDao().getConfig(id)
    }

    val latestConfig: LiveData<ConfigEntity>
        get() = configDao().latestConfig
    val latestConfigDirect: ConfigEntity
        get() = configDao().latestConfigDirect
    val allConfigs: LiveData<List<ConfigEntity>>
        get() = configDao().allConfigs

    // Master methods ------------------------------------------------------------------------------
    suspend fun addMaster(master: MasterEntity) {
        masterDao().insert(master)
    }

    suspend fun updateMaster(master: MasterEntity) {
        masterDao().update(master)
    }

    suspend fun deleteMaster(configId: Long) {
        masterDao().delete(configId)
    }

    fun getMaster(id: Long): LiveData<MasterEntity> {
        return masterDao().getMaster(id)
    }

    // SSH methods ---------------------------------------------------------------------------------
    suspend fun addSSH(ssh: SSHEntity) {
        sshDao().insert(ssh)
    }

    suspend fun updateSSH(ssh: SSHEntity) {
        sshDao().update(ssh)
    }

    suspend fun deleteSSH(configId: Long) {
        sshDao().delete(configId)
    }

    fun getSSH(id: Long): LiveData<SSHEntity> {
        return sshDao().getSSH(id)
    }

    // Widget methods ------------------------------------------------------------------------------
    suspend fun addWidget(widget: BaseEntity) {
        widgetDao().insert(widget)
    }

    suspend fun updateWidget(widget: BaseEntity) {
        widgetDao().update(widget)
    }

    suspend fun deleteWidget(widget: BaseEntity) {
        widgetDao().delete(widget)
    }

    fun getWidget(configId: Long, widgetId: Long): LiveData<BaseEntity?> {
        return widgetDao().getWidget(configId, widgetId)
    }

    fun getWidgets(configId: Long): LiveData<List<BaseEntity>> {
        return widgetDao().getWidgets(configId)
    }

    suspend fun widgetNameExists(configId: Long, name: String): Boolean {
        return widgetDao().exists(configId, name)
    }

    companion object {
        private val TAG = DataStorage::class.java.canonicalName
        private lateinit var instance: DataStorage
        private lateinit var widgetNames: Array<String>
        @Synchronized
        fun getInstance(context: Context): DataStorage {
            if (!this::instance.isInitialized) {
                instance = databaseBuilder(
                    context.applicationContext,
                    DataStorage::class.java, Constants.DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            widgetNames =
                context.resources.getStringArray(R.array.widget_names)
            return instance
        }
    }
}
