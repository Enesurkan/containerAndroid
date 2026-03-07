package com.example.altintakipandroid.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

/**
 * Text with theme colors (iOS ThemedText).
 * isSecondary = textSecondary, otherwise textPrimary.
 */
@Composable
fun ThemedText(
    text: String,
    modifier: Modifier = Modifier,
    isSecondary: Boolean = false,
    color: Color? = null,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign? = null
) {
    val resolvedColor = color ?: if (isSecondary) MaterialTheme.colorScheme.onSurfaceVariant
    else MaterialTheme.colorScheme.onSurface
    Text(
        text = text,
        modifier = modifier,
        color = resolvedColor,
        style = style,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}
