package com.example.altintakipandroid.ui.assets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.example.altintakipandroid.domain.ExchangeRate
import com.example.altintakipandroid.domain.UserAsset
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.ui.converter.AssetPickerSheet

@Composable
fun AddAssetScreen(
    rates: List<ExchangeRate>,
    onSave: (UserAsset) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedRate by remember { mutableStateOf<ExchangeRate?>(null) }
    var amount by remember { mutableStateOf("") }
    var purchasePrice by remember { mutableStateOf("") }
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showPicker = false }) {
            AssetPickerSheet(
                rates = rates,
                onSelect = {
                    selectedRate = it
                    if (purchasePrice.isEmpty()) purchasePrice = "%.2f".format(it.sell ?: 0.0)
                    showPicker = false
                },
                onDismiss = { showPicker = false }
            )
        }
    }

    ThemedView {
        Column(modifier = Modifier.padding(24.dp)) {
            ThemedText(
                text = "Varlık Ekle",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))

            // Varlık
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { showPicker = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ThemedText(text = "Varlık", modifier = Modifier.weight(1f))
                ThemedText(
                    text = selectedRate?.displayName ?: "Seçiniz",
                    color = MaterialTheme.colorScheme.secondary
                )
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))

            // Miktar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ThemedText(text = "Miktar", modifier = Modifier.padding(end = 16.dp))
                BasicTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary)
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))

            // Alış Fiyatı (₺)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ThemedText(text = "Alış Fiyatı (₺)", modifier = Modifier.padding(end = 16.dp))
                BasicTextField(
                    value = purchasePrice,
                    onValueChange = { purchasePrice = it },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary)
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("İptal")
                }
                Button(
                    onClick = {
                        val r = selectedRate ?: return@Button
                        val a = amount.replace(",", ".").toDoubleOrNull() ?: return@Button
                        val p = purchasePrice.replace(",", ".").toDoubleOrNull() ?: return@Button
                        onSave(
                            UserAsset(
                                exchangeRateId = r.apiId ?: 0,
                                displayName = r.displayName,
                                amount = a,
                                purchasePrice = p
                            )
                        )
                    },
                    enabled = selectedRate != null && amount.isNotBlank() && purchasePrice.isNotBlank()
                ) {
                    Text("Kaydet")
                }
            }
        }
    }
}
