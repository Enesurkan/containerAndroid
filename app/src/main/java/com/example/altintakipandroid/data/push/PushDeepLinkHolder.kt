package com.example.altintakipandroid.data.push

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Intent extra keys for push deep link (used by MessagingService and MainActivity). */
const val PUSH_EXTRA_DEEPLINK = "deeplink"
const val PUSH_EXTRA_CURRENCY_CODE = "currencyCode"

/**
 * Holds pending push payload (deeplink, currencyCode) when user opens app from notification tap.
 * MainActivity sets from intent; MainTabScreen consumes and applies (iOS pushNotificationTapped + cold start).
 */
object PushDeepLinkHolder {
    private val _pending = MutableStateFlow<Pair<String?, String?>>(null to null)
    val pending: StateFlow<Pair<String?, String?>> = _pending.asStateFlow()

    fun set(deeplink: String?, currencyCode: String?) {
        _pending.value = (
            deeplink?.takeIf { it.isNotBlank() }
                to currencyCode?.takeIf { it.isNotBlank() }
        )
    }

    /** Consume and clear; returns (deeplink, currencyCode). */
    fun consume(): Pair<String?, String?> {
        val v = _pending.value
        _pending.value = null to null
        return v
    }
}
