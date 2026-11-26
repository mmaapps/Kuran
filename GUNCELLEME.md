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
