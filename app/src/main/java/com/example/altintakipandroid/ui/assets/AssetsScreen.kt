package com.example.altintakipandroid.ui.assets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.altintakipandroid.domain.UserAsset
import com.example.altintakipandroid.domain.AppInformationData
import com.example.altintakipandroid.domain.UIConfig
import com.example.altintakipandroid.ui.components.CustomHeader
import com.example.altintakipandroid.ui.main.getNavigationConfig
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.ui.theme.LocalAppTheme

@Composable
fun AssetsScreen(
    config: UIConfig,
    appInfo: AppInformationData,
    viewModel: AssetsViewModel
) {
    val state by viewModel.state.collectAsState()
    val navConfig = remember(config.navigationStyle) { getNavigationConfig(config.navigationStyle) }
    val cornerRadius = (config.cornerRadiusScale).dp

    ThemedView {
        Column(modifier = Modifier.fillMaxSize()) {
            CustomHeader(
                title = "Varlıklarım",
                navigationStyle = config.navigationStyle,
                navConfig = navConfig,
                appInfo = appInfo,
                trailingContent = {
                    IconButton(onClick = { viewModel.showAddAsset(true) }) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Ekle",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Summary card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 32.dp)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ThemedText(
                        text = "Toplam Portföy Değeri",
                        isSecondary = true,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    ThemedText(
                        text = "%.2f ₺".format(state.totalValue),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ThemedText(
                            text = "Toplam Kar/Zarar:",
                            isSecondary = true,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = formatProfit(state.totalProfit),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = if (state.totalProfit >= 0) LocalAppTheme.current.success else LocalAppTheme.current.danger
                        )
                    }
                }

                if (state.assets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                modifier = Modifier.padding(8.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            ThemedText(
                                text = "Henüz varlık eklemediniz",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.assets) { asset ->
                            val rate = state.rates.find { it.apiId == asset.exchangeRateId }
                            val currentValue = (rate?.sell ?: 0.0) * asset.amount
                            val profit = currentValue - (asset.purchasePrice * asset.amount)
                            AssetRow(
                                asset = asset,
                                currentValue = currentValue,
                                profit = profit,
                                onDelete = { viewModel.deleteAsset(asset.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (state.showAddAsset) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { viewModel.showAddAsset(false) }) {
            AddAssetScreen(
                rates = state.rates,
                onSave = { viewModel.saveAsset(it) },
                onDismiss = { viewModel.showAddAsset(false) }
            )
        }
    }
}

@Composable
private fun AssetRow(
    asset: UserAsset,
    currentValue: Double,
    profit: Double,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            ThemedText(
                text = asset.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            ThemedText(
                text = "%.2f Birim".format(asset.amount),
                isSecondary = true,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            ThemedText(
                text = "%.2f ₺".format(currentValue),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = formatProfit(profit),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = if (profit >= 0) LocalAppTheme.current.success else LocalAppTheme.current.danger
            )
        }
        TextButton(onClick = onDelete) {
            Text("Sil", color = LocalAppTheme.current.danger)
        }
    }
}

private fun formatProfit(profit: Double): String {
    val sign = if (profit >= 0) "+" else ""
    return "$sign%.2f ₺".format(profit)
}
