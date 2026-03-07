package com.example.altintakipandroid.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.altintakipandroid.data.api.RetrofitClient
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.domain.PortalLoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PortalLoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val redirectUrl: String? = null
)

class PortalLoginViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application.applicationContext)
    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow(PortalLoginState())
    val state: StateFlow<PortalLoginState> = _state.asStateFlow()

    fun setUsername(s: String) {
        _state.value = _state.value.copy(username = s, errorMessage = null)
    }

    fun setPassword(s: String) {
        _state.value = _state.value.copy(password = s, errorMessage = null)
    }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, successMessage = null, redirectUrl = null)
    }

    fun login() {
        val username = _state.value.username.trim()
        val password = _state.value.password
        if (username.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "Kullanıcı adı ve şifre gerekli")
            return
        }
        viewModelScope.launch {
            val apiKey = prefs.getApiKey() ?: run {
                _state.value = _state.value.copy(errorMessage = "API anahtarı yok", isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            runCatching {
                val resp = api.portalLogin(apiKey, body = PortalLoginRequest(username = username, password = password))
                val body = resp.body()
                if (!resp.isSuccessful) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = body?.message ?: "Giriş başarısız (${resp.code()})"
                    )
                    return@launch
                }
                val code = body?.statusCode ?: resp.code()
                if (code in 200..299) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = body?.message ?: "Giriş başarılı",
                        redirectUrl = body?.data?.redirectUrl
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = body?.message ?: "Giriş başarısız"
                    )
                }
            }.onFailure {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = it.message ?: "Bağlantı hatası"
                )
            }
        }
    }
}
