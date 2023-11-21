package ru.hse.miem.ros.data.model.repositories.rosRepo.node

import android.util.Log
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.Node
import org.ros.node.NodeMain
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.RosData
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 15.09.20
 */
open class AbstractNode : NodeMain {
    companion object {
        val TAG = AbstractNode::class.java.simpleName
    }

    lateinit var topic: Topic
    open var widget: BaseEntity? = null
        set(value) {
            field = value
            value?.let { topic = it.topic }
        }
    var lastRosData: RosData? = null

    override fun onStart(parentNode: ConnectedNode) {
        Log.i(TAG, "On Start:  ${topic.name}")
    }

    override fun onShutdown(node: Node) {
        Log.i(TAG, "On Shutdown:  ${topic.name}")
    }

    override fun onShutdownComplete(node: Node) {
        Log.i(TAG, "On Shutdown Complete: ${topic.name}")
    }

    override fun onError(node: Node, throwable: Throwable) {
        throwable.printStackTrace()
    }

    override fun getDefaultNodeName(): GraphName {
        return GraphName.of(topic.name)
    }
}
