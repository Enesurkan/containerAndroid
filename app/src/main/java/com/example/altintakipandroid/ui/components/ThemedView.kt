package com.example.altintakipandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.altintakipandroid.ui.theme.SurfaceCream

/**
 * Full-screen background with theme surface color (iOS ThemedView).
 */
@Composable
fun ThemedView(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCream)
    ) {
        content()
    }
}
