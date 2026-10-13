#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "$0")/.." && pwd)"
legacy_source_dir="${LEGACY_SOURCE_DIR:?Informe LEGACY_SOURCE_DIR com o checkout isolado do legado}"
output_dir="${1:-$project_dir/build/visual-audit/legacy-compose}"
inventory="$project_dir/docs/LEGACY_UI_INVENTORY.tsv"
legacy_package="${LEGACY_PACKAGE:-br.gov.caixa.loterias.apostas.legacy}"
compose_package="${COMPOSE_PACKAGE:-br.gov.caixa.loterias.apostas.hmp}"
legacy_launcher="$legacy_package/br.gov.caixa.loterias.apostas.golden.LegacyGoldenLauncherActivity"
compose_activity="$compose_package/br.gov.caixa.loterias.apostas.MainActivity"
system_bar_height="${SYSTEM_BAR_HEIGHT:-112}"
route_filter="${ROUTE_FILTER:-.*}"
settle_seconds="${SETTLE_SECONDS:-10}"
focus_attempts="${FOCUS_ATTEMPTS:-30}"
legacy_fixture_mode="${LEGACY_FIXTURE_MODE:-empty}"
legacy_post_tap_text="${LEGACY_POST_TAP_TEXT:-}"
compose_post_tap_text="${COMPOSE_POST_TAP_TEXT:-}"
post_tap_settle_seconds="${POST_TAP_SETTLE_SECONDS:-1}"
fixture_expiry="$(date -u -v+45M '+%Y-%m-%dT%H:%M:%SZ' 2>/dev/null || date -u -d '+45 minutes' '+%Y-%m-%dT%H:%M:%SZ')"

command -v adb >/dev/null
command -v ffmpeg >/dev/null
adb shell pm path "$legacy_package" </dev/null >/dev/null
adb shell pm path "$compose_package" </dev/null >/dev/null
adb shell pm grant "$compose_package" android.permission.CAMERA </dev/null >/dev/null 2>&1 || true

mkdir -p \
  "$output_dir/legacy" \
  "$output_dir/compose" \
  "$output_dir/legacy-ui" \
  "$output_dir/compose-ui"
report="$output_dir/report.tsv"
printf 'legacy_owner\tcompose_route\tlegacy_target\tlegacy_result\tssim_without_status_bar\n' > "$report"
{
  printf 'legacy.package=%s\n' "$legacy_package"
  printf 'compose.package=%s\n' "$compose_package"
  printf 'legacy.fixture.mode=%s\n' "$legacy_fixture_mode"
  printf 'route.filter=%s\n' "$route_filter"
  printf 'settle.seconds=%s\n' "$settle_seconds"
  printf 'system.bar.height=%s\n' "$system_bar_height"
  printf 'device.serial=%s\n' "$(adb get-serialno </dev/null)"
  printf 'device.model=%s\n' "$(adb shell getprop ro.product.model </dev/null | tr -d '\r')"
  printf 'device.density=%s\n' "$(adb shell wm density </dev/null | tr -d '\r')"
  printf 'device.size=%s\n' "$(adb shell wm size </dev/null | tr -d '\r')"
} > "$output_dir/run.properties"

resolve_target() {
  local owner="$1"
  local source package class_name
  source="$(find "$legacy_source_dir/app/src/main/java" -type f -name "$owner" -print -quit)"
  [[ -n "$source" ]] || return 1
  package="$(tr '\r' '\n' < "$source" | sed -n 's/^package \([^;]*\);/\1/p' | head -1)"
  class_name="${owner%.java}"
  printf '%s.%s' "$package" "$class_name"
}

launch_route_for() {
  case "$1" in
    betting/pool-detail) printf '%s' 'betting/pool-detail/visual-test' ;;
    betting/combo-detail) printf '%s' 'betting/combo-detail/1' ;;
    cart/favorite-detail) printf '%s' 'cart/favorite-detail/1' ;;
    cart/included) printf '%s' "cart/included?modality=MEGA_SENA&contest=2800&drawDate=2026-09-10&amount=5.00" ;;
    cart/pool-included) printf '%s' "cart/pool-included?expiresAt=$fixture_expiry&modality=MEGA_SENA&contest=2800&drawDate=2026-09-10&amount=10.00" ;;
    *) printf '%s' "$1" ;;
  esac
}

ssim_for() {
  local legacy_image="$1"
  local compose_image="$2"
  ffmpeg -hide_banner -i "$legacy_image" -i "$compose_image" \
    -lavfi "[0:v]crop=iw:ih-${system_bar_height}:0:${system_bar_height}[a];[1:v]crop=iw:ih-${system_bar_height}:0:${system_bar_height}[b];[a][b]ssim" \
    -f null - 2>&1 | sed -n 's/.* All:\([0-9.]*\) .*/\1/p' | tail -1
}

average_luma_for() {
  ffmpeg -hide_banner -i "$1" -vf signalstats,metadata=print -f null - 2>&1 |
    sed -n 's/.*lavfi.signalstats.YAVG=//p' | tail -1
}

tap_visible_text() {
  local label="$1"
  local dump_path="/sdcard/codex-parity-window.xml"
  local node="" bounds left top right bottom
  for _ in $(seq 1 5); do
    adb shell rm -f "$dump_path" </dev/null >/dev/null 2>&1 || true
    adb shell uiautomator dump --compressed "$dump_path" </dev/null >/dev/null 2>&1 || true
    node="$(adb shell cat "$dump_path" </dev/null 2>/dev/null | tr '>' '\n' | grep -F "text=\"$label\"" | head -1 || true)"
    [[ -n "$node" ]] && break
    sleep 0.5
  done
  [[ -n "$node" ]] || {
    echo "Texto não encontrado para toque pós-abertura: $label" >&2
    return 1
  }
  bounds="$(printf '%s' "$node" | sed -n 's/.*bounds="\[\([0-9]*\),\([0-9]*\)\]\[\([0-9]*\),\([0-9]*\)\]".*/\1 \2 \3 \4/p')"
  read -r left top right bottom <<< "$bounds"
  [[ -n "${bottom:-}" ]] || return 1
  adb shell input tap "$(((left + right) / 2))" "$(((top + bottom) / 2))" </dev/null >/dev/null
}

reset_legacy_app() {
  adb shell am force-stop com.android.settings </dev/null >/dev/null 2>&1 || true
  adb shell am force-stop com.android.chrome </dev/null >/dev/null 2>&1 || true
  adb shell input keyevent 3 </dev/null >/dev/null
  adb shell pm clear "$legacy_package" </dev/null >/dev/null
  for permission in \
    android.permission.POST_NOTIFICATIONS \
    android.permission.ACCESS_FINE_LOCATION \
    android.permission.ACCESS_COARSE_LOCATION \
    android.permission.READ_PHONE_NUMBERS \
    android.permission.READ_PHONE_STATE \
    android.permission.CAMERA \
    android.permission.READ_CONTACTS \
    android.permission.GET_ACCOUNTS \
    android.permission.READ_EXTERNAL_STORAGE \
    android.permission.WRITE_EXTERNAL_STORAGE \
    android.permission.BLUETOOTH_CONNECT; do
    adb shell pm grant "$legacy_package" "$permission" </dev/null >/dev/null 2>&1 || true
  done
}

reset_compose_app() {
  adb shell am force-stop com.android.settings </dev/null >/dev/null 2>&1 || true
  adb shell am force-stop com.android.chrome </dev/null >/dev/null 2>&1 || true
  adb shell input keyevent 3 </dev/null >/dev/null
  adb shell pm clear "$compose_package" </dev/null >/dev/null
  for permission in \
    android.permission.POST_NOTIFICATIONS \
    android.permission.ACCESS_FINE_LOCATION \
    android.permission.ACCESS_COARSE_LOCATION \
    android.permission.READ_PHONE_NUMBERS \
    android.permission.READ_PHONE_STATE \
    android.permission.CAMERA \
    android.permission.READ_CONTACTS; do
    adb shell pm grant "$compose_package" "$permission" </dev/null >/dev/null 2>&1 || true
  done
}

awk -F '\t' 'NR > 1 && $4 == "compose" && $1 ~ /Activity\.java$/ && $3 !~ /#/ {print $1 "\t" $3}' "$inventory" |
  sort -u |
  while IFS=$'\t' read -r owner route; do
    [[ "$route" =~ $route_filter ]] || continue
    target="$(resolve_target "$owner" || true)"
    if [[ -z "$target" ]]; then
      printf '%s\t%s\t\tmissing-source\t\n' "$owner" "$route" >> "$report"
      continue
    fi

    reset_legacy_app
    adb shell am start -n "$legacy_launcher" \
      --es legacy_golden_target "$target" \
      --ez legacy_golden_fixture true \
      --es legacy_golden_fixture_mode "$legacy_fixture_mode" </dev/null >/dev/null 2>&1 || true
    focus=""
    for _ in $(seq 1 "$focus_attempts"); do
      focus="$(adb shell dumpsys window </dev/null | sed -n 's/.*mCurrentFocus=Window{[^ ]* [^ ]* \([^}]*\)}.*/\1/p' | head -1)"
      [[ "$focus" == *"$target"* || "$focus" == *'Application Error'* ]] && break
      sleep 0.5
    done

    if [[ "$focus" != *"$target"* ]]; then
      result="redirected"
      [[ "$focus" == *'Application Error'* ]] && result="requires-fixture"
      printf '%s\t%s\t%s\t%s\t\n' "$owner" "$route" "$target" "$result" >> "$report"
      adb shell input keyevent 66 </dev/null >/dev/null 2>&1 || true
      continue
    fi

    sleep "$settle_seconds"
    if [[ -n "$legacy_post_tap_text" ]]; then
      tap_visible_text "$legacy_post_tap_text"
      sleep "$post_tap_settle_seconds"
    fi
    # Algumas Activities aceitam o Intent e só encerram depois da inicialização
    # assíncrona. Revalida o foco após o tempo de estabilização para não comparar
    # a Home do launcher (ou outra tela) como se fosse a referência legada.
    settled_focus="$(adb shell dumpsys window </dev/null | sed -n 's/.*mCurrentFocus=Window{[^ ]* [^ ]* \([^}]*\)}.*/\1/p' | head -1)"
    if [[ "$settled_focus" != *"$target"* ]]; then
      result="redirected-after-settle"
      [[ "$settled_focus" == *'Application Error'* ]] && result="requires-fixture"
      printf '%s\t%s\t%s\t%s\t\n' "$owner" "$route" "$target" "$result" >> "$report"
      continue
    fi
    filename="${route//\//_}.png"
    legacy_image="$output_dir/legacy/$filename"
    compose_image="$output_dir/compose/$filename"
    adb exec-out screencap -p </dev/null > "$legacy_image"

    # A referência sem backend pode abrir o alerta global de timeout. Ele não
    # representa a tela solicitada e não deve produzir um SSIM enganoso.
    legacy_dump_path="/sdcard/codex-parity-legacy.xml"
    adb shell rm -f "$legacy_dump_path" </dev/null >/dev/null 2>&1 || true
    adb shell uiautomator dump --compressed "$legacy_dump_path" </dev/null >/dev/null 2>&1 || true
    adb pull "$legacy_dump_path" "$output_dir/legacy-ui/${filename%.png}.xml" </dev/null >/dev/null 2>&1 || true
    if adb shell cat "$legacy_dump_path" </dev/null 2>/dev/null |
      grep -Eiq 'Tempo de conexão expirou|conexão expirou|Não foi possível concluir esta operação'; then
      printf '%s\t%s\t%s\tlegacy-network-error\t\n' "$owner" "$route" "$target" >> "$report"
      continue
    fi
    if [[ "$route" != "onboarding/unavailable" ]] &&
      adb shell cat "$legacy_dump_path" </dev/null 2>/dev/null |
        grep -Eiq 'aplicativo está temporariamente indisponível'; then
      printf '%s\t%s\t%s\tlegacy-network-error\t\n' "$owner" "$route" "$target" >> "$report"
      continue
    fi

    legacy_luma="$(average_luma_for "$legacy_image")"
    # Em YUV, preto absoluto é reportado pelo ffmpeg como luma 16 (limited range).
    if [[ -z "$legacy_luma" ]] || awk "BEGIN {exit !($legacy_luma <= 16.5)}"; then
      printf '%s\t%s\t%s\tnot-rendered\t\n' "$owner" "$route" "$target" >> "$report"
      continue
    fi

    # Evita que um processo legado lento exiba ANR sobre a rota Compose durante a segunda captura.
    adb shell am force-stop "$legacy_package" </dev/null >/dev/null 2>&1 || true
    compose_route="$(launch_route_for "$route")"
    # `adb shell` recompõe os argumentos em um comando remoto. Sem escapar `&`,
    # rotas com query string eram truncadas e os parâmetros seguintes viravam
    # processos de shell em segundo plano.
    compose_route_shell="${compose_route//&/\\&}"
    reset_compose_app
    compose_focus=""
    for _ in $(seq 1 "$focus_attempts"); do
      adb shell am start -n "$compose_activity" \
        --es visual_test_route "$compose_route_shell" </dev/null >/dev/null 2>&1 || true
      sleep 0.5
      compose_focus="$(adb shell dumpsys window </dev/null | sed -n 's/.*mCurrentFocus=Window{[^ ]* [^ ]* \([^}]*\)}.*/\1/p' | head -1)"
      [[ "$compose_focus" == *"$compose_package"* ]] && break
    done
    if [[ "$compose_focus" != *"$compose_package"* ]]; then
      printf '%s\t%s\t%s\tcompose-not-focused\t\n' "$owner" "$route" "$target" >> "$report"
      continue
    fi
    sleep "$settle_seconds"
    if [[ -n "$compose_post_tap_text" ]]; then
      tap_visible_text "$compose_post_tap_text"
      sleep "$post_tap_settle_seconds"
    fi
    adb exec-out screencap -p </dev/null > "$compose_image"
    compose_dump_path="/sdcard/codex-parity-compose.xml"
    adb shell rm -f "$compose_dump_path" </dev/null >/dev/null 2>&1 || true
    adb shell uiautomator dump --compressed "$compose_dump_path" </dev/null >/dev/null 2>&1 || true
    adb pull "$compose_dump_path" "$output_dir/compose-ui/${filename%.png}.xml" </dev/null >/dev/null 2>&1 || true

    ssim="$(ssim_for "$legacy_image" "$compose_image")"
    printf '%s\t%s\t%s\tcompared\t%s\n' "$owner" "$route" "$target" "$ssim" >> "$report"
  done

echo "Relatorio gravado em $report"
