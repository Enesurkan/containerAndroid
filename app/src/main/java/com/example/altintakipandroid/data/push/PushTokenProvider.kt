package com.example.altintakipandroid.data.push

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * FCM token alır (suspend). Hata durumunda exception fırlatır.
 */
suspend fun getFcmToken(): String = suspendCancellableCoroutine { cont ->
    FirebaseMessaging.getInstance().token
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                if (token != null && token.isNotBlank()) {
                    cont.resume(token)
                } else {
                    cont.resumeWithException(Exception("FCM token boş"))
                }
            } else {
                cont.resumeWithException(task.exception ?: Exception("FCM token alınamadı"))
            }
        }
}
