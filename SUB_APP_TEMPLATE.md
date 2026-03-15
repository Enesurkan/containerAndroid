# Alt Uygulama (Sub App) Oluşturma Şablonu – Android

Bu doküman, mevcut `containerAndroid` projesinden türetilen white-label alt uygulamaları (ör. spesifik kuyumcu app’leri) hızlı ve tutarlı şekilde üretmek için kullanılacak standart akışı anlatır.

## 1. Her yeni alt app için alınacak girdiler

- **Proje / klasör:** Yeni proje klasör adı (ör. `containerAndroid-Ceyrekcim`). Ana kaynak: `containerAndroid`.
- **App kimliği:** Display name (launcher’da görünen ad), **Application ID** (ör. `com.app.ceyrekcim`).
- **Backend:** **X-Api-Key**, base URL (aynı veya farklı domain).
- **Firebase:** Yeni applicationId için Firebase Android app, **`google-services.json`**.
- **AppInformation JSON:** Marka / iletişim (navigation_title, splash_logo, contact_*, vb.) – aşağıdaki gibi; `AppInformationData.default` ile eşlenecek.

```json
{
  "navigation_icon": "",
  "navigation_title": "Çeyrekçi",
  "splash_logo": "https://assets.dienu.work/clients/ceyrek/splash-logo.png",
  "contact_image": "https://assets.dienu.work/clients/ceyrek/appi-logo.png",
  "contact_title": "Çeyrekçi",
  "contact_phone": "+905417433232",
  "contact_address": "Kuyumcular Çarşısı ...",
  "contact_email": "saglarsarrafiye@gmail.com",
  "contact_whatsapp": "905417433232",
  "contact_instagram": "https://www.instagram.com/ceyrekcikdzeregli/",
  "contact_maps_url": "https://maps.app.goo.gl/..."
}
```

## 2. Genel akış (özet)

- Projeyi yeni klasöre kopyala; yeni klasörde `.git` silip `git init` (opsiyonel).
- **build.gradle.kts:** `applicationId`, `resValue` veya `strings.xml` ile app adı.
- **AppGate’i atla (white-label):** Sabit API key ile doğrudan ana ekrana gir. `AppGateViewModel` içinde `Secrets.defaultApiKey` (veya BuildConfig) kullan; kayıtlı key yoksa bu key’i yazıp `loadInitialData` çağır, onboarding/aktivasyon ekranı gösterme.
- **Sabit X-Api-Key:** `Secrets.kt` + `SecurityUtils.kt` ile obfuscate edilmiş key (iOS ile aynı mantık). Alternatif: `secret.properties` + BuildConfig (geliştirme için).
- **AppInformationData.default** ve gerekirse **UIConfig.default** müşteri değerleriyle güncelle.
- **Firebase:** `google-services.json` yerleştir; FCM/push akışı aynen kullanılır.
- **Deep link:** `dienu://market/...` için `AndroidManifest.xml` intent-filter (isteğe bağlı; push zaten deeplink taşıyor).
- Smoke test: app adı, branding, veri, push.

## 3. Activation’ı atlama ve sabit müşteri modeli

- **AppGateViewModel:** Kayıtlı API key yoksa `Secrets.defaultApiKey` (veya `BuildConfig.DEFAULT_API_KEY`) boş değilse bunu kullan: `prefs.saveApiKey(apiKey)`, `loadInitialData(apiKey)`, `isActivated = true`, `isDataReady = true` (veri gelince). Onboarding/ActivationFormScreen gösterilmez.
- **AppGate.kt:** Değişiklik gerekmez; ViewModel zaten “activated + data ready” durumuna geçince `MainTabScreen` gösterir.

## 4. X-Api-Key obfuscation (SecurityUtils + Secrets)

- **SecurityUtils.kt:** `obfuscate(String) -> ByteArray`, `deobfuscate(ByteArray) -> String` (XOR with salt). iOS ile aynı algoritma önerilir (salt aynı olabilir).
- **Secrets.kt:** `defaultApiKey: String` → `SecurityUtils.deobfuscate(_defaultApiKeyBytes)`. `_defaultApiKeyBytes` yeni müşteri key’i obfuscate edilerek doldurulur.
- Yeni müşteri: Raw key → obfuscate → byte array’i `Secrets` içine yapıştır; projede açık key kalmasın.

## 5. AppInformation JSON → AppInformationData.default

- Dosya: `domain/AppInformation.kt`. `AppInformationData.default` companion değerini verilen JSON’a göre güncelle (navigation_title, splash_logo, contact_*, vb.).

## 6. UIConfig.default (opsiyonel)

- `domain/UIConfig.kt` içinde `UIConfig.default` (veya varsa companion default) ile marketEnabled, pushEnabled, converterEnabled, timerInterval vb. başlangıç değerleri verilebilir. Backend ui-config bunları override eder.

## 7. Firebase & Push

- `AltinTakipMessagingService`: `onNewToken` → backend pushRegister. `onMessageReceived`: deeplink/currencyCode → intent → MainActivity → PushDeepLinkHolder → MainTabScreen’de işlenir.
- POST_NOTIFICATIONS izni (Android 13+); `config.pushEnabled` ile register/unregister mantığı aynı.

## 8. Deep link (dienu://) intent-filter

- `AndroidManifest.xml` içinde MainActivity’ye intent-filter ekle:
  - `android:scheme="dienu"`, `android:host="market"`, `android:pathPrefix="/"` (veya spesifik path). Böylece harici linkler (tarayıcı, başka uygulama) uygulamayı açar; mevcut `applyPushPayload` mantığı intent’ten de okunabilir.

## 9. Smoke test checklist

- App adı doğru mu? Aktivasyon ekranı yok, direkt ana ekran mı?
- Fiyatlar, kampanyalar, logolar doğru müşteriye mi ait?
- Push: izin, register, deeplink tıklama (tab/campaign/product) çalışıyor mu?
- İletişim sekmesi: telefon, adres, WhatsApp, Instagram, Maps doğru mu?

Detaylı adımlar için unified şablona bakın: **SUB_APP_TEMPLATE_UNIFIED.md** (üst klasörde veya repo kökünde).
