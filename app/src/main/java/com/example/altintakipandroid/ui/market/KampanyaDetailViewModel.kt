package com.example.altintakipandroid.ui.market

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.altintakipandroid.data.api.RetrofitClient
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.domain.BannerSlide
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class KampanyaDetailState(
    val banner: BannerSlide? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class KampanyaDetailViewModel(
    application: Application,
    private val slideId: Int
) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application.applicationContext)
    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow(KampanyaDetailState())
    val state: StateFlow<KampanyaDetailState> = _state.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            val apiKey = prefs.getApiKey() ?: run {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "API Anahtarı bulunamadı"
                )
                return@launch
            }
            runCatching {
                val resp = api.fetchBannerSlide(apiKey, slideId)
                if (resp.code() == 404) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Kampanya bulunamadı veya artık aktif değil."
                    )
                    return@launch
                }
                val banner = resp.body()?.data
                _state.value = _state.value.copy(
                    banner = banner,
                    isLoading = false,
                    errorMessage = if (banner == null) "Veri alınamadı" else null
                )
            }.onFailure {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = it.message ?: "Yüklenemedi"
                )
            }
        }
    }
}
