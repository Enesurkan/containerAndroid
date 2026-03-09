package com.example.altintakipandroid.data.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.altintakipandroid.MainActivity
import com.example.altintakipandroid.R
import com.example.altintakipandroid.data.api.RetrofitClient
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.domain.PushRegisterRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val CHANNEL_ID = "altintakip_push"
private const val NOTIFICATION_ID = 1

/**
 * FCM token yenilendiğinde backend'e tekrar kaydeder; gelen bildirimleri gösterir.
 * Bildirime tıklandığında deeplink/currencyCode ile MainActivity açılır (iOS ile aynı deep link desteği).
 */
class AltinTakipMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            runCatching {
                val prefs = PreferencesManager(applicationContext)
                val apiKey = prefs.getApiKey() ?: return@launch
                RetrofitClient.apiService.pushRegister(apiKey, body = PushRegisterRequest(token))
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        val deeplink = data["deeplink"]?.takeIf { it.isNotBlank() }
        val currencyCode = data["currencyCode"]?.takeIf { it.isNotBlank() }
        // Data-only mesajlarda kendi bildirimimizi gösteriyoruz (tıklanınca deeplink intent'te olsun).
        // notification payload varsa sistem bildirimi gösterebilir; tıklanınca intent data ile gelir.
        val title = message.notification?.title ?: getString(R.string.app_name)
        val body = message.notification?.body ?: ""

        createChannel()
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (deeplink != null) putExtra(PUSH_EXTRA_DEEPLINK, deeplink)
            if (currencyCode != null) putExtra(PUSH_EXTRA_CURRENCY_CODE, currencyCode)
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Sadece data-only mesajlarda kendi bildirimimizi göster (aksi halde çift bildirim olabilir)
        if (message.notification == null && (deeplink != null || currencyCode != null || body.isNotBlank())) {
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(NOTIFICATION_ID, notification)
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }
}
