package com.example.altintakipandroid.ui.contact

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.altintakipandroid.ui.theme.AccentOrange
import com.example.altintakipandroid.ui.theme.AppTheme
import com.example.altintakipandroid.ui.theme.Danger
import com.example.altintakipandroid.ui.theme.Success
import com.example.altintakipandroid.ui.theme.getAppTheme

/**
 * Contact style config (iOS ContactStyleConfig). Values match StyleManager.getContactConfig.
 */
data class ContactStyleConfig(
    val layoutType: ContactLayoutType = ContactLayoutType.CLASSIC,
    val logoPresentation: LogoPresentation = LogoPresentation.SHADOWED,
    val logoSize: Int = 180,
    val buttonRadius: Int = 12,
    val phoneButtonColor: Color = AccentOrange,
    val whatsappButtonColor: Color = Success,
    val instagramButtonColor: Color = Danger,
    val hasButtonShadow: Boolean = true,
    val hasButtonBorder: Boolean = false,
    val buttonLayout: ContactButtonLayout = ContactButtonLayout.VERTICAL,
    val buttonStyle: ContactButtonStyle = ContactButtonStyle.FULL,
    val infoCardStyle: InfoCardStyle = InfoCardStyle.ELEVATED,
    val spacing: ContactSpacing = ContactSpacing.NORMAL
) {
    val logoSizeDp: Dp get() = logoSize.dp
    val buttonRadiusDp: Dp get() = buttonRadius.dp
    val buttonGapDp: Dp get() = spacing.buttonGapDp
    val cardMarginBottomDp: Dp get() = spacing.cardMarginBottomDp
    val infoPaddingDp: Dp get() = spacing.infoPaddingDp
}

enum class LogoPresentation { PLAIN, FRAMED, SHADOWED, GRADIENT }

enum class InfoCardStyle { TRANSPARENT, ELEVATED, BORDERED, GRADIENT }

enum class ContactLayoutType {
    CLASSIC,  // style1: logo, title, info, buttons, directions
    COMPACT,  // style2: logo+title row, info, buttons grid
    SPLIT,    // style3: logo+title, buttons, info, directions
    MINIMAL,  // style4: logo+title compact, info, buttons horizontal
    HERO      // style5: logo+title, info, directions, buttons
}

enum class ContactButtonLayout {
    VERTICAL,
    HORIZONTAL,
    GRID,
    SPECIAL
}

enum class ContactButtonStyle {
    FULL,
    PILL,
    COMPACT,
    ICON_ONLY
}

enum class ContactSpacing(val buttonGap: Int, val cardMarginBottom: Int, val infoPadding: Int) {
    TIGHT(8, 16, 12),
    NORMAL(12, 24, 16),
    SPACIOUS(16, 32, 20);

    val buttonGapDp: Dp get() = buttonGap.dp
    val cardMarginBottomDp: Dp get() = cardMarginBottom.dp
    val infoPaddingDp: Dp get() = infoPadding.dp
}

fun getContactConfig(contactStyle: Int, cornerRadiusScale: Int, appTheme: AppTheme = getAppTheme(1)): ContactStyleConfig {
    return when (contactStyle) {
        1 -> ContactStyleConfig(
            layoutType = ContactLayoutType.CLASSIC,
            logoPresentation = LogoPresentation.SHADOWED,
            logoSize = 180,
            buttonRadius = cornerRadiusScale,
            phoneButtonColor = appTheme.accentColor,
            whatsappButtonColor = appTheme.success,
            instagramButtonColor = appTheme.danger,
            hasButtonShadow = true,
            buttonLayout = ContactButtonLayout.VERTICAL,
            buttonStyle = ContactButtonStyle.FULL,
            infoCardStyle = InfoCardStyle.ELEVATED,
            spacing = ContactSpacing.NORMAL
        )
        2 -> ContactStyleConfig(
            layoutType = ContactLayoutType.COMPACT,
            logoPresentation = LogoPresentation.FRAMED,
            logoSize = 110,
            buttonRadius = cornerRadiusScale + 4,
            phoneButtonColor = appTheme.accentColor,
            whatsappButtonColor = appTheme.success,
            instagramButtonColor = appTheme.accentColor,
            hasButtonShadow = true,
            buttonLayout = ContactButtonLayout.GRID,
            buttonStyle = ContactButtonStyle.PILL,
            infoCardStyle = InfoCardStyle.ELEVATED,
            spacing = ContactSpacing.NORMAL
        )
        3 -> ContactStyleConfig(
            layoutType = ContactLayoutType.SPLIT,
            logoPresentation = LogoPresentation.FRAMED,
            logoSize = 130,
            buttonRadius = cornerRadiusScale + 3,
            phoneButtonColor = appTheme.accentColor,
            whatsappButtonColor = appTheme.success,
            instagramButtonColor = appTheme.danger,
            hasButtonShadow = true,
            buttonLayout = ContactButtonLayout.HORIZONTAL,
            buttonStyle = ContactButtonStyle.COMPACT,
            infoCardStyle = InfoCardStyle.ELEVATED,
            spacing = ContactSpacing.NORMAL
        )
        4 -> ContactStyleConfig(
            layoutType = ContactLayoutType.MINIMAL,
            logoPresentation = LogoPresentation.FRAMED,
            logoSize = 150,
            buttonRadius = cornerRadiusScale + 6,
            phoneButtonColor = appTheme.accentColor,
            whatsappButtonColor = appTheme.success,
            instagramButtonColor = appTheme.danger,
            hasButtonShadow = true,
            buttonLayout = ContactButtonLayout.HORIZONTAL,
            buttonStyle = ContactButtonStyle.ICON_ONLY,
            infoCardStyle = InfoCardStyle.ELEVATED,
            spacing = ContactSpacing.NORMAL
        )
        5 -> ContactStyleConfig(
            layoutType = ContactLayoutType.HERO,
            logoPresentation = LogoPresentation.FRAMED,
            logoSize = 160,
            buttonRadius = cornerRadiusScale + 6,
            phoneButtonColor = appTheme.accentColor,
            whatsappButtonColor = appTheme.success,
            instagramButtonColor = appTheme.accentColor,
            hasButtonShadow = true,
            buttonLayout = ContactButtonLayout.SPECIAL,
            buttonStyle = ContactButtonStyle.FULL,
            infoCardStyle = InfoCardStyle.ELEVATED,
            spacing = ContactSpacing.NORMAL
        )
        else -> getContactConfig(1, cornerRadiusScale, appTheme)
    }
}
