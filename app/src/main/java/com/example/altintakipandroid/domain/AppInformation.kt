package com.example.altintakipandroid.domain

import com.google.gson.annotations.SerializedName

/**
 * App information from server (aligned with iOS AppInformationData).
 */
data class AppInformationData(
    @SerializedName("navigation_icon") val navigationIcon: String? = null,
    @SerializedName("navigation_title") val navigationTitle: String? = null,
    @SerializedName("splash_logo") val splashLogo: String? = null,
    @SerializedName("contact_image") val contactImage: String? = null,
    @SerializedName("contact_title") val contactTitle: String? = null,
    @SerializedName("contact_phone") val contactPhone: String? = null,
    @SerializedName("contact_address") val contactAddress: String? = null,
    @SerializedName("contact_email") val contactEmail: String? = null,
    @SerializedName("contact_whatsapp") val contactWhatsapp: String? = null,
    @SerializedName("contact_instagram") val contactInstagram: String? = null,
    @SerializedName("contact_maps_url") val contactMapsUrl: String? = null
) {
    companion object {
        val default = AppInformationData(
            navigationTitle = "Altın Takip",
            contactTitle = "Altın Takip",
            contactPhone = "+905417433232",
            contactAddress = "Çeyrekçi Sarrafiye&Kuyum, Müftü Mahallesi Yukarı Sokak, Eski Belediye Arkası No:39, 67300 Ereğli/Zonguldak",
            contactWhatsapp = "905417433232",
            contactInstagram = "https://www.instagram.com/ceyrekcikdzeregli/",
            contactMapsUrl = "https://maps.app.goo.gl/WnRmjFm7cGaLg5HW8"
        )
    }
}

data class AppInformationResponse(
    @SerializedName("statusCode") val statusCode: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: AppInformationData? = null
) {
    fun getDataOrDefault(): AppInformationData = data ?: AppInformationData.default
}
