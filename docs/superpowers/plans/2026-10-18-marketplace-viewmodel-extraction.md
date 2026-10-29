# MarketplaceViewModel Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox syntax for tracking.

**Goal:** Separar as sete responsabilidades solicitadas sem alterar contratos ou comportamento.

**Architecture:** Fachada dona do estado/init/API e seis colaboradores internos recebendo o mesmo escopo/estado. Paginação conserva filtro e próxima área; loading executa requisições e aplica respostas.

**Tech Stack:** Kotlin, ViewModel/StateFlow/Coroutines, JUnit, Compose UI Test, JaCoCo.

**Spec:** docs/superpowers/specs/2026-10-18-marketplace-viewmodel-extraction-design.md; SPEC.md.

## Global Constraints

- Preservar interface pública, StateFlow e comportamento atual.
- Preservar todos os includes e mínimo0.95; acrescentar colaboradores e coroutines da fachada.
- Não alterar UI, DI, rede, persistência, protocolos ou dependências.
- Continuar checkout codex/migracao-compose preservando trabalho anterior; sem commits/pushes.

## Review Focus

- Paginação por área reseta página apenas ao avançar área; retry volta ao filtro original.
- Cabeçalho usa modalidade/tipo e ignora filtro de lotérica; init/revisões conservam ordem das chamadas.
- Remoção e favoritar compartilham updatingFavoriteLotteryIds, sem atualizar a lista favorita por efeito novo.
- Carrinho congela cotas do pedido em andamento e só confirma reserva válida correspondente.
- Coroutines de todos os colaboradores são canceladas ao limpar ViewModel; nenhum filtro/snapshot é antecipado.

### Task 1: Caracterização do original

**Files:** MarketplaceViewModelTest.kt; snapshots/evidências em build/verification/marketplace-viewmodel-extraction-20261018/.

**Interfaces:** apenas construtor, métodos e StateFlow públicos existentes.

- [x] Adicionar casos de retry após área/falha de append, cabeçalho especial/guards/falha, trava compartilhada dos favoritos, cotas durante carrinho e cancelamento de requisições.
- [x] Executar `./gradlew :feature:marketplace:testDebugUnitTest --offline --console=plain`. Esperado: testes verdes com produção original; caracterização prévia sem RED artificial porque não há comportamento novo.

### Task 2: Extração e validação

**Files:** MarketplaceViewModel.kt; criar MarketplaceLoadingCoordinator.kt, MarketplaceSearchPagination.kt, MarketplaceFavoriteRetailersCoordinator.kt, MarketplacePoolFavoriteCoordinator.kt, MarketplaceSharesCoordinator.kt e MarketplaceCartCoordinator.kt; feature/marketplace/build.gradle (includes aditivos).

**Interfaces:** helpers internal recebem MutableStateFlow<MarketplaceUiState>, CoroutineScope e dependências existentes. MarketplaceSearchPagination(filter) mantém searchFilter/nextArea; reset():Unit e nextPage(snapshot:MarketplaceUiState):Int? fornecem as mesmas transições. Loading expõe search/loadMore/loadContestHeader():Unit. API da fachada mantém todos os Unit/when existentes.

- [x] Mover corpos preservando guards, init, instantes das leituras, retorno e mutações.
- [x] Comparar corpos/contratos/configuração com snapshots anteriores; manter MarketplaceContract byte a byte.
- [x] Executar `ANDROID_SERIAL=emulator-5560 ./gradlew :feature:marketplace:check :feature:marketplace:connectedDebugAndroidTest :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain`. Esperado: JVM/UI/lint/gates/compilação e cobertura>=95% aprovados; condições legadas reportadas.
- [x] Revisão independente somente leitura, sem Gradle/dispositivo concorrente.
- [x] Atualizar arquitetura/progresso/validação, concluir plano e restaurar/encerrar apenas emulador próprio.
