package com.example.altintakipandroid.ui.main

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * List style config (iOS ListStyleConfig). Values match StyleManager.getListConfig.
 */
data class ListStyleConfig(
    val rowHeight: Int = 50,
    val hasEmphasizedStyle: Boolean = false,
    val hasCard: Boolean = false,
    val cardRadius: Int = 0,
    val hasShadow: Boolean = false,
    val borderWidth: Int = 0,
    val paddingHorizontal: Int = 16,
    val paddingVertical: Int = 2,
    val marginHorizontal: Int = 0,
    val marginVertical: Int = 1,
    val layoutType: ListLayoutType = ListLayoutType.MINIMAL,
    val showSectionHeader: Boolean = true,
    val priceColWidth: Int = 105
) {
    val rowHeightDp: Dp get() = rowHeight.dp
    val cardRadiusDp: Dp get() = cardRadius.dp
    val paddingHorizontalDp: Dp get() = paddingHorizontal.dp
    val paddingVerticalDp: Dp get() = paddingVertical.dp
    val marginHorizontalDp: Dp get() = marginHorizontal.dp
    val marginVerticalDp: Dp get() = marginVertical.dp
    val borderWidthDp: Dp get() = borderWidth.dp
    val priceColWidthDp: Dp get() = priceColWidth.dp
}

enum class ListLayoutType {
    MINIMAL,   // Style 1
    CARD,      // Style 2
    ELEVATED,  // Style 3
    BORDERED,  // Style 4
    COMPACT    // Style 5
}

fun getListConfig(listStyle: Int): ListStyleConfig {
    return when (listStyle) {
        1 -> ListStyleConfig(
            rowHeight = 50,
            hasEmphasizedStyle = false,
            hasCard = false,
            cardRadius = 0,
            hasShadow = false,
            borderWidth = 0,
            paddingHorizontal = 16,
            paddingVertical = 2,
            marginHorizontal = 0,
            marginVertical = 1,
            layoutType = ListLayoutType.MINIMAL,
            showSectionHeader = true,
            priceColWidth = 105
        )
        2 -> ListStyleConfig(
            rowHeight = 60,
            hasEmphasizedStyle = false,
            hasCard = true,
            cardRadius = 16,
            hasShadow = true,
            borderWidth = 0,
            paddingHorizontal = 16,
            paddingVertical = 12,
            marginHorizontal = 16,
            marginVertical = 6,
            layoutType = ListLayoutType.CARD,
            showSectionHeader = true,
            priceColWidth = 105
        )
        3 -> ListStyleConfig(
            rowHeight = 70,
            hasEmphasizedStyle = true,
            hasCard = true,
            cardRadius = 16,
            hasShadow = true,
            borderWidth = 0,
            paddingHorizontal = 16,
            paddingVertical = 10,
            marginHorizontal = 16,
            marginVertical = 6,
            layoutType = ListLayoutType.ELEVATED,
            showSectionHeader = true,
            priceColWidth = 120
        )
        4 -> ListStyleConfig(
            rowHeight = 60,
            hasEmphasizedStyle = true,
            hasCard = true,
            cardRadius = 14,
            hasShadow = false,
            borderWidth = 2,
            paddingHorizontal = 18,
            paddingVertical = 2,
            marginHorizontal = 16,
            marginVertical = 5,
            layoutType = ListLayoutType.BORDERED,
            showSectionHeader = true,
            priceColWidth = 125
        )
        5 -> ListStyleConfig(
            rowHeight = 80,
            hasEmphasizedStyle = false,
            hasCard = true,
            cardRadius = 24,
            hasShadow = true,
            borderWidth = 0,
            paddingHorizontal = 20,
            paddingVertical = 12,
            marginHorizontal = 16,
            marginVertical = 8,
            layoutType = ListLayoutType.COMPACT,
            showSectionHeader = false,
            priceColWidth = 150
        )
        else -> ListStyleConfig()
    }
}
