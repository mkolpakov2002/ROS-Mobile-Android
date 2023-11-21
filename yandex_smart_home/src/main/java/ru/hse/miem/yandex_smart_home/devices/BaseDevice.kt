package ru.hse.miem.yandex_smart_home.devices

import ru.hse.miem.yandex_smart_home.api.YandexApi

abstract class BaseDevice(val api: YandexApi) {
    lateinit var deviceId: String

}