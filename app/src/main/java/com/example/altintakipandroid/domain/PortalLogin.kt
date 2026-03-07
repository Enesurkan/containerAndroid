package com.example.altintakipandroid.domain

import com.google.gson.annotations.SerializedName

/**
 * Portal (Admin) login request/response.
 */
data class PortalLoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class PortalLoginResponse(
    @SerializedName("statusCode") val statusCode: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: PortalLoginData? = null
) {
    data class PortalLoginData(
        @SerializedName("token") val token: String? = null,
        @SerializedName("redirectUrl") val redirectUrl: String? = null
    )
}
