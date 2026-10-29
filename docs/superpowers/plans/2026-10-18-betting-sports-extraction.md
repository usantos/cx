# BettingSportsContent Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox syntax for tracking.

**Goal:** Separar apresentação esportiva/callbacks mantendo ViewModel/geometria/comportamento.

**Architecture:** Duas entradas de compatibilidade ligam o mesmo VM a três referências de métodos. Visuais recebem estado/funções; cartões/escudo/medidas movidos literalmente a arquivos por responsabilidade.

**Tech Stack:** Kotlin, Compose, StateFlow/Coroutines existentes, JUnit/Compose UI Test, Android fonts/Bitmap/Base64, JaCoCo.

**Spec:** docs/superpowers/specs/2026-10-18-betting-sports-extraction-design.md; SPEC.md.

## Global Constraints

- Preservar ViewModel, geometria visual e comportamento atual; APIs/defaults internos existentes intactos.
- Preservar feature/betting/build.gradle byte a byte, mínimo0.9501/includes e nenhum teste existente alterado.
- Não alterar Route, recursos, fontes/medidas, Domain/Data/DI/dependências.
- Continuar checkout codex/migracao-compose preservando trabalho anterior; sem commits/pushes.

## Review Focus

- Trocar VM deve atualizar query/seleção/outcomes; callbacks externos de ajuda/quantidade usam versão atual.
- Filtro/label selecionado/grade de três colunas e ausência de times mantêm exatamente os ramos originais.
- Escudo usa chave remember(shieldBase64), aceita raw/dataURI, fallback nulo/inválido e mesmo espaço visual.
- DescriptionBindingKey e remember(team,key) mantêm descrição montada até rebind; não acrescentar selected na chave.
- Font metrics, alinhamento inteiro, ordem de Modifier, Layout/FlowRow e dimensões/fontes/semântica permanecem literais.

### Task 1: Caracterização do original

**Files:** criar BettingSportsCoordinationTest.kt em feature/betting/src/androidTest/java/br/gov/caixa/loterias/feature/betting/; snapshots/evidências em build/verification/betting-sports-extraction-20261018/.

**Interfaces:** somente duas funções internas originais e APIs do VM real; fakes apenas para use cases, sem requests reais.

- [x] Acrescentar três casos: troca do VM para query/seleção, troca do VM/callbacks Loteca/quantidade/ajuda e escudo inválido/raw/dataURI com espaço visual estável.
- [x] Registrar perfil/pacotes do AVD; executar JVM do módulo e instrumentação filtrada para BettingSportsCoordinationTest, BettingTimemaniaSearchTest, BettingLotecaParityTest e BettingNativeGeometryTest contra original. Esperado: baseline verde sem alterar oráculos; reportar eventual condição/falha real.

### Task 2: Extração e validação

**Files:** BettingSportsContent.kt; criar BettingTeamSelection.kt, BettingLotecaContent.kt, BettingLotecaOutcomeCard.kt, BettingTeamShield.kt, BettingSportsMetrics.kt e BettingSportsCallbacks.kt.

**Interfaces:** BettingSportsCallbacks(viewModel) fornece onQueryChanged:(String)->Unit, onTeamSelected:(SportsTeam)->Unit e onOutcomeSelected:(Int,LotecaOutcome)->Unit. BettingTeamsContent(state,onQueryChanged,onTeamSelected); BettingLotecaContent(state,onOutcomeSelected,onQuantity,onHelp,descriptionBindingKey). TeamShield/metrics/OutcomeCard internos conservam argumentos/defaults/corpos.

- [x] Mover corpos e trocar apenas referência dos callbacks; manter VM/Route/recursos/build/testes existentes intactos.
- [x] Comparar corpos/constantes/chaves/assinaturas/ligação e APIs compiladas; conferir escopo.
- [x] Executar `ANDROID_SERIAL=emulator-5560 ./gradlew :feature:betting:check :feature:betting:connectedDebugAndroidTest :app:compilePrdDebugKotlin verifyKotlinFormatting verifyCleanArchitecture verifyComposeMigration --offline --continue --console=plain` com argumento de instrumentação das quatro classes da baseline. Esperado: mesma seleção UI e JVM/cobertura/lint/gates/compilação aprovados.
- [x] Revisão independente somente leitura sem Gradle/ADB concorrentes.
- [x] Atualizar docs/plano e restaurar/encerrar só o emulador desta rodada.

Validação final:139JVM/24UI480dpi aprovados;check/lint/PrdDebug/gates e20checks de equivalência aprovados. Cobertura98,339483%,configuração intacta. Original450dpi21/24;mesmas três falhas reproduzidas no final com valores/mensagens idênticos, sem alterar oráculos. Revisão independente sem achados;AVD próprio restaurado e encerrado. Detalhes/evidências em MIGRATION_VALIDATION.md.
