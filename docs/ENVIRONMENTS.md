# Ambientes e product flavors

O módulo `:app` usa o namespace oficial `br.gov.caixa.loterias.apostas` e mantém as três flavors do aplicativo anterior.

| Flavor | Application ID | API SILCE padrão | SSO | Firebase | FilaBRA |
|---|---|---|---|---|---|
| `prd` | `br.gov.caixa.loterias.apostas` | `mobileloterias.caixa.gov.br` | `login.caixa.gov.br` | `app-loterias-simlo-prd` | `caixa` / `loteriasprd` |
| `tqs` | `br.gov.caixa.loterias.apostas.hmp` | `silce.simlo.tqs.caixa.gov.br` | `logindes.caixa.gov.br` | `app-loterias-simlo-nprd` | `caixaloteriasdev` / `loteriasdes` |
| `automacao` | `br.gov.caixa.loterias.apostas.hmp` | `simlo.tqs.caixa.gov.br` | `logindes.caixa.gov.br` | `app-loterias-simlo-nprd` | `caixaloteriasdev` / `loteriasdes` |

O sufixo `.hmp` de TQS e automação foi preservado para manter compatibilidade com os cadastros Firebase e SSO existentes. Por isso essas duas variantes substituem uma à outra quando instaladas no mesmo dispositivo; produção pode permanecer instalada paralelamente.

## Comandos

```bash
./gradlew :app:assemblePrdDebug
./gradlew :app:assembleTqsDebug
./gradlew :app:assembleAutomacaoDebug
```

Os APKs são gerados, respectivamente, em:

```text
app/build/outputs/apk/prd/debug/
app/build/outputs/apk/tqs/debug/
app/build/outputs/apk/automacao/debug/
```

Versões produtivas usam `PRD_VERSION_CODE` e `PRD_VERSION_NAME`. TQS e automação usam `DES_VERSION_CODE` e `DES_VERSION_NAME`, definidos em `gradle.properties`.

Logs HTTP/cURL continuam controlados por `-PnetworkLogging=true` somente em builds debug. Toda variante release força os logs para `false`.
