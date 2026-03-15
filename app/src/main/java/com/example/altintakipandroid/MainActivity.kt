package com.example.altintakipandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.altintakipandroid.data.network.NetworkMonitor
import com.example.altintakipandroid.data.security.SecurityChecker
import com.example.altintakipandroid.ui.components.ConnectionStatusOverlay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.altintakipandroid.data.push.PUSH_EXTRA_CURRENCY_CODE
import com.example.altintakipandroid.data.push.PUSH_EXTRA_DEEPLINK
import com.example.altintakipandroid.data.push.PushDeepLinkHolder
import com.example.altintakipandroid.ui.AppGate
import com.example.altintakipandroid.ui.AppGateViewModel
import com.example.altintakipandroid.ui.theme.AltintakipAndroidTheme

import androidx.activity.SystemBarStyle
import android.graphics.Color as AndroidColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Release'de root veya debugger tespit edilirse uygulamayı kapat (reverse eng. koruması)
        if (!BuildConfig.DEBUG && SecurityChecker.isUnsafeEnvironment()) {
            AlertDialog.Builder(this)
                .setTitle("Güvenlik Uyarısı")
                .setMessage(SecurityChecker.getUnsafeMessage())
                .setCancelable(false)
                .setPositiveButton("Tamam") { _, _ -> finish() }
                .show()
            return
        }

        NetworkMonitor.init(applicationContext)
        applyPushIntent(intent)

        setContent {
            val viewModel: AppGateViewModel = viewModel()
            val state by viewModel.state.collectAsState()
            val themeStyle = state.config.theme

            // Dinamik edge-to-edge ayarı: Tema 3 (Koyu) ise koyu arka plana açık ikonlar,
            // diğerlerinde açık arka plana koyu ikonlar.
            LaunchedEffect(themeStyle) {
                if (themeStyle == 3) {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
                        navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
                    )
                } else {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
                        navigationBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
                    )
                }
            }

            AltintakipAndroidTheme(themeStyle = themeStyle) {
                val isConnected by NetworkMonitor.isConnected.collectAsState(initial = true)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(Modifier.fillMaxSize()) {
                        AppGate(viewModel = viewModel)
                        ConnectionStatusOverlay(isConnected = isConnected)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        applyPushIntent(intent)
    }

    private fun applyPushIntent(intent: Intent?) {
        val d = intent?.getStringExtra(PUSH_EXTRA_DEEPLINK)
            ?: intent?.data?.toString()?.takeIf { it.startsWith("dienu://") }
        val c = intent?.getStringExtra(PUSH_EXTRA_CURRENCY_CODE)
        if (!d.isNullOrBlank() || !c.isNullOrBlank()) {
            PushDeepLinkHolder.set(d, c)
        }
    }
}
