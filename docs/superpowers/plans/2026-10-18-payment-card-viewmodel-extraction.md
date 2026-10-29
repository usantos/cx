# PaymentCardViewModel Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox syntax for tracking.

**Goal:** Separar as sete responsabilidades solicitadas sem alterar contratos ou comportamento.

**Architecture:** Fachada dona do estado/init/API e seis coordenadores internos recebendo o mesmo escopo/estado. Contexto compartilhado mantém a referência única de acompanhamento; tipos públicos permanecem no mesmo package.

**Tech Stack:** Kotlin, ViewModel/StateFlow/Coroutines, JUnit, Compose UI Test, JaCoCo.

**Spec:** docs/superpowers/specs/2026-10-18-payment-card-viewmodel-extraction-design.md; SPEC.md.

## Global Constraints

- Preservar interface pública, StateFlow e comportamento atual, incluindo retorno Job de load.
- Preservar todos os includes e mínimo0.9501; acrescentar colaboradores/contexto/tipos/coroutines.
- Não alterar UI, DI, rede, persistência, protocolos ou dependências.
- Continuar checkout codex/migracao-compose preservando trabalho anterior; sem commits/pushes.

## Review Focus

- Snapshot de seleção/CVV antes de consultar carrinho; submissão bloqueada enquanto em andamento.
- Load retorna Job e verifica guards dentro da coroutine, preservando entradas legadas e cartões reais.
- Polling conserva id/intervalos/contagem, conclusão de protocolo, amount original e retry sem novo POST.
- Avisos por identidade e redirecionamentos por igualdade mantêm pausas/registro/revisão e consumo único.
- Limpar ViewModel cancela carga de cartões, carrinho, envio, delay/consulta e histórico sem mutações tardias.

### Task 1: Caracterização do original

**Files:** PaymentCardViewModelTest.kt e snapshots/evidências em build/verification/payment-card-viewmodel-extraction-20261018/.

**Interfaces:** apenas construtor/métodos/state públicos existentes; fakes só para use cases externos de teste.

- [x] Acrescentar caracterizações de snapshot durante cart/POST, cancelamento de cartões (incluindo Job load), cancelamento cart/POST e delay/consulta de polling, cancelamento do registro local.
- [x] Executar `./gradlew :feature:payment:testDebugUnitTest --offline --console=plain`. Esperado: suíte verde no original; sem comportamento novo ou RED artificial.

### Task 2: Extração e validação

**Files:** PaymentCardViewModel.kt; criar PaymentCardUiState.kt, PaymentCardPurchaseContext.kt, PaymentCardFormCoordinator.kt, PaymentCardSavedCardsCoordinator.kt, PaymentCardSubmissionCoordinator.kt, PaymentCardMonitoringCoordinator.kt, PaymentCardNoticeCoordinator.kt, PaymentCardRedirectCoordinator.kt; feature/payment/build.gradle (includes aditivos).

**Interfaces:** helpers internal recebem MutableStateFlow<PaymentCardUiState>, mesmo CoroutineScope/dispatchers e dependências existentes. PaymentCardPurchaseContext.trackedPurchase:PaymentCardTrackedPurchase? compartilha poll/amount; Redirect possui reviewHandled=false. Monitoring expõe retry():Unit e suspend beginMonitoring(PurchaseSubmission,BigDecimal):Unit; Notice expõe requireNotice/consumos/registro; Redirect expõe requirePurchaseReview/requireRedirect/consumos/resume. SavedCards.load():Job. Fachada mantém fail(AppError):Unit com o corpo original.

- [x] Extrair corpos/tipos e ligar referências preservando leituras/guards/init/retornos e sem inicialização circular.
- [x] Comparar corpos/API/tipos/configuração com snapshots e garantir demais arquivos intactos.
- [x] Executar `ANDROID_SERIAL=emulator-5560 ./gradlew :feature:payment:check :feature:payment:connectedDebugAndroidTest :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain`. Esperado: JVM/UI/lint/gates/compilação e cobertura>=95,01% aprovados, condições legadas reportadas.
- [x] Revisão independente somente leitura, sem Gradle/dispositivo concorrente.
- [x] Atualizar arquitetura/progresso/validação e plano; restaurar/encerrar apenas emulador próprio.
