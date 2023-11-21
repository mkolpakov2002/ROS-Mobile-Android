package ru.hse.miem.yandex_smart_home.devices

import ru.hse.miem.yandex_smart_home.api.YandexApi

abstract class DeviceCategories(val apiInstance: YandexApi) {
    val purifer: Purifer
        get() = Purifer(apiInstance)

    val vacuumCleaner: VacuumCleaner
        get() = VacuumCleaner(apiInstance)

    val light: Light
        get() = Light(apiInstance)

    val tvoc: Tvoc
        get() = Tvoc(apiInstance)
}
