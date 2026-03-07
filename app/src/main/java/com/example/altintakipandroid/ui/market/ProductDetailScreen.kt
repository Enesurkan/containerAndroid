package com.example.altintakipandroid.ui.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.ui.theme.AccentOrange
import com.example.altintakipandroid.ui.theme.Danger
import com.example.altintakipandroid.ui.theme.SurfaceCream

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
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
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Geri")
                }
                ThemedText(
                    text = "Ürün Detayı",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentOrange)
                    }
                }
                state.errorMessage != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            ThemedText(text = state.errorMessage!!, color = Danger)
                            Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
                                ThemedText(text = "Kapat", color = SurfaceCream)
                            }
                        }
                    }
                }
                state.product != null -> {
                    val product = state.product!!
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
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
                        product.price?.let { price ->
                            ThemedText(
                                text = "%.2f ₺".format(price),
                                style = MaterialTheme.typography.titleLarge,
                                color = AccentOrange,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                        product.linkUrl?.takeIf { it.isNotBlank() }?.let { link ->
                            Button(
                                onClick = { uriHandler.openUri(link) },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                ThemedText(text = "Ürün linki", color = SurfaceCream)
                                Icon(
                                    Icons.AutoMirrored.Outlined.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        Button(
                            onClick = {
                                viewModel.generateShareUrl { url ->
                                    url?.let { u ->
                                        val sendIntent = android.content.Intent().apply {
                                            action = android.content.Intent.ACTION_SEND
                                            putExtra(android.content.Intent.EXTRA_TEXT, u)
                                            type = "text/plain"
                                        }
                                        context.startActivity(android.content.Intent.createChooser(sendIntent, "Paylaş"))
                                    }
                                }
                            },
                            modifier = Modifier.padding(top = 8.dp),
                            enabled = !state.isShareLoading
                        ) {
                            if (state.isShareLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = SurfaceCream
                                )
                            } else {
                                Icon(
                                    Icons.Outlined.Share,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                ThemedText(text = "Linki paylaş", color = SurfaceCream)
                            }
                        }
                    }
                }
            }
        }
    }
}
