package com.example.altintakipandroid.ui.markets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.TextButton
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.altintakipandroid.domain.ExchangeRate
import com.example.altintakipandroid.domain.AppInformationData
import com.example.altintakipandroid.domain.UIConfig
import com.example.altintakipandroid.ui.components.CustomHeader
import com.example.altintakipandroid.ui.main.getNavigationConfig
import com.example.altintakipandroid.ui.favorites.FavoritesState
import com.example.altintakipandroid.ui.favorites.FavoritesViewModel
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.ui.main.ListLayoutType
import com.example.altintakipandroid.ui.main.ListStyleConfig
import com.example.altintakipandroid.ui.main.getListConfig
import com.example.altintakipandroid.ui.theme.LocalAppTheme
import com.example.altintakipandroid.ui.util.formatPriceForDisplay
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.ExperimentalMaterial3Api

/** Section header row (iOS MarketListHeader). Shown when listConfig.showSectionHeader; "Birim" for listStyle 2, "Varlık" otherwise. */
@Composable
fun MarketListHeader(
    listConfig: ListStyleConfig,
    listStyle: Int,
    reserveTrailingSpaceForFavorites: Boolean = false
) {
    val appTheme = LocalAppTheme.current
    val labelFirst = if (listStyle == 2) "Birim" else "Varlık"
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(appTheme.surfaceElevatedColor)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = listConfig.marginHorizontalDp + listConfig.paddingHorizontalDp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemedText(
                text = labelFirst,
                isSecondary = true,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium
            )
            ThemedText(
                text = "Alış",
                isSecondary = true,
                modifier = Modifier.width(listConfig.priceColWidthDp).padding(end = 8.dp),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.End
            )
            ThemedText(
                text = "Satış",
                isSecondary = true,
                modifier = Modifier.width(listConfig.priceColWidthDp),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (reserveTrailingSpaceForFavorites) {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketsScreen(
    config: UIConfig,
    appInfo: AppInformationData,
    viewModel: MarketsViewModel,
    favoritesViewModel: FavoritesViewModel? = null
) {
    val state by viewModel.state.collectAsState()
    val defaultFavState = remember { kotlinx.coroutines.flow.MutableStateFlow(FavoritesState()) }
    val favState by (favoritesViewModel?.state ?: defaultFavState).collectAsState(initial = FavoritesState())
    val navConfig = remember(config.navigationStyle) { getNavigationConfig(config.navigationStyle) }
    val listConfig = remember(config.listStyle) { getListConfig(config.listStyle) }

    ThemedView {
        Column(modifier = Modifier.fillMaxSize()) {
            CustomHeader(
                title = if (state.wsConnected) "Piyasalar • Canlı" else "Piyasalar",
                navigationStyle = config.navigationStyle,
                navConfig = navConfig,
                appInfo = appInfo
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
                            TextButton(onClick = { viewModel.loadRates(force = true) }) {
                                ThemedText(text = "Tekrar dene")
                            }
                        }
                    }
                }
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (listConfig.showSectionHeader) {
                            MarketListHeader(
                                listConfig = listConfig,
                                listStyle = config.listStyle,
                                reserveTrailingSpaceForFavorites = config.isFavoriteEnabled
                            )
                        }
                        PullToRefreshBox(
                            isRefreshing = state.isLoading && state.rates.isNotEmpty(),
                            onRefresh = { viewModel.loadRates(force = true) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = listConfig.marginHorizontalDp,
                                    end = listConfig.marginHorizontalDp,
                                    top = listConfig.marginVerticalDp,
                                    bottom = listConfig.marginVerticalDp
                                ),
                                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(listConfig.marginVerticalDp)
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
                        itemsIndexed(
                            state.rates,
                            key = { index, r -> "rate_${index}_${r.apiId}_${r.currencyCode}" }
                        ) { _, rate ->
                            val isFav = rate.apiId != null && rate.apiId in favState.favoriteIds
                            val showFavoriteAction = favoritesViewModel != null && rate.apiId != null
                            MarketRateRow(
                                rate = rate,
                                listConfig = listConfig,
                                changeRateEnabled = config.changeRateEnabled,
                                marketFontSize = config.marketFontSize,
                                marketFontWeight = config.marketFontWeight,
                                marketFontFamily = config.marketFontFamily,
                                trailingContent = if (showFavoriteAction) {
                                    {
                                        IconButton(
                                            onClick = {
                                                if (isFav) favoritesViewModel?.removeFavoriteById(rate.apiId!!)
                                                else rate.apiId?.let { favoritesViewModel?.addFavorite(it) }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                                contentDescription = if (isFav) "Favoriden çıkar" else "Favori ekle",
                                                modifier = Modifier.size(24.dp),
                                                tint = if (isFav) Color(0xFFF97316) else MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                } else null,
                                isFavorite = isFav
                            )
                        }
                        }
                    }
            }
        }
            }
        }
    }
}
