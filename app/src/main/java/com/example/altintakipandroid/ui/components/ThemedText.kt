package com.example.altintakipandroid.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

/**
 * Text with theme colors (iOS ThemedText).
 * isSecondary = textSecondary, otherwise textPrimary.
 * When marketFontSize/marketFontWeight/marketFontFamily are set (from UIConfig), they override style.
 */
@Composable
fun ThemedText(
    text: String,
    modifier: Modifier = Modifier,
    isSecondary: Boolean = false,
    color: Color? = null,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign? = null,
    marketFontSize: Double? = null,
    marketFontWeight: String? = null,
    marketFontFamily: String? = null
) {
    val resolvedColor = color ?: if (isSecondary) MaterialTheme.colorScheme.onSurfaceVariant
    else MaterialTheme.colorScheme.onSurface
    var resolvedStyle = style
    if (marketFontSize != null) resolvedStyle = resolvedStyle.copy(fontSize = marketFontSize.toFloat().sp)
    if (!marketFontFamily.isNullOrBlank()) resolvedStyle = resolvedStyle.copy(fontFamily = parseMarketFontFamily(marketFontFamily))
    val resolvedWeight = if (marketFontWeight != null) parseMarketFontWeight(marketFontWeight) else fontWeight
    Text(
        text = text,
        modifier = modifier,
        color = resolvedColor,
        style = resolvedStyle,
        fontWeight = resolvedWeight,
        textAlign = textAlign
    )
}

private fun parseMarketFontFamily(name: String): FontFamily {
    return when (name.trim().lowercase()) {
        "serif" -> FontFamily.Serif
        "monospace", "mono" -> FontFamily.Monospace
        "cursive" -> FontFamily.Cursive
        "roboto", "sans", "sans-serif", "default" -> FontFamily.SansSerif
        else -> FontFamily.SansSerif
    }
}

private fun parseMarketFontWeight(weight: String): FontWeight {
    val normalized = weight.trim().lowercase()
    return when (normalized) {
        "black", "900" -> FontWeight.Black
        "heavy", "800" -> FontWeight.ExtraBold
        "bold", "700" -> FontWeight.Bold
        "semibold", "600" -> FontWeight.SemiBold
        "medium", "500" -> FontWeight.Medium
        "regular", "normal", "400" -> FontWeight.Normal
        "light", "300" -> FontWeight.Light
        "thin", "200" -> FontWeight.Thin
        else -> FontWeight.Normal
    }
}
