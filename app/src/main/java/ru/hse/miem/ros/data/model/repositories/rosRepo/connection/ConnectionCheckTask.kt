package ru.hse.miem.ros.data.model.repositories.rosRepo.connection

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.1
 * @created on 15.04.20
 * @updated on 16.04.20
 * @modified by
 */
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.hse.miem.ros.data.model.entities.MasterEntity
import ru.hse.miem.ros.utility.Utils

class ConnectionCheckTask(private val listener: ConnectionListener) {
    companion object {
        private const val TIMEOUT_TIME = 2 * 1000
    }

    suspend fun execute(masterEnt: MasterEntity) {
        val result = withContext(Dispatchers.IO) {
            Utils.isHostAvailable(masterEnt.ip, masterEnt.port, TIMEOUT_TIME)
        }
        if (result) listener.onSuccess() else listener.onFailed()
    }
}