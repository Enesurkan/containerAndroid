package com.example.altintakipandroid.ui.markets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.altintakipandroid.domain.ExchangeRate
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.theme.LocalAppTheme
import com.example.altintakipandroid.ui.util.formatPriceForDisplay

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
                    text = "Alış: ${formatPriceForDisplay(buy, false)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                ThemedText(
                    text = "Satış: ${formatPriceForDisplay(sell, false)}",
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
