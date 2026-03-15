# Sertifika Pinning (Certificate Pinning)

API sunucusuna yapılan HTTPS bağlantılarını, yalnızca belirlediğimiz sertifika (veya public key) ile kabul edecek şekilde kısıtlar. Böylece MITM (araya girme) ve sahte sertifika ile yapılabilecek saldırılar zorlaşır.

## Pin değerini alma

Hedef host: **api.dienu.work** (veya kendi API host’unuz).

### Android (OkHttp – public key hash, önerilen)

Sertifikanın **public key**’inin SHA-256 hash’i. Sertifika yenilense bile aynı key kullanılıyorsa pin değişmez.

```bash
echo | openssl s_client -servername api.dienu.work -connect api.dienu.work:443 2>/dev/null | \
  openssl x509 -pubkey -noout | \
  openssl pkey -pubin -outform der | \
  openssl dgst -sha256 -binary | \
  openssl enc -base64
```

Çıktı örneği: `AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=`

### iOS (sertifika hash’i)

Sunucunun **leaf sertifikasının** SHA-256 hash’i. Sertifika yenilendiğinde bu değer değişir; o zaman uygulamadaki pin’i güncellemeniz gerekir.

```bash
echo | openssl s_client -servername api.dienu.work -connect api.dienu.work:443 2>/dev/null | \
  openssl x509 -outform DER | \
  openssl dgst -sha256 -binary | \
  openssl enc -base64
```

---

## Android’de kullanım

1. Yukarıdaki **Android** komutu ile pin’i alın (base64 çıktı, `sha256/` ön eki olmadan).
2. Proje kökünde `secret.properties` dosyasına ekleyin (yoksa oluşturun; bu dosyayı git’e eklemeyin):

```properties
CERT_PIN_API_DIENU_WORK=sha256/BURAYA_ALINAN_PIN
```

veya sadece pin (uygulama `sha256/` ekler):

```properties
CERT_PIN_API_DIENU_WORK=BURAYA_ALINAN_PIN
```

3. Release build alın; pin boş bırakılırsa pinning devre dışı kalır.

---

## iOS’ta kullanım

1. Yukarıdaki **iOS** komutu ile pin’i alın.
2. `AltinTakip/Services/CertificatePinning.swift` (veya APIClient’ın kullandığı pin listesi) içindeki `pinnedCertHashes` dizisine bu değeri ekleyin. Dosyada açıklama ve örnek bulunur.

---

## Önemli notlar

- **Yedek pin:** Sertifika yenilendiğinde uygulama güncellenene kadar bağlantı kırılmasın diye hem eski hem yeni pin’i ekleyebilirsiniz (Android’de `secret.properties` veya koda ikinci `.add(host, "sha256/yeni")`, iOS’ta `pinnedCertHashes` dizisine ikinci eleman).
- **Debug:** iOS’ta `#if DEBUG` ile pinning atlanıyor; Android’de pin boş ise pinning kapalı.
- **WebSocket:** iOS’ta WebSocket bağlantısı şu an ayrı bir URLSession kullandığı için pinning sadece REST (APIClient) ve Activation isteklerinde uygulanır. İstenirse WebSocket için de aynı pinning session kullanılacak şekilde güncellenebilir.
- **Host değişirse:** API farklı bir domaine taşınırsa pin’i ve host’u her iki tarafta da güncellemeyi unutmayın.
