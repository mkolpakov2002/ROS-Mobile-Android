package ru.hse.miem.yandex_smart_home.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json

abstract class ABCAPI(clientToken: String, host: String = "https://api.iot.yandex.net", version: String = "/v1.0") {
    val client = HttpClient(CIO) {
        install(ContentNegotiation){
            json()
        }
        defaultRequest {
            header("Authorization", "Bearer $clientToken")
            url {
                takeFrom(host)
                encodedPath = version + encodedPath
            }
        }
    }

//    suspend fun getSmartHomeInfo(resource: String = "/user/info"): Map<String, Any> {
//        return client.get(resource)
//    }
//
//    suspend fun getDeviceInfo(deviceId: String, resource: String = "/devices/"): Map<String, Any> {
//        return client.get("$resource$deviceId")
//    }
//
//    suspend fun getGroupInfo(groupId: String, resource: String = "/groups/"): Map<String, Any> {
//        return client.get("$resource$groupId")
//    }
//
//    suspend fun deleteDevice(deviceId: String, resource: String = "/devices/"): Map<String, Any> {
//        return client.delete("$resource$deviceId")
//    }

//    suspend fun devicesAction(deviceId: String, actions: List<Map<String, Any>>, resource: String = "/devices/actions"): Map<String, Any> {
//        val data = mapOf(
//            "devices" to listOf(
//                mapOf(
//                    "id" to deviceId,
//                    "actions" to actions
//                )
//            )
//        )
//        return client.post(resource) {
//            body = data
//        }
//    }
//
//    suspend fun groupAction(groupId: String, data: Map<String, Any>, resource: String = "/groups/"): Map<String, Any> {
//        return client.post("$resource$groupId/actions") {
//            body = data
//        }
//    }
//
//    suspend fun scenarioAction(scenarioId: String, resource: String = "/scenarios/"): Map<String, Any> {
//        return client.post("$resource$scenarioId/actions")
//    }
}
