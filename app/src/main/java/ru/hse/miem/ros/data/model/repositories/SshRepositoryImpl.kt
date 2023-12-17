package ru.hse.miem.ros.data.model.repositories

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.hse.miem.ros.data.model.entities.SSHEntity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintStream
import java.util.Properties

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.1
 * @created on 19.02.20
 * @updated on 03.03.20
 * @modified by Maxim Kolpakov
 * @updated on 04.06.20
 * @modified by Tanya Rykova
 * @updated on 16.11.2020
 * @modified by Tanya Rykova
 */
class SshRepositoryImpl private constructor(application: Application) : SshRepository,
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val configRepository: ConfigRepository
    override val currentSSH: LiveData<SSHEntity>
    private lateinit var jsch: JSch
    private lateinit var session: Session
    private lateinit var channelssh: ChannelShell
    private var inputForTheChannel: OutputStream? = null
    private var outputFromTheChannel: InputStream? = null
    private lateinit var commander: PrintStream
    private lateinit var br: BufferedReader
    override var outputData: MutableLiveData<String> = MutableLiveData()
    private var connected: MutableLiveData<Boolean> = MutableLiveData()

    // Generate Handler
    private var mainHandler: Handler

    init {
        configRepository = ConfigRepositoryImpl.getInstance(application)

        // React on Config Changes
        currentSSH = configRepository.currentConfigId.switchMap {
            configId: Long -> configRepository.getSSH(configId)
        }
        //TODO
        currentSSH.observeForever {
            ssh: SSHEntity? ->
            ssh?.let { updateSSH(it) }
        }
        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun startSession() {
        currentSSH.getValue()?.let { ssh ->
            launch {
                try {
                    startSessionTask(ssh.username, ssh.password, ssh.ip, ssh.port)
                } catch (e: Exception) {
                    Log.e(TAG, "${e.message}")
                }
            }
        }
    }

    @Throws(JSchException::class, IOException::class)
    private suspend fun startSessionTask(username: String, password: String, ip: String, port: Int) {
        // Check if session already running
        if (this::session.isInitialized && session.isConnected) {
            Log.i(TAG, "Session is running already")
            return
        }
        Log.i(TAG, "Start session")

        // Create new session
        jsch = JSch()
        session = jsch.getSession(username, ip, port)
        session.setPassword(password)

        // Avoid asking for key confirmation
        val prop = Properties()
        prop["StrictHostKeyChecking"] = "no"
        session.setConfig(prop)

        // Start connection
        session.connect(30000)

        // SSH Channel
        channelssh = session.openChannel("shell") as ChannelShell
        inputForTheChannel = channelssh.getOutputStream()
        outputFromTheChannel = channelssh.getInputStream()
        commander = PrintStream(inputForTheChannel, true)
        br = BufferedReader(InputStreamReader(outputFromTheChannel))

        // Connect to channel
        channelssh.connect()
        delay(100)

        // Check for connection
        if (channelssh.isConnected()) {
            connected.postValue(true)
        }
        var line: String
        while ((br.readLine().also { line = it }) != null && channelssh.isConnected()) {
            // TODO: Check if every line will be displayed
            Log.i(TAG, "looper session")

            // Remove ANSI control chars (Terminal VT 100)
            line = line.replace("\u001B\\[[\\d;]*[^\\d;]".toRegex(), "")
            val finalLine = line

            // Publish lineData to LiveData
            mainHandler.post {
                outputData.setValue(finalLine)
            }
        }
    }

    override fun stopSession() {
        if (channelssh.isConnected()) {
            channelssh.disconnect()
            session.disconnect()
            connected.postValue(false)
        }
    }

    override val isConnected: LiveData<Boolean>
        get() = connected

    override fun sendMessage(message: String) {
        launch {
            try {
                commander.println(message)
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}")
            }
        }
    }

    override fun abort() {
        launch {
            try {
                commander.write(3)
                commander.flush()
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}")
            }
        }
    }

//    override fun getOutputData(): LiveData<String> {
//        return outputData
//    }

    override fun updateSSH(ssh: SSHEntity) {
        Log.i(TAG, "Update SSH")
    }

    suspend fun updateSSHConfig(ssh: SSHEntity) {
        configRepository.updateSSH(ssh)
    }

//    override fun getCurrentSSH(): LiveData<SSHEntity> {
//        return currentSSH
//    }

    companion object {
        val TAG = SshRepositoryImpl::class.java.simpleName
        private lateinit var mInstance: SshRepositoryImpl
        fun getInstance(application: Application): SshRepositoryImpl {
            if (!this::mInstance.isInitialized) {
                mInstance = SshRepositoryImpl(application)
            }
            return mInstance
        }
    }
}
