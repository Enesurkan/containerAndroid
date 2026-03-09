package com.example.altintakipandroid.ui.admin

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.domain.GoldMultiplier
import com.example.altintakipandroid.ui.components.ThemedText
import com.example.altintakipandroid.ui.components.ThemedView
import com.example.altintakipandroid.ui.theme.LocalAppTheme
import kotlinx.coroutines.launch

/**
 * Full-screen admin flow (iOS AdminLoginView + RatesSettingsGateView).
 * Opened by 5 taps on contact logo. Shows login form, then on success Yönetici Paneli with Kur Ayarla and Güvenli Çıkış.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSheet(
    viewModel: PortalLoginViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var isLoggedIn by remember { mutableStateOf(false) }
    var showGoldMultipliers by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val prefs = remember(context) { PreferencesManager(context.applicationContext) }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            isLoggedIn = true
            viewModel.clearMessages()
        }
    }

    when {
        showGoldMultipliers -> GoldMultipliersScreen(
            onBack = { showGoldMultipliers = false }
        )
        isLoggedIn -> RatesSettingsGateScreen(
            onDismiss = onDismiss,
            onLogout = {
                scope.launch {
                    prefs.clearPortalCredentials()
                    isLoggedIn = false
                }
                onDismiss()
            },
            onKurAyarla = { showGoldMultipliers = true }
        )
        else -> AdminLoginFormScreen(
            viewModel = viewModel,
            state = state,
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminLoginFormScreen(
    viewModel: PortalLoginViewModel,
    state: PortalLoginState,
    onDismiss: () -> Unit
) {
    val appTheme = LocalAppTheme.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { ThemedText(text = "Portal Girişi", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onDismiss) {
                        Text("Kapat", color = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appTheme.surfaceColor)
            )
        }
    ) { padding ->
        ThemedView {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Outlined.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = appTheme.accentColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ThemedText(text = "Yönetici Girişi", style = MaterialTheme.typography.headlineSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    ThemedText(text = "Portal erişimi için kimlik bilgilerinizi girin.", isSecondary = true, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = state.username,
                    onValueChange = viewModel::setUsername,
                    label = { Text("Kullanıcı Adı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::setPassword,
                    label = { Text("Şifre") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                state.errorMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(8.dp))
                    ThemedText(text = msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Giriş Yap", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RatesSettingsGateScreen(
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
    onKurAyarla: () -> Unit
) {
    val appTheme = LocalAppTheme.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    TextButton(onClick = onDismiss) {
                        Text("Kapat", color = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appTheme.surfaceColor)
            )
        }
    ) { padding ->
        ThemedView {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        ThemedText(text = "Hoş Geldiniz", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        ThemedText(text = "Yönetici Paneli", isSecondary = true, style = MaterialTheme.typography.bodyMedium)
                    }
                    Icon(
                        imageVector = Icons.Outlined.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = appTheme.accentColor
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = appTheme.separator)
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onKurAyarla),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = appTheme.surfaceElevatedColor),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .background(appTheme.accentColor, RoundedCornerShape(12.dp)),
                            tint = androidx.compose.ui.graphics.Color.White
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            ThemedText(text = "Kur Ayarla", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            ThemedText(text = "Altın/Döviz çarpanlarını güncelleyin", isSecondary = true, style = MaterialTheme.typography.bodySmall)
                        }
                        Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = null, tint = appTheme.accentColor)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = appTheme.danger.copy(alpha = 0.2f),
                        contentColor = appTheme.danger
                    )
                ) {
                    Text("Güvenli Çıkış", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoldMultipliersScreen(onBack: () -> Unit) {
    val viewModel: GoldMultipliersViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val appTheme = LocalAppTheme.current

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    if (state.showSuccess) {
        AlertDialog(
            onDismissRequest = { },
            title = { ThemedText(text = "Başarılı", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
            text = { ThemedText(text = "Kurlar başarıyla kaydedildi.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearSuccess()
                    onBack()
                }) {
                    Text("Tamam", color = appTheme.accentColor, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
        )
    }

    if (state.saveErrorMessage != null) {
        val msg = state.saveErrorMessage!!
        val displayMsg = if (msg.length > 500) msg.take(500) + "…" else msg
        AlertDialog(
            onDismissRequest = { viewModel.clearSaveError() },
            title = { ThemedText(text = "Kaydetme Başarısız", color = appTheme.danger, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
            text = { ThemedText(text = displayMsg, isSecondary = true) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearSaveError() }) {
                    Text("Tamam", color = appTheme.accentColor, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { ThemedText(text = "Kur Ayarla", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Geri", modifier = Modifier.size(24.dp))
                        Text("Geri")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.save() },
                        enabled = !state.isSaving && !state.isLoading
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = appTheme.accentColor)
                        } else {
                            Text("Kaydet", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = appTheme.accentColor)
                        }
                    }
                }
            )
        }
    ) { padding ->
        ThemedView {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = appTheme.accentColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        ThemedText(text = "Yükleniyor...", isSecondary = true)
                    }
                }
                state.errorMessage != null && state.multipliers.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ThemedText(text = state.errorMessage!!, color = appTheme.danger)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.clearError(); viewModel.loadData() }) {
                            Text("Tekrar Dene")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(rememberScrollState())
                    ) {
                        state.errorMessage?.let { msg ->
                            ThemedText(text = msg, color = appTheme.danger, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(16.dp))
                        }
                        state.multipliers.forEachIndexed { index, item ->
                            GoldMultiplierRow(
                                item = item,
                                index = index,
                                viewModel = viewModel
                            )
                        }
                        if (state.multipliers.isEmpty() && state.errorMessage == null) {
                            ThemedText(text = "Henüz çarpan bulunmuyor.", isSecondary = true, modifier = Modifier.padding(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GoldMultiplierRow(
    item: GoldMultiplier,
    index: Int,
    viewModel: GoldMultipliersViewModel
) {
    val appTheme = LocalAppTheme.current
    var buyStr by remember(item) { mutableStateOf(formatMultiplier(item.buyMultiplier)) }
    var sellStr by remember(item) { mutableStateOf(formatMultiplier(item.sellMultiplier)) }
    LaunchedEffect(item) {
        buyStr = formatMultiplier(item.buyMultiplier)
        sellStr = formatMultiplier(item.sellMultiplier)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = appTheme.surfaceElevatedColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ThemedText(
                text = item.currencyCode,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = appTheme.accentColor
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    ThemedText(text = "Alış Çarpanı", style = MaterialTheme.typography.labelSmall, isSecondary = true)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = buyStr,
                        onValueChange = { s ->
                            buyStr = s
                            s.toDoubleOrNull()?.let { viewModel.updateBuyMultiplier(index, it) }
                            if (s.isEmpty()) viewModel.updateBuyMultiplier(index, 0.0)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    ThemedText(text = "Satış Çarpanı", style = MaterialTheme.typography.labelSmall, isSecondary = true)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = sellStr,
                        onValueChange = { s ->
                            sellStr = s
                            s.toDoubleOrNull()?.let { viewModel.updateSellMultiplier(index, it) }
                            if (s.isEmpty()) viewModel.updateSellMultiplier(index, 0.0)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }
        }
    }
}

private fun formatMultiplier(v: Double): String {
    return if (v == 0.0) "" else v.toString()
}
