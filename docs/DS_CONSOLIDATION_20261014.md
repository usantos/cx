# Consolidação CaixaDS e validação integrada — 14/10/2026

Continuação em `codex/migracao-compose`, base `2cf348582`, preservando os ajustes solicitados de Home: concurso no card com registro encerrado e Dia de Sorte branco. Complementa `COMPOSE_CX_EXPERIMENTE_PARITY_20261014.md` e o relatório de runtime; não amplia a homologação de estados inacessíveis.

## Mudanças

| Padrão anterior nas features | Contrato centralizado em `:core:design-system` |
|---|---|
| `BettingOptionDialog` | `CaixaDSLegacyOptionDialog`: título/botões medidos como no nativo, viewport, scroll/tap, escolha pendente, confirmação e tags preservados. Cor literal substituída pelo token idêntico `LegacyAccountHelper` com alpha 0,6. |
| `BettingDSButton` | `CaixaDSLegacyBettingButton`: fontes, arredondamento em pixels, borda, ícones e parâmetros originais. |
| Campo, moeda, aviso e rodapé de conta | `CaixaDSLegacyAccountField`, `CaixaDSLegacyAccountInlineWarning`, `CaixaDSLegacyAccountSaveFooter`; centavos, locale pt-BR, espaços e seleção no fim preservados. Regras de limite e validações continuam na feature. |
| Texto, checkbox, seletor e estilo de conta | `CaixaDSLegacyAccountCopy`, `CaixaDSLegacyAccountCheckbox`, `CaixaDSLegacyAccountChoice`, `CaixaDSLegacyAccountTextStyle` e `CaixaDSLegacyAccountBackground`; `Gender` e textos de consentimento permanecem no consumidor. |
| Tema isolado da Home v2 | `CaixaDSHomeV2Theme`: mesmas métricas Material3 e fontScale 1 do CX. |
| Barra de suporte e HTML de termos/aceite | `CaixaDSLegacySupportTopBar` e `CaixaDSLegacyHtmlDocument`; HTML/CSS, links, JavaScript desabilitado e descarte do WebView preservados. |

Os recursos dos componentes de conta e a seta do suporte foram movidos para o DS, com o inventário XML atualizado. Os consumidores e testes de interação usam a implementação pública do DS. Previews adicionados em `CaixaDSLegacyParityCatalog.kt`.

## Gates

As duas falhas iniciais foram reproduzidas: `verifyCaixaDS` rejeitava a cor literal do seletor; `verifyComposeMigration` rejeitava os manifests de consulta ao APK CX usados como oráculo nos testes.

O gate de migração agora aceita somente os manifests androidTest de betting e onboarding com uma única query para `br.gov.caixa.loterias.apostas.cxv2validation`. Não permite permissões, Activities ou pacotes extras.

O gate do DS continua rejeitando cores literais e exige que Typography, imports de ícones Material e declarações de componentes com prefixo CaixaDS pertençam ao DS. As assinaturas genéricas também são verificadas. Isso não equivale a uma análise semântica completa de Kotlin nem a uma certificação visual.

`PYTHONOPTIMIZE=1 python3 scripts/check-compose-guardrails.py` passou em **13 verificações**: 11 entradas proibidas rejeitadas (permissão, Activity e pacote extra em cada um dos dois manifests; layout; cor literal; função CaixaDS genérica; Typography; ícone Material) e duas entradas válidas aprovadas. O script não depende de asserts para validar resultados ou proteger arquivos existentes e restaura os manifests em bytes. Executar separadamente da compilação, pois usa fixtures temporárias em src. Logs desta rodada estão em `guardrails/`, sob o diretório de evidências.

## Evidências desta rodada

`ANDROID_SERIAL=emulator-5558 ./gradlew modernCheck releaseCheck --offline --continue --console=plain` **aprovado em 5min17s, 2.154 tarefas**. Os três Releases minificados (PRD, TQS e Automação), lint, testes, inventários e gates de arquitetura/DS passaram, sem exclusões nem redução de limiares. Os relatórios JVM da matriz verificada somam **1.676 execuções de testes**, zero falhas/erros/ignorados; incluem a repetição do host por flavor.

Cobertura dos escopos exigidos: conta **97,7551%** (479/490 linhas), apostas **97,7033%** (1.021/1.045) e carrinho **96,0683%** (1.857/1.933), com mínimos de 95,01% preservados. Esses valores não são cobertura global de toda a aplicação.

A primeira execução terminou com sete falhas Android do carrinho durante um ANR do `com.android.systemui`, cujo diálogo capturava o foco/toques; a captura está em `device-diagnostic.png`. Depois da recuperação do AVD, os **54 testes do carrinho passaram**, sem alterar a produção ou as asserções desses testes. O primeiro log (20min37s) foi preservado; o sucesso integrado corresponde à segunda execução (`modern-release-final.log`).

A revisão estática independente das extrações não identificou mudanças adicionais de comportamento. As duas observações da revisão — assinatura genérica no gate e uso de asserts no script — foram corrigidas, e a execução otimizada das 13 verificações comprovou os casos exercitados.

As suítes Android adicionais cobriram **133 casos distintos**, com êxito em **131**, usando os perfis correspondentes:

| Suíte | Resultado |
|---|---|
| CaixaDS | 10/10 aprovados |
| Conta | 34/34 aprovados |
| Apostas | 77/79 casos com êxito entre os dois perfis; dois testes raster pendentes |
| Onboarding | 6/6 aprovados |
| Suporte/HTML | 4/4 aprovados |

O perfil padrão do AVD é 1280×2856/480dpi. Cinco testes de apostas falharam inicialmente por exigir 1080×2408/450dpi. As duas classes foram repetidas com essas dimensões/densidade e fontScale 1: os cinco testes de `BettingInstalledGridGeometryTest` passaram; os dois de `BettingPromptOriginTest` apontaram origem raster x=93, com expectativa x=95. Os demais limites do raster não foram avaliados depois dessa primeira asserção.

Uma execução controlada dos mesmos dois testes com os três fontes de apostas anteriores à extração, da base `2cf348582`, apresentou **a mesma diferença 95→93**. Os arquivos da árvore integrada foram restaurados byte a byte em `finally`, e seus hashes foram conferidos com o atestado anterior. A diferença é preexistente nesta comparação; sua causa e o pareamento exato com a captura do XCover7 continuam pendentes. Não se alteraram expectativas, margens ou fontes para mascará-la. Logs: `extracted-components-android.log`, `installed-raster-1080-450dpi.log`, `prompt-origin-head-baseline.log` e `extracted-android-results.json`.

Os **3 testes Android da Home v2 no host mock passaram**, incluindo geometria sob o tema do aplicativo, ações e destinos. `verifyCaixaDS` e `verifyComposeMigration` passaram novamente nesse comando após a restauração dos fontes usados no diagnóstico.

**Smoke dos 80 destinos:** a passagem completa aprovou 79/80 de primeira; `betting/favorites` teve timeout de inicialização do processo no Android. A repetição dirigida da mesma rota passou na primeira leitura da hierarquia, com processo vivo, `android.view.View` do pacote esperado e buffer de crashes sem referência ao aplicativo. Os **80 destinos renderizaram entre a passagem completa e essa repetição**. O primeiro log permanece com sua falha original; não é apresentado como uma execução única 80/80 sem intercorrências. Evidências: `smoke-80-routes.log`, `favorites-repeat-launch.txt`, `favorites-repeat.xml` e `favorites-repeat.json`.

Os artefatos locais ficam em `build/verification/ds-consolidation-20261014/`. O AVD de testes é `SimloComposeGates`, porta 5558, Android 16; o emulador 5554 não é utilizado. Resolução e densidade do AVD dedicado foram restauradas depois da comparação raster. `source-and-apk-attestation.json` registra a base, hashes dos fontes alterados e dos APKs reais; o APK mock foi preservado separadamente como `app-prd-mock-final.apk`.

Ao terminar, `verifyCaixaDS verifyComposeMigration :app:assemblePrdDebug` passou novamente, em 1min41s, sem a propriedade mock. O APK debug gerado voltou à configuração normal (`ENABLE_MOCK_BACKEND=false`); o APK mock de evidência permanece separado. O AVD headless criado para esta validação foi encerrado depois dos testes, liberando seus recursos.

## Reprodução

Instalar previamente no AVD o APK CX de validação indicado em `COMPOSE_CX_EXPERIMENTE_PARITY_20261014.md`, usado como oráculo de recursos nos testes. Os gates integrados e a verificação negativa podem ser repetidos com:

```sh
ANDROID_SERIAL=emulator-5558 ./gradlew modernCheck releaseCheck --offline --continue --console=plain
PYTHONOPTIMIZE=1 python3 scripts/check-compose-guardrails.py
```

Executar o script de fixtures separado de qualquer compilação. As suítes adicionais são `:core:design-system:connectedDebugAndroidTest`, `:feature:account:connectedDebugAndroidTest`, `:feature:betting:connectedDebugAndroidTest`, `:feature:onboarding:connectedDebugAndroidTest` e `:feature:support:connectedDebugAndroidTest`. As classes `BettingInstalledGridGeometryTest` e `BettingPromptOriginTest` exigem o perfil 1080×2408/450dpi/fontScale1, explicitamente validado por elas; o segundo mantém as duas falhas descritas acima.

Para Home/smoke, gerar o debug mock com `-PmockBackend=true`. O teste do host usa `:app:connectedPrdDebugAndroidTest` com `-Pandroid.testInstrumentationRunnerArguments.class=br.gov.caixa.loterias.apostas.HomeV2UiTest`. A tarefa conectada remove o aplicativo ao terminar; instalar explicitamente o APK mock preservado antes de executar:

```sh
adb -s emulator-5558 install -r build/verification/ds-consolidation-20261014/app-prd-mock-final.apk
ANDROID_SERIAL=emulator-5558 bash scripts/smoke-compose-routes.sh
```

Renderização mock não homologa SSO, notificações, pagamentos ou resgates reais. O build debug normal é restaurado com `:app:assemblePrdDebug` sem a propriedade mock; nenhuma configuração de mock é persistida nos fontes.

## O que ainda está fora da centralização integral

O inventário estático pós-extração encontrou 3.235 ocorrências numéricas de dp/sp em 54 arquivos consumidores, imports explícitos de controles Material em 34 arquivos e construção local de TextStyle em 32 arquivos. A busca textual inclui comentários e não quantifica os controles importados por wildcard. São indicadores de composição e estilo local, não contagem de defeitos ou percentual de migração. O detalhamento por arquivo está em [DS_CONSOLIDATION_INVENTORY_20261014.tsv](DS_CONSOLIDATION_INVENTORY_20261014.tsv), com JSON de apoio nos artefatos locais.

Ainda há composição específica de volantes, resgate, pagamento, resultados, notificações e demais telas nas features. Não se substituíram medidas específicas por tokens genéricos nem se criaram wrappers que apenas renomeiam todo primitivo Material. A ampliação de padrões compartilhados deve ser sustentada por usos equivalentes e comparação dos estados de cada tela.

## O que falta para declarar 100% de equivalência

- Reconciliar os dois testes de origem raster do prompt (Dia de Sorte/Lotofácil: 95px esperados, 93px observados também na base anterior), com o mesmo dispositivo/insets/renderizador ou oráculo nativo equivalente. Os demais limites do raster devem ser verificados depois disso.
- Comparação pareada das demais telas/estados, com o mesmo backend, dados, dispositivo, fonte e insets; incluindo carregamento, vazio, erro, permissões, retorno e restauração.
- Dados equivalentes de apostas confirmadas/premiadas, Teimosinha, comprovantes e histórico de compras.
- Pagamentos e resgates em ambiente autorizado de homologação, cobrindo pendência, aprovação, falha, expiração e cancelamento.
- Biometria/matrícula em hardware apropriado, rotação real da sessão, callbacks da Conta CAIXA, digest Topaz e entrega FCM real.
- Execução com serviços disponíveis em PRD, TQS e automação. Build dos três ambientes não comprova os serviços dos três.
- Consolidação adicional dos padrões visuais que se demonstrarem recorrentes no inventário residual, sem alterar as variantes legadas.

Os dois ajustes solicitados na Home são exceções deliberadas em relação ao legado. Build, testes controlados e smoke não demonstram, isoladamente, equivalência integral nem permitem calcular um percentual confiável até os 100%.
