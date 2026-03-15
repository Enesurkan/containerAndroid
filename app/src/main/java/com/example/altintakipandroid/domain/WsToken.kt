package com.example.altintakipandroid.domain

import com.google.gson.annotations.SerializedName

/**
 * Response for WebSocket token endpoint (POST /client-apps/ws-token).
 */
data class WsTokenResponse(
    @SerializedName("statusCode") val statusCode: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: WsTokenData? = null
) {
    data class WsTokenData(
        @SerializedName("token") val token: String? = null,
        @SerializedName("expiresIn") val expiresIn: Int? = null
    )
}
