package com.example.altintakipandroid.ui.favorites

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

data class FavoritesState(
    val favoriteRates: List<ExchangeRate> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application.applicationContext)
    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    init {
        viewModelScope.launch { loadFavorites() }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            val apiKey = prefs.getApiKey() ?: run {
                _state.value = _state.value.copy(isLoading = false)
                return@launch
            }
            val ids = prefs.getUserFavorites()
            if (ids.isEmpty()) {
                _state.value = _state.value.copy(
                    favoriteRates = emptyList(),
                    favoriteIds = emptySet(),
                    isLoading = false,
                    errorMessage = null
                )
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
                val allRates = resp.body()?.data ?: emptyList()
                val idSet = ids.toSet()
                val favorites = allRates.filter { it.apiId != null && it.apiId in idSet }
                    .sortedBy { idSet.indexOf(it.apiId) }
                _state.value = _state.value.copy(
                    favoriteRates = favorites,
                    favoriteIds = idSet,
                    isLoading = false,
                    errorMessage = null
                )
            }.onFailure {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = it.message ?: "Bağlantı hatası"
                )
            }
        }
    }

    fun removeFavorite(rate: ExchangeRate) {
        val id = rate.apiId ?: return
        viewModelScope.launch {
            val ids = prefs.getUserFavorites().filter { it != id }
            prefs.saveUserFavorites(ids)
            _state.value = _state.value.copy(
                favoriteIds = ids.toSet(),
                favoriteRates = _state.value.favoriteRates.filter { it.apiId != id }
            )
        }
    }

    fun isFavorite(apiId: Int?): Boolean = apiId != null && apiId in _state.value.favoriteIds

    fun addFavorite(apiId: Int) {
        viewModelScope.launch {
            val ids = prefs.getUserFavorites()
            if (apiId in ids) return@launch
            prefs.saveUserFavorites(ids + apiId)
            _state.value = _state.value.copy(favoriteIds = _state.value.favoriteIds + apiId)
        }
    }

    fun removeFavoriteById(apiId: Int) {
        viewModelScope.launch {
            val ids = prefs.getUserFavorites().filter { it != apiId }
            prefs.saveUserFavorites(ids)
            _state.value = _state.value.copy(
                favoriteIds = ids.toSet(),
                favoriteRates = _state.value.favoriteRates.filter { it.apiId != apiId }
            )
        }
    }
}
