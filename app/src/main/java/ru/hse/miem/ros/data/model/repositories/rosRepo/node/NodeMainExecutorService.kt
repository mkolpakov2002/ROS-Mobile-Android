/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.hse.miem.ros.data.model.repositories.rosRepo.node

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import org.ros.concurrent.ListenerGroup
import org.ros.node.DefaultNodeMainExecutor
import org.ros.node.NodeConfiguration
import org.ros.node.NodeListener
import org.ros.node.NodeMain
import org.ros.node.NodeMainExecutor
import ru.hse.miem.ros.R
import java.net.URI
import java.util.concurrent.ScheduledExecutorService

/**
 * TODO: Description
 *
 * @author Damon Kohler
 * @version 1.0.0
 * @created on 15.04.20
 * @updated on 15.04.20
 * @modified by Maxim Kolpakov
 */
class NodeMainExecutorService : Service(), NodeMainExecutor {
    private val nodeMainExecutor: NodeMainExecutor = DefaultNodeMainExecutor.newDefault()
    private val binder: IBinder = LocalBinder()
    private val listeners: ListenerGroup<NodeMainExecutorServiceListener> = ListenerGroup(nodeMainExecutor.scheduledExecutorService)
    private val wakeLock: PowerManager.WakeLock by lazy {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ROSANDROID:$TAG")
    }
    private val wifiLock: WifiManager.WifiLock by lazy {
        val wifiManager = getSystemService(WIFI_SERVICE) as WifiManager
        wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, TAG)
    }
    var masterUri: URI? = null
    var rosHostname: String? = null

    override fun onCreate() {
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)
        wifiLock.acquire()
    }

    override fun execute(nodeMain: NodeMain, nodeConfiguration: NodeConfiguration) {
        execute(nodeMain, nodeConfiguration, null)
    }

    override fun execute(
        nodeMain: NodeMain, nodeConfiguration: NodeConfiguration,
        nodeListeneners: Collection<NodeListener>?
    ) {
        nodeMainExecutor.execute(nodeMain, nodeConfiguration, nodeListeneners)
    }

    override fun getScheduledExecutorService(): ScheduledExecutorService {
        return nodeMainExecutor.scheduledExecutorService
    }

    override fun shutdownNodeMain(nodeMain: NodeMain) {
        nodeMainExecutor.shutdownNodeMain(nodeMain)
    }

    override fun shutdown() {
        signalOnShutdown()
        stopForeground(true)
        stopSelf()
    }

    fun addListener(listener: NodeMainExecutorServiceListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: NodeMainExecutorServiceListener) {
        listeners.remove(listener)
    }

    private fun signalOnShutdown() {
        listeners.signal { nodeMainExecutorServiceListener: NodeMainExecutorServiceListener ->
            nodeMainExecutorServiceListener.onShutdown(
                this
            )
        }
    }

    override fun onDestroy() {
        nodeMainExecutor.shutdown()
        if (wakeLock.isHeld) wakeLock.release()
        if (wifiLock.isHeld) wifiLock.release()
        super.onDestroy()
    }

    //TODO
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_START -> {
                val notificationIntent = Intent(this, NodeMainExecutorService::class.java)
                notificationIntent.setAction(ACTION_SHUTDOWN)
                val pendingIntent = PendingIntent.getService(this, 0, notificationIntent,
                    Intent.FILL_IN_ACTION or PendingIntent.FLAG_IMMUTABLE)
                val notification: Notification = Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
                    .setContentTitle("getText(R.string.notification_title)")
                    .setContentText("getText(R.string.notification_message)")
                    .setSmallIcon(R.drawable.intro_start)
                    .setContentIntent(pendingIntent)
                    .setTicker("getText(R.string.ticker_text)")
                    .build()
                startForeground(ONGOING_NOTIFICATION_ID, notification)
            }
            ACTION_SHUTDOWN -> shutdown()
            else -> return START_NOT_STICKY
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        val service: NodeMainExecutorService
            get() = this@NodeMainExecutorService
    }

    companion object {
        const val ACTION_START = "org.ru.hse.miem.ros.android.ACTION_START_NODE_RUNNER_SERVICE"
        const val ACTION_SHUTDOWN = "org.ru.hse.miem.ros.android.ACTION_SHUTDOWN_NODE_RUNNER_SERVICE"
        private const val ONGOING_NOTIFICATION_ID = 1
        private const val CHANNEL_DEFAULT_IMPORTANCE = "default_importance"
        private const val TAG = "NodeMainExecutorService"
    }
}
