package ru.hse.miem.ros.data.model.repositories.rosRepo.node

import org.ros.internal.message.Message
import org.ros.node.ConnectedNode
import org.ros.node.topic.Publisher
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.entities.widgets.PublisherLayerEntity
import java.util.Timer
import java.util.TimerTask

/**
 * ROS Node for publishing Messages on a specific topic.
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 16.09.20
 * @updated on
 * @modified by
 */
class PubNode : AbstractNode() {
    private var publisher: Publisher<Message>? = null
    private var lastData: BaseData? = null
    private var pubTimer: Timer? = null
    private var pubPeriod: Long = 100L
    private var immediatePublish: Boolean = true

    override var widget: BaseEntity? = null
        set(value) {
            field = value
            if (value is PublisherLayerEntity) {
                setImmediatePublish(value.immediatePublish)
                setFrequency(value.publishRate)
            }
        }

    override fun onStart(parentNode: ConnectedNode) {
        publisher = parentNode.newPublisher(topic.name, topic.type)
        createAndStartSchedule()
    }

    fun setData(data: BaseData) {
        this.lastData = data
        if (immediatePublish) {
            publish()
        }
    }

    fun setFrequency(hz: Float) {
        this.pubPeriod = (1000 / hz).toLong()
    }

    fun setImmediatePublish(flag: Boolean) {
        this.immediatePublish = flag
    }

    private fun createAndStartSchedule() {
        pubTimer?.cancel()
        if (!immediatePublish) {
            pubTimer = Timer().apply {
                schedule(object : TimerTask() {
                    override fun run() {
                        publish()
                    }
                }, pubPeriod, pubPeriod)
            }
        }
    }

    private fun publish() {
        if (publisher == null || lastData == null) {
            return
        }
        val message = lastData?.toRosMessage(publisher!!, widget!!)
        publisher?.publish(message)
    }

}
