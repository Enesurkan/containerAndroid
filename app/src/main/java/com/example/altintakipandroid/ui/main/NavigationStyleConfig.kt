package com.example.altintakipandroid.ui.main

/**
 * Navigation style config (iOS NavigationStyleConfig). Values match StyleManager.getNavigationConfig.
 */
data class NavigationStyleConfig(
    val height: Int = 50,
    val hasShadow: Boolean = true,
    val hasUnderline: Boolean = false,
    val fontSize: Int = 20,
    val hasContainer: Boolean = false,
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
            hasUnderline = false,
            fontSize = 20,
            tabBarStyle = "default",
            tabBarHasBackground = true,
            tabBarHasBorder = true,
            tabBarSelectedBackground = false
        )
        2 -> NavigationStyleConfig(
            height = 50,
            hasShadow = true,
            hasUnderline = false,
            fontSize = 21,
            tabBarStyle = "highlighted",
            tabBarHasBackground = true,
            tabBarHasBorder = true,
            tabBarSelectedBackground = true
        )
        3 -> NavigationStyleConfig(
            height = 50,
            hasShadow = false,
            hasUnderline = false,
            fontSize = 18,
            tabBarStyle = "highlighted",
            tabBarHasBackground = true,
            tabBarHasBorder = false,
            tabBarSelectedBackground = true
        )
        4 -> NavigationStyleConfig(
            height = 50,
            hasShadow = true,
            hasUnderline = false,
            fontSize = 20,
            tabBarStyle = "bordered",
            tabBarHasBackground = true,
            tabBarHasBorder = true,
            tabBarSelectedBackground = false
        )
        5 -> NavigationStyleConfig(
            height = 50,
            hasShadow = false,
            hasUnderline = true,
            fontSize = 19,
            tabBarStyle = "minimal",
            tabBarHasBackground = false,
            tabBarHasBorder = false,
            tabBarSelectedBackground = false
        )
        else -> NavigationStyleConfig()
    }
}
