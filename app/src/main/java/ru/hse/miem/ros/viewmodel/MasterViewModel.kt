package ru.hse.miem.ros.viewmodel

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ru.hse.miem.ros.data.model.entities.MasterEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.connection.ConnectionType
import ru.hse.miem.ros.domain.RosDomain
import ru.hse.miem.ros.utility.Utils
import kotlinx.coroutines.launch

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.1.3
 * @created on 10.01.20
 * @updated on 11.04.20
 * @modified by Maxim Kolpakov
 * @updated on 16.11.2020
 * @modified by Tanya Rykova
 * @updated on 13.05.2021
 * @modified by Maxim Kolpakov
 */
class MasterViewModel(application: Application) : AndroidViewModel(application) {
    private val rosDomain: RosDomain
    private val currentMaster: LiveData<MasterEntity>
    private lateinit var networkSSIDLiveData: MutableLiveData<String?>
    private var lastTimeHelpShowed: Long = 0

    init {
        rosDomain = RosDomain.getInstance(application)
        currentMaster = rosDomain.currentMaster
    }

    fun updateHelpDisplay() {
        lastTimeHelpShowed = System.currentTimeMillis()
    }

    fun shouldShowHelp(): Boolean {
        return System.currentTimeMillis() - lastTimeHelpShowed >= MIN_HELP_TIMESPAM
    }

    fun setMasterIp(ipString: String) {
        val master: MasterEntity = currentMaster.getValue() ?: return
        master.ip = ipString
        viewModelScope.launch {
            rosDomain.updateMaster(master)
        }
    }

    fun setMasterPort(portString: String) {
        val port: Int = portString.toInt()
        val master: MasterEntity = currentMaster.getValue() ?: return
        master.port = port
        viewModelScope.launch {
            rosDomain.updateMaster(master)
        }
    }

    fun setMasterDeviceIp(deviceIpString: String) {
        rosDomain.setMasterDeviceIp(deviceIpString)
    }

    fun connectToMaster() {
        setWifiSSID()
        rosDomain.connectToMaster()
    }

    fun disconnectFromMaster() {
        rosDomain.disconnectFromMaster()
    }

    val master: LiveData<MasterEntity>
        get() {
            return rosDomain.currentMaster
        }
    val rosConnection: LiveData<ConnectionType>
        get() {
            return rosDomain.rosConnection
        }

    fun setDeviceIp(deviceIp: String): String {
        return deviceIp
    }

    val currentNetworkSSID: LiveData<String?>
        get() {
            if (!this::networkSSIDLiveData.isInitialized) {
                networkSSIDLiveData = MutableLiveData()
            }
            setWifiSSID()
            return networkSSIDLiveData
        }
    val iPAddressList: ArrayList<String?>
        get() {
            return Utils.getIPAddressList(true)
        }
    val iPAddress: String
        get() {
            return Utils.getIPAddress(true)
        }

    private fun setWifiSSID() {
        val wifiManager: WifiManager = getApplication<Application>().applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
        var ssid: String? = Utils.getWifiSSID(wifiManager)
        if (ssid == null) {
            ssid = "None"
        }
        networkSSIDLiveData.postValue(ssid)
    }

    companion object {
        private val TAG: String = MasterViewModel::class.java.simpleName
        private val MIN_HELP_TIMESPAM: Long = (30 * 1000).toLong()
    }
}
