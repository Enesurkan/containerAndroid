package com.example.altintakipandroid.data.security

import android.os.Build
import com.example.altintakipandroid.BuildConfig
import java.io.File

/**
 * Reverse engineering ve güvensiz ortam tespiti.
 * - Root: cihaz root'lu mu
 * - Debugger: release build'de debugger bağlı mı
 */
object SecurityChecker {

    private val ROOT_INDICATORS = listOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/data/local/su",
        "/su/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/system/xbin/magisk",
        "/sbin/magisk",
        "/data/adb/magisk"
    )

    /**
     * Cihazın root'lu olup olmadığını tespit eder (yaygın yollar).
     */
    fun isRooted(): Boolean {
        if (Build.TAGS != null && Build.TAGS.contains("test-keys")) return true
        return ROOT_INDICATORS.any { File(it).exists() } ||
            runCatching { Runtime.getRuntime().exec("su"); true }.getOrElse { false }
    }

    /**
     * Debugger bağlı mı (sadece release'de anlamlı; debug'da geliştirici debugger kullanır).
     */
    fun isDebuggerAttached(): Boolean = android.os.Debug.isDebuggerConnected()

    /**
     * Release build'de güvensiz ortam var mı (root veya debugger).
     * Debug build'de false döner (geliştirme deneyimini bozmamak için).
     */
    fun isUnsafeEnvironment(): Boolean {
        if (BuildConfig.DEBUG) return false
        return isRooted() || isDebuggerAttached()
    }

    /**
     * Kısa açıklama metni (uyarı dialogu için).
     */
    fun getUnsafeMessage(): String {
        val parts = mutableListOf<String>()
        if (isRooted()) parts.add("Bu cihaz root erişimine sahip.")
        if (isDebuggerAttached()) parts.add("Uygulama bir hata ayıklayıcıya bağlı.")
        return parts.joinToString(" ") + " Güvenlik nedeniyle uygulama kullanılamayabilir."
    }
}
