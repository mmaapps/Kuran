#!/data/data/com.termux/files/usr/bin/bash
# Release adayı: kalıcı anahtar yalnız yerel cihazda kalır. Otomatik yayın YOK.
set -euo pipefail
umask 077
repo=mmaapps/Kuran
key="$HOME/imzalar/kurankerim.p12"
alias=kurankerim
run_id="${1:?Release adayı run ID gerekli}"
[[ "$run_id" =~ ^[0-9]+$ ]] || exit 1
[[ -r "$key" ]] || { echo "Mevcut anahtar bulunamadı: $key"; exit 1; }
for cmd in gh apksigner aapt sha256sum; do
  command -v "$cmd" >/dev/null || { echo "$cmd bulunamadı."; exit 1; }
done
gh auth status >/dev/null
[[ "$(gh api "repos/$repo/actions/runs/$run_id" --jq '.conclusion')" == success ]]
[[ "$(gh api "repos/$repo/actions/runs/$run_id" --jq '.path')" == .github/workflows/release-adayi.yml ]]
sha="$(gh api "repos/$repo/actions/runs/$run_id" --jq '.head_sha')"
[[ "$sha" =~ ^[0-9a-f]{40}$ ]]
work="$(mktemp -d)"
trap 'rm -rf -- "$work"' EXIT
gh run download "$run_id" -R "$repo" -n KuranKerim-release-aday -D "$work"
[[ "$(cat "$work/commit.txt")" == "$sha" ]]
(cd "$work" && sha256sum -c SHA256SUMS)
g="$(gh api "repos/$repo/contents/app/build.gradle.kts?ref=$sha" --jq '.content' | base64 -d)"
code="$(printf '%s\n' "$g" | sed -nE 's/^[[:space:]]*versionCode = ([0-9]+)[[:space:]]*$/\1/p')"
name="$(printf '%s\n' "$g" | sed -nE 's/^[[:space:]]*versionName = "([^"]+)"[[:space:]]*$/\1/p')"
[[ "$code" =~ ^[1-9][0-9]*$ && "$name" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]
verify_metadata() {
  aapt dump badging "$1" > "$work/metadata.txt"
  grep -F "package: name='com.atasoyyazilim.kurankerim' versionCode='$code' versionName='$name'" "$work/metadata.txt"
  if grep -q '^application-debuggable' "$work/metadata.txt"; then echo "Debug APK reddedildi."; exit 1; fi
}
verify_metadata "$work/KuranKerim-release-aday.apk"
echo "Mevcut anahtarın şifresini yalnız Termux ekranına gir."
apksigner sign --ks "$key" --ks-type PKCS12 --ks-key-alias "$alias" --out "$work/KuranKerim-$code.apk" "$work/KuranKerim-release-aday.apk"
apksigner verify --verbose --print-certs "$work/KuranKerim-$code.apk"
verify_metadata "$work/KuranKerim-$code.apk"
[[ -d "$HOME/storage/downloads" ]] || { echo "Önce termux-setup-storage çalıştır."; exit 1; }
out="$HOME/storage/downloads/KuranKerim-$code.apk"
# Önceden imzalanmış test dosyasını sessizce ezme.
if [[ -e "$out" ]]; then
  out="$HOME/storage/downloads/KuranKerim-$code-release-aday.apk"
fi
cp -- "$work/KuranKerim-$code.apk" "$out"
sha256sum "$out"
printf 'Yerel imzalı release adayı: %s\nSürüm: %s (%s)\nCommit: %s\n' "$out" "$name" "$code" "$sha"
echo "Kararlı yayın yapılmadı; cihaz ve içerik doğrulaması gerekli."
