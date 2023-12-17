package ru.hse.miem.yandex_smart_home.capabilities

open class BaseCapability(val type: String) {
    val _type: String
        get() = "devices.capabilities.$type"
}