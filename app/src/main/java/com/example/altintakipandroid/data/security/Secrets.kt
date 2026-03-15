package com.example.altintakipandroid.data.security

/**
 * White-label alt uygulamalarda sabit X-Api-Key burada obfuscate edilmiş tutulur.
 * Yeni müşteri için: raw key → SecurityUtils.obfuscate() → _defaultApiKeyBytes güncelle.
 * Multi-tenant ana projede boş bırakılabilir; BuildConfig.DEFAULT_API_KEY veya aktivasyon kullanılır.
 */
object Secrets {
    // Örnek: boş = multi-tenant; dolu = white-label (obfuscate edilmiş key bytes)
    private val _defaultApiKeyBytes: ByteArray = byteArrayOf() // SecurityUtils.obfuscate("your-api-key")

    val defaultApiKey: String
        get() = if (_defaultApiKeyBytes.isEmpty()) "" else SecurityUtils.deobfuscate(_defaultApiKeyBytes)
}
