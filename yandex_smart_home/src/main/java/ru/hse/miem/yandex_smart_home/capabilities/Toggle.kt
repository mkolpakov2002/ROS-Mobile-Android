package ru.hse.miem.yandex_smart_home.capabilities

data class Toggle(var value: Boolean, var instance: ToggleFunctions) : BaseCapability("toggle") {
    val state: Map<String, Any>
        get() = mapOf("instance" to instance, "value" to value)

    operator fun invoke(): Map<String, Any> {
        return mapOf("type" to _type, "state" to state)
    }
}