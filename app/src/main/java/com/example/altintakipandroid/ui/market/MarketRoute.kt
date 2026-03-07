package com.example.altintakipandroid.ui.market

import com.example.altintakipandroid.domain.BannerSlide

sealed class MarketRoute {
    data object Main : MarketRoute()
    data object Campaigns : MarketRoute()
    data class CampaignDetail(val slideId: Int) : MarketRoute()
    data class ProductList(val categoryId: Int, val subcategoryId: Int?, val title: String) : MarketRoute()
    data class ProductDetail(val productId: Int) : MarketRoute()
}
