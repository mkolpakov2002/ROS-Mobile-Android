package ru.hse.miem.ros.data.model.repositories

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.switchMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.hse.miem.ros.data.model.db.DataStorage
import ru.hse.miem.ros.data.model.entities.ConfigEntity
import ru.hse.miem.ros.data.model.entities.MasterEntity
import ru.hse.miem.ros.data.model.entities.SSHEntity
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.entities.widgets.I2DLayerEntity
import ru.hse.miem.ros.utility.Constants
import ru.hse.miem.ros.utility.Utils
import java.util.Locale

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.1
 * @created on 26.01.2020
 * @updated on 20.05.2020
 * @modified by Maxim Kolpakov
 * @updated on 04.06.2020
 * @modified by Tanya Rykova
 * @updated on 27.07.2020
 * @modified by Tanya Rykova
 * @updated on 23.09.2020
 * @modified by Maxim Kolpakov
 * @updated on 23.03.2021
 * @modified by Maxim Kolpakov
 */
class ConfigRepositoryImpl private constructor(application: Application) : ConfigRepository,
    CoroutineScope by CoroutineScope(Dispatchers.Default){
    private val mDataStorage: DataStorage = DataStorage.getInstance(application)
    private val mCurrentConfigId: MediatorLiveData<Long> = MediatorLiveData()

    init {
        mCurrentConfigId.addSource(
            mDataStorage.getLatestConfig()
        ) { config: ConfigEntity? ->
            Log.i(TAG, "New Config: $config")
            if (config != null) mCurrentConfigId.postValue(config.id)
        }
    }

    // CONFIGS -------------------------------------------------------------------------------------
    public override fun chooseConfig(configId: Long) {
        if (mCurrentConfigId.getValue() == null || mCurrentConfigId.getValue() != configId) {
            mCurrentConfigId.postValue(configId)
        }
    }

    override suspend fun createConfig(configName: String?) {
        coroutineScope {
            launch {
                val config = ConfigEntity()
                config.creationTime = System.currentTimeMillis()
                config.lastUsed = config.creationTime
                config.name = configName
                mDataStorage.addConfig(config)
                val configId: Long = mDataStorage.getLatestConfigDirect().id
                val master = MasterEntity()
                master.configId = configId
                mDataStorage.addMaster(master)
                val sshEntity = SSHEntity()
                sshEntity.configId = configId
                mDataStorage.addSSH(sshEntity)
            }
        }
    }


    public override suspend fun removeConfig(configId: Long) {
        mDataStorage.deleteConfig(configId)
        mDataStorage.deleteMaster(configId)
        mDataStorage.deleteSSH(configId)
    }

    public override suspend fun updateConfig(config: ConfigEntity) {
        mDataStorage.updateConfig(config)
    }

    public override fun getConfig(id: Long): LiveData<ConfigEntity> {
        return mDataStorage.getConfig(id)
    }

    override val allConfigs: LiveData<List<ConfigEntity>>
        get() = mDataStorage.allConfigs
    override val currentConfigId: LiveData<Long>
        get() = mCurrentConfigId
    override val currentConfig: LiveData<ConfigEntity>
    get() = mCurrentConfigId.switchMap { id: Long ->
            mDataStorage.getConfig((id))
        }

    // WIDGETS -------------------------------------------------------------------------------------
    public override suspend fun addWidget(parentId: Long?, widget: BaseEntity) {
        Log.i(TAG, "Add widget: " + widget.name)
        if(parentId != null){
            searchParent(widget, parentId, object : ParentListener {
                override suspend fun onParent(parentEntity: BaseEntity?) {
                    parentEntity?.let {
                        it.addEntity(widget)
                        mDataStorage.updateWidget(it)
                    }
                }
            })
        } else {
            mDataStorage.addWidget(widget)
        }
    }

    public override suspend fun createWidget(parentId: Long?, widgetType: String) {
        val widget: BaseEntity? = getWidgetFromType(widgetType)
        when (widget) {
            null -> return
            is I2DLayerEntity -> {
                // Check for parent
                parentId?.let{
                    searchParent(widget, it, object : ParentListener {
                        override suspend fun onParent(parentEntity: BaseEntity?) {
                            parentEntity?.addEntity(widget)
                            if (parentEntity != null) {
                                mDataStorage.updateWidget(parentEntity)
                            }
                        }
                    })
                }
            } else -> {
                mDataStorage.addWidget(widget)
            }
        }
        Log.i(TAG, "Widget added to database: " + widget.type)
    }

    public override suspend fun updateWidget(parentId: Long?, widget: BaseEntity) {
        if (parentId != null){
            searchParent(widget, parentId, object : ParentListener {
                override suspend fun onParent(parentEntity: BaseEntity?) {
                    parentEntity?.replaceChild(widget)
                    if (parentEntity != null) {
                        mDataStorage.updateWidget(parentEntity)
                    }
                }
            })
        } else {
            mDataStorage.updateWidget(widget)
        }
    }

    public override suspend fun deleteWidget(parentId: Long?, widget: BaseEntity) {
        if (parentId != null){
            searchParent(widget, parentId, object : ParentListener {
                override suspend fun onParent(parentEntity: BaseEntity?) {
                    parentEntity?.removeChild(widget)
                    if (parentEntity != null) {
                        mDataStorage.updateWidget(parentEntity)
                    }
                }
            })
        } else {
            mDataStorage.deleteWidget(widget)
        }

    }

    public override fun getWidgets(id: Long): LiveData<List<BaseEntity>> {
        return mDataStorage.getWidgets(id)
    }

    public override fun findWidget(widgetId: Long): LiveData<BaseEntity> {
        return mDataStorage.getWidget((mCurrentConfigId.getValue())!!, widgetId)
    }

    private suspend fun getWidgetFromType(widgetType: String): BaseEntity? {
        // Create actual widget object
        val classPath: String = String.format(
            Constants.ENTITY_FORMAT,
            widgetType.lowercase(Locale.getDefault()),
            widgetType
        )
        val `object`: Any? = Utils.getObjectFromClassName(classPath)
        if (`object` !is BaseEntity) {
            Log.i(TAG, "Widget can not be created from: $classPath")
            return null
        }
        val widget: BaseEntity = `object`
        val configId: Long = (mCurrentConfigId.getValue())!!
        var widgetName = ""
        var count: Int = 1
        while (count > 0) {
            widgetName = String.format(Locale.ENGLISH, Constants.WIDGET_NAMING, widgetType, count)
            if (!mDataStorage.widgetNameExists(configId, widgetName)) {
                break
            }
            count++
        }
        if (widget is I2DLayerEntity) {
            widget.id = System.currentTimeMillis()
        }
        widget.configId = configId
        widget.creationTime = System.currentTimeMillis()
        widget.name = widgetName
        widget.type = widgetType
        return widget
    }

    private fun searchParent(widget: BaseEntity, parentId: Long, listener: ParentListener) {
        val liveParent: LiveData<BaseEntity> = mDataStorage.getWidget(widget.configId, parentId)
        liveParent.observeForever(object : Observer<BaseEntity> {
            public override fun onChanged(value: BaseEntity) {
                val observer = this
                launch {
                    listener.onParent(value)
                    liveParent.removeObserver(observer)
                }
            }
        })
    }

    // Masters -------------------------------------------------------------------------------------
    public override suspend fun updateMaster(master: MasterEntity) {
        mDataStorage.updateMaster(master)
    }

    public override fun getMaster(configId: Long): LiveData<MasterEntity?> {
        return mDataStorage.getMaster(configId)
    }

    // SSH -------------------------------------------------------------------------------------
    public override fun setSSH(ssh: SSHEntity, configId: String) {
        ssh.ip = configId
    }

    public override suspend fun updateSSH(ssh: SSHEntity) {
        mDataStorage.updateSSH(ssh)
    }

    public override fun getSSH(configId: Long): LiveData<SSHEntity> {
        return mDataStorage.getSSH(configId)
    }

    private interface ParentListener {
        suspend fun onParent(parentEntity: BaseEntity?)
    }

    companion object {
        private val TAG: String = ConfigRepositoryImpl::class.java.simpleName
        private lateinit var mInstance: ConfigRepositoryImpl
        fun getInstance(application: Application): ConfigRepositoryImpl {
            if (!this::mInstance.isInitialized) {
                mInstance = ConfigRepositoryImpl(application)
            }
            return mInstance
        }
    }
}
