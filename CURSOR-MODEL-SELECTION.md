# Cursor: Görev Türüne Göre Model Seçimi

Bu projede **görev türüne göre hangi modeli kullanmanız gerektiği** aşağıdaki tabloya göre. Cursor’da modeli Composer veya Chat üstündeki model seçiciden değiştirirsiniz. AI, `.cursor/rules/model-selection.mdc` kuralı sayesinde her görevde size uygun modeli **önerir**; isterseniz öneriyi takip edebilirsiniz.

> **Not:** Cursor modeli otomatik değiştirmez; siz seçersiniz. Bu dosya ve kural, doğru modeli hızlıca seçmeniz içindir.

---

## Kısa tablo

| Ne yapıyorsunuz?           | Seçmeniz gereken model              |
|---------------------------|-------------------------------------|
| Bug fix / hata düzeltme   | **Composer 1.5** veya **Auto**      |
| Mimari / büyük tasarım    | **Claude 4.5/4.6 Opus** veya **Premium** |
| Refactor / kod temizliği  | **Composer 1.5** veya **Claude 4.5 Sonnet** |
| Yeni özellik / ekran      | **Composer 1.5** veya **Claude 4.5 Sonnet** |
| Dokümantasyon / README    | **Claude 4.5 Sonnet**               |
| Hızlı soru / küçük iş     | **Auto** veya **Gemini 3 Flash**    |

---

## Neden bu modeller?

- **Auto / Composer 1.5:** “Auto + Composer” kredi havuzundan tüketir; günlük kodlama ve bug fix için hem hızlı hem kredi dostu.
- **Claude Opus / Premium:** Zor mimari kararlar ve büyük refactor’lar için daha yetenekli; API havuzundan tüketir.
- **Claude Sonnet:** Refactor, özellik ve dokümantasyon için dengeli fiyat/performans.
- **Gemini 3 Flash:** Çok ucuz; basit sorular ve küçük değişiklikler için.

---

## Bu projede nasıl kullanılır?

1. **Chat/Composer’da** görevinizi yazın (örn. “şu crash’i düzelt”, “bu ekranı MVVM’e taşı”).
2. AI ilk yanıtta **“Bu görev için önerilen model: …”** diye önerecek.
3. Öneriyi uygun bulursanız Composer/Chat’teki model seçiciden o modeli seçin; gerekirse sonraki mesajlarda da aynı modeli kullanın.

İsterseniz bu MD dosyasına bakmadan, sadece AI’ın her cevaptaki model önerisini takip edebilirsiniz.

---

## On-demand usage nasıl kullanılır?

Cursor'da aylık dahil API krediniz (Pro'da $20) bittiğinde iki seçenek var:

| Ayar | Ne olur? | Ne zaman tercih? |
|------|----------|-------------------|
| **On-demand kapalı** | Kredi bitince API modelleri (Claude, GPT vb.) kullanılamaz; ekstra ücret yazılmaz. | Faturalandırmayı sınırlamak istiyorsanız. Sürpriz fatura istemiyorsanız **kapalı** tutun. |
| **On-demand açık** | Kredi bitince kullanım devam eder; aşım kullanımı sonradan faturalanır. | Ayda çok kullanıyorsanız ve "kredi bitsin yeter ki kesintisiz devam etsin" diyorsanız açın. |

**Nereden değiştirilir:** Cursor → **Settings** → **Subscription / Billing** (veya **Usage**) bölümünde "On-demand usage" / "Pay as you go" benzeri seçenek.

**Özet:** Sürpriz ücret istemiyorsanız on-demand'i **kapatın**; dahil kredi bitince sadece o ay için limiti aşmış olursunuz, ekstra çekim yapılmaz.
