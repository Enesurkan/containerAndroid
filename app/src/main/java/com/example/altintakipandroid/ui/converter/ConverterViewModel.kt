package com.example.altintakipandroid.ui.converter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.altintakipandroid.data.api.RetrofitClient
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.domain.ExchangeRate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConverterState(
    val rates: List<ExchangeRate> = emptyList(),
    val sourceAsset: ExchangeRate? = null,
    val amount: String = "",
    val result: Double = 0.0,
    val showAssetPicker: Boolean = false
)

class ConverterViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application.applicationContext)
    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow(ConverterState())
    val state: StateFlow<ConverterState> = _state.asStateFlow()

    init {
        viewModelScope.launch { loadRates() }
    }

    fun loadRates() {
        viewModelScope.launch {
            val apiKey = prefs.getApiKey() ?: return@launch
            runCatching {
                val resp = api.fetchExchangeRates(apiKey)
                val list = resp.body()?.data ?: emptyList()
                _state.value = _state.value.copy(rates = list)
                if (_state.value.sourceAsset == null && list.isNotEmpty()) {
                    _state.value = _state.value.copy(sourceAsset = list.first())
                    calculate()
                }
            }
        }
    }

    fun setAmount(amount: String) {
        _state.value = _state.value.copy(amount = amount)
        calculate()
    }

    fun setSourceAsset(rate: ExchangeRate) {
        _state.value = _state.value.copy(sourceAsset = rate, showAssetPicker = false)
        calculate()
    }

    fun showAssetPicker(show: Boolean) {
        _state.value = _state.value.copy(showAssetPicker = show)
    }

    private fun calculate() {
        val amountVal = _state.value.amount.replace(",", ".").toDoubleOrNull() ?: 0.0
        val sell = _state.value.sourceAsset?.sell ?: 0.0
        _state.value = _state.value.copy(result = sell * amountVal)
    }
}
