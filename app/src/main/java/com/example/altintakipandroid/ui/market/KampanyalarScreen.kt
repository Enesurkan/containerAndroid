package com.example.altintakipandroid.ui.market

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.altintakipandroid.domain.BannerSlide
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView

@Composable
fun KampanyalarScreen(
    banners: List<BannerSlide>,
    onBack: () -> Unit,
    onCampaignClick: (Int) -> Unit
) {
    ThemedView {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Geri"
                    )
                }
                ThemedText(
                    text = "Kampanyalar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp, 24.dp)
            ) {
                items(banners) { banner ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onCampaignClick(banner.id) }
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = banner.imageUrl,
                            contentDescription = banner.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                        if (!banner.title.isNullOrBlank()) {
                            ThemedText(
                                text = banner.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
