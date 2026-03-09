package com.example.altintakipandroid.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.ContactPhone
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.navigationBarsPadding
import com.example.altintakipandroid.ui.theme.LocalAppTheme

private val tabIcons: Map<TabType, ImageVector> = mapOf(
    TabType.MARKETS to Icons.Outlined.AttachMoney,
    TabType.FAVORITES to Icons.Outlined.Star,
    TabType.CONVERTER to Icons.Outlined.SwapHoriz,
    TabType.ASSETS to Icons.Outlined.Inventory,
    TabType.MARKET to Icons.Outlined.ShoppingBag,
    TabType.CONTACT to Icons.Outlined.ContactPhone
)

@Composable
fun CustomTabBar(
    selectedTab: TabType,
    activeTabs: List<TabType>,
    onTabSelected: (TabType) -> Unit,
    navConfig: NavigationStyleConfig
) {
    val appTheme = LocalAppTheme.current
    val backgroundColor = if (navConfig.tabBarHasBackground) appTheme.tabBarBackground else androidx.compose.ui.graphics.Color.Transparent
    val rowHeight = navConfig.height.dp

    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .navigationBarsPadding()
    ) {
        androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight)
                .background(backgroundColor)
                .padding(bottom = 0.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            activeTabs.forEach { tab ->
                TabItem(
                    tab = tab,
                    isSelected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f),
                    navConfig = navConfig
                )
            }
        }
    }
}

@Composable
private fun RowScope.TabItem(
    tab: TabType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    navConfig: NavigationStyleConfig
) {
    val color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
    val icon = tabIcons[tab] ?: Icons.Outlined.AttachMoney
    val itemHeight = navConfig.height.dp

    androidx.compose.material3.IconButton(
        onClick = onClick,
        modifier = modifier.height(itemHeight)
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = tab.label,
                modifier = Modifier.size(24.dp),
                tint = color
            )
            Text(
                text = tab.label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
            )
        }
    }
}
