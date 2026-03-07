package com.example.altintakipandroid.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.altintakipandroid.ui.components.CustomHeader
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.domain.AppInformationData
import com.example.altintakipandroid.domain.UIConfig
import com.example.altintakipandroid.ui.main.getListConfig
import com.example.altintakipandroid.ui.main.getNavigationConfig
import com.example.altintakipandroid.ui.markets.MarketRateRow

@Composable
fun FavoritesScreen(
    config: UIConfig,
    appInfo: AppInformationData,
    viewModel: FavoritesViewModel
) {
    val state by viewModel.state.collectAsState()
    val navConfig = remember(config.navigationStyle) { getNavigationConfig(config.navigationStyle) }
    val listConfig = remember(config.listStyle) { getListConfig(config.listStyle) }

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    ThemedView {
        Column(modifier = Modifier.fillMaxSize()) {
            CustomHeader(
                title = "Favoriler",
                navigationStyle = config.navigationStyle,
                navConfig = navConfig,
                appInfo = appInfo
            )
            when {
                state.isLoading && state.favoriteRates.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ThemedText(text = "Yükleniyor...", isSecondary = true)
                    }
                }
                state.favoriteRates.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                modifier = Modifier.padding(16.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            ThemedText(
                                text = "Henüz favori kur eklemediniz",
                                isSecondary = true
                            )
                            ThemedText(
                                text = "Piyasalar sekmesinden yıldıza basarak ekleyebilirsiniz",
                                style = MaterialTheme.typography.bodySmall,
                                isSecondary = true,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.favoriteRates) { rate ->
                            MarketRateRow(
                                rate = rate,
                                listConfig = listConfig,
                                changeRateEnabled = config.changeRateEnabled,
                                marketFontSize = config.marketFontSize,
                                marketFontWeight = config.marketFontWeight,
                                marketFontFamily = config.marketFontFamily,
                                trailingContent = {
                                    IconButton(onClick = { viewModel.removeFavorite(rate) }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Star,
                                            contentDescription = "Favorilerden çıkar",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
