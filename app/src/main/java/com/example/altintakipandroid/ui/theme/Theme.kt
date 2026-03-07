package com.example.altintakipandroid.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/** CompositionLocal for current AppTheme (iOS ThemeManager.theme). */
val LocalAppTheme = staticCompositionLocalOf { getAppTheme(1) }

/**
 * App theme driven by ui-config theme (1-5). Same color sets as iOS ThemeManager.AppTheme.
 */
@Composable
fun AltintakipAndroidTheme(
    themeStyle: Int = 1,
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val appTheme = getAppTheme(themeStyle)
    val colorScheme = appTheme.toLightColorScheme()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.primary.toArgb()
            // Style 3 = dark surface → light status bar content
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = (themeStyle != 3)
        }
    }

    CompositionLocalProvider(LocalAppTheme provides appTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
