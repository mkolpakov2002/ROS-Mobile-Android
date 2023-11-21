package ru.hse.miem.ros.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import ru.hse.miem.ros.data.model.entities.ConfigEntity
import ru.hse.miem.ros.data.model.repositories.ConfigRepository
import ru.hse.miem.ros.data.model.repositories.ConfigRepositoryImpl
import ru.hse.miem.ros.domain.RosDomain
import kotlinx.coroutines.launch
import java.util.Collections

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.1
 * @created on 10.01.20
 * @updated on 15.05.20
 * @modified by Maxim Kolpakov
 */
class ConfigurationsViewModel(application: Application) : AndroidViewModel(application) {
    private val rosDomain: RosDomain
    private lateinit var configRepository: ConfigRepository
    private lateinit var currentConfigTitle: MediatorLiveData<String?>
    private lateinit var currentConfig: LiveData<ConfigEntity>
    private lateinit var lastOpenedConfigNames: MediatorLiveData<List<String>>
    private lateinit var favoriteConfigNames: MediatorLiveData<List<String>>
    private lateinit var configList: LiveData<List<ConfigEntity>>

    init {
        rosDomain = RosDomain.getInstance(application)
        configRepository = ConfigRepositoryImpl.getInstance(application)
        initListeners()
    }

    private fun initListeners() {
        configList = configRepository.allConfigs
        currentConfig = configRepository.currentConfig
        currentConfigTitle = MediatorLiveData()
        currentConfigTitle.addSource(currentConfig) { configuration: ConfigEntity? ->
            if (configuration != null) {
                currentConfigTitle.postValue(configuration.name)
            } else {
                currentConfigTitle.postValue(null)
            }
        }

        // Sort config comparators
        val compareByLastOpened: Comparator<ConfigEntity> =
            Comparator { c1: ConfigEntity, c2: ConfigEntity ->
                c2.creationTime.compareTo(c1.creationTime)
            }
        lastOpenedConfigNames = MediatorLiveData()
        lastOpenedConfigNames.addSource(
            configList
        ) { configEntities: List<ConfigEntity> ->
            Collections.sort(configEntities, compareByLastOpened)
            val nameList: MutableList<String> = ArrayList()
            for (configEntity: ConfigEntity in configEntities) {
                configEntity.name?.let { nameList.add(it) }
            }
            lastOpenedConfigNames.postValue(nameList)
        }
        favoriteConfigNames = MediatorLiveData()
        favoriteConfigNames.addSource(
            configList
        ) { configEntities: List<ConfigEntity>? ->
            val nameList: MutableList<String> = ArrayList()
            for (configEntity: ConfigEntity in configEntities!!) {
                if (configEntity.isFavourite) {
                    configEntity.name?.let { nameList.add(it) }
                }
            }
            favoriteConfigNames.postValue(nameList)
        }
    }

    fun renameConfig(newName: String) {
        var newName: String = newName
        if (currentConfig.getValue() == null) {
            return
        }
        newName = newName.trim { it <= ' ' }
        val config: ConfigEntity? = currentConfig.getValue()
        config?.let {
            config.name = newName
            viewModelScope.launch {
                configRepository.updateConfig(config)
            }
        }
    }

    fun deleteConfig() {
        if (currentConfig.getValue() == null) {
            return
        }
        Log.i(TAG, "Delete current config")
        viewModelScope.launch {
            currentConfig.getValue()?.let {
                configRepository.removeConfig(it.id)
            }
        }
    }

    fun addConfig() {
        viewModelScope.launch{
            configRepository.createConfig(null)
        }
    }

    fun chooseConfig(configName: String?) {
        if (configList.getValue() == null) return
        configList.getValue()?.let {
            for (config: ConfigEntity in it) {
                if ((config.name == configName)) {
                    rosDomain.disconnectFromMaster()
                    configRepository.chooseConfig(config.id)
                    return
                }
            }
        }
    }

    val configTitle: LiveData<String?>
        get() {
            return currentConfigTitle
        }

    fun getLastOpenedConfigNames(): LiveData<List<String>> {
        return lastOpenedConfigNames
    }

    fun getFavoriteConfigNames(): LiveData<List<String>> {
        return favoriteConfigNames
    }

    companion object {
        private val TAG: String = ConfigurationsViewModel::class.java.simpleName
    }
}
