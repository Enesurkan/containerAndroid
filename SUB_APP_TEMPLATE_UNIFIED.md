# Alt Uygulama (Sub App) Oluşturma – Tek Şablon (iOS + Android)

Bu doküman, verilen bilgilere göre **hem iOS hem Android** white-label alt uygulamasını çıkarmak için kullanılır. Önce ortak girdileri toplayın, sonra platform bölümlerini uygulayın.

---

## Ortak girdiler (her iki platform için)

| Bilgi | Örnek |
|-------|--------|
| **Proje / klasör adı** | iOS: `AltinTakipiOS-Ceyrekcim`, Android: `containerAndroid-Ceyrekcim` |
| **Display name** (uygulama adı) | `Çeyrekçi`, `Müşteri X Altın` |
| **iOS Bundle ID** | `com.app.ceyrekcim` |
| **Android Application ID** | `com.app.ceyrekcim` |
| **X-Api-Key** | Backend’in verdiği sabit API anahtarı |
| **Base URL** | Aynı: `https://api.dienu.work` veya müşteriye özel domain |
| **Firebase** | iOS: `GoogleService-Info.plist`, Android: `google-services.json` (yeni bundle/application id ile) |
| **AppInformation JSON** | Aşağıdaki formatta marka / iletişim bilgileri |

**AppInformation JSON örneği:**

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

---

## iOS için adımlar

1. Ana projeyi (`AltinTakipiOS`) yeni klasöre kopyala; git ayrımı (isteğe bağlı).
2. Xcode: `PRODUCT_BUNDLE_IDENTIFIER`, `INFOPLIST_KEY_CFBundleDisplayName` güncelle.
3. **ActivationGate’i kaldır:** `AltinTakipApp` içinde doğrudan `MainTabView()` göster; `loadAppData()` içinde `apiKey = Secrets.defaultApiKey`, kaydet, cihaz kaydı, themeManager.loadInitialData.
4. **Secrets.swift:** X-Api-Key’i `SecurityUtils.obfuscate` ile obfuscate et, `_defaultApiKey` dizisine yaz.
5. **AppInformationData.default** (AppConfig.swift) ve isteğe bağlı **UIConfig.default** değerlerini JSON’a göre güncelle.
6. Firebase: `GoogleService-Info.plist` yerleştir.
7. Smoke test (app adı, direkt ana ekran, veri, push).

**Detay:** [AltinTakipiOS/SUB_APP_TEMPLATE.md](SUB_APP_TEMPLATE.md) (iOS repo kökünde).

---

## Android için adımlar

1. Ana projeyi (`containerAndroid`) yeni klasöre kopyala; git ayrımı (isteğe bağlı).
2. **build.gradle.kts:** `applicationId`, app name (strings.xml veya resValue).
3. **AppGate’i atla:** `AppGateViewModel` içinde kayıtlı API key yoksa `Secrets.defaultApiKey` (veya BuildConfig) kullan; key’i kaydet, `loadInitialData` çağır, onboarding/aktivasyon gösterme.
4. **Secrets.kt + SecurityUtils.kt:** X-Api-Key’i obfuscate et, `Secrets.defaultApiKey` ile runtime’da çöz. (Alternatif: `secret.properties` + BuildConfig.)
5. **AppInformationData.default** (domain/AppInformation.kt) ve isteğe bağlı **UIConfig.default** değerlerini JSON’a göre güncelle.
6. Firebase: `google-services.json` yerleştir.
7. **Deep link (opsiyonel):** `AndroidManifest.xml` MainActivity’ye `dienu://market` scheme intent-filter ekle.
8. Smoke test (app adı, direkt ana ekran, veri, push).

**Detay:** [containerAndroid/SUB_APP_TEMPLATE.md](SUB_APP_TEMPLATE.md) (Android repo kökünde).

---

## Özet

- **Tek seferde toplanan girdiler** (yukarıdaki tablo + JSON) ile önce iOS, sonra Android (veya tersi) adımlarını uygulayarak aynı müşteri için iki platformu da üretebilirsiniz.
- Platforma özel ayrıntılar için ilgili repo’daki **SUB_APP_TEMPLATE.md** dosyasına bakın.
