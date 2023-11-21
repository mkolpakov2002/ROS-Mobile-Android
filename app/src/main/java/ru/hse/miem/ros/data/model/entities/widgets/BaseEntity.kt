package ru.hse.miem.ros.data.model.entities.widgets

import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.1
 * @created on 23.09.20
 * @updated on 10.03.21
 * @modified by Maxim Kolpakov
 */
abstract class BaseEntity {
    var id: Long = 0
    var name: String = ""
    var type: String? = null
    var configId: Long = 0
    var creationTime: Long = 0
    lateinit var topic: Topic
    var validMessage = false
    var childEntities: MutableList<BaseEntity> = mutableListOf()

    open fun equalRosState(other: BaseEntity): Boolean {
        return topic == other.topic
    }

    fun addEntity(entity: BaseEntity) {
        childEntities.add(entity)
    }

    fun getChildById(id: Long): BaseEntity? {
        return childEntities.find { it.id == id }
    }

    fun removeChild(entity: BaseEntity) {
        childEntities.removeIf { it.id == entity.id }
    }

    fun replaceChild(entity: BaseEntity) {
        val index = childEntities.indexOfFirst { it.id == entity.id }
        if (index != -1) {
            childEntities[index] = entity
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BaseEntity
        if (id != other.id) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (configId != other.configId) return false
        if (creationTime != other.creationTime) return false
        if (topic != other.topic) return false
        if (validMessage != other.validMessage) return false
        if (childEntities != other.childEntities) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (name.hashCode())
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + configId.hashCode()
        result = 31 * result + creationTime.hashCode()
        result = 31 * result + topic.hashCode()
        result = 31 * result + validMessage.hashCode()
        result = 31 * result + childEntities.hashCode()
        return result
    }

    fun copy(): BaseEntity {
        val copy = this::class.java.getDeclaredConstructor().newInstance()
        copy.id = id
        copy.name = name
        copy.type = type
        copy.configId = configId
        copy.creationTime = creationTime
        copy.topic = topic
        copy.validMessage = validMessage
        copy.childEntities = childEntities.map { it.copy() }.toMutableList()
        return copy
    }

    override fun toString(): String {
        return "BaseEntity(id=$id, name=$name, type=$type, configId=$configId, creationTime=$creationTime, topic=$topic, validMessage=$validMessage, childEntities=$childEntities)"
    }
}
