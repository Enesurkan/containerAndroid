package com.example.altintakipandroid.ui.markets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
                appInfo = appInfo,
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
                        items(state.rates) { rate ->
                            MarketRateRow(
                                rate = rate,
                                listConfig = listConfig,
                                changeRateEnabled = config.changeRateEnabled,
                                marketFontSize = config.marketFontSize,
                                marketFontWeight = config.marketFontWeight,
                                marketFontFamily = config.marketFontFamily,
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

/** Rate row driven by ui-config listStyle (iOS ExchangeRateRow). Respects changeRateEnabled. */
@Composable
fun MarketRateRow(
    rate: ExchangeRate,
    listConfig: ListStyleConfig,
    changeRateEnabled: Boolean,
    trailingContent: @Composable (() -> Unit)? = null,
    marketFontSize: Double? = null,
    marketFontWeight: String? = null,
    marketFontFamily: String? = null
) {
    val buy = rate.buy ?: 0.0
    val sell = rate.sell ?: 0.0
    val change = rate.changeRate ?: 0.0
    val isPositive = change >= 0
    val showChange = changeRateEnabled && change != 0.0

    val rowModifier = Modifier
        .fillMaxWidth()
        .height(listConfig.rowHeightDp)
        .padding(horizontal = listConfig.paddingHorizontalDp, vertical = listConfig.paddingVerticalDp)
        .then(
            if (listConfig.hasCard) Modifier
                .clip(RoundedCornerShape(listConfig.cardRadiusDp))
                .then(
                    if (listConfig.borderWidth > 0) Modifier.border(
                        listConfig.borderWidthDp,
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                        RoundedCornerShape(listConfig.cardRadiusDp)
                    ) else Modifier
                )
            else Modifier
        )

    val backgroundColor = when {
        listConfig.layoutType == ListLayoutType.ELEVATED -> Color(0xFF222222)
        listConfig.hasCard -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val elevation = if (listConfig.hasShadow) 2.dp else 0.dp

    when (listConfig.layoutType) {
        ListLayoutType.MINIMAL -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = rowModifier.background(backgroundColor),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                Column(modifier = Modifier.weight(1f)) {
                    ThemedText(
                        text = rate.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        marketFontSize = marketFontSize,
                        marketFontWeight = marketFontWeight,
                        marketFontFamily = marketFontFamily
                    )
                }
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
                    ThemedText(text = "%.2f".format(buy), style = MaterialTheme.typography.bodyMedium, marketFontSize = marketFontSize, marketFontWeight = marketFontWeight, marketFontFamily = marketFontFamily)
                    ThemedText(text = "%.2f".format(sell), style = MaterialTheme.typography.bodyMedium, marketFontSize = marketFontSize, marketFontWeight = marketFontWeight, marketFontFamily = marketFontFamily)
                        if (showChange) {
                            ThemedText(
                                text = "%s%.2f%%".format(if (isPositive) "+" else "", change),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isPositive) LocalAppTheme.current.success else LocalAppTheme.current.danger
                            )
                        }
                    }
                    if (trailingContent != null) trailingContent()
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            }
        }
        ListLayoutType.CARD, ListLayoutType.BORDERED -> {
            Card(
                modifier = rowModifier,
                shape = RoundedCornerShape(listConfig.cardRadiusDp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                elevation = CardDefaults.cardElevation(defaultElevation = elevation)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = listConfig.paddingHorizontalDp, vertical = listConfig.paddingVerticalDp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        ThemedText(
                            text = rate.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            marketFontSize = marketFontSize,
                            marketFontWeight = marketFontWeight,
                            marketFontFamily = marketFontFamily
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        ThemedText(text = "Alış: %.2f".format(buy), style = MaterialTheme.typography.bodyMedium, marketFontSize = marketFontSize, marketFontWeight = marketFontWeight, marketFontFamily = marketFontFamily)
                        ThemedText(text = "Satış: %.2f".format(sell), style = MaterialTheme.typography.bodyMedium, marketFontSize = marketFontSize, marketFontWeight = marketFontWeight, marketFontFamily = marketFontFamily)
                        if (showChange) {
                            ThemedText(
                                text = "%s%.2f%%".format(if (isPositive) "+" else "", change),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isPositive) LocalAppTheme.current.success else LocalAppTheme.current.danger
                            )
                        }
                    }
                    if (trailingContent != null) trailingContent()
                }
            }
        }
        ListLayoutType.ELEVATED -> {
            Card(
                modifier = rowModifier,
                shape = RoundedCornerShape(listConfig.cardRadiusDp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                elevation = CardDefaults.cardElevation(defaultElevation = elevation)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(listConfig.rowHeightDp)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = listConfig.paddingHorizontalDp, vertical = listConfig.paddingVerticalDp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            ThemedText(
                                text = rate.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                marketFontSize = marketFontSize,
                                marketFontWeight = marketFontWeight,
                                marketFontFamily = marketFontFamily
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            ThemedText(text = "%.2f".format(buy), style = MaterialTheme.typography.bodyMedium, marketFontSize = marketFontSize, marketFontWeight = marketFontWeight, marketFontFamily = marketFontFamily)
                            ThemedText(text = "%.2f".format(sell), style = MaterialTheme.typography.bodyMedium, marketFontSize = marketFontSize, marketFontWeight = marketFontWeight, marketFontFamily = marketFontFamily)
                            if (showChange) {
                                ThemedText(
                                    text = "%s%.2f%%".format(if (isPositive) "+" else "", change),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isPositive) LocalAppTheme.current.success else LocalAppTheme.current.danger
                                )
                            }
                        }
                        if (trailingContent != null) trailingContent()
                    }
                }
            }
        }
        ListLayoutType.COMPACT -> {
            Card(
                modifier = rowModifier,
                shape = RoundedCornerShape(listConfig.cardRadiusDp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = elevation)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = listConfig.paddingHorizontalDp, vertical = listConfig.paddingVerticalDp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        ThemedText(
                            text = rate.displayName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            marketFontSize = marketFontSize,
                            marketFontWeight = marketFontWeight
                        )
                        if (showChange) {
                            ThemedText(
                                text = "%s%.2f%%".format(if (isPositive) "+" else "", change),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isPositive) LocalAppTheme.current.success else LocalAppTheme.current.danger
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        ThemedText(text = "ALIŞ", style = MaterialTheme.typography.labelSmall, isSecondary = true)
                        ThemedText(text = "%.2f".format(buy), style = MaterialTheme.typography.bodyLarge, marketFontSize = marketFontSize, marketFontWeight = marketFontWeight, marketFontFamily = marketFontFamily)
                        ThemedText(text = "SATIŞ", style = MaterialTheme.typography.labelSmall, isSecondary = true)
                        ThemedText(text = "%.2f".format(sell), style = MaterialTheme.typography.bodyLarge, marketFontSize = marketFontSize, marketFontWeight = marketFontWeight, marketFontFamily = marketFontFamily)
                    }
                    if (trailingContent != null) trailingContent()
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                        color = if (isPositive) LocalAppTheme.current.success else LocalAppTheme.current.danger
                    )
                }
            }
            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}
