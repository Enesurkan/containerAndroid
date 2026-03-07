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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.altintakipandroid.domain.AppInformationData
import com.example.altintakipandroid.ui.admin.PortalLoginDialog
import com.example.altintakipandroid.ui.admin.PortalLoginViewModel
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.ui.theme.SurfaceElevated

@Composable
fun ContactScreen(appInfo: AppInformationData, onLogout: (() -> Unit)? = null) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showPortalLogin by remember { mutableStateOf(false) }
    val portalLoginViewModel: PortalLoginViewModel = viewModel()

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
            appInfo.contactImage?.takeIf { it.isNotBlank() }?.let { url ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    AsyncImage(
                        model = url,
                        contentDescription = appInfo.contactTitle,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            ThemedText(
                text = appInfo.contactTitle ?: "İletişim",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            appInfo.contactPhone?.takeIf { it.isNotBlank() }?.let { phone ->
                ContactRow(
                    icon = Icons.Outlined.Phone,
                    label = "Telefon",
                    value = phone,
                    onClick = { openPhone(phone) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            appInfo.contactEmail?.takeIf { it.isNotBlank() }?.let { email ->
                ContactRow(
                    icon = Icons.Outlined.Email,
                    label = "E-posta",
                    value = email,
                    onClick = { openUrl("mailto:$email") }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            appInfo.contactAddress?.takeIf { it.isNotBlank() }?.let { address ->
                ContactRow(
                    icon = Icons.Outlined.LocationOn,
                    label = "Adres",
                    value = address,
                    onClick = { appInfo.contactMapsUrl?.takeIf { u -> u.isNotBlank() }?.let { openUrl(it) } }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            appInfo.contactWhatsapp?.takeIf { it.isNotBlank() }?.let { whatsapp ->
                ContactRow(
                    icon = Icons.Outlined.Phone,
                    label = "WhatsApp",
                    value = whatsapp,
                    onClick = { openWhatsApp(whatsapp) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            appInfo.contactInstagram?.takeIf { it.isNotBlank() }?.let { instagram ->
                ContactRow(
                    icon = Icons.Outlined.Link,
                    label = "Instagram",
                    value = instagram,
                    onClick = { openUrl(instagram) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            appInfo.contactMapsUrl?.takeIf { it.isNotBlank() }?.let { mapsUrl ->
                if (appInfo.contactAddress.isNullOrBlank()) {
                    ContactRow(
                        icon = Icons.Outlined.LocationOn,
                        label = "Haritada aç",
                        value = "Konumu görüntüle",
                        onClick = { openUrl(mapsUrl) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            ContactRow(
                icon = Icons.Outlined.Lock,
                label = "Portal / Admin Girişi",
                value = "Yönetim paneline giriş",
                onClick = { showPortalLogin = true }
            )

            onLogout?.let { logout ->
                Spacer(modifier = Modifier.height(12.dp))
                ContactRow(
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    label = "Çıkış Yap",
                    value = "Hesabı kapat ve aktivasyon ekranına dön",
                    onClick = logout
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
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
