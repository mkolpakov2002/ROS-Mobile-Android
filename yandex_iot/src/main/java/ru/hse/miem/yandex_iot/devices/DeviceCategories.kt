package ru.hse.miem.yandex_iot.devices


abstract class DeviceCategories() {
    val purifer: Purifer
        get() = Purifer()

    val vacuumCleaner: VacuumCleaner
        get() = VacuumCleaner()

    val light: Light
        get() = Light()

    val tvoc: Tvoc
        get() = Tvoc()
}
