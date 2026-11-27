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

## 2026-11-26 — Fontlu Android derlemesi ve Aşama 3
DÜZELTİLDİ: Fontlu 14e8df3 commitinde Android CI 45870857314 (iş 140085587668) 42/42 Gradle işlemi başarılı (28s), APK/AAB artifact 15142876778. Aşama 2 font ve metin aktarımı tamamlandı.
DEĞİŞİKLİK: ZIP içindeki TASARIM_KURALLARI §4, Namaz.dc.html, Hadis.dc.html, Zikirmatik.dc.html, Hatim.dc.html, EzanVakti.dc.html ve Mushaf.dc.html doğrudan incelendi. SegmentKontrol, Cip, IlerlemeHalkasi ve OynaticiCubugu eklendi. Dokunma alanı görünür ölçüden ayrı en az 44 dp tutuldu. Ses oynatıcı yalnız UI; izinli ses yoksa düğmeler etkinleştirilmiyor. Ekranlara henüz bağlanmadı; KelimeAltSayfasi ve BosDurum sırada. Aşama 3 derlemesi ve görsel doğrulama bekliyor.

## 2026-11-26 — Aşama 3: kalan ortak bileşenler
DÜZELTİLDİ: CI 45871215179 (iş 140086809224) gerçek loglarda 42/42 Gradle işlemi başarılı (40s), artifact 15143012397. Önceki dört bileşen derlendi. Özgün KuranKerim_Proje.zip içindeki 01_CODEX_TALIMATLARI.md, TASARIM_KURALLARI.md §4 ve Mushaf.dc.html yeniden okundu. KelimeAltSayfasi ve BosDurum eklendi. Kelime paneli 22 dp üst köşe, 52×58 dp harf çipleri, sağdan sola akış, 44 dp düğmeler; anlam/meal alanı yok. Okunuş/harf bilgileri yalnız doğrulanmış veri parametrelerinden gelir; demo dizisi kopyalanmadı. İzinli ses yoksa oynatma devre dışı. CSS gölge Android 10 dp elevation ile yaklaşık karşılanıyor; yan yana görsel test yapılmadı. BosDurum için kaynak yalnız ikon+tek cümle tarif ediyor; ikon çağıran ekran tarafından sağlanır, 16 dp kenar ve 8 dp ara seçildi. Aşama 3 tüm sekiz bileşenin kaynak kodu mevcut; son derleme, ekran bağlantısı ve görsel karşılaştırma bekliyor. Sonraki ekran Main.dc.html.

## 2026-11-26 — Aşama 4: Ana sayfa
DÜZELTİLDİ: Aşama 3 son CI 45871598428 (iş 140088118913), 42/42 Gradle işlemi başarılı (28s), artifact 15142513476. Sekiz bileşen derlenebilir; görsel karşılaştırma tamamlanmadı.
DEĞİŞİKLİK: KuranKerim_Proje.zip içindeki Main.dc.html, 01_CODEX_TALIMATLARI.md ve TASARIM_KURALLARI.md doğrudan okundu. AnaSayfaEkrani gerçek navigasyona bağlandı; 336 dp desenli başlık, özgün madalyonlar, saniyelik ezan geri sayımı, altı vakit, 314 dp başlangıçlı devam kartı, 12 kutu özgün SVG yollarıyla kodlandı. Vakitler yerel Adhan ve mevcut kullanıcı ayarlarından; kayıtlı konum DataStore'dan; sûre etiketi Tanzil metadata'sından okunuyor. HTML örnek tarih/saat/%14 canlı veri yerine kullanılmadı. Hatim yüzdesi Hatim aşamasına kadar gösterilmiyor. Gölge Compose 8 dp ile yaklaşık; küçük ekranda dikey kaydırma ve mevcut alt sekme çubuğu, güvenli alanlar kullanılıyor. Görsel yan yana cihaz testi ve fonksiyonel test henüz yok; bir sonraki ekran öncesi doğrulanmalı. Ezan vakitleri resmî Diyanet takvimi değildir; mevcut Adhan hesabı kullanılır, fark açıklaması Vakitler ekranı aşamasında kaynak talimatına göre bağlanacak.

## 2026-11-26 — Ana sayfa derlemesi ve Mushaf veri katmanı
Android CI 45871977589 (iş 140089426480), 42/42 işlem başarılı (33s); artifact 15142199472. Cihaz görsel karşılaştırması henüz yapılmadı. Orijinal KuranKerim_Proje.zip içindeki 01_CODEX_TALIMATLARI, 03_ICERIK_KAYNAKLARI, TASARIM_KURALLARI ve Mushaf.dc.html doğrudan açıldı. MushafVerisi ve IcerikDeposu.mushaf() eklendi; özgün Tanzil XML dosyalarına dokunulmadı. 114 sûre, 6236 âyet, 604 sayfa, 30 cüz, tüm âyetlerin kaynak metnine birebir eşitliği ve sınırları için dört JVM birim testi eklendi. Sadece test kapsamına JUnit bağımlılığı eklendi; uygulama çalışma zamanı bağımlılıkları değişmedi. Mushaf ekranı ve yatay kaydırma henüz eklenmedi. Ses izni yok; kelime dinleme düğmesi izinli ses yokken gizlenmeli (03_ICERIK_KAYNAKLARI §2). Yeni CI ve görsel/cihaz doğrulaması bekliyor.

## 2026-11-26 — Mushaf ekranı
DÜZELTİLDİ: CI 45872298287 (iş 140090558826) testDebugUnitTest başarılı (33s), AAB başarılı (12s), artifact 15142309742. Özgün ZIP 01_CODEX_TALIMATLARI, TASARIM_KURALLARI, 03_ICERIK_KAYNAKLARI, Mushaf.dc.html yeniden doğrudan okundu. DEĞİŞİKLİK: MushafEkrani gerçek navigasyona bağlandı; 604 sayfa RTL pager, KFGQPC metni, çift altın çerçeve, sayfa/cüz bilgisi, kelime paneli, DataStore yer imi ve 22–40 sp yazı boyutu; sayfa/kelime değişiminde ve ON_STOP'ta kayıt. Kelime sesi düğmesi izinli kayıt olmadığından gizleniyor, tilâvet pasif, harf adları hazır Elif-Ba verisinden. Meal, örnek okunuş, yapay ses yok. SORU/NOT: Özgün tasarım yalnız Fâtiha örneğiyle kelime akışı gösteriyor; Tanzil metadata sayfa sınırı sağlar ancak basılı Mushaf'ın satır koordinatlarını sağlamaz. FlowRow cihaz genişliğine göre satır sarar; fiziksel baskıyla birebir satır hizası henüz doğrulanmadı. Varsayılan 28 sp kuralı HTML 27 px örneğine göre önceliklendirildi. Aa düğmesi 2 sp adımla 22–40 aralığında döner; etkileşim tasarımda tarif edilmedi. Gölge 6 dp yaklaşık. Sûre adı Tanzil latinAd üzerinden (Fâtiha etiketi mevcut), tüm Türkçe sûre adlarının doğrulanması ve cihaz görsel karşılaştırması bekliyor. Kayıtlı yer ve sayfa kaydırma gerçek cihazda henüz sınanmadı. CI bekliyor; kararlı yayın yok.

## 2026-11-26 — Ezan vakitleri ekranı ve alarm ayarları
CI 45873036257 (iş 140093054467) Mushaf birim testleri 28s ve AAB 12s başarılı; artifact 15143389108. Cihaz görsel testi yok. Orijinal KuranKerim_Proje.zip içindeki 01_CODEX_TALIMATLARI, TASARIM_KURALLARI, 03_ICERIK_KAYNAKLARI ve EzanVakti.dc.html okundu. EzanVaktiEkrani gerçek navigasyona bağlandı: 132 dp halka (r58, 9 dp), haftalık takvim 62 dp, vakit satırları 56 dp, 4 ayar ve 5 sabah gecikmesi. Canlı hesap yerel Adhan; resmî takvim olmadığı açıkça yazıldı. HATA: önceki alarm planı kapatılan alarmları iptal etmiyordu, ses kapalıysa sonraki sayım da yenilenmeyebiliyordu. DÜZELTİLDİ: eski PendingIntent'ler iptal edilip tekrar kurulur; ses kapalıyken de zamanlayıcı yenilenir; bildirim kapatılırsa kaldırılır. İzinli ses yoksa servis başlatılmaz. Yaklaşık alarm için sistem gecikmesi olabilir; 1 dk garantisi kaldırıldı. SORU: sehirler.json mevcut değil, yalnız varsayılan İstanbul gösteriliyor, şehir seçim işlevi ve yaklaşık konum sonraki iş; yeni tasarım uydurulmadı. Tam zamanlı alarm izin yönlendirmesi, sessizde titreşim, ezan sonrası âyet ve cihaz alarm testleri tamamlanmadı. Görsel karşılaştırma bekliyor; ses izinleri bekliyor. 7 günlük tarih aralığı bugünün üç gün öncesi/sonrası seçildi (HTML örneği ortalanmış hafta). Yeni CI bekliyor.

## 2026-11-26 — Kullanıcı onaylı uygulama içi güncelleme
Ezan CI 45881640413 (iş logu: unit test 28s, AAB 13s, artifact 15147089535) başarılı. Kullanıcı çevrimdışı kuralına yalnız GitHub güncelleme istisnasını açıkça onayladı. Paket adı aynı, versionName 1.0.1 ve versionCode 2. GitHub Releases son kararlı yayın kontrolü, SHA-256/uzunluk, artan sürüm, paket ve aynı imza kontrolü, özel cache FileProvider ile Android onaylı yükleme ve Ayarlar'dan elle kontrol eklendi. 24 saatlik otomatik kontrol yalnız uygulama açılışında; kullanıcı verisi/analiz gönderilmez. Üç kural testi eklendi. Depo herkese açık, henüz release yok. Kalıcı imza anahtarı Termux'ta olduğu için CI çıktısı debug imzalıdır, kararlı yayın yapılmadı. Gerçek cihazda güncelleme testi henüz yapılmadı. Güncelleme ekranı özgün yeşil/altın renkler kullanır; yeni kullanıcı talebi dışında tasarıma müdahale yok.

## 2026-11-26 — Güncelleme derlemesi doğrulandı ve Termux imza betiği
CI 45882025257 (iş 140123424108) testDebugUnitTest 32s, AAB 14s, evrensel APK başarılı; artifact 15148105714. GitHub kalıcı secret durumu erişim kısıtından doğrulanamadı. Kullanıcı mevcut Termux anahtarının kurulu olduğunu belirtti. scripts/termux-imzala.sh eklendi: başarılı Android CI, kaynak sürüm kodu ve APK imza doğrulaması; mevcut anahtara dokunulmaz, GitHub'a anahtar/şifre gönderilmez. Betik gerçek Termux'ta çalıştırılmadı; imzalı test APK ve cihaz güncelleme doğrulaması bekliyor. Kararlı release yok.

## 2026-11-26 — Normal release adayı derleme hattı
Kullanıcı kurulumu tamamladığını bildirdi; cihaz sertifikası ve kurulu sürüm uzaktan doğrulanmadı. GitHub'da yayımlanmış Release yok. Ayrı release adayı CI ve mevcut Termux anahtarıyla yerel imzalama betiği eklendi; gerçek kalıcı anahtar CI'ya aktarılmaz. Debug ve release adayları ayrıldı, release adayında paket/sürüm, debug bayrağı, commit ve SHA-256 denetimleri bulunuyor. Yeni CI bekliyor; otomatik yayın yok. Uygulama versionCode 2 olarak korundu; sonraki kullanıcıya sunulan OTA sürümü en az 3 olmalı. Şehir verisi, izinli sesler ve diğer ekranlar bekliyor.

## 2026-11-26 — 1.0.2 alarm/bildirim iyileştirmesi
Release CI 45885456250 (iş 140134920737, 72/72 başarılı, artifact 15149266374) ve debug CI 45885456476 (iş 140134921744) başarılı. Release artifact indirildi; Tanzil XML ve KFGQPC fontu APK içinde doğrulandı (paket girdileri). Orijinal KuranKerim_Proje.zip içindeki EzanVakti.dc.html, Bildirim.dc.html, 01_CODEX_TALIMATLARI ve 03_ICERIK_KAYNAKLARI doğrudan incelendi. Tam alarm izni yoksa açıklama/yönlendirme; dönüşte plan yenileme; izin yarışında yaklaşık alarm; uygulama güncellenince ve saat/dilim değişince plan yenileme; sessiz modda kullanıcı seçimine göre kısa titreşim, ses engeli; üç birim testi eklendi. İzinli ses yoksa normal modda ses yok. SORU: tasarım izin yönlendirmesinin görünümünü tarif etmiyor, gerekli sistem izin açıklaması mevcut temada metin olarak kullanıldı; titreşim süresi 600 ms seçildi. versionCode 3, versionName 1.0.2. Release CI ayrıca kaynak XML/font dosyalarının APK içinde birebir eşitliğini kontrol eder. Şehir listesi, izinli ses, gerçek cihaz alarmları ve kararlı yayın bekliyor. Yeni CI henüz doğrulanmadı.

## 2026-11-26 — 1.0.2 CI doğrulaması
Debug CI 45885982872 (iş 140136669261): birim testleri 29s, AAB 12s başarılı; artifact 15149731068. Release CI 45885982824 (iş 140136668953): 72 görev başarılı, artifact 15149242369; paket com.atasoyyazilim.kurankerim, versionCode 3, versionName 1.0.2, debug bayrağı yok. Kaynak Tanzil XML ve HAFS fontu APK ile birebir eşitlik denetimini geçti. Gerçek cihaz, kalıcı imza ve ses lisansları bekliyor; kararlı yayın yok. Sıradaki ekranın özgün tasarımı tekrar erişilebilir olmadan yeni arayüz eklenmez.

## 2026-11-27 — Ezan Anı ekranı
Özgün ZIP doğrudan açıldı: EzanAni.dc.html, 01_CODEX_TALIMATLARI, TASARIM_KURALLARI, 03_ICERIK_KAYNAKLARI. EzanAniEkrani gerçek navigasyona bağlandı; 92 dp madalyonlar, 56 dp desen ve 21 çubuk, 1.1 sn dalga, 190 dp içerik kartı, 60×56 dp oynatma düğmesi, sustur/kapat. Gerçek MediaPlayer durumu StateFlow ile; bildirimden açılır; duraklatma/susturma gerçek hizmete bağlıdır. Ayet metni yalnız Tanzil'den, ses yalnız paket içi tilavet kaydı; eksik aralık atlanır. SORU: örnek ezan cümlesinin senkron kaynağı yok, 'ŞU AN OKUNAN' alanı BosDurum; aşamalar gerçek sesin sırasını gösterir, elle atlama etkin değil. Görsel karşılaştırma ve sesli cihaz testi yapılmadı. İzinli ezan/tilâvet dosyası hâlâ yok; yapay ses yok. 1.0.2 sürümü geliştirme adayı olarak korundu, kararlı yayın yok. Yeni CI bekliyor.
