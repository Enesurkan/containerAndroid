package com.example.altintakipandroid.ui.converter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.example.altintakipandroid.ui.components.CustomHeader
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.domain.AppInformationData
import com.example.altintakipandroid.domain.UIConfig
import com.example.altintakipandroid.ui.main.getNavigationConfig
import com.example.altintakipandroid.ui.util.formatPriceForDisplay

@Composable
fun ConverterScreen(
    config: UIConfig,
    appInfo: AppInformationData,
    viewModel: ConverterViewModel
) {
    val state by viewModel.state.collectAsState()
    val cornerRadius = (config.cornerRadiusScale).dp
    val navConfig = remember(config.navigationStyle) { getNavigationConfig(config.navigationStyle) }

    ThemedView {
        Column(modifier = Modifier.fillMaxSize()) {
            CustomHeader(
                title = "Çevirici",
                navigationStyle = config.navigationStyle,
                navConfig = navConfig,
                appInfo = appInfo
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Varlık Seçin card
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { viewModel.showAssetPicker(true) }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            ThemedText(
                                text = "Varlık Seçin",
                                style = MaterialTheme.typography.labelMedium,
                                isSecondary = true
                            )
                            ThemedText(
                                text = state.sourceAsset?.displayName ?: "Seçiniz",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Miktar
                Column {
                    ThemedText(
                        text = "Miktar",
                        style = MaterialTheme.typography.labelMedium,
                        isSecondary = true,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    BasicTextField(
                        value = state.amount,
                        onValueChange = viewModel::setAmount,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(cornerRadius))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp),
                        textStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Toplam Tutar (TRY)
                Column {
                    ThemedText(
                        text = "Toplam Tutar (TRY)",
                        style = MaterialTheme.typography.labelMedium,
                        isSecondary = true,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(cornerRadius))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp)
                    ) {
                        ThemedText(
                            text = formatPriceForDisplay(state.result),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

    if (state.showAssetPicker) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { viewModel.showAssetPicker(false) }) {
            AssetPickerSheet(
                rates = state.rates,
                onSelect = viewModel::setSourceAsset,
                onDismiss = { viewModel.showAssetPicker(false) }
            )
        }
    }
}
