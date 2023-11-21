package ru.hse.miem.ros.data.model.repositories

import androidx.lifecycle.LiveData
import ru.hse.miem.ros.data.model.entities.SSHEntity

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 04.06.20
 * @updated on
 * @modified by
 */
interface SshRepository {
    fun startSession()
    fun stopSession()
    val isConnected: LiveData<Boolean>
    fun sendMessage(message: String)
    fun abort()
    val outputData: LiveData<String>
    fun updateSSH(ssh: SSHEntity)
    val currentSSH: LiveData<SSHEntity>
}
