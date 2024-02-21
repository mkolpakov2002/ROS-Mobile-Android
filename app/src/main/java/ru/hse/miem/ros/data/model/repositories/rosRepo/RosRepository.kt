package ru.hse.miem.ros.data.model.repositories.rosRepo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ros.address.InetAddressFactory
import org.ros.internal.node.client.MasterClient
import org.ros.namespace.GraphName
import org.ros.node.NodeConfiguration
import org.ros.rosjava_geometry.FrameTransformTree
import ru.hse.miem.ros.data.model.entities.MasterEntity
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.entities.widgets.GroupEntity
import ru.hse.miem.ros.data.model.entities.widgets.IPublisherEntity
import ru.hse.miem.ros.data.model.entities.widgets.ISilentEntity
import ru.hse.miem.ros.data.model.entities.widgets.ISubscriberEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.connection.ConnectionCheckTask
import ru.hse.miem.ros.data.model.repositories.rosRepo.connection.ConnectionListener
import ru.hse.miem.ros.data.model.repositories.rosRepo.connection.ConnectionType
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.RosData
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.AbstractNode
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.NodeMainExecutorService
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.NodeMainExecutorServiceListener
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.PubNode
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.SubNode
import tf2_msgs.TFMessage
import java.lang.ref.WeakReference
import java.net.URI

/**
 * The ROS repository is responsible for connecting to the ROS master
 * and creating nodes depending on the respective widgets.
 *
 * @author Maxim Kolpakov
 * @version 1.1.3
 * @created on 16.01.20
 * @updated on 20.05.20
 * @modified by Maxim Kolpakov
 * @updated on 24.09.20
 * @modified by Maxim Kolpakov
 * @updated on 16.11.2020
 * @modified by Tanya Rykova
 * @updated on 10.03.2021
 * @modified by Maxim Kolpakov
 */
class RosRepository private constructor(context: Context) : SubNode.NodeListener {
    private val contextReference: WeakReference<Context> = WeakReference(context)
    private val currentWidgets: MutableList<BaseEntity> = ArrayList()
    val lastRosData: HashMap<Topic?, AbstractNode> = HashMap()
    private val rosConnected: MutableLiveData<ConnectionType> = MutableLiveData(ConnectionType.DISCONNECTED)
    private val receivedData: MutableLiveData<RosData> = MutableLiveData()
    private val frameTransformTree: FrameTransformTree = TransformProvider.getInstance().tree
    private lateinit var master: MasterEntity
    private lateinit var nodeMainExecutorService: NodeMainExecutorService
    private lateinit var nodeConfiguration: NodeConfiguration

    /**
     * Default private constructor. Initialize empty lists and maps of intern widgets and nodes.
     */
    init {
        initStaticNodes()
    }

    /**
     * Initialize static nodes eg. tf and tf_static.
     */
    private fun initStaticNodes() {
        val tfTopic = Topic("/tf", TFMessage._TYPE)
        val tfNode = SubNode(this)
        tfNode.topic = tfTopic
        lastRosData[tfTopic] = tfNode
        val tfStaticTopic = Topic("/tf_static", TFMessage._TYPE)
        val tfStaticNode = SubNode(this)
        tfStaticNode.topic = tfStaticTopic
        lastRosData[tfStaticTopic] = tfStaticNode
    }

    override fun onNewMessage(message: RosData) {
        // Save transforms from tf messages
        if (message.message is TFMessage) {
            val tf = message.message
            for (transform in tf.transforms) {
                frameTransformTree.update(transform)
            }
        }
        receivedData.postValue(message)
    }

    /**
     * Find the associated node and inform it about the changed data.
     *
     * @param data Widget data that has changed
     */
    fun publishData(data: BaseData) {
        val node = lastRosData[data.topic]
        if (node is PubNode) {
            node.setData(data)
        }
    }

    /**
     * Connect all registered nodes and establish a connection to the ROS master with
     * the connection details given by the already delivered master entity.
     */
    fun connectToMaster() {
        Log.i(TAG, "Connect to Master")
        val connectionType = rosConnected.getValue()
        if (connectionType == ConnectionType.CONNECTED || connectionType == ConnectionType.PENDING) {
            return
        }
        rosConnected.value = ConnectionType.PENDING

        // Check connection
        CoroutineScope(Dispatchers.Main).launch {
            ConnectionCheckTask(object : ConnectionListener {
                override fun onSuccess() {
                    bindService()
                }

                override fun onFailed() {
                    rosConnected.postValue(ConnectionType.FAILED)
                }
            }).execute(master)
        }
    }

    /**
     * Disconnect all running nodes and cut the connection to the ROS master.
     */
    fun disconnectFromMaster() {
        Log.i(TAG, "Disconnect from Master")
        if (!this::nodeMainExecutorService.isInitialized) {
            return
        }
        unregisterAllNodes()
        nodeMainExecutorService.shutdown()
    }

    /**
     * Change the connection details to the ROS master like the IP or port.
     *
     * @param master Master data
     */
    fun updateMaster(master: MasterEntity?) {
        Log.i(TAG, "Update Master")
        if (master == null) {
            Log.i(TAG, "Master is null")
            return
        }
        this.master = master

        // nodeConfiguration = NodeConfiguration.newPublic(master.deviceIp, getMasterURI());
    }

    /**
     * Set the master device IP in the Node configuration
     */
    fun setMasterDeviceIp(deviceIp: String?) {
        nodeConfiguration = NodeConfiguration.newPublic(deviceIp, masterURI)
    }

    /**
     * React on a widget change. If at least one widget is added, deleted or changed this method
     * should be called.
     *
     * @param newWidgets Current list of widgets
     */
    fun updateWidgets(newWidgets: List<BaseEntity>) {
        Log.i(TAG, "Update widgets")

        // Unpack widgets as a widget can contain child widgets
        val newEntities: MutableList<BaseEntity> = ArrayList()
        for (baseEntity in newWidgets) {
            if (baseEntity is GroupEntity) {
                newEntities.addAll(baseEntity.childEntities)
            } else {
                newEntities.add(baseEntity)
            }
        }
        for (baseEntity in newEntities) {
            Log.i(TAG, "Entity: " + baseEntity.name)
        }

        // Compare old and new widget lists
        // Create widget check with ids
        val widgetCheckMap = HashMap<Long, Boolean>()
        val widgetEntryMap = HashMap<Long, BaseEntity>()
        for (oldWidget in currentWidgets) {
            widgetCheckMap[oldWidget.id] = false
            widgetEntryMap[oldWidget.id] = oldWidget
        }
        for (newWidget in newEntities) {
            if (widgetCheckMap.containsKey(newWidget.id)) {
                // Node included in old and new list
                widgetCheckMap[newWidget.id] = true

                // Check if widget has changed
                val oldWidget = widgetEntryMap[newWidget.id]
                updateNode(oldWidget, newWidget)
            } else {
                // Node not included in old list
                addNode(newWidget)
            }
        }

        // Delete unused widgets
        for (id in widgetCheckMap.keys) {
            if (!widgetCheckMap[id]!!) {
                // Node not included in new list
                removeNode(widgetEntryMap[id])
            }
        }
        currentWidgets.clear()
        currentWidgets.addAll(newEntities)
    }

    val rosConnectionStatus: LiveData<ConnectionType>
        /**
         * Get the current connection status of the ROS service as a live data.
         *
         * @return Connection status
         */
        get() = rosConnected

    private fun bindService() {
        val context = contextReference.get() ?: return
        val serviceConnection = RosServiceConnection(masterURI)

        // Create service intent
        val serviceIntent = Intent(context, NodeMainExecutorService::class.java)
        serviceIntent.setAction(NodeMainExecutorService.Companion.ACTION_START)

        // Start service and check state
        context.startService(serviceIntent)
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * Create a node for a specific widget entity.
     * The node will be created and subsequently registered.
     *
     * @param widget Widget to be added
     */
    private fun addNode(widget: BaseEntity): AbstractNode? {
        if (widget is ISilentEntity) return null
        Log.i(TAG, "Add node: " + widget.name)

        // Create a new node from widget
        val node: AbstractNode = when (widget) {
            is IPublisherEntity -> {
                PubNode()
            }

            is ISubscriberEntity -> {
                SubNode(this)
            }

            else -> {
                Log.i(
                    TAG,
                    "Widget is either publisher nor subscriber."
                )
                return null
            }
        }

        // Set node topic, add to node list and register it
        node.widget = (widget)
        lastRosData[node.topic] = node
        registerNode(node)
        return node
    }

    /**
     * Update a widget and its associated Node by ID in the ROS graph.
     *
     * @param oldWidget Old version of the widget
     * @param widget    Widget to update
     */
    private fun updateNode(oldWidget: BaseEntity?, widget: BaseEntity) {
        if (widget is ISilentEntity) return
        Log.i(TAG, "Update Node: " + oldWidget!!.name)
        if (oldWidget.equalRosState(widget)) {
            val node = lastRosData[widget.topic]
            if (node == null) {
                addNode(widget)
                return
            }
            node.widget = (widget)
        } else {
            removeNode(oldWidget)
            addNode(widget)
        }
    }

    /**
     * Remove a widget and its associated Node in the ROS graph.
     *
     * @param widget Widget to remove
     */
    private fun removeNode(widget: BaseEntity?) {
        if (widget is ISilentEntity) return
        Log.i(TAG, "Remove Node: " + widget!!.name)
        val node = lastRosData.remove(
            widget.topic
        )
        unregisterNode(node)
    }

    /**
     * Connect the node to ROS node graph if a connection to the ROS master is running.
     *
     * @param node Node to connect
     */
    private fun registerNode(node: AbstractNode) {
        Log.i(TAG, "Register Node: " + node.topic.name)
        if (rosConnected.getValue() != ConnectionType.CONNECTED) {
            Log.w(TAG, "Not connected with master")
            return
        }
        nodeMainExecutorService.execute(node, nodeConfiguration)
    }

    /**
     * Disconnect the node from ROS node graph if a connection to the ROS master is running.
     *
     * @param node Node to disconnect
     */
    private fun unregisterNode(node: AbstractNode?) {
        if (node == null) return
        Log.i(TAG, "Unregister Node: " + node.topic.name)
        if (rosConnected.getValue() != ConnectionType.CONNECTED) {
            Log.w(TAG, "Not connected with master")
            return
        }
        nodeMainExecutorService.shutdownNodeMain(node)
    }

    /**
     * Result of a change in the internal data of a node header. Therefore it has to be
     * unregistered from the service and reregistered due to the implementation of ROS.
     *
     * @param node Node main to be reregistered
     */
    private fun reregisterNode(node: AbstractNode) {
        Log.i(TAG, "Reregister Node")
        unregisterNode(node)
        registerNode(node)
    }

    private fun registerAllNodes() {
        for (node in lastRosData.values) {
            registerNode(node)
        }
    }

    private fun unregisterAllNodes() {
        for (node in lastRosData.values) {
            unregisterNode(node)
        }
    }

    private val masterURI: URI
        get() {
            val masterString = String.format("http://%s:%s/", master.ip, master.port)
            return URI.create(masterString)
        }
    private val defaultHostAddress: String?
        get() = InetAddressFactory.newNonLoopback().hostAddress
    val data: LiveData<RosData>
        get() = receivedData
    val topicList: List<Topic>
        /**
         * Get a list from the ROS Master with all available topics.
         *
         * @return Topic list
         */
        get() {
            val topicList = ArrayList<Topic>()
            if (!this::nodeMainExecutorService.isInitialized || !this::nodeConfiguration.isInitialized) {
                return topicList
            }
            val masterClient = MasterClient(nodeMainExecutorService.masterUri)
            val graphName = GraphName.newAnonymous()
            val responseList = masterClient.getTopicTypes(graphName)
            for (result in responseList.result) {
                val name = result.name
                val type = result.messageType
                val rosTopic = Topic(name, type)
                topicList.add(rosTopic)
            }
            return topicList
        }

    private inner class RosServiceConnection internal constructor(var customMasterUri: URI) :
        ServiceConnection {
        var serviceListener: NodeMainExecutorServiceListener? = null
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            nodeMainExecutorService = (binder as NodeMainExecutorService.LocalBinder).service
            nodeMainExecutorService.masterUri = (customMasterUri)
            nodeMainExecutorService.rosHostname = (defaultHostAddress)
            serviceListener =
                NodeMainExecutorServiceListener {
                    rosConnected.postValue(ConnectionType.DISCONNECTED)
                }
            serviceListener?.let{
                nodeMainExecutorService.addListener(it)
            }
            rosConnected.value = ConnectionType.CONNECTED
            registerAllNodes()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceListener?.let { nodeMainExecutorService.removeListener(it) }
        }
    }

    companion object {
        private val TAG = RosRepository::class.java.simpleName
        private lateinit var instance: RosRepository

        /**
         * Return the singleton instance of the repository.
         *
         * @return Instance of this Repository
         */
        fun getInstance(context: Context): RosRepository {
            if (!this::instance.isInitialized) {
                instance = RosRepository(context)
            }
            return instance
        }
    }
}
