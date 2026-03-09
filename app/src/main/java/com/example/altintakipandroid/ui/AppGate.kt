package com.example.altintakipandroid.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.altintakipandroid.ui.activation.ActivationFormScreen
import com.example.altintakipandroid.ui.activation.QrScanActivity
import com.example.altintakipandroid.ui.main.MainTabScreen
import com.example.altintakipandroid.ui.onboarding.OnboardingScreen
import com.example.altintakipandroid.ui.splash.ConfigLoadErrorContent
import com.example.altintakipandroid.ui.splash.SplashScreen

/**
 * Root gate (iOS ActivationGate): Splash / Onboarding / Activation / Main.
 */
@Composable
fun AppGate(
    viewModel: AppGateViewModel
) {
    val state by viewModel.state.collectAsState()

    val showSplash = state.isChecking ||
            state.isInitialLoading ||
            (state.isActivated && !state.isDataReady && state.isInitialLoading)

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isActivated && state.isDataReady -> {
                MainTabScreen(
                    config = state.config,
                    appInfo = state.appInfo,
                    onLogout = { viewModel.deactivate() }
                )
            }
            state.isActivated && !state.isDataReady && !state.isInitialLoading -> {
                ConfigLoadErrorContent(
                    message = state.errorMessage ?: "Veri yüklenemedi",
                    onRetry = { viewModel.retryLoadInitialData() }
                )
            }
            !state.isChecking && !state.isInitialLoading -> {
                if (state.showOnboarding) {
                    OnboardingScreen(
                        onComplete = {
                            viewModel.completeOnboarding()
                        },
                        onSkip = viewModel::completeOnboarding
                    )
                } else {
                    val context = LocalContext.current
                    val qrLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        if (result.resultCode == android.app.Activity.RESULT_OK) {
                            result.data?.getStringExtra(QrScanActivity.EXTRA_SCAN_RESULT)?.let { str ->
                                viewModel.setClientName(str)
                                viewModel.activate()
                            }
                        }
                    }
                    ActivationFormScreen(
                        clientName = state.clientName,
                        onClientNameChange = viewModel::setClientName,
                        isLoading = state.isLoading,
                        errorMessage = state.errorMessage,
                        onActivate = viewModel::activate,
                        onSkip = viewModel::skip,
                        onScanQr = { qrLauncher.launch(Intent(context, QrScanActivity::class.java)) }
                    )
                }
            }
        }

        if (showSplash) {
            SplashScreen(appInfo = state.appInfo)
        }
    }
}
