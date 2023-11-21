package ru.hse.miem.yandex_smart_home.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

class YandexSessionAsync(val login: String, val password: String, val stationId: String) {
    private val quasarUrl = "https://iot.quasar.yandex.ru/m/user"
    private val musicUrl = "https://api.music.yandex.net"
    private var csrfToken: String? = null
    private var musicUid = 0
    private val headers = mapOf("User-Agent" to "Chrome", "Host" to "passport.yandex.ru")
    private val client = HttpClient {
        install(ContentNegotiation){
            json()
        }
//        defaultRequest {
//            headers.forEach { (key, value) -> header(key, value) }
//        }
    }

    private fun createScenario(scenarioName: String, activationCommand: String, logic: Map<String, Any>): Map<String, Any> {
        return mapOf(
            "name" to scenarioName,
            "icon" to "scenario",
            "triggers" to listOf(
                mapOf(
                    "type" to "scenario.trigger.voice",
                    "state" to mapOf(
                        "instance" to "phrase",
                        "value" to activationCommand
                    )
                )
            ),
            "actions" to listOf(logic)
        )
    }
}
