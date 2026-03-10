package com.example.altintakipandroid.ui.contact

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.altintakipandroid.domain.AppInformationData
import com.example.altintakipandroid.domain.UIConfig
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.shadow
import kotlinx.coroutines.delay
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.ui.contact.ContactButtonLayout
import com.example.altintakipandroid.ui.contact.ContactButtonStyle
import com.example.altintakipandroid.ui.contact.ContactLayoutType
import com.example.altintakipandroid.ui.contact.ContactStyleConfig
import com.example.altintakipandroid.ui.contact.InfoCardStyle
import com.example.altintakipandroid.ui.contact.LogoPresentation
import com.example.altintakipandroid.ui.contact.getContactConfig
import com.example.altintakipandroid.ui.theme.LocalAppTheme
import com.example.altintakipandroid.ui.admin.AdminSheet
import com.example.altintakipandroid.ui.admin.PortalLoginViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.draw.shadow

@Composable
fun ContactScreen(
    config: UIConfig,
    appInfo: AppInformationData,
    onLogout: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showAdminSheet by remember { mutableStateOf(false) }
    var tapCount by remember { mutableStateOf(0) }
    val portalLoginViewModel: PortalLoginViewModel = viewModel()
    val appTheme = LocalAppTheme.current

    LaunchedEffect(tapCount) {
        if (tapCount in 1..4) {
            delay(5000)
            tapCount = 0
        }
    }

    val onLogoClick: () -> Unit = {
        tapCount++
        if (tapCount >= 5) {
            tapCount = 0
            showAdminSheet = true
        }
    }
    val contactConfig = remember(config.contactStyle, config.cornerRadiusScale, config.theme) {
        getContactConfig(config.contactStyle, config.cornerRadiusScale, appTheme)
    }
    val cardRadius = contactConfig.buttonRadiusDp

    fun openUrl(url: String?) {
        url?.takeIf { it.isNotBlank() }?.let { u ->
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(u)))
        }
    }

    fun openPhone(phone: String?) {
        phone?.takeIf { it.isNotBlank() }?.let { p ->
            val telUri = Uri.parse("tel:${p.replace(Regex("[^+0-9]"), "")}")
            context.startActivity(Intent(Intent.ACTION_DIAL, telUri))
        }
    }

    fun openWhatsApp(whatsapp: String?) {
        whatsapp?.takeIf { it.isNotBlank() }?.let { w ->
            val num = w.replace(Regex("[^0-9]"), "")
            val uri = Uri.parse("https://wa.me/$num")
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    ThemedView {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            when (contactConfig.layoutType) {
                ContactLayoutType.CLASSIC -> ContactClassicLayout(
                    appInfo = appInfo,
                    contactConfig = contactConfig,
                    cardRadius = cardRadius,
                    openUrl = ::openUrl,
                    openPhone = ::openPhone,
                    openWhatsApp = ::openWhatsApp,
                    onLogoClick = onLogoClick,
                    onLogout = onLogout,
                    showResetCodeButton = config.isHasChangeRouter == true
                )
                ContactLayoutType.COMPACT -> ContactCompactLayout(
                    appInfo = appInfo,
                    contactConfig = contactConfig,
                    cardRadius = cardRadius,
                    openUrl = ::openUrl,
                    openPhone = ::openPhone,
                    openWhatsApp = ::openWhatsApp,
                    onLogoClick = onLogoClick,
                    onLogout = onLogout,
                    showResetCodeButton = config.isHasChangeRouter == true
                )
                ContactLayoutType.SPLIT -> ContactSplitLayout(
                    appInfo = appInfo,
                    contactConfig = contactConfig,
                    cardRadius = cardRadius,
                    openUrl = ::openUrl,
                    openPhone = ::openPhone,
                    openWhatsApp = ::openWhatsApp,
                    onLogoClick = onLogoClick,
                    onLogout = onLogout,
                    showResetCodeButton = config.isHasChangeRouter == true
                )
                ContactLayoutType.MINIMAL -> ContactMinimalLayout(
                    appInfo = appInfo,
                    contactConfig = contactConfig,
                    cardRadius = cardRadius,
                    openUrl = ::openUrl,
                    openPhone = ::openPhone,
                    openWhatsApp = ::openWhatsApp,
                    onLogoClick = onLogoClick,
                    onLogout = onLogout,
                    showResetCodeButton = config.isHasChangeRouter == true
                )
                ContactLayoutType.HERO -> ContactHeroLayout(
                    appInfo = appInfo,
                    contactConfig = contactConfig,
                    cardRadius = cardRadius,
                    openUrl = ::openUrl,
                    openPhone = ::openPhone,
                    openWhatsApp = ::openWhatsApp,
                    onLogoClick = onLogoClick,
                    onLogout = onLogout,
                    showResetCodeButton = config.isHasChangeRouter == true
                )
            }
        }
    }

    if (showAdminSheet) {
        AdminSheet(
            viewModel = portalLoginViewModel,
            onDismiss = {
                showAdminSheet = false
                portalLoginViewModel.clearMessages()
            }
        )
    }
}

@Composable
private fun ContactRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit,
    cornerRadius: Dp = 12.dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                ThemedText(text = label, style = MaterialTheme.typography.labelMedium)
                ThemedText(text = value, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ContactResetCodeButton(cardRadius: Dp, onResetCode: () -> Unit) {
    OutlinedButton(
        onClick = onResetCode,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cardRadius),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
    ) {
        Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.width(8.dp))
        ThemedText(text = "Yeniden Kod Gir", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ContactInfoCard(
    appInfo: AppInformationData,
    contactConfig: ContactStyleConfig,
    cardRadius: Dp
) {
    val appTheme = LocalAppTheme.current
    val radiusDp = cardRadius + 2.dp
    val shape = RoundedCornerShape(radiusDp)
    val backgroundColor = when (contactConfig.infoCardStyle) {
        InfoCardStyle.TRANSPARENT -> appTheme.surfaceElevatedColor.copy(alpha = 0.5f)
        InfoCardStyle.ELEVATED -> appTheme.surfaceElevatedColor
        InfoCardStyle.BORDERED -> appTheme.surfaceElevatedColor.copy(alpha = 0.56f)
        InfoCardStyle.GRADIENT -> appTheme.surfaceElevatedColor.copy(alpha = 0.56f)
    }
    val border = when (contactConfig.infoCardStyle) {
        InfoCardStyle.TRANSPARENT -> BorderStroke(1.dp, appTheme.separator)
        InfoCardStyle.BORDERED -> BorderStroke(2.dp, appTheme.accentColor)
        InfoCardStyle.GRADIENT -> BorderStroke(1.5.dp, appTheme.accentColor.copy(alpha = 0.3f))
        else -> null
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (contactConfig.infoCardStyle == InfoCardStyle.ELEVATED) Modifier.shadow(8.dp, shape) else Modifier),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp),
        border = border
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contactConfig.infoPaddingDp)
        ) {
            ContactInfoRow(Icons.Outlined.Phone, appInfo.contactPhone ?: "Bilinmiyor", appTheme)
            Spacer(modifier = Modifier.height(18.dp))
            ContactInfoRow(Icons.Outlined.LocationOn, appInfo.contactAddress ?: "Bilinmiyor", appTheme)
            Spacer(modifier = Modifier.height(18.dp))
            ContactInfoRow(Icons.Outlined.Email, appInfo.contactEmail ?: "info@altintakip.com", appTheme)
        }
    }
}

@Composable
private fun ContactInfoRow(icon: ImageVector, text: String, appTheme: com.example.altintakipandroid.ui.theme.AppTheme) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = appTheme.accentColor)
        ThemedText(text = text, style = MaterialTheme.typography.bodyMedium, isSecondary = true, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ContactActionButton(
    icon: ImageVector,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    contactConfig: ContactStyleConfig,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val isPill = contactConfig.buttonStyle == ContactButtonStyle.PILL
    val shape = if (isPill) RoundedCornerShape(50) else RoundedCornerShape(contactConfig.buttonRadiusDp)
    val isIconOnly = contactConfig.buttonStyle == ContactButtonStyle.ICON_ONLY
    val isCompact = contactConfig.buttonStyle == ContactButtonStyle.COMPACT
    val white = androidx.compose.ui.graphics.Color.White
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        elevation = if (contactConfig.hasButtonShadow) ButtonDefaults.buttonElevation(8.dp) else ButtonDefaults.buttonElevation(0.dp),
        contentPadding = when (contactConfig.buttonStyle) {
            ContactButtonStyle.FULL -> androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp, horizontal = 20.dp)
            ContactButtonStyle.PILL -> androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp, horizontal = 24.dp)
            ContactButtonStyle.COMPACT -> androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp, horizontal = 16.dp)
            ContactButtonStyle.ICON_ONLY -> androidx.compose.foundation.layout.PaddingValues(0.dp)
        }
    ) {
        if (isCompact) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(32.dp), tint = white)
                if (!isIconOnly) ThemedText(text = label, fontWeight = FontWeight.Bold, color = white, style = MaterialTheme.typography.bodySmall)
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(if (isIconOnly) 36.dp else 24.dp), tint = white)
                if (!isIconOnly) ThemedText(text = label, fontWeight = FontWeight.Bold, color = white)
            }
        }
    }
}

@Composable
private fun ContactButtonsView(
    appInfo: AppInformationData,
    contactConfig: ContactStyleConfig,
    openPhone: (String?) -> Unit,
    openWhatsApp: (String?) -> Unit,
    openUrl: (String?) -> Unit
) {
    val gap = contactConfig.buttonGapDp

    when (contactConfig.buttonLayout) {
        ContactButtonLayout.VERTICAL -> Column(verticalArrangement = Arrangement.spacedBy(gap)) {
            ContactActionButton(Icons.Outlined.Phone, "Ara", contactConfig.phoneButtonColor, contactConfig, Modifier.fillMaxWidth()) { appInfo.contactPhone?.let { openPhone(it) } }
            ContactActionButton(Icons.AutoMirrored.Outlined.Message, "WhatsApp", contactConfig.whatsappButtonColor, contactConfig, Modifier.fillMaxWidth()) { appInfo.contactWhatsapp?.let { openWhatsApp(it) } }
            ContactActionButton(Icons.Outlined.CameraAlt, "Instagram", contactConfig.instagramButtonColor, contactConfig, Modifier.fillMaxWidth()) { appInfo.contactInstagram?.let { openUrl(it) } }
        }
        ContactButtonLayout.SPECIAL -> Column(verticalArrangement = Arrangement.spacedBy(gap)) {
            ContactActionButton(Icons.Outlined.CameraAlt, "Instagram", contactConfig.instagramButtonColor, contactConfig, Modifier.fillMaxWidth()) { appInfo.contactInstagram?.let { openUrl(it) } }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                ContactActionButton(Icons.Outlined.Phone, "Ara", contactConfig.phoneButtonColor, contactConfig, Modifier.weight(1f)) { appInfo.contactPhone?.let { openPhone(it) } }
                ContactActionButton(Icons.AutoMirrored.Outlined.Message, "WhatsApp", contactConfig.whatsappButtonColor, contactConfig, Modifier.weight(1f)) { appInfo.contactWhatsapp?.let { openWhatsApp(it) } }
            }
        }
        ContactButtonLayout.GRID -> Column(verticalArrangement = Arrangement.spacedBy(gap)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                ContactActionButton(Icons.Outlined.Phone, "Ara", contactConfig.phoneButtonColor, contactConfig, Modifier.weight(1f)) { appInfo.contactPhone?.let { openPhone(it) } }
                ContactActionButton(Icons.AutoMirrored.Outlined.Message, "WhatsApp", contactConfig.whatsappButtonColor, contactConfig, Modifier.weight(1f)) { appInfo.contactWhatsapp?.let { openWhatsApp(it) } }
            }
            ContactActionButton(Icons.Outlined.CameraAlt, "Instagram", contactConfig.instagramButtonColor, contactConfig, Modifier.fillMaxWidth()) { appInfo.contactInstagram?.let { openUrl(it) } }
        }
        ContactButtonLayout.HORIZONTAL -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
            ContactActionButton(Icons.Outlined.Phone, "Ara", contactConfig.phoneButtonColor, contactConfig, Modifier.weight(1f)) { appInfo.contactPhone?.let { openPhone(it) } }
            ContactActionButton(Icons.AutoMirrored.Outlined.Message, "WhatsApp", contactConfig.whatsappButtonColor, contactConfig, Modifier.weight(1f)) { appInfo.contactWhatsapp?.let { openWhatsApp(it) } }
            ContactActionButton(Icons.Outlined.CameraAlt, "Instagram", contactConfig.instagramButtonColor, contactConfig, Modifier.weight(1f)) { appInfo.contactInstagram?.let { openUrl(it) } }
        }
    }
}

@Composable
private fun ContactDirectionsButton(
    appInfo: AppInformationData,
    contactConfig: ContactStyleConfig,
    openUrl: (String?) -> Unit,
    isHero: Boolean
) {
    val appTheme = LocalAppTheme.current
    OutlinedButton(
        onClick = { appInfo.contactMapsUrl?.let { openUrl(it) } },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(contactConfig.buttonRadiusDp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        border = BorderStroke(1.dp, appTheme.separator)
    ) {
        Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.width(8.dp))
        ThemedText(text = "Yol Tarifi Al", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ContactClassicLayout(
    appInfo: AppInformationData,
    contactConfig: ContactStyleConfig,
    cardRadius: Dp,
    openUrl: (String?) -> Unit,
    openPhone: (String?) -> Unit,
    openWhatsApp: (String?) -> Unit,
    onLogoClick: () -> Unit,
    onLogout: (() -> Unit)?,
    showResetCodeButton: Boolean
) {
    ContactLogoCard(appInfo = appInfo, contactConfig = contactConfig, fullWidth = true, onLogoClick = onLogoClick)
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ThemedText(
        text = appInfo.contactTitle ?: appInfo.navigationTitle ?: "İletişim",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ContactInfoCard(appInfo, contactConfig, cardRadius)
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ContactButtonsView(appInfo, contactConfig, openPhone, openWhatsApp, openUrl)
    Spacer(modifier = Modifier.height(contactConfig.buttonGapDp))
    ContactDirectionsButton(appInfo, contactConfig, openUrl, isHero = false)
    Spacer(modifier = Modifier.height(12.dp))
    if (showResetCodeButton && onLogout != null) {
        Spacer(modifier = Modifier.height(8.dp))
        ContactResetCodeButton(cardRadius = cardRadius, onResetCode = onLogout)
    }
}

@Composable
private fun ContactCompactLayout(
    appInfo: AppInformationData,
    contactConfig: ContactStyleConfig,
    cardRadius: Dp,
    openUrl: (String?) -> Unit,
    openPhone: (String?) -> Unit,
    openWhatsApp: (String?) -> Unit,
    onLogoClick: () -> Unit,
    onLogout: (() -> Unit)?,
    showResetCodeButton: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ContactLogoCard(appInfo = appInfo, contactConfig = contactConfig, fullWidth = false, onLogoClick = onLogoClick)
        ThemedText(
            text = appInfo.contactTitle ?: appInfo.navigationTitle ?: "İletişim",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ContactInfoCard(appInfo, contactConfig, cardRadius)
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ContactButtonsView(appInfo, contactConfig, openPhone, openWhatsApp, openUrl)
    Spacer(modifier = Modifier.height(contactConfig.buttonGapDp))
    ContactDirectionsButton(appInfo, contactConfig, openUrl, isHero = false)
    Spacer(modifier = Modifier.height(12.dp))
    if (showResetCodeButton && onLogout != null) {
        Spacer(modifier = Modifier.height(8.dp))
        ContactResetCodeButton(cardRadius = cardRadius, onResetCode = onLogout)
    }
}

@Composable
private fun ContactSplitLayout(
    appInfo: AppInformationData,
    contactConfig: ContactStyleConfig,
    cardRadius: Dp,
    openUrl: (String?) -> Unit,
    openPhone: (String?) -> Unit,
    openWhatsApp: (String?) -> Unit,
    onLogoClick: () -> Unit,
    onLogout: (() -> Unit)?,
    showResetCodeButton: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ContactLogoCard(appInfo = appInfo, contactConfig = contactConfig, fullWidth = true, onLogoClick = onLogoClick)
        ThemedText(
            text = appInfo.navigationTitle ?: "İletişim",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ContactButtonsView(appInfo, contactConfig, openPhone, openWhatsApp, openUrl)
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ContactInfoCard(appInfo, contactConfig, cardRadius)
    Spacer(modifier = Modifier.height(contactConfig.buttonGapDp))
    ContactDirectionsButton(appInfo, contactConfig, openUrl, isHero = false)
    Spacer(modifier = Modifier.height(12.dp))
    if (showResetCodeButton && onLogout != null) {
        Spacer(modifier = Modifier.height(8.dp))
        ContactResetCodeButton(cardRadius = cardRadius, onResetCode = onLogout)
    }
}

@Composable
private fun ContactMinimalLayout(
    appInfo: AppInformationData,
    contactConfig: ContactStyleConfig,
    cardRadius: Dp,
    openUrl: (String?) -> Unit,
    openPhone: (String?) -> Unit,
    openWhatsApp: (String?) -> Unit,
    onLogoClick: () -> Unit,
    onLogout: (() -> Unit)?,
    showResetCodeButton: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ContactLogoCard(appInfo = appInfo, contactConfig = contactConfig, fullWidth = true, onLogoClick = onLogoClick)
        ThemedText(
            text = appInfo.contactTitle ?: appInfo.navigationTitle ?: "İletişim",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
    ContactInfoCard(appInfo, contactConfig, cardRadius)
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ContactButtonsView(appInfo, contactConfig, openPhone, openWhatsApp, openUrl)
    Spacer(modifier = Modifier.height(contactConfig.buttonGapDp))
    ContactDirectionsButton(appInfo, contactConfig, openUrl, isHero = false)
    Spacer(modifier = Modifier.height(12.dp))
    if (showResetCodeButton && onLogout != null) {
        Spacer(modifier = Modifier.height(8.dp))
        ContactResetCodeButton(cardRadius = cardRadius, onResetCode = onLogout)
    }
}

@Composable
private fun ContactHeroLayout(
    appInfo: AppInformationData,
    contactConfig: ContactStyleConfig,
    cardRadius: Dp,
    openUrl: (String?) -> Unit,
    openPhone: (String?) -> Unit,
    openWhatsApp: (String?) -> Unit,
    onLogoClick: () -> Unit,
    onLogout: (() -> Unit)?,
    showResetCodeButton: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ContactLogoCard(appInfo = appInfo, contactConfig = contactConfig, fullWidth = true, onLogoClick = onLogoClick)
        ThemedText(
            text = appInfo.contactTitle ?: appInfo.navigationTitle ?: "İletişim",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ContactInfoCard(appInfo, contactConfig, cardRadius)
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ContactDirectionsButton(appInfo, contactConfig, openUrl, isHero = true)
    Spacer(modifier = Modifier.height(contactConfig.buttonGapDp))
    ContactButtonsView(appInfo, contactConfig, openPhone, openWhatsApp, openUrl)
    Spacer(modifier = Modifier.height(12.dp))
    if (showResetCodeButton && onLogout != null) {
        Spacer(modifier = Modifier.height(8.dp))
        ContactResetCodeButton(cardRadius = cardRadius, onResetCode = onLogout)
    }
}

@Composable
private fun ContactLogoCard(
    appInfo: AppInformationData,
    contactConfig: ContactStyleConfig,
    fullWidth: Boolean = true,
    onLogoClick: (() -> Unit)? = null
) {
    val url = appInfo.contactImage?.takeIf { it.isNotBlank() } ?: appInfo.navigationIcon?.takeIf { it.isNotBlank() }
    if (url == null) return
    val context = LocalContext.current
    val appTheme = LocalAppTheme.current
    val sizeDp = contactConfig.logoSizeDp
    val circleShape = CircleShape
    val strokeWidth = when (contactConfig.logoPresentation) {
        LogoPresentation.GRADIENT, LogoPresentation.FRAMED -> 4.dp
        else -> 3.dp
    }
    val shadowModifier = when (contactConfig.logoPresentation) {
        LogoPresentation.FRAMED -> Modifier.shadow(4.dp, circleShape)
        LogoPresentation.SHADOWED -> Modifier.shadow(12.dp, circleShape)
        LogoPresentation.GRADIENT -> Modifier.shadow(16.dp, circleShape)
        LogoPresentation.PLAIN -> Modifier
    }
    // Fill the circle and crop if needed (sığdır)
    val contentScale = ContentScale.Crop
    val logoContent = @Composable {
        Box(
            modifier = Modifier
                .size(sizeDp)
                .clip(circleShape)
                .then(
                    if (onLogoClick != null) Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onLogoClick
                    ) else Modifier
                )
                .then(shadowModifier)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(circleShape)
                    .background(androidx.compose.ui.graphics.Color.White)
            )
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = appInfo.contactTitle,
                contentScale = contentScale,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(circleShape)
            )
            // Only add border when logo is not already heavily framed (PLAIN/SHADOWED); FRAMED/GRADIENT assets often have their own ring
            if (contactConfig.logoPresentation != LogoPresentation.FRAMED) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(circleShape)
                        .border(strokeWidth, appTheme.accentColor, circleShape)
                )
            }
        }
    }
    if (contactConfig.logoPresentation == LogoPresentation.GRADIENT) {
        Box(
            modifier = if (fullWidth) Modifier.fillMaxWidth() else Modifier,
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(sizeDp + 10.dp)
                    .clip(circleShape)
                    .background(appTheme.surfaceElevatedColor.copy(alpha = 0.125f)),
                contentAlignment = Alignment.Center
            ) {
                logoContent()
            }
        }
    } else {
        Box(
            modifier = if (fullWidth) Modifier.fillMaxWidth() else Modifier,
            contentAlignment = Alignment.Center
        ) {
            logoContent()
        }
    }
}

@Composable
private fun ContactContentRows(
    appInfo: AppInformationData,
    cardRadius: Dp,
    openUrl: (String?) -> Unit,
    openPhone: (String?) -> Unit,
    openWhatsApp: (String?) -> Unit
) {
    appInfo.contactPhone?.takeIf { it.isNotBlank() }?.let { phone ->
        ContactRow(Icons.Outlined.Phone, "Telefon", phone, { openPhone(phone) }, cardRadius)
        Spacer(modifier = Modifier.height(12.dp))
    }
    appInfo.contactEmail?.takeIf { it.isNotBlank() }?.let { email ->
        ContactRow(Icons.Outlined.Email, "E-posta", email, { openUrl("mailto:$email") }, cardRadius)
        Spacer(modifier = Modifier.height(12.dp))
    }
    appInfo.contactAddress?.takeIf { it.isNotBlank() }?.let { address ->
        ContactRow(
            Icons.Outlined.LocationOn,
            "Adres",
            address,
            { appInfo.contactMapsUrl?.takeIf { u -> u.isNotBlank() }?.let { openUrl(it) } },
            cardRadius
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
    appInfo.contactWhatsapp?.takeIf { it.isNotBlank() }?.let { whatsapp ->
        ContactRow(Icons.Outlined.Phone, "WhatsApp", whatsapp, { openWhatsApp(whatsapp) }, cardRadius)
        Spacer(modifier = Modifier.height(12.dp))
    }
    appInfo.contactInstagram?.takeIf { it.isNotBlank() }?.let { instagram ->
        ContactRow(Icons.Outlined.Link, "Instagram", instagram, { openUrl(instagram) }, cardRadius)
        Spacer(modifier = Modifier.height(12.dp))
    }
    appInfo.contactMapsUrl?.takeIf { it.isNotBlank() }?.let { mapsUrl ->
        if (appInfo.contactAddress.isNullOrBlank()) {
            ContactRow(Icons.Outlined.LocationOn, "Haritada aç", "Konumu görüntüle", { openUrl(mapsUrl) }, cardRadius)
        }
    }
}
