package com.example.altintakipandroid.ui.market

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.altintakipandroid.data.api.RetrofitClient
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.domain.ProductOut
import com.example.altintakipandroid.domain.GenerateShareUrlRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductDetailState(
    val product: ProductOut? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val shareUrl: String? = null,
    val isShareLoading: Boolean = false
)

class ProductDetailViewModel(
    application: Application,
    private val productId: Int
) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application.applicationContext)
    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow(ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state.asStateFlow()

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
                val resp = api.fetchProductById(apiKey, productId)
                if (resp.code() == 404) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Ürün bulunamadı."
                    )
                    return@launch
                }
                val product = resp.body()?.data
                _state.value = _state.value.copy(
                    product = product,
                    isLoading = false,
                    errorMessage = if (product == null) "Veri alınamadı" else null
                )
            }.onFailure {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = it.message ?: "Yüklenemedi"
                )
            }
        }
    }

    fun generateShareUrl(onResult: (String?) -> Unit) {
        val product = _state.value.product ?: run {
            onResult(null)
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isShareLoading = true)
            runCatching {
                val apiKey = prefs.getApiKey() ?: return@launch
                val resp = api.generateShareUrl(apiKey, body = GenerateShareUrlRequest(product.id))
                val url = resp.body()?.data?.url
                _state.value = _state.value.copy(isShareLoading = false, shareUrl = url)
                onResult(url)
            }.onFailure {
                _state.value = _state.value.copy(isShareLoading = false)
                onResult(null)
            }
        }
    }
}
