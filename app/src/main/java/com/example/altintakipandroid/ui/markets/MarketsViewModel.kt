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

    /**
     * iOS labelDictionary: REST /gold-currency'den gelen showableText etiketlerini tutar.
     * WS rate'lerinde etiket hicbir zaman WS'ten alinmaz; once buradan, sonra mevcut satirdan cozumlenir.
     */
    private val labelDictionary = mutableMapOf<String, String>()

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
                val tokenData = tokenResp.body()?.data
                val token = tokenData?.token
                if (token.isNullOrBlank()) {
                    Log.w(TAG, "startWebSocket: no token in response (code=${tokenResp.code()}), skipping")
                    return@launch
                }

                // iOS: token suresi dolmadan once (%80) yeniden baglan
                val expiresIn = tokenData.expiresIn ?: 3600
                val refreshDelayMs = (expiresIn * 0.8 * 1000).toLong()
                Log.d(TAG, "startWebSocket: token expiresIn=${expiresIn}s, refresh in ${refreshDelayMs}ms")
                scheduleTokenRefresh(refreshDelayMs)

                Log.d(TAG, "startWebSocket: connecting with token...")
                wsClient.connect(token)
                    .catch {
                        // iOS: WS kopunca jitter'i durdur, REST fallback'e gec
                        jitterJob?.cancel()
                        _state.value = _state.value.copy(wsConnected = false)
                        loadRates(force = true)
                    }
                    .collect { newRates ->
                        if (newRates.isEmpty()) return@collect
                        handleIncomingWsRates(newRates)
                    }
            }
            // WS normal kapandiysa jitter'i durdur, REST fallback'e gec
            jitterJob?.cancel()
            _state.value = _state.value.copy(wsConnected = false)
            loadRates(force = true)
        }
    }

    private var tokenRefreshJob: Job? = null

    /**
     * iOS: token suresinin %80'inde WS'i yeniden baslat.
     */
    private fun scheduleTokenRefresh(delayMs: Long) {
        tokenRefreshJob?.cancel()
        tokenRefreshJob = viewModelScope.launch {
            delay(delayMs)
            Log.d(TAG, "Token refresh: reconnecting WebSocket...")
            startWebSocket()
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
                        if (pendingDripUpdates.isEmpty()) null
                        else pendingDripUpdates.removeAt(Random.nextInt(pendingDripUpdates.size))
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
     * iOS dripTimer blogu ile ayni: mevcut satirdaki alanlari WS guncellmesiyle birlestir.
     * Eksik WS alanlari icin mevcut rate degerini koru (buy ?? existing.buy vb.).
     * showableText asla WS'ten alinmaz; once labelDictionary, sonra mevcut satir.
     */
    private fun applyOneDripRate(rate: ExchangeRate) {
        val current = _state.value.rates.toMutableList()
        val code = rate.currencyCode ?: rate.baseCurrencyCode ?: ""
        val index = current.indexOfFirst { r ->
            (r.apiId != null && r.apiId != 0 && r.apiId == rate.apiId) ||
                (r.currencyCode != null && r.currencyCode == rate.currencyCode) ||
                (code.isNotBlank() && (r.currencyCode == code || r.baseCurrencyCode == code))
        }

        if (index >= 0) {
            val existing = current[index]
            val resolvedCode = rate.currencyCode ?: rate.baseCurrencyCode
                ?: existing.currencyCode ?: existing.baseCurrencyCode ?: ""
            val labelFromDict = labelDictionary[resolvedCode]
            val resolvedLabel = labelFromDict ?: existing.showableText
                ?: existing.baseCurrencyCode ?: existing.currencyCode ?: ""

            val merged = existing.copy(
                apiId = if (rate.apiId != null && rate.apiId != 0) rate.apiId else existing.apiId,
                baseCurrencyCode = rate.baseCurrencyCode ?: existing.baseCurrencyCode,
                targetCurrencyCode = rate.targetCurrencyCode ?: existing.targetCurrencyCode,
                currencyCode = rate.currencyCode ?: existing.currencyCode,
                buy = rate.buy ?: existing.buy,
                sell = rate.sell ?: existing.sell,
                changeRate = rate.changeRate ?: existing.changeRate,
                dayHigh = rate.dayHigh ?: existing.dayHigh,
                dayLow = rate.dayLow ?: existing.dayLow,
                prevClose = rate.prevClose ?: existing.prevClose,
                fetchedAt = rate.fetchedAt ?: existing.fetchedAt,
                showableText = resolvedLabel
            )
            current[index] = merged
        } else {
            // REST'te olmayan yeni bir kod; labelDictionary'den veya kendi alanlari
            val resolvedLabel = labelDictionary[code]
                ?: rate.showableText ?: rate.baseCurrencyCode ?: rate.currencyCode ?: ""
            current.add(rate.copy(showableText = resolvedLabel))
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

    /**
     * REST /gold-currency. When WebSocket is active and already providing data, skip to avoid redundant request (iOS: fetchRates(force: false) early return).
     */
    fun loadRates(force: Boolean = false) {
        if (!force && useWebSocket && _state.value.wsConnected) {
            Log.d(TAG, "loadRates: skipped (WebSocket active)")
            return
        }
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
                // iOS labelDictionary: REST cevabindan etiketleri al, WS driplerinde kullanilacak
                list.forEach { rate ->
                    val code = rate.currencyCode ?: rate.baseCurrencyCode ?: return@forEach
                    if (code.isNotBlank()) {
                        val label = rate.showableText ?: rate.baseCurrencyCode ?: rate.currencyCode ?: ""
                        if (label.isNotBlank()) labelDictionary[code] = label
                    }
                }
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
        tokenRefreshJob?.cancel()
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
