package com.example.altintakipandroid.ui.markets

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.altintakipandroid.data.api.RetrofitClient
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.data.websocket.PriceWebSocketClient
import com.example.altintakipandroid.domain.ExchangeRate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class MarketsState(
    val rates: List<ExchangeRate> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val wsConnected: Boolean = false
)

class MarketsViewModel(
    application: Application,
    private val useWebSocket: Boolean = false,
    private val timerIntervalSeconds: Int? = null,
    private val wsPriceJitterEnabled: Boolean? = null,
    private val wsPriceJitterIntervalSec: Int? = null,
    private val wsDripIntervalMs: Int? = null
) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application.applicationContext)
    private val api = RetrofitClient.apiService
    private val wsClient = PriceWebSocketClient()

    private val _state = MutableStateFlow(MarketsState())
    val state: StateFlow<MarketsState> = _state.asStateFlow()

    private var wsJob: Job? = null
    private var refreshJob: Job? = null
    private var dripJob: Job? = null
    private var jitterJob: Job? = null

    private val dripMutex = Mutex()
    private val pendingDripUpdates = mutableListOf<ExchangeRate>()

    companion object {
        private const val TAG = "MarketsVM"
    }

    init {
        Log.d(TAG, "init: useWebSocket=$useWebSocket, wsDripIntervalMs=$wsDripIntervalMs, wsPriceJitterEnabled=$wsPriceJitterEnabled")
        viewModelScope.launch { loadRates() }
        if (useWebSocket) {
            startWebSocket()
            startJitterIfNeeded()
        } else {
            Log.d(TAG, "init: skipping WebSocket (mobileUseWebSocket is not true)")
        }
        startAutoRefreshIfNeeded()
    }

    private fun startAutoRefreshIfNeeded() {
        // iOS: when mobileUseWebSocket is true, only WS is used; no periodic REST polling.
        if (useWebSocket) return
        val interval = timerIntervalSeconds?.takeIf { it > 0 } ?: return
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (true) {
                delay(interval * 1000L)
                loadRates()
            }
        }
    }

    private fun startWebSocket() {
        wsJob?.cancel()
        dripJob?.cancel()
        wsJob = viewModelScope.launch {
            val apiKey = prefs.getApiKey()
            if (apiKey.isNullOrBlank()) {
                Log.w(TAG, "startWebSocket: no API key, skipping")
                return@launch
            }
            runCatching {
                val tokenResp = api.getWsToken(apiKey)
                val token = tokenResp.body()?.data?.token
                if (token.isNullOrBlank()) {
                    Log.w(TAG, "startWebSocket: no token in response (code=${tokenResp.code()}), skipping")
                    return@launch
                }
                Log.d(TAG, "startWebSocket: connecting with token...")
                wsClient.connect(token)
                    .catch { _state.value = _state.value.copy(wsConnected = false) }
                    .collect { newRates ->
                        if (newRates.isEmpty()) return@collect
                        handleIncomingWsRates(newRates)
                    }
            }
            _state.value = _state.value.copy(wsConnected = false)
        }
    }

    /**
     * iOS handleIncomingWSRates: initial snapshot applies all; later batches drip one-by-one if wsDripIntervalMs set.
     */
    private suspend fun handleIncomingWsRates(newRates: List<ExchangeRate>) {
        val current = _state.value.rates
        val dripMs = wsDripIntervalMs?.takeIf { it > 0 }
        Log.d(TAG, "WS data: ${newRates.size} rates, wsDripIntervalMs=$wsDripIntervalMs, drip=${dripMs != null}")

        if (current.isEmpty()) {
            // Initial snapshot: apply all at once (like iOS)
            val merged = mergeRates(emptyList(), newRates)
            _state.value = _state.value.copy(rates = merged, wsConnected = true)
            dripMutex.withLock { pendingDripUpdates.clear() }
            return
        }

        if (dripMs != null) {
            _state.value = _state.value.copy(wsConnected = true)
            dripMutex.withLock { pendingDripUpdates.clear(); pendingDripUpdates.addAll(newRates) }
            dripJob?.cancel()
            dripJob = viewModelScope.launch {
                while (true) {
                    delay(dripMs.toLong())
                    val next = dripMutex.withLock {
                        if (pendingDripUpdates.isEmpty()) null else pendingDripUpdates.removeAt(0)
                    } ?: break
                    applyOneDripRate(next)
                }
            }
        } else {
            val merged = mergeRates(current, newRates)
            _state.value = _state.value.copy(rates = merged, wsConnected = true)
        }
    }

    /**
     * Apply a single rate from drip queue: update existing by apiId/currencyCode or append.
     */
    private fun applyOneDripRate(rate: ExchangeRate) {
        val current = _state.value.rates.toMutableList()
        val code = rate.currencyCode ?: rate.baseCurrencyCode ?: ""
        val index = current.indexOfFirst { r ->
            (r.apiId != null && r.apiId != 0 && r.apiId == rate.apiId) ||
                (r.currencyCode != null && r.currencyCode == rate.currencyCode) ||
                (code.isNotBlank() && (r.currencyCode == code || r.baseCurrencyCode == code))
        }
        val existing = if (index >= 0) current[index] else null
        val label = existing?.showableText ?: rate.showableText ?: rate.baseCurrencyCode ?: rate.currencyCode ?: ""
        val toApply = rate.copy(showableText = label.ifBlank { rate.showableText ?: rate.baseCurrencyCode ?: rate.currencyCode ?: "" })
        if (index >= 0) {
            current[index] = toApply
        } else {
            current.add(toApply)
        }
        _state.value = _state.value.copy(rates = current, wsConnected = true)
    }

    private fun mergeRates(current: List<ExchangeRate>, update: List<ExchangeRate>): List<ExchangeRate> {
        val byId = current.associateBy { it.apiId }.toMutableMap()
        update.forEach { r -> if (r.apiId != null) byId[r.apiId] = r }
        val updatedOrder = current.map { byId[it.apiId] ?: it }
        val newIds = update.mapNotNull { it.apiId }.toSet() - current.mapNotNull { it.apiId }.toSet()
        val appended = update.filter { it.apiId in newIds }
        return updatedOrder + appended
    }

    /**
     * iOS applyJitterEffect: every jitterIntervalSec, ~25% of rows get ±0.05% fluctuation on buy/sell.
     */
    private fun startJitterIfNeeded() {
        if (wsPriceJitterEnabled != true) return
        val intervalSec = (wsPriceJitterIntervalSec ?: 10).coerceAtLeast(1)
        Log.d(TAG, "WS jitter: enabled, interval=${intervalSec}s")
        jitterJob?.cancel()
        jitterJob = viewModelScope.launch {
            while (true) {
                delay(intervalSec * 1000L)
                applyJitterEffect()
            }
        }
    }

    private fun applyJitterEffect() {
        val rates = _state.value.rates
        if (rates.isEmpty()) return
        val updated = rates.mapIndexed { i, rate ->
            // ~25% chance per row (iOS: Bool.random() && Bool.random())
            if (Random.nextBoolean() && Random.nextBoolean()) {
                val sellVal = rate.sell ?: 0.0
                val jitterAmount = sellVal * 0.0005 // 0.05%
                val sign = if (Random.nextBoolean()) 1.0 else -1.0
                val delta = jitterAmount * sign
                rate.copy(
                    buy = (rate.buy ?: 0.0) + delta,
                    sell = sellVal + delta
                )
            } else rate
        }
        _state.value = _state.value.copy(rates = updated)
    }

    fun loadRates() {
        viewModelScope.launch {
            val apiKey = prefs.getApiKey() ?: run {
                _state.value = _state.value.copy(errorMessage = "API anahtarı yok", isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                val resp = api.fetchExchangeRates(apiKey)
                if (!resp.isSuccessful) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Veri alınamadı (${resp.code()})"
                    )
                    return@launch
                }
                val list = resp.body()?.data ?: emptyList()
                _state.value = _state.value.copy(rates = list, isLoading = false, errorMessage = null)
            }.onFailure {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = it.message ?: "Bağlantı hatası"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        wsJob?.cancel()
        refreshJob?.cancel()
        dripJob?.cancel()
        jitterJob?.cancel()
    }

    class Factory(
        private val application: Application,
        private val useWebSocket: Boolean,
        private val timerIntervalSeconds: Int? = null,
        private val wsPriceJitterEnabled: Boolean? = null,
        private val wsPriceJitterIntervalSec: Int? = null,
        private val wsDripIntervalMs: Int? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MarketsViewModel::class.java)) {
                return MarketsViewModel(
                    application,
                    useWebSocket,
                    timerIntervalSeconds,
                    wsPriceJitterEnabled,
                    wsPriceJitterIntervalSec,
                    wsDripIntervalMs
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
