package com.example.altintakipandroid.ui.assets

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.altintakipandroid.data.api.RetrofitClient
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.domain.ExchangeRate
import com.example.altintakipandroid.domain.UserAsset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AssetsState(
    val rates: List<ExchangeRate> = emptyList(),
    val assets: List<UserAsset> = emptyList(),
    val totalValue: Double = 0.0,
    val totalProfit: Double = 0.0,
    val showAddAsset: Boolean = false
)

class AssetsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application.applicationContext)
    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow(AssetsState())
    val state: StateFlow<AssetsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadRates()
            loadAssets()
        }
    }

    fun loadRates() {
        viewModelScope.launch {
            val apiKey = prefs.getApiKey() ?: return@launch
            runCatching {
                val resp = api.fetchExchangeRates(apiKey)
                _state.value = _state.value.copy(rates = resp.body()?.data ?: emptyList())
                updateTotals()
            }
        }
    }

    fun loadAssets() {
        viewModelScope.launch {
            val list = prefs.getUserAssets()
            _state.value = _state.value.copy(assets = list)
            updateTotals()
        }
    }

    fun saveAsset(asset: UserAsset) {
        viewModelScope.launch {
            val list = _state.value.assets + asset
            prefs.saveUserAssets(list)
            _state.value = _state.value.copy(assets = list, showAddAsset = false)
            updateTotals()
        }
    }

    fun deleteAsset(id: String) {
        viewModelScope.launch {
            val list = _state.value.assets.filter { it.id != id }
            prefs.saveUserAssets(list)
            _state.value = _state.value.copy(assets = list)
            updateTotals()
        }
    }

    fun showAddAsset(show: Boolean) {
        _state.value = _state.value.copy(showAddAsset = show)
    }

    private fun updateTotals() {
        val rates = _state.value.rates
        var totalValue = 0.0
        var totalCost = 0.0
        _state.value.assets.forEach { asset ->
            val rate = rates.find { it.apiId == asset.exchangeRateId }
            val sell = rate?.sell ?: 0.0
            totalValue += sell * asset.amount
            totalCost += asset.purchasePrice * asset.amount
        }
        _state.value = _state.value.copy(
            totalValue = totalValue,
            totalProfit = totalValue - totalCost
        )
    }
}
