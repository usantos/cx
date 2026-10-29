# RemoteRetailRepository Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox syntax for tracking.

**Goal:** Separar as seis responsabilidades solicitadas sem alterar API, payloads ou comportamento.

**Architecture:** Fachada preserva os três contratos e validação tardia de prizeSource. Cinco colaboradores internos executam as operações e três arquivos de mapeamento mantêm as regras existentes; interfaces remotas no mesmo package.

**Tech Stack:** Kotlin/JVM, Coroutines, Gson/Retrofit/OkHttp existentes, JUnit, JaCoCo.

**Spec:** docs/superpowers/specs/2026-10-18-retail-repository-extraction-design.md; SPEC.md.

## Global Constraints

- Preservar interface pública, payloads e comportamento atual.
- Preservar core/data/build.gradle byte a byte: todas as classes/coroutines já são incluídas, mínimo0.9501.
- Não alterar UI, DI, Domain/Network/Retrofit, persistência ou dependências.
- Continuar checkout codex/migracao-compose preservando trabalho anterior; sem commits/pushes.

## Review Focus

- Favoritos consultados antes de cada busca, sem cache; prioridade codigo/id e parsing recursivo preservados.
- Fonte de prêmio opcional só é exigida na operação, com o mesmo default cast e erro.
- Payloads Pix/Mercado Pago mantêm chaves, valores crus, IDs e ordem de avaliação; comprovantes mantêm bytes/referência.
- Mapeadores conservam precedência de Loteca, zeros/escala decimal/fallbacks, canais e sequência das premiações.
- allowRegistration difere por canal; HTTP/IOException mapeados e demais falhas/cancelamento propagam sem retry.

### Task 1: Caracterização do original

**Files:** core/data/src/test/java/br/gov/caixa/loterias/core/data/RemoteRetailRepositoryTest.kt e snapshots/evidências em build/verification/retail-repository-extraction-20261018/.

**Interfaces:** apenas construtor e métodos públicos do repository/data sources originais; fakes só nos limites de IO.

- [x] Adicionar cinco caracterizações: ordem/frescor/prioridade de favoritos; ausência tardia da fonte; payloads crus/IDs/bytes; cancelamento de todas as operações; valores opcionais/decimais malformados.
- [x] Executar `./gradlew :core:data:test --offline --console=plain`. Esperado: suíte verde no original; sem mudança de comportamento ou RED artificial.

### Task 2: Extração e validação

**Files:** RemoteRetailRepository.kt; criar RetailRemoteDataSource.kt, PrizeRedemptionRemoteDataSource.kt, RetailLotterySearch.kt, RetailFavorites.kt, PrizeRedemptionOperations.kt, MercadoPagoRegistrationLoader.kt, RedemptionReceiptLoader.kt, RetailPayloadMapper.kt, PrizeRedemptionPayloadMapper.kt e RetailJsonValues.kt.

**Interfaces:** helpers internal; RetailFavorites(remote) expõe suspend favoriteIds():Set<Long>, favorites():List<Retailer> e setFavorite(Long,Boolean):Unit; RetailLotterySearch(remote,favorites).search(String):List<Retailer>. Operações de prêmio/cadastro/PDF recebem ()->PrizeRedemptionRemoteDataSource e mantêm as funções públicas equivalentes da fachada. Mappers internos JsonElement?.toPrizeDetails(Long), toLotteryRedemptionCode(), toPixBeneficiary(), prizePixBody(String), mercadoPagoRedemptionBody(String), noticeCode e resultado tipado, sem regras novas.

- [x] Extrair interfaces/operações/mapeadores com guardas de nomes para evitar sobrescritas; preservar validação tardia e corpos/montagem literal.
- [x] Comparar métodos/mapeamentos/APIs compiladas/constructor/interfaces e manter build.gradle idêntico; conferir escopo.
- [x] Executar `./gradlew :core:data:test --rerun :core:data:check :feature:retail:testDebugUnitTest --rerun :feature:payment:testDebugUnitTest --rerun :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain`. Esperado: testes/check/cobertura>=95,01%, compilação/Hilt e gates aprovados. Sem nova execução de instrumentação/matriz global neste refactor JVM.
- [x] Revisão independente somente leitura, sem Gradle concorrente.
- [x] Atualizar arquitetura/progresso/validação e plano com os resultados reais.
