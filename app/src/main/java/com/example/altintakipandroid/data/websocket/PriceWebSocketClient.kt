package com.example.altintakipandroid.data.websocket

import android.util.Log
import com.example.altintakipandroid.domain.AppConstants
import com.example.altintakipandroid.domain.ExchangeRate
import com.example.altintakipandroid.domain.ExchangeRatesResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
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
 * iOS WebSocketManager'a uygun: sadece type="gold_currency" mesajlari emit edilir;
 * "hello", "ping", "snapshot", "delta" mesajlari yoksayilir.
 */
class PriceWebSocketClient {

    companion object {
        private const val TAG = "PriceWebSocket"
    }

    private data class WsMessageHeader(val type: String?)

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS) // no timeout for WS
        .writeTimeout(15, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val gsonPretty = GsonBuilder().setPrettyPrinting().create()
    private val listType = object : TypeToken<List<ExchangeRate>>() {}.type

    private fun prettyLogMessage(text: String) {
        val pretty = try {
            val element = JsonParser.parseString(text.trim())
            gsonPretty.toJson(element)
        } catch (_: Exception) {
            text
        }
        val maxChunk = 3500
        if (pretty.length <= maxChunk) {
            Log.d(TAG, "onMessage (pretty):\n$pretty")
        } else {
            var start = 0
            var part = 1
            while (start < pretty.length) {
                val end = minOf(start + maxChunk, pretty.length)
                Log.d(TAG, "onMessage (pretty) part $part:\n${pretty.substring(start, end)}")
                start = end
                part++
            }
        }
    }

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
                Log.d(TAG, "onOpen: connected to $wsUrl")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                prettyLogMessage(text)
                try {
                    val header = gson.fromJson(text.trim(), WsMessageHeader::class.java)
                    when (header?.type) {
                        "gold_currency" -> {
                            val list = parseRates(text)
                            Log.d(TAG, "gold_currency: parsed ${list.size} rates")
                            if (list.isNotEmpty()) trySend(list)
                        }
                        "hello" -> Log.d(TAG, "WS connected: hello received")
                        "ping" -> { /* no-op */ }
                        "snapshot", "delta" -> Log.d(TAG, "WS message ignored: type=${header.type}")
                        null -> {
                            // type alani yoksa direkt liste formatini dene (geriye uyumluluk)
                            val list = parseRates(text)
                            if (list.isNotEmpty()) trySend(list)
                        }
                        else -> Log.d(TAG, "WS message unknown type: ${header.type}")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "onMessage: parse error", e)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "onClosing: code=$code reason=$reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "onClosed: code=$code reason=$reason")
                close()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "onFailure: ${t.message}", t)
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
