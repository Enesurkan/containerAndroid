package com.example.altintakipandroid.domain

import com.google.gson.annotations.SerializedName

/**
 * Activation API response (aligned with iOS ActivationResponse).
 */
data class ActivationResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ActivationData
) {
    data class ActivationData(
        @SerializedName("apiKey") val apiKey: String
    )
}
