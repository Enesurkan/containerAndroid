package com.example.altintakipandroid.ui.market

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProductImageZoomRequest(
    val imageUrls: List<String>,
    val initialIndex: Int,
    val contentDescription: String?,
    val onDismiss: () -> Unit
)

/**
 * Holder for showing product image zoom at root level (MainTabScreen)
 * so the overlay covers tab bar and back button (full-screen black).
 */
object ProductImageZoomHolder {
    private val _request = MutableStateFlow<ProductImageZoomRequest?>(null)
    val request: StateFlow<ProductImageZoomRequest?> = _request.asStateFlow()

    fun show(
        imageUrls: List<String>,
        initialIndex: Int,
        contentDescription: String?,
        onDismiss: () -> Unit
    ) {
        _request.value = ProductImageZoomRequest(
            imageUrls = imageUrls,
            initialIndex = initialIndex,
            contentDescription = contentDescription,
            onDismiss = onDismiss
        )
    }

    fun dismiss() {
        _request.value?.onDismiss?.invoke()
        _request.value = null
    }
}
