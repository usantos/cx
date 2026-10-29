# HomeViewModel Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox syntax for tracking.

**Goal:** Separar responsabilidades de HomeViewModel mantendo contrato e comportamento.

**Architecture:** Fachada mantém estado e revisões compartilhadas. Seis colaboradores internos recebem referências/escopo/dependências atuais. Tipos públicos no mesmo package.

**Tech Stack:** Kotlin, StateFlow, Android ViewModel, coroutines, JUnit/Compose UI Test, JaCoCo.

**Spec:** docs/superpowers/specs/2026-10-18-home-viewmodel-extraction-design.md e SPEC.md.

## Global Constraints

- Manter interface pública, StateFlow e comportamento atual.
- Nenhum include ou mínimo 0.9501 anterior removido/reduzido; colaboradores/coroutines incluídos.
- Sem alterações de UI/DI/rede/persistência/dependências.
- Mesmo checkout codex/migracao-compose, preservando trabalho anterior; sem commits/pushes.

## Review Focus

- Ordem sessão → invalidação das três revisões → atualização do estado → refresh de não lidas.
- Flags e guards de carregamento nos mesmos instantes; finally executado no cancelamento.
- Resultados atrasados não substituem conteúdo da sessão nova; identidade e CAS preservados.
- Três decisões de entrada compartilham o mesmo bloqueio e consumo.
- Todos os Jobs usam o mesmo viewModelScope, sem observadores independentes.

### Task 1: Caracterizar o original

**Files:** HomeViewModelTest.kt e snapshots em build/verification/home-viewmodel-extraction-20261018/.

**Interfaces:** apenas construtor/métodos/state existentes.

- [x] Adicionar 3 casos: load pendente deduplica e libera retry; repasses falham/permitem retry/cacheiam sucesso; limpar VM cancela load e observação da sessão.
- [x] Executar `./gradlew :feature:home:testDebugUnitTest --offline --console=plain`. Esperado: suíte verde contra original. Caracterização de refatoração, sem RED artificial ou comportamento novo.

### Task 2: Extrair e validar

**Files:** HomeViewModel.kt; criar HomeUiState.kt, HomeSessionCoordinator.kt, HomeLoadingCoordinator.kt, HomeNotificationsCoordinator.kt, HomeCartCoordinator.kt, HomeSocialTransfersCoordinator.kt e HomeEntryCoordinator.kt no mesmo package; feature/home/build.gradle (includes aditivos).

**Interfaces:** helpers internal usam MutableStateFlow<HomeUiState>, CoroutineScope e CoroutineDispatchers existentes. Session recebe as mesmas três AtomicLong e callback de refresh das notificações; nenhuma dependência circular. A fachada mantém todos os métodos/retornos.

- [x] Extrair tipos/corpos preservando init, assinaturas, flags, mensagens, versões e dependências.
- [x] Comparar fontes/contratos com snapshot e manter includes anteriores byte-idênticos após remover somente adições.
- [x] Executar `ANDROID_SERIAL=emulator-5560 ./gradlew :feature:home:check :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain`. Esperado: build/JVM/UI obrigatória/lint/gates/cobertura >=95.01% verdes.
- [x] Revisão independente somente leitura, sem Gradle concorrente.
- [x] Executar HomeScreenCoordinationUiTest com homeV2=false e depois padrão true com HomeScreenCoordinationUiTest, HomeV2CoordinationUiTest, AppNavigationStateRestorationTest e VisitorHomeEntryUiTest. Esperado: todas aprovadas; default real backend e HOME_V2 restaurados.
- [x] Atualizar plano, progresso, arquitetura e validação com resultados reais e limpar somente emulador próprio.
