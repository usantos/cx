#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "$0")/.." && pwd)"
output_dir="${1:-$project_dir/build/visual-parity}"
application_id="br.gov.caixa.loterias.apostas"
activity="$application_id/.MainActivity"
route_catalog="$project_dir/docs/COMPOSE_ROUTES.txt"
routes=()
while IFS= read -r route; do
  [[ -n "$route" ]] && routes+=("$route")
done < "$route_catalog"

mkdir -p "$output_dir"
cd "$project_dir"
./gradlew :app:assemblePrdDebug -PmockBackend=true
adb install -r app/build/outputs/apk/prd/debug/app-prd-debug.apk >/dev/null

for route in "${routes[@]}"; do
  launch_route="$route"
  [[ "$route" == "results/home/{modality}" ]] && launch_route="results/home/megasena"
  [[ "$route" == "betting/pool-detail" ]] && launch_route="betting/pool-detail/visual-test"
  [[ "$route" == "betting/combo-detail" ]] && launch_route="betting/combo-detail/1"
  [[ "$route" == "cart/favorite-detail" ]] && launch_route="cart/favorite-detail/1"
  adb shell am start -S -W -n "$activity" --es visual_test_route "$launch_route" </dev/null >/dev/null
  ready=false
  for _ in $(seq 1 8); do
    if adb shell uiautomator dump /sdcard/visual-route.xml </dev/null >/dev/null 2>&1 &&
      adb shell cat /sdcard/visual-route.xml </dev/null 2>/dev/null |
        grep -q "class=\"android.view.View\" package=\"$application_id\""; then
      ready=true
      break
    fi
    if [[ "$route" == "account/change-login" ]] &&
      adb shell cat /sdcard/visual-route.xml </dev/null 2>/dev/null | grep -q 'package="com.android.chrome"'; then
      ready=true
      break
    fi
    sleep 0.5
  done
  if [[ "$ready" != true ]]; then
    echo "Falha ao renderizar a rota $route" >&2
    exit 1
  fi
  filename="${route//\//_}"
  filename="${filename//\?/_}"
  filename="${filename//\&/_}"
  filename="${filename//=/_}.png"
  adb exec-out screencap -p </dev/null > "$output_dir/$filename"
done

echo "${#routes[@]} screenshots gravados em $output_dir"
