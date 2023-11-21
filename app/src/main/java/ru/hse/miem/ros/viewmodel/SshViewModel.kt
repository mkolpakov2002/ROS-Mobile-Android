package ru.hse.miem.ros.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.miem.ros.data.model.entities.SSHEntity
import ru.hse.miem.ros.data.model.repositories.SshRepositoryImpl

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 18.03.20
 * @updated on 04.06.20
 * @modified by Tanya Rykova
 */
class SshViewModel(application: Application) : AndroidViewModel(application) {
    private val currentSSH: LiveData<SSHEntity>
    private var sshRepositoryImpl: SshRepositoryImpl

    init {
        sshRepositoryImpl = SshRepositoryImpl.getInstance(application)
        currentSSH = sshRepositoryImpl.currentSSH
    }

    fun setSshIp(ipString: String) {
        viewModelScope.launch {
            val ssh: SSHEntity? = currentSSH.getValue()
            ssh?.let{
                it.ip = ipString
                sshRepositoryImpl.updateSSHConfig(it)
            }
        }
    }

    fun setSshPort(portString: String) {
        var port = 22
        try {
            port = portString.toInt()
        } catch (nfe: NumberFormatException) {
            nfe.printStackTrace()
        }
        val ssh: SSHEntity? = currentSSH.getValue()
        ssh?.let{
            it.port = port
            viewModelScope.launch {
                sshRepositoryImpl.updateSSHConfig(it)
            }
        }
    }

    fun setSshUsername(usernameString: String) {
        val ssh: SSHEntity? = currentSSH.getValue()
        ssh?.let{
            ssh.username = usernameString
            viewModelScope.launch {
                sshRepositoryImpl.updateSSHConfig(ssh)
            }
        }
    }

    fun setSshPassword(passwordString: String) {
        val ssh: SSHEntity? = currentSSH.getValue()
        ssh?.let{
            it.password = passwordString
            viewModelScope.launch {
                sshRepositoryImpl.updateSSHConfig(it)
            }
        }
    }

    fun connectViaSSH() {
        sshRepositoryImpl.startSession()
    }

    fun stopSsh() {
        sshRepositoryImpl.stopSession()
    }

    fun sendViaSSH(message: String) {
        sshRepositoryImpl.sendMessage(message)
    }

    fun abortAction() {
        sshRepositoryImpl.abort()
    }

    val isConnected: LiveData<Boolean>
        get() {
            return sshRepositoryImpl.isConnected
        }
    val outputData: LiveData<String>
        get() {
            return sshRepositoryImpl.outputData
        }
    val sSH: LiveData<SSHEntity>
        get() {
            return sshRepositoryImpl.currentSSH
        }
}
