package com.example.altintakipandroid.ui.market

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import coil.compose.AsyncImage
import com.example.altintakipandroid.domain.AppInformationData
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.ui.util.formatProductDetailPrice
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    appInfo: AppInformationData,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    val context = LocalContext.current

    ThemedView {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Geri")
                }
                ThemedText(
                    text = "Ürün Detayı",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (state.product != null) {
                    IconButton(
                        onClick = {
                            state.product?.let { p ->
                                viewModel.generateShareUrl { url ->
                                    url?.let { u ->
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, u)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Paylaş"))
                                    }
                                }
                            }
                        },
                        enabled = !state.isShareLoading
                    ) {
                        if (state.isShareLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(Icons.Outlined.Share, contentDescription = "Paylaş")
                        }
                    }
                }
            }
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                    }
                }
                state.errorMessage != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            ThemedText(text = state.errorMessage!!, color = MaterialTheme.colorScheme.error)
                            Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
                                ThemedText(text = "Kapat", color = MaterialTheme.colorScheme.surface)
                            }
                        }
                    }
                }
                state.product != null -> {
                    val product = state.product!!
                    val imageList = product.imageList
                    val pagerState = rememberPagerState(pageCount = { imageList.size })
                    Box(Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp)
                            ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        ProductImageZoomHolder.show(
                                            imageUrls = imageList,
                                            initialIndex = pagerState.currentPage,
                                            contentDescription = product.title,
                                            onDismiss = { }
                                        )
                                    }
                            ) {
                                if (imageList.size > 1) {
                                    HorizontalPager(
                                        state = pagerState,
                                        modifier = Modifier.fillMaxSize(),
                                        userScrollEnabled = true
                                    ) { page ->
                                        AsyncImage(
                                            model = imageList[page],
                                            contentDescription = product.title,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        val dotCount = minOf(5, imageList.size)
                                        val currentPage = pagerState.currentPage
                                        val activeDotIndex = if (imageList.size <= 5) currentPage
                                            else (currentPage * (dotCount - 1).coerceAtLeast(0) / (imageList.size - 1).coerceAtLeast(1)).coerceIn(0, dotCount - 1)
                                        repeat(dotCount) { index ->
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (index == activeDotIndex)
                                                            MaterialTheme.colorScheme.primary
                                                        else
                                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                                    )
                                            )
                                        }
                                    }
                                } else {
                                    AsyncImage(
                                        model = imageList.firstOrNull() ?: product.imageUrl,
                                        contentDescription = product.title,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        product.categoryPath?.let { path ->
                            if (path.isNotBlank()) {
                                ThemedText(
                                    text = path,
                                    isSecondary = true,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }
                        ThemedText(
                            text = product.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        product.description?.takeIf { it.isNotBlank() }?.let { desc ->
                            ThemedText(
                                text = desc,
                                isSecondary = true,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        product.linkUrl?.takeIf { it.isNotBlank() }?.let { link ->
                            Button(
                                onClick = { uriHandler.openUri(link) },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                ThemedText(text = "Ürün linki", color = MaterialTheme.colorScheme.surface)
                                Icon(
                                    Icons.AutoMirrored.Outlined.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                state.product?.price?.let { price ->
                                    ThemedText(
                                        text = formatProductDetailPrice(price),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Button(
                                    onClick = {
                                        val p = state.product ?: return@Button
                                        val phone = (appInfo.contactWhatsapp ?: appInfo.contactPhone).orEmpty()
                                            .replace(Regex("[^0-9]"), "")
                                        if (phone.isNotBlank()) {
                                            val productUrl = "https://altintakip.net/urunler/${p.id}"
                                            val text = "Merhaba, ${p.title} adlı ürün stoklarınızda mevcut mu?\nÜrün Linki: $productUrl"
                                            val encoded = URLEncoder.encode(text, StandardCharsets.UTF_8.name())
                                            val uri = Uri.parse("https://wa.me/$phone?text=$encoded")
                                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                        }
                                    },
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    ThemedText(text = "Stok Sor", color = MaterialTheme.colorScheme.surface)
                                }
                            }
                        }
                        }
                    }
                }
            }
        }
    }
}
