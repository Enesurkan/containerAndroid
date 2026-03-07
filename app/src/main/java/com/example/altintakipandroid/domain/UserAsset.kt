package com.example.altintakipandroid.domain

import java.util.UUID

data class UserAsset(
    val id: String = UUID.randomUUID().toString(),
    val exchangeRateId: Int,
    val displayName: String,
    val amount: Double,
    val purchasePrice: Double
)
