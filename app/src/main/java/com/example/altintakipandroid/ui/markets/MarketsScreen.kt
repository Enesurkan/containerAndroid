package com.example.altintakipandroid.ui.markets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.altintakipandroid.domain.ExchangeRate
import com.example.altintakipandroid.ui.components.CustomHeader
import com.example.altintakipandroid.ui.favorites.FavoritesState
import com.example.altintakipandroid.ui.favorites.FavoritesViewModel
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.ui.theme.Danger
import com.example.altintakipandroid.ui.theme.Success
import com.example.altintakipandroid.ui.theme.SurfaceElevated

@Composable
fun MarketsScreen(
    viewModel: MarketsViewModel,
    favoritesViewModel: FavoritesViewModel? = null
) {
    val state by viewModel.state.collectAsState()
    val defaultFavState = remember { kotlinx.coroutines.flow.MutableStateFlow(FavoritesState()) }
    val favState by (favoritesViewModel?.state ?: defaultFavState).collectAsState(initial = FavoritesState())

    ThemedView {
        Column(modifier = Modifier.fillMaxSize()) {
            CustomHeader(
                title = if (state.wsConnected) "Piyasalar • Canlı" else "Piyasalar",
                trailingContent = {
                    IconButton(onClick = { viewModel.loadRates() }) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Yenile",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
            when {
                state.isLoading && state.rates.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                state.errorMessage != null && state.rates.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            ThemedText(
                                text = state.errorMessage!!,
                                isSecondary = true
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TextButton(onClick = { viewModel.loadRates() }) {
                                ThemedText(text = "Tekrar dene")
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                    ) {
                        if (state.isLoading) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.padding(8.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        items(state.rates) { rate ->
                            RateCard(
                                rate = rate,
                                trailingContent = if (favoritesViewModel != null && rate.apiId != null) {
                                    {
                                        val isFav = rate.apiId in favState.favoriteIds
                                        IconButton(
                                            onClick = {
                                                if (isFav) favoritesViewModel.removeFavoriteById(rate.apiId!!)
                                                else favoritesViewModel.addFavorite(rate.apiId!!)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Star,
                                                contentDescription = if (isFav) "Favorilerden çıkar" else "Favorilere ekle",
                                                tint = if (isFav) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                } else null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RateCard(
    rate: ExchangeRate,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val buy = rate.buy ?: 0.0
    val sell = rate.sell ?: 0.0
    val change = rate.changeRate ?: 0.0
    val isPositive = change >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                ThemedText(
                    text = rate.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                if (rate.description != null && rate.description != rate.showableText) {
                    ThemedText(
                        text = rate.description,
                        style = MaterialTheme.typography.bodySmall,
                        isSecondary = true
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                ThemedText(
                    text = "Alış: %.2f".format(buy),
                    style = MaterialTheme.typography.bodyMedium
                )
                ThemedText(
                    text = "Satış: %.2f".format(sell),
                    style = MaterialTheme.typography.bodyMedium
                )
                if (change != 0.0) {
                    ThemedText(
                        text = "%s%.2f%%".format(if (isPositive) "+" else "", change),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isPositive) Success else Danger
                    )
                }
            }
            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}
