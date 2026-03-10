package com.example.altintakipandroid.ui.util

import java.text.NumberFormat
import java.util.Locale

/**
 * iOS StyleManager.formatPrice uyumlu fiyat formatı.
 * tr_TR locale, 0 ondalık, binlik ayırıcı, " ₺" veya "₺" (symbolFirst).
 */
fun formatPriceForDisplay(price: Double, symbolFirst: Boolean = false): String {
    val nf = NumberFormat.getNumberInstance(Locale("tr", "TR")).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
        isGroupingUsed = true
    }
    val formatted = nf.format(kotlin.math.round(price))
    return if (symbolFirst) "₺$formatted" else "$formatted ₺"
}

/**
 * Ürün detay ve liste fiyatı: Türkçe format 101.234,00 TL (binlik nokta, ondalık virgül, 2 hane).
 */
fun formatProductDetailPrice(price: Double): String {
    val nf = NumberFormat.getNumberInstance(Locale("tr", "TR")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
        isGroupingUsed = true
    }
    return "${nf.format(price)} TL"
}
