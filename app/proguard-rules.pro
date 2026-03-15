# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
# See http://developer.android.com/guide/developing/tools/proguard.html

# ========== Genel ==========
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Stack trace'ta satır numarası kalsın (crash raporları için)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ========== Kotlin ==========
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-keepclassmembernames class kotlin.reflect.jvm.internal.** { *; }

# ========== Kotlinx Coroutines ==========
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ========== Retrofit + OkHttp ==========
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# ========== Gson ==========
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class sun.misc.Unsafe { *; }
-keep class com.example.altintakipandroid.domain.** { *; }
-keepclassmembers class com.example.altintakipandroid.domain.** {
    <fields>;
}
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# ========== Compose ==========
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ========== Firebase / FCM ==========
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ========== DataStore ==========
-keep class androidx.datastore.** { *; }

# ========== Uygulama modelleri (Gson serileştirme) ==========
-keep class com.example.altintakipandroid.domain.UIConfig { *; }
-keep class com.example.altintakipandroid.domain.UIConfigResponse { *; }
-keep class com.example.altintakipandroid.domain.AppInformationData { *; }
-keep class com.example.altintakipandroid.domain.AppInformationResponse { *; }
-keep class com.example.altintakipandroid.domain.ExchangeRate { *; }
-keep class com.example.altintakipandroid.domain.ExchangeRatesResponse { *; }
-keep class com.example.altintakipandroid.domain.ActivationResponse { *; }
-keep class com.example.altintakipandroid.domain.WsTokenResponse { *; }
-keep class com.example.altintakipandroid.domain.PushRegisterRequest { *; }
-keep class com.example.altintakipandroid.domain.PushRegisterResponse { *; }
-keep class com.example.altintakipandroid.domain.UserAsset { *; }
-keep class com.example.altintakipandroid.domain.GoldMultiplier { *; }
-keep class com.example.altintakipandroid.domain.MarketModels$** { *; }
-keep class com.example.altintakipandroid.domain.PortalLoginRequest { *; }
-keep class com.example.altintakipandroid.domain.PortalLoginResponse { *; }
-keep class com.example.altintakipandroid.domain.OnboardingSlide { *; }

# ========== Güvenlik: obfuscation korusun ama sınıf adları kırpılmasın (opsiyonel) ==========
# -keep class com.example.altintakipandroid.data.security.** { *; }
