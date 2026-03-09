package com.example.altintakipandroid.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.altintakipandroid.data.api.RetrofitClient
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.domain.GoldMultiplier
import com.example.altintakipandroid.domain.SaveGoldMultiplierItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GoldMultipliersState(
    val multipliers: List<GoldMultiplier> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val showSuccess: Boolean = false,
    val saveErrorMessage: String? = null
)

class GoldMultipliersViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application.applicationContext)
    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow(GoldMultipliersState())
    val state: StateFlow<GoldMultipliersState> = _state.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            val apiKey = prefs.getApiKey() ?: run {
                _state.value = _state.value.copy(isLoading = false, errorMessage = "API anahtarı yok")
                return@launch
            }
            runCatching {
                val resp = api.fetchGoldMultipliers(apiKey)
                val list = resp.body()?.data ?: emptyList()
                _state.value = _state.value.copy(multipliers = list, isLoading = false)
            }.onFailure {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = it.message ?: "Yükleme hatası"
                )
            }
        }
    }

    fun updateBuyMultiplier(index: Int, value: Double) {
        val list = _state.value.multipliers.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(buyMultiplier = value)
            _state.value = _state.value.copy(multipliers = list)
        }
    }

    fun updateSellMultiplier(index: Int, value: Double) {
        val list = _state.value.multipliers.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(sellMultiplier = value)
            _state.value = _state.value.copy(multipliers = list)
        }
    }

    fun save() {
        viewModelScope.launch {
            val list = _state.value.multipliers
            if (list.isEmpty()) {
                _state.value = _state.value.copy(saveErrorMessage = "Kaydedilecek veri yok")
                return@launch
            }
            _state.value = _state.value.copy(isSaving = true, saveErrorMessage = null)
            val apiKey = prefs.getApiKey() ?: run {
                _state.value = _state.value.copy(isSaving = false, saveErrorMessage = "API anahtarı yok")
                return@launch
            }
            val body = list.map { SaveGoldMultiplierItem(it.currencyCode, it.buyMultiplier, it.sellMultiplier) }
            runCatching {
                val resp = api.saveGoldMultipliers(apiKey, body = body)
                if (resp.isSuccessful) {
                    _state.value = _state.value.copy(isSaving = false, showSuccess = true, saveErrorMessage = null)
                } else {
                    val bodyStr = resp.errorBody()?.string()?.take(300)?.trim() ?: ""
                    _state.value = _state.value.copy(
                        isSaving = false,
                        saveErrorMessage = "Kaydetme başarısız (${resp.code()})${if (bodyStr.isNotBlank()) "\n\n$bodyStr" else ""}"
                    )
                }
            }.onFailure {
                _state.value = _state.value.copy(
                    isSaving = false,
                    saveErrorMessage = it.message ?: "Kaydetme hatası"
                )
            }
        }
    }

    fun clearSuccess() {
        _state.value = _state.value.copy(showSuccess = false)
    }

    fun clearSaveError() {
        _state.value = _state.value.copy(saveErrorMessage = null)
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
