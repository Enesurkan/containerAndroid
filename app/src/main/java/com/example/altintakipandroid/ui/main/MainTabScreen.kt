package com.example.altintakipandroid.ui.main

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.example.altintakipandroid.domain.AppInformationData
import com.example.altintakipandroid.domain.UIConfig
import com.example.altintakipandroid.ui.assets.AssetsScreen
import com.example.altintakipandroid.ui.assets.AssetsViewModel
import com.example.altintakipandroid.ui.contact.ContactScreen
import com.example.altintakipandroid.ui.converter.ConverterScreen
import com.example.altintakipandroid.ui.converter.ConverterViewModel
import com.example.altintakipandroid.ui.favorites.FavoritesScreen
import com.example.altintakipandroid.ui.favorites.FavoritesViewModel
import com.example.altintakipandroid.ui.market.MarketTabContent
import com.example.altintakipandroid.ui.market.MarketViewModel
import com.example.altintakipandroid.ui.markets.MarketsScreen
import com.example.altintakipandroid.ui.markets.MarketsViewModel

@Composable
fun MainTabScreen(
    config: UIConfig,
    appInfo: AppInformationData,
    onLogout: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableStateOf(TabType.MARKETS) }

    val activeTabs = remember(config) {
        buildList {
            add(TabType.MARKETS)
            if (config.isFavoriteEnabled) add(TabType.FAVORITES)
            if (config.converterEnabled) add(TabType.CONVERTER)
            if (config.isAssetEnabled) add(TabType.ASSETS)
            if (config.marketEnabled == true) add(TabType.MARKET)
            add(TabType.CONTACT)
        }
    }

    // If selected tab is not in activeTabs (e.g. config changed), switch to first
    if (selectedTab !in activeTabs) {
        selectedTab = activeTabs.first()
    }

    val navConfig = remember(config.navigationStyle) { getNavigationConfig(config.navigationStyle) }

    val converterViewModel: ConverterViewModel = viewModel()
    val assetsViewModel: AssetsViewModel = viewModel()
    val marketViewModel: MarketViewModel = viewModel()
    val app = LocalContext.current.applicationContext as android.app.Application
    val marketsViewModel: MarketsViewModel = viewModel(
        factory = MarketsViewModel.Factory(
            application = app,
            useWebSocket = config.mobileUseWebSocket == true,
            timerIntervalSeconds = config.timerInterval,
            wsPriceJitterEnabled = config.wsPriceJitterEnabled,
            wsPriceJitterIntervalSec = config.wsPriceJitterIntervalSec,
            wsDripIntervalMs = config.wsDripIntervalMs
        )
    )
    val favoritesViewModel: FavoritesViewModel = viewModel()

    // ui-config'te pushEnabled true ise ve daha önce bildirim izni verilmediyse iste (iOS ile uyumlu)
    if (config.pushEnabled == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val context = LocalContext.current
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }
        LaunchedEffect(Unit) {
            delay(500)
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                runCatching {
                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab"
            ) { tab ->
                when (tab) {
                    TabType.MARKETS -> MarketsScreen(
                        config = config,
                        appInfo = appInfo,
                        viewModel = marketsViewModel,
                        favoritesViewModel = if (config.isFavoriteEnabled) favoritesViewModel else null
                    )
                    TabType.FAVORITES -> FavoritesScreen(
                        config = config,
                        appInfo = appInfo,
                        viewModel = favoritesViewModel
                    )
                    TabType.CONVERTER -> ConverterScreen(
                        config = config,
                        appInfo = appInfo,
                        viewModel = converterViewModel
                    )
                    TabType.ASSETS -> AssetsScreen(
                        config = config,
                        appInfo = appInfo,
                        viewModel = assetsViewModel
                    )
                    TabType.MARKET -> MarketTabContent(
                        config = config,
                        appInfo = appInfo,
                        marketViewModel = marketViewModel
                    )
                    TabType.CONTACT -> ContactScreen(
                        config = config,
                        appInfo = appInfo,
                        onLogout = onLogout
                    )
                }
            }
        }
        CustomTabBar(
            selectedTab = selectedTab,
            activeTabs = activeTabs,
            onTabSelected = { selectedTab = it },
            navConfig = navConfig
        )
    }
}
