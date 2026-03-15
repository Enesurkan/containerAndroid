# Store'a Gönderme Adımları

Bu doküman, Android uygulamasını Google Play Store'a yüklemek için adım adım yapılacakları özetler.

---

## 1. Keystore oluşturma (tek seferlik)

Release imzası için bir **upload keystore** oluşturun. Keystore ve şifreleri kaybederseniz aynı keystore ile güncelleme yükleyemezsiniz; güvenli yerde saklayın.

Proje kökünde (veya güvenli bir dizinde):

```bash
keytool -genkey -v -keystore keystore/altintakip-upload.jks -keyalg RSA -keysize 2048 -validity 10000 -alias altintakip
```

- `keystore/` klasörünü proje dışında da tutabilirsiniz; `STORE_FILE` ile tam yolu verebilirsiniz.
- İstenen bilgileri doldurun (isim, kurum, şehir vb.); keystore ve key şifrelerini not alın.
- Bu dosyayı **git'e eklemeyin** (`.gitignore`'da `*.jks` var).

---

## 2. secret.properties ayarlama

`secret.properties.example` dosyasını kopyalayıp `secret.properties` yapın (veya mevcut `secret.properties`'i açın). Store imzası için şu satırları ekleyin:

```properties
STORE_FILE=keystore/altintakip-upload.jks
STORE_PASSWORD=keystore_sifreniz
KEY_ALIAS=altintakip
KEY_PASSWORD=key_sifreniz
```

- Keystore'u farklı bir yere koyduysanız `STORE_FILE` için tam yol veya proje köküne göre relative yol verin (örn. `../keys/altintakip-upload.jks`).

---

## 3. Uygulama kimliği (applicationId) ve Firebase

`applicationId` **com.dienu.altintakip** olarak ayarlı (Play Store `com.example.*` kabul etmez). Bu paket adı için Firebase’i tanımlamanız gerekir:

1. **Firebase Console** → [altintakip-d60a1](https://console.firebase.google.com/project/altintakip-d60a1) (veya kendi projeniz) → Project settings → **Add app** → Android.
2. **Android package name:** `com.dienu.altintakip` yazın.
3. İndirilen **google-services.json** dosyasını `app/` klasörüne koyup mevcut dosyanın üzerine yazın.
4. Projeyi tekrar derleyin: `./gradlew clean bundleRelease`.

---

## 4. Release bundle (AAB) üretme

```bash
./gradlew bundleRelease
```

Çıktı: `app/build/outputs/bundle/release/app-release.aab`

- `secret.properties` ve keystore doğruysa AAB imzalı olur. İmza yoksa Console’da “Release imzası yapılandırılmamış” benzeri uyarı alırsınız; 1. ve 2. adımları kontrol edin.

---

## 5. Google Play Console

1. [Play Console](https://play.google.com/console) hesabı açın; **Yeni uygulama** oluşturun.
2. **Store listesi:** Uygulama adı, kısa/tam açıklama, ikon (512x512), ekran görüntüleri (en az 2), kategori, iletişim e-postası.
3. **Gizlilik politikası:** Veri topluyorsanız politikayı yayınlayıp URL’i girin.
4. **İçerik derecelendirmesi:** Anketi doldurup derece alın.
5. **Veri güvenliği:** Toplanan verileri ve şifrelemeyi işaretleyin.
6. **Yayın > Üretim (veya test)** > **Yeni sürüm oluştur** > **App Bundle’ları yükle** ile `app-release.aab` dosyasını yükleyin.
7. **Sürüm notları** yazın, **İncelemeye gönder** / **Yayınla**.

---

## 6. Sonraki sürümler

- Her yeni store sürümünde [app/build.gradle.kts](../app/build.gradle.kts) içinde **versionCode** değerini artırın (2, 3, …).
- **versionName**’i istediğiniz gibi güncelleyin (örn. "1.1", "1.2").
- Aynı keystore ile `bundleRelease` alıp yeni AAB’yi yükleyin.
