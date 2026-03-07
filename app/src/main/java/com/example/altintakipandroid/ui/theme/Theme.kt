package com.example.altintakipandroid.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * App theme using style1 (Gold) - aligned with iOS ThemeManager AppTheme.
 */
private val AltinTakipLightColorScheme = lightColorScheme(
    primary = PrimaryGold,
    onPrimary = Color.Black,
    primaryContainer = SurfaceElevated,
    onPrimaryContainer = TextPrimary,
    secondary = AccentOrange,
    onSecondary = Color.Black,
    tertiary = AccentOrange,
    onTertiary = Color.Black,
    background = SurfaceCream,
    onBackground = TextPrimary,
    surface = SurfaceCream,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = Separator,
    error = Danger,
    onError = Color.White
)

@Composable
fun AltintakipAndroidTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = AltinTakipLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
