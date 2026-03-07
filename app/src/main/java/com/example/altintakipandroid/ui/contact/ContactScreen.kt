package com.example.altintakipandroid.ui.contact

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.automirrored.outlined.Logout
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.altintakipandroid.domain.AppInformationData
import com.example.altintakipandroid.domain.UIConfig
import com.example.altintakipandroid.ui.admin.PortalLoginDialog
import com.example.altintakipandroid.ui.admin.PortalLoginViewModel
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.ui.contact.ContactLayoutType
import com.example.altintakipandroid.ui.contact.ContactStyleConfig
import com.example.altintakipandroid.ui.contact.getContactConfig
import com.example.altintakipandroid.ui.theme.LocalAppTheme

@Composable
fun ContactScreen(
    config: UIConfig,
    appInfo: AppInformationData,
    onLogout: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showPortalLogin by remember { mutableStateOf(false) }
    val portalLoginViewModel: PortalLoginViewModel = viewModel()
    val appTheme = LocalAppTheme.current
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
                    onPortalLogin = { showPortalLogin = true },
                    onLogout = onLogout,
                    showPortalRow = config.allowHiddenAdmin != false,
                    showResetCodeButton = config.isHasChangeRouter == true
                )
                ContactLayoutType.COMPACT -> ContactCompactLayout(
                    appInfo = appInfo,
                    contactConfig = contactConfig,
                    cardRadius = cardRadius,
                    openUrl = ::openUrl,
                    openPhone = ::openPhone,
                    openWhatsApp = ::openWhatsApp,
                    onPortalLogin = { showPortalLogin = true },
                    onLogout = onLogout,
                    showPortalRow = config.allowHiddenAdmin != false,
                    showResetCodeButton = config.isHasChangeRouter == true
                )
                ContactLayoutType.SPLIT -> ContactSplitLayout(
                    appInfo = appInfo,
                    contactConfig = contactConfig,
                    cardRadius = cardRadius,
                    openUrl = ::openUrl,
                    openPhone = ::openPhone,
                    openWhatsApp = ::openWhatsApp,
                    onPortalLogin = { showPortalLogin = true },
                    onLogout = onLogout,
                    showPortalRow = config.allowHiddenAdmin != false,
                    showResetCodeButton = config.isHasChangeRouter == true
                )
                ContactLayoutType.MINIMAL -> ContactMinimalLayout(
                    appInfo = appInfo,
                    contactConfig = contactConfig,
                    cardRadius = cardRadius,
                    openUrl = ::openUrl,
                    openPhone = ::openPhone,
                    openWhatsApp = ::openWhatsApp,
                    onPortalLogin = { showPortalLogin = true },
                    onLogout = onLogout,
                    showPortalRow = config.allowHiddenAdmin != false,
                    showResetCodeButton = config.isHasChangeRouter == true
                )
                ContactLayoutType.HERO -> ContactHeroLayout(
                    appInfo = appInfo,
                    contactConfig = contactConfig,
                    cardRadius = cardRadius,
                    openUrl = ::openUrl,
                    openPhone = ::openPhone,
                    openWhatsApp = ::openWhatsApp,
                    onPortalLogin = { showPortalLogin = true },
                    onLogout = onLogout,
                    showPortalRow = config.allowHiddenAdmin != false,
                    showResetCodeButton = config.isHasChangeRouter == true
                )
            }
        }
    }

    if (showPortalLogin) {
        PortalLoginDialog(
            viewModel = portalLoginViewModel,
            onDismiss = {
                showPortalLogin = false
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
    androidx.compose.material3.OutlinedButton(
        onClick = onResetCode,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cardRadius),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
    ) {
        Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.width(8.dp))
        ThemedText(text = "Yeniden Kod Gir", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
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
    onPortalLogin: () -> Unit,
    onLogout: (() -> Unit)?,
    showPortalRow: Boolean,
    showResetCodeButton: Boolean
) {
    ContactLogoCard(appInfo = appInfo, sizeDp = contactConfig.logoSizeDp, cornerRadius = cardRadius)
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ThemedText(
        text = appInfo.contactTitle ?: appInfo.navigationTitle ?: "İletişim",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ContactContentRows(appInfo, cardRadius, openUrl, openPhone, openWhatsApp)
    Spacer(modifier = Modifier.height(12.dp))
    if (showPortalRow) ContactRow(Icons.Outlined.Lock, "Portal / Admin Girişi", "Yönetim paneline giriş", onPortalLogin, cardRadius)
    onLogout?.let { logout ->
        Spacer(modifier = Modifier.height(12.dp))
        ContactRow(Icons.AutoMirrored.Outlined.Logout, "Çıkış Yap", "Hesabı kapat ve aktivasyon ekranına dön", logout, cardRadius)
    }
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
    onPortalLogin: () -> Unit,
    onLogout: (() -> Unit)?,
    showPortalRow: Boolean,
    showResetCodeButton: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ContactLogoCard(appInfo = appInfo, sizeDp = contactConfig.logoSizeDp, cornerRadius = cardRadius, fullWidth = false)
        ThemedText(
            text = appInfo.contactTitle ?: appInfo.navigationTitle ?: "İletişim",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ContactContentRows(appInfo, cardRadius, openUrl, openPhone, openWhatsApp)
    Spacer(modifier = Modifier.height(12.dp))
    if (showPortalRow) ContactRow(Icons.Outlined.Lock, "Portal / Admin Girişi", "Yönetim paneline giriş", onPortalLogin, cardRadius)
    onLogout?.let { logout ->
        Spacer(modifier = Modifier.height(12.dp))
        ContactRow(Icons.AutoMirrored.Outlined.Logout, "Çıkış Yap", "Hesabı kapat ve aktivasyon ekranına dön", logout, cardRadius)
    }
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
    onPortalLogin: () -> Unit,
    onLogout: (() -> Unit)?,
    showPortalRow: Boolean,
    showResetCodeButton: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ContactLogoCard(appInfo = appInfo, sizeDp = contactConfig.logoSizeDp, cornerRadius = cardRadius)
        ThemedText(
            text = appInfo.navigationTitle ?: "İletişim",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ContactContentRows(appInfo, cardRadius, openUrl, openPhone, openWhatsApp)
    Spacer(modifier = Modifier.height(12.dp))
    if (showPortalRow) ContactRow(Icons.Outlined.Lock, "Portal / Admin Girişi", "Yönetim paneline giriş", onPortalLogin, cardRadius)
    onLogout?.let { logout ->
        Spacer(modifier = Modifier.height(12.dp))
        ContactRow(Icons.AutoMirrored.Outlined.Logout, "Çıkış Yap", "Hesabı kapat ve aktivasyon ekranına dön", logout, cardRadius)
    }
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
    onPortalLogin: () -> Unit,
    onLogout: (() -> Unit)?,
    showPortalRow: Boolean,
    showResetCodeButton: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ContactLogoCard(appInfo = appInfo, sizeDp = contactConfig.logoSizeDp, cornerRadius = cardRadius)
        ThemedText(
            text = appInfo.contactTitle ?: appInfo.navigationTitle ?: "İletişim",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
    ContactContentRows(appInfo, cardRadius, openUrl, openPhone, openWhatsApp)
    Spacer(modifier = Modifier.height(12.dp))
    if (showPortalRow) ContactRow(Icons.Outlined.Lock, "Portal / Admin Girişi", "Yönetim paneline giriş", onPortalLogin, cardRadius)
    onLogout?.let { logout ->
        Spacer(modifier = Modifier.height(12.dp))
        ContactRow(Icons.AutoMirrored.Outlined.Logout, "Çıkış Yap", "Hesabı kapat ve aktivasyon ekranına dön", logout, cardRadius)
    }
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
    onPortalLogin: () -> Unit,
    onLogout: (() -> Unit)?,
    showPortalRow: Boolean,
    showResetCodeButton: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ContactLogoCard(appInfo = appInfo, sizeDp = contactConfig.logoSizeDp, cornerRadius = cardRadius)
        ThemedText(
            text = appInfo.contactTitle ?: appInfo.navigationTitle ?: "İletişim",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(contactConfig.cardMarginBottomDp))
    ContactContentRows(appInfo, cardRadius, openUrl, openPhone, openWhatsApp)
    Spacer(modifier = Modifier.height(12.dp))
    if (showPortalRow) ContactRow(Icons.Outlined.Lock, "Portal / Admin Girişi", "Yönetim paneline giriş", onPortalLogin, cardRadius)
    onLogout?.let { logout ->
        Spacer(modifier = Modifier.height(12.dp))
        ContactRow(Icons.AutoMirrored.Outlined.Logout, "Çıkış Yap", "Hesabı kapat ve aktivasyon ekranına dön", logout, cardRadius)
    }
    if (showResetCodeButton && onLogout != null) {
        Spacer(modifier = Modifier.height(8.dp))
        ContactResetCodeButton(cardRadius = cardRadius, onResetCode = onLogout)
    }
}

@Composable
private fun ContactLogoCard(appInfo: AppInformationData, sizeDp: Dp, cornerRadius: Dp, fullWidth: Boolean = true) {
    val url = appInfo.contactImage?.takeIf { it.isNotBlank() } ?: appInfo.navigationIcon?.takeIf { it.isNotBlank() }
    if (url == null) return
    val mod = if (fullWidth) Modifier.fillMaxWidth().height(sizeDp) else Modifier.size(sizeDp)
    Card(
        modifier = mod,
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(model = url, contentDescription = appInfo.contactTitle, modifier = Modifier.fillMaxSize())
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
