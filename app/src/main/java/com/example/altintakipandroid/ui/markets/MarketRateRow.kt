package com.example.altintakipandroid.ui.markets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.altintakipandroid.domain.ExchangeRate
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.main.ListLayoutType
import com.example.altintakipandroid.ui.main.ListStyleConfig
import com.example.altintakipandroid.ui.theme.LocalAppTheme
import com.example.altintakipandroid.ui.util.formatPriceForDisplay
import kotlin.math.abs

/** Rate row driven by ui-config listStyle (iOS ExchangeRateRow). Respects changeRateEnabled. When isFavorite, applies same list-style visuals as iOS (background tint, leading bar or card stroke). */
@Composable
fun MarketRateRow(
    rate: ExchangeRate,
    listConfig: ListStyleConfig,
    changeRateEnabled: Boolean,
    trailingContent: @Composable (() -> Unit)? = null,
    marketFontSize: Double? = null,
    marketFontWeight: String? = null,
    marketFontFamily: String? = null,
    isFavorite: Boolean = false
) {
    val buy = rate.buy ?: 0.0
    val sell = rate.sell ?: 0.0
    val change = rate.changeRate ?: 0.0
    val isPositive = change >= 0
    val showChange = changeRateEnabled && change != 0.0
    val symbolFirst = listConfig.layoutType == ListLayoutType.MINIMAL

    val appTheme = LocalAppTheme.current
    val successColor = appTheme.success
    val dangerColor = appTheme.danger
    var previousBuy by remember { mutableStateOf(buy) }
    var previousSell by remember { mutableStateOf(sell) }
    val buyFlashAlpha = remember { Animatable(0f) }
    val sellFlashAlpha = remember { Animatable(0f) }
    var buyFlashColor by remember { mutableStateOf<Color?>(null) }
    var sellFlashColor by remember { mutableStateOf<Color?>(null) }

    LaunchedEffect(buy) {
        if (abs(buy - previousBuy) > 0.0001) {
            val isIncrease = buy > previousBuy
            buyFlashColor = if (isIncrease) successColor else dangerColor
            buyFlashAlpha.snapTo(0f)
            repeat(5) {
                buyFlashAlpha.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
                buyFlashAlpha.animateTo(0f, animationSpec = tween(300, easing = FastOutSlowInEasing))
            }
            buyFlashColor = null
        }
        previousBuy = buy
    }
    LaunchedEffect(sell) {
        if (abs(sell - previousSell) > 0.0001) {
            val isIncrease = sell > previousSell
            sellFlashColor = if (isIncrease) successColor else dangerColor
            sellFlashAlpha.snapTo(0f)
            repeat(5) {
                sellFlashAlpha.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
                sellFlashAlpha.animateTo(0f, animationSpec = tween(300, easing = FastOutSlowInEasing))
            }
            sellFlashColor = null
        }
        previousSell = sell
    }

    val backgroundColor = when {
        isFavorite -> if (listConfig.layoutType == ListLayoutType.ELEVATED) appTheme.accentColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        listConfig.layoutType == ListLayoutType.ELEVATED -> Color(0xFF222222)
        listConfig.hasCard -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val elevation = if (listConfig.hasShadow) 2.dp else 0.dp
    val favoriteLeadingBar = isFavorite && !listConfig.hasCard
    val favoriteCardStroke = isFavorite && listConfig.hasCard
    val favoriteStrokeColor = when {
        !favoriteCardStroke -> null
        listConfig.layoutType == ListLayoutType.COMPACT -> appTheme.accentColor.copy(alpha = 0.3f)
        listConfig.layoutType == ListLayoutType.CARD -> appTheme.accentColor.copy(alpha = 0.15f)
        else -> appTheme.accentColor.copy(alpha = 0.4f)
    }
    val effectiveBackgroundColor = backgroundColor

    val defaultTextColor: Color = MaterialTheme.colorScheme.onSurface
    val buyPriceColor: Color? = buyFlashColor?.let { flashColor: Color ->
        androidx.compose.ui.graphics.lerp(defaultTextColor, flashColor, buyFlashAlpha.value)
    }
    val sellPriceColor: Color? = sellFlashColor?.let { flashColor: Color ->
        androidx.compose.ui.graphics.lerp(defaultTextColor, flashColor, sellFlashAlpha.value)
    }

    val rowModifier = Modifier
        .fillMaxWidth()
        .height(listConfig.rowHeightDp)
        .then(
            if (listConfig.layoutType != ListLayoutType.COMPACT)
                Modifier.padding(horizontal = listConfig.paddingHorizontalDp, vertical = listConfig.paddingVerticalDp)
            else Modifier
        )
        .then(
            if (listConfig.hasCard) Modifier
                .clip(RoundedCornerShape(listConfig.cardRadiusDp))
                .then(
                    when {
                        favoriteCardStroke && favoriteStrokeColor != null -> Modifier.border(1.dp, favoriteStrokeColor, RoundedCornerShape(listConfig.cardRadiusDp))
                        listConfig.borderWidth > 0 -> Modifier.border(listConfig.borderWidthDp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f), RoundedCornerShape(listConfig.cardRadiusDp))
                        listConfig.layoutType == ListLayoutType.COMPACT -> Modifier.border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(listConfig.cardRadiusDp))
                        else -> Modifier
                    }
                )
            else Modifier
        )

    when (listConfig.layoutType) {
        ListLayoutType.MINIMAL -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(listConfig.rowHeightDp)
                        .background(effectiveBackgroundColor)
                ) {
                    if (favoriteLeadingBar) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(appTheme.accentColor)
                        )
                    }
                    Row(
                        modifier = rowModifier
                            .then(if (favoriteLeadingBar) Modifier.padding(start = 4.dp) else Modifier),
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
                    ThemedText(
                        text = formatPriceForDisplay(buy, symbolFirst),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(listConfig.priceColWidthDp).padding(end = 8.dp),
                        textAlign = TextAlign.End,
                        marketFontSize = marketFontSize,
                        marketFontWeight = marketFontWeight,
                        marketFontFamily = marketFontFamily,
                        color = buyPriceColor
                    )
                    ThemedText(
                        text = formatPriceForDisplay(sell, symbolFirst),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(listConfig.priceColWidthDp),
                        textAlign = TextAlign.End,
                        marketFontSize = marketFontSize,
                        marketFontWeight = marketFontWeight,
                        marketFontFamily = marketFontFamily,
                        color = sellPriceColor
                    )
                    if (showChange) {
                        Spacer(modifier = Modifier.width(8.dp))
                        ThemedText(
                            text = "%s%.2f%%".format(if (isPositive) "+" else "", change),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isPositive) LocalAppTheme.current.success else LocalAppTheme.current.danger
                        )
                    }
                    if (trailingContent != null) trailingContent()
                }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            }
        }
        ListLayoutType.CARD, ListLayoutType.BORDERED -> {
            Card(
                modifier = rowModifier,
                shape = RoundedCornerShape(listConfig.cardRadiusDp),
                colors = CardDefaults.cardColors(containerColor = effectiveBackgroundColor),
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
                        ThemedText(text = "Alış: ${formatPriceForDisplay(buy, symbolFirst)}", style = MaterialTheme.typography.bodyMedium, marketFontSize = marketFontSize, marketFontWeight = marketFontWeight, marketFontFamily = marketFontFamily, color = buyPriceColor)
                        ThemedText(text = "Satış: ${formatPriceForDisplay(sell, symbolFirst)}", style = MaterialTheme.typography.bodyMedium, marketFontSize = marketFontSize, marketFontWeight = marketFontWeight, marketFontFamily = marketFontFamily, color = sellPriceColor)
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
                colors = CardDefaults.cardColors(containerColor = effectiveBackgroundColor),
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
                            ThemedText(text = formatPriceForDisplay(buy, symbolFirst), style = MaterialTheme.typography.bodyMedium, marketFontSize = marketFontSize, marketFontWeight = marketFontWeight, marketFontFamily = marketFontFamily, color = buyPriceColor)
                            ThemedText(text = formatPriceForDisplay(sell, symbolFirst), style = MaterialTheme.typography.bodyMedium, marketFontSize = marketFontSize, marketFontWeight = marketFontWeight, marketFontFamily = marketFontFamily, color = sellPriceColor)
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
            val appTheme = LocalAppTheme.current
            Card(
                modifier = rowModifier,
                shape = RoundedCornerShape(listConfig.cardRadiusDp),
                colors = CardDefaults.cardColors(containerColor = effectiveBackgroundColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = listConfig.rowHeightDp)
                        .padding(horizontal = listConfig.paddingHorizontalDp, vertical = listConfig.paddingVerticalDp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sol: showableText (Gram Altın vb) + altında changeRate bilgisi
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)
                    ) {
                        ThemedText(
                            text = rate.showableText ?: rate.displayName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                            marketFontSize = marketFontSize?.coerceAtLeast(22.0) ?: 22.0,
                            marketFontWeight = marketFontWeight,
                            marketFontFamily = marketFontFamily
                        )
                        if (changeRateEnabled && rate.changeRate != null) {
                            val changeVal = rate.changeRate!!
                            val isNeutral = abs(changeVal) < 0.05
                            val changeColor = when {
                                isNeutral -> appTheme.textSecondary
                                changeVal > 0 -> appTheme.success
                                else -> appTheme.danger
                            }
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(changeColor.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when {
                                        isNeutral -> Icons.AutoMirrored.Outlined.TrendingUp
                                        changeVal > 0 -> Icons.AutoMirrored.Outlined.TrendingUp
                                        else -> Icons.AutoMirrored.Outlined.TrendingDown
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(10.dp),
                                    tint = changeColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                ThemedText(
                                    text = if (isNeutral) "%.1f%%".format(kotlin.math.abs(changeVal)) else "%+.1f%%".format(changeVal),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = changeColor,
                                    marketFontSize = 12.0
                                )
                            }
                        }
                    }
                    // Sağ: Fiyatlar — üstte buy solda + "ALIŞ" sağda, altta sell solda + "SATIŞ" sağda
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(appTheme.surfaceElevatedColor.copy(alpha = 0.45f))
                            .padding(12.dp)
                    ) {
                        Column(
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ThemedText(
                                    text = "ALIŞ",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    isSecondary = true,
                                    marketFontSize = 9.0
                                )
                                ThemedText(
                                    text = formatPriceForDisplay(buy, symbolFirst),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    marketFontSize = marketFontSize,
                                    marketFontWeight = marketFontWeight,
                                    marketFontFamily = marketFontFamily,
                                    color = buyPriceColor
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ThemedText(
                                    text = "SATIŞ",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    isSecondary = true,
                                    marketFontSize = 9.0
                                )
                                ThemedText(
                                    text = formatPriceForDisplay(sell, symbolFirst),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    marketFontSize = marketFontSize,
                                    marketFontWeight = marketFontWeight,
                                    marketFontFamily = marketFontFamily,
                                    color = sellPriceColor
                                )
                            }
                        }
                    }
                    if (trailingContent != null) trailingContent()
                }
            }
        }
    }
}
