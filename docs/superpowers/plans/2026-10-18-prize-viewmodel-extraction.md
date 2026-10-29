# PrizeRedemptionViewModel Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Extrair responsabilidades de PrizeRedemptionViewModel preservando a fachada pública e comportamento.

**Architecture:** Estado e fila permanecem privados no ViewModel. Colaboradores internos do mesmo módulo recebem dependências, estado e escopo existentes. Tipos públicos mantêm package e assinaturas.

**Tech Stack:** Kotlin, Android ViewModel, StateFlow, Coroutines, JUnit, JaCoCo.

**Spec:** docs/superpowers/specs/2026-10-18-prize-viewmodel-extraction-design.md; SPEC.md.

## Global Constraints

- Manter interface pública, StateFlow e comportamento atual.
- Preservar todos os gates/includes e mínimo de cobertura 0.9501; incluir colaboradores e coroutines.
- Não alterar UI, DI, protocolos, integrações, persistência ou dependências.
- Não fazer commits/pushes. Continuar no checkout codex/migracao-compose com snapshots da árvore atual.

## Review Focus

- Atualização do código cancela contador anterior sem mudar seu instante inicial.
- Back cancela contador e preserva os demais campos exatamente como hoje.
- Avisos usam identidade e redirecionamentos só são consumidos uma vez; rede consultada nos mesmos instantes.
- Comprovantes aguardam coletor sem replay após recebimento; conteúdo e nomes iguais.
- Limpeza da ViewModel cancela timer/downloads; nenhuma nova coroutine independente.

### Task 1: Caracterização

**Files:** teste PrizeRedemptionViewModelTest.kt existente e snapshots em build/verification/prize-viewmodel-extraction-20261018/.

**Interfaces:** somente construtor/métodos/state/receipts públicos existentes.

- [x] Acrescentar casos de timer (refresh/Back/expiração), comprovantes sem coletor e cancelamento ao limpar a ViewModel. Uma mudança no cancelamento ou na fila deve fazê-los falhar.
- [x] Executar `./gradlew :feature:retail:testDebugUnitTest --offline --console=plain` contra original. Esperado: suíte verde. É caracterização para refatoração, sem comportamento novo ou RED artificial.

### Task 2: Extração e validação

**Files:** PrizeRedemptionViewModel.kt; criar PrizeRedemptionUiState.kt, PrizeRedemptionCoordinator.kt, PrizeRedemptionChannels.kt, PrizeRedemptionSubmission.kt, PrizeRedemptionTimer.kt, PrizeRedemptionReceipts.kt, PrizeRedemptionNotices.kt e PrizeRedemptionRedirects.kt no mesmo package; feature/retail/build.gradle (includes aditivos de cobertura).

**Interfaces:** helpers internal recebem MutableStateFlow<PrizeRedemptionUiState>, CoroutineScope e dependências atuais; facade preserva os métodos atuais, incluindo retornos Job/Boolean/String?/PrizeRedirect?. Timer expõe start(Int)/cancel(); redirects expõe blockOfflineLogin(PrizeRedirect):Boolean sem criar dependência circular com notices.

- [x] Extrair corpos e tipos sem reescrever regras; manter inicialização de colaboradores antes do load inicial.
- [x] Comparar assinaturas públicas, tipos realocados, corpos de métodos e fontes protegidas com snapshot.
- [x] Executar `./gradlew :feature:retail:check :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain`. Esperado: build/test/lint/gates e cobertura >=95.01% verdes.
- [x] Revisão independente somente leitura do escopo, sem Gradle concorrente; corrigir achados concretos com prova.
- [x] Atualizar plano e progresso com comandos/resultados reais, sem substituir o relatório histórico da rodada anterior.
