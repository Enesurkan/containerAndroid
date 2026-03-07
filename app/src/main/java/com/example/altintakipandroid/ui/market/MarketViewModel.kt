package com.example.altintakipandroid.ui.market

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.altintakipandroid.data.api.RetrofitClient
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.domain.BannerSlide
import com.example.altintakipandroid.domain.CategoryTree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MarketState(
    val categories: List<CategoryTree> = emptyList(),
    val bannerSlides: List<BannerSlide> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val hasFetched: Boolean = false
)

class MarketViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application.applicationContext)
    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow(MarketState())
    val state: StateFlow<MarketState> = _state.asStateFlow()

    fun fetchDataIfNeeded() {
        if (_state.value.hasFetched) return
        viewModelScope.launch {
            val apiKey = prefs.getApiKey() ?: run {
                _state.value = _state.value.copy(errorMessage = "API Anahtarı bulunamadı")
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                val catResp = api.fetchCategories(apiKey)
                val banResp = api.fetchBannerSlides(apiKey)
                val categories = catResp.body()?.data ?: emptyList()
                val banners = banResp.body()?.data ?: emptyList()
                _state.value = _state.value.copy(
                    categories = categories,
                    bannerSlides = banners,
                    hasFetched = true,
                    isLoading = false
                )
            }.onFailure {
                _state.value = _state.value.copy(
                    errorMessage = it.message ?: "Veri yüklenemedi",
                    hasFetched = true,
                    isLoading = false
                )
            }
        }
    }
}
