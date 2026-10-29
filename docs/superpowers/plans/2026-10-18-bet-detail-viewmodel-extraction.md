# BetDetailViewModel Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox syntax for tracking.

**Goal:** Separar coordenação de detalhes, PDFs, favoritos, repetição e mapeadores sem alterar comportamento.

**Architecture:** Fachada mantém estado/fila/init/API. Quatro coordenadores internos recebem mesmo estado/escopo/dependências. Mapeadores e tipos públicos no mesmo package.

**Tech Stack:** Kotlin, ViewModel/StateFlow/Coroutines, JUnit/Compose UI Test, JaCoCo.

**Spec:** docs/superpowers/specs/2026-10-18-bet-detail-viewmodel-extraction-design.md; SPEC.md.

## Global Constraints

- Preservar interface pública, StateFlow e comportamento atual.
- Preservar todos os includes e mínimo0.95; acrescentar fachada, colaboradores/coroutines/mappers/tipos.
- Não alterar UI, DI, rede, persistência, protocolos ou dependências.
- Continuar checkout codex/migracao-compose preservando trabalho anterior; sem commits/pushes.

## Review Focus

- Init positivo marca loading/detailsReady antes da coroutine; preview não hidrata automaticamente.
- Favoritos confirmam o draft validado uma vez; cancelar invalida a pendência.
- PDF pago usa serviço de prêmio quando disponível, fallback original e FIFO/bytes/nome/cancelamento preservados.
- Repetição mantém colunas, quantidades e complementos; opções/conversão exigem o mesmo consentimento.
- Todos os Jobs continuam no mesmo viewModelScope; falhas tipadas não liberam dados não hidratados.

### Task 1: Caracterização do original

**Files:** BetDetailViewModelTest.kt e snapshots em build/verification/bet-detail-viewmodel-extraction-20261018/.

**Interfaces:** apenas construtor, métodos, state e receipts públicos existentes.

- [x] Acrescentar6casos: cancelamento da sobrescrita, PDF pago sem serviço opcional/fila, mapeamento SuperSete, repetição inválida, PDF preview indisponível e cancelamento de hidratação/download.
- [x] Executar `./gradlew :feature:tickets:testDebugUnitTest --offline --console=plain`. Esperado: suíte verde contra original; sem RED artificial ou comportamento novo.

### Task 2: Extração e validação

**Files:** BetDetailViewModel.kt; criar BetDetailUiState.kt, BetDetailDetailsCoordinator.kt, BetDetailReceiptCoordinator.kt, BetDetailFavoriteCoordinator.kt, BetDetailRepeatCoordinator.kt e BetDetailMappers.kt no mesmo package; feature/tickets/build.gradle (includes aditivos precisos).

**Interfaces:** helpers internal recebem MutableStateFlow<BetDetailUiState>, CoroutineScope, dispatchers/use cases existentes; Receipt recebe a mesma SingleConsumerEventQueue<BetReceipt>. API pública preserva retornos Unit/Job. Mappers internal mantêm expressões existentes; ConfirmedBet.toRepeatDraft(contestType:String):BetDraft contém somente o construtor original.

- [x] Extrair corpos/tipos e mapping, preservando assinatura/defaults/init/owners e instantes das operações.
- [x] Comparar fontes/contratos/constructor mapping com snapshots e configuração de cobertura anterior sem retirar classes.
- [x] Executar `ANDROID_SERIAL=emulator-5560 ./gradlew :feature:tickets:check :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain`. Esperado: JVM/UI obrigatória/lint/gates/compilação e cobertura>=95% aprovados; condições legadas existentes reportadas.
- [x] Revisão independente somente leitura, sem Gradle/dispositivo concorrente.
- [x] Documentar resultados reais, atualizar plano/progresso/arquitetura/validação e restaurar/encerrar só emulador próprio.
