package com.example.altintakipandroid.ui.markets

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.altintakipandroid.data.api.RetrofitClient
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.data.websocket.PriceWebSocketClient
import com.example.altintakipandroid.domain.ExchangeRate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class MarketsState(
    val rates: List<ExchangeRate> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val wsConnected: Boolean = false
)

class MarketsViewModel(
    application: Application,
    private val useWebSocket: Boolean = false
) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application.applicationContext)
    private val api = RetrofitClient.apiService
    private val wsClient = PriceWebSocketClient()

    private val _state = MutableStateFlow(MarketsState())
    val state: StateFlow<MarketsState> = _state.asStateFlow()

    private var wsJob: Job? = null

    init {
        viewModelScope.launch { loadRates() }
        if (useWebSocket) startWebSocket()
    }

    private fun startWebSocket() {
        wsJob?.cancel()
        wsJob = viewModelScope.launch {
            val apiKey = prefs.getApiKey() ?: return@launch
            runCatching {
                val tokenResp = api.getWsToken(apiKey)
                val token = tokenResp.body()?.data?.token
                if (token.isNullOrBlank()) return@launch
                wsClient.connect(token)
                    .catch { _state.value = _state.value.copy(wsConnected = false) }
                    .collect { newRates ->
                        if (newRates.isEmpty()) return@collect
                        val current = _state.value.rates
                        val merged = mergeRates(current, newRates)
                        _state.value = _state.value.copy(
                            rates = merged,
                            wsConnected = true
                        )
                    }
            }
            _state.value = _state.value.copy(wsConnected = false)
        }
    }

    private fun mergeRates(current: List<ExchangeRate>, update: List<ExchangeRate>): List<ExchangeRate> {
        val byId = current.associateBy { it.apiId }.toMutableMap()
        update.forEach { r -> if (r.apiId != null) byId[r.apiId] = r }
        val updatedOrder = current.map { byId[it.apiId] ?: it }
        val newIds = update.mapNotNull { it.apiId }.toSet() - current.mapNotNull { it.apiId }.toSet()
        val appended = update.filter { it.apiId in newIds }
        return updatedOrder + appended
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
    }

    class Factory(
        private val application: Application,
        private val useWebSocket: Boolean
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MarketsViewModel::class.java)) {
                return MarketsViewModel(application, useWebSocket) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
