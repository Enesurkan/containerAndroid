package com.example.altintakipandroid.domain

/**
 * API base URL and endpoint paths (aligned with iOS AppConstants).
 */
object AppConstants {
    const val BASE_URL = "https://api.dienu.work"
    const val DEFAULT_REFRESH_INTERVAL = 10

    /** Client name for optional fallback: backend can return demo key for this client. */
    const val DEFAULT_CLIENT_NAME = "default"

    object Api {
        const val GOLD_CURRENCY = "/gold-currency"
        const val UI_CONFIG = "/api/v1/ui-config"
        const val APP_INFORMATION = "/client-apps/app-information"
        const val ACTIVATE = "/activate"
        const val PORTAL_LOGIN = "/client-apps/portal-login"
        const val GOLD_MULTIPLIERS = "/client-apps/gold-multipliers"
        const val REGISTER_DEVICE = "/client-apps/register-device"
        const val WS_TOKEN = "/client-apps/ws-token"
        const val WS_PRICES = "/ws/prices"
        const val PUSH_REGISTER = "/client-apps/push/register"
        const val PUSH_UNREGISTER = "/client-apps/push/unregister"
        const val MARKET_CONFIG = "/client/market-config"
        const val MARKET_BANNER_SLIDES = "/client/banner-slides"
        const val MARKET_CATEGORIES = "/client/categories"
        const val MARKET_PRODUCTS = "/client/products"
        const val GENERATE_SHARE_URL = "/client/products/generate-share-url"
    }
}
