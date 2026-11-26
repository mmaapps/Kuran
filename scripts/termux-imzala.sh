#!/data/data/com.termux/files/usr/bin/bash
# Kullanıcının mevcut anahtarıyla yerel test APK'sını imzalar. Anahtarı yüklemez; yayın yapmaz.
set -euo pipefail
umask 077
repo=mmaapps/Kuran
key="$HOME/imzalar/kurankerim.p12"
alias=kurankerim
run_id="${1:-45882025257}"
code="${2:-2}"
[[ "$run_id" =~ ^[0-9]+$ && "$code" =~ ^[1-9][0-9]*$ ]] || { echo "Geçersiz derleme/sürüm numarası."; exit 1; }
[[ -r "$key" ]] || { echo "Mevcut anahtar bulunamadı: $key"; exit 1; }
for cmd in gh apksigner sha256sum; do
  command -v "$cmd" >/dev/null || { echo "$cmd bulunamadı."; exit 1; }
done
gh auth status >/dev/null
[[ "$(gh api "repos/$repo/actions/runs/$run_id" --jq '.conclusion')" == success ]] || { echo "Derleme başarılı değil."; exit 1; }
[[ "$(gh api "repos/$repo/actions/runs/$run_id" --jq '.path')" == .github/workflows/android.yml ]] || { echo "Beklenmeyen iş akışı."; exit 1; }
sha="$(gh api "repos/$repo/actions/runs/$run_id" --jq '.head_sha')"
[[ "$sha" =~ ^[0-9a-f]{40}$ ]] || exit 1
actual="$(gh api "repos/$repo/contents/app/build.gradle.kts?ref=$sha" --jq '.content' | base64 -d | sed -nE 's/^[[:space:]]*versionCode = ([0-9]+)[[:space:]]*$/\1/p')"
[[ "$actual" == "$code" ]] || { echo "Sürüm kodu uyuşmuyor."; exit 1; }
work="$(mktemp -d)"
trap 'rm -rf -- "$work"' EXIT
gh run download "$run_id" -R "$repo" -n KuranKerim-debug -D "$work"
[[ -s "$work/KuranKerim-debug.apk" ]] || { echo "APK yok."; exit 1; }
out="$HOME/storage/downloads/KuranKerim-$code.apk"
[[ -d "$HOME/storage/downloads" ]] || { echo "Önce termux-setup-storage çalıştır."; exit 1; }
echo "Mevcut kalıcı anahtarın şifresini sadece Termux ekranına gir."
apksigner sign --ks "$key" --ks-type PKCS12 --ks-key-alias "$alias" --out "$work/signed.apk" "$work/KuranKerim-debug.apk"
apksigner verify --verbose --print-certs "$work/signed.apk"
cp -- "$work/signed.apk" "$out"
sha256sum "$out"
echo "İmzalı test APK: $out"
echo "Kur'an-ı Kerim verilerini silmeden Android üzerinden güncelle. Bu betik kararlı yayın yapmaz."
