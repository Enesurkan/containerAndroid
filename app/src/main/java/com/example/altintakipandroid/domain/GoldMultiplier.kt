package com.example.altintakipandroid.domain

import com.google.gson.annotations.SerializedName

/**
 * Gold multiplier item (iOS GoldMultiplier). GET /client-apps/gold-multipliers returns list.
 */
data class GoldMultiplier(
    @SerializedName("id") val id: Int,
    @SerializedName("client_id") val clientId: Int? = null,
    @SerializedName("currency_code") val currencyCode: String,
    @SerializedName("buy_multiplier") var buyMultiplier: Double,
    @SerializedName("sell_multiplier") var sellMultiplier: Double
)

data class GoldMultipliersResponse(
    @SerializedName("statusCode") val statusCode: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<GoldMultiplier>? = null
)

/**
 * Item for POST save. Backend expects camelCase: currencyCode, buyMultiplier, sellMultiplier.
 */
data class SaveGoldMultiplierItem(
    val currencyCode: String,
    val buyMultiplier: Double,
    val sellMultiplier: Double
)
