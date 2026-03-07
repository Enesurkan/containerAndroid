package com.example.altintakipandroid.ui.main

/**
 * Navigation style config (iOS NavigationStyleConfig). style1 = default.
 */
data class NavigationStyleConfig(
    val height: Int = 50,
    val hasShadow: Boolean = true,
    val hasUnderline: Boolean = false,
    val fontSize: Int = 20,
    val tabBarStyle: String = "default",
    val tabBarHasBackground: Boolean = true,
    val tabBarHasBorder: Boolean = true,
    val tabBarSelectedBackground: Boolean = false
)

fun getNavigationConfig(navStyle: Int): NavigationStyleConfig {
    return when (navStyle) {
        1 -> NavigationStyleConfig(
            height = 50,
            hasShadow = true,
            tabBarStyle = "default",
            tabBarHasBackground = true,
            tabBarHasBorder = true
        )
        2 -> NavigationStyleConfig(
            height = 56,
            hasShadow = true,
            tabBarStyle = "highlighted",
            tabBarHasBackground = true,
            tabBarHasBorder = true
        )
        3 -> NavigationStyleConfig(
            height = 50,
            hasShadow = false,
            tabBarStyle = "minimal",
            tabBarHasBackground = false,
            tabBarHasBorder = true
        )
        4 -> NavigationStyleConfig(
            height = 52,
            hasShadow = true,
            tabBarStyle = "bordered",
            tabBarHasBackground = true,
            tabBarHasBorder = true
        )
        5 -> NavigationStyleConfig(
            height = 48,
            hasShadow = false,
            tabBarStyle = "minimal",
            tabBarHasBackground = false,
            tabBarHasBorder = false
        )
        else -> NavigationStyleConfig()
    }
}
