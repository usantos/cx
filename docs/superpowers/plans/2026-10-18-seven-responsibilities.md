# Seven Responsibilities Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans. Execute all tasks inline; checkbox syntax tracks progress.

**Goal:** Refatorar os sete arquivos solicitados por responsabilidade, mantendo contratos e comportamento.

**Architecture:** Fachadas mantêm entradas e owners; colaboradores internos recebem dependências, estado, escopo e callbacks existentes. Mapeadores e corpos visuais são movidos literalmente. Autenticação mantém o monitor da fachada e a mesma ordem de stores/commits.

**Tech Stack:** Kotlin, Compose, AppAuth, StateFlow/coroutines, Gson, JUnit, Android instrumentation, JaCoCo.

**Spec:** SPEC.md e pedido do usuário com sete arquivos/separações nesta conversa.

## Global Constraints

- Preservar APIs/construtores/defaults/retornos, StateFlow, escopos, efeitos/remember, geometria, payloads e tratamentos de falha/cancelamento.
- Preservar checkout/trabalho anterior. Sem commits/pushes, mudanças de versões/DI/protocolo/recursos ou chamadas privadas reais.
- Não reduzir mínimos0.9501, includes ou fontes de cobertura. Expandir includes apenas quando necessário para manter comportamento extraído no denominador.
- Caracterização e equivalência antes/depois; nenhum teste antigo enfraquecido. Falhas preexistentes documentadas separadamente.

## Review Focus

- Avisos de números/trevos compartilham flags, incluindo shake/quantidades; reset não limpa flags implicitamente.
- Carrinhos favoritos compartilham cache e IDs importados; mesma validação de sessão e ordem de callbacks.
- Pix mantém restauração/AtomicBoolean/POST ambíguo, snapshots, prioridade redirects/avisos e registro só quando concluído.
- Auth mantém synchronized da fachada, lock gateway→store, cancelamento por ticket, PKCE/nonce/identidade, renovação e persistência tardia/rollback originais.
- Fila mantém tempo mínimo, efeitos/owners, retry, fallback de erro, broadcasts e disposal WebView na mesma ordem.
- Data mantém recursão, prioridades, defaults, filtros/ordem/distinct, colunas/zeros/escala e seleção de endpoint/erros.

## Shared interfaces / pre-flight

Data: toDomainCart e extensões ticket permanecem no package e disponíveis aos consumidores; novos helpers privados ganham prefixo específico para evitar colisão. Betting: um contexto de avisos compartilhado e mesmo updateState. Payment/Favorite: MutableStateFlow original compartilhado e mesmo viewModelScope/dispatchers. Auth: um contexto interno recebe monitor=this e callback logout da fachada. Fila: mesmas duas entradas públicas e destinos internos, componentes internos conservam chaves/owners.

### Task 1: Baseline e Data

**Files:** core/data/.../TicketsPayloadMapper.kt, RemoteCartRepository.kt; criar TicketFilterMapper, TicketCheckedResultMapper, TicketConfirmedBetMapper, TicketPurchaseMapper, TicketSportsMapper, TicketJsonValues, CartPayloadMapper, CartItemMapper, CartSportsMapper, CartJsonValues, CartValidationOperations.
**Interfaces:** extensões toBets/toPurchases/toCheckedTicketResult/toConfirmedBetsConfiguration/toPurchaseMonths/toFilterOptions/ticketLong/ticketText/ticketFindFirstText/ticketDisplayName e toDomainCart preservadas; RemoteCartRepository mantém CartRepository/construtor.
- [x] Executar suítes originais JVM dos seis módulos afetados antes de editar produção; preservar XMLs/APIs/fontes.
- [x] Extrair funções por domínio, preservar corpos com renomeação de helpers/receivers/delegações verificável.
- [x] Executar Data:test/check e consumidores; comparar contrato, payloads e exceções nos testes existentes.

### Task 2: Betting selection

**Files:** feature/betting/.../BettingGameSelection.kt; criar BettingNumberSelection, BettingSuperSevenSelection, BettingSportsSelection, BettingSelectionQuantities, BettingSelectionWarnings, BettingSelectionReset.
**Interfaces:** mesmos métodos de BettingGameSelection; colaboradores recebem updateState e flags compartilhadas quando necessários.
- [x] Extrair seleção por modalidade/quantidades/reset; preservar todos os corpos e estado dos avisos.
- [x] Executar Betting JVM/check/cobertura; comparar flags, resets, seleções e API.

### Task 3: Favorite carts

**Files:** feature/cart/.../FavoriteCartsViewModel.kt; criar FavoriteCartsUiState, FavoriteCartsLoading, FavoriteCartsRemoval, FavoriteCartsImport, FavoriteCartsFailures.
**Interfaces:** ViewModel mantém construtor/API/StateFlow; loading e import compartilham betsCache; import mantém importedFavoriteCartIds.
- [x] Extrair carregamento/expansão, remoção, importação e falhas; manter confirmações e wiring na fachada.
- [x] Executar JVM e check Android/cobertura; validar cache/remoção/importação/retornos e sessão.

### Task 4: Payment

**Files:** feature/payment/.../PaymentViewModel.kt; criar PaymentUiState, PaymentMethodsLoading, PaymentSavedCards, PaymentPrePurchase, PaymentPixOperations, PaymentNotices, PaymentRedirects.
**Interfaces:** ViewModel mantém load/acceptPrePurchase retornando Job, demais APIs e state; colaboradores recebem mesmo estado/escopo/dispatchers e applyFailure.
- [x] Extrair por responsabilidade sem alterar guards, restauração Pix, monitoramento e callbacks.
- [x] Expandir includes de cobertura aditivamente; executar JVM/check/Android com restauração, cancelamento, avisos e redirects existentes.

### Task 5: FilaBRA

**Files:** app/.../FilaBraGate.kt; criar FilaBraInitialization, FilaBraQueueGate, FilaBraMockGate, FilaBraStatusContent, FilaBraQueueWebView, FilaBraBroadcasts.
**Interfaces:** FilaBraAppGate/FilaBraQueueScreen e destinos/defaults preservados; mesma Activity/Container; sem novo ViewModel ou state owner.
- [x] Extrair inicialização, fila, mock, status e WebView/broadcast; conservar corpos e contexto composicional.
- [x] Executar testes app/startup/queue, instrumentação do fluxo e gates; equivalência visual/efeitos.

### Task 6: AppAuth

**Files:** core/auth/.../AppAuthAuthenticationGateway.kt; criar AppAuthSession, AppAuthBiometricAvailability, AppAuthBrowserAuthorization, AppAuthBiometricAuthorization, AppAuthRefresh, AppAuthTokenUpdates, AppAuthEndSession.
**Interfaces:** quatro interfaces públicas e funções internas existentes preservadas; contexto mantém stores/service/identity e monitor da fachada. Nenhuma nova fonte de verdade/lock.
- [x] Mover operações preservando annotations Synchronized públicas e substituindo this apenas pelo monitor original nos blocos movidos.
- [x] Executar Auth JVM/check/Android e app compile; comparar APIs/corpos/lock order e testes de identidade/cancelamento/credenciais.

### Task 7: Integração e registro

- [x] Executar novamente JVM dos módulos, checks/lint/cobertura/gates e compilação app; instrumentação centralizada em AVD próprio, sem Gradles concorrentes.
- [x] Revisão independente única somente leitura do escopo desta rodada, conforme executing-plans; corrigir achados concretos e repetir verificações afetadas.
- [x] Atualizar ARCHITECTURE/MIGRATION_PROGRESS/MIGRATION_VALIDATION; registrar contagens reais/limites; conferir escopo/hashes/APIs e restaurar/encerrar somente AVD próprio.

Ruling: o pedido explícito autoriza execução das sete tarefas. Seguir inline sem nova aprovação de plano ou commits; extração sem comportamento novo usa suítes de caracterização verdes e comparação literal.

Resultado: 825 JVM e 122 Android distintos aprovados; 175 verificações de equivalência/APIs, check/lint/cobertura, APK PrdDebug e dez gates aprovados. Revisão independente sem achados pendentes após restaurar imports implícitos Compose. Metadados Auth originais preservados. AVD próprio preservado/encerrado, sem commits/pushes. Detalhes e limites em MIGRATION_VALIDATION.md.
