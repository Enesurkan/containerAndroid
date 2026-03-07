package com.example.altintakipandroid.ui.market

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MarketTabContent(
    marketViewModel: MarketViewModel
) {
    var navStack by remember { mutableStateOf(listOf<MarketRoute>(MarketRoute.Main)) }
    val current = navStack.last()
    val app = LocalContext.current.applicationContext as Application

    fun push(route: MarketRoute) {
        navStack = navStack + route
    }

    fun pop() {
        if (navStack.size > 1) navStack = navStack.dropLast(1)
    }

    when (val route = current) {
        is MarketRoute.Main -> {
            MarketMainScreen(
                viewModel = marketViewModel,
                onOpenCampaigns = { push(MarketRoute.Campaigns) },
                onOpenCampaignDetail = { id -> push(MarketRoute.CampaignDetail(id)) },
                onOpenProductList = { catId, subId, title ->
                    push(MarketRoute.ProductList(catId, subId, title))
                }
            )
        }
        is MarketRoute.Campaigns -> {
            KampanyalarScreen(
                banners = marketViewModel.state.value.bannerSlides,
                onBack = { pop() },
                onCampaignClick = { id -> push(MarketRoute.CampaignDetail(id)) }
            )
        }
        is MarketRoute.CampaignDetail -> {
            val detailViewModel: KampanyaDetailViewModel = viewModel(
                factory = KampanyaDetailViewModelFactory(app, route.slideId)
            )
            KampanyaDetailScreen(
                viewModel = detailViewModel,
                onBack = { pop() }
            )
        }
        is MarketRoute.ProductList -> {
            val listViewModel: ProductListViewModel = viewModel(
                factory = ProductListViewModelFactory(app, route.categoryId, route.subcategoryId)
            )
            ProductListScreen(
                viewModel = listViewModel,
                title = route.title,
                onBack = { pop() },
                onProductClick = { id -> push(MarketRoute.ProductDetail(id)) }
            )
        }
        is MarketRoute.ProductDetail -> {
            val detailViewModel: ProductDetailViewModel = viewModel(
                factory = ProductDetailViewModelFactory(app, route.productId)
            )
            ProductDetailScreen(
                viewModel = detailViewModel,
                onBack = { pop() }
            )
        }
    }
}
