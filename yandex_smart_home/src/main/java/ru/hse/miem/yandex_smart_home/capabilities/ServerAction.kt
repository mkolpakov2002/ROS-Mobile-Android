package ru.hse.miem.yandex_smart_home.capabilities

data class ServerAction(var value: Any, var instance: String = "text_action") : BaseCapability("server_action") {
    val state: Map<String, Any>
        get() = mapOf("instance" to instance, "value" to value)

    operator fun invoke(): Map<String, Any> {
        return mapOf("type" to _type, "state" to state)
    }
}