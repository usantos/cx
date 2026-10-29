# Validação da modernização

Data: 18/10/2026. Escopo: os 12 arquivos restantes identificados na auditoria e a consolidação técnica da SPEC. Evidências completas em `build/verification/modernization-spec-20261018/`. Nenhum commit ou push foi feito.

## Inventário e métricas

- 26 módulos e 80 destinos Compose identificados. As 102 entradas do inventário legado incluem componentes e diálogos, portanto não representam 102 telas independentes.
- 80 destinos já eram Compose na baseline. Nesta rodada, 6 rotas foram refatoradas; não houve nova conversão de tela XML.
- 0 arquivos Java de produção antes e depois. Os 3 testes Java restantes foram convertidos para Kotlin, preservando os mesmos 23 casos. Restam 0 arquivos Java próprios em app/core/feature.
- 64 arquivos Kotlin adicionados, dos quais 52 de produção e 12 de teste. São 29 tipos nomeados novos (19 de produção e 10 de teste), 3 tipos de teste traduzidos de Java e 21 tipos existentes realocados. As funções Compose em novos arquivos não são contabilizadas como classes.
- 629→681 arquivos Kotlin de produção; 1015→1079 incluindo testes. Manifest de tipos e critério de contagem em `new-kotlin-types.json`.

| Arquivo de entrada | Linhas antes | Linhas depois |
|---|---:|---:|
| BettingRoute.kt | 292 | 27 |
| PaymentCardFlowRoute.kt | 261 | 57 |
| LocationGateRoute.kt | 258 | 115 |
| PersonalDataRoute.kt | 257 | 90 |
| TicketScannerRoute.kt | 220 | 50 |
| HomeRoute.kt | 375 | 260 |
| BettingGameViewModel.kt | 851 | 296 |
| TicketsViewModel.kt | 792 | 254 |
| CartViewModel.kt | 529 | 264 |
| RemoteTicketsRepository.kt | 603 | 72 |
| RemotePaymentRepository.kt | 377 | 58 |
| CaixaDSLegacyComponents.kt | 1007 | 85 |

A redução representa extração de responsabilidades, não eliminação de funcionalidades. Os arquivos vizinhos mantêm os componentes e contratos necessários.

## Evidência já obtida

1. Baseline `./gradlew modernCheck releaseCheck --offline --continue --console=plain`: os 3 releases minificados, JVM, lint e gates compilaram; 5 tarefas de instrumentação falharam por falta de dispositivo. Log: `baseline-full.log`.
2. Caracterização contra os originais: 782 testes JVM nos módulos envolvidos; 2 testes Android de coordenação do tutorial e 2 de cartão; 17 testes Android do Design System com assertiva geométrica corrigida para pixels exatos na densidade fracionária. Logs `baseline-characterization-jvm.log`, `baseline-betting-ui.log`, `baseline-payment-ui.log`, `baseline-ds-corrected.log`.
3. Integração após extrações: 798 testes JVM, sem falha/erro/skip: Domain175, Data213, Presentation13, Betting139, Tickets58, Cart101, Payment99. Esse checkpoint precede a mudança de localização da fila pura de Presentation para Common, coberta na matriz final. Log: `integrated-jvm-2.log`.
4. Fontes: 35 comparações de corpos/blocos, 6 inventários de literais e todas as assinaturas/keys/owners de estado das rotas; 13 comparações dos repositories; 26 declarações e estilo privado do Design System idênticos. Scripts e JSONs em `ui/`, `data/`, `design-system/`.
5. Dependências: 21 coordenadas/versões/scopes preservados nos 17 builds. `dependency-equivalence.json`.
6. Formatação: 1079 arquivos Kotlin sem violações de UTF-8/LF/newline/whitespace; fixtures inválidas rejeitadas. Não equivale a análise semântica Detekt/ktlint. Android Lint e os gates arquiteturais complementam a análise estática.
7. Auditoria estrutural em 681 fontes: sem violações nas fronteiras conferidas; 94 arquivos protegidos de rede/autenticação/persistência/plataforma idênticos à baseline. `architecture-source-audit.json` e `protected-contracts-equivalence.json`.
8. Revisão independente somente leitura: nenhum achado concreto; limites e complemento registrados em `independent-review.md`.

## Matriz final

Comando executado integralmente, sem exclusões de tarefas ou redução de gates:

```sh
ANDROID_SERIAL=emulator-5560 ./gradlew modernCheck releaseCheck --offline --continue --console=plain
```

**BUILD SUCCESSFUL em 12m 7s**, 2190 tarefas (80 executadas e 2110 atualizadas). Log: `final-matrix-3.log`. Foram aprovados modernCheck/releaseCheck, os builds debug dos três flavors, os três releases minificados (prd, tqs, automacao), lint, gates arquiteturais, de integração, segurança e cobertura. Os releases já tinham sido recompilados e aprovados na segunda matriz; a terceira aprovou o conjunto após corrigir a cobertura de Tickets.

JVM: **1833 execuções aprovadas**, zero falhas, erros ou skips. São **1565 nomes de casos distintos**; os 134 casos do app são executados nos três flavors. XMLs preservados em `matrix-jvm-results/`; sumário em `final-results.json`.

Instrumentação obrigatória da matriz:

| Módulo | Casos | Aprovados | Condicionais não executados |
|---|---:|---:|---:|
| core/preferences | 27 | 27 | 0 |
| core/presentation | 3 | 3 | 0 |
| feature/cart | 65 | 65 | 0 |
| feature/home | 4 | 4 | 0 |
| feature/notifications | 10 | 10 | 0 |
| feature/tickets | 43 | 39 | 4 |
| Total | 152 | 148 | 4 |

AVD dedicado `SimloComposeValidation`, Android 16/API 36, 1280×2856, densidade efetiva 450dpi e fontScale 1,0. Os quatro skips pertencem a `InstalledPurchasePairTest`, por condições já existentes: os dois testes de coordenadas físicas exigem 1080×2408/450dpi; os dois de tipografia/baseline dependem do APK original `br.gov.caixa.loterias.apostas` e de `DetalhesComprasActivity`, ausentes durante a suíte Tickets. As condições foram mantidas. Para executar esses quatro casos, é necessário o perfil exato e o APK legado autorizado; eles não são contados como aprovados.

| Cobertura de linhas | Cobertas/total | Resultado |
|---|---:|---:|
| Domain | 1622/1680 | 96,55% |
| Data | 2163/2263 | 95,58% |
| Betting | 1066/1084 | 98,34% |
| Cart | 1991/2047 | 97,26% |
| Tickets (JVM + Android) | 738/756 | 97,62% |

Os mínimos anteriores permanecem iguais. Os colaboradores extraídos integram a cobertura; os includes de filtros de Tickets também foram mantidos. `verifyTicketsCoverageInputs` exige dados JVM e Android reais, além de executar a suíte conectada antes da verificação. O gate não pode aprovar apenas com um relatório JVM parcial.

Instrumentação complementar concluída, com todos os comandos retornando exit code 0. Os comandos exatos estão em `affected-ui-runs.json`, `classic-home-run.json`, `run-affected-ui.py` e `run-additional-ui.py`. Dois XMLs históricos do flavor tqs, de 14/10, permanecem preservados nos diretórios copiados, mas foram explicitamente excluídos da contagem: essas invocações executaram somente prd.

| Suíte complementar | Aprovados | Falhas/erros | Skips |
|---|---:|---:|---:|
| App / Home clássica (`homeV2=false`) | 6 | 0 | 0 |
| App / Home padrão, V2 e restauração | 12 | 0 | 0 |
| Design System | 17 | 0 | 0 |
| Dados pessoais / formulário, IME e limite | 8 | 0 | 0 |
| Apostas / tutorial, pager e modalidades | 27 | 0 | 0 |
| Cartão / processamento | 11 | 0 | 0 |
| Localização / comprovantes | 7 | 0 | 0 |
| Total | 88 | 0 | 0 |

Somando matriz e complemento: **236 execuções Android aprovadas**, quatro condicionais não executadas e nenhuma falha/erro na execução final. São contagens de execuções/configurações; não representam o mesmo número de telas distintas. O app foi recompilado ao final com HOME_V2=true e ENABLE_MOCK_BACKEND=false, preservando os defaults. As seis variantes debug/release e seus APKs foram conferidos em `final-default-flags-and-apks.json`. O lint do teste de tutorial ajustado e `verifyKotlinFormatting` passaram novamente em 5s (`final-test-lint.log`).

## Correções e limites

O teste `BettingFlowPendingEffectTest` reproduziu perda de evento antes do coletor (esperado NavigateBack, recebido null). A fila limitada de consumidor único corrige esse caso nos 11 pontos auditados. As propriedades de efeitos passam de SharedFlow para Flow; todos os consumidores internos continuam usando collect/first e são recompilados. Não se trata de uma API de biblioteca externa publicada. O estado da tela e os payloads de efeitos permanecem iguais.

A assertiva DS de espaçamento agora confere o pixel arredondado exato, sem tolerância:14dp×2,8125=39px. O layout de produção não foi ajustado para fazer o teste passar. A comparação do tutorial também foi corrigida após um RED diagnóstico provar duas linhas no TextView nativo e no Compose: a assertiva usa agora igualdade exata com o controle nativo medido, mantendo fonte/spans/altura. Nenhuma geometria de produção foi alterada. Log: `pool-native-diagnostic.log`. Dois erros de checkpoint foram resolvidos: comando Data corrigido de testDebugUnitTest para test e parser de extração DS corrigido para a sobrecarga com expression body. A matriz também identificou a dependência ausente do módulo de filtros na localização inicial da fila; a primitiva foi movida para Common, sem elevar minSdk ou adicionar dependência Android.

Nenhuma integração privada foi substituída por fake de produção. Preservados autenticação, pinning, Topaz, FilaBRA, contratos das 99 operações, serialização, persistência e manifests. Os mocks usados nos testes/previews são ambientes explícitos.

Homologação com credenciais/provedor, pagamentos/resgates reais, câmera física e GPS/geocoder reais não foram executados. A validação local também não demonstra paridade raster de todos os estados privados do aplicativo legado. Esses são limites de evidência, não implementações fictícias para aparentar conclusão.

## Rastreabilidade da SPEC

| Seções | Implementação/evidência |
|---|---|
| 1–3, 15, 18–19 | Auditoria, snapshots, plano incremental, caracterizações antes/depois, preservação do checkout anterior e ausência de commits/pushes |
| 4–5, 8–9 | Domain puro, colaboradores coesos, DataSources/mappers, Hilt existente, sem módulo por classe ou service locator |
| 6–7, 11–12 | Route/Screen, callbacks/UDF/StateFlow, efeitos testados, câmera/permissões preservadas, fila limitada e cancelável |
| 10, 13 | Contratos/payloads/erros/cancelamento e gates de rede/segurança; catálogo, editorconfig, lint e gate de whitespace |
| 14, 16 | JVM, testes de UI críticos, cobertura ampliada e matriz debug/release; resultados acima e limites externos explícitos |
| 17, 20 | MIGRATION_AUDIT.md, MIGRATION_PLAN.md, MIGRATION_PROGRESS.md, ARCHITECTURE.md e este relatório |

A matriz intermediária também bloqueou Tickets com 83% de linhas, abaixo dos 95% exigidos. O denominador ampliado alcança os componentes de filtros. Foram adicionados 3 testes de interação e cobertura Android obrigatória, sem retirar classes ou reduzir o mínimo. A execução focada passou em 35s, com 737/756 linhas cobertas (97,49%); a matriz completa elevou o resultado a 738/756 (97,62%). Log: `tickets-filter-coverage-2.log`. A primeira execução desses testes encontrou uma corrida no próprio helper de Back: ele fechava a Activity antes de o diálogo reabrir. O helper agora aguarda o diálogo visível antes da tecla; as mesmas assertivas passaram e produção não foi alterada.

Recomendações posteriores: homologar os provedores com dados autorizados e continuar a comparação visual histórica dos estados sem evidência pareada. Não alegar 100% de homologação externa com testes locais.

## Conclusão do escopo e próximos passos

Concluídos os 12 arquivos de entrada e as ações locais de consolidação da SPEC: auditoria, plano incremental, Kotlin/Compose, separação Presentation/Domain/Data, Hilt existente, testes, cobertura, catálogo, análise estática, formatação e os cinco documentos obrigatórios. Na matriz dessa rodada, não houve falhas finais nas verificações executadas; complementos posteriores estão registrados abaixo. Sete novos previews usam dados inertes. Não foram criados módulos vazios, serviços fictícios ou commits/pushes.

A revisão de performance nesta rodada é estrutural: owners/lifetime/keys de composição preservados, coroutines estruturadas e ausência de novos threads de negócio/I/O na UI nas fontes auditadas. Não foi realizado benchmark comparativo de startup, memória ou jank; builds release minificados não equivalem a esse benchmark.

Próximas ações externas: executar os quatro casos no perfil legado correto, homologar integrações com credenciais e dados autorizados, verificar câmera/GPS físicos e ampliar a comparação visual dos estados históricos. A conclusão técnica local não declara 100% de homologação externa ou de paridade raster global.

## Complemento — PrizeRedemptionViewModel (18/10/2026)

Refatoração adicional à rodada de 12 arquivos registrada acima. Os resultados globais anteriores não foram recontados como se toda a matriz tivesse sido executada novamente.

- ViewModel: 400→177 linhas. Oito novos arquivos organizam coordenação, canais, envio, contador, comprovantes, avisos/redirecionamentos e tipos de estado no mesmo módulo/package.
- API: construtor, métodos públicos, tipos públicos e ownership de StateFlow/fila preservados. Comparação de 35 corpos permite somente substituições de receivers/escopo/callbacks e visibilidade dos colaboradores; declarações de estado e mensagem legada idênticas.
- 55 testes JVM passaram contra o original e após extração, incluindo 5 caracterizações novas: refresh sem tick duplicado, Back cancelando contador, expiração em zero, fila de PDFs sem coletor/replay e cancelamento ao limpar ViewModel.
- Cobertura final: 506/516 linhas (98,06%). Nenhum include anterior ou mínimo 0.9501 foi removido/reduzido. O novo padrão de PrizeReceipt foi delimitado a classe exata e nested classes porque seu prefixo inicial capturava indevidamente o componente Android/Compose PrizeReceiptManagerKt.
- Revisão independente sem achados funcionais. Fontes não relacionadas permanecem idênticas ao snapshot desta rodada; UI, DI, rede e contratos não foram modificados.

Comando final, aprovado em 13s (`final-checks-3.log`):

```sh
./gradlew :feature:retail:check :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain
```

UI adicional (`final-ui.log`): 23 casos selecionados, 22 aprovados, zero falhas/erros e 1 skip condicional. O caso `installedPrizeSummaryRetainsTheProductionBottomCoordinate` exige screenshot 1080×2408/450dpi; o AVD inicial usa 1280×2856/450dpi. Após aplicar reversivelmente o tamanho exigido, o teste executou e falhou: esperado 880px, medido 960px. A mesma falha foi reproduzida com o arquivo original do ViewModel byte-idêntico (`geometry-original-ui.log` e `geometry-comparison.json`). O original temporário foi restaurado à refatoração e o check final passou novamente.

**Pendência visual preexistente:** investigar a diferença de geometria acima em tarefa própria. Não foi alterada a UI nem a assertiva para aparentar aprovação. A refatoração preserva o comportamento e não introduziu essa diferença; não se declara esta suíte inteira aprovada no perfil legado. Logs/XMLs dos dois resultados foram preservados.

Evidências desta continuação: `build/verification/prize-viewmodel-extraction-20261018/`, incluindo snapshots, equivalência, API compilada, resultados JVM/cobertura, UI e revisão. Plano: `docs/superpowers/plans/2026-10-18-prize-viewmodel-extraction.md`. A homologação externa citada na rodada anterior permanece separada.

Após restaurar o código final e o tamanho original do AVD, `PrizeReceiptInteractionTest#agencyWritesAndOpensPdfThenReusesTheSavedUri` passou novamente, sem skip (`restored-final-ui-smoke.log`, 7s). Este smoke é repetição de um dos 22 casos aprovados, não um caso distinto adicional.

## Complemento — HomeViewModel (18/10/2026)

Escopo adicional: HomeViewModel 343→111 linhas; sete novos arquivos organizam seis coordenadores internos e os tipos públicos HomeUiState/HomeNotificationRedirect no mesmo package. Estado e revisões compartilhadas permanecem no ViewModel; nenhum helper cria escopo independente. Os resultados globais anteriores não foram recontados como uma matriz novamente executada.

- Contrato: construtor, defaults, métodos/retornos públicos e StateFlow preservados. Comparados 25 corpos e o observador da sessão, normalizando somente receivers de estado/escopo. Mesmos AtomicLong, flags, mensagem de retry e ordem do init; nenhum arquivo não relacionado mudou.
- Baseline e estado final: 33 JVM aprovados, zero falhas/erros/skips. Três caracterizações novas protegem deduplicação/liberação do load, retry/cache de repasses e cancelamento de load/observação ao limpar VM. A primeira compilação da fixture nova foi corrigida para os cinco campos reais de AccountNotification antes da baseline verde; produção permanecia original.
- Check, lint, gates de formatação/arquitetura/Compose e compilação PrdDebug aprovados em 39s (`final-checks.log`).
- Cobertura JVM+Android: 438/444 linhas, 98,65%. Mínimo 0.9501 e todos os includes anteriores preservados; seis padrões adicionados incluem colaboradores e coroutines. Connected Android continua dependência obrigatória.
- Android: 4 testes do módulo aprovados; app clássico (`homeV2=false`) 6 aprovados; app padrão 12 aprovados (HomeScreenCoordinationUiTest, HomeV2CoordinationUiTest, AppNavigationStateRestorationTest e VisitorHomeEntryUiTest). São 22 execuções aprovadas, zero falhas/erros/skips, considerando as duas configurações; não são 22 telas ou nomes distintos.
- O último comando do app usa HOME_V2=true e ENABLE_MOCK_BACKEND=false. Os três BuildConfigs debug foram conferidos; não se afirma nova execução dos releases nesta rodada.
- Revisão independente somente leitura sem achados: CAS/identidade, versões, guards/loading/finally, estados e consumo das decisões preservados.

Comando de verificação principal:

```sh
ANDROID_SERIAL=emulator-5560 ./gradlew :feature:home:check :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain
```

Comandos completos dos dois perfis do app e XMLs somente do flavor prd estão em `app-ui-results.json` e `app-ui-*-results/`. Evidências em `build/verification/home-viewmodel-extraction-20261018/`: snapshots, baseline/check logs, equivalência, API compilada, XMLs, cobertura e revisão. Sem commits/pushes, alteração de UI ou integrações reais. As pendências externas e a divergência geométrica do resgate registradas nas rodadas anteriores permanecem separadas desta refatoração.

## Complemento — BetDetailViewModel (18/10/2026)

Refatoração adicional: BetDetailViewModel: 336→83 linhas. Seis arquivos novos organizam quatro coordenadores, mapeadores e os três tipos públicos realocados no mesmo package. A fachada permanece responsável por API/estado/fila/init; todos os Jobs usam o mesmo viewModelScope/dispatcher. Nenhuma contagem global das rodadas anteriores representa uma nova execução nesta continuação.

- API preservada: construtor/defaults, métodos/retornos públicos, StateFlow/Flow e tipos públicos. Comparados19 corpos,7 mappers e o construtor completo do BetDraft; init e ownership idênticos. O único ajuste dos corpos foi delegar ao mapper o mesmo construtor, sem alterar campos ou cálculos.
- Baseline e estado final:64 testes JVM aprovados, zero falhas/erros/skips;6 novos casos protegem cancelamento da sobrescrita, fallback/ordem/bytes/nome/replay dos PDFs, colunas e quantidades SuperSete, dados inválidos, preview e cancelamento de hidratação/download. A fixture sem seleção inicialmente retinha numberColumns pelo copy; ambos os campos foram limpos antes da baseline verde, mantendo produção intacta.
- Check completo Tickets, lint, gates de arquitetura/Compose/formatação e compilação PrdDebug aprovados em 3m 6s (`final-checks.log`).
- Cobertura JVM + Android: 1013/1041 linhas = 97,31%. Mínimo0.95 e todos os includes anteriores intactos; novos includes precisos abrangem fachada, coordenadores/coroutines, mappers, estado e tipos de comprovante/opção. Tipos exatos evitam capturar arquivos UI com prefixos parecidos. Nenhuma cobertura fictícia, classe retirada ou tarefa conectada omitida.
- Android: 43 casos, 39 aprovados, zero falhas/erros e 4 skips existentes de InstalledPurchasePairTest: dois exigem screenshot 1080×2408/450dpi, dois exigem APK original/DetalhesComprasActivity ausentes. O AVD usa 1280×2856/450dpi, fontScale 1 e Android 16. Condições não alteradas e casos não contados como aprovados. Os testes de coordenação dos detalhes/favoritos/repetição/comprovantes foram executados dentro dessa suíte.
- Revisão independente somente leitura sem achados nas guardas, snapshots, fallbacks, mapeadores, fila e cancelamento. Fontes não relacionadas continuam idênticas ao snapshot da rodada.

Comando executado:

```sh
ANDROID_SERIAL=emulator-5560 ./gradlew :feature:tickets:check :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain
```

Evidências em `build/verification/bet-detail-viewmodel-extraction-20261018/`: snapshots, baseline/check logs, equivalência, API compilada, XMLs JVM/Android, cobertura e revisão independente. Esta rodada não executou novamente os releases nem homologou serviços privados. As pendências externas/comparativos históricos permanecem separadas da refatoração; UI e protocolos não foram alterados. Nenhum commit/push.

## Complemento — MarketplaceViewModel (18/10/2026)

Escopo adicional: MarketplaceViewModel328→125 linhas. Seis arquivos novos separam carregamento, paginação, lotéricas favoritas, favorito pela lista de bolões, cotas e carrinho. Coordenação/API/estado/init permanecem na fachada. Esta rodada não representa nova execução da matriz global histórica.

- **Contrato/equivalência:** construtor/defaults, métodos públicos/retornos Unit, StateFlow, init/revisão e ownership preservados.47 checks de equivalência aprovados, incluindo corpos extraídos, decomposição da paginação, delegações, contrato público, configuração de cobertura e19 testes originais intactos. API compilada em public-api.txt. MarketplaceContract.kt e o efeito Compose MarketplacePagination.kt permanecem idênticos à baseline.
- **JVM:**41 aprovados tanto no original quanto após a extração, zero falhas/erros/skips.7 novas caracterizações cobrem falha de append/reset da área no retry, ordem de init/retry, cabeçalho especial/abreviado/inválido/falha, guards de modalidade/lotérica, trava compartilhada entre favoritos, cotas durante reserva e cancelamento de requisições ao limpar o ViewModel.
- **Android:**27 aprovados no módulo Marketplace, zero falhas/erros/skips, AVD SimloComposeValidation Android 16/API 36, 1280×2856, densidade 450/fonte 1.0. Inclui paginação, coordenação, mutations/loading, resumo de carrinho, carrossel e detalhes por modalidade. Não foram alteradas assertivas ou condições de execução da UI.
- **Check/build/gates:** comando abaixo aprovado em2m 33s,280 tasks (43 executadas, 7 cache, 230 up-to-date). Inclui check/JVM/cobertura/lint, instrumentação, compilação app PrdDebug, verifyKotlinFormatting(1106 arquivos, 0 violações), verifyCleanArchitecture e verifyComposeMigration.
- **Cobertura:**376/377 linhas=99,734748%. Todos os includes anteriores e mínimo 0.95 preservados; includes adicionais abrangem fachada/coroutines e os seis colaboradores. A fonte de execução JVM existente não foi substituída nem filtrada. Os 27 casos Android foram executados separadamente, sem alegar agregação de sua cobertura ao relatório JVM.
- **Revisão:** revisão independente estática somente leitura sem achados; revisor não executou Gradle/ADB. Execuções registradas pelo agente principal nos logs/XMLs.
- **Ocorrências da execução:** fixture inicialmente tentou MarketplaceFilter.copy privado; corrigida para withLottery público antes da baseline verde. Na extração, colisão de nome com MarketplacePagination.kt detectada no compile; o efeito Compose foi restaurado pelo gerador da rodada anterior e seu SHA256 conferido idêntico à baseline. O novo colaborador foi renomeado MarketplaceSearchPagination. Nenhuma alteração de UI permanece e ambas as ocorrências estão registradas nos logs.

```sh
ANDROID_SERIAL=emulator-5560 ./gradlew :feature:marketplace:check :feature:marketplace:connectedDebugAndroidTest :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain
```

Baseline: `./gradlew :feature:marketplace:testDebugUnitTest --offline --console=plain`. Após extração: `./gradlew :feature:marketplace:testDebugUnitTest :feature:marketplace:jacocoDebugCoverageVerification --offline --continue --console=plain`. Evidências: build/verification/marketplace-viewmodel-extraction-20261018/ (snapshots/hashes, baseline/check logs, XMLs, cobertura, equivalência/API, revisão e escopo). Inventário de pacotes, tamanho/densidade/fonte idênticos antes/depois; apenas o emulador iniciado nesta rodada foi encerrado.

Sem commits/pushes, novas dependências ou alterações de DI/rede/persistência. Não se afirma nova execução dos releases/três flavors, homologação privada ou comparação visual de todos os estados. Pendências externas e divergência geométrica do resgate documentadas anteriormente continuam separadas.

## Complemento — PaymentCardViewModel (18/10/2026)

Escopo adicional: PaymentCardViewModel 322→93 linhas. Seis coordenadores internos separam formulário, cartões salvos, envio, acompanhamento, avisos e redirecionamentos. A fachada conserva API, estado, init, roteamento e tratamento comum das falhas. O contexto de compra compartilhado e os tipos públicos realocados completam os oito novos arquivos. Esta rodada não representa nova execução da matriz global histórica.

- **Contrato/equivalência:** sessenta verificações aprovadas, incluindo os 27 corpos de métodos, delegações, construtor/defaults, ownership do estado/init, campos compartilhados, tipos públicos, configuração de cobertura e APIs compiladas. `load()` continua retornando Job; demais métodos preservam Unit/Boolean. Os 28 testes originais permanecem intactos. Comparações javap ignoram somente bridges sintéticos de acesso privado e o filename dos tipos realocados.
- **JVM:** 104 aprovados no original e após extração, zero falhas/erros/skips. Cinco novas caracterizações protegem o snapshot durante carrinho/POST suspensos, cancelamento de cargas de cartões (incluindo Job público), cancelamento no carrinho ou POST, cancelamento no delay ou consulta do polling e cancelamento durante o registro local sem sucesso tardio. Produção original permaneceu intacta até a baseline verde. A primeira compilação da fixture usou `.card` no tipo New; corrigida para o campo real `.draft` antes da baseline, conforme o contrato Domain, sem alterar produção.
- **Android:** 32 aprovados no módulo Payment, zero falhas/erros/skips. AVD SimloComposeValidation Android 16/API 36, 1280×2856, densidade 450/fonte 1.0. Inclui formulário/CVV/confirmação, coordenação e falha/retry do fluxo de cartão, cartões salvos, dados de pagamento, Pix e telas terminais existentes. Não foram modificadas assertivas, condições ou fontes da UI.
- **Check/build/gates:** comando abaixo aprovado em 2m 20s, 313 tasks (23 executadas, 290 up-to-date). Inclui check/JVM/cobertura/lint, instrumentação, compilação app PrdDebug, verifyKotlinFormatting (1114 arquivos, zero violações), verifyCleanArchitecture e verifyComposeMigration.
- **Cobertura:** 608/612 linhas = 99,346405%. Todos os includes anteriores e mínimo 0.9501 intactos; inclusões aditivas abrangem coordenadores/coroutines, contexto/tipo acompanhado e enum de etapas. A execução JVM existente continua sendo a fonte do relatório; os 32 testes Android foram executados separadamente e não são apresentados como cobertura agregada.
- **Revisão:** revisão independente estática somente leitura sem achados concretos de regressão. Revisor conferiu artefatos de API/JVM/cobertura, mas não executou Gradle/ADB nem reproduziu gates/UI; execuções locais estão registradas pelo agente principal.

```sh
ANDROID_SERIAL=emulator-5560 ./gradlew :feature:payment:check :feature:payment:connectedDebugAndroidTest :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain
```

Baseline: `./gradlew :feature:payment:testDebugUnitTest --offline --console=plain`. Após extração: `./gradlew :feature:payment:testDebugUnitTest :feature:payment:jacocoDebugCoverageVerification --offline --continue --console=plain`. Evidências em build/verification/payment-card-viewmodel-extraction-20261018/: snapshots/hashes, logs/XMLs, cobertura, equivalência/APIs, revisão e escopo. Inventário de pacotes, tamanho/densidade/fonte idênticos antes/depois; apenas o emulador iniciado nesta rodada foi encerrado.

Sem commits/pushes, dependências novas ou mudanças de UI/DI/rede/persistência. Não se afirma nova execução de releases/três flavors, integração com provedores privados ou paridade visual de todos os estados. As pendências externas e a divergência geométrica do resgate registradas anteriormente permanecem separadas.

## Complemento — RemoteRetailRepository (18/10/2026)

Escopo adicional: RemoteRetailRepository 261→47 linhas. Dez arquivos novos separam lotéricas, favoritos, resgates, cadastro Mercado Pago, PDFs e mapeadores; as duas interfaces remotas permanecem públicas no mesmo package, em arquivos próprios. Nenhuma contagem histórica representa nova execução da matriz global nesta rodada.

- **Contrato/equivalência:** 56 verificações aprovadas para delegações, corpos extraídos, detalhamento dos mapeadores/receiver, montagem literal Mercado Pago, interfaces/construtor/default cast/validação tardia, APIs compiladas e configuração de cobertura. As três APIs públicas compiladas (repository e duas interfaces) permanecem equivalentes; apenas bridges sintéticos privados/nomes de arquivo fonte são desconsiderados na comparação. Os 29 testes originais foram preservados integralmente.
- **Caracterização:** cinco novos casos exercitados com o código original e final: favoritos consultados antes de cada busca com prioridade de código/ID e sem cache; fonte de prêmio ausente exigida apenas nas operações; payloads/IDs/tokens crus e mesma referência dos PDFs; propagação de cancelamento por identidade nas 13 operações/ramificações; campos opcionais malformados com fallbacks, zeros e escala decimal preservados. Nenhuma correção de fixture ou mudança de comportamento foi necessária.
- **JVM Data:** 218 testes aprovados no original e após extração, zero falhas/erros/skips. Inclui as cadeias Retrofit/OkHttp controladas dos testes existentes, canais/erros HTTP/IO, cliente dedicado/rotação, redirecionamentos/avisos/cadastro, payloads e transporte dos comprovantes; não são chamadas a provedores privados em produção.
- **Consumidores JVM:** 55 testes de Retail e 104 de Payment executados novamente e aprovados. O comando final usa `--rerun` nos três tasks de testes; total final de 377 execuções, zero falhas/erros/skips. Não se incluem testes históricos/cached como novas execuções.
- **Check/build/gates:** comando final abaixo aprovado em 14s, 201 tasks (11 executadas, 190 up-to-date). Check Data/cobertura, compilação app PrdDebug/Hilt e gates verifyKotlinFormatting (1124 arquivos, zero violações), verifyCleanArchitecture e verifyComposeMigration aprovados. A primeira verificação também passou em 13s; após ajuste de espaçamento no arquivo sem imports, a rodada final refez os testes e verificações. Não houve falha de compilação/teste nesta continuação.
- **Cobertura:** 2196/2296 linhas = 95,644599%. `core/data/build.gradle` byte a byte idêntico à baseline: todas as classes/coroutines novas entram automaticamente no denominador existente; nenhum include, filtro, input de execução ou mínimo 0.9501 foi alterado. Sem exclusões para aprovar o gate.
- **Revisão:** revisão independente somente leitura sem achados. Conferiu separação, corpos/payloads, fonte tardia, bytes, mapeadores/tratamento de falhas, APIs compiladas, 29 testes originais e build.gradle idêntico. Não executou Gradle/ADB; execuções/build/cobertura estão registradas pelo agente principal.

```sh
./gradlew :core:data:test --rerun :core:data:check :feature:retail:testDebugUnitTest --rerun :feature:payment:testDebugUnitTest --rerun :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain
```

Baseline: `./gradlew :core:data:test --offline --console=plain`. Após extração: `./gradlew :core:data:test :core:data:jacocoTestCoverageVerification --offline --continue --console=plain`. Evidências em build/verification/retail-repository-extraction-20261018/: snapshots/hashes, baseline/final logs, XMLs dos três módulos, cobertura, equivalência/APIs, revisão e escopo.

O módulo alterado é JVM e nenhuma tela, integração Android ou DI foi modificada. Não houve emulador/instrumentação Android nesta rodada, nem nova execução dos releases/três flavors, homologação de serviços privados ou comparação visual completa. As pendências externas e a divergência geométrica do resgate registradas anteriormente permanecem separadas. Sem commits/pushes ou novas dependências.

## Complemento — RemoteBettingRepository (18/10/2026)

Escopo adicional: RemoteBettingRepository 266→27 linhas. Nove arquivos novos dividem quatro colaboradores de operações e cinco mapeadores internos; a fachada mantém BettingRepository, construtor e a mesma fonte. Esta rodada não representa nova execução da matriz global histórica.

- **Contrato/equivalência:** 31 verificações aprovadas para os dez mapeamentos originais, delegações, corpos de leitura/combos, decomposição dos parâmetros, preparação e construtor completo do AddBetRequestDto, alias dos receivers, fonte compartilhada, construtor/API compilada e integridade dos contratos/configuração/testes existentes. Comparação de API omite somente bridges sintéticos de acesso privado/filename; métodos e tipos públicos permanecem iguais.
- **Caracterização:** oito testes novos no arquivo RemoteBettingRepositoryTest.kt, executados no original e final sem alterar testes antigos ou corrigir fixtures. Cobrem JSON completo de 13 códigos/IDs por três descrições de concurso (39 pedidos dentro de um caso, sem inflar a contagem de testes); Super Sete preserva zeros, colunas, ordem/repetições e quantidades explícitas. Outro caso compara JSON esportivo completo com flags, placares string, time do coração, mês e espelho, incluindo omissão de campos nulos/escudos/legendas. Os demais verificam prioridade de concurso/consulta de escudos/último ID, falhas opcionais, catálogo, combos/carrinho, parâmetros/pools/defaults/escala e propagação de falhas sem retry.
- **Compatibilidade dos escudos:** preservado literalmente o runCatching amplo da leitura opcional sportsTeamShields, inclusive quando a fonte lança CancellationException. Essa é a regra anterior caracterizada; não foi corrigida ou ampliada nesta extração. Nas operações obrigatórias, IOException/cancelamento continuam propagados por identidade. Não foram criados scopes, caches, retries, normalizações ou guardas adicionais.
- **JVM:** 226 testes de Data aprovados com produção original e final. Comando final executou novamente 226 Data + 139 Betting + 101 Cart = 466 execuções, zero falhas/erros/skips. `--rerun` foi aplicado aos três tasks de teste; os resultados históricos/cached não foram somados como novas execuções.
- **Check/build/gates:** comando final abaixo aprovado em 15s, 201 tasks (11 executadas, 190 up-to-date). Check/cobertura Data, compilação app PrdDebug/Hilt e gates verifyKotlinFormatting (1134 arquivos, zero violações), verifyCleanArchitecture e verifyComposeMigration aprovados. Não houve falha de compilação/teste nesta rodada.
- **Cobertura:** 2210/2310 linhas = 95,670996%. core/data/build.gradle byte a byte idêntico à baseline: todas as classes/coroutines novas permanecem automaticamente no denominador existente. Sem mudança de mínimos (0.9501), includes, filtros ou fonte dos dados de execução. RemoteDataSources.kt, DTOs/Retrofit/Hilt/consumidores e todos os testes existentes permanecem intactos.
- **Revisão:** revisão independente estática somente leitura sem achados. Comparou original, dez arquivos finais e oito caracterizações; não executou Gradle/ADB/instrumentação. Logs/XMLs/checks da execução principal registram os resultados reais.

```sh
./gradlew :core:data:test --rerun :core:data:check :feature:betting:testDebugUnitTest --rerun :feature:cart:testDebugUnitTest --rerun :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain
```

Baseline: `./gradlew :core:data:test --offline --console=plain`. Após extração: `./gradlew :core:data:test :core:data:jacocoTestCoverageVerification --offline --continue --console=plain`. Evidências em build/verification/betting-repository-extraction-20261018/: snapshots/hashes, logs/XMLs dos três módulos, cobertura, equivalência/API/construtor original do DTO, revisão e escopo.

Refatoração JVM sem mudanças de tela/integração Android ou DI; não houve emulador/instrumentação nesta rodada. Não se afirma nova execução de releases/três flavors, homologação privada ou paridade visual de todos os estados. Pendências externas e divergência geométrica do resgate documentadas anteriormente permanecem separadas. Sem commits/pushes ou novas dependências.

## Complemento — BettingSportsContent (18/10/2026)

Escopo adicional: BettingSportsContent 410→15 linhas. As duas entradas internas permanecem, com os mesmos argumentos/defaults, e seis arquivos novos separam seleção de times, Loteca, cartão de resultado, escudos, medidas visuais e ligação dos callbacks ao ViewModel. Esta rodada não representa nova execução da matriz global histórica.

- **Contrato/equivalência:** 20 verificações aprovadas para corpos visuais, fontes/dimensões/modificadores, alinhamento inteiro, assinaturas/defaults, delegações, três referências de métodos e APIs compiladas. BettingGameViewModel, BettingGameRoute, recursos, testes existentes e feature/betting/build.gradle permanecem intactos. Nenhum estado, cache, scope ou normalização novo.
- **Caracterização:** três novos testes Compose executados nas entradas originais antes e depois da extração. Cobrem troca do ViewModel para busca/seleção e label do time, troca de ViewModel/callbacks de resultado/quantidade/ajuda, fallback nulo/inválido e pixels Base64/raw/data URI com os mesmos limites do cartão de 115dp. Fakes restritos aos use cases dos testes; sem submissões reais. A primeira compilação da fixture encadeou performTextClearance (retorna Unit) com performTextInput; chamadas separadas antes da baseline verde, sem mudança de assertiva ou produção.
- **JVM:** 139 aprovados após extração, executados novamente por `testDebugUnitTest --rerun`, zero falhas/erros/skips. Na baseline o task JVM foi verificado como UP-TO-DATE; não se conta isso como nova execução anterior à extração.
- **Android no perfil do oráculo:** 24 aprovados tanto no original quanto no final, zero falhas/erros/skips. Quatro classes: BettingSportsCoordinationTest (3), BettingTimemaniaSearchTest (1), BettingLotecaParityTest (11) e BettingNativeGeometryTest (9). AVD SimloComposeValidation, Android 16/API 36, 1280×2856, 480 dpi/fonte 1.0, conforme o perfil declarado nos testes nativos. Inclui geometria, fontes, grade, margens, semântica, vínculo da descrição e interações. Não se afirma comparação visual completa de todos os estados.
- **Divergências preexistentes a 450 dpi:** no perfil inicial com override de 450 dpi, original executou 24 casos: 21 aprovados/3 falhas. Após extração foram repetidos somente os três métodos com falha: mesmos nomes/mensagens/valores, zero erros/skips. A execução comparativa final termina BUILD FAILED em 21s, como a baseline deste perfil; não é apresentada como aprovação. Nenhum teste foi enfraquecido/ignorado e a geometria não foi corrigida nesta refatoração.
  - Loteca counterAndSelectedTextMetricsMatchActualNativeTextViewsAndFlowCenter: altura nativa Partidas esperada 54 px, medida 55 px.
  - Loteca installedToolbarSelectorFirstMatchAndEqualCardGeometry: distribuição inteira esperada 379 px, medida 380 px.
  - SuperSete sevenColumnRemainderDistributionMatchesMeasuredAndroidLinearLayout: largura do cabeçalho esperada 158 px, medida 156 px.
- **Check/build/gates:** comando principal abaixo aprovado em 3m 24s, 313 tasks (33 executadas, 280 up-to-date). Inclui check/JVM/cobertura/lint Betting, instrumentação a 480 dpi, compilação app PrdDebug e verifyKotlinFormatting/verifyCleanArchitecture/verifyComposeMigration. Os oráculos e as assertivas originais permanecem sem mudanças.
- **Cobertura:** 1066/1084 linhas = 98,339483%. Build.gradle byte a byte idêntico: mínimo 0.9501, includes e fonte de execução JVM preservados. O relatório mantém o escopo JVM anterior; os 24 casos Android são evidência funcional separada e sua cobertura não é apresentada como agregada ao JaCoCo.
- **Revisão:** revisão independente somente leitura sem achados acionáveis. Conferiu os sete arquivos finais, original, APIs, ligação do ViewModel, corpos/chaves/medidas e três testes novos; não executou Gradle/ADB.

```sh
ANDROID_SERIAL=emulator-5560 ./gradlew :feature:betting:testDebugUnitTest --rerun :feature:betting:check :feature:betting:connectedDebugAndroidTest :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration -Pandroid.testInstrumentationRunnerArguments.class=br.gov.caixa.loterias.feature.betting.BettingSportsCoordinationTest,br.gov.caixa.loterias.feature.betting.BettingTimemaniaSearchTest,br.gov.caixa.loterias.feature.betting.BettingLotecaParityTest,br.gov.caixa.loterias.feature.betting.BettingNativeGeometryTest --offline --continue --console=plain
```

Baseline a 480 dpi: mesmo filtro com `:feature:betting:testDebugUnitTest :feature:betting:connectedDebugAndroidTest`, sem `--rerun`. Comparação a 450 dpi: `:feature:betting:connectedDebugAndroidTest` com os três métodos acima (classe#metodo), sem alterar código/assertivas. Evidências em `build/verification/betting-sports-extraction-20261018/`: snapshots/hashes, logs/XMLs originais/finais por perfil, equivalência/APIs, cobertura, geometry-450-comparison.json, revisão e escopo.

Configuração e inventário de pacotes do AVD conferidos como iguais aos iniciais após restaurar 450 dpi (comparação de pacotes por conjunto); somente o emulador iniciado nesta rodada foi encerrado. Sem commits/pushes, dependências novas ou mudanças de ViewModel/Route/Domain/Data/DI. Não foram repetidos releases/três flavors ou homologações privadas. Pendências externas e divergências históricas registradas acima continuam separadas.

## Complemento — sete arquivos por responsabilidade (18/10/2026)

Refatoração solicitada de FilaBraGate 359→82, AppAuthAuthenticationGateway 344→56, TicketsPayloadMapper 341→46, BettingGameSelection 329→57, FavoriteCartsViewModel 275→79, PaymentViewModel 269→115 e RemoteCartRepository 247→20. As duas ViewModels mantêm construtores, API/retornos, StateFlow e ownership; as demais fachadas mantêm interfaces/entradas/defaults. 43 novos arquivos de produção contêm 25 novas classes nomeadas e quatro tipos existentes realocados: FavoriteCartsUiState, PaymentUiState, PaymentMode e QueueGateState. Não há novas telas/rotas, Java ou dependências. O detalhamento por feature está em MIGRATION_PROGRESS.md e ARCHITECTURE.md.

### Evidência de preservação

- **175 verificações aprovadas:** 126 para Data/Betting/ViewModels, 37 para Auth/Fila e 12 APIs compiladas. As comparações normalizam somente visibilidade de helpers, renomeações/delegações e extrações previstas. APIs são ordenadas por declaração, ignorando filename e bridges sintéticos privados access$; métodos públicos, defaults, retornos e synchronized permanecem. Os dois stubs públicos de anotações das capacidades biométricas também foram preservados, sem excluí-los do comparador.
- **Estado/concorrência:** uma única instância de flags de seleção; mesmos caches/IDs de favoritos; mesmos estados/escopos/dispatchers e guards de Pix/sessão. Auth compartilha as mesmas instâncias de stores/service/registries e o monitor original da fachada; blocos movidos mantêm gateway→store, cancelamento por ticket, identidade, persistência/rollback e logout. Sem novos locks/scopes/caches/retries.
- **UI/protocolos:** corpos visuais e efeitos da fila, chaves/owners, tempo mínimo, broadcasts e disposal preservados. Payloads/mapeadores conservam ordem, prioridades, recursão, defaults, colunas/zeros/escala e tratamento de falhas. Domain/DTOs/Retrofit/Hilt/recursos/manifests permanecem intactos.
- **Revisão independente:** somente leitura, sem Gradle/ADB ou subagentes adicionais. Identificou imports getValue/setValue implícitos ausentes nos quatro arquivos Compose da fila; restaurados antes da compilação do app, e revisor conferiu a correção. Resultado final sem achados concretos pendentes. Não revisou refatorações anteriores nem homologação privada.

### Baseline e testes

- Baseline JVM forçada antes de editar produção: 226 Data + 99 Auth + 139 Betting + 101 Cart + 104 Payment + 134 App = 803 aprovados; zero falhas/erros/skips. Comando `:core:data:test --rerun :core:auth:testDebugUnitTest --rerun :feature:betting:testDebugUnitTest --rerun :feature:cart:testDebugUnitTest --rerun :feature:payment:testDebugUnitTest --rerun :app:testPrdDebugUnitTest --rerun --offline --continue --console=plain`, BUILD SUCCESSFUL em 14s.
- Baseline Android: 13 Auth (inclui os dois novos casos de sincronização), 65 Cart e 32 Payment = 110 aprovados no original de cada módulo, sem falhas/erros/skips; BUILD SUCCESSFUL em 9m 8s. A nova caracterização da fila passou antes de extrair FilaBraGate (checkpoint2, 16s).
- Três novos casos Android: logout aguarda o guard de refresh antes de limpar a mesma sessão; pós-commit exige ambos os guards; endereço inseguro de fila produz um ERROR seguido de finish, sem repetir os efeitos na recomposição. Exercitam entradas reais; não iniciam autenticação/transações privadas.
- Matriz principal após extração: 825 JVM distintos aprovados (os 803 anteriores, 4 Queue e 18 Onboarding), todos os oito tasks de teste executados com --rerun. 110 Android de Auth/Cart/Payment aprovados. Check/lint/cobertura, App PrdDebug compile/lint e três gates principais aprovados em 10m 14s, 930 tasks (129 executadas, 801 up-to-date).
- Após restaurar as duas anotações originais da fachada Auth, a verificação adicional executou novamente 99 Auth JVM, 134 App JVM e 13 Auth Android; check/lint/PrdDebug/gates aprovados em 51s (806 tasks, 54 executadas, 752 up-to-date). Repetições não foram somadas como casos distintos.
- Instrumentação Betting: 11 aprovados, zero falhas/erros/skips, em 44s. Classes BettingComposeOptionsTest(6), BettingWheelSwipeTest(1), BettingSportsCoordinationTest(3) e BettingTimemaniaSearchTest(1). Verifica seletores/quantidade/recorrência/swipe, troca de ViewModel/callbacks, times, Loteca e pixels dos escudos.
- App: nova caracterização da fila aprovada após extração (15s) e repetida no APK final após a restauração dos metadados Auth. A última execução com assemblePrdDebug e os sete gates complementares passou em 12s (462 tasks, 14 executadas, 448 up-to-date).
- **Total distinto após extração:**825 JVM e 122 Android (Auth 13,Cart 65,Payment 32,Betting 11,App 1), zero falhas/erros/skips. AVD SimloComposeValidation Android 16/API 36, 1280×2856,override 450 dpi/fonte 1.0. XML Tqs histórico de outro AVD foi detectado na coleta App e isolado em unrelated-historical-app-results; somente os XMLs do task Prd desta rodada entraram nas contagens.

### Comandos finais

```sh
ANDROID_SERIAL=emulator-5560 ./gradlew :core:data:test --rerun :core:data:check :core:auth:testDebugUnitTest --rerun :core:auth:check :core:auth:connectedDebugAndroidTest :feature:betting:testDebugUnitTest --rerun :feature:betting:check :feature:cart:testDebugUnitTest --rerun :feature:cart:check :feature:payment:testDebugUnitTest --rerun :feature:payment:check :feature:payment:connectedDebugAndroidTest :app:testPrdDebugUnitTest --rerun :app:lintPrdDebug :app:compilePrdDebugKotlin :core:queue:testDebugUnitTest --rerun :feature:onboarding:testDebugUnitTest --rerun verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain
```

```sh
ANDROID_SERIAL=emulator-5560 ./gradlew :core:auth:testDebugUnitTest --rerun :core:auth:check :core:auth:connectedDebugAndroidTest :app:testPrdDebugUnitTest --rerun :app:lintPrdDebug :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain
```

```sh
ANDROID_SERIAL=emulator-5560 ./gradlew :feature:betting:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=br.gov.caixa.loterias.feature.betting.BettingComposeOptionsTest,br.gov.caixa.loterias.feature.betting.BettingWheelSwipeTest,br.gov.caixa.loterias.feature.betting.BettingSportsCoordinationTest,br.gov.caixa.loterias.feature.betting.BettingTimemaniaSearchTest --offline --continue --console=plain
```

```sh
ANDROID_SERIAL=emulator-5560 ./gradlew :app:assemblePrdDebug :app:connectedPrdDebugAndroidTest verifyLegacyUiInventory verifyLegacyBackendParity verifyCaixaDS verifyFilaBraAar verifyTopazHeartbeat verifyNetworkSecurity verifyAppIdentityAndFlavors -Pandroid.testInstrumentationRunnerArguments.class=br.gov.caixa.loterias.apostas.FilaBraQueueCoordinationTest --offline --continue --console=plain
```

Os dez gates do projeto foram aprovados. verifyKotlinFormatting conferiu 1186 arquivos, zero violações. Nenhum gate, assertiva, dependência ou oráculo foi enfraquecido.

### Cobertura

| Módulo | Linhas cobertas/total | Percentual | Fonte/escopo |
|---|---:|---:|---|
| Data |2226/2328|95,618557%|JVM; todas as classes, incluindo helpers novos |
| Auth |316/323|97,832817%|JVM; gate de estado/tentativas original, não certificação do bridge Android |
| Betting |1107/1126|98,312611%|JVM; includes anteriores e seis colaboradores de seleção |
| Cart |2044/2101|97,287006%|JVM+Android, escopo de todas as classes anterior |
| Payment |664/668|99,401198%|JVM; includes anteriores e seis colaboradores novos |

Mínimos 0.9501, fontes de execução e padrões anteriores preservados. Só Betting/Payment receberam includes aditivos; Data/Cart já incluem todas as classes, e Auth mantém configuração byte a byte igual. Os 122 casos Android são contados como testes; sua cobertura não foi apresentada como agregada ao relatório JVM de outros módulos.

### Ocorrências e limites

O primeiro checkpoint encontrou inferência recursiva de tipos em Auth (callback ::logout→contexto→ending→retorno inferido); retorno Unit público explicitado, preservando contrato. O segundo checkpoint compilou e passou os testes. O comparador estático precisou normalizar private suspend e whitespace final, sem mudar produção/assertivas. A revisão identificou os imports implícitos Compose; a comparação de API identificou dois stubs $annotations ausentes, resolvidos restaurando as anotações originais. Logs de todas as execuções e correções foram preservados; não houve falha funcional de teste nesta rodada.

Esta rodada não é nova execução de modernCheck/releaseCheck globais, releases/Tqs/Automacao, comparação visual integral ou homologação real de OAuth/biometria/SSO, FilaBRA e provedores privados. Os limites e divergências geométricas históricos acima continuam separados. Configuração e inventário de pacotes do AVD conferidos iguais aos iniciais; somente o emulador iniciado nesta rodada foi encerrado. Nenhum commit/push, mudança de DI/contrato de rede/recursos/manifests ou dependência nova.

Evidências em build/verification/seven-responsibilities-20261018/: snapshots/hashes, APIs, logs/XMLs por fase/módulo, final-counts.json, cobertura, equivalência, revisão, inventário do dispositivo e escopo final.

## Continuação — dez arquivos por responsabilidade (18/10/2026)

882 testes JVM aprovados (baseline 877; cinco novas caracterizações aprovadas também com as fachadas originais); 65 testes Android aprovados, um teste condicional ignorado, zero falhas/erros; 352 comparações de fonte/API aprovadas. Revisão independente sem achados. Checks/cobertura/lint dos sete módulos envolvidos, lint PrdDebug do app, dez gates e APK PrdDebug aprovados.

- `./gradlew :core:network:test --rerun :core:data:test --rerun :core:auth:testDebugUnitTest --rerun :feature:betting:testDebugUnitTest --rerun :feature:account:testDebugUnitTest --rerun :feature:responsible-gaming:testDebugUnitTest --rerun :feature:notifications:testDebugUnitTest --rerun :feature:retail:testDebugUnitTest --rerun :app:testPrdDebugUnitTest --rerun --offline --continue --console=plain` — baseline 877/877.
- `python3 build/verification/ten-responsibilities-20261018/verify-characterizations.py` — cinco novos testes contra as fachadas originais, restauradas as finais em finally; apenas arquivos da rodada foram substituídos temporariamente.
- `ANDROID_SERIAL=emulator-5560 zsh build/verification/ten-responsibilities-20261018/run-validation.sh` — checks de Network/Data/Betting/Account/ResponsibleGaming/Notifications/Retail, JVM PrdDebug do app, APK e dez gates. Repetição final após caracterização/ordem dos métodos: `final-checks-followup.log`, com lint PrdDebug adicional.
- `python3 build/verification/ten-responsibilities-20261018/run-ui.py` — instrumentação separada por módulo, comandos completos e classes em ui-results.json.
- `python3 build/verification/ten-responsibilities-20261018/capture.py final` e `python3 build/verification/ten-responsibilities-20261018/verify-equivalence.py` — APIs, contagens e equivalência.

| Instrumentação | Casos | Aprovados | Ignorados | Falhas |
|---|---:|---:|---:|---:|
| retail | 18 | 17 | 1 | 0 |
| account | 18 | 18 | 0 | 0 |
| betting | 8 | 8 | 0 | 0 |
| app | 12 | 12 | 0 | 0 |
| notifications | 10 | 10 | 0 | 0 |

| Gate de linhas | Resultado |
|---|---|
| core/network | 669/675 = 99.11% |
| core/data | 2226/2328 = 95.62% |
| feature/betting | 1204/1224 = 98.37% |
| feature/account | 518/529 = 97.92% |
| feature/notifications | 583/586 = 99.49% |
| feature/retail | 506/516 = 98.06% |
| responsible-gaming | Gate original de 95% aprovado; sem relatório XML próprio configurado |

Todos os filtros anteriores e os mínimos 0.9501 (0.95 em ResponsibleGaming) foram preservados. Denominadores incluem os novos colaboradores/coroutines/formatadores dos quatro filtros ajustados; Notifications mantém todas as classes e entradas JVM+Android.

A equivalência inclui 86 DTOs byte a byte, 47 métodos mock com JSON/whitespace, dez métodos das quatro famílias de clientes, 55 métodos VM, dez componentes Compose, oito registros de destinos e os owners originais de estado/fila. Os 110 contratos compilados com membros públicos mantêm assinaturas e ordem original; a fachada LoginViewModelKt, que contém somente um getter privado e bridge sintética, é contabilizada explicitamente como artefato privado do compilador. A verificação estrita detectou a constante JVM LAST_PAGE do companion privado; sua declaração original foi restaurada e o teste de API passou após a recompilação, seguido de check/JVM/app/lint/gates (binary-api-fix.log). Revisão independente somente leitura sem achados.

Mudanças comparadas com hashes iniciais, preservando os trabalhos anteriores: dez fachadas, quatro includes Gradle aditivos, cinco documentos atualizados, um plano, 75 arquivos Kotlin de produção e dois novos de teste. Nenhum arquivo anterior foi apagado; nenhum oráculo, manifesto, recurso, dependência ou flag alterado.

Limites: um caso geométrico exige 1080×2408/450dpi e foi ignorado condicionalmente no perfil atual 1280×2856/450dpi/font1.0; não é evidência de paridade global. Testes locais não homologam serviços privados ou hardware biométrico. A matriz global/release histórica não foi repetida integralmente. Pacotes nativos de referência, tamanho/densidade/fonte preservados; somente o AVD desta rodada foi encerrado. Nenhum commit/push.

APK conferido: `app/build/outputs/apk/prd/debug/app-prd-debug.apk`, 99.976.172 bytes, applicationId `br.gov.caixa.loterias.apostas`; DEX contém os novos colaboradores. `ENABLE_MOCK_BACKEND=false` e `HOME_V2=true` preservados. Evidências: `apk-proof.json`, `device-proof.json`, `final-scope.json` e `summary.json` na pasta desta rodada.

## Sete ajustes concretos de conformidade — 19/10/2026

755 testes JVM aprovados, 108 Android aprovados, cinco casos visuais condicionais ignorados, zero falhas/erros. Revisão independente somente leitura sem achados acionáveis. Todos os sete ajustes foram validados; esta rodada não declara a modernização global encerrada.

| JVM | Casos aprovados | Falhas |
|---|---:|---:|
| domain | 185 | 0 |
| data | 229 | 0 |
| preferences | 36 | 0 |
| retail | 66 | 0 |
| tickets | 69 | 0 |
| notifications | 22 | 0 |
| responsible-gaming | 14 | 0 |
| app | 134 | 0 |

| Android | Aprovados | Ignorados | Falhas |
|---|---:|---:|---:|
| retail | 26 | 1 | 0 |
| preferences | 28 | 0 | 0 |
| tickets | 39 | 4 | 0 |
| notifications | 10 | 0 | 0 |
| app | 5 | 0 | 0 |

| Cobertura de linhas | Linhas cobertas/total | Percentual |
|---|---:|---:|
| Domain | 1667/1724 | 96.69% |
| Data | 2228/2330 | 95.62% |
| Preferences | 487/493 | 98.78% |
| Retail | 531/543 | 97.79% |
| Tickets | 1026/1054 | 97.34% |
| Notifications | 583/586 | 99.49% |

ResponsibleGaming: gate original de 95% aprovado, sem XML próprio configurado. Demais mínimos de 95,01% mantidos, exceto Tickets com seu mínimo original de 95%. Os três filtros alterados só receberam inclusões; nenhuma classe/padrão anterior foi removido. Comparação automatizada em coverage-contracts.json.

Comandos executados com JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home, --offline --continue --console=plain, ANDROID_SERIAL=emulator-5560 para instrumentação:

- ./gradlew :core:domain:test --rerun :core:preferences:testDebugUnitTest --rerun :core:data:test --rerun :feature:retail:testDebugUnitTest --rerun :feature:tickets:testDebugUnitTest --rerun :feature:notifications:testDebugUnitTest --rerun :feature:responsible-gaming:testDebugUnitTest --rerun :app:testPrdDebugUnitTest --rerun — matriz inicial de 754; o caso adicional do encoder padrão foi executado pelo check final, totalizando 755.
- ./gradlew :feature:retail:connectedDebugAndroidTest com classes PrizeReceiptIoBoundaryTest, PrizeReceiptInteractionTest, PrizeQrCodeExecutionTest e ControlledPrizeInteractionTest — 27 casos, 26 aprovados e um ignorado.
- python3 build/verification/seven-compliance-20261018/run-android.py — Preferences, Tickets e Notifications completos; app PrdDebug com StateCollectionLifecycleUiTest e BetReceiptOpeningTest. Comandos exatos em android-results.json.
- zsh build/verification/seven-compliance-20261018/run-checks.sh — checks de Domain/Data/Preferences/Retail/Tickets/Notifications/ResponsibleGaming; lint e assemble PrdDebug; verifyKotlinFormatting, verifyCleanArchitecture, verifyComposeMigration, verifyLegacyUiInventory, verifyLegacyBackendParity, verifyCaixaDS, verifyFilaBraAar, verifyTopazHeartbeat, verifyNetworkSecurity e verifyAppIdentityAndFlavors. Dependências de instrumentação foram executadas novamente pelo check; contagens não duplicadas.
- python3 build/verification/seven-compliance-20261018/verify-contracts.py — 63 provas aprovadas; nenhum ViewModel/UiState alterado; APIs dos sete arquivos preservadas; mapper do carrinho idêntico.

Evidências de regressão antes/depois: receipt-red.log comprova ContentProvider na Main antes da correção; domain-data-red.log comprova dois cancelamentos absorvidos; lifecycle-red.log comprova atualizações indevidas abaixo de STARTED; receipt-cancellation-red.log reproduz falsos erros de PDF; qr-stale-red.log comprova substituição indevida por geração obsoleta sem propriedade por valor. Logs finais, XMLs JVM/Android/cobertura, scope.json, contracts.json e revisão em build/verification/seven-compliance-20261018/.

Emulador: SimloComposeGates, API 36, 1280×2856/480dpi. Os cinco casos condicionais usam raster/tipografia de outro perfil; nenhum oráculo/assertiva foi alterado. Um ensaio Android foi interrompido durante ajuste do harness e descartado, seguido por repetição limpa aprovada. Limitação nativa: escrita bloqueante pode terminar fisicamente após cancelamento; nenhum efeito/cache/erro é publicado depois do descarte. Testes usam fixtures e arquivos temporários, sem compras, resgates ou chamadas privadas reais. Matriz global modernCheck/releaseCheck de todos os flavors e homologação das integrações privadas permanecem fora desta execução.

## Links externos, recuperação de comprovantes e matriz global — 19/10/2026

As correções locais e os dois gates globais foram aprovados. O encerramento integral da SPEC.md permanece condicionado às pendências físicas/privadas e decisões funcionais registradas abaixo. Evidências desta rodada: build/verification/links-receipts-global-20261019/. Baseline preservada antes das alterações, sem reset/stage/commit/push.

### Correções e regressões comprovadas

| Verificação dirigida | Aprovados | Falhas | Ignorados | Evidência |
|---|---:|---:|---:|---|
| Conta: sucesso, recomposição, três exceções de abertura, retry/cancelamento de coroutine | 5 | 0 | 0 | account-links-green.log |
| Home clássico: abertura, três exceções, retry e Cancelar | 5 | 0 | 0 | home-links-green.log |
| Comprovantes: seletor, recriação real de Activity, nova VM/metadados, gravação, visualizador, falhas, retry e Cancelar | 17 | 0 | 0 | receipts-final-green.log e receipts-final-android/ |
| Retail JVM completo, incluindo 32 ViewModel e 4 Saving | 68 | 0 | 0 | receipts-final-jvm/ |

REDs verificados antes das respectivas correções: account-links-red.log, home-links-red.log, receipts-red.log, recovery-retry-red.log, recovery-write-red.log e receipt-cancel-red.log. A última revisão independente não encontrou achados acionáveis; três problemas identificados durante a revisão foram corrigidos e cobertos por regressões (recuperação em OPTIONS, retry de gravação conservando bytes/URI e Cancelar acessível nos três diálogos).

A Activity real foi recriada via ActivityScenario.recreate com ViewModelProvider. Testes separados restauram saved state com uma nova VM e rejeitam ByteArray em Bundle/registries; não foi executado um teste de encerramento real do processo pelo sistema. A escrita retida não armazena Activity/Context/callback de UI; ContentResolver vem de applicationContext. Nenhuma abertura/cache/falha tardia é publicada por uma gravação cancelada após descarte. Uma chamada nativa bloqueante já iniciada ainda pode terminar fisicamente.

Os testes Home usaram exclusivamente no comando de instrumentação o applicationId de validação, flavor Tqs e -PhomeV2=false, pois o atalho pertence à Home clássica. O init script está nesta pasta de evidências. Essas opções não são usadas nos gates globais/releases e não alteram arquivos de configuração. Os testes usam fixtures, não realizam compras/resgates/chamadas privadas.

### Matriz global desta rodada

| Etapa | Resultado |
|---|---|
| modernCheck completo | Aprovado em 14m15s; dez gates e 24 checks de módulos |
| Debug Prd/Tqs/Automacao | assemble, 134 casos JVM por flavor e lint aprovados |
| releaseCheck Prd/Tqs/Automacao | Aprovado em 12m53s; os três APKs release gerados sem assinatura de distribuição |

Relatórios do gate: 1.910 casos JVM, zero falhas/erros/skips; 193 Android, 190 aprovados e 3 ignorados condicionais, zero falhas/erros. Os JVM incluem repetição por flavor e saídas válidas reutilizadas pelo Gradle; não representam 1.910 testes únicos reexecutados à força. Logs completos em modern-check.log e release-check.log; XMLs e cobertura em global-reports/, global-coverage/ e global-results.json. modernCheck: 1.407 tarefas, 226 executadas, 13 de cache e 1.168 up-to-date.

| Instrumentação do gate | Aprovados | Ignorados |
|---|---:|---:|
| Preferences | 28 | 0 |
| Presentation | 3 | 0 |
| Cart | 65 | 0 |
| Home | 4 | 0 |
| Notifications | 10 | 0 |
| Retail | 39 | 1 |
| Tickets | 41 | 2 |

releaseCheck: 872 tarefas, 215 executadas, 3 de cache e 654 up-to-date. verify-release-artifacts.py conferiu arquivos atualizados nesta execução, SHA256, metadados, applicationId e flags. Os três APKs são unsigned; apksigner verify confirmou ausência de assinatura (Missing META-INF/MANIFEST.MF), coerente com a falta dos recursos privados de assinatura. Não foram assinados com chave debug nem distribuídos. Evidência: release-artifacts.json.

| Release | Identidade | Versão | Bytes |
|---|---|---|---:|
| Prd | br.gov.caixa.loterias.apostas | 1.25.0/230125000 | 38.802.864 |
| Tqs | br.gov.caixa.loterias.apostas.hmp | 1.24.0/230102400 | 38.802.896 |
| Automacao | br.gov.caixa.loterias.apostas.hmp | 1.24.0/230102400 | 38.802.900 |

HOME_V2=true, ENABLE_MOCK_BACKEND=false e ENABLE_NETWORK_LOGGING=false nos três debug/release; targetSdk 37 preservado. A lista de signatários de keyset está vazia nesta configuração local; não foi inventada configuração privada.

Retail cobre 585/592 linhas = 98,82%, com o novo colaborador incluído e mínimo 95,01% preservado. Domain 1667/1724 = 96,69%; Data 2228/2330 = 95,62%; demais relatórios/gates estão em global-results.json. Os três skips desta matriz são os casos de coordenadas que exigem 1080×2408/450dpi. As duas comparações de tipografia nativa executaram com o APK de referência instalado. Isto é separado da execução explícita de todos os cinco casos em 450dpi, descrita abaixo.

Comandos completos, sem exclusões/relaxamento de gates e com configurações de produção: JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home ANDROID_SERIAL=emulator-5562 ./gradlew modernCheck --offline --continue --console=plain; depois o mesmo ambiente com ./gradlew releaseCheck --offline --continue --console=plain. O Gradle pode reutilizar saídas válidas UP-TO-DATE/FROM-CACHE; as contagens de relatórios não significam reexecução de cada teste em cada etapa.

O filtro Retail recebeu somente PrizeReceiptDelivery*.class; todos os padrões anteriores e mínimo 0.9501 foram preservados. As entradas agora exigem JVM e cobertura Android efetiva, incluindo o novo colaborador. Nenhum gate, oráculo visual ou assertion foi removido/enfraquecido. contracts.json registra preservação dos fontes públicos/StateFlows, Domain/Data, manifestos/recursos XML, configuração global/app e dos cinco casos visuais. Inventário: 80 destinos Compose, zero Java próprio de produção e 872 arquivos Kotlin de produção (incluídos os source sets de flavors); uma classe colaboradora nova e seu Completion interno nesta rodada.

### Cinco casos condicionais: execução efetiva e limites

A AVD nova SimloSpecVisual20261019/API36 recebeu o APK nativo local de referência, sem substituir pacotes existentes. Override 1080×2408, 450dpi, fontScale 1.0. O original 1.24.0/build220102400 contém DetalhesComprasActivity, tema e as mesmas fontes regulares/bold; o registro físico tinha build230102400. Equivalência de fontes não prova equivalência integral do artefato. O hardware emulado é Pixel9Pro, com cutout/insets diferentes do Samsung SM-G556B usado pelas coordenadas gravadas.

| Caso unchanged | Resultado real |
|---|---|
| ControlledPrizeInteractionTest.installedPrizeSummaryRetainsTheProductionBottomCoordinate | Falha: esperado 880, obtido 960 |
| InstalledPurchasePairTest.installedPhysicalPairStartsFirstPurpleRowAtRecordedScreenCoordinate | Falha: esperado 720, obtido null; repetição unchanged também null |
| InstalledPurchasePairTest.installedPhysicalPairKeepsFavoriteAtNativeWeightedRowPosition | Aprovado |
| InstalledPurchasePairTest.installedMixedTypefaceStatusUsesNativeMultilineWrapContentHeight | Aprovado com contexto/tema/fontes nativos |
| InstalledPurchasePairTest.installedDataRowUsesNativeBaselineAlignedLinearLayoutHeight | Aprovado com contexto/tema/fontes nativos |

Total: 5 executados, 3 aprovados, 2 reprovados, 0 ignorados. O script run-five-visual.py registra os exits individuais em visual-results.json; seu exit 0 externo não significa aprovação dos casos. XMLs/logs originais ficam em visual-prize/ e visual-purchase/. Não se declara paridade visual completa.

Diagnóstico sem modificar os testes: métodos de captura existentes produziram PNGs em capture-retail/ e capture-tickets/. O painel do prêmio é [224,880) na referência e [304,960) no AVD, mesma altura 656px. A compra preparada mostra primeiro roxo 800, contra 720 da referência. A faixa superior branca é 66px versus 146px; visual-window-active.txt confirma statusBars 146px no perfil de 450dpi. CaixaDSLegacyTopBar aplica statusBarsPadding. Isso explica o deslocamento de 80px das capturas preparadas; não explica por si o null do teste sem readiness semântico, nem prova igualdade raster interna. Não foram aplicadas compensações de pixels, alterações de fonte/Row ou novos oráculos. Esses dois casos exigem repetição no perfil físico correspondente; o usuário informou que o aparelho ainda não está disponível. Tamanho/densidade originais do AVD foram restaurados após cada diagnóstico.

### Recursos locais e homologação real

| Integração | Evidência local disponível | Limite atual |
|---|---|---|
| OAuth PRD/DES | Discovery OIDC público respondeu 200 nas duas bases, TLS verificado | Sem conta autorizada; login/callback/refresh/logout não homologados |
| Biometria | Código e testes locais existentes | Sem dispositivo físico com biometria; hardware real não homologado |
| SSO | Integração AppAuth e configuração existente | Sem sessão/conta autorizada; SSO real não homologado |
| Fila | SDK FilaBRA2.6.5.1 e Topaz4.38.0.1658708 locais, gates estáticos | Sessão/fila privada real não homologada |
| Provedores privados | Configurações e contratos existentes | Acessos/ambiente autorizado e keyset aprovado não disponíveis; pagamentos/resgates reais não executados |

O inventário não encontrou aparelho físico conectado, sessão CAIXA/SSO no navegador ou variáveis locais de acesso; local.properties fornece apenas sdk.dir. O usuário confirmou explicitamente que conta e aparelho ainda não estão disponíveis. Não foram lidos senhas/tokens nem tentadas autenticações/transações financeiras. certificado.properties/Caixakeystore não estavam disponíveis para assinatura de distribuição.

As sondas do host usaram GET sem autenticação e TLS normal: os dois discoveries 200; os endpoints públicos de validação de versão PRD/TQS retornaram 403. real-endpoint-probes.json registra método/URL/status, não payloads privados. Isso comprova somente alcance/metadados, não a pilha/pinning do app, OAuth autenticado, biometria, SSO, fila ou provedores. Nenhuma proteção foi contornada.

**Pendências desta rodada:** aprovação das duas coordenadas no perfil físico correto; homologação autenticada real das cinco integrações; assinatura de distribuição com recursos privados aprovados. A indisponibilidade externa foi documentada, sem substituir integrações por mocks nem declarar 100% de atendimento à SPEC.md. Decisões funcionais anteriores, como a origem das mensagens legadas, permanecem conforme MIGRATION_PLAN.md e não foram resolvidas por estes testes.

Encerramento da infraestrutura de teste: os dois processos de emulador iniciados nesta rodada foram encerrados; somente a AVD temporária SimloSpecVisual20261019 foi apagada. As definições anteriores de AVD foram preservadas. device-final.json comprova restauração de 1280×2856/480dpi/font1.0 antes da limpeza. Sem alterações em credenciais, assinatura/pinning, manifestos, recursos de produção, flags persistentes, commits ou pushes.

## Central de mensagens local e módulos Hilt — 19/10/2026

**Referência e escopo:** usuário confirmou comparação com o legado. A central Java usa `getAllMensagensMock`; manter esse comportamento foi uma decisão explícita, sem fabricar histórico remoto, categorias, datas, exclusão ou preferências de push. Origem detalhada em docs/NOTIFICATION_INBOX_PARITY_AUDIT.md. A extração não comprova conformidade integral com SPEC3.3.

**Caracterização anterior:** suites Data/Domain/Notifications/App PrdDebug executadas novamente com `--rerun-tasks`: 570 casos JVM, zero falhas, BUILD SUCCESSFUL em 1m31s. O teste existente da factory já usava NotificationsModule.notificationRepository nesta execução anterior à extração, verificando a propriedade da instância e as ações através do ViewModel real. Não há correção funcional nova ou teste de reprodução RED nesta extração literal.

**Resultados dos testes após a extração:**

| Suite | Casos | Falhas/erros/skips |
|---|---:|---:|
| core:data | 229 | 0/0/0 |
| core:domain | 185 | 0/0/0 |
| feature:notifications JVM | 22 | 0/0/0 |
| app PrdDebug | 134 | 0/0/0 |
| app TqsDebug | 134 | 0/0/0 |
| app AutomacaoDebug | 134 | 0/0/0 |
| feature:notifications Android | 10 | 0/0/0 |

São 838 casos JVM, incluindo a repetição da suite App por flavor, e dez Android no AVD SimloComposeGates (Android16/API36, 1280×2856, 480dpi). O Gradle pode reutilizar saídas válidas; a caracterização anterior foi forçada e os testes Android desta rodada efetivamente executaram no emulador.

**Cobertura de linhas:** Data 2236/2338 (95,64%), Domain 1667/1724 (96,69%), Notifications 583/586 (99,49%). Gates de 95,01%, inputs de execução e padrões de inclusão de classes permaneceram intactos. Os três novos arquivos Data estão incluídos, sem exclusões novas.

**Contratos:** catálogo movido literalmente e todas as strings de mensagens/preferências iguais. `javap -public` do repository e de NotificationsModule idêntico à baseline. Hilt gerado nos três flavors mantém o provider local sem DoubleCheck e os dois providers de leitura com DoubleCheck; não há binding duplicado. Domain, ViewModel, UiState, StateFlow, fila de eventos, factory de produção, UI, manifest, rede/payloads e configurações de build mantiveram o conteúdo da baseline.

**Inventário desta etapa:** cinco arquivos Kotlin de produção adicionados (quatro tipos nomeados e uma função de catálogo), dois arquivos de produção refatorados e um teste existente atualizado; nenhuma tela nova migrada ou arquivo Java próprio introduzido. O inventário segue com 80 destinos Compose; existem 872 arquivos Kotlin em src/main de app/core/feature e zero Java de produção. As métricas históricas acima pertencem às respectivas rodadas.

**Limites:** não foram repetidos modernCheck integral, releaseCheck, os cinco visuais condicionais ou homologação real. A última matriz global aprovada permanece registrada na seção anterior; os dois visuais reprovados, releases unsigned e falta de conta/aparelho biométrico permanecem pendências. Nenhum oráculo, gate ou comportamento de autenticação foi alterado. Nenhum commit/push.

**Evidências:** build/verification/notification-origin-20261019/{characterization-before.log,validation.log,source-check.json,contracts-and-coverage.json,results.json,scoped.patch}; baseline de fontes/API preservada no mesmo diretório.

**Comando de validação do escopo** (JAVA_HOME OpenJDK17 e ANDROID_SERIAL=emulator-5560):

```sh
./gradlew :core:data:check :core:domain:check :feature:notifications:check \
  :app:assembleDebug \
  :app:testPrdDebugUnitTest :app:testTqsDebugUnitTest :app:testAutomacaoDebugUnitTest \
  :app:lintPrdDebug :app:lintTqsDebug :app:lintAutomacaoDebug \
  verifyKotlinFormatting verifyComposeMigration verifyLegacyUiInventory \
  verifyLegacyBackendParity verifyCleanArchitecture verifyCaixaDS verifyFilaBraAar \
  verifyTopazHeartbeat verifyNetworkSecurity verifyAppIdentityAndFlavors \
  --offline --continue --console=plain
```

Primeira execução: BUILD SUCCESSFUL em 2m59s; 1013 tarefas (155 executadas, seis de cache e 852 atualizadas). Checks/cobertura dos três módulos, APKs/JVM/lint dos três flavors debug e os dez gates aprovados. Revisão independente estática do patch delimitado, sem achados acionáveis; não substitui homologação externa. A revisão confirmou semântica, provenance, APIs, escopos e preservação dos demais arquivos. A última alteração de indentação foi submetida à mesma validação incremental, registrada em final-validation.log.

**Fechamento incremental:** mesmo comando aprovado novamente em 1m24s (23 tarefas executadas, 990 atualizadas). Os dez Android executaram novamente, sem falhas/skips. Relatórios finais confirmam as mesmas contagens/coberturas e assinaturas. Os três APKs debug e relatórios lint sem erros constam em debug-artifacts.json. Encerrado somente o emulador iniciado para esta rodada; definição e perfil do AVD existentes preservados.

## Checkout do carrinho: separação e validação — 19/10/2026

**Escopo:** CartCheckoutCoordinator separa entrada/coordenação, localização, permissões, compra recente, validação, avisos e redirecionamentos. Plano: docs/superpowers/plans/2026-10-19-cart-checkout.md. Baseline de 1554 arquivos e APIs em build/verification/cart-checkout-20261019/. O checkout contém trabalho anterior autorizado; esta comparação usa snapshots desta etapa, não HEAD.

**Caracterização no original:** `./gradlew :feature:cart:testDebugUnitTest --rerun :core:domain:test --offline --continue --console=plain` aprovado em 3 s,103 testes Cart e 185 Domain. Duas caracterizações adicionadas antes da extração passaram sobre o coordenador original. A extração passou novamente nos 103 Cart em 3 s. Trata-se de preservação de comportamento; nenhum RED de correção funcional foi alegado.

As caracterizações protegem publicação de ação somente após registro suspenso da permissão, bloqueio de repetição e retomada; também protegem o cancelamento suspenso nas consultas de histórico/validação sob o try/catch/finally da localização, propagação da CancellationException, limpeza e retry. Os testes existentes cobrem ordem de conta/refresh/localização/histórico/validação, carrinho vazio, limites, falhas, compra recente, consentimento com carrinho alterado, permissões/settings, avisos/conectividade e consumo por identidade.

| Suite final | Casos | Falhas/erros/skips |
|---|---:|---:|
| Cart JVM |103|0/0/0|
| Domain JVM |185|0/0/0|
| App PrdDebug JVM |134|0/0/0|
| Cart Android |65|0/0/0|

Total:422 casos JVM e 65 Android aprovados. Gradle reutilizou saídas válidas de testes/fontes inalterados; os 103 Cart foram forçados na caracterização anterior, e os 65 Android efetivamente executaram nesta rodada no SimloComposeGates/API 36,1280 × 2856/480 dpi/fontScale 1.0.

**Cobertura:** Cart 2103/2160 (97,36%), Domain 1667/1724 (96,69%); coordenador e seis colaboradores 209/209 (100% de linhas, não de todos os comportamentos possíveis). Gate 95,01%, includes de todas as classes, inputs JVM/Android e tarefas conectadas inalterados; sem novas exclusões. Cart check inclui lint e instrumentação obrigatória.

**Comando final** (JAVA_HOME OpenJDK 17 e ANDROID_SERIAL=emulator-5560):

```sh
./gradlew :feature:cart:check :core:domain:check \
  :app:assemblePrdDebug :app:testPrdDebugUnitTest :app:lintPrdDebug \
  verifyKotlinFormatting verifyComposeMigration verifyLegacyUiInventory \
  verifyLegacyBackendParity verifyCleanArchitecture verifyCaixaDS verifyFilaBraAar \
  verifyTopazHeartbeat verifyNetworkSecurity verifyAppIdentityAndFlavors \
  --offline --continue --console=plain
```

Resultado: BUILD SUCCESSFUL em 5 m 18 s;886 tarefas,50 executadas e 836 atualizadas. Cart/Domain checks, cobertura, assemble/JVM/lint PrdDebug e dez gates aprovados. Logs completos: characterization-before.log, characterization-after.log e validation.log.

**Equivalência/revisão:**18 corpos movidos literalmente; outras verificações confirmam guardas/ordem, ramos de validação, mapeamento de rejeição dentro do update original e ausência de novo dono do estado. Construtor/métodos declarados de CartCheckoutCoordinator, CartViewModel e CartUiState iguais via javap, excluídas bridges access$ geradas pelo compilador para implementação privada. ViewModel/UiState, Domain/Data, UI, manifestos, recursos, segurança e Gradle preservados por hash. Revisão independente final sem achados ou itens menores pendentes.

**Artefatos e limites:** resultados em results.json, source-contracts.json, public-api-check.json e scoped.patch. Nenhuma nova conversão de tela; seis classes Kotlin internas adicionadas. Outros flavors/releaseCheck e modernCheck integral não foram repetidos, nem os cinco visuais condicionais ou homologação privada. Permanecem as duas divergências geométricas, assinatura release indisponível e limitações de conta/aparelho já documentadas. A central de mensagens mantém a referência demonstrativa autorizada. A rodada não declara 100% da SPEC. Sem commits/pushes.

## Histórico Tickets: extração e validação — 19/10/2026

**Escopo:** coordenação, apostas, compras, filtros, paginação e recuperação de falhas. Plano: docs/superpowers/plans/2026-10-19-tickets-history.md. Baseline 1560 arquivos e três APIs compiladas em build/verification/tickets-history-20261019/. A comparação preserva o checkout com trabalho anterior autorizado, usando snapshots desta rodada.

**Caracterização anterior:** `./gradlew :feature:tickets:testDebugUnitTest --rerun :core:domain:test --offline --continue --console=plain` aprovado em 12 s:72 TicketsJVM e 185 Domain, sem falhas/erros/skips. Os três testes novos já estavam presentes e passaram no coordenador original. Após extração, os 72 Tickets passaram em 11 s (characterization-after-fixed.log). Uma referência incorreta gerada no retry foi corrigida após a primeira compilação; o diagnóstico está preservado em characterization-after.log. Nenhum teste RED de correção funcional nova foi alegado para esta extração de comportamento existente.

As novas caracterizações protegem: descarte de resposta append após expirar a sessão, sem duplicar chamadas; retry com catálogos já existentes e filtros atuais; leitura de filtros na execução da coroutine, preservando alterações feitas após o enfileiramento. Os testes anteriores também cobrem falha/retry do mesmo offset, página vazia, origem de histórico vazio, filtros/querykeys, cancelamento e verificações de sessão entre catálogos/páginas.

| Suite final | Casos | Aprovados | Falhas/erros | Ignorados |
|---|---:|---:|---:|---:|
| Tickets JVM |72|72|0/0|0|
| Domain JVM |185|185|0/0|0|
| App PrdDebug JVM |134|134|0/0|0|
| Tickets Android |43|41|0/0|2|

Total 391 JVM e 41 Android aprovados. Gradle reutilizou saídas válidas de módulos/testes inalterados; a suite Tickets JVM anterior foi forçada, e os 43 casos Android efetivamente rodaram nesta etapa (41 aprovados e 2 condicionais ignorados).

**Ambiente:** AVD temporário SimloTicketsHistory 20261019/API 36,1280 × 2856/480 dpi/fontScale 1.0, serial emulator-5562. O APK legado local 1.24.0/build 220102400 foi instalado somente nessa AVD para o contexto nativo de fonte/tema. A instalação moderna 1.25.0 do SimloComposeGates foi preservada; o emulador 5580 alheio à tarefa não foi alvo de instalação/instrumentação. Perfil/hash da referência em device-profile.json. Nenhum login ou teste financeiro real.

**Dois skips:** InstalledPurchasePairTest.installedPhysicalPairStartsFirstPurpleRowAtRecordedScreenCoordinate e installedPhysicalPairKeepsFavoriteAtNativeWeightedRowPosition exigem o perfil físico 1080 × 2408/450 dpi. As assertivas/condições permaneceram intactas. installedDataRowUsesNativeBaselineAlignedLinearLayoutHeight e installedMixedTypefaceStatusUsesNativeMultilineWrapContentHeight executaram com contexto/tema/fontes nativos e passaram. O APK/build do contexto não comprova equivalência integral ao Samsung físico registrado; as divergências visuais globais anteriores permanecem.

**Cobertura:** Tickets 1090/1118(97,50%), Domain 1667/1724(96,69%); coordenador e seis colaboradores 236/236 linhas(100% de linhas, sem afirmar todos os comportamentos possíveis). O build recebeu somente seis includes de classes/arquivos/nested coroutine bodies. Todos os includes anteriores, entradas obrigatórias JVM/Android e mínimo 0.95 permaneceram iguais.

**Falha de ambiente diagnosticada:** a primeira validação do escopo passou nos componentes não instrumentados, mas connectedDebugAndroidTest falhou antes de iniciar casos com UnsupportedOperationException. Repetição estreita com stacktrace localizou AGP 9.2.1, DeviceProviderInstrumentTestTask.getFilteredDevices → ImmutableCollection.remove, acionado pela opção --serial. O bytecode local confirmou a seleção alternativa por ANDROID_SERIAL no provider. A execução final usa apenas essa variável, com o mesmo dispositivo e sem alterar Gradle/configuração/testes/gates. Logs: validation.log e device-selection-stacktrace.log; sucesso em final-validation.log.

**Comando final** (JAVA_HOME OpenJDK 17 e ANDROID_SERIAL=emulator-5562):

```sh
./gradlew :feature:tickets:check :core:domain:check \
  :app:assemblePrdDebug :app:testPrdDebugUnitTest :app:lintPrdDebug \
  verifyKotlinFormatting verifyComposeMigration verifyLegacyUiInventory \
  verifyLegacyBackendParity verifyCleanArchitecture verifyCaixaDS verifyFilaBraAar \
  verifyTopazHeartbeat verifyNetworkSecurity verifyAppIdentityAndFlavors \
  --offline --continue --console=plain
```

Resultado: BUILD SUCCESSFUL em 4 m 2 s;886 tarefas,17 executadas e 869 atualizadas. Tickets/Domain checks, cobertura/lint, assemble/JVM/lint PrdDebug e dez gates aprovados. Houve aviso de Metaspace com encerramento previsto do daemon ao final; o build terminou com sucesso, sem alteração da configuração de memória do projeto.

**Equivalência/revisão:**29 métodos comparados, admitindo somente qualificação do novo colaborador e visibilidade interna necessária. APIs declaradas de TicketsHistoryCoordinator/TicketsViewModel/TicketsUiState iguais via javap, excluídas bridges sintéticas access$ de implementação privada. ViewModel, UiState, StateFlow, Domain/Data, UI, rede, Hilt, recursos, manifests e demais builds preservados por hash. Revisão independente reconstruiu os nove arquivos do patch e não encontrou achados acionáveis ou itens menores pendentes.

**Evidências/limites:** results.json, source-contracts.json, scoped.patch, APIs e logs no diretório desta rodada. Seis classes Kotlin internas introduzidas, sem nova migração de tela. modernCheck integral, demais flavors/releaseCheck, os cinco visuais no perfil especial e homologação privada não foram repetidos. Permanecem os limites globais anteriores, inclusive assinatura release indisponível e origem demonstrativa autorizada das mensagens. Sem commits/pushes.

**Limpeza concluída:** encerrado somente o processo temporário5562 e removida somente a AVD SimloTicketsHistory20261019 criada nesta etapa. As definições anteriores e a instalação moderna de referência foram preservadas.

## Home clássica: composição e equivalência visual — 19/10/2026

**Escopo:** HomeLegacyContent de 302 para 103 linhas; seis arquivos novos (TopBar, Carousel, Cards, QuickAccess, Campaign e CartSummary), sete funções Composable internas sem estado e nenhuma classe de produção nova. As duas data classes e a assinatura de conteúdo permanecem iguais. Inventário de destinos/telas não mudou: nenhuma nova migração nesta etapa, nenhum Java próprio introduzido. Plano em docs/superpowers/plans/2026-10-19-home-legacy-content.md; 1566 snapshots/APIs/evidências em build/verification/home-legacy-20261019/.

**Preservação:** key/remember/LaunchedEffect, seleção/cálculos de largura e tratamento vazio continuam no dono original. BoxScopes sem wrappers novos preservam alinhamento/constraints, ordem de desenho, modifiers/insets/dimensões, escala/offset, tempos/easings das animações, gates de scroll/clique/rodapé e argumentos de callbacks. HomeRoute/HomeScreen, LotteryCard existente, mapeadores, ViewModels/UiStates/StateFlows, Domain/Data, Hilt, rede, recursos, manifestos e builds foram preservados por hash. Nove verificações de corpos/estado/modelos passaram. As três APIs declaradas foram comparadas por javap, excluindo apenas bridges sintéticas access$ de implementação privada. Revisão independente: zero achados críticos/importantes/menores; nenhum julgamento de comportamento do escopo recusado. O único ajuste posterior foi sincronização do relógio nativo nos helpers de captura dos testes.

**Caracterização:** os seis testes de coordenação e os quatro de drawer existentes mantêm suas assertivas. Foram adicionados dois casos diretos de conteúdo (geometria/callbacks/rodapé expandido e seleção após recomposição) e um caso com oito quadros intermediários de atalhos/campanha, controlados pelo mesmo relógio Compose. Total direcionado: 13 casos, sem erros/falhas/skips no original e na extração.

**Capturas e diagnóstico:** a primeira execução no original teve dois erros transitórios de PixelCopy/forceRedraw; os dois casos passaram na repetição inalterada. O UTP removia os arquivos externos ao desinstalar o APK; a opção local do AGP leaveApksInstalledAfterRun foi verificada e aplicada somente no comando para recuperar os artefatos. Uma execução completa posterior do original passou (1 m 1 s), assim como a primeira execução da extração (1 m 9 s). A comparação inicial encontrou cinco diferenças localizadas nas regiões do último toque. Repetir Quina com o mesmo código também alterou 151719 pixels no mesmo retângulo do botão, comprovando instabilidade da captura sem mudança de produção.

Os helpers de captura agora esperam 600 ms de relógio nativo para o ripple; o relógio Compose, os deltas dos quadros, animações/indications de produção e todas as assertivas permanecem iguais. Original byte-idêntico e extração foram então executados com o mesmo harness: 13/13 em 1 m 21 s e 13/13 em 1 m 39 s. **17 PNGs (incluindo oito quadros de animação) idênticos pixel a pixel e um arquivo de bounds idêntico; 18/18 artefatos iguais.** Nenhuma máscara, tolerância, exclusão de pixels ou substituição de golden. Evidências iniciais/repetição e finais preservadas separadamente em captures/before, after, repeat, stable-before e stable-after; resultado em stable-capture-comparison.json e diagnóstico em same-source-ripple-diagnosis.json.

**Ambiente e comando direcionado:** AVD temporário SimloHomeLegacy20261019, serial emulator-5560, API 36, 1280 × 2856/480 dpi/fontScale 1.0. Identidade de validação e HOME_V2=false aplicados somente no comando/init script para alcançar a Home clássica, sem gravar configuração no projeto. O perfil está em device-profile.json. Nenhum login ou operação financeira real.

```sh
JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home \
ANDROID_SERIAL=emulator-5560 ./gradlew :app:connectedPrdDebugAndroidTest \
  -I build/verification/home-legacy-20261019/validation.init.gradle \
  -PhomeV2=false -Pandroid.injected.androidTest.leaveApksInstalledAfterRun=true \
  -Pandroid.testInstrumentationRunnerArguments.class=br.gov.caixa.loterias.apostas.HomeScreenCoordinationUiTest,br.gov.caixa.loterias.apostas.HomeDrawerUiTest,br.gov.caixa.loterias.apostas.HomeLegacyContentUiTest \
  -Pandroid.testInstrumentationRunnerArguments.homePhase=stable-after --offline --console=plain
```

**Validação padrão:** sem init script, sem override de identidade e sem flag homeV2. Baseline JVM aprovado em 2 m 7 s (App PrdDebug 134 + Home 33, saídas válidas atualizadas). Comando final com JAVA_HOME OpenJDK17 e ANDROID_SERIAL=emulator-5560:

```sh
./gradlew :feature:home:check \
  :app:assemblePrdDebug :app:testPrdDebugUnitTest :app:lintPrdDebug \
  verifyKotlinFormatting verifyComposeMigration verifyLegacyUiInventory \
  verifyLegacyBackendParity verifyCleanArchitecture verifyCaixaDS verifyFilaBraAar \
  verifyTopazHeartbeat verifyNetworkSecurity verifyAppIdentityAndFlavors \
  --offline --continue --console=plain
```

Resultado: **BUILD SUCCESSFUL em 1 m 55 s**, 873 tarefas (39 executadas, sete de cache e 827 atualizadas). Home check/lint/cobertura, AppPrdDebug assemble/JVM/lint e os dez gates aprovados. Nenhum override de identidade/HOME_V2 nessa execução. Log: final-validation.log.

| Suíte validada | Casos | Falhas/erros | Ignorados |
|---|---:|---:|---:|
| App PrdDebug JVM |134|0/0|0|
| Home JVM |33|0/0|0|
| Home Android (check) |4|0/0|0|
| Home clássica Android (direcionado) |13|0/0|0|

Resultados: 167 JVM e 17 Android aprovados. O Gradle reutilizou resultados válidos dos testes Home JVM inalterados; App JVM e os quatro Home Android executaram na validação final, e os 13 direcionados executaram em ambas as versões. Cobertura Home: 438/444 linhas (98,65%); mínimo original 0.9501, padrões e entradas JVM/Android preservados. A cobertura de Home não representa cobertura percentual dos novos Composables do módulo App; eles são protegidos pela caracterização instrumentada e comparação visual exata.

**Limites:** nenhum gate/oráculo/mínimo de cobertura alterado. Não foram repetidos modernCheck integral, outros flavors/releaseCheck, os cinco visuais condicionais de perfil especial ou homologação privada. Permanecem as duas divergências físicas anteriores, assinatura release indisponível, ausência confirmada de conta/aparelho biométrico e a central demonstrativa autorizada como referência legada. A equivalência desta extração não encerra 100% da SPEC. Sem commits/pushes.

**Limpeza:** encerrado e recolhido somente o emulador5560; removida somente a AVD temporária SimloHomeLegacy20261019. Definições anteriores preservadas. Comparação final dos1566snapshots confirmou mudanças apenas no conteúdo/teste de coordenação e nos cinco relatórios; sete novos fontes desta rodada (seis produção, um teste), além de plano/ledger.

## MVVM/Clean e propriedade por feature — 19/10/2026

**Escopo concluído em produção:** 14 features com Presentation/Domain/Data pertencentes à feature; estados/efeitos fora dos arquivos de ViewModel; coordenação, componentes e mapeadores classificados por responsabilidade real. São 665 fontes Kotlin de produção nas raízes físicas: 464 main, 58 domain, 81 data, 17 platform e 45 host. A tabela por feature está em MIGRATION_PROGRESS.md. Plano/design em docs/superpowers/; evidências e snapshots anteriores em build/verification/feature-mvvm-20261019/.

Domain/Data/Platform continuam compilados uma única vez pelos módulos core existentes; Home host é compilado somente por app. Não há novo módulo/dependência. Essa organização conserva os ciclos de contratos preexistentes e não representa isolamento binário de cada domínio nem migração para KMP. Account usa portas puras, deixando Intents nas rotas; revisão local do carrinho usa casos de uso; preferências Home têm ViewModel/StateFlow próprio, escrita ordenada em IO e proteção contra publicação após cancelamento. Os StateFlows existentes são mantidos. FQNs e assinaturas nas fronteiras UI/plataforma de Account/injeção da revisão local foram adaptados com os consumidores; não se declara compatibilidade binária dos pacotes antigos.

Também concluídas as sete extrações concretas: HomeLotteryCard 290→100 linhas, HomeRoute 283→186, BettingGameViewModel 296→154, CartViewModel 264→156, TicketsViewModel 254→206, PaymentMethodsContent 268→63 e ConfirmedBetContent 267→92. Os colaboradores recebem os mesmos owners, estados, escopos e dispatchers. Nenhuma alteração em payloads, chaves de preferências, recursos, manifestos, identidade ou segurança.

**Caracterização e provas:** baseline de 1602 snapshots do checkout já modificado pelo trabalho anterior, não comparação com HEAD. Dos 631 registros de movimento, 600 corpos são literalmente equivalentes após normalizar pacotes/imports e 31 separações/fronteiras estão explicitamente revisadas; zero diferença inexplicada. Há 65 reclassificações de auxiliares e sete divisões mistas. Cinco APIs públicas compiladas de fachadas de extração iguais; 42 trechos das extrações não Home e 14 verificações Home equivalentes. São 251 recursos/configurações protegidos idênticos e 23 configurações de cobertura com mínimos/padrões anteriores mantidos, somente adições. Revisões independentes do lote, da classificação final e do escopo completo sem achados acionáveis.

O guard de arquitetura verifica as raízes reais, direção de dependência e acessos totalmente qualificados. Nove testes de política aprovados. Fixtures no Gradle real rejeitaram Android em Domain relocado e Activity/Java/cor literal em Home host; fixtures removidas após execução. Não houve redução de limites, exclusão de testes ou relaxamento de gates.

**Testes novos dos seams:** duas caracterizações de capacidades de login, sete de consulta/exclusão do carrinho local e uma de preferências em Domain; oito testes de HomePreferencesViewModel, incluindo thread IO real, ordem de comandos e gravação nativa concluída após cancelamento sem publicação tardia. Assertivas anteriores de Account/Auth e demais features mantidas. Ajustes de construtor/import de fixtures acompanham os novos pacotes/portas.

**Home visual:** 22 testes Android aprovados (13 clássica/drawer e nove V2). A referência e o resultado final dos 13 casos clássicos produziram 17 PNGs e um arquivo de medidas exatamente iguais, incluindo oito quadros intermediários. A primeira comparação teve diferença RGB máxima 2 em uma região 120×120 do último toque; repetir o caso inalterado e depois a suíte completa inalterada reproduziu a referência exatamente. Não foram alterados produção, assertivas, oráculos, máscaras ou tolerâncias para esse diagnóstico. Evidências: stable-capture-comparison.json e home-ripple-diagnosis.json.

**Matriz global padrão:** OpenJDK17, SDK local, AVD temporário SimloFeatureMvvm20261019/emulator-5560, API36, 1280×2856/480dpi/fontScale1.0. Nenhum init script, filtro de teste ou override de flags/identidade nos dois comandos globais:

```sh
JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home \
ANDROID_SERIAL=emulator-5560 ./gradlew modernCheck --offline --continue --console=plain
JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home \
ANDROID_SERIAL=emulator-5560 ./gradlew releaseCheck --offline --continue --console=plain
```

modernCheck: **BUILD SUCCESSFUL, 15m29s**, 1407 tarefas (233 executadas, duas de cache, 1172 atualizadas). Inclui 24 checks de módulos, dez gates e assemble/JVM/lint dos três flavors debug. Relatórios preservados antes de qualquer instrumentação filtrada: global-results.json, global-reports/ e global-coverage/. São 1933 casos JVM, zero falhas/erros/skips; 193 Android, 190 aprovados e três condicionais ignorados no perfil padrão. Contagens incluem repetições por flavor e resultados válidos reutilizados pelo Gradle, não 1933 testes únicos reexecutados à força. A tentativa global inicial falhou somente na compilação de duas fixtures; imports/construtor foram corrigidos e o comando completo repetido com sucesso.

releaseCheck: **BUILD SUCCESSFUL, 7m39s**, 872 tarefas (271 executadas, seis de cache, 595 atualizadas). Três APKs minificados atuais, target37 e flags HOME_V2=true, ENABLE_MOCK_BACKEND=false, ENABLE_NETWORK_LOGGING=false. Prd mantém applicationId oficial, versão1.25.0/build230125000; Tqs/Automacao mantêm applicationId oficial.hmp, versão1.24.0/build230102400. SHA-256 e inspeção dos mapas R8 confirmam os novos pacotes e a implementação atual. Os três APKs são unsigned; assinatura privada ausente, sem publicação/distribuição.

Coberturas verificadas contra os mínimos originais: Auth97,81%, Data95,64%, KeyManagement99,16%, Network99,11%, Domain96,28%, Preferences98,78%, Queue100%, Account98,10%, Presentation100%, Betting98,28%, Home98,63%, Marketplace99,73%, MarketplaceFilter97,98%, Cart97,41%, Notifications99,49%, Onboarding100%, Payment99,40%, Results98,11%, Retail98,82%, Tickets97,49%. ResponsibleGaming passou seu gate original95%, sem relatório XML configurado; Common também passou seu gate. Percentuais correspondem ao escopo de classes original e às adições explícitas, não à cobertura de todos os Composables.

**Instrumentação complementar:** os testes Android existentes das features restantes foram compilados/executados após a matriz global, sem alterar assertivas. Comando inicial: `./gradlew :core:auth:connectedDebugAndroidTest :feature:account:connectedDebugAndroidTest :feature:betting:connectedDebugAndroidTest :feature:marketplace:connectedDebugAndroidTest :feature:marketplace-filter:connectedDebugAndroidTest :feature:onboarding:connectedDebugAndroidTest :feature:payment:connectedDebugAndroidTest :feature:support:connectedDebugAndroidTest --offline --continue --console=plain`, com JAVA_HOME/ANDROID_SERIAL acima e perfil1080×2408/450dpi. Resultados preservados em additional-ui-reports/ antes das repetições direcionadas.

| Suíte complementar | Casos | Resultado |
|---|---:|---|
| Auth |13|13 aprovados|
| Account, repetição final |43|43 aprovados; check completo aprovado|
| Betting no perfil450 |106|100 aprovados; seis divergências abaixo|
| Marketplace no perfil450 |27|25 aprovados; dois itens fora do viewport|
| MarketplaceFilter |17|17 aprovados|
| Onboarding |9|9 aprovados|
| Payment |32|32 aprovados|
| Support |7|7 aprovados|
| Betting, quatro classes nativas no perfil480 |24|24 aprovados|
| MarketplaceSpecialGames no perfil480 |4|4 aprovados|

A primeira compilação de Account revelou um import de LogoutStatusTest ainda apontando para route depois da separação de LogoutStatus em components. Corrigido somente esse import, mantendo todas as assertivas; a repetição executou 43 testes e o check, aprovada em2m57s. Os cinco XMLs Account anteriores encontrados no primeiro runner eram antigos, pois a compilação impediu a execução; estão explicitamente marcados como stale no relatório e não contam como casos novos. Nenhuma mudança de produção posterior ao modernCheck/releaseCheck.

As quatro divergências geométricas nativas de Betting em450dpi (Loteca/colunas) desapareceram ao repetir as quatro classes historicamente validadas em1280×2856/480dpi: 24/24 aprovados em2m19s. As duas falhas de visibilidade de Marketplace desapareceram no mesmo perfil, quatro casos aprovados em15s. O runner misto de450dpi teve exit1 e foi preservado como falha, sem converter seu resultado em aprovação global.

Os outros dois erros Betting são os prompts físicos de Dia de Sorte/Lotofácil: origem esperada95px, obtida93px. Foi construído um projeto temporário isolado a partir dos1602snapshots iniciais, todos com hashes verificados idênticos, e executada a mesma BettingPromptOriginTest sem modificar fontes/assertivas, no mesmo AVD/API/perfil450dpi. **Os dois erros são idênticos antes e depois da refatoração.** O projeto anterior também retornou exit1, com dois casos executados. Provas: baseline-visual-source-equivalence.json, baseline-visual-profile.json, baseline-prompt-ui.log e prompt-before-after-comparison.json. Isso comprova ausência de regressão nesses dois casos, mas não aprova sua paridade com o aparelho físico de referência.

Os comandos direcionados completos estão em final-ui-results.json. As repetições480 usam somente `-Pandroid.testInstrumentationRunnerArguments.class=` para as quatro classes Betting e MarketplaceDetailSpecialGamesUiTest; Account foi executado integralmente, sem filtro. Não há mudança de coordenadas, métricas, scroll de produção, tolerâncias ou oráculos para acomodar o emulador.

**Cinco visuais condicionais, executados explicitamente:** nenhum skip. Foi instalado somente no AVD próprio o APK legado local `installed-reference-20261005/legacy-installed.apk` (versão1.24/build230102400), correspondente à versão registrada e com os controladores/tema nativos necessários. Foi usado também o APK oráculo CX local já existente; nenhuma fonte/serviço fictício novo. Perfil1080×2408/450dpi/fontScale1.0. O runner run-five-visual.py registra comandos completos, copia XMLs antes de sobrescrita e retorna exit1 quando há falha.

| Caso | Resultado |
|---|---|
| Resumo de prêmio, coordenada inferior física |Falhou: esperado880, obtido960|
| Primeira linha roxa de compra, coordenada física |Falhou: esperado720, obtido800|
| Favorito na posição da linha ponderada nativa |Aprovado|
| Altura multilinha com a fonte nativa |Aprovado|
| Altura de linha nativa com alinhamento por baseline |Aprovado|

Três aprovados, duas falhas preservadas; ambos os erros físicos apresentam deslocamento80px. O APK legado desta execução corresponde ao build230102400; o diagnóstico antigo que encontrava linha roxa nula usava outro artefato e não foi reutilizado como resultado atual. Não foram compensados insets de produção nem editados asserts/goldens. Evidências atuais: visual-results.json, visual-profile.json, visual-prize/, visual-purchase/ e respectivos logs. O mesmo número de falhas físicas já era pendente; esta rodada não declara paridade integral com o firmware/insets Samsung. Os dois prompts adicionais também aguardam validação física, embora sua falha idêntica antes/depois esteja comprovada localmente.

**Limites finais:** refatoração de produção e matriz global de build/check concluídas; a SPEC inteira não pode ser declarada100% homologada. Faltam perfil físico equivalente para os quatro casos de coordenadas citados, conta autorizada/dispositivo biométrico para OAuth, biometria, SSO, fila e provedores privados, além dos recursos privados de assinatura de distribuição. A indisponibilidade de conta/aparelho foi confirmada pelo usuário; recursos locais foram verificados, sem solicitar/enviar credenciais ou executar transações financeiras reais. A central de mensagens conserva as amostras e semântica local do legado, conforme referência expressamente autorizada, sem API inventada. Não houve stage, reset, commit, push ou publicação.

**Encerramento local:** após o único ajuste final de import de teste, os dez gates foram executados novamente: BUILD SUCCESSFUL em17s, dez tarefas executadas, 1377 fontes Kotlin verificados sem violações de whitespace. Guard de camadas, nove testes de política e prova631/600/31 repetidos e aprovados. O perfil do AVD foi restaurado para1280×2856/480dpi/fontScale1.0 antes de encerrar somente emulator-5560 e remover somente SimloFeatureMvvm20261019. Pixel_9_Pro, SimloComposeGates e SimloComposeValidation preservados. Projeto temporário da comparação anterior removido; snapshots1602, hashes, comandos, logs, XMLs, imagens e patch desta rodada preservados. Evidências: final-gates.log, restored-device-profile.json, cleanup.json e scope.json/scoped.patch.

## Módulos independentes por feature — 19/10/2026 (validação local concluída)

Evidências: `build/verification/feature-modules-20261019`. Snapshot de 1.676 arquivos e 1.683 contratos compilados antes das mudanças; checkout anterior preservado. A captura final, repetida após restaurar o debug padrão, contém 2.293 classes/fachadas de topo e 1.027 superfícies públicas protegidas, com zero quebra de classe, método, campo, superclasse ou interface e zero dono compilado duplicado. As 37 declarações de ViewModel/StateFlows são iguais; 392 declarações Domain foram preservadas e 95 corpos de casos de uso têm implementação canônica com fachada pública antiga. Essas verificações não afirmam comparação separada dos bridges sintéticos `$default`; as declarações HTTP originais, incluindo defaults, foram mantidas literalmente.

**Arquitetura:** 67 módulos declarados; 69 projetos no grafo configurado incluem os dois pais implícitos Core/Feature. 16 módulos Domain/API,13 Data,9 Transport, CoreDatabase/CoreDatastore/CoreTesting e cinco plugins de convenção. Cada fonte de produção é compilada por seu próprio módulo. Grafo real acíclico em `build/reports/architecture/feature-modules.json`; não há roots externos, compilação agregada ou dependência de feature no app. Removidos 13 imports não usados e quatro dependências Data em SupportData, confirmados também pela ausência de referências nas 213 classes consumidoras compiladas. 158 bindings e grafos por feature conservam escopos, chaves, owners e ordem das ações. As fábricas/contratos públicos antigos continuam disponíveis.

**Matriz global final:** executada com Java17 e `ANDROID_SERIAL=emulator-5560`, sem init de identidade, flag Home ou exclusão de tarefa. Comandos: `./gradlew modernCheck --offline --continue --console=plain` e `./gradlew releaseCheck --offline --continue --console=plain`. O primeiro inclui assemble/JVM/lint de Prd, Tqs e Automação e todos os checks; o segundo gera os três flavors release minificados.

| Comando | Resultado final | Tarefas |
|---|---|---|
| modernCheck |Aprovado em16m50s|2418:455 executadas,26 cache,1937 up-to-date|
| releaseCheck |Aprovado em5m30s|1539:307 executadas,25 cache,1207 up-to-date|
| Checks dos módulos corrigidos, Data/App JVM e guards |Aprovados em9m9s|1441:163 executadas,1278 up-to-date|
| Restauração do PrdDebug padrão e guards |Aprovada em20s|885:79 executadas,19 cache,787 up-to-date|

Os relatórios efetivos foram copiados antes de serem sobrescritos pelos visuais: 2.228 casos reportados, zero falha/erro e três skips condicionais. O total inclui resultados válidos reutilizados pelo Gradle e repetidos por flavor; não representa casos únicos ou todos forçados a executar novamente. Os três skips físicos da matriz padrão foram executados explicitamente na rodada dos cinco visuais abaixo. Logs: `modern-check-final.log`, `release-check-final.log`, `corrected-modules-final.log`, `default-debug-restoration.log`; XMLs/totais em `final-global-reports/` e `final-global-report-totals.json`.

| Fronteira | Linhas cobertas/total | Cobertura |
|---|---|---|
| Domain |2028/2110|96,11%|
| Data |2449/2571|95,25%|
| Network |669/675|99,11%|
| Persistência |530/536|98,88%|
| Cart presentation |2145/2202|97,41%|

Todos os mínimos anteriores permanecem. Persistência inclui os novos handles comuns e 28 casos Android reais,6 AccountData e22 CartData, sem falha/erro/skip. SQLite/SharedPreferences mantêm schema, SQL, nomes, chaves, flags, transações, normalização de contexto e commit/apply. MainDispatcherRule mantém Standard nos seis consumidores anteriores e Unconfined nos outros dois. Relatórios agregam classes/execuções dos donos reais; não compilam fontes de outros módulos.

**Correções observadas pela matriz:** a primeira modernCheck falhou em três merges de manifest de teste por falta de appAuthRedirectScheme e em dois lints NewApi de Base64 nos novos Data Android. Os mesmos placeholders já usados em Account/CoreAuth/app foram fornecidos a Cart/Home/Retail. O desugaring já estava habilitado, mas 2.0.3 não contém Base64; somente essa linha do catálogo foi atualizada para 2.0.4, primeira versão com o suporte, conforme o [changelog oficial](https://github.com/google/desugar_jdk_libs/blob/master/CHANGELOG.md). Sem troca de encoder/payload, aumento de minSdk, baseline/supressão de lint ou atualização indiscriminada. O DEX minSdk24 contém `j$/util/Base64` e não `java/util/Base64`; isso não equivale a execução em aparelho API24/25. As 241 declarações protegidas de rede/auth/chaves/fila e 246 arquivos protegidos continuam iguais; o catálogo tem exatamente essa alteração intencional, registrada na prova de equivalência.

O gate Cart inicialmente caiu para 87,15% porque o padrão amplo passou a incluir 259 linhas de DI/navegação movidas do app. A fronteira anterior de presentation já tinha 97,41%. `**/presentation/**/*.class` conserva as 61 famílias anteriores, 132 classes incluindo filhos/lambdas/Compose e 51 fontes anteriores, mínimo 0.9501 e todos os produtores/assertivas. O padrão HomeDrawer conserva as três famílias e 20 classes de topo anteriormente protegidas. Provas: `cart-coverage-baseline-boundary.json` e `home-coverage-baseline-boundary.json`; relatórios falhos anteriores preservados. Nenhuma classe antes protegida foi retirada.

**Guards e revisão:** dez fixtures reais rejeitadas: Android em Domain, Domain→Network, raiz externa, dono duplicado, ciclo, dependência no app, cor literal em CoreNavigation, controller de feature, Java em módulo filho e layout XML. Arquivos restaurados byte a byte e baseline positivo aprovado; 14 testes de política aprovados. Inventário recebeu somente 24 cópias XML idênticas de desenhos já auditados, sem exceção geral. Revisão independente do conjunto e das correções finais sem achados acionáveis; 95 corpos canônicos e 408 testes existentes conservaram corpos/assertivas. Relatório `independent-review.md`. Contratos HTTP comparados por reflexão; teste de rotação do provider durante avaliação do argumento aprovado, conservando o cliente selecionado para a operação iniciada.

**Home/navegação:** 13 testes antes e 13 depois do isolamento da Home; 18 arquivos de captura (17PNG+medidas) idênticos no checkpoint. Após navegação/DI, 13 Home, 5 links e 22 navegação V2/restauração/visitante/carrinho/fila aprovados, sem skips. A captura final teve 17/18 arquivos iguais; a única diferença foi o ripple no botão de acesso rápido, quadro quick0, delimitado em 1142,180–1262,300. Repetido o teste original de animação sem editar fontes/assertivas: aprovado em 37s, oito/oito quadros byte a byte iguais ao baseline. Todas as capturas foram retidas, inclusive a diferença transitória; não há alegação falsa de18/18 na fase posterior. Evidências: `home-visual-equivalence.json`, `home-navigation-final-equivalence.json`, `animation-repeat-equivalence.json` e `captures/`.

**Cinco visuais condicionais finais:** todos executados, zero skip, com APK legado local 1.24/build230102400 e oráculo CX já existente, perfil 1080×2408/450dpi/fontScale1.0. Assertivas/oráculos/máscaras/tolerâncias não foram alterados.

| Caso | Resultado |
|---|---|
| Coordenada inferior do resumo de prêmio |Falhou:880 esperado,960 obtido|
| Primeira linha roxa de compra |Falhou:720 esperado,800 obtido|
| Favorito na linha ponderada nativa |Aprovado|
| Altura multilinha com fonte nativa |Aprovado|
| Altura de linha com baseline nativo |Aprovado|

Três aprovações e duas divergências físicas anteriores preservadas, ambas 80px. Logs/XMLs/comandos atuais em `final-visual/`; o runner retorna exit1 por essas falhas. A primeira tentativa de prêmio antes da correção do manifest não chegou a executar o teste; seu XML antigo está explicitamente marcado stale em `visual-results.json` e não conta como evidência atual. Os dois prompts físicos já reproduzidos antes/depois na rodada MVVM também seguem documentados na seção anterior, sem homologação física integral.

**Encerramento:** debug padrão restaurado sem init/flag Home, applicationId oficial confirmado e API compilada comparada novamente. Somente o AVD temporário SimloFeatureModules20261019/emulator-5560 foi encerrado/removido após restaurar 1280×2856/480dpi/fontScale1.0; Pixel_9_Pro, SimloComposeGates e SimloComposeValidation preservados. `default-debug-artifact.json`, `restored-device-profile.json` e `cleanup.json` registram a conferência. Snapshots, hashes, comandos, XMLs, imagens e patch desta rodada permanecem disponíveis. Sem clean/reset/stage/commit/push/publicação.

**Limites:** refatoração arquitetural e validação global local concluídas; não se declara 100% de homologação da SPEC. Faltam perfil físico equivalente para as coordenadas citadas, conta/aparelho biométrico autorizados para OAuth/biometria/SSO/fila/provedores privados e recursos de assinatura de distribuição, conforme indisponibilidade já confirmada pelo usuário. Não foram inventadas APIs, acessos ou homologações. A central de mensagens conserva a origem demonstrativa e a semântica local do legado expressamente autorizadas.

## Verificação global do host APP — 20/10/2026

Esta é a execução mais recente após corrigir as falhas de validação do host e integrar testes Android reais do adaptador de notificações.

| Fase | Resultado | Evidência |
|---|---|---|
| `modernCheck --offline --continue` | Passou em 14m22s; 2498 tarefas, 210 executadas | `build/verification/app-mvvm-20261019/modern-check.log` |
| `releaseCheck --offline --continue` | Passou em 37s para Automação, Prd e Tqs; 1575 tarefas, 35 executadas | `build/verification/app-mvvm-20261019/release-check.log` |
| Cinco visuais condicionais | 3 aprovados, 2 reprovados, nenhum skip | `build/verification/app-mvvm-20261019/visual-results.json` |
| Restauração padrão | Passou: assemble/test/compile `PrdDebug`, Clean Architecture e formatação | `build/verification/app-mvvm-20261019/global-validation-phases.json` |

O gate `verifyCaixaDS` agora verifica o tema na composição raiz `AppContent`, que é a dona real da árvore Compose; `MainActivity` permanece entry point fino. A checagem `verifyComposeMigration` continua rejeitando manifest XML dentro das features. A permissão de notificações já declarada pelo app protege a chamada de envio, e `PushNotifications.show` mantém sua checagem runtime antes de chamar a API Android; a supressão de lint é restrita a essa função protegida.

Quatro testes instrumentados em `feature:notifications:data` validam a store local (incluída a fachada compatível), mensagens FCM de notificação e de dados, guarda de permissão/canal, inicialização idempotente, refresh na sessão e token rotacionado. O token Firebase real não é solicitado nesses testes. O construtor público de `AndroidPushRuntime` e o comportamento padrão de Firebase permanecem; apenas a factory de token para testes é interna. O JaCoCo Data final cobre 2628/2762 linhas (95,148%), acima do mínimo 95,01%, sem exclusões. Os relatórios Domain, Network e Preferences declaram dependência explícita do teste Android compartilhado e todos passaram.

Resultados visuais completos: o resumo de prêmio esperava y=880 e mediu y=960; a primeira linha de compra esperava y=720 e mediu y=800. Favorito ponderado, altura multilinha e baseline nativo passaram. Ambos os desvios de 80 px são os já documentados e não foram corrigidos alterando layout, imagem de referência, assertiva, máscara ou tolerância. A suite padrão reportou os testes condicionais de coordenada como skips; o runner dedicado executou os cinco e não ignorou nenhum.

O AAR público `androidx.fragment:fragment:1.3.4` ausente no cache offline foi resolvido em uma consulta online aos repositórios configurados; nenhuma versão ou dependência do projeto foi alterada. Os limites de homologação permanecem: o usuário informou que não há conta/aparelho físico autorizados para OAuth, biometria, SSO, fila e provedores privados; o perfil físico equivalente e os materiais de assinatura de distribuição também não estavam disponíveis. Não se declara homologação dessas integrações nem assinatura real. Revisão independente da arquitetura: sem achados acionáveis; seu corte antecede somente os ajustes finais de gate/testes, validados pelos testes direcionados e pela matriz acima.

## Host APP tipado e separação de ownership — 20/10/2026

Execução do plano `docs/superpowers/plans/2026-10-20-app-host-coherence.md` após aprovação do usuário. `AppNavHost` e `LoteriasApp` agora usam `AppFeatureNavigationDependencies` tipado; as 13 implementações/bindings de navegação vivem nos owners das features. `HostFeatureConfiguration` entrega valores de build e ambiente ao grafo de features. A inicialização e coordenação de sessão usam dependências estreitas no host. `AppContainer` não é injetado nem chamado por composição/rotas; continua apenas no inventário de compatibilidade. As 13 dependências de produção `app → feature:*:data` foram removidas; dependências de teste do app seguem permitidas.

| Verificação | Resultado |
|---|---|
| `scripts/test_app_architecture.py` | 29 testes aprovados |
| `scripts/check-app-architecture.py` | APP ownership/MVVM aprovado |
| `scripts/test_feature_architecture.py` | 11 testes aprovados |
| `scripts/check-feature-architecture.py` | Feature ownership/layers aprovado |
| `verifyCleanArchitecture` e `verifyFeatureModules` | Aprovados em `modernCheck` |
| `modernCheck releaseCheck` | Ambos aprovados juntos em 20m42s; 3932 tarefas, 500 executadas, 21 de cache e 3411 atualizadas; três flavors debug/release agregados |

Os testes instrumentados incluídos passaram: Account Data (6), Cart (65), Notifications (11), Retail (40, com 1 caso condicional ignorado) e Tickets (43, com 2 casos condicionais ignorados). Notifications alcançou 95,80% de cobertura de linhas contra mínimo 95,01%, com o gate intacto. O teste novo `PushRuntimeCompatibilityTest` cobre o forwarding do ciclo de vida e das mensagens pela fachada antiga; a caracterização em Support exercita as ações `ContentLoaded`, `SectionsLoaded`, `QuestionsLoaded`, `LoadFailed` e `StartLoading`, mantendo seu mínimo de 95%. A instrumentação completa não declara validação física dos casos condicionais.

A matriz encontrou e corrigiu dois placeholders de OAuth ausentes nos manifests instrumentados: Notifications e Tickets agora usam o mesmo `appAuthRedirectScheme` já adotado em Account/Core Auth. A configuração do runtime OAuth não foi alterada. Nenhum limite de cobertura foi reduzido.

**Limites:** os cinco testes visuais condicionais já têm resultado dedicado na seção MVVM acima: três aprovados e duas coordenadas físicas com diferença de 80 px; esta rodada não repetiu esse runner e não modificou geometria/oráculos. OAuth real, biometria, SSO, fila e provedores privados continuam sem homologação porque a conta autorizada e o dispositivo físico não estão disponíveis, conforme confirmado pelo usuário. A checagem de descritores binários pré/pós também não foi possível: o snapshot preexistente é um inventário de fonte que declara não substituir o dump compilado. Os FQNs compatíveis inventariados e os módulos realocados compilaram, mas isto não é uma prova byte a byte da ABI antiga. Não houve alteração de credenciais, tráfego financeiro real, stage, commit, push ou publicação.

### Revisão contra Android modularization e Now in Android — 20/10/2026

Comparação feita com a [orientação oficial de modularização Android](https://developer.android.com/topic/modularization/patterns) e a [estratégia do Now in Android](https://github.com/android/nowinandroid/blob/main/docs/ModularizationLearningJourney.md). Ambos descrevem `app` como entry point que agrega features e normalmente provê navegação raiz; a orientação oficial ressalta que não existe uma única topologia e recomenda coesão, baixo acoplamento e fronteiras explícitas. O desenho é compatível com a estrutura pedida neste projeto.

Essa revisão encontrou dois providers de domínio ainda ativos em `app`: a configuração remota de aplicação e o gateway/política OAuth. A configuração e seus use cases foram movidos para `core:data`; Account passou a prover OAuth e a política de autorização. `AppConfigurationBindings` e `AppSessionBindings` permanecem como fachadas antigas sem `@Provides`/`@InstallIn`, registradas no inventário. O gate agora rejeita providers de implementações de Core Data e do gateway de autenticação no DI do app.

Verificação desta correção incremental: `scripts/test_app_architecture.py` (31 testes), `scripts/check-app-architecture.py`, `scripts/check-feature-architecture.py`, `:core:data:compileKotlin`, `:app:compileTqsDebugKotlin`, `:feature:account:compileDebugKotlin`, `:app:testTqsDebugUnitTest`, `verifyCleanArchitecture` e `verifyFeatureModules` passaram. Esta execução não repetiu a matriz `modernCheck releaseCheck`; os resultados da matriz anterior permanecem acima e não cobrem esses últimos ajustes.

### Remoção das fachadas inativas do host — 20/10/2026

A pedido do usuário, `AppContainer` e `ApplicationModule` foram removidos com suas entradas em `config/app-compatibility.json`. A decisão substitui, somente para essas duas classes, a preservação dos contratos públicos registrada na rodada anterior. Não há referências a elas em fontes de produção ou testes Kotlin de app/CORE/features. Getters de ambiente/segurança e o método de troca de ambiente do runtime que só atendiam à fachada também foram removidos; o caso de uso ativo de troca de ambiente permanece no binding existente.

O gate agora rejeita a restauração dessas fachadas mesmo se inventariadas como compatibilidade. A fixture real de provider duplicado usa `NetworkModule`, uma fachada que continua existente. Documentação atual usa `AppFeatureGraphDependencies`, `AppHostRuntime` e `AppInitializationBindings` como donos efetivos.

Validação final desta limpeza: 38 testes de política APP e 11 de features aprovados; `verifyCleanArchitecture`, `verifyFeatureModules`, `verifyComposeMigration`, `verifyKotlinFormatting`, `:app:compileTqsDebugKotlin`, `:app:compileTqsReleaseKotlin` e `:app:testTqsDebugUnitTest` aprovados juntos em 51 s (1.121 tarefas). O relatório do app registra 203 testes, zero falhas/erros/ignorados. Log: `/tmp/app-facade-cleanup-final-20261020.log`. A fixture real de provider duplicado em `NetworkModule` foi rejeitada pelo Gradle com o diagnóstico esperado e restaurada byte a byte; log: `/tmp/app-facade-duplicate-provider-20261020.log`. Esta limpeza não repetiu a matriz global `modernCheck releaseCheck` nem homologação física/privada.

## Limpeza da camada CORE — 20/10/2026

A pedido do usuário, a limpeza das superfícies antigas do APP foi aplicada a CORE. Foram removidas as fachadas nos pacotes históricos do app para seleção de ambiente, criação/atualização de clientes, store de chaves assinadas e adaptadores de autorização. Os modelos de ambiente, estado de background e seção de apresentação foram realocados para os pacotes dos módulos CORE que já os compilavam. As nove entradas de CORE em `config/host-public-compatibility.json` foram retiradas; seus contratos JVM históricos deixam de ser preservados nesta decisão.

Os consumidores usam diretamente `core.domain.environment`, `core.network.config`, `core.auth.foreground`, `core.security` e `core.presentation`. Os quatro aliases de identidade/encerramento de sessão de `core.auth` foram removidos e seus consumidores importam os tipos efetivos de Account. Os aliases sem consumidores `CaixaTheme` e `CaixaColors` foram removidos, mantendo `CaixaDSTheme` e `CaixaDS.colors`. O fake de sessão compartilhado agora pertence a `core.testing`. Os arquivos de implementação JWT e do datasource de configuração foram nomeados conforme seus tipos, sem alterar seus comportamentos. `core:preferences` foi retirado das dependências de produção do app; permanece como verificação de cobertura de persistência, com os mesmos produtores e limiares.

As mudanças não alteram URLs, payloads, chaves de persistência, regras de autorização/sessão, ordem de rotação dos clientes/pins, callbacks ou escopos Hilt. Contratos de transporte ativos e as fachadas das features fora deste escopo permanecem. O teste de ABI do host conserva a assertiva da fachada push remanescente; as assertivas dos contratos CORE intencionalmente retirados foram removidas.

| Verificação desta rodada | Resultado |
|---|---|
| Política de arquitetura do app/CORE | 41 testes aprovados; os três casos novos falharam antes da implementação das regras |
| Política/ownership das features | 11 testes aprovados e gate positivo aprovado |
| Fixtures reais de CORE no Gradle | Pacote do app, pacote de outro módulo CORE e API obsoleta rejeitados; arquivos restaurados byte a byte |
| `verifyCleanArchitecture`, `verifyFeatureModules`, `verifyComposeMigration`, `verifyKotlinFormatting` | Aprovados na árvore final restaurada |
| Testes unitários dos módulos CORE | 498 testes, zero falhas/erros/ignorados |
| Testes unitários do app TQS, Account e Account Data | 318 testes, zero falhas/erros/ignorados |
| Compilação do host TQS | Debug e Release aprovados, incluindo geração Hilt/KSP |
| Compilação dos testes Android alterados | App TQS, Account e Notifications aprovados |

A execução final Gradle concluiu em 1min06s: 1.179 tarefas, 277 executadas, quatro de cache e 898 atualizadas. Os 816 testes unitários incluem todos os módulos CORE com suites JVM e os consumidores mencionados; tarefas sem fontes de teste não são contadas como testes executados. Logs: `/tmp/core-cleanup-final-20261020.log`, `/tmp/core-cleanup-negative-gates-20261020.log`; contagem dos relatórios: `/tmp/core-cleanup-test-summary-20261020.json`. Nenhuma fonte de produção CORE permanece no pacote `apostas`, com aliases `typealias` ou declaração `@Deprecated`.

Esta rodada não executou novamente `modernCheck releaseCheck`, testes instrumentados em dispositivo/emulador ou homologação física/privada. A compilação dos testes Android confirma seus tipos e geração, não sua execução. Não houve redução de limiares, commit, push ou publicação; alterações anteriores do checkout foram preservadas.

## Limpeza de todas as features — 20/10/2026

Solicitação do usuário: aplicar às 14 features a mesma retirada de compatibilidade redundante já concluída no APP e em CORE. Foram removidos 395 aliases públicos e 97 wrappers de casos de uso/acesso remoto, além de três propriedades que republicavam políticas, uma extensão de campanha e uma entrada antiga de Home sem consumidores; seus consumidores usam os tipos efetivos e as implementações existentes. As 23 entradas de features no inventário de pacotes históricos do app foram aposentadas. A decisão retira intencionalmente esses contratos antigos; os contratos de transporte ativos e suas classes/payloads continuam preservados.

| Feature revisada | Aliases públicos retirados | Wrappers de casos de uso/acesso remoto retirados |
|---|---:|---:|
| `account` | 67 | 10 |
| `betting` | 78 | 18 |
| `cart` | 77 | 17 |
| `home` | 2 | 1 |
| `marketplace` | 24 | 2 |
| `marketplace-filter` | 0 | 0 |
| `notifications` | 12 | 6 |
| `onboarding` | 5 | 3 |
| `payment` | 31 | 8 |
| `responsible-gaming` | 15 | 4 |
| `results` | 18 | 3 |
| `retail` | 30 | 9 |
| `support` | 10 | 4 |
| `tickets` | 26 | 12 |

Os módulos instalados `AppBettingBindings`, `AppTicketsBindings` e `AppPushBindings`, além do adaptador ativo `PushRuntime`, foram realocados para `di` da própria feature. As fachadas duplicadas de DI, factory de Notifications, store push e módulos antigos foram removidos. O transporte de cadastro push foi realocado para `Account Data`, mantendo sua porta compartilhada em CORE. Os callbacks de lifecycle são responsabilidade de `AndroidPushRuntime`; o adaptador de DI recebe configuração do host e expõe os pontos usados por Application/FCM. Testes que só afirmavam os campos binários ou encaminhamentos das fachadas retiradas foram removidos com elas; os testes da implementação de push em Notifications Data permanecem.

A comparação dos literais dos 656 fontes Kotlin mantidos que receberam alterações não identificou mudanças. As regras, valores de configuração, rotas, URLs, payloads, chaves de persistência, StateFlows e escopos existentes foram preservados. As classes de fachada deixam de existir; referências concretas a elas nos consumidores passam a usar suas implementações atuais. Os originais anteriores a esta rodada foram guardados em `/tmp/features-cleanup-before-20261020` para revisão, preservando o checkout previamente alterado.

| Verificação direcionada | Resultado |
|---|---|
| Política APP/CORE | 41 testes aprovados |
| Política de features | 19 testes aprovados; sete casos negativos novos falharam antes da implementação das regras |
| Política de módulos | 5 testes aprovados |
| Gate Gradle com violações reais | Sete casos rejeitados individualmente: Android em Domain, alias público, API obsoleta, namespace antigo mesmo inventariado, wrapper do caso de uso existente, propriedade que republica um objeto e namespace antigo de host de apresentação; fontes/inventário restaurados byte a byte |
| Host TQS | Debug/Release e testes unitários aprovados com os gates de arquitetura, composição e formatação |

A primeira compilação detectou uma substituição de imports que tratava nomes de implementação como prefixos de interfaces. A regra foi corrigida para casar nomes completos, com verificações de preservação do prefixo; a compilação repetida passou em 1min24s (1.101 tarefas). Logs: `/tmp/features-cleanup-compile-fixed-20261020.log` e `/tmp/features-cleanup-negative-gates-20261020.log`.

A revisão independente identificou e ajudou a retirar as três propriedades de políticas e a extensão de campanha; a revisão adicional confirmou a correção do import implícito de campanha do drawer. A entrada ativa `presentation.screen.HomeScreen` conserva a composição existente; a fachada antiga em `presentation.host.route` não tinha consumidores e foi retirada. A revisão encerrou sem achados pendentes.

A primeira matriz global foi interrompida de forma controlada antes dessas remoções adicionais; o daemon confirmou o cancelamento e ficou ocioso antes das edições. Matriz final: `ANDROID_SERIAL=emulator-5560 ./gradlew modernCheck releaseCheck --offline --continue --console=plain`, após a restauração das sete fixtures negativas. Log dessa execução: `/tmp/features-cleanup-global-final-20261020.log`. Essa execução terminou em 31min12s com uma única reprovação, o limiar de Notifications descrito abaixo; `releaseCheck` e os três releases foram aprovados. Não houve commit/push nem redução de limiares.

A matriz global encontrou cobertura de Notifications em 93,96% (592/630 linhas), abaixo dos 95,01% exigidos. A retirada do smoke da fachada antiga deixou sem cobertura o adaptador ativo de DI. A correção mantém o limiar: teste instrumentado da configuração mock e da permissão/payload de notificação no `feature.notifications.di.PushRuntime`, com sessão sintética autenticada e backend local observável. Os dois cenários instrumentados foram acrescentados em `PushRuntimeIntegrationTest`; a revisão independente não encontrou achados pendentes. A permissão positiva pertence ao APK temporário de testes da biblioteca; o cenário negado usa contexto explícito. A repetição da matriz completa foi aprovada; log `/tmp/features-cleanup-global-verified-20261020.log`.


| Validação final das 14 features | Resultado |
|---|---|
| `modernCheck releaseCheck` | Aprovados juntos em 11min55s; 3.905 tarefas, 178 executadas e 3.727 atualizadas |
| Arquitetura, ownership, composição e formatação | Aprovados na árvore final |
| Unitários | Relatórios de 35 tarefas: 2.088 testes, zero falhas/erros/ignorados; inclui 202 testes do app em cada flavor PRD, TQS e Automação |
| Instrumentados | Relatórios de nove tarefas: 199 testes, zero falhas/erros, três ignorados por condições explícitas das suítes (um Retail e dois Tickets) |
| Notifications | 12 testes instrumentados aprovados, incluindo os dois novos cenários do adaptador; cobertura de linhas 95,87% (604/630), acima do limiar inalterado de 95,01% |
| Lint e demais gates de cobertura | Aprovados, sem exclusões nem redução dos limiares |
| Release PRD, TQS e Automação | Geração e otimização R8 aprovadas na rodada anterior e artefatos verificados como atualizados na matriz final |
| Revisão independente | Sem achados pendentes, incluindo revisão do teste de push |

Evidências consolidadas em `build/verification/features-cleanup-20261020/`: manifesto das remoções/alterações e auditoria de literais, mapeamento para símbolos efetivos, logs das sete fixtures negativas restauradas, revisão, log global final e `test-summary.json`. A limpeza foi validada no emulador existente `emulator-5560`; os três casos condicionais não são apresentados como executados. Homologação privada de OAuth, pagamentos, biometria e dispositivos físicos permanece fora desta execução. Não houve commit, push, publicação ou alteração dos limiares; o checkout previamente alterado foi preservado.


## Revisão final transversal de APP, CORE e features — 20/10/2026

Revisão solicitada após as três limpezas anteriores, com inventário de fontes de todos os donos e passes independentes de APP/CORE, Domain/Data/API/Transport/DI e apresentação/navegação das 14 features. A revisão final usa o patch desta rodada contra o snapshot indicado em `/tmp/final-coherence-snapshot-path-20261020`; não confunde alterações anteriores do checkout com esta tarefa.

Foram retiradas dez fachadas APP, os overloads de navegação antigos, a subclasse pública do registro de grafos, o wrapper de política de resgate, dois casos de uso de ambiente sem consumidor runtime, Mapper e helpers visuais sem chamadas. Os consumidores usam AppNavHost, os módulos Hilt efetivos, ProcessEnvironmentSettings injetável/singleton e o adapter Firebase de Core Security com os mesmos quatro BuildConfig. As três entradas Android do inventário APP permanecem porque têm responsabilidade real. Contratos HTTP/DTOs, adapters de plataforma e sessão, callbacks e escopos ativos foram preservados.

O teste de falha de commit foi transferido de Account Data para o dono efetivo Datastore, verificando que uma atualização em memória de commit(false) não confirma durabilidade e que uma nova tentativa grava em disco. O teste exclusivo do wrapper PersistEnvironmentChange foi aposentado; EnvironmentSelectionTest e HostViewModelsTest continuam cobrindo ordem/retentativa do fluxo real. A limpeza de dependências foi guiada pelos imports e assinaturas reais; os 52 módulos que passavam por três scripts de encaminhamento agora aplicam diretamente os mesmos plugins. Gson/coroutines não são mais impostos pelo plugin Data a módulos sem uso dessas bibliotecas.

A revisão independente detectou consumidores por import alias de CaixaDSMarketplaceFilterInput/BetGrid. A compilação em andamento foi cancelada pelo cliente (exit 130), e o daemon confirmou IDLE antes da edição; ambas as declarações e seus imports foram restaurados byte a byte. As buscas seguintes consideram aliases. O achado foi retirado após confirmação da restauração. Também foram corrigidas três descrições obsoletas de DI/KDoc.

A compilação integrada após a restauração passou em 2min41s; os warnings de nulidade/tipo redundantes motivaram ajustes equivalentes e dois usos de Locale passaram ao construtor equivalente por linguagem pt-BR. A verificação integrada repetida na árvore final passou em 1min17s (1.140 tarefas: 128 executadas e 1.012 atualizadas), com arquitetura, módulos, composição, formatação, TQS Debug/Release, compilação dos testes Android PRD, unitários TQS e Datastore. Logs: `/tmp/final-coherence-targeted-fixed-20261020.log` e `/tmp/final-coherence-targeted-verified-20261020.log`.

Políticas Python: 44 APP/CORE + 19 features + 5 módulos = 68 testes aprovados. Os novos casos falharam primeiro contra o guard anterior (11 falhas de cenários/subtests), depois passaram. Quatro violações reais Gradle foram rejeitadas individualmente: provider de compatibilidade duplicado, fachada de DI aposentada com arquivo renomeado, função raiz aposentada e tentativa de autorizar nova fachada pelo inventário. Fontes e inventário foram restaurados byte a byte; gates positivos passaram após a restauração. Evidências em `build/verification/final-coherence-20261020/negative-host`.

Revisão independente final encerrada sem Critical, Important ou Minor pendentes, incluindo os ajustes de fluxo não nulo e a distribuição das bibliotecas opcionais nos módulos. Instrumentação do host e matriz global final concluídas; resultados abaixo. Sem redução de limiares, commit, push ou publicação.


O teste instrumentado adicional do host não executou na primeira tentativa: INSTALL_FAILED_UPDATE_INCOMPATIBLE impediu a instalação sobre o pacote com outra assinatura (zero casos executados). A mesma classe FilaBraQueueCoordinationTest passou em APK temporário com applicationId separado, definido somente por init script fora das fontes: um teste, zero falhas/erros/ignorados, build em 16s. O pacote temporário já não aparece no inventário consultado após a execução. Contudo, a checagem posterior mostrou que o pacote nativo de referência deixou de estar instalado após a tentativa padrão de instrumentação. O app anterior não deve ser descrito como preservado por essa tentativa. A matriz global usa novamente os identificadores normais, sem esse init script.

A primeira tentativa global desta rodada foi cancelada ao detectar erro de resolução offline de `com.google.errorprone:error_prone_annotations:2.26.0`, dependência transitiva do Firebase agora resolvida no módulo Notifications Data sem o antigo trânsito de Network. A reprodução isolada confirmou ausência no cache, não erro de fonte. As duas tarefas de metadados passaram em 38s após resolução online pelos repositórios configurados, sem alterar código ou versões declaradas. A matriz final foi reiniciada em modo offline com o cache completo; log `/tmp/final-coherence-global-verified-20261020.log`.


Incidente da instrumentação do host: a tentativa padrão deveria ter sido isolada desde o início. Após o conflito de assinatura e a finalização do runner, o pacote nativo de referência estava ausente; isso levou a dois skips adicionais em Tickets (comparação com fontes/tema do app nativo). A cópia oficial previamente arquivada `build/verification/installed-reference-20261005/legacy-installed.apk` foi conferida e reinstalada: pacote `br.gov.caixa.loterias.apostas`, versão 1.24.0/230102400, SHA-256 `2e8ef3bd96c3db48a4391695429bf8531ccae72a821e3683a742bc61eb90dc3e`. A instalação foi confirmada pelo Package Manager. A preservação dos dados locais anteriores desse pacote não pôde ser verificada; não se afirma recuperação desses dados. Os dois casos de comparação nativa foram repetidos separadamente após o término da matriz global: ambos passaram, zero falhas/erros/ignorados, build em 15s. Os skips por perfil físico de tela continuam explicitamente separados.


| Validação final transversal | Resultado |
|---|---|
| `modernCheck releaseCheck` | Aprovados juntos em 46min46s; 3.908 tarefas: 1.758 executadas, 312 de cache e 1.838 atualizadas |
| Arquitetura, módulos, composição, formatação e lint | Aprovados na árvore final; 68 testes das políticas Python também aprovados |
| Unitários | 2.087 testes em 36 tarefas, zero falhas/erros/ignorados; inclui 202 testes do app em cada flavor PRD, TQS e Automação |
| Instrumentados da matriz | 199 casos em nove tarefas: 194 executados com sucesso, zero falhas/erros e cinco ignorados |
| Complementos instrumentados | Um caso do host isolado e os dois casos de comparação nativa anteriormente ignorados passaram, zero falhas/erros/ignorados |
| Limite de dispositivo | Restam três casos condicionais sem execução: um Retail e dois Tickets dependentes de outro perfil de tela/densidade |
| Cobertura | Todos os gates aprovados; Core Data: 2.554/2.686 linhas (95,09%); Notifications: 604/630 (95,87%); limiar de ambos preservado em 95,01% |
| Release PRD, TQS e Automação | Assemblies e otimização R8 aprovadas |
| Revisão independente | Sem achados arquiteturais ou redundâncias demonstradas pendentes no escopo revisado |

Os totais brutos da matriz foram arquivados antes da execução complementar para preservar a distinção entre os cinco skips originais e os dois casos posteriormente executados. São 197 execuções instrumentadas bem-sucedidas considerando matriz e complementos, com três casos condicionais restantes; não se afirma execução desses três casos. Os 378 relatórios XML da matriz estão em `build/verification/final-coherence-20261020/global-test-results/`; os relatórios complementares, logs, inventários e resumo estão na mesma pasta de evidências. O arquivo `review.patch` isola esta rodada contra o snapshot anterior, sem atribuir a esta tarefa o restante do checkout alterado.

A revisão e limpeza solicitadas estão concluídas dentro desse escopo verificado. Isso não equivale a uma garantia absoluta de ausência de redundância nem à homologação privada de OAuth, pagamentos, biometria ou dispositivos físicos. A reinstalação do APK nativo de referência não comprova preservação dos seus dados locais anteriores. Não houve redução de limiares, commit, push ou publicação.


## Remoção de core:database — 20/10/2026

Removidos o módulo `core:database` e seu objeto `AppDatabase`, que apenas encaminhava chamadas Android. As seis chamadas em cinco arquivos de Account Data e Cart Data usam diretamente `SQLiteDatabase.openDatabase(path, null, flags[, errorHandler])` ou `context.openOrCreateDatabase(name, mode, null)`. A auditoria contra o snapshot desta rodada confirmou que os consumidores mudaram somente pela expansão exata desses encaminhamentos e remoção do import. Flags, handlers de corrupção, nome/caminho do banco, SQL, schema, transações e blocos `use` foram preservados.

O registro em settings, a tarefa dependente de modernCheck, duas dependências Data e a exportação em Preferences foram removidos. O agregado de cobertura deixou de incluir a classe eliminada; o limiar de 95,01% permanece. Os testes existentes de armazenamento usam bancos isolados e continuam verificando persistência entre recriações, ausência de criação em leituras, conservação de tabelas não relacionadas, rollback e preservação de arquivos corrompidos; nenhuma expectativa foi enfraquecida ou teste removido. A documentação atual registra 67 módulos.

Validação: `ANDROID_SERIAL=emulator-5560 ./gradlew verifyCleanArchitecture verifyFeatureModules verifyComposeMigration verifyKotlinFormatting :app:compileTqsDebugKotlin :app:compileTqsReleaseKotlin :feature:account:data:testDebugUnitTest :feature:cart:data:testDebugUnitTest :feature:account:data:connectedDebugAndroidTest :feature:cart:data:connectedDebugAndroidTest :core:preferences:jacocoDebugCoverageVerification --offline --continue --console=plain`.

Resultado: BUILD SUCCESSFUL em 1min29s, 1.304 tarefas (180 executadas, 1.124 atualizadas). Account Data: 41 unitários e seis instrumentados; Cart Data: 40 unitários e 22 instrumentados, todos sem falhas/erros/ignorados. O agregado executou também quatro testes instrumentados de Notifications Data. Cobertura de persistência: 527/533 linhas, 98,87%, gate aprovado. A matriz global modernCheck/releaseCheck não foi repetida nesta rodada; a compilação direcionada e o gate de cobertura executaram suas dependências necessárias. Não houve commit/push nem manipulação do pacote nativo de referência.

Evidências e snapshot em `build/verification/remove-core-database-20261020/manifest.json`, patch isolado em `review.patch`, log em `verification.log` e XML dos testes afetados em `test-results/`. Alterações anteriores do checkout foram preservadas.


## Auditoria e retirada de módulos CORE redundantes — 20/10/2026

Revisados os 14 módulos CORE que restavam após a retirada de Database. Removidos `core:datastore` e `core:preferences`; restam 12 módulos CORE/65 módulos no projeto. O primeiro continha um encaminhamento Android e a persistência de ambiente exclusiva do host; os cinco adaptadores Data usam agora `getSharedPreferences` diretamente, enquanto implementação e teste de `EnvironmentPreferences` pertencem ao APP. Corpos do teste e da implementação foram preservados, com alteração de pacote e visibilidade interna de produção. Chaves, arquivo, modos, contexto e commit/apply não mudaram.

O segundo módulo estava sem fontes. Seu gate foi preservado como tarefa raiz `verifyPersistenceCoverage`, em `gradle/persistence-coverage.gradle`, exigida por `modernCheck`. Permanecem todos os seletores de classes ativos, os inputs de execução dos módulos reais, as suítes JVM/instrumentadas e o limiar de 95,01%; saíram somente o helper removido e as referências a tarefas/exec sem fontes do módulo eliminado. A dependência do agregado Domain no antigo teste sem fontes também saiu. Nenhum teste comportamental foi excluído ou expectativa enfraquecida.

Comando: `ANDROID_SERIAL=emulator-5560 ./gradlew verifyCleanArchitecture verifyFeatureModules verifyComposeMigration verifyKotlinFormatting :app:compileTqsDebugKotlin :app:compileTqsReleaseKotlin :app:testPrdDebugUnitTest :app:testTqsDebugUnitTest :app:testAutomacaoDebugUnitTest verifyPersistenceCoverage :core:domain:jacocoTestCoverageVerification --offline --continue --console=plain`.

BUILD SUCCESSFUL em 1min34s: 1.292 tarefas, 235 executadas, cinco de cache e 1.052 atualizadas. Relatórios: 1.024 unitários, zero falhas/erros/ignorados; inclui 203 testes do APP em cada flavor e o teste de commit falho/retentativa nos três. Instrumentados: seis de Account Data, 22 de Cart Data e quatro de Notifications Data, total 32, zero falhas/erros/ignorados. Cobertura de persistência: 526/532 linhas (98,87%); Domain: 1.763/1.823 (96,71%); gates aprovados. Revisão independente: nenhum Critical, Important ou Minor pendente.

`./gradlew modernCheck releaseCheck --dry-run --offline --console=plain` passou em 3s, confirmando que o grafo completo resolve e inclui `verifyPersistenceCoverage`, sem módulos retirados. Esse dry-run não equivale à execução completa da matriz ou à geração dos releases; a matriz global não foi repetida nesta rodada. A compilação Debug/Release direcionada e os testes/gates acima foram executados. Inventário e justificativas dos módulos mantidos: `docs/CORE_MODULE_AUDIT.md`. Snapshot, patch isolado, auditoria de equivalência, revisão, relatórios e logs: `build/verification/core-module-facades-20261020/`. Alterações anteriores do checkout foram preservadas; sem commit/push e sem manipular o pacote nativo de referência.


## Matriz completa após remoção de Database, Datastore e Preferences — 20/10/2026

Solicitação explícita do usuário para executar a matriz global após as remoções, corrigindo eventuais falhas sem reduzir os limiares. Comando executado na árvore final: `ANDROID_SERIAL=emulator-5560 ./gradlew modernCheck releaseCheck --offline --continue --console=plain`.

**Resultado: BUILD SUCCESSFUL em 20min46s**, exit 0. Foram 3.674 tarefas: 551 executadas, 11 de cache e 3.112 atualizadas. `modernCheck`, `releaseCheck` e os assemblies PRD, TQS e Automação concluíram; os três releases passaram por R8 e seus APKs foram verificados e registrados com tamanho/hash no manifesto. Nenhuma falha exigiu correção.

| Verificação | Resultado |
|---|---|
| Arquitetura, ownership, composição, formatação, lint e demais gates de modernCheck | Aprovados |
| Unitários | 2.089 testes em 35 tarefas; zero falhas/erros/ignorados; 203 testes do APP em cada flavor |
| Instrumentados | 199 casos em nove tarefas; 196 execuções aprovadas, zero falhas/erros e três ignorados por perfil de tela |
| Persistência, tarefa raiz `verifyPersistenceCoverage` | 526/532 linhas, 98,87%; limiar 95,01% preservado |
| Core Data | 2.554/2.686 linhas, 95,09%; limiar 95,01% preservado |
| Core Domain | 1.763/1.823 linhas, 96,71%; limiar 95,01% preservado |
| Notifications | 604/630 linhas, 95,87%; limiar 95,01% preservado |
| Releases PRD, TQS e Automação | Gerados com sucesso, incluindo otimização R8 |
| Integridade da execução | 2.242 hashes de fontes/configurações e 23 configurações de cobertura conferidos sem alterações durante a matriz |

Os três casos não executados continuam explícitos: Retail `installedPrizeSummaryRetainsTheProductionBottomCoordinate`; Tickets `installedPhysicalPairStartsFirstPurpleRowAtRecordedScreenCoordinate` e `installedPhysicalPairKeepsFavoriteAtNativeWeightedRowPosition`. Suas condições de perfil de dispositivo não foram alteradas e esses casos não são apresentados como aprovados. Os demais casos instrumentados, inclusive as duas comparações de fonte/altura com o APK nativo de referência, executaram sem falhas.

Evidências em `build/verification/core-removals-full-matrix-20261020/`: `manifest.json`, `verification.log`, `test-summary.json`, `coverage-summary.json`, 380 XML de testes arquivados, 20 relatórios XML de cobertura e snapshots/hashes dos inputs. A matriz usou o emulador existente, sem manipular o pacote nativo de referência, sem alterar os limiares e sem commit, push ou publicação.


## Auditoria dos módulos de todas as features — 20/10/2026

Revisados individualmente os 52 módulos das 14 features: 14 de apresentação, 13 Domain, 13 Data, três API e nove Transport. As revisões independentes de Domain/API e Data/Transport e a revisão principal de apresentação não identificaram módulos inteiros que sejam somente fachadas redundantes. Todos possuem consumidores ativos e comportamento, contratos ou tipos próprios. Não houve remoção de módulos nem alteração de produção, testes, dependências ou limiares.

Foram conferidos especialmente os candidatos menores: Home Data mantém arquivo/chave/apply; Home Domain mantém ordenação e alteração conjunta de preferências; Onboarding conserva chaves/defaults/tutoriais; Account API valida a identidade referencial; Cart/Marketplace API evitam ciclos e compartilham modelos/regras. Support Domain mantém contratos reais usados por Data e apresentação; Payment/Retail Transport preservam envelopes distintos. Projeções de API internas não justificam apagar seus módulos Data, que também possuem mapeamentos, persistência e políticas de erro/sessão.

Verificação fresca: 68 testes Python das políticas aprovados; `./gradlew verifyCleanArchitecture verifyFeatureModules verifyComposeMigration verifyKotlinFormatting --offline --continue --console=plain` passou em 48s (76 tarefas: 72 executadas e quatro atualizadas). Comparação dos 2.242 hashes de fontes/configurações com a matriz completa anterior: zero adições, remoções ou alterações. As 23 configurações de cobertura estão byte a byte inalteradas. A matriz completa anterior, registrada acima, permanece referente à mesma árvore de código/configuração (2.089 unitários, 199 casos instrumentados com três skips por perfil de tela, releases dos três flavors aprovados); não foi repetida nesta revisão sem mudança de código.

Relatório com os 52 módulos, responsabilidades e consumidores: `docs/FEATURE_MODULE_AUDIT.md`. Evidências em `build/verification/feature-module-facades-20261020/`: inventário, decisões, revisões independentes, logs, manifesto e auditoria de hashes. Nenhum teste ou input de cobertura foi excluído. Sem commit/push/publicação ou alteração no pacote nativo instalado.


## Revisão de fachadas e encaminhamentos do APP — 20/10/2026

A revisão dos fontes main/debug do APP identificou três arquivos redundantes: `composition/startup/FilaBraQueueScreen.kt` (um composable que só chamava FilaBraQueueWebView de CORE), `navigation/route/PrizeRedirectRouteMapper.kt` (duas funções que só chamavam serviceRedirectRoute de CORE) e `di/AppCoreDataCompositionModule.kt` (somente includes de um módulo Data). Os dois primeiros foram removidos com consumidores migrados diretamente para CORE; o includes do terceiro foi incorporado ao módulo AppInitializationBindings já instalado no mesmo SingletonComponent, sem alterar providers ou escopos. As duas propriedades de AppSessionDependencies que republicavam getters de Account foram retiradas, com acesso direto ao mesmo account/Flow nos consumidores.

Os testes existentes de redirecionamento e recomposição da fila foram migrados sem alterar expectativas ou remover casos. A auditoria de equivalência confirmou os corpos dos providers, substituições dos getters e expectativas dos testes. O inventário atual de main passa de 32 para 29 fontes Kotlin; as duas entradas debug e as entradas de manifesto foram preservadas. O texto antigo de contagem da arquitetura foi corrigido para o inventário efetivo. Factories/estado/lifecycle, políticas de startup/foreground, agregação de DI e adapters Android com responsabilidade real foram mantidos.

O guard de arquitetura impede reintroduzir o módulo e as três funções aposentadas mesmo com outro nome de arquivo. Quatro cenários/subtests falharam antes da regra; depois passaram com os demais: 45 testes APP/CORE + 19 features + cinco módulos = 69 políticas aprovadas.

| Verificação desta rodada | Resultado |
|---|---|
| Arquitetura, módulos, composição e formatação | Aprovados |
| TQS Debug/Release e compilação dos testes Android PRD | Aprovadas, incluindo geração Hilt/KSP |
| APP unitário PRD, TQS e Automação | 203 casos por flavor, total 609; zero falhas/erros/ignorados |
| Unitários das tarefas únicas acionadas pelos comandos direcionado e de cobertura | 1.144 casos em 15 tarefas (inclui os 609 do APP); zero falhas/erros/ignorados |
| Instrumentação de persistência | Account Data: seis; Cart Data: 22; Notifications Data: quatro; todos aprovados |
| Instrumentação do host | FilaBraQueueCoordinationTest: um caso aprovado, verificando um único broadcast/finish após recomposição; zero falhas/erros/ignorados |
| Cobertura de persistência | 526/532 linhas, 98,87% |
| Cobertura de Core Data | 2.554/2.686 linhas, 95,09% |
| Cobertura de Core Domain | 1.763/1.823 linhas, 96,71% |
| Cobertura de Core Network | 669/675 linhas, 99,11% |
| Configurações de cobertura | 23 arquivos byte a byte inalterados; os quatro gates acima passaram com mínimo de 95,01% |
| Revisão independente | Nenhum Critical, Important ou Minor pendente |

Comando direcionado: `./gradlew verifyCleanArchitecture verifyFeatureModules verifyComposeMigration verifyKotlinFormatting :app:compileTqsDebugKotlin :app:compileTqsReleaseKotlin :app:compilePrdDebugAndroidTestKotlin :app:testPrdDebugUnitTest :app:testTqsDebugUnitTest :app:testAutomacaoDebugUnitTest --offline --continue --console=plain`; BUILD SUCCESSFUL em 1min08s, 1.128 tarefas (115 executadas, cinco de cache, 1.008 atualizadas).

Cobertura: `ANDROID_SERIAL=emulator-5560 ./gradlew verifyPersistenceCoverage :core:domain:jacocoTestCoverageVerification :core:data:jacocoTestCoverageVerification :core:network:jacocoTestCoverageVerification --offline --continue --console=plain`; BUILD SUCCESSFUL em 28s, 824 tarefas (14 executadas, 810 atualizadas).

O teste do host foi executado somente com applicationId separado, por init script temporário fora dos fontes: `ANDROID_SERIAL=emulator-5560 ./gradlew -I /tmp/app-facade-review-isolated-host-20261020.gradle :app:connectedPrdDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=br.gov.caixa.loterias.apostas.FilaBraQueueCoordinationTest --offline --continue --console=plain`; BUILD SUCCESSFUL em 25s, 818 tarefas (31 executadas, 787 atualizadas). Pacote temporário `br.gov.caixa.loterias.apostas.appforwarders20261020`; ele não aparece instalado após o runner. O pacote nativo de referência continuou instalado, com versão e registros de instalação/atualização idênticos antes/depois. Nenhuma alegação adicional sobre recuperação dos dados anteriores do incidente histórico é feita.

Foram arquivados apenas os XML correspondentes às tarefas/flavor desta rodada, sem contar o XML TQS de uma execução instrumentada anterior. Os 33 instrumentados são os 32 de persistência e um do host. A matriz completa modernCheck/releaseCheck não foi repetida após este patch; não se reutiliza o resultado anterior como prova da árvore alterada. Foram executadas as compilações, testes e gates direcionados acima. Não houve alteração de limiares, exclusão de testes, commit/push/publicação ou manipulação do pacote nativo instalado.

Evidências em `build/verification/app-facade-review-20261020/`: snapshot e patch isolado desta rodada, logs RED/GREEN, revisão, auditoria de equivalência, relatórios unitários/instrumentados/JaCoCo, init script isolado, configurações de cobertura originais e metadados do pacote de referência. Alterações anteriores do checkout foram preservadas.


## Matriz completa após a limpeza de encaminhamentos do APP — 20/10/2026

Solicitação explícita do usuário para executar a matriz completa após retirar as fachadas/encaminhamentos do APP e corrigir eventuais falhas sem reduzir os limiares. Comando: `ANDROID_SERIAL=emulator-5560 ./gradlew modernCheck releaseCheck --offline --continue --console=plain`.

**BUILD SUCCESSFUL em 15min11s**, exit 0. Foram 3.674 tarefas: 244 executadas, 15 de cache e 3.415 atualizadas. `modernCheck`, `releaseCheck` e os assemblies PRD/TQS/Automação passaram; os três releases executaram R8 e seus APKs foram verificados com tamanho/hash e applicationId correspondente a cada flavor (PRD sem sufixo, TQS/Automação com `.hmp`). Nenhuma falha exigiu correção de código, testes ou build.

| Verificação | Resultado |
|---|---|
| Arquitetura, ownership, composição, formatação, lint e demais gates da matriz | Aprovados |
| Unitários | 2.089 casos em 35 tarefas; zero falhas/erros/ignorados; 203 testes do APP por flavor |
| Instrumentados | 199 casos em nove tarefas; 196 execuções aprovadas, zero falhas/erros e três ignorados por perfil físico de tela |
| Persistência | 526/532 linhas (98,87%); gate aprovado, mínimo 95,01% |
| Core Data | 2.554/2.686 linhas (95,09%); gate aprovado, mínimo 95,01% |
| Core Domain | 1.763/1.823 linhas (96,71%); gate aprovado, mínimo 95,01% |
| Core Network | 669/675 linhas (99,11%); gate aprovado, mínimo 95,01% |
| Notifications | 604/630 linhas (95,87%); gate aprovado, mínimo 95,01% |
| Releases | PRD, TQS e Automação gerados com sucesso, incluindo otimização R8 |
| Integridade | 2.239 hashes de fontes/configurações e 23 configurações de cobertura inalterados durante a execução |

Os três casos condicionais continuam explicitamente sem execução: Retail `installedPrizeSummaryRetainsTheProductionBottomCoordinate`; Tickets `installedPhysicalPairStartsFirstPurpleRowAtRecordedScreenCoordinate` e `installedPhysicalPairKeepsFavoriteAtNativeWeightedRowPosition`. Suas condições de perfil de dispositivo foram mantidas e esses casos não são contados como aprovados. As duas comparações com fontes/altura do APK nativo executaram sem falhas. O teste isolado do host executado na rodada direcionada anterior não é acrescentado aos totais desta matriz, que contém as nove tarefas instrumentadas acima.

Evidências em `build/verification/app-cleanup-full-matrix-20261020/`: `manifest.json`, `verification.log`, `test-summary.json`, `coverage-summary.json`, 380 XML de testes, 20 XML de cobertura, hashes dos APKs e snapshots/hashes dos inputs. A instrumentação usou o emulador existente; não houve execução do host com o applicationId nativo nesta matriz, alteração de limiares, exclusão de testes, commit/push ou publicação.


## Execução dos três casos condicionais em perfil compatível — 20/10/2026

Os três testes ignorados na matriz anterior foram executados individualmente em emulador temporário `emulator-5562`, com Android 16/API 36 AOSP ARM64,1080×2408,450dpi,fontScale 1,0 e barra de status 66 px. Resultado: **três testes aprovados, zero falhas/erros/skips**, sem alteração de fontes de produção ou testes.

A investigação inicial demonstrou uma diferença de ambiente: o perfil Pixel 9 Pro adicionava inset superior de 156 px, deslocando Retail 880→970 e Tickets 720→810; o caso horizontal da estrela já passava. Sem esse recorte, o Android padrão usava68px e Retail media 882 px. A captura sintética original arquivada confirma 66 px de barra superior e limite do resumo 880 px, por medição de pixels independente das asserções. Foi configurado um overlay somente no sistema do emulador AOSP para reproduzir essas métricas. O frame de status 66 px foi confirmado antes da execução; expectativas, cores, tolerâncias e condições dos testes foram mantidas.

| Caso executado | Resultado |
|---|---|
| Retail `installedPrizeSummaryRetainsTheProductionBottomCoordinate` | Aprovado,880px/roxo879px |
| Tickets `installedPhysicalPairStartsFirstPurpleRowAtRecordedScreenCoordinate` | Aprovado,720px |
| Tickets `installedPhysicalPairKeepsFavoriteAtNativeWeightedRowPosition` | Aprovado,centro783,5px com tolerância original1px |

Retail: BUILD SUCCESSFUL em 7s,254 tarefas (uma executada,253 atualizadas). Tickets: BUILD SUCCESSFUL em 7s,257 tarefas (uma executada,256 atualizadas). Seleção por classe/método e serial explícitos; nenhum caso foi executado no emulador original nesta rodada. Os XML finais foram conferidos contra os três nomes solicitados, com contagem/expectativas intactas.

Os 2.239 hashes de fontes/configurações e 23 configurações de cobertura permaneceram inalterados. A matriz completa anterior não foi repetida porque não houve mudança de código; seus 196 casos instrumentados aprovados e estes três complementares fecham os 199 casos distintos entre os dois perfis. O relatório bruto anterior continua registrando seus três skips, sem conversão retroativa em sucesso.

O AVD criado para a tarefa e a imagem AOSP instalada exclusivamente para ele foram removidos após arquivar a configuração e as evidências. Os quatro AVDs originais e o emulador 5560 em execução foram preservados. Não houve alteração de limiares, exclusão de testes, manipulação do pacote nativo instalado, commit/push ou publicação. Guia/perfil e diagnóstico: `docs/SCREEN_PROFILE_VALIDATION.md`. Evidências: `build/verification/pending-screen-tests-20261020/`, incluindo XML, logs iniciais/finais, configuração do AVD, helper do overlay de sistema, frame do WindowManager, hashes e a captura sintética original usada para medir o inset.


## Limpeza de resíduos de módulos sem uso — 21/10/2026

O grafo configurado pelo Gradle, incluindo dependências de variantes, testes e plugins de convenção, não contém módulos declarados sem uso. Dos 65 módulos registrados, 64 são alcançados pelo app em produção; `:core:testing` é consumido por 22 projetos em configurações de teste. O build incluído `build-logic` fornece os plugins de convenção utilizados pelo projeto. Nenhuma entrada foi removida de `settings.gradle` nesta rodada.

Foram removidas somente as pastas residuais `core/database`, `core/datastore` e `core/preferences`, de módulos já retirados anteriormente. Antes da exclusão, foi confirmado que todos os arquivos estavam em `build/`, sem fontes ou configurações remanescentes. Foram eliminados 2.140 arquivos gerados (27.382.613 bytes). Os inventários e XML históricos de testes/cobertura encontrados foram arquivados nas evidências.

Após a limpeza, os 69 testes das políticas de arquitetura/módulos passaram, assim como `verifyCleanArchitecture`, `verifyFeatureModules`, `verifyComposeMigration` e `verifyKotlinFormatting` (Gradle: 45s, 76 tarefas). Os 2.239 hashes de fontes/configurações e as 23 configurações de cobertura da matriz anterior permanecem idênticos. Nenhum comportamento, teste ou limiar de cobertura foi alterado. A matriz completa `modernCheck releaseCheck` não foi repetida nesta rodada, pois foram excluídos apenas resíduos gerados fora do build ativo.

Evidências: `build/verification/unused-modules-20261021/`, incluindo `manifest.json`, `configured-graph.json`, script temporário de auditoria, inventários e logs de validação.



## Matriz completa após limpeza dos resíduos — 21/10/2026

Executado `ANDROID_SERIAL=emulator-5560 ./gradlew modernCheck releaseCheck --offline --continue --console=plain`. Resultado: **BUILD SUCCESSFUL em 10min39s**, 3.674 tarefas (164 executadas, uma de cache, 3.509 atualizadas). Nenhuma falha exigiu correção de fontes, testes ou build.

Os relatórios das 35 tarefas unitárias contêm 2.089 testes aprovados, sem falhas/erros/skips. As nove tarefas instrumentadas contêm 199 casos: 196 aprovados e três ignorados por perfil de tela, sem falhas/erros. Os skips são os mesmos casos de coordenadas de Retail/Tickets documentados anteriormente; não são contados como aprovados nesta rodada. Sua execução anterior no perfil compatível permanece arquivada em `build/verification/pending-screen-tests-20261020/`.

Arquitetura, formatação, lint, cobertura e os releases minificados PRD/TQS/Automação passaram pela matriz. Os três APKs e respectivos applicationIds foram conferidos e seus hashes arquivados; as tarefas release estavam atualizadas. Persistência: 526/532 linhas (98,87%); Core Data: 2.554/2.686 (95,09%); Core Domain: 1.763/1.823 (96,71%); Core Network: 669/675 (99,11%). Nenhum limiar foi reduzido: as 23 configurações de cobertura são byte a byte idênticas, e os 2.239 hashes de fontes/configurações permaneceram inalterados durante a execução.

Evidências: `build/verification/unused-modules-full-matrix-20261021/`, com `manifest.json`, log integral, resumos, 380 XML de testes, 20 XML de cobertura, snapshots das configurações e hashes dos APKs. A execução respeitou os resultados atualizados/cache do Gradle; não foi usado `--rerun-tasks`. Não houve commit ou push.


## Reexecução dos três casos condicionais após a matriz — 21/10/2026

Os três casos ignorados na matriz `modernCheck releaseCheck` desta data foram executados no emulador temporário `SimloScreen1080x2408D45020261021`, serial `emulator-5562`, com Android 16/API 36 AOSP ARM64 (revisão 2),1080×2408,450dpi,fontScale1,0 e barra de status66px. Os recursos do sistema e o frame do WindowManager confirmaram o perfil antes da execução.

Resultado: **três testes aprovados, zero falhas, zero erros e zero skips**. Retail `ControlledPrizeInteractionTest#installedPrizeSummaryRetainsTheProductionBottomCoordinate` passou em8s; Tickets `InstalledPurchasePairTest#installedPhysicalPairStartsFirstPurpleRowAtRecordedScreenCoordinate` e `#installedPhysicalPairKeepsFavoriteAtNativeWeightedRowPosition` passaram em10s. A seleção usou os mesmos comandos documentados acima, com classe/método e serial explícitos.

Não houve falha a corrigir nem mudança em fontes, testes, condições de execução, expectativas ou tolerâncias. Os2.239 hashes de fontes/configurações e as23 configurações de cobertura permaneceram idênticos à matriz anterior; nenhum limiar foi reduzido. A matriz mantém seus três skips no relatório bruto, e esta execução complementar cobre esses três casos no perfil apropriado:199 casos instrumentados distintos aprovados entre os dois perfis.

O emulador temporário foi encerrado, seu AVD excluído e a imagem instalada exclusivamente para ele removida. O inventário original de quatro AVDs foi restaurado e `emulator-5560` continua em execução. Evidências em `build/verification/pending-screen-tests-20261021/`: XML finais, logs, perfil, frame do WindowManager, helper de configuração, hashes e manifest de resultados/limpeza. Não houve commit, push ou alteração do pacote nativo de referência.


## Revisão final de arquitetura, DS e DRY — 21/10/2026

Tipos e arquivos Legacy migrados em todos os módulos, APIs/cores compartilhadas do DS consolidadas, helpers JSON/moeda centralizados, encaminhamento HomeScreen retirado, limpeza de sessão deduplicada e135 recursos idênticos do app removidos com inventário e consumidores ajustados. Novas regras impedem nomes Legacy em tipos/fachadas de fonte e dependências estáticas repetidas.

Matriz final `ANDROID_SERIAL=emulator-5560 ./gradlew modernCheck releaseCheck --offline --continue --console=plain`: BUILD SUCCESSFUL em30min59s,3.674 tarefas (748 executadas,35 de cache,2.891 atualizadas).2.095 testes unitários aprovados;196 casos instrumentados aprovados e três skips por perfil. Os três casos foram reexecutados com sucesso em perfil1080×2408/450dpi/fontScale1/statusBar66px. Os18 testes instrumentados adicionais do DS passaram, assim como a compilação AndroidTest dos três flavors do app. Total217 casos instrumentados distintos aprovados entre os perfis.

74 testes das políticas e63 cenários dos guardrails passaram.49 conjuntos de regras/limiares JaCoCo permaneceram idênticos, sem perda de classes anteriormente selecionadas após normalizar renomeações; os helpers compartilhados e cinco DTOs adicionais foram incluídos. Persistência526/532 (98,87%), Domain1.763/1.823 (96,71%), Data2.531/2.662 (95,08%), Network724/731 (99,04%). Os2.218 hashes de inputs da árvore final ficaram inalterados durante os testes. Nenhum teste foi removido nem expectativa/limiar relaxado.

Releases PRD/TQS/Automação gerados com R8 e hashes/applicationIds conferidos. O emulador complementar e sua imagem foram removidos; emulador5560 e inventário original preservados. Detalhes: `docs/FINAL_ARCHITECTURE_DS_DRY_REVIEW.md`. Evidências: `build/verification/final-architecture-ds-dry-20261021/`, incluindo XML, logs, comparações de cobertura, mapas/inventários e snapshot inicial compactado com hashes conferidos. Sem commit/push/publicação.

## Revisão KISS — 21/10/2026

Revisados app, 12 módulos core e 52 módulos de features. Foram removidos dois espelhos de estado, 27 getters privados intermediários e sete wrappers de callbacks; simplificadas troca de ambiente, cópias de estado, precedência de rótulos, conflito visual e configuração de cobertura. Contratos e variantes com responsabilidade própria foram preservados.

`modernCheck releaseCheck` passou em 36min11s, com 2.099 unitários aprovados, 196 aprovações instrumentadas e três skips de perfil. Os três casos passaram no perfil correto ; 35 casos instrumentados adicionais passaram, totalizando 234 casos distintos aprovados. Os 49 conjuntos de cobertura mantêm regras, fontes e classes, sem reduzir limiares. Os 2.221 hashes de inputs permaneceram iguais durante a validação. O host foi instrumentado com applicationId isolado; AVD/imagem temporários removidos e mappings restaurados com hashes idênticos.

Detalhes em [docs/KISS_MODULE_REVIEW.md](docs/KISS_MODULE_REVIEW.md); evidências em `build/verification/kiss-review-20261021/`.

## Contrato MVVM e padronização Impl — 21/10/2026

Revisadas 14 features e 41 ViewModels. Corrigida a remoção de cotas expiradas para separar UseCase no domínio e Repository em Data, preservando validação, identidade, ordem, AppResult e cancelamento. Pós-login depende de contrato de DataSource. Padronizadas 60 implementações com Impl e 10 contratos relacionados, com consumidores, Hilt, testes e seletores de cobertura atualizados; nenhum literal de produção foi alterado.

Matriz completa modernCheck/releaseCheck aprovada em25min33s:3.676 tarefas (1.055 executadas,20 de cache,2.601 atualizadas),2.103 testes unitários e196 execuções instrumentadas aprovadas, sem falhas ou erros. Os três skips exigem outro perfil de tela; passaram na rodada KISS anterior e não foram reexecutados nesta rodada sem alterações visuais. AndroidTest dos três flavors compilado; PRD/TQS/Automação gerados com R8, IDs e hashes conferidos.

98 testes das políticas aprovados;1.876 arquivos Kotlin sem violações de formatação.49 conjuntos de regras/mínimos e quatro listas de pré-requisitos de cobertura preservados. Nenhuma classe antes selecionada perdeu cobertura pelas renomeações; a separação de Cart mantém suas regras e acesso a dados selecionados nos respectivos gates. Persistência526/532 (98,87%), Domain1.769/1.829 (96,72%), Data2.527/2.658 (95,07%), Network724/731 (99,04%). Os2.226 hashes de inputs permaneceram idênticos. Revisão independente sem achados restantes.

Relatório: [docs/FEATURE_MVVM_CONTRACT.md](docs/FEATURE_MVVM_CONTRACT.md). Evidências: `build/verification/feature-mvvm-contract-20261021/`. Nenhum commit, push ou publicação.

## Organização dos pacotes MVVM — 21/10/2026

Organizadas as14 features com41 ViewModels: Domain em model/repository/usecase/rule/port; Data em repository/datasource/local/datasource/remote/mapper e agrupamentos de integração existentes. Core Data usa a mesma separação.397 arquivos movidos, dez extraídos de cinco originais; quatro implementações privadas passaram a internal para acesso entre arquivos do mesmo módulo. Corpos e8.022 literais preservados, com equivalência conferida por revisão independente.

modernCheck/releaseCheck passou em22min07s (3.676 tarefas:1.144 executadas,20 de cache,2.512 atualizadas).2.103 unitários e196 execuções instrumentadas aprovadas, sem falhas ou erros. Três casos condicionados a perfil de tela mantêm skips nesta matriz e já passaram no perfil apropriado na rodada KISS anterior; não houve alteração visual.98 políticas e formatação de1.886 fontes Kotlin aprovadas. Compilação AndroidTest e releases dos três flavors aprovados, APKs com IDs/hashes conferidos.

49 conjuntos de regras/mínimos, fontes e quatro listas de pré-requisitos de cobertura preservados. Nenhuma classe antes selecionada perdida após mapear os pacotes; uma classe de metadados da typealias foi acrescentada à persistência. Contagens mantidas: persistência526/532, Domain1.769/1.829, Data2.527/2.658 e Network724/731. Os2.236 hashes de inputs permaneceram idênticos.

Relatório e árvore de pacotes: [docs/MVVM_PACKAGES.md](docs/MVVM_PACKAGES.md). Evidências: `build/verification/mvvm-packages-20261021/`. Nenhum commit, push ou publicação.

## Organização dos pacotes APP/CORE e fronteiras MVVM — 21/10/2026

Organizados os pacotes de APP e dos 12 módulos CORE por responsabilidade, preservando os grupos já coesos de Data/Security e os owners Android/públicos registrados. 209 origens movidas e três arquivos extraídos de Core Domain, totalizando 212 destinos. SessionCartRepositoryImpl, SessionBettingRepositoryImpl e SessionBettingParametersRepositoryImpl passaram para Data com seus testes. A coordenação de campanha passou para CampaignConfigurationUseCases/Impl em Domain; CampaignViewModel consome o contrato de aplicação. Dez owners explícitos de keeps R8 foram atualizados sem alterar flags ou regras.

modernCheck/releaseCheck aprovado em 20min18s: 3.676 tarefas (968 executadas, 21 de cache, 2.687 atualizadas), 2.105 unitários e 196 execuções instrumentadas aprovadas, sem falhas/erros. Os três skips exigem outro perfil de tela e já passaram no perfil adequado na rodada KISS anterior; não houve alteração visual. Design System (21) e Auth (13) passaram em rodada adicional de 1min59s, sem skips, totalizando 230 aprovações instrumentadas nesta rodada. Nenhum teste anterior perdido; dois novos casos comportamentais de sessão.

103 políticas arquiteturais e formatação de 1.889 arquivos Kotlin aprovadas. 49 gates e quatro listas de pré-requisitos mantêm regras, mínimos e fontes; nenhuma classe medida perdida globalmente. Sete classes de Repository foram transferidas de Domain para Data e duas da coordenação de campanha no sentido inverso. Cobertura: Data 2.568/2.699 (95,15%), Domain 1.732/1.788 (96,87%), persistência 526/532 (98,87%) e Network 724/731 (99,04%). Os 8.022 literais de produção e os 2.239 hashes dos inputs finais foram preservados.

AndroidTest dos três flavors compilado; releases PRD/TQS/Automação gerados com R8, IDs/hashes conferidos, sem instalar ou instrumentar o app oficial. Relatório, revisão e limites: [docs/APP_CORE_PACKAGES.md](docs/APP_CORE_PACKAGES.md). Evidências: `build/verification/app-core-packages-20261021/`. Nenhum commit ou push.

## Ownership de DTOs e limpeza de pacotes — 21/10/2026

Corrigidos os 18 arquivos com 95 DTOs dos nove módulos transport: namespace core.network → feature.<feature>.data.dto, com imports explícitos e nomes Dtos para arquivos agrupadores. Removido config/feature-transport-compatibility.json e seu desvio de ownership. 41 arquivos de teste passaram dos namespaces antigos core.data/core.preferences/apostas aos pacotes de suas features. Regra R8 explícita ampliada para o novo namespace com as mesmas flags; regra de Core Network preservada para seus DTOs próprios. Nenhum corpo/asserção alterado.

Removidos 510 diretórios vazios. Grafo Gradle dos 65 módulos sem órfãos: 64 alcançáveis em produção, Core Testing usado pelos testes. Módulos transport/api são contratos usados e foram mantidos. 107 políticas arquiteturais e formatação dos 1.889 arquivos Kotlin aprovadas.

modernCheck/releaseCheck passou em 22min49s: 3.675 tarefas (993 executadas, 23 de cache, 2.659 atualizadas), 2.105 unitários e 196 aprovações instrumentadas, sem falhas/erros. Os três skips dependem de perfil de tela e já passaram no perfil apropriado na rodada KISS anterior; não houve alteração visual. Comparação confirma os mesmos 2.105 casos unitários e 199 instrumentados, sem perdas/acréscimos.

49 gates mantêm regras/mínimos/fontes, quatro listas de pré-requisitos e classes medidas após mapear os namespaces. Os 20 relatórios mantêm suas contagens de linhas: Data 2.568/2.699, Domain 1.732/1.788, persistência 526/532 e Network 724/731. Corpos dos 59 arquivos movidos e 75 consumidores preservados, assim como os 21.851 literais Kotlin e 2.228 hashes finais. Manifestos, dependências e configuração Gradle iguais. Releases PRD/TQS/Automação gerados com IDs/hashes conferidos; AndroidTest dos três flavors compilado, sem instalar ou instrumentar o app oficial.

Relatório: [docs/UNUSED_PACKAGES_REVIEW.md](docs/UNUSED_PACKAGES_REVIEW.md). Evidências: `build/verification/unused-packages-20261021/`. Nenhum commit ou push.
