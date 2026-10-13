#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PACKAGE_NAME="${1:-br.gov.caixa.loterias.apostas}"
ACTIVITY_CLASS="${2:-br.gov.caixa.loterias.apostas.MainActivity}"
ACTIVITY_NAME="$PACKAGE_NAME/$ACTIVITY_CLASS"
ROUTE_CATALOG="$PROJECT_DIR/docs/COMPOSE_ROUTES.txt"
FAILURES=0
COUNT=0
EXPECTED_COUNT="$(grep -cve '^[[:space:]]*$' "$ROUTE_CATALOG")"

if ! adb get-state >/dev/null 2>&1; then
  echo "Nenhum emulador/dispositivo ADB conectado."
  exit 2
fi

adb logcat -c </dev/null
while IFS= read -r route; do
  [[ -z "$route" ]] && continue
  launch_route="$route"
  [[ "$route" == "betting/pool-detail" ]] && launch_route="betting/pool-detail/SMOKE"
  [[ "$route" == "betting/combo-detail" ]] && launch_route="betting/combo-detail/1"
  [[ "$route" == "cart/favorite-detail" ]] && launch_route="cart/favorite-detail/1"
  adb shell am force-stop "$PACKAGE_NAME" </dev/null >/dev/null
  adb shell am start -W -n "$ACTIVITY_NAME" --es visual_test_route "$launch_route" </dev/null >/dev/null

  started=false
  for _ in {1..20}; do
    if adb shell pidof "$PACKAGE_NAME" </dev/null >/dev/null; then
      started=true
      break
    fi
    sleep 0.1
  done
  rendered=false
  if [[ "$started" == true ]]; then
    for _ in {1..4}; do
      if adb shell uiautomator dump /sdcard/compose-route.xml </dev/null >/dev/null 2>&1 &&
        adb shell cat /sdcard/compose-route.xml </dev/null 2>/dev/null |
          grep -q "class=\"android.view.View\" package=\"$PACKAGE_NAME\""; then
        rendered=true
        break
      fi
      if [[ "$route" == "account/change-login" ]] &&
        adb shell cat /sdcard/compose-route.xml </dev/null 2>/dev/null |
          grep -q 'package="com.android.chrome"'; then
        # Esta rota abre imediatamente a Conta CAIXA no navegador seguro. O processo do
        # app deve continuar vivo para receber o retorno e a UI visível pertence ao Chrome.
        rendered=true
        break
      fi
      sleep 0.15
    done
  fi
  if [[ "$started" != true ]] || [[ "$rendered" != true ]] ||
    ! adb shell pidof "$PACKAGE_NAME" </dev/null >/dev/null; then
    echo "FALHOU: $launch_route"
    FAILURES=$((FAILURES + 1))
  fi
  COUNT=$((COUNT + 1))
done < "$ROUTE_CATALOG"

echo "Rotas verificadas: $COUNT; falhas: $FAILURES"
if [[ "$COUNT" -ne "$EXPECTED_COUNT" ]]; then
  echo "Catálogo incompleto: esperadas $EXPECTED_COUNT rotas e executadas $COUNT."
  exit 1
fi
if adb logcat -d -b crash '*:V' </dev/null | grep -q "Process: $PACKAGE_NAME"; then
  adb logcat -d -b crash '*:V' </dev/null
  exit 1
fi
[[ "$FAILURES" -eq 0 ]]
