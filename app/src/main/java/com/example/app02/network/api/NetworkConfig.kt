package com.example.app02.network.api

object NetworkConfig {
    private const val IP = "192.168.101.96"
    private const val PORT = "8080"

    const val BASE_URL = "http://${IP}:${PORT}"
    const val WS_URL = "ws://${IP}:${PORT}/ws/websocket"
}