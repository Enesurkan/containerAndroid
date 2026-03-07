package com.example.altintakipandroid.domain

import com.google.gson.annotations.SerializedName

data class ExchangeRate(
    @SerializedName("id") val apiId: Int? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("base_currency_code") val baseCurrencyCode: String? = null,
    @SerializedName("target_currency_code") val targetCurrencyCode: String? = null,
    @SerializedName("currency_code") val currencyCode: String? = null,
    @SerializedName("buy") val buy: Double? = null,
    @SerializedName("sell") val sell: Double? = null,
    @SerializedName("change_rate") val changeRate: Double? = null,
    @SerializedName("day_high") val dayHigh: Double? = null,
    @SerializedName("day_low") val dayLow: Double? = null,
    @SerializedName("prev_close") val prevClose: Double? = null,
    @SerializedName("fetched_at") val fetchedAt: String? = null,
    @SerializedName("showableText") val showableText: String? = null
) {
    val displayName: String
        get() = showableText ?: description ?: "Bilinmiyor"
    val code: String
        get() = baseCurrencyCode ?: currencyCode ?: ""
}

data class ExchangeRatesResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<ExchangeRate>? = null
)
