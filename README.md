# Altın Takip Android

Altın ve döviz kurları, vitrin (kampanya/ürün), favoriler, çevirici, varlıklar ve push bildirimleri sunan multi-tenant Android uygulaması. White-label alt uygulamalar için [SUB_APP_TEMPLATE.md](SUB_APP_TEMPLATE.md) kullanılır; tek şablonda hem iOS hem Android için [SUB_APP_TEMPLATE_UNIFIED.md](SUB_APP_TEMPLATE_UNIFIED.md) vardır.

## Proje yapısı

- **app/src/main/java/com/example/altintakipandroid/**
  - **MainActivity.kt** – Entry point, tema, push intent ve **dienu://** deep link (intent.data), edge-to-edge, **NetworkMonitor** init, **ConnectionStatusOverlay**
  - **ui/** – AppGate, Splash, Onboarding, Activation (ActivationFormScreen, QrScanActivity), MainTabScreen, MarketsScreen, FavoritesScreen, ConverterScreen, AssetsScreen, AddAssetScreen, ContactScreen, **components** (ConnectionStatusOverlay), market/, admin, theme, main
  - **data/** – api, push, websocket, local, **security** (SecurityUtils, Secrets), **network** (NetworkMonitor)
  - **domain/** – AppConstants, UIConfig, AppInformationData, Activation, ExchangeRate, WsToken, MarketModels, UserAsset, PushRegister, PortalLogin, GoldMultiplier, OnboardingSlide

## Ana özellikler

- **Sekmeler (UIConfig ile aç/kapa):** Piyasalar, Favoriler, Çevirici, Varlıklarım, Vitrin, İletişim
- **Aktivasyon / onboarding:** AppGate ile Splash → Onboarding → Aktivasyon (kod/client name, QR) → MainTabScreen. **White-label:** Kayıtlı API key yoksa **Secrets.defaultApiKey** veya BuildConfig.DEFAULT_API_KEY kullanılır; varsa doğrudan ana ekrana girilir (SUB_APP_TEMPLATE).
- **Sabit API key (white-label):** **Secrets.kt** + **SecurityUtils.kt** ile obfuscate edilmiş X-Api-Key; iOS ile aynı XOR algoritması. BuildConfig (secret.properties) alternatif.
- **Veri:** REST API (ui-config, app-information, gold-currency, ws-token, push register/unregister, market, kampanya, ürün, share URL, portal-login, gold-multipliers). WebSocket canlı fiyat (`/ws/prices`), `mobileUseWebSocket` ile aç/kapa.
- **Push:** FCM, backend register/unregister, bildirimde `deeplink` ve `currencyCode`; PushDeepLinkHolder ile MainTabScreen’de işlenir (tab, campaigns, campaign, category, product).
- **Deep link:** **dienu://market/...** hem push payload’ta hem **manifest intent-filter** ile desteklenir; harici link (tarayıcı, başka uygulama) uygulamayı açar, MainActivity `intent.data` ile PushDeepLinkHolder’a iletir.
- **Ağ durumu:** **NetworkMonitor** (ConnectivityManager) ile bağlantı izlenir; **ConnectionStatusOverlay** bağlantı yokken üstte “İnternet Bağlantısı Yok” banner’ı gösterir (iOS ile aynı amaç).
- **Tema / marka:** UIConfig + AppInformationData (API + PreferencesManager cache). Theme (Color, AltintakipAndroidTheme), ListStyleConfig, ContactStyleConfig.

## Reverse engineering / güvenlik önlemleri

- **R8/ProGuard:** Release build’de `isMinifyEnabled = true` ve `isShrinkResources = true`; kod ve kaynak isimleri obfuscate edilir, kullanılmayan kaynaklar kaldırılır.
- **Logging:** Release’de API istek/cevap ve header’lar loglanmaz (BuildConfig.DEBUG kontrolü); API key ve veri loglara düşmez.
- **Root ve debugger:** Release’de root’lu cihaz veya bağlı debugger tespit edilirse uyarı dialog’u gösterilir ve uygulama kapatılır (`SecurityChecker`, `MainActivity`).
- **Certificate pinning:** API host için SHA-256 pin; `secret.properties` içinde `CERT_PIN_API_DIENU_WORK=sha256/...` ile açılır. Detay: [docs/CERT_PINNING.md](docs/CERT_PINNING.md).
- **API key:** White-label için obfuscate edilmiş key (`Secrets` + `SecurityUtils`) kullanılır.

## Build ve çalıştırma

1. Android Studio ile projeyi açın.
2. `app/build.gradle.kts` içinde `applicationId` ve `versionName` ayarlayın.
3. `google-services.json` ekleyin (Firebase).
4. Run (Debug veya Release). Release APK: `./gradlew assembleRelease`. Store için AAB: `./gradlew bundleRelease`.

**Store’a gönderme:** Keystore oluşturma, imzalama, Play Console adımları için [docs/STORE_RELEASE.md](docs/STORE_RELEASE.md) dosyasına bakın.

## White-label alt uygulama

Yeni müşteri için tek-tenant alt uygulama çıkarmak için **[SUB_APP_TEMPLATE.md](SUB_APP_TEMPLATE.md)** dosyasındaki adımları uygulayın. **Hem iOS hem Android** için tek girdi listesi ve adımlar **[SUB_APP_TEMPLATE_UNIFIED.md](SUB_APP_TEMPLATE_UNIFIED.md)** içindedir.
