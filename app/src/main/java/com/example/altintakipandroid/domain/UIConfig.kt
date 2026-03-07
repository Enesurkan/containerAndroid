package com.example.altintakipandroid.domain

import com.google.gson.annotations.SerializedName

/**
 * UI configuration from server (aligned with iOS UIConfig).
 */
data class UIConfig(
    @SerializedName("theme") val theme: Int = 1,
    @SerializedName("navigationStyle") val navigationStyle: Int = 1,
    @SerializedName("listStyle") val listStyle: Int = 1,
    @SerializedName("contactStyle") val contactStyle: Int = 1,
    @SerializedName("cornerRadiusScale") val cornerRadiusScale: Int = 12,
    @SerializedName("converterEnabled") val converterEnabled: Boolean = false,
    @SerializedName("changeRateEnabled") val changeRateEnabled: Boolean = false,
    @SerializedName("isFavoriteEnabled") val isFavoriteEnabled: Boolean = true,
    @SerializedName("isAssetEnabled") val isAssetEnabled: Boolean = true,
    @SerializedName("isHasChangeRouter") val isHasChangeRouter: Boolean? = null,
    @SerializedName("allowHiddenAdmin") val allowHiddenAdmin: Boolean? = true,
    @SerializedName("marketEnabled") val marketEnabled: Boolean? = true,
    @SerializedName("pushEnabled") val pushEnabled: Boolean? = true,
    @SerializedName("marketFontFamily") val marketFontFamily: String? = null,
    @SerializedName("marketFontSize") val marketFontSize: Double? = null,
    @SerializedName("marketFontWeight") val marketFontWeight: String? = null,
    @SerializedName("timerInterval") val timerInterval: Int? = null,
    @SerializedName("webUseWebSocket") val webUseWebSocket: Boolean? = null,
    @SerializedName("mobileUseWebSocket") val mobileUseWebSocket: Boolean? = false,
    @SerializedName("wsPriceJitterEnabled") val wsPriceJitterEnabled: Boolean? = null,
    @SerializedName("wsPriceJitterIntervalSec") val wsPriceJitterIntervalSec: Int? = null,
    @SerializedName("wsDripIntervalMs") val wsDripIntervalMs: Int? = null
) {
    companion object {
        val default = UIConfig()
    }
}

data class UIConfigResponse(
    @SerializedName("statusCode") val statusCode: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("ui") val ui: UIConfig? = null,
    @SerializedName("data") val data: UIConfigContainer? = null
) {
    data class UIConfigContainer(
        @SerializedName("ui") val ui: UIConfig
    )

    fun getConfig(): UIConfig = ui ?: data?.ui ?: UIConfig.default
}
