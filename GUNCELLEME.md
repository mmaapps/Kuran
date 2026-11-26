# GitHub APK güncellemeleri

26 Kasım 2026 kullanıcı onayı: yalnız güncelleme için INTERNET ve REQUEST_INSTALL_PACKAGES. Diğer işlevler çevrimdışı.
Otomatik kontrol uygulama açıldığında başarılı kontrolden sonra 24 saat arayla; Ayarlar'dan elle kontrol. Sadece herkese açık mmaapps/Kuran son kararlı Release; taslak/ön sürüm kabul edilmez. GitHub API digest, boyut, paket adı, versionName, artan versionCode ve kurulu APK ile aynı imza zorunlu. İndirme ve yükleme kullanıcı onayıyla. Anahtar ve şifre yalnız kullanıcının Termux'unda kalır.

## Yayın protokolü
- Kararlı sürüm için testler ve içerik/lisans doğrulaması tamamlanmalı. Bu commit bir geliştirme adayıdır, kararlı yayın değildir.
- versionCode her yüklemede artırılır, versionName X.Y.Z; Release etiketi vX.Y.Z.
- Kalıcı imzalı APK adı KuranKerim-<versionCode>.apk (ör. KuranKerim-2.apk). Release'e tek uygun APK yükle; GitHub API asset digest alanında sha256:... bulunmalı.
- APK'yı mevcut kurankerim.p12 ile imzala; apksigner verify --verbose --print-certs ile kontrol et. Uygulamada kurulu imza farklıysa Android yükseltme yapamaz. Veri kaybına neden olacak kaldırma önermeyin.
- GitHub Actions debug artifact'i kararlı release olarak yayımlanmaz. Release öncesi gerçek cihaz testi gerekir.
- Ayarlar ekranı tamamlanınca mevcut güncelleme kontrolü düğmesini özgün tasarıma taşı.

## Termux: mevcut anahtarla tek komutlu test imzası
`scripts/termux-imzala.sh <Android derleme run ID> <versionCode>` başarılı Android iş akışının APK'sını indirir, kaynak commit'indeki versionCode'u karşılaştırır, mevcut PKCS12 anahtarıyla yerelde imzalar, apksigner doğrulaması ve SHA-256 çıktısını verir. Şifre yalnız Termux terminalinde girilir; anahtar GitHub'a yüklenmez. Debug tabanlı imzalı test APK kararlı yayın değildir.

## Normal release derleme hattı
Actions > Kararlı APK adayı, release AAB ve evrensel APK üretir; debug bayrağının olmadığını, paket adı/sürümünü, imzalanabilirliği ve SHA-256'yı denetler. CI geçici sertifikası dağıtım imzası değildir. `scripts/termux-release-imzala.sh RUN_ID` aynı kalıcı anahtar ile yerel release adayını imzalar. Aday, kararlı Release olarak otomatik yayımlanmaz. İmzalı adayın cihazda önceki sürümün üzerine kurulması, içerik ve lisans kontrolleri tamamlandıktan sonra yalnız KuranKerim-<versionCode>.apk dosyası vX.Y.Z etiketli yayına yüklenebilir. İlk güncellemeli 1.0.1 (2) kuruluysa bir sonraki OTA sürüm kodu en az 3 olmalıdır. İlk kurulum tekrarlanmaz.
