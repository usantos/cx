#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/../.." && pwd)"
out="$root/build/verification/marketplace-paired-20261015/native-runner"
sdk="${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}"
tools="$sdk/build-tools/36.0.0"
platform="$sdk/platforms/android-36/android.jar"
java_bin="${JAVA_HOME:?Configure JAVA_HOME para um JDK 21}/bin"
mkdir -p "$out/classes" "$out/dex" "$out/assets"
cp "$root/core/network/src/main/resources/marketplace-parity-fixture.json" "$out/assets/"
cat > "$out/AndroidManifest.xml" <<'XML'
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="br.gov.caixa.parity.marketplace">
 <uses-sdk android:minSdkVersion="26" android:targetSdkVersion="36"/>
 <application android:label="CX visual parity runner" android:debuggable="true"/>
 <instrumentation android:name="br.gov.caixa.parity.LegacyMarketplaceInstrumentation" android:targetPackage="br.gov.caixa.loterias.apostas.cxv2validation"/>
</manifest>
XML
"$java_bin/javac" --release 21 -cp "$platform" -d "$out/classes" "$root/scripts/visual-parity/LegacyMarketplaceInstrumentation.java"
"$java_bin/jar" cf "$out/classes.jar" -C "$out/classes" .
"$tools/d8" --lib "$platform" --min-api 26 --output "$out/dex" "$out/classes.jar"
"$tools/aapt2" link -I "$platform" --manifest "$out/AndroidManifest.xml" -A "$out/assets" -o "$out/unsigned.apk"
(cd "$out/dex" && zip -q -u "$out/unsigned.apk" classes.dex)
"$tools/zipalign" -f 4 "$out/unsigned.apk" "$out/aligned.apk"
"$tools/apksigner" sign --ks "$HOME/.android/debug.keystore" --ks-pass pass:android --key-pass pass:android --out "$out/runner.apk" "$out/aligned.apk"
