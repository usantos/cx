# Progresso da modernização

Autoridade: SPEC.md. Atualização: 19/10/2026. O repositório mantém 80 rotas Compose e nenhum Java próprio. Este documento reúne as etapas anteriores; a seção mais recente registra os sete ajustes concretos de conformidade e sua validação.

A arquitetura atual tem Presentation/DI/navegação e módulos Domain/Data pertencentes às features; core contém somente responsabilidades compartilhadas. Consulte a seção final desta rodada e ARCHITECTURE.md para os donos atuais. Todas as frentes preservam os contratos externos. A única correção funcional adicional é a retenção de eventos com coletor ausente, exigida pela seção 6.3 da spec e reproduzida antes da correção.

| Feature/frente | Situação anterior | Nova implementação e arquitetura | Regras preservadas | Testes efetivamente executados | Pendências e riscos | Status |
|---|---|---|---|---|---|---|
| betting/tutorial | Route com 292 linhas, coordenação e tutorial juntos | Route 27 linhas; BettingScreen, AnimatedLotteryTutorial, SuperSevenTutorial e TutorialIndicator na Presentation | Efeitos, skip/finish, pager e persistência da preferência | 2 casos de coordenação passaram no original; 27 testes Android de tutorial/pager/modalidades passaram após extração | Sem pendência de código no escopo; paridade visual global requer APK legado | Concluído (escopo técnico local) |
| payment/cartão | Route com 261 linhas e todas as etapas | Route 57 linhas; PaymentCardFlowScreen e PaymentCardFlowComponents, com ações explícitas por etapa | CVV, tokenização, confirmação, redirect, avisos, review e progresso | 2 casos de coordenação passaram no original; 11 testes Android e 99 JVM aprovados no estado final | Provedor privado e transações reais não homologados nesta execução | Concluído (escopo técnico local) |
| retail/localização | Route 258 linhas, UI e plataforma misturadas | Route 115 linhas; LocationGateScreen, LocationGatePlatform e LocationGatePreviews separados | Permissões, recusa, retry, settings, provider e país | 7 testes Android aprovados, incluindo 2 novos de Screen e 5 de comprovantes; suíte JVM na matriz | GPS/geocoder físico e serviços privados dependem de ambiente autorizado | Concluído (escopo técnico local) |
| account/dados | Route 257 linhas com formulário e formatadores | Route 90 linhas; PersonalDataScreen, PersonalDataPresentation e PersonalDataPreviews | CEP, limite, notificações, IME, footer e diálogos | 8 testes Android de formulário/IME/limite/falha e 70 JVM aprovados | Atualização real de cadastro e serviços privados não homologados | Concluído (escopo técnico local) |
| tickets/scanner | Route 220 linhas com tela e câmera | Route 50 linhas; TicketScannerScreen, TicketScannerPreviews e TicketCameraPreview (CameraX/MLKit) | Permissão, formatos, QR/query, callbacks e disposal | 2 novos testes de Screen aprovados na suíte Tickets: 39 aprovados e 4 condicionais não executados | Câmera física não homologada; condições dos 4 testes descritas no relatório | Concluído (escopo técnico local) |
| home | Route 375 linhas com efeitos, duas UIs, diálogos e persistência concreta | Route 260 linhas; HomeRouteEffects, HomeRouteDialogs, HomeSystemBarsEffect, HomeV2RouteContent e di/HomeLayoutDependencies no app | Saved state, drawer, animações, consumo único, proteção e recovery | 6 testes app/Home clássica, 12 app/Home padrão/restauração e 4 feature/home aprovados | Comparação histórica de todos os estados privados continua separada | Concluído (escopo técnico local) |
| betting/estado | ViewModel 851 linhas | Fachada 296 linhas; BettingGameSelection, BettingGameFavorites, BettingGameDraftMapper, BettingGameUiState; LotterySelectionPolicy em Domain | Seleção, rascunho, mirror, favoritos e cancelamento | 139 testes JVM aprovados; gate ampliado com 98,34% de linhas | Sem pendência de código no escopo; colaboradores incluídos na cobertura | Concluído (escopo técnico local) |
| tickets/estado | ViewModel 792 linhas | Fachada 254 linhas; TicketsHistoryCoordinator, TicketsPurchaseCoordinator, TicketsPixCoordinator, TicketsPixDeadlineTracker e TicketsUiState | Filtros, paginação/offset, cache, prazos e cancelamento | 58 testes JVM e 3 novos testes de interação de filtros aprovados; gate JVM+Android em 97,62% | Sem ciclo entre coordenadores; cobertura Android obrigatória | Concluído (escopo técnico local) |
| cart/estado | ViewModel 529 linhas | Fachada 264 linhas; CartCheckoutCoordinator, CartReservationCoordinator e CartUiState/ação de localização | Ordem, sessão, concorrência, expiração e limpeza | 101 testes JVM e 65 testes Android aprovados; cobertura 97,26% | Sem pendência de código no escopo; transações reais não executadas | Concluído (escopo técnico local) |
| data/tickets | Repository 603 linhas | Fachada 72 linhas; TicketsRemoteDataSource, BetHistoryRoutingPolicy, TicketsPayloadMapper e ConfirmedBetReceiptLoader | Payload, rollout, status, prêmio, identidade e cache | 6 novas caracterizações passaram nos originais e na extração; suíte Data com 213 aprovados | Serviço privado não exercitado ao vivo | Concluído (escopo técnico local) |
| data/payment | Repository 377 linhas | Fachada 58 linhas; PaymentRemoteDataSource, CardPaymentTokenizer, CardPurchaseRequestMapper, PaymentPayloadMapper e PurchaseResponseMapper | JSON/nulls/flags, HTTP 303, avisos/rejeições, purchaseId e cancelamento | 3 novas caracterizações passaram nos originais e na extração; Data 213 aprovados e 95,58% de linhas | Nenhum pagamento real executado | Concluído (escopo técnico local) |
| design system | Arquivo 1007 linhas com responsabilidades misturadas | Scaffold 85 linhas e 8 arquivos por grupo; 2 previews adicionais | 26 declarações, defaults, corpos e estilo privado idênticos | 17 testes Android aprovados no original e no estado final; 26 declarações e estilo privado idênticos | Fixture nativa não substitui homologação visual global | Concluído (escopo técnico local) |
| Domain/Java | 3 classes Java de teste, 23 casos | Mesmas 3 classes em Kotlin, sem alterar modelos de filtros | Imutabilidade, normalização e query | Mesmos 23 casos traduzidos; Domain com 175 testes aprovados e 96,55% de linhas | Nenhum Java próprio restante em app/core/feature | Concluído (escopo técnico local) |
| Eventos UDF | 11 SharedFlows sem replay perdiam eventos com coletor ausente | SingleConsumerEventQueue em common; Flow público, FIFO limitada e cancelável | Conteúdo e ordem; StateFlow das telas inalterado | RED NavigateBack→null preservado; regressão e 3 testes da fila aprovados; matriz recompila os 11 consumidores | Não garante ACK de efeito externo nem restauração após morte do processo | Concluído (escopo técnico local) |
| Ferramentas | 21 literais de dependência em 17 builds; sem contrato de whitespace central | Catálogo mantém versões/scopes; .editorconfig e gate sem reescrita | Todos os gates/limites anteriores mantidos | modernCheck/releaseCheck aprovados; 1079 Kotlin sem violações e fixtures negativas rejeitadas | Android Lint e gates existentes mantidos; versões/scopes preservados | Concluído (escopo técnico local) |

O primeiro build completo aprovou os três releases minificados; a baseline falhou apenas nas cinco tarefas Android sem dispositivo. A matriz final no AVD dedicado passou em 12m 7s, com 1833 execuções JVM aprovadas. A instrumentação obrigatória aprovou 148 casos e registrou 4 condicionais não executados; a instrumentação complementar aprovou 88 casos, sem falhas ou skips. A revisão independente de código não encontrou regressões concretas nas extrações e complementos. Os resultados finais e limites estão em [MIGRATION_VALIDATION.md](MIGRATION_VALIDATION.md).

Documentos em `docs/` continuam como evidência histórica datada. A transferência pós-login de carrinho local, descrita como pendente em alguns deles, já está implementada no código atual e coberta por testes de contrato de envio, confirmação, limpeza e identidade de sessão. Homologação externa e paridade visual global não podem ser inferidas desses testes.

O comando comum de todas as frentes foi `ANDROID_SERIAL=emulator-5560 ./gradlew modernCheck releaseCheck --offline --continue --console=plain`. Os comandos completos dos testes Android complementares estão em `build/verification/modernization-spec-20261018/affected-ui-runs.json` e `classic-home-run.json`; resultados e cobertura estão no relatório de validação. As seis rotas, três ViewModels, dois repositories e o arquivo do Design System foram concluídos. Restam homologação externa e os quatro testes de comparação condicionados ao ambiente legado, sem pendência de implementação local identificada nesta rodada.

## Complemento — PrizeRedemptionViewModel (18/10/2026)

A rodada anterior acima permanece como registro histórico. Nesta continuação, o ViewModel de resgate foi reduzido de 400 para 177 linhas, preservando construtor/métodos/retornos públicos, StateFlow, ownership de estado/fila e viewModelScope.

| Feature | Situação anterior | Nova implementação/arquitetura | Regras preservadas | Testes executados | Pendências/riscos | Status |
|---|---|---|---|---|---|---|
| retail/resgate — estado | Coordenação, quatro canais, contador, PDFs e decisões concentrados | Presentation: Coordinator, Channels (lotérica/agência, Pix, Mercado Pago), Submission, Timer, Receipts, Notices, Redirects; tipos em UiState | Guardas/loading nos mesmos instantes, QR só obrigatório na lotérica, consumo por identidade, rede/relógio, retorno às opções, bytes/nomes/fila e cancelamento | 55 JVM antes/depois (5 novos); 35 corpos/contratos comparados; check/lint/gates/compilação PrdDebug aprovados; cobertura 506/516 = 98,06%; UI 22 aprovados e 1 condicional ignorado no perfil padrão | No perfil 1080×2408/450dpi, teste geométrico espera 880px e mede 960px; mesma falha reproduzida com VM original byte-idêntico. UI fora deste escopo permanece igual | Concluído (refatoração); divergência visual preexistente documentada |

Comando final: `./gradlew :feature:retail:check :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain` — BUILD SUCCESSFUL em 13s. Instrumentação: `:feature:retail:connectedDebugAndroidTest` com as classes `ControlledPrizeInteractionTest` e `PrizeReceiptInteractionTest`. Logs, snapshots e comparação original/refatorado em `build/verification/prize-viewmodel-extraction-20261018/`. Nenhum commit/push, alteração de UI, dependência ou protocolo.

## Complemento — HomeViewModel (18/10/2026)

Continuação posterior às rodadas anteriores; suas contagens globais permanecem históricas. A fachada foi reduzida de 343 para 111 linhas, preservando API pública, StateFlow, três revisões AtomicLong e mesmo escopo/dispatcher.

| Feature | Situação anterior | Nova implementação/arquitetura | Regras preservadas | Testes executados | Pendências/riscos | Status |
|---|---|---|---|---|---|---|
| home/estado | Sessão, parâmetros, notificações, carrinho, repasses e entradas concentrados no ViewModel | Presentation: HomeSessionCoordinator, HomeLoadingCoordinator, HomeNotificationsCoordinator, HomeCartCoordinator, HomeSocialTransfersCoordinator, HomeEntryCoordinator; tipos públicos em HomeUiState.kt | Ordem de invalidação/estado/badge, identidade/CAS, descarte de respostas atrasadas, deduplicação/cache/retry, finally, bloqueio comum e consumo de decisões | 33 JVM no original e após extração (3 novos); 25 corpos + observador comparados; 4 UI módulo, 6 app clássico, 12 app padrão/V2/restauração/visitante aprovados; cobertura 438/444=98,65% | Sem pendência local identificada no escopo; serviços privados não homologados nesta extração | Concluído |

Comando: `ANDROID_SERIAL=emulator-5560 ./gradlew :feature:home:check :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain` — BUILD SUCCESSFUL em 39s. Instrumentação adicional usa `:app:connectedPrdDebugAndroidTest` nas configurações e classes registradas em `build/verification/home-viewmodel-extraction-20261018/app-ui-results.json`. Default HOME_V2=true/backend real restaurado e conferido. Revisão independente sem achados; nenhuma alteração de UI, Hilt, rede, persistência, dependência, mínimo/includes anteriores; sem commits/pushes.

## Complemento — BetDetailViewModel (18/10/2026)

Continuação posterior às rodadas anteriores, cujas contagens globais permanecem históricas. A fachada de detalhes foi reduzida de 336 para 83 linhas; mantém construtor/métodos/retornos públicos, init, StateFlow e fila de comprovantes.

| Feature | Situação anterior | Nova implementação/arquitetura | Regras preservadas | Testes executados | Pendências/riscos | Status |
|---|---|---|---|---|---|---|
| tickets/detalhe — estado | Hidratação, PDF, favoritos, repetição e mappers concentrados | Presentation: BetDetailDetailsCoordinator, BetDetailReceiptCoordinator, BetDetailFavoriteCoordinator, BetDetailRepeatCoordinator; BetDetailMappers e tipos em BetDetailUiState.kt | Mesmos guards/loading, fallback opcional, falhas tipadas, snapshot/consentimento, coluna/quantidade/complementos, fila/cancelamento e resultados | 64 JVM no original e após extração (6 novos); 19 corpos/7 mappers/draft/init/contrato comparados; 43 UI: 39 aprovados e 4 condicionais ignorados; cobertura 1013/1041 = 97,31%; check/lint/gates/app compile aprovados | Quatro comparativos legados exigem perfil 1080×2408 e/ou APK original; guardas preservadas. Sem regressão local identificada na refatoração | Concluído (refatoração) |

Comando final: `ANDROID_SERIAL=emulator-5560 ./gradlew :feature:tickets:check :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain` — BUILD SUCCESSFUL em 3m 6s. Todos os includes anteriores e mínimo 0.95 permanecem; fachada/colaboradores/coroutines/mappers/tipos passaram a integrar o denominador. Revisão independente sem achados. Evidências em `build/verification/bet-detail-viewmodel-extraction-20261018/`. Sem commits/pushes, alteração de UI, Hilt, dependências, rede ou persistência.

## Continuação — MarketplaceViewModel (18/10/2026)

- **Status:** concluído e validado no escopo solicitado.
- **Antes:** ViewModel de328 linhas concentrava buscas/cabeçalho, filtro/paginação por área, duas operações de favoritos, cotas e reserva no carrinho.
- **Agora:** fachada de125 linhas; seis novos colaboradores internos: MarketplaceLoadingCoordinator, MarketplaceSearchPagination, MarketplaceFavoriteRetailersCoordinator, MarketplacePoolFavoriteCoordinator, MarketplaceSharesCoordinator e MarketplaceCartCoordinator. A fachada conserva coordenação/API, estado, init/revisão, abas e expansão. Presentation continua consumindo os mesmos use cases Domain, sem alteração de Data/DI/UI.
- **Preservado:** construtor/defaults/retornos Unit/StateFlow; mesmo MutableStateFlow e viewModelScope; init/retry/revisões; filtro/áreas/páginas/append automático; cabeçalho regular/especial; travas dos favoritos; cotas/pedido/preços/reserva/mensagens. Nenhum escopo, lock, retry, deduplicação ou sincronização de listas adicional.
- **Validação:**41 JVM aprovados no original e final, incluindo7 novas caracterizações;27 Android aprovados, zero falhas/erros/skips. Check/lint, compilação PrdDebug e gates de formatação/arquitetura/Compose aprovados em2m 33s. Cobertura376/377=99,73%, mínimo 0.95 e todos os includes anteriores intactos; colaboradores/coroutines incluídos de forma aditiva.47 verificações de equivalência aprovadas;19 testes originais intactos. Revisão independente somente leitura sem achados.
- **Pendências/riscos:** nenhuma nova pendência identificada nesta extração. Homologação de serviços privados, matriz global/release e paridade visual integral não foram repetidas; permanecem os limites históricos de MIGRATION_VALIDATION.md. AVD/pacotes/tamanho/densidade/fonte preservados. Sem commits/pushes.
- **Evidências:** build/verification/marketplace-viewmodel-extraction-20261018/ e docs/superpowers/plans/2026-10-18-marketplace-viewmodel-extraction.md.

## Continuação — PaymentCardViewModel (18/10/2026)

- **Status:** concluído e validado no escopo solicitado.
- **Antes:** arquivo de 322 linhas concentrava estado/etapas, formulário, cartões salvos, envio, polling, avisos e redirecionamentos.
- **Agora:** fachada de 93 linhas e oito novos arquivos: seis coordenadores (PaymentCardFormCoordinator, PaymentCardSavedCardsCoordinator, PaymentCardSubmissionCoordinator, PaymentCardMonitoringCoordinator, PaymentCardNoticeCoordinator e PaymentCardRedirectCoordinator), contexto compartilhado PaymentCardPurchaseContext/PaymentCardTrackedPurchase e tipos públicos em PaymentCardUiState.kt. Presentation continua consumindo os mesmos use cases Domain; UI/DI/Data/rede/persistência permanecem intactos.
- **Preservado:** API/defaults/retornos Unit/Boolean/Job, MutableStateFlow privado/StateFlow público, init, entradas legadas, snapshot de seleção/CVV antes do carrinho/POST, total original, guardas e marcador de revisão, polling/IDs/intervalos/limites/retry/conclusão, avisos por identidade, redirects por igualdade, pausas e cancelamento. Nenhuma submissão automática, timeout, limpeza ou trava nova.
- **Validação:** 104 JVM aprovados no original e final, incluindo cinco novas caracterizações; 32 Android aprovados, zero falhas/erros/skips. Check/lint, compilação PrdDebug e gates de formatação/arquitetura/Compose aprovados em 2m 20s. Cobertura ampliada para 608/612 linhas = 99,35%, mínimo 0.9501 e todos os includes anteriores preservados. Sessenta verificações de equivalência aprovadas; 28 testes originais intactos; revisão independente somente leitura sem achados de regressão.
- **Pendências/riscos:** nenhuma nova pendência identificada nesta extração. Homologação de provedores privados, matriz global/release e paridade visual integral não foram repetidas; limites históricos permanecem em MIGRATION_VALIDATION.md. Pacotes/tamanho/densidade/fonte do AVD preservados. Sem commits/pushes.
- **Evidências:** build/verification/payment-card-viewmodel-extraction-20261018/ e docs/superpowers/plans/2026-10-18-payment-card-viewmodel-extraction.md.

## Continuação — RemoteRetailRepository (18/10/2026)

- **Status:** concluído e validado no escopo solicitado.
- **Antes:** arquivo de 261 linhas reunia duas interfaces remotas, três contratos Domain, lotéricas/favoritos, resgates, cadastro Mercado Pago, PDFs e mapeadores.
- **Agora:** fachada de 47 linhas; dez arquivos novos separam cinco colaboradores internos (RetailLotterySearch, RetailFavorites, PrizeRedemptionOperations, MercadoPagoRegistrationLoader e RedemptionReceiptLoader), três grupos de mapeadores/leitura JSON e as duas interfaces públicas realocadas no mesmo package. Data continua usando os mesmos contratos, adaptador Retrofit e clientes reais da aplicação.
- **Preservado:** construtor/default cast, três contratos públicos, validação tardia da fonte, consulta de favoritos antes de cada busca/sem cache, payloads crus e IDs, prioridades/fallbacks/escala decimal/zeros/canais/Loteca/ordem das premiações, bytes dos PDFs, allowRegistration por canal, tratamento HTTP/IO e propagação das demais falhas/cancelamento. Nenhum retry, scope, normalização ou fallback novo.
- **Validação:** 218 JVM de Data aprovados no original e final, incluindo cinco novas caracterizações; mais 55 JVM de Retail e 104 de Payment executados novamente. Total final: 377, zero falhas/erros/skips. Check Data/cobertura, compilação app PrdDebug/Hilt e gates de formatação/arquitetura/Compose aprovados em 14s. Cobertura Data 2196/2296 linhas = 95,64%; build.gradle byte a byte idêntico, incluindo todas as classes/coroutines e mínimo 0.9501. Cinquenta e seis verificações de equivalência aprovadas, 29 testes originais intactos e revisão independente sem achados.
- **Pendências/riscos:** nenhuma nova pendência identificada na extração. Esta rodada JVM não repetiu instrumentação Android, matriz global/release ou homologação privada; limites históricos permanecem em MIGRATION_VALIDATION.md. Sem mudanças de UI/DI/Domain/Network/Retrofit/persistência/dependências e sem commits/pushes.
- **Evidências:** build/verification/retail-repository-extraction-20261018/ e docs/superpowers/plans/2026-10-18-retail-repository-extraction.md.

## Continuação — RemoteBettingRepository (18/10/2026)

- **Status:** concluído e validado no escopo solicitado.
- **Antes:** arquivo de 266 linhas concentrava modalidades, parâmetros, escudos, combos, envio de apostas e todos os mapeamentos/serialização.
- **Agora:** fachada de 27 linhas com o mesmo BettingRepository/construtor/fonte; nove arquivos novos separam quatro colaboradores (BettingModalitiesLoader, BettingParametersLoader, BettingCombosLoader e BettingSubmission) e cinco mapeadores (BettingModalityMapper, BettingParametersMapper, BettingComboMapper, BettingSportsMapper e BettingRequestMapper). Data continua consumindo os mesmos DTOs, fonte e adaptadores; UI/DI/Domain/Network/persistência não mudam.
- **Preservado:** API, payloads completos por modalidade, tipos/descrições/quantidades/trevos, números versus colunas Super Sete sem reordenação, campos esportivos/placares string/mês/espelho/opcionais, game?:nextGame/hasNextContest, escudos condicionais/último ID, defaults/case/valores/escala/combos/pools, retorno do carrinho e falhas. Mantido o runCatching amplo original da consulta opcional de escudos, inclusive fallback quando a fonte lança cancelamento; não foi alterada esta regra durante refatoração.
- **Validação:** 226 testes JVM Data aprovados no original e final, incluindo oito novas caracterizações; 139 de Betting e 101 de Cart executados novamente. Total final: 466, zero falhas/erros/skips. Check/cobertura Data, compilação PrdDebug/Hilt e gates de formatação/arquitetura/Compose aprovados em 15s. Cobertura Data 2210/2310 linhas = 95,67%; build.gradle byte-idêntico, incluindo todas as classes/coroutines e mínimo 0.9501. Trinta e uma verificações de equivalência aprovadas; fontes/contratos/testes antigos intactos; revisão independente sem achados.
- **Pendências/riscos:** nenhuma nova pendência identificada nesta extração. Rodada JVM sem nova instrumentação Android, matriz global/release, homologação de provedores privados ou comparação visual integral; limites históricos permanecem em MIGRATION_VALIDATION.md. Nenhum scope/cache/retry/validação/dependência novo e sem commits/pushes.
- **Evidências:** build/verification/betting-repository-extraction-20261018/ e docs/superpowers/plans/2026-10-18-betting-repository-extraction.md.

## Continuação — BettingSportsContent (18/10/2026)

- **Status:** refatoração concluída e validada no escopo solicitado; diferenças geométricas preexistentes a 450 dpi documentadas.
- **Antes:** arquivo de 410 linhas reunia seleção de times, Loteca, cartões, escudos, medidas e chamadas ao ViewModel.
- **Agora:** fachada de 15 linhas conserva BettingLegacyTeams/BettingLegacyLoteca; seis arquivos novos: BettingTeamSelection, BettingLotecaContent, BettingLotecaOutcomeCard, BettingTeamShield, BettingSportsMetrics e BettingSportsCallbacks. Os visuais recebem estado/funções, e o adaptador liga os três métodos ao ViewModel atual sem remember/cache/estado próprio.
- **Preservado:** ViewModel/Route/API/defaults, filtro/label selecionado/grade 3 colunas, tamanhos/fontes/modificadores, distribuição e alinhamento inteiro, descrição vinculada a remember(team,descriptionBindingKey), scroll, estados/ordem dos resultados, Base64/raw/data URI/fallback e chamadas de quantidade/ajuda. Recursos e testes antigos intactos.
- **Validação:** 139 JVM executados novamente e24 Android aprovados a 480 dpi, zero falhas/erros/skips; mesmos 24 Android passaram antes da extração. Três caracterizações novas testam troca de VM/callbacks e pixels/geometria dos escudos. Check/lint, PrdDebug e gates aprovados em 3m 24s; cobertura1066/1084= 98,34%,configuração/mínimo 0.9501 intactos.20 checks de equivalência/APIs aprovados e revisão independente somente leitura sem achados.
- **Pendências/riscos:** no perfil inicial450 dpi, três comparativos nativos já falhavam no original (Partidas54 vs 55 px, distribuição379 vs 380 px, cabeçalho SuperSete158 vs 156 px). Repetidos após a extração: mesmas três falhas e mensagens; sem alterar oráculos/gates/geometria. Perfil480 dpi é o declarado pelos testes nativos. Paridade completa, matriz global/release e homologação privada não foram repetidas. Configuração/pacotes do AVD restaurados; sem commits/pushes.
- **Evidências:** build/verification/betting-sports-extraction-20261018/ e docs/superpowers/plans/2026-10-18-betting-sports-extraction.md; detalhes em MIGRATION_VALIDATION.md.

## Continuação — sete arquivos por responsabilidade (18/10/2026)

**Status:** os sete arquivos solicitados foram refatorados e validados no escopo desta rodada. As fachadas somavam 2164 linhas e agora somam 455; 43 arquivos de produção separam as responsabilidades, com 25 novas classes nomeadas e quatro tipos existentes realocados no mesmo package. Dois arquivos de teste acrescentam três caracterizações Android. Nenhuma rota/tela nova ou classe Java foi introduzida.

| Frente | Antes → agora | Separação | Contratos/regras preservados | Validação |
|---|---:|---|---|---|
| FilaBraGate | 359 → 82 | Initialization, QueueGate, MockGate, StatusContent, QueueWebView e Broadcasts | Duas entradas/defaults, destinos, owners/chaves, splash/segurança, SDK/retry, eventos, Back e disposal | App 134 JVM, Queue 4, Onboarding 18; nova caracterização da fila antes/depois; equivalência e API |
| AppAuthAuthenticationGateway | 344 → 56 | Session, BiometricAvailability, BrowserAuthorization, BiometricAuthorization, Refresh, TokenUpdates e EndSession | Quatro interfaces, mesmo monitor/stores/service, gateway→store, PKCE/nonce/identidade, tickets/cancelamento, persistência e logout | Auth 99 JVM e 13 Android; dois novos casos de guards/logout aprovados antes/depois; API/metadados preservados |
| TicketsPayloadMapper | 341 → 46 | Filtros, consulta manual, apostas, compras, esportes e leitura JSON | Extensões de entrada, recursão, prioridades/defaults, distinct/ordem, colunas, zeros e escala | Suíte Data 226 compartilhada com carrinho; equivalência dos mapeadores/API |
| BettingGameSelection | 329 → 57 | Seleção numérica, Super Sete, esportes, quantidades, reset e avisos | Mesmo updateState e uma única instância das flags de avisos; políticas/guards/limites/mensagens intactos | Betting 139 JVM e 11 Android de seletores/quantidade/swipe/times/Loteca/escudos; equivalência/API |
| FavoriteCartsViewModel | 275 → 79 | Loading, Removal, Import, Failures e tipos públicos realocados | API/Job, StateFlow, scope/dispatchers, mesmo cache/IDs, validações de sessão e confirmações | Cart 101 JVM e 65 Android; equivalência/API |
| PaymentViewModel | 269 → 115 | MethodsLoading, SavedCards, PrePurchase, PixOperations, Notices, Redirects e tipos públicos realocados | API/Job/Boolean/Unit, estado/scope, restauração e guarda Pix, POST ambíguo, avisos por identidade e redirects | Payment 104 JVM e 32 Android; equivalência/API |
| RemoteCartRepository | 247 → 20 | RemoteOperations, ValidationOperations, PayloadMapper, ItemMapper, SportsMapper e JsonValues | CartRepository/construtor, toDomainCart, endpoints, payloads/ordem/badges, HTTP/JSON/IO e cancelamento | Suíte Data 226 compartilhada com Tickets; equivalência/API |

**Resultados:** 825 testes JVM e 122 Android distintos aprovados, zero falhas/erros/skips. A baseline original executou 803 JVM nos seis módulos e 110 Android em Auth/Cart/Payment; a nova caracterização da fila passou antes de sua extração. Após a restauração dos metadados de Auth, 99 JVM Auth + 134 App e 13 Android Auth foram executados novamente, sem inflar a contagem de casos distintos. Os 122 Android incluem App 1, Auth 13, Betting 11, Cart 65 e Payment 32.

**Build/gates:** check/cobertura/lint dos módulos alterados, lint App PrdDebug, compilação e APK PrdDebug, mais os dez gates de formatação, Compose, arquitetura, inventário UI, backend99 operações, CaixaDS, FilaBRA, Topaz, rede e identidade/flavors aprovados. 175 verificações de equivalência/APIs aprovadas. Revisão independente terminou sem achados pendentes; imports implícitos Compose identificados pelo revisor foram restaurados antes da compilação final.

**Cobertura:** Data 2226/2328 (95,62%), Auth 316/323 (97,83%), Betting 1107/1126 (98,31%), Cart 2044/2101 (97,29%) e Payment 664/668 (99,40%). Mínimos 0.9501 e inputs anteriores mantidos; includes de Betting/Payment ampliados apenas para incorporar os colaboradores. Nenhuma exclusão para aprovar gate.

**Pendências/riscos:** nenhuma regressão conhecida identificada nesta rodada. Homologação real de OAuth/biometria/SSO, fila SDK/provedores privados, matriz global/release dos três flavors e paridade visual de todos os estados não foram repetidas. Divergências geométricas históricas permanecem registradas separadamente. Configuração/pacotes do AVD preservados; somente o emulador iniciado nesta rodada foi encerrado. Sem novas dependências, alterações de DI/DTOs/recursos/manifests, commits ou pushes.

**Evidências:** build/verification/seven-responsibilities-20261018/; plano em docs/superpowers/plans/2026-10-18-seven-responsibilities.md; comandos, resultados e limites em MIGRATION_VALIDATION.md.

## Continuação — dez arquivos por responsabilidade (18/10/2026)

| Fachada/arquivo original | Antes | Depois |
|---|---:|---:|
| `RapidConfigurationViewModel.kt` | 245 | 78 |
| `LoginViewModel.kt` | 227 | 65 |
| `ResponsibleGamingViewModel.kt` | 226 | 85 |
| `NotificationsViewModel.kt` | 215 | 71 |
| `TicketsNavigationGraph.kt` | 355 | 32 |
| `PrizeOptionsScreen.kt` | 325 | 87 |
| `PrizePixScreen.kt` | 294 | 110 |
| `NetworkClientFactory.kt` | 247 | 144 |
| `ApiModels.kt` | 737 | 10 |
| `MockBackendInterceptor.kt` | 744 | 54 |

| Feature | Situação anterior→agora (linhas) | Implementação/arquitetura | Regras preservadas | Testes JVM | Status |
|---|---|---|---|---:|---|
| betting/Rapidão | 245→78 | Loading/Amounts/Favorites/Numbers/Submission/Formatting | limites, snapshot, seleção e init | 142 | Concluído no escopo local |
| account/login | 227→65 | AuthorizationCoordinator/BiometricPreferences/Completion/EntryDecisions/ResultMapping | cancelamento, fallback, bloqueio e logout | 70 | Concluído no escopo local |
| responsible-gaming | 226→85 | Assessment/Suspension/SocialTransfers/Errors | sessão, respostas, suspensão e fila de efeitos | 14 | Concluído no escopo local |
| notifications | 215→71 | Loading/Projection/Mutations/Preferences/Actions/Errors | revisão, leitura, confirmações, preferências e compartilhamento | 22 | Concluído no escopo local |
| app/tickets | 355→32 | cinco grupos de destinos | ordem, owners/keys, argumentos e back stack | 134 | Concluído no escopo local |
| retail/resgate | 325→87 e 294→110 | composição e componentes de opções/Pix | geometria, estágios, habilitação e callbacks | 55 | Concluído no escopo local |
| network | 247→144; 737→10; 744→54 | famílias de clientes, builder, DTOs por domínio e cenários/fixtures | JSON, defaults, políticas HTTP, pinning e interceptação | 120 | Concluído no escopo local |

882 testes JVM aprovados (baseline 877; cinco novas caracterizações aprovadas também com as fachadas originais); 65 testes Android aprovados, um teste condicional ignorado, zero falhas/erros; 352 comparações de fonte/API aprovadas. Revisão independente sem achados. Checks/cobertura/lint dos sete módulos envolvidos, lint PrdDebug do app, dez gates e APK PrdDebug aprovados.

As features permanecem na Presentation consumindo os mesmos use cases de Domain; rede continua em infraestrutura compartilhada. Foram criados 75 arquivos de produção e dois arquivos de testes, com cinco casos novos de ordem, captura de estado e cancelamento. Não foram alterados oráculos existentes.

**Pendências/riscos:** nenhuma regressão local identificada no escopo; um teste geométrico de resgate exige perfil 1080×2408/450dpi e permaneceu condicional no AVD 1280×2856/450dpi. Não foram repetidas a matriz global/release, homologação de serviços privados, hardware biométrico ou paridade visual integral. Comportamentos preexistentes foram preservados, inclusive limites de concorrência/tratamento de exceções.

**Evidências:** `build/verification/ten-responsibilities-20261018/`; plano em `docs/superpowers/plans/2026-10-18-ten-responsibilities.md`. Pacotes nativos de referência e perfil do AVD preservados; encerrado somente o emulador criado nesta rodada. Sem commits/pushes.

## Sete ajustes concretos de conformidade — 19/10/2026

**Feature:** conformidade de comprovantes, parâmetros esportivos, lifecycle, QR e carrinho visitante. **Status:** concluído no escopo dos sete ajustes.

| Arquivo anterior | Implementação e regras preservadas | Validação |
|---|---|---|
| PrizeReceiptManager.kt | Escrita em IO no escopo da composição; cache e visualizador somente após fechar o arquivo com sucesso; falha de gravação apresenta aviso; visualizador ausente mantém comportamento anterior | Provedor Android real observado fora da Main, visualizador na Main, bytes/nome/URI/cache, falhas e cancelamento |
| BetDetailReceipts.kt | BetReceiptDelivery propaga cancelamento e impede erro/abertura após descarte | Escrita com executor controlado, falha real, visualizador ausente, cancelamento direto e durante escrita |
| BettingParametersLoader.kt | CancellationException propagada; fallback dos escudos preservado para falhas comuns | Cancelamento direto/suspenso e IOException; teste antigo de cancelamento atualizado |
| NotificationsRoute.kt | Coleta de StateFlow vinculada ao lifecycle; mesmos efeitos e navegação | Rota real STARTED → CREATED → RESUMED, sem renderizar alterações enquanto parada |
| ResponsibleGamingRoute.kt | Mesma correção, preservando API, ações e seleção | Rota real mantém seleção renderizada enquanto parada e recebe a última ao retomar |
| PrizeLotteryScreen.kt | Gerador e componente próprios, trabalho em Default, produtor por valor | Pixels idênticos, decodificação, worker/Main, cancelamento, resultado obsoleto, tamanho e acessibilidade |
| SQLiteVisitorCartRepository.kt | Políticas em Domain; armazenamento e mapeadores em Preferences | Quantidade/preços/duplicidade/espelhos, mapper idêntico, persistência, IDs reservados e rollback real de aposta/espelho/sequência |

**Arquitetura:** sete novos arquivos Kotlin de produção, seis novas classes/objetos; Presentation/Domain/Data mantidos, sem novas dependências. Nenhum ViewModel ou UiState foi modificado. Foram aprovadas 63 verificações de contratos/fontes, incluindo o mapeador visual movido sem alteração.

**Testes executados:** 755 testes JVM aprovados nos oito módulos; 108 Android aprovados e cinco testes visuais condicionais ignorados pelo perfil do emulador, zero falhas. Foram adicionados 31 casos JVM e sete casos Android. Checks/cobertura dos sete módulos, lint PrdDebug do app, APK PrdDebug e dez gates aprovados. Comandos e contagens por módulo em MIGRATION_VALIDATION.md e build/verification/seven-compliance-20261018/.

**Riscos e pendências:** escrita nativa bloqueante já iniciada pode terminar após cancelamento, mas não publica cache, visualizador ou erro; os testes cobrem esse descarte. Estado do seletor de documentos durante recriação e visualizador de PDF ausente mantêm os comportamentos anteriores. Os cinco casos visuais exigem outro perfil; integrações privadas, matriz global de flavors/release e decisão sobre mensagens legadas não foram homologadas nesta rodada. Nenhum commit ou push.

## Links externos e recuperação de comprovantes — 19/10/2026

**Feature:** abertura de links em Conta/Home e entrega dos PDFs de resgate. **Status:** correções locais e matriz global concluídas; duas coordenadas visuais e homologação real permanecem pendentes conforme os limites registrados. O resultado final dos gates consta na última seção de MIGRATION_VALIDATION.md.

**Situação anterior:** Conta anunciava sucesso mesmo quando a abertura falhava; Home não tratava exceções do atalho; o PDF pendente dependia da composição e podia desaparecer na recriação. Falhas após restaurar para OPTIONS não ofereciam recuperação suficiente.

**Nova implementação:** AccountRoute/HomeRoute exibem erro e permitem repetir a URL exata. PrizeReceiptManager coordena seletor, recuperação e abertura; PrizeReceiptDelivery retém bytes/operação/cache na ViewModel existente; PrizeRedemptionReceipts oferece recuperação interna pelo nome permitido. Falhas de recuperação, gravação e abertura têm retry e cancelamento explícitos.

**Arquitetura e regras preservadas:** nenhuma alteração em Domain/Data, HTTP, segurança, UiState ou assinaturas públicas. StateFlows e fila de eventos originais continuam sendo os mesmos. Bytes permanecem na memória da ViewModel; saved state contém apenas nome/URI. Escrita usa IO e propagação de cancelamento; efeitos de UI pertencem à composição atual. Retry de escrita/abertura não repete download, seletor nem resgate.

**Testes executados:** regressões RED antes das correções de links, perda de comprovante, recuperação em OPTIONS, falha de gravação e cancelamento dos diálogos; GREEN final com 17 Android de comprovantes, 68 JVM de Retail e cinco Android de Conta mais cinco Android de Home. ActivityScenario.recreate valida retenção real da ViewModel; restauração de metadados com nova VM, falha repetida do ContentProvider e descarte durante escrita têm testes separados. Nenhum ensaio de encerramento real do processo foi realizado. Revisão independente final sem achados acionáveis.

**Pendências e riscos:** os cinco testes visuais rodaram sem skips no perfil emulado 1080×2408/450dpi: três aprovados e dois reprovados; as capturas preparadas evidenciam deslocamento superior de 80px frente à referência Samsung. A homologação de OAuth, biometria, SSO, fila e provedores privados exige conta/aparelho ainda indisponíveis, conforme confirmação do usuário. Sondas HTTPS e fixtures não substituem essa homologação. Uma chamada nativa bloqueante já iniciada pode terminar fisicamente após cancelamento, sem publicar sucesso/erro/visualizador depois do descarte. Sem commits/pushes.

**Fechamento da matriz desta rodada:** modernCheck aprovado (24 checks de módulos, dez gates, assemble/JVM/lint dos três flavors); releaseCheck aprovado para Prd/Tqs/Automacao. Relatórios globais somam 1.910 casos JVM e 190 Android aprovados, três skips condicionais e zero falhas; incluem saídas válidas reutilizadas pelo Gradle e repetição por flavor. Os três APKs release são unsigned, sem recursos locais de assinatura; não foram distribuídos. Detalhes e hashes em MIGRATION_VALIDATION.md e release-artifacts.json. A execução dedicada dos cinco visuais continua com três aprovados e dois reprovados no AVD, sem mudança dos oráculos.

## Central de mensagens local e módulos Hilt — 19/10/2026

**Feature:** Notifications, origem local e composição de dependências. **Situação anterior:** a fachada reunia catálogo demonstrativo, mutações da lista e switches; NotificationsModule reunia caixa local, contador e leitura do popup.

**Implementação:** LocalNotificationRepository coordena LocalNotificationMessages e LocalNotificationPreferences; LegacyNotificationInboxSamples identifica a origem literal dos dados. UnreadNotificationsModule e LatestNotificationModule isolam os fluxos remotos existentes. Os sete métodos públicos de NotificationsModule permanecem disponíveis, sem bindings duplicados. O teste existente da factory passou a usar o provider real da caixa.

**Arquitetura/regras:** Data contém estado local e implementa Domain; App é o composition root; Presentation e seus StateFlows não foram modificados. Preservados ordem/textos/datas/IDs, leitura/exclusão local, snapshots, seis switches independentes, monitor único, reset por destino e escopos Hilt. A comparação com o Java confirmou origem demonstrativa, conforme a referência solicitada pelo usuário. Não foi inventada integração.

**Validação e status:** resultados finais e comandos desta etapa são registrados na seção correspondente de MIGRATION_VALIDATION.md. O escopo é uma extração de responsabilidades, sem conversão adicional de telas ou alteração de ViewModel.

**Pendências/riscos:** a caixa legada não é um histórico real de FCM/backend; sua preservação explícita não encerra a conformidade global com SPEC3.3. Continuam os limites anteriores de homologação privada, assinatura e dois testes geométricos. Nenhum commit ou push.

**Fechamento:** refatoração concluída no escopo autorizado. Caracterização prévia e validação final aprovadas: 838 casos JVM (incluindo repetições por flavor), dez Android sem falhas/skips, checks/cobertura de Data/Domain/Notifications, assemble/JVM/lint dos três flavors debug e dez gates existentes. Revisão independente sem achados acionáveis. APIs públicas comparadas por javap e StateFlows/fontes de Presentation preservados. Detalhes em MIGRATION_VALIDATION.md; limites de dados demonstrativos e homologação global permanecem explícitos.

## Checkout do carrinho — 19/10/2026

**Feature:** checkout de Cart. **Status:** concluído no escopo da refatoração e validação local.

**Situação anterior:** CartCheckoutCoordinator (241 linhas) reunia conta/refresh de reservas, localização, permissões, compra recente, validação, avisos e redirecionamentos.

**Nova implementação:** fachada com 102 linhas e mesmo construtor/métodos; seis colaboradores internos: CartCheckoutLocation (77), CartCheckoutPermissions (73), CartCheckoutRecentPurchase (58), CartCheckoutValidation (32), CartCheckoutNotices (29) e CartCheckoutRedirects (20). São seis arquivos/classes Kotlin novos e dois arquivos existentes alterados (fachada e testes). Nenhuma nova tela migrada, nenhum Java introduzido e nenhum módulo/dependência criado.

**Arquitetura e regras preservadas:** mesma CartViewModel, CartUiState, MutableStateFlow/StateFlow e viewModelScope; mesmos dispatchers e use cases de Domain. Preservadas guardas/ordem, carrinho vazio, falha opcional de refresh, cancelamento/finally da localização incluindo chamadas seguintes, consumo único das permissões, consentimento por carrinho capturado, relógio/conectividade e identidade dos avisos/redirects. Nenhuma mudança em UI, Domain/Data, rede, persistência, Hilt ou segurança.

**Testes executados:** duas caracterizações novas passaram no original e após a extração: gravação suspensa de permissão, sem publicar ação antecipada/duplicar checkout; cancelamento suspenso em compra recente e validação, com limpeza e nova tentativa. Baseline: 103 Cart JVM +185 Domain. Validação final: 422 JVM (103 Cart,185 Domain,134 App PrdDebug) e 65 Android, sem falhas/erros/skips; Cart check/Domain check, assemble/JVM/lint App PrdDebug e dez gates aprovados. Cart:2103/2160 linhas (97,36%); fachada e seis colaboradores:209/209. O mínimo 95,01% e inputs/padrões de cobertura foram preservados.

**Revisão/evidências:** revisão independente sem achados acionáveis;18 métodos movidos literalmente, guardas/validação/mapeamento comparados, assinaturas declaradas de coordenador/ViewModel/UiState iguais (bridges sintéticas excluídas). Evidências: build/verification/cart-checkout-20261019/. Comandos completos e limites em MIGRATION_VALIDATION.md.

**Pendências/riscos:** nenhuma regressão identificada no escopo. Outros flavors/releaseCheck, modernCheck integral, cinco visuais condicionais e homologação privada não foram repetidos; permanecem os limites das rodadas anteriores. Não houve teste financeiro real. Checkout e comportamento de cancelamento existentes foram preservados. Sem commits/pushes.

## Histórico Tickets — 19/10/2026

**Feature:** histórico de apostas/compras. **Status:** refatoração e validação local concluídas.

**Situação anterior:** TicketsHistoryCoordinator (246 linhas) reunia catálogos, consultas, filtros, paginação e retries. **Implementação:** fachada compatível com 72 linhas; seis classes internas novas: TicketsHistoryBets, TicketsHistoryPurchases, TicketsHistoryBetFilters, TicketsHistoryPurchaseFilters, TicketsHistoryPagination e TicketsHistoryRecovery. São seis arquivos/classes Kotlin novos; coordenador, teste existente e build de Tickets alterados. Nenhuma tela nova migrada, Java introduzido ou dependência/módulo criado.

**Arquitetura/regras:** mesma ViewModel, UiState, StateFlow, scope, dispatchers e callback de falha. Preservados catálogos/defaults, verificações de sessão, momento de leitura dos filtros, queries/offset/tamanho 10, deduplicação/ordem, flags, cancelamento e retry. Domain/Data, UI, navegação, Hilt, rede e persistência mantêm os fontes.

**Testes:** três caracterizações novas passaram no original e após a extração (append após expirar sessão, retry com catálogos existentes/filtros atuais e leitura enfileirada de filtros). Baseline 72 TicketsJVM+185 Domain; validação final 391 JVM (72 Tickets,185 Domain,134 AppPrdDebug) e 41 Android aprovados, dois casos geométricos condicionais ignorados, zero falhas/erros. Tickets/Domain checks, assemble/JVM/lint PrdDebug e dez gates aprovados. Cobertura Tickets 1090/1118(97,50%), coordenador/colaboradores 236/236 linhas. Todos os padrões/inputs e mínimo 0.95 preservados; somente seis padrões de colaboradores adicionados.

**Revisão/evidências:** revisão independente sem achados;29 métodos e APIs declaradas comparados. Evidências em build/verification/tickets-history-20261019/. A primeira tentativa de instrumentação falhou antes dos testes por defeito do AGP com --serial; a mesma validação passou usando ANDROID_SERIAL. Não houve modificação do projeto para contornar a falha.

**Pendências/riscos:** dois testes de coordenadas físicas mantêm condição de resolução/densidade. As duas verificações nativas de fonte/tema executaram com o APK legado local no AVD temporário. Isso não comprova paridade visual integral nem homologação real. Outros flavors/releaseCheck, modernCheck integral e integrações privadas não foram repetidos; pendências anteriores permanecem. Sem commits/pushes.

## Composição da Home clássica — 19/10/2026

**Status:** refatoração e validação local concluídas. HomeLegacyContent reduzido de 302 para 103 linhas. Seis arquivos novos separam barra superior, carrossel, cartões/ações, atalhos, campanha e resumo do carrinho; sete funções Composable internas sem estado, nenhuma nova classe de produção/tela/Java/dependência. As duas data classes existentes conservam as assinaturas.

**Preservação:** composição continua proprietária de key/remember/effect/seleção/constraints. Mesmos pais BoxScope, ordem, modifiers, geometria, insets, escala, scroll/clique/rodapé, animações e callbacks. HomeRoute/HomeScreen, LotteryCard, mapeadores, ViewModels/StateFlows e demais camadas/configurações mantêm os fontes por hash.

**Validação:** três novas caracterizações passaram no original e na extração, junto dos dez casos de coordenação/drawer existentes. Após estabilizar somente a captura do ripple nativo nos testes, 17 imagens (oito quadros intermediários) e o arquivo de medidas são exatos antes/depois, sem máscaras/tolerâncias. Nove contratos de fonte e três APIs compiladas iguais; revisão independente sem achados. Check final padrão aprovado em 1 m 55 s: Home check, cobertura 98,65%, assemble/JVM/lint AppPrdDebug e dez gates. Resultados de 167 JVM (com reutilização válida de Home inalterado) e 17 Android sem falhas/erros/skips. Evidências em build/verification/home-legacy-20261019/ e comandos/diagnóstico completo em MIGRATION_VALIDATION.md.

**Limites:** nenhuma nova regressão identificada no escopo; os limites globais anteriores continuam (dois visuais físicos, assinatura release, conta/aparelho biométrico e homologação privada, mensagens demonstrativas autorizadas). modernCheck integral e outros flavors/releaseCheck não foram repetidos. Sem commits/pushes.

## MVVM/Clean em todas as features — 19/10/2026

**Implementação concluída:** 14 features com propriedade de Presentation/Domain/Data/Platform conforme a responsabilidade real. As ViewModels existentes mantêm seus StateFlows. Coordenação, estados, mapeadores, rotas, telas, componentes e estilos estão separados. Domain/Data/Platform pertencem fisicamente às features e preservam os agregadores de compilação existentes; Home host permanece compilada por app, sem dependência do AAR Home para o aplicativo.

**Separações adicionais:** sete fachadas concluídas, capacidades puras de sessão em conta, revisão local do carrinho por casos de uso e preferências da Home por ViewModel/IO. Nomes de pacote foram alterados e as fronteiras UI/Android foram adaptadas com seus consumidores; a compatibilidade binária com os pacotes antigos não é declarada. Demais contratos de rede, persistência, StateFlows e recursos preservados.

**Validação global:** modernCheck padrão aprovado em 15m29s: 24 checks de módulos, dez gates, três flavors debug, 1.933 casos JVM sem falhas/erros/skips e 193 Android, sendo 190 aprovados e três condicionais ignorados no perfil padrão. Contagens incluem repetição por flavor e resultados válidos reutilizados pelo Gradle; não representam testes únicos reexecutados à força. releaseCheck padrão também aprovado em7m39s nos três flavors. Instrumentação complementar concluída: Account43/Auth13/Filter17/Onboarding9/Payment32/Support7 aprovados; Betting24 e MarketplaceSpecialGames4 aprovados no perfil480. Os cinco visuais especiais foram executados: três aprovados e duas falhas físicas80px, mantidas. Os dois prompts Betting falham identicamente no código anterior e no refatorado. Detalhes dos perfis e resultados não aprovados preservados em MIGRATION_VALIDATION.md.

**Provas:** 631 registros de movimento; 600 corpos literais iguais e 31 fronteiras/divisões explicitamente revisadas; zero mudanças inexplicadas. 65 reclassificações de auxiliares e sete divisões mistas. Cinco APIs de fachadas idênticas após extração, 42 trechos do lote e 14 verificações Home equivalentes, 17 imagens mais medidas exatas antes/depois. 251 recursos/configurações protegidos iguais; 23 configurações de cobertura com mínimos/padrões mantidos. Revisões independentes sem achados acionáveis.

Contagens de Kotlin de produção por raiz física; zero significa ausência de responsabilidade exclusiva naquela camada, sem código artificial para preencher pastas.

| Feature | Presentation | Domain | Data | Platform | Host |
|---|---:|---:|---:|---:|---:|
| account | 34 | 12 | 12 | 1 | 0 |
| betting | 93 | 11 | 14 | 1 | 0 |
| cart | 51 | 10 | 13 | 13 | 0 |
| home | 17 | 2 | 0 | 1 | 45 |
| marketplace | 29 | 6 | 2 | 0 | 0 |
| marketplace-filter | 14 | 0 | 0 | 0 | 0 |
| notifications | 17 | 1 | 4 | 0 | 0 |
| onboarding | 16 | 1 | 0 | 1 | 0 |
| payment | 44 | 7 | 6 | 0 | 0 |
| responsible-gaming | 16 | 1 | 2 | 0 | 0 |
| results | 15 | 2 | 2 | 0 | 0 |
| retail | 46 | 2 | 12 | 0 | 0 |
| support | 16 | 2 | 2 | 0 | 0 |
| tickets | 56 | 1 | 12 | 0 | 0 |

**Limites:** a migração de propriedade não declara isolamento binário dos domínios; ciclos de contratos preexistentes continuam agregados. As limitações físicas/privadas, mensagens demonstrativas autorizadas e assinatura de distribuição seguem documentadas. Evidências em build/verification/feature-mvvm-20261019/. Nenhum commit/push/stage/reset.

## Etapa atual — independência real por feature, 19/10/2026

Implementação arquitetural e validação global local concluídas. São 68 módulos declarados e cinco plugins no build de convenções. Domain/API, Data/Transport e Presentation têm donos reais; as features possuem DI e navegação. Ciclos Account/Cart/Betting e agregação de compilação removidos. Os contratos e modelos públicos anteriores permanecem em fachadas/aliases; nenhuma API remota foi inventada.

Validação local já concluída: 1.027 superfícies públicas sem quebras; 37 ViewModels/StateFlows e 392 declarações Domain preservados; 28 testes Android de persistência aprovados; contratos HTTP e troca de snapshot do provider aprovados; dez fixtures reais dos guards rejeitadas e baseline restaurado. Home antes/depois do primeiro checkpoint: 13 testes em cada fase e 18 capturas byte a byte iguais. A última rodada conjunta de Domain/Data/Network/persistência/gates passou em55s, 991 tarefas (117 executadas e874 up-to-date); mínimos anteriores mantidos.

Escopos e corpos de estado/efeitos/cancelamento foram conservados. LocalNotificationRepository mantém a referência demonstrativa do legado autorizada pelo usuário e continua com instância por destino. Persistência continua em SharedPreferences/SQLite com os mesmos nomes, chaves, SQL e transações. Nenhuma homologação privada ou assinatura de distribuição é inferida desses testes.

Revisão independente concluída sem achados. modernCheck aprovado em16m50s e releaseCheck dos três flavors aprovado em5m30s;2228 casos reportados, zero falha/erro e três skips condicionais, incluindo reutilização válida e repetição por flavor. Cinco visuais executados separadamente, sem skip: três aprovados e duas divergências físicas anteriores preservadas. Repetição da animação: oito quadros idênticos ao baseline. Debug padrão restaurado e somente o AVD temporário desta rodada removido. Os limites físicos/privados continuam pendentes. Evidências completas em `build/verification/feature-modules-20261019`; resultados finais e pendências na última seção de MIGRATION_VALIDATION.md.

## Validação final do host APP — 20/10/2026

O checkout contém 68 módulos declarados e 62 fontes Kotlin de produção no APP, sem ViewModel de feature. A composição raiz aplica CaixaDS; navegação, inicialização e bindings de composição ficam no host, enquanto os fluxos de negócio permanecem em features e CORE.

`modernCheck` passou em 14m22s (2498 tarefas); `releaseCheck` passou para Automação, Prd e Tqs (1575 tarefas, 37s, com R8 já válido reutilizado); a restauração `PrdDebug`, os gates de arquitetura e formatação passaram. A cobertura agregada Data ficou em 2628/2762 linhas (95,148%), acima de 95,01%. Quatro testes instrumentados adicionais do runtime de notificações passaram; nenhum limiar foi reduzido.

Os cinco visuais condicionais foram executados sem skips: três passaram e dois preservam as divergências de 80px no resumo de prêmio (880 esperado/960 medido) e primeira linha da compra (720/800). Nenhum oráculo foi alterado. A revisão independente da arquitetura não teve achados acionáveis; os pequenos ajustes de gate/testes feitos após o corte da revisão passaram na matriz global e nos testes direcionados.

Ainda dependem de recursos externos indisponíveis: OAuth/biometria/SSO/fila/provedores privados em homologação real, perfil físico para as duas coordenadas e assinatura de distribuição. Evidências reproduzíveis: `build/verification/app-mvvm-20261019/global-validation-phases.json`, `modern-check.log`, `release-check.log`, `visual-results.json` e `core/data/build/reports/jacoco/test/jacocoTestReport.xml`. Nenhum commit, push ou publicação.

## Conformidade MVVM — transições de estado no ViewModel (20/10/2026)

**Features:** Support e MarketplaceFilter.

**Situação anterior:** `SupportReducer` e `MarketplaceFilterReducer` transformavam ações em novos estados fora dos ViewModels, contrariando a SPEC anexada, que proíbe Reducers/Store/Middleware.

**Nova implementação:** o `SupportViewModel` e o `MarketplaceFilterViewModel` agora processam as ações diretamente e atualizam seus `MutableStateFlow`; mapeadores permanecem somente para conversões e validações. O gate `check-feature-architecture.py` rejeita novos pacotes `presentation.reducer` e funções `UiState.reduce`.

**Regras preservadas:** as interfaces públicas, os StateFlows, a emissão de efeitos, as validações de cotas, os filtros, a busca de lotéricas e as transições anteriores foram mantidos. A troca de aba continua preservando os IDs de perguntas expandidas, como no comportamento legado.

**Testes executados:** `:feature:support:testDebugUnitTest` (16 aprovados), `:feature:marketplace-filter:testDebugUnitTest` (25 aprovados), cobertura de MarketplaceFilter acima do mínimo, `SupportScreenCoordinationTest` (3 aprovados) e `MarketplaceFilterCoordinationUiTest` (7 aprovados) no AVD API 16. Os testes do guard de arquitetura (10) e os gates de arquitetura de feature, host e módulos passaram. A primeira rodada instrumentada revelou que o fake Compose precisava refletir os valores editados; após ajustar somente o fake, os sete casos passaram.

**Pendências e riscos:** nenhum identificado nesta etapa. A validação global e homologações físicas/privadas permanecem conforme `MIGRATION_VALIDATION.md`.

**Status:** concluído.
