package com.example.altintakipandroid.ui.market

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.altintakipandroid.data.api.RetrofitClient
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.domain.ProductOut
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductListState(
    val products: List<ProductOut> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class ProductListViewModel(
    application: Application,
    private val categoryId: Int,
    private val subcategoryId: Int?
) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application.applicationContext)
    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow(ProductListState())
    val state: StateFlow<ProductListState> = _state.asStateFlow()

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            val apiKey = prefs.getApiKey() ?: run {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "API Anahtarı bulunamadı"
                )
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                val resp = api.fetchProducts(
                    apiKey = apiKey,
                    categoryId = categoryId,
                    subcategoryId = subcategoryId
                )
                val list = resp.body()?.data ?: emptyList()
                _state.value = _state.value.copy(
                    products = list,
                    isLoading = false
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
