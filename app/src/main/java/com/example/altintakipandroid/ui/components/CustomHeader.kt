package com.example.altintakipandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.altintakipandroid.domain.AppInformationData
import com.example.altintakipandroid.ui.main.NavigationStyleConfig

/**
 * Header with optional navigation style (iOS CustomHeader). When navConfig and appInfo are provided,
 * renders one of 5 layouts by navigationStyle; otherwise simple title row.
 */
@Composable
fun CustomHeader(
    title: String,
    modifier: Modifier = Modifier,
    navigationStyle: Int = 1,
    navConfig: NavigationStyleConfig? = null,
    appInfo: AppInformationData? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val config = navConfig ?: com.example.altintakipandroid.ui.main.getNavigationConfig(navigationStyle)
    val heightDp = config.height.dp
    val showShadow = config.hasShadow
    val headerTitle = appInfo?.navigationTitle?.takeIf { it.isNotBlank() } ?: title
    val logoUrl = appInfo?.navigationIcon?.takeIf { it.isNotBlank() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (showShadow) Modifier.shadow(4.dp, ambientColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                else Modifier
            )
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(heightDp)) {
            when (navigationStyle) {
                1, 5 -> HeaderStyle1And5(
                    title = headerTitle,
                    logoUrl = logoUrl,
                    config = config,
                    showUnderline = navigationStyle == 5
                )
                2 -> HeaderStyle2(title = headerTitle, logoUrl = logoUrl, config = config)
                3 -> HeaderStyle3(title = headerTitle, logoUrl = logoUrl, config = config)
                4 -> HeaderStyle4(title = headerTitle, logoUrl = logoUrl, config = config)
                else -> HeaderStyle1And5(
                    title = headerTitle,
                    logoUrl = logoUrl,
                    config = config,
                    showUnderline = false
                )
            }
            if (trailingContent != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                ) {
                    trailingContent()
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun LogoImage(url: String, sizeDp: Dp, modifier: Modifier = Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(true).build(),
        contentDescription = null,
        modifier = modifier.size(sizeDp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun HeaderStyle1And5(
    title: String,
    logoUrl: String?,
    config: NavigationStyleConfig,
    showUnderline: Boolean
) {
    val logoSize = minOf(60.dp, (config.height - 4).dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(config.height.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (logoUrl != null) {
            LogoImage(url = logoUrl, sizeDp = logoSize)
        }
        Column(
            modifier = Modifier.padding(start = if (logoUrl != null) 12.dp else 0.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            ThemedText(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            if (showUnderline) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .height(2.dp)
                        .width(40.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                )
            }
        }
    }
}

@Composable
private fun HeaderStyle2(title: String, logoUrl: String?, config: NavigationStyleConfig) {
    val logoSize = minOf(60.dp, (config.height - 4).dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(config.height.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ThemedText(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )
        Box(modifier = Modifier.weight(1f))
        if (logoUrl != null) {
            LogoImage(url = logoUrl, sizeDp = logoSize)
        }
    }
}

@Composable
private fun HeaderStyle3(title: String, logoUrl: String?, config: NavigationStyleConfig) {
    val logoSize = minOf(60.dp, (config.height - 4).dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(config.height.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(config.height.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (logoUrl != null) {
                LogoImage(url = logoUrl, sizeDp = logoSize)
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(config.height.dp),
            contentAlignment = Alignment.Center
        ) {
            ThemedText(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun HeaderStyle4(title: String, logoUrl: String?, config: NavigationStyleConfig) {
    val logoSize = minOf(50.dp, (config.height - 4).dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(config.height.dp)
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (logoUrl != null) {
            LogoImage(url = logoUrl, sizeDp = logoSize)
        }
        ThemedText(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
