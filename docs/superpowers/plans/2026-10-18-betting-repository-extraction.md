# RemoteBettingRepository Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox syntax for tracking.

**Goal:** Separar responsabilidades sem alterar API, payloads ou comportamento.

**Architecture:** Fachada com quatro colaboradores de operações e cinco arquivos de mapeadores internos. Mesma fonte em todos os loaders; parâmetros controlam a consulta opcional de escudos, mapper recebe DTO+mapa existentes.

**Tech Stack:** Kotlin/JVM, Coroutines, Gson/Retrofit/OkHttp existentes, JUnit, JaCoCo.

**Spec:** docs/superpowers/specs/2026-10-18-betting-repository-extraction-design.md; SPEC.md.

## Global Constraints

- Preservar interface pública, payloads por modalidade e comportamento atual.
- Preservar core/data/build.gradle byte a byte: todas as classes/coroutines, mínimo0.9501.
- Não alterar UI, DI, Domain/Network/Retrofit/DTOs, persistência ou dependências.
- Continuar checkout codex/migracao-compose preservando trabalho anterior; sem commits/pushes.

## Review Focus

- Números/colunas Super Sete, zeros e quantidades explícitas são enviados literalmente; opcionais nulos continuam omitidos.
- Loteca/Lotogol/time do coração conservam campos, flags, placares string e omissão de escudos/legendas na requisição.
- game?:nextGame, hasNextContest e consulta condicional/ordem/último escudo por ID são preservados.
- Parâmetros/pools/combos/modalidades mantêm defaults/case/identidades/valores/escala, sem normalização nova.
- Falhas propagam sem retry; somente a consulta opcional de escudos mantém o catch amplo original, inclusive cancelamento recebido.

### Task 1: Caracterização do original

**Files:** criar core/data/src/test/java/br/gov/caixa/loterias/core/data/RemoteBettingRepositoryTest.kt; snapshots em build/verification/betting-repository-extraction-20261018/.

**Interfaces:** somente API pública do repository e fonte; capturar DTO no limite IO e serializar com Gson real, comparar JSON completo a literais independentes.

- [x] Cobrir payload numérico por códigos/tipos, payload completo esportes/Super Sete/mês/espelho, prioridade/consulta de escudos e falhas do fallback, catálogo/combos, parâmetros/pools e propagação de falhas; sem editar testes existentes.
- [x] Executar `./gradlew :core:data:test --offline --console=plain`. Esperado: suíte verde no original, sem RED artificial ou comportamento novo.

### Task 2: Extração e validação

**Files:** RemoteBettingRepository.kt; criar BettingModalitiesLoader.kt, BettingParametersLoader.kt, BettingCombosLoader.kt, BettingSubmission.kt, BettingModalityMapper.kt, BettingParametersMapper.kt, BettingComboMapper.kt, BettingSportsMapper.kt e BettingRequestMapper.kt.

**Interfaces:** helpers internal com a mesma BettingRemoteDataSource. Mappers internos: LotteryModalityDto.toBettingModality(), bettingModalityCode(Int), bettingModalityId(String?), String?.displayBettingModalityName(), BettingParametersDto.toBettingParameters(Map<Int,String?>), LotteryGameParameterDto.toBettingGame(Map<Int,String?>), BetComboDto.toBettingCombo(), SportsTeamParameterDto.toBettingTeam(Map<Int,String?>), SportsMatchParameterDto.toBettingMatch(Map<Int,String?>), SportsTeam.toBettingTeamRequest(Boolean?=null,String?=null), BetDraft.toAddBetRequest():AddBetRequestDto; mês privado no RequestMapper. APIs da fachada inalteradas.

- [x] Extrair corpos/mapeamentos com guardas de nomes; sem modificar a avaliação do remote antes de construir argumentos.
- [x] Comparar corpos/API/constructor completo do DTO e manter build.gradle, RemoteDataSources e testes existentes idênticos; conferir escopo.
- [x] Executar `./gradlew :core:data:test --rerun :core:data:check :feature:betting:testDebugUnitTest --rerun :feature:cart:testDebugUnitTest --rerun :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain`. Esperado: JVM/check/cobertura>=95,01%, compilação/Hilt e gates aprovados; sem nova instrumentação/matriz global neste refactor JVM.
- [x] Revisão independente somente leitura, sem Gradle concorrente.
- [x] Atualizar arquitetura/progresso/validação e plano com resultados reais.
