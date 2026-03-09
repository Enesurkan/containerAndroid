package com.example.altintakipandroid.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.altintakipandroid.R
import com.example.altintakipandroid.domain.AppInformationData
import com.example.altintakipandroid.ui.components.ThemedText

/**
 * Splash screen (iOS SplashView): surface background, logo only (no text).
 * Uses appInfo.splashLogo when present (activated customer), else default drawable.
 */
@Composable
fun SplashScreen(
    appInfo: AppInformationData?
) {
    val logoUrl = appInfo?.splashLogo?.takeIf { it.isNotBlank() } ?: appInfo?.navigationIcon?.takeIf { it.isNotBlank() }
    // Default icon has clear/black background; use black splash background so it blends
    val splashBackground = if (logoUrl != null) MaterialTheme.colorScheme.surface else Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(splashBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        if (logoUrl != null) {
            AsyncImage(
                model = logoUrl,
                contentDescription = null,
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Image(
                painter = painterResource(R.drawable.ic_splash_default),
                contentDescription = null,
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.secondary,
            strokeWidth = 3.dp
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

/**
 * Shown when config load failed or timed out (isActivated && !isDataReady && !isInitialLoading).
 * Avoids opening MainTab with default config (e.g. mobileUseWebSocket = false).
 */
@Composable
fun ConfigLoadErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        ThemedText(
            text = message,
            isSecondary = true,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            ThemedText(text = "Tekrar dene", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
