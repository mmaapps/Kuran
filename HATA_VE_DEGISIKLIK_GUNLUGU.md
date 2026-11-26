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

## 2026-11-26 — Android proje aktarımı
DÜZELTİLDİ: 05_ANDROID_PROJE içeriği depoya aktarılıyor. İlk GitHub Actions derlemesinin sonucu henüz doğrulanmadı.

## 2026-11-26 — İlk CI (45847393301, 499d9f8)
HATA: AAB derlemesinde Gradle classpath bağımlılıkları Maven Central HTTP 429 (Too Many Requests) nedeniyle indirilemedi. Kotlin derlemesine ulaşılamadı. Örnek: org.jetbrains.kotlin:kotlin-reflect:2.0.20.
NOT: Başarısız iş yeniden başlatıldı. Bağımlılık sürümleri ve tasarım değiştirilmedi. Sonuç doğrulanınca kayıt güncellenecek.

DÜZELTİLDİ: İkinci denemede Maven bağımlılıkları çözüldü; :app:bundleDebug BUILD SUCCESSFUL (42 işlem, 2m 23s).
HATA: Bundletool, /home/runner/.android/debug.keystore bulunamadığı için evrensel APK üretemedi (iş 140025369968). AAB oluşturuldu ancak artifact yükleme adımı atlandı.
NOT: CI üzerinde yalnız debug amaçlı geçici imzalama anahtarı oluşturulması gerekiyor; üretim imzası kullanılmayacak. Tam CI başarısı henüz doğrulanmadı.

## 2026-11-26 — Üçüncü CI (45855943894, c8c4bf7)
DÜZELTİLDİ: CI debug anahtarı geçici olarak oluşturuluyor. İş 140034220021 gerçek loglarında :app:bundleDebug BUILD SUCCESSFUL (42 işlem, 2m 18s), bundletool evrensel APK üretimi ve artifact yükleme başarılı. Artifact 15134938880 (27.875.871 bayt; ZIP SHA-256 2c7a8c180a1ad2b29dac4fad918467065ab12dfc8761e0b93cbcf2cd236bd8eb). CI: https://github.com/mmaapps/Kuran/actions/runs/45855943894
NOT: Kullanıcı Termux kalıcı imzalama başarısını bildirdi; yerel APK ve imza sertifikası bağımsız doğrulanmadı. Bu iskelet kararlı sürüm değildir; Release yayımlanmadı.

## 2026-11-26 — Aşama 2: İçerik kaynakları
NOT: Orijinal 01_CODEX_TALIMATLARI.md ve 03_ICERIK_KAYNAKLARI.md yeniden incelendi. Depoda altı Amiri/Manrope/Lora fontu ve ilgili lisanslar mevcut. quran-uthmani.xml, quran-data.xml ve kfgqpc_hafs.ttf eksik.
HATA: Resmî Tanzil metadata indirme adresine (https://tanzil.net/res/text/metadata/quran-data.xml) çalışma ortamından curl ile erişilemedi: Could not resolve host: tanzil.net. Resmî kaynak sayfaları doğrulandı ancak özgün XML dosyalarının baytları alınamadı. Doğrulanmamış metin veya alternatif kaynak kullanılmadı.
SORU: KFGQPC HAFS yazı tipi için orijinal pakette yazılı lisans teyidi bekliyor; teyit gelmeden font eklenmeyecek. Tanzil XML dosyaları resmî kaynaktan değişmeden temin edilmeli. Aşama 2 açık; ekran kodlamasına geçilmedi.

NOT: 26 Kasım — Kullanıcı ZIP'teki kodlama ve ekran kurallarının aynen uygulanmasını tekrar istedi. Yerel Tanzil DNS hatası yeniden oluştu. Resmî HTTPS kaynaklarından özgün XML'leri indiren, lisans bildirimi ve 114 sûre/6236 âyet/604 sayfa/30 cüz yapısını denetleyen, SHA-256 kaydeden, mevcut dosyaları değiştirmeyen CI aktarım işlemi c2f8e6e commitinde eklendi. CI 45858283037 sırada; indirme/aktarım henüz doğrulanmadı. KFGQPC yazılı teyit bekliyor; ekran kodlamasına geçilmedi.

## 2026-11-26 — KFGQPC HAFS izin kararı
DÜZELTİLDİ: Tanzil özgün XML aktarımı 9d05f8dd commitinde tamamlandı; Android CI 45858936036 başarılı.
KARAR: Kullanıcı KFGQPC HAFS yazı tipi için yazılı izin aldığını bildirdi ve geliştirmeye devam edilmesini istedi. İzin belgesi bağımsız olarak incelenmedi; özel telif klasörüne dokunulmadı. Yalnız resmî kaynaktaki özgün font ve lisans metni kullanılacak, MushafYazi ancak dosya doğrulandıktan sonra değişecek. Sıradan Arapça sistem yazısı Mushaf yerine kullanılmayacak. Ses izinleri ayrı ve hâlâ bekliyor.
HATA: Resmî font sitesinin ana sayfası yeni qurancomplex.gov.sa sitesine yönlendiriliyor; eski HAFS sayfası 404 dönüyor. Resmî arşiv dosyasının içeriği henüz doğrulanamadı.

## 2026-11-26 — HAFS kaynak kontrolü sonucu
HATA: CI 45865969087, iş 140068758232, eski resmî Hafs.zip adresinde HTTP 404 (curl 22). Android CI 45865969253, iş 140068760014, gerçek loglarda 42/42 Gradle işlemi başarılı (32s), APK/AAB artifact 15139688530 yüklendi. Font henüz yok.
NOT: Kurumun yeni sitesinde font kataloğu bulundu ancak istenen HAFS arşivinin doğrudan bağlantısı doğrulanamadı. Resmî alan adındaki tarihsel Ver18 arşiv adresi 7fa56a16 commitinde sınanıyor; arşiv yayımlanmayacak ve font/lisans doğrulanmadan uygulamaya eklenmeyecek. Ekran geliştirme aşamasına geçilmedi; uygulama kodu değiştirilmedi.

HATA: Ver18 adresi de CI 45868811144 (iş 140078545966) gerçek loglarında HTTP 404/curl 22 döndürdü. Resmî font ve lisans dosyası henüz alınamadı. Aynı geçersiz adresler tekrar denenmemeli; yeni doğrulanabilir resmî bağlantı veya kullanıcının izinli özgün arşivi gerekiyor. Android CI 45868811066 henüz tamamlanmadı.

## 2026-11-26 — Resmî dağıtım paketi araştırması
Android CI 45868811066 gerçek iş loglarında 42/42 Gradle işlemi başarılı (43s), APK/AAB artifact 15141541910 yüklendi. Orijinal ZIP içindeki 01_CODEX_TALIMATLARI ve 03_ICERIK_KAYNAKLARI yeniden okundu. Kurumun yeni https://qurancomplex.gov.sa/fonts kataloğunda HAFS doğrudan indirmesi görünmüyor; geliştirici platformu yakında olarak belirtilmiş. Yeni resmî https://qurancomplex.gov.sa/mushaf-computer-publishing sayfası Hafs/Verş yazı tipleri içeren 3.16 Windows paketini ve SHA-256 özetini yayımlıyor. 87b3f43 commitinde yalnızca resmî paketi indirme, SHA-256 doğrulama ve ZIP içeriğini listeleme CI işlemi eklendi. Windows programı çalıştırılmayacak; özgün HAFS ve lisans dosyası doğrulanmadan aktarım yok. Alt domain bağlanmayacak (kullanıcı kararı). Font henüz bulunmuş sayılmaz.

## 2026-11-26 — Resmî Mushaf paketi doğrulandı
CI 45869570928 (iş 140081165460) başarılı: Mushaf Publisher 3.16 ZIP dosyası kurumun yayımladığı SHA-256 ile eşleşti. Arşivde SetupProject.msi ve setup.exe var; doğrudan font bulunmadı. Android CI 45869570994 (iş 140081165782) 42/42 Gradle işlemi başarılı (40s), artifact 15141917153 yüklendi. 101877dc commitinde msiextract ile kurulum çalıştırmadan MSI içeriğini açan, font adlarını ve lisans metadata bilgilerini okuyan yeni CI incelemesi eklendi. Font ve izin kapsamı doğrulanmadan uygulamaya aktarım yok.

## 2026-11-26 — Özgün HAFS yazı tipi bulundu
CI 45870196155 (iş 140083310980) resmî, SHA-256 doğrulanmış MSI paketinden 103 fontu statik olarak çıkardı. İstenen dosya Fonts Folder/uthmanic_hafs_v20.ttf, aile adı KFGQPC_HAFS_Uthmanic_Script_H, Version 2.00 (6 Şubat 2023), SHA-256 abfdc24eba06d05c34879e2514791d6bb25471b8ae3a1d436efa88b4d4351126. Gömülü elektronik lisansın ilk bölümü görüldü, tam metin henüz incelenmedi. Kullanıcı yazılı izin aldığını bildirdi. Android CI 45870196160: 42/42 Gradle işlemi başarılı (28s), artifact 15141398427. 577c862 commitinde tam lisans ve Tanzil karakter kapsamı kontrolü eklendi. Fontun baytları depoya yüklenmedi, Yazi.kt değiştirilmedi. Tam lisans ve karakter kapsamı sonucu incelenince entegrasyon yapılacak. Orijinal ZIP'teki çalışma sırası tekrar doğrulandı.

## 2026-11-26 — HAFS entegrasyonu tamamlandı
DÜZELTİLDİ: CI 45870466897 iş 140084232654 tam gömülü lisansı gösterdi; Tanzil metninde 68 Unicode karakterin tamamı font cmap tablosunda mevcut. CI 45870625546 iş 140084787519 özgün font SHA-256 kontrolü, lisans aktarımı ve Yazi.kt MushafYazi değişikliğini başarıyla tamamladı; 690f4df commitinde res/font/kfgqpc_hafs.ttf (290136 bayt) ve assets/lisanslar/kfgqpc_hafs_EULA.txt mevcut. Font değiştirilmedi. Önceki Android CI 45870625600 başarılı fakat font commitinden önceki SHA'ya ait. CI 45870711460 da API'de 33bff59 SHA'sını gösteriyor; fontlu HEAD'in derlemesini açıkça doğrulamak için bu günlük commitinde [skip ci] kullanılmıyor. Aşama 3 için ZIP'teki 01_CODEX_TALIMATLARI ve TASARIM_KURALLARI §4 doğrudan okundu; fontlu derleme doğrulanmadan ortak bileşenlere geçilmedi.
