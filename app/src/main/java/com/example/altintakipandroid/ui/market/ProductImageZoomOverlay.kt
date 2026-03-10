package com.example.altintakipandroid.ui.market

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductImageZoomOverlay(
    imageUrls: List<String>,
    initialIndex: Int,
    contentDescription: String?,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, (imageUrls.size - 1).coerceAtLeast(0)),
        pageCount = { imageUrls.size }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black)
    ) {
        if (imageUrls.size > 1) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true
            ) { page ->
                ZoomableImagePage(
                    imageUrl = imageUrls[page],
                    contentDescription = contentDescription
                )
            }
        } else {
            ZoomableImagePage(
                imageUrl = imageUrls.firstOrNull() ?: "",
                contentDescription = contentDescription
            )
        }
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(8.dp)
        ) {
            Icon(
                Icons.Outlined.Close,
                contentDescription = "Kapat",
                tint = androidx.compose.ui.graphics.Color.White
            )
        }
    }
}

@Composable
fun ZoomableImagePage(
    imageUrl: String,
    contentDescription: String?
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val maxScale = 5f
    val doubleTapScale = 2.5f

    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .pointerInput(imageUrl) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale > 1f) 1f else doubleTapScale
                        if (scale <= 1f) offset = Offset.Zero
                    }
                )
            }
            .pointerInput(imageUrl) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, maxScale)
                    if (scale > 1f) {
                        offset = Offset(
                            offset.x + pan.x,
                            offset.y + pan.y
                        )
                    } else {
                        offset = Offset.Zero
                    }
                }
            }
    )
}
