package ru.hse.miem.ros.data.model.repositories.rosRepo.node

import org.ros.internal.message.Message
import org.ros.node.ConnectedNode
import org.ros.node.topic.Subscriber
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.RosData

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 16.09.20
 */
class SubNode(private val listener: NodeListener) : AbstractNode() {
    override fun onStart(parentNode: ConnectedNode) {
        super.onStart(parentNode)
        try {
            widget?.let {
                it.validMessage = true
            }
            val subscriber: Subscriber<out Message> =
                parentNode.newSubscriber(topic.name, topic.type)
            subscriber.addMessageListener { data: Message ->
                lastRosData = RosData(topic, data)
                lastRosData?.let{
                    listener.onNewMessage(it)
                }
            }
        } catch (e: Exception) {
            widget?.let {
                it.validMessage = false
            }
            e.printStackTrace()
        }
    }

    interface NodeListener {
        fun onNewMessage(message: RosData)
    }
}
