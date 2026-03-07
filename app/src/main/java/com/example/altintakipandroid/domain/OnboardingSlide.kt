package com.example.altintakipandroid.domain

/**
 * Onboarding slide content (aligned with iOS OnboardingSlide.ONBOARDING_SLIDES).
 */
data class OnboardingSlide(
    val title: String,
    val description: String,
    val imageRes: Int? = null, // Android drawable resource; null = use Icon
    val iconName: String? = null // For Material Icons: "qrcode", "show_chart", "payments"
) {
    companion object {
        val ONBOARDING_SLIDES: List<OnboardingSlide> = listOf(
            OnboardingSlide(
                title = "Kuyumcunla Aktive Et",
                description = "Kuyumcunuzun verdiği QR kodu okutun veya aktivasyon kodunu girerek uygulamayı kullanmaya başlayın.",
                iconName = "qrcode"
            ),
            OnboardingSlide(
                title = "Altınlarınızı Takip Edin",
                description = "Sahip olduğunuz altın miktarlarını ekleyin, güncel fiyatlara göre toplam değerinizi kolayca görüntüleyin.",
                iconName = "show_chart"
            ),
            OnboardingSlide(
                title = "Güncel Fiyatlar, Net Bilgi",
                description = "Fiyatlar, seçili kuyumcunun sağladığı verilere göre gösterilir. Her zaman sade ve anlaşılır.",
                iconName = "payments"
            )
        )
    }
}
