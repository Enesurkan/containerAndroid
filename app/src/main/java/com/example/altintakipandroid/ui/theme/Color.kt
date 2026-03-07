package com.example.altintakipandroid.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * App theme color set (iOS AppTheme). One of five styles from ui-config theme (1-5).
 */
data class AppTheme(
    val brandName: String,
    val primaryColor: Color,
    val accentColor: Color,
    val surfaceColor: Color,
    val surfaceElevatedColor: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val separator: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
    val tabBarBackground: Color
) {
    fun toLightColorScheme(): ColorScheme = lightColorScheme(
        primary = primaryColor,
        onPrimary = Color.Black,
        primaryContainer = surfaceElevatedColor,
        onPrimaryContainer = textPrimary,
        secondary = accentColor,
        onSecondary = Color.Black,
        tertiary = accentColor,
        onTertiary = Color.Black,
        background = surfaceColor,
        onBackground = textPrimary,
        surface = surfaceColor,
        onSurface = textPrimary,
        surfaceVariant = surfaceElevatedColor,
        onSurfaceVariant = textSecondary,
        outline = separator,
        error = danger,
        onError = Color.White
    )
}

/** Style 1: Gold (Altın) - default */
private fun style1() = AppTheme(
    brandName = "Altın",
    primaryColor = Color(0xFFFFD700),
    accentColor = Color(0xFFFFA500),
    surfaceColor = Color(0xFFFFFEF5),
    surfaceElevatedColor = Color(0xFFFFF9E6),
    textPrimary = Color(0xFF1A1A1A),
    textSecondary = Color(0xFF666666),
    separator = Color(0xFFE0D4A0),
    success = Color(0xFF00C800),
    warning = Color(0xFFFFA500),
    danger = Color(0xFFFF4444),
    tabBarBackground = Color(0xFFFFFEF5)
)

/** Style 2: Blue (Mavi) */
private fun style2() = AppTheme(
    brandName = "Mavi",
    primaryColor = Color(0xFF0a7ea4),
    accentColor = Color(0xFF007AFF),
    surfaceColor = Color(0xFFF5F9FC),
    surfaceElevatedColor = Color(0xFFE8F4F8),
    textPrimary = Color(0xFF11181C),
    textSecondary = Color(0xFF687076),
    separator = Color(0xFFC0D4E0),
    success = Color(0xFF00C800),
    warning = Color(0xFFFFA500),
    danger = Color(0xFFFF4444),
    tabBarBackground = Color(0xFFF5F9FC)
)

/** Style 3: Premium Black/Gold */
private fun style3() = AppTheme(
    brandName = "Premium",
    primaryColor = Color(0xFFFFD700),
    accentColor = Color(0xFFFFA500),
    surfaceColor = Color(0xFF000000),
    surfaceElevatedColor = Color(0xFF111111),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFCCCCCC),
    separator = Color(0xFF333333),
    success = Color(0xFF00C800),
    warning = Color(0xFFFFA500),
    danger = Color(0xFFFF4444),
    tabBarBackground = Color(0xFF111111)
)

/** Style 4: Green (Yeşil) */
private fun style4() = AppTheme(
    brandName = "Yeşil",
    primaryColor = Color(0xFF2E7D32),
    accentColor = Color(0xFF4CAF50),
    surfaceColor = Color(0xFFF1F8F4),
    surfaceElevatedColor = Color(0xFFE8F5E9),
    textPrimary = Color(0xFF1A1A1A),
    textSecondary = Color(0xFF666666),
    separator = Color(0xFFB0C4B0),
    success = Color(0xFF4CAF50),
    warning = Color(0xFFFFA500),
    danger = Color(0xFFFF4444),
    tabBarBackground = Color(0xFFF1F8F4)
)

/** Style 5: Gray (Gri) */
private fun style5() = AppTheme(
    brandName = "Gri",
    primaryColor = Color(0xFF6B7280),
    accentColor = Color(0xFF374151),
    surfaceColor = Color(0xFFF3F4F6),
    surfaceElevatedColor = Color(0xFFE5E7EB),
    textPrimary = Color(0xFF1F2937),
    textSecondary = Color(0xFF6B7280),
    separator = Color(0xFFD1D5DB),
    success = Color(0xFF10B981),
    warning = Color(0xFFF59E0B),
    danger = Color(0xFFEF4444),
    tabBarBackground = Color(0xFFF3F4F6)
)

/**
 * Returns AppTheme for ui-config theme value (1-5). Defaults to style1 if out of range.
 */
fun getAppTheme(themeStyle: Int): AppTheme = when (themeStyle) {
    1 -> style1()
    2 -> style2()
    3 -> style3()
    4 -> style4()
    5 -> style5()
    else -> style1()
}

// Legacy aliases (style1) for compatibility during migration
val PrimaryGold = Color(0xFFFFD700)
val AccentOrange = Color(0xFFFFA500)
val SurfaceCream = Color(0xFFFFFEF5)
val SurfaceElevated = Color(0xFFFFF9E6)
val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF666666)
val Separator = Color(0xFFE0D4A0)
val Success = Color(0xFF00C800)
val Warning = Color(0xFFFFA500)
val Danger = Color(0xFFFF4444)
val TabBarBackground = Color(0xFFFFFEF5)

// Legacy Material defaults (keep for compatibility if needed)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
