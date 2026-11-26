# Tilâvet sesleri buraya

- **Sadece yazılı izni olan** hâfız kaydı konur (bkz. `04_TELIF_VE_IZINLER/IZIN_TAKIP_TABLOSU.csv`, satır 9).
- Âyet âyet dosya: `SSSAAA.opus` → `001001.opus` (Fâtiha 1) … `114006.opus` (Nâs 6). Toplam 6236 dosya.
- Biçim: Opus, mono, ~48 kbps (Play sınırını aşarsa 32 kbps).
- Kelime sesleri (izin varsa): `kelime/SSS_AAA_KKK.m4a` (sûre_âyet_kelime sırası, 1'den başlar).
- Uygulama bu dosyalara `asset:///tilavet/001001.opus` adresiyle ulaşır (install-time paketler
  uygulamanın assets klasörüyle birleşir).
- Bu dosyayı (BENI_OKU.md) yayın paketinden önce silebilirsiniz.
