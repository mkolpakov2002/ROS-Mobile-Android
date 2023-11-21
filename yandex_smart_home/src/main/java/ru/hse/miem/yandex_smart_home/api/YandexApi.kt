package ru.hse.miem.yandex_smart_home.api

class YandexApi(clientToken: String, host: String = "https://api.iot.yandex.net",
                version: String = "/v1.0")
    : ABCAPI(clientToken, host, version)
