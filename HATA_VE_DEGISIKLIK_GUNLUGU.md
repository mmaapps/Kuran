# Hata ve Değişiklik Günlüğü

Kurallar `01_CODEX_TALIMATLARI.md` içinde. Her hata oluştuğu anda yazılır; düzeltilince altına "DÜZELTİLDİ:" eklenir.
Tasarımdan sapma gerekiyorsa "SORU:" yazılır ve kullanıcının cevabı beklenir.

---

## 2026-11-15 — Proje iskeleti (Claude)
NOT: İskelet, Android SDK'sı olmayan bir ortamda yazıldı; **henüz derlenmedi.** İlk GitHub Actions derlemesinde
çıkan hatalar buraya yazılıp düzeltilecek. Özellikle kontrol edilecekler:
- `com.batoulapps.adhan:adhan:1.2.1` Maven Central'dan çözülüyor mu; `CalculationParameters.methodAdjustments`
  alanı bu sürümde var mı (yoksa: Adhan'ın güncel Kotlin sürümü `com.batoulapps.adhan:adhan2` ve `CalculationMethod.TURKEY`).
- AGP 8.7.3 / Kotlin 2.1.0 / Compose BOM 2024.12.01 uyumu.

## 2026-11-15 — Kullanıcı kararları (cevaplanan sorular)
KARAR (güncellendi): MEAL UYGULAMADAN ÇIKARILDI. Elmalılı'nın 1935–38 orijinal metninin telifsiz ve doğrulanmış
  dijital kopyası bulunamadı; internetteki kopyalar sadeleştirilmiş (telifli) baskılar ya da kaynağı belirsiz.
  Kullanıcı kararı: "insanları kandırmayalım". Meal ekranı, meal dosyası, ezan sonrası meal, kelime anlamı ve
  günün âyeti widget'ındaki Türkçe satır kaldırıldı. Tasarımdan Meal.dc.html silindi; ekran numaraları yeniden sıralandı.
KARAR: Sabah ezanı imsaktan SONRA okunur. Varsayılan 30 dk; kullanıcı EzanVakti > "Sabah ezanı" satırından
  İmsakta / 15 / 30 / 45 / 60 dk seçebilir (tasarıma eklendi). Kod: `VakitAyarlari.sabahEzaniGecikmeDk`,
  `EzanZamanlayici.ezanZamani`. "5 dakika kaldı" hatırlatması da sabah ezanı zamanına göre kurulur.
KARAR: Uygulama adı "Kur'an-ı Kerim"; mağaza başlığı "Kur'an-ı Kerim · Ezan Vakti".
DEĞİŞİKLİK: Paket adı `com.kurankerim.app` → `com.atasoyyazilim.kurankerim`
  (eski ad Google Play'de başka bir geliştiricinin uygulamasında kayıtlı). Geliştirici: Atasoy Yazılım,
  destek: destek@atasoyyazilim.com.

## Açık konular
NOT: KFGQPC yazı tipi eklenince `ui/theme/Yazi.kt` içindeki `MushafYazi` satırı değişecek.

## 2026-11-26 — GitHub aktarımı
HATA: ZIP dosyasının doğrudan git ile aktarımı sırasında "Could not resolve host: github.com" hatası alındı.
NOT: GitHub bağlantısı üzerinden özgün .gitignore ve bu günlük eklendi. Android kaynakları ve ilk CI henüz aktarılmadı. Tasarım ve telif belgeleri değiştirilmedi.
