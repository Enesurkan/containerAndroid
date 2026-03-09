package com.example.altintakipandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    AppGate(viewModel = viewModel)
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
        val c = intent?.getStringExtra(PUSH_EXTRA_CURRENCY_CODE)
        if (!d.isNullOrBlank() || !c.isNullOrBlank()) {
            PushDeepLinkHolder.set(d, c)
        }
    }
}
