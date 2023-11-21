package ru.hse.miem.ros.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.miem.ros.data.model.entities.ConfigEntity
import ru.hse.miem.ros.data.model.repositories.ConfigRepository
import ru.hse.miem.ros.data.model.repositories.ConfigRepositoryImpl

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.1
 * @created on 10.01.20
 * @updated on 27.07.20
 * @modified by Tanya Rykova
 */
class MainViewModel() : ViewModel() {
    lateinit var application: Application
    private lateinit var configRepo: ConfigRepository
    private lateinit var configTitle: MediatorLiveData<String?>

    fun init(application: Application) {
        this.application = application
        configRepo = ConfigRepositoryImpl.getInstance(application)
    }

    fun getConfigTitle(): LiveData<String?> {
        if (!this::configTitle.isInitialized) {
            configTitle = MediatorLiveData()
            configTitle.addSource(
                configRepo.currentConfig
            ) { configuration: ConfigEntity? ->
                if (configuration == null) return@addSource
                configTitle.setValue(configuration.name)
            }
        }
        return configTitle
    }

    fun createFirstConfig(name: String?) {
        viewModelScope.launch {
            configRepo.createConfig(name)
        }
    }

    companion object {
        private val TAG: String = MainViewModel::class.java.simpleName
    }
}
