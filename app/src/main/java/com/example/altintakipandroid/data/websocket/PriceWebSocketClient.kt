package com.example.altintakipandroid.data.websocket

import com.example.altintakipandroid.domain.AppConstants
import com.example.altintakipandroid.domain.ExchangeRate
import com.example.altintakipandroid.domain.ExchangeRatesResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

/**
 * WebSocket client for live price updates. Connects to wss://host/ws/prices?token=xxx.
 */
class PriceWebSocketClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS) // no timeout for WS
        .writeTimeout(15, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val listType = object : TypeToken<List<ExchangeRate>>() {}.type

    /**
     * Connect to WS and emit each parsed list of [ExchangeRate] from incoming messages.
     * Emits when connection opens, then on each text message (parsed as list or data wrapper).
     */
    fun connect(token: String): Flow<List<ExchangeRate>> = callbackFlow {
        val baseUrl = AppConstants.BASE_URL
            .replace("https://", "wss://")
            .replace("http://", "ws://")
        val wsUrl = "$baseUrl${AppConstants.Api.WS_PRICES}?token=${token.trim()}"

        val request = Request.Builder().url(wsUrl).build()
        var webSocket: WebSocket? = null

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Initial open - no data yet; first snapshot may come from server
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val list = parseRates(text)
                    if (list.isNotEmpty()) trySend(list)
                } catch (_: Exception) { /* ignore parse errors */ }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {}
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                close()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                close(t)
            }
        }

        webSocket = client.newWebSocket(request, listener)

        awaitClose {
            webSocket?.close(1000, null)
        }
    }

    private fun parseRates(json: String): List<ExchangeRate> {
        return try {
            val trimmed = json.trim()
            when {
                trimmed.startsWith("[") -> gson.fromJson(trimmed, listType) ?: emptyList()
                trimmed.startsWith("{") -> {
                    val wrapper = gson.fromJson(trimmed, ExchangeRatesResponse::class.java)
                    wrapper.data ?: emptyList()
                }
                else -> emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
