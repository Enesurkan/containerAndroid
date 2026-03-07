package com.example.altintakipandroid.data.push

import com.example.altintakipandroid.data.api.RetrofitClient
import com.example.altintakipandroid.data.local.PreferencesManager
import com.example.altintakipandroid.domain.PushRegisterRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * FCM token yenilendiğinde backend'e tekrar kaydeder; gelen bildirimleri işler.
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
        // İsteğe bağlı: bildirimi göster (NotificationCompat ile)
        message.notification?.let { notification ->
            // NotificationManager ile gösterebilirsiniz
        }
    }
}
