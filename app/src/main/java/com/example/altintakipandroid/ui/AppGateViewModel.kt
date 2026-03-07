package com.example.altintakipandroid.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.altintakipandroid.data.api.RetrofitClient
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.domain.AppInformationData
import com.example.altintakipandroid.domain.UIConfig
import com.example.altintakipandroid.domain.PushRegisterRequest
import com.example.altintakipandroid.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Gate state (iOS ActivationGate + ThemeManager minimal state).
 */
data class AppGateState(
    val isChecking: Boolean = true,
    val isInitialLoading: Boolean = true,
    val isDataReady: Boolean = false,
    val isActivated: Boolean = false,
    val showOnboarding: Boolean = false,
    val clientName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val config: UIConfig = UIConfig.default,
    val appInfo: AppInformationData = AppInformationData.default
)

class AppGateViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application.applicationContext)
    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow(AppGateState())
    val state: StateFlow<AppGateState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { checkInitialActivation() }.onFailure {
                Log.e(TAG, "AppGateVM checkInitialActivation failed", it)
                _state.value = _state.value.copy(
                    isChecking = false,
                    isInitialLoading = false,
                    showOnboarding = true
                )
            }
        }
    }

    private suspend fun checkInitialActivation() {
        runCatching {
            val apiKey = prefs.getApiKey()
            Log.d(TAG, "AppGateVM checkInitialActivation: hasApiKey=${!apiKey.isNullOrBlank()}")
            if (apiKey != null && apiKey.isNotBlank()) {
                _state.value = _state.value.copy(isActivated = true, isChecking = false)
                loadInitialData(apiKey)
                return
            }
            val onboardingDone = prefs.isOnboardingDone()
            _state.value = _state.value.copy(
                isChecking = false,
                isInitialLoading = false,
                showOnboarding = !onboardingDone
            )
        }.onFailure {
            _state.value = _state.value.copy(
                isChecking = false,
                isInitialLoading = false,
                showOnboarding = true
            )
        }
    }

    fun loadInitialData(apiKey: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isInitialLoading = true)
            val result = withTimeoutOrNull(25_000L) {
                runCatching {
                    // 1) UI Config
                    val t0 = System.currentTimeMillis()
                    Log.d(TAG, "AppGateVM loadInitialData: calling GET /api/v1/ui-config")
                    val configResp = api.fetchUIConfig(apiKey)
                    Log.d(TAG, "AppGateVM loadInitialData: GET /api/v1/ui-config done in ${System.currentTimeMillis() - t0}ms code=${configResp.code()}")
                    val config = configResp.body()?.getConfig() ?: UIConfig.default

                    // 2) App Information
                    val t1 = System.currentTimeMillis()
                    Log.d(TAG, "AppGateVM loadInitialData: calling GET /client-apps/app-information")
                    val appResp = api.fetchAppInformation(apiKey)
                    Log.d(TAG, "AppGateVM loadInitialData: GET /client-apps/app-information done in ${System.currentTimeMillis() - t1}ms code=${appResp.code()}")

                    val appInfo = appResp.body()?.getDataOrDefault() ?: AppInformationData.default
                    _state.value = _state.value.copy(
                        config = config,
                        appInfo = appInfo,
                        isInitialLoading = false,
                        isDataReady = true
                    )
                    if (config.pushEnabled == true) {
                        viewModelScope.launch {
                            runCatching {
                                val token = com.example.altintakipandroid.data.push.getFcmToken()
                                api.pushRegister(apiKey, body = PushRegisterRequest(token))
                            }
                        }
                    }
                }.onFailure {
                    Log.e(TAG, "AppGateVM loadInitialData: request failed", it)
                    _state.value = _state.value.copy(
                        isInitialLoading = false,
                        isDataReady = true
                    )
                }
            }
            // Timeout: don't stay on splash forever; continue with default config
            if (result == null) {
                Log.w(TAG, "AppGateVM loadInitialData: timeout after 25s (no response from ui-config or app-information)")
                _state.value = _state.value.copy(
                    isInitialLoading = false,
                    isDataReady = true
                )
            }
        }
    }

    companion object {
        private const val TAG = "AppGateVM"
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            runCatching { prefs.setOnboardingDone(true) }
            _state.value = _state.value.copy(showOnboarding = false)
        }
    }

    fun setClientName(name: String) {
        _state.value = _state.value.copy(clientName = name, errorMessage = null)
    }

    fun activate() {
        val clientName = _state.value.clientName.trim()
        if (clientName.isBlank()) return
        activateWithClientName(clientName)
    }

    /** Skip: use default API key from secret (iOS skipActivation). Key from secret.properties → BuildConfig.DEFAULT_API_KEY. */
    fun skip() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            val defaultKey = BuildConfig.DEFAULT_API_KEY
            if (defaultKey.isNotBlank()) {
                runCatching {
                    prefs.saveApiKey(defaultKey)
                    loadInitialData(defaultKey)
                    _state.value = _state.value.copy(isActivated = true, isLoading = false, errorMessage = null)
                }.onFailure {
                    _state.value = _state.value.copy(isLoading = false, errorMessage = it.message ?: "Ağ hatası.")
                }
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Geç için secret.properties içinde DEFAULT_API_KEY tanımlayın."
                )
            }
        }
    }

    private fun activateWithClientName(clientName: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                val resp = api.activate(clientName)
                if (!resp.isSuccessful) {
                    val body = resp.errorBody()?.string()
                    val msg = when (resp.code()) {
                        404 -> "Kuyumcu bulunamadı. Lütfen geçerli bir QR kod kullanın."
                        403 -> "Bu kuyumcu aktif değil."
                        else -> body ?: "Aktivasyon başarısız oldu (Hata: ${resp.code()})"
                    }
                    _state.value = _state.value.copy(isLoading = false, errorMessage = msg)
                    return@launch
                }
                val apiKey = resp.body()?.data?.apiKey ?: run {
                    _state.value = _state.value.copy(isLoading = false, errorMessage = "Sunucudan anahtar alınamadı.")
                    return@launch
                }
                prefs.saveApiKey(apiKey)
                loadInitialData(apiKey)
                _state.value = _state.value.copy(
                    isActivated = true,
                    isLoading = false,
                    errorMessage = null
                )
            }.onFailure {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = it.message ?: "Ağ hatası."
                )
            }
        }
    }

    fun onScanQr() {
        // QR scanning is handled in AppGate via ActivityResultLauncher; result sets clientName and calls activate().
    }

    /** Log out: unregister push, clear API key, return to activation screen. */
    fun deactivate() {
        viewModelScope.launch {
            runCatching {
                val apiKey = prefs.getApiKey()
                if (!apiKey.isNullOrBlank()) {
                    runCatching {
                        val token = com.example.altintakipandroid.data.push.getFcmToken()
                        api.pushUnregister(apiKey, body = PushRegisterRequest(token))
                    }
                }
                prefs.clearApiKey()
            }
            _state.value = AppGateState()
        }
    }
}
