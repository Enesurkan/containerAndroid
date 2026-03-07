package com.example.altintakipandroid.domain

import com.google.gson.annotations.SerializedName

/**
 * Request/response for push register and unregister.
 */
data class PushRegisterRequest(
    @SerializedName("token") val token: String
)

data class PushRegisterResponse(
    @SerializedName("statusCode") val statusCode: Int? = null,
    @SerializedName("message") val message: String? = null
)
