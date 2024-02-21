package ru.hse.miem.yandex_smart_home.capabilities

data class Range(var value: Any, var instance: RangeFunctions) : BaseCapability("range") {
    val state: Map<String, Any>
        get() = mapOf("instance" to instance, "value" to value)

    operator fun invoke(): Map<String, Any> {
        return mapOf("type" to _type, "state" to state)
    }
}