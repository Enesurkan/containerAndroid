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
 * Ürün detay fiyatı – iOS ile aynı: 2 ondalık, " TL" (%.2f TL).
 */
fun formatProductDetailPrice(price: Double): String = "%.2f TL".format(price)
