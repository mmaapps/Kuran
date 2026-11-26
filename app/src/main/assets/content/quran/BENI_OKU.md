# Kur'an metni buraya

1. https://tanzil.net/download/ adresinden indir:
   - Quran type: **Uthmani**
   - Output file format: **XML**
   - Dosya adı: `quran-uthmani.xml` (bu klasöre)
2. https://tanzil.net/docs/Quran_Metadata → `quran-data.xml` (sayfa, cüz, hizb, sûre bilgileri) → bu klasöre.
3. **Dosyaları açıp DÜZENLEME.** Sondaki lisans yorumlarını silme. (Tanzil şartı: metin değiştirilemez.)
4. Mushaf sayfa düzeni: `quran-data.xml` içindeki `<pages>` 604 sayfanın başladığı sûre/âyeti verir.
   Kelimeler, âyet metni boşluklara göre bölünerek elde edilir (kelimeye dokunma özelliği).
5. Harf çipleri: kelimeden harekeler (U+064B–U+065F, U+0670, U+06D6–U+06ED, U+0640) atılır,
   kalan her karakter bir harftir (Mushaf.dc.html'deki mantığın aynısı).
