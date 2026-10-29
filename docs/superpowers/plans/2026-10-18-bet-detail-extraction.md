# Bet Detail Extraction Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: superpowers:executing-plans. Execução inline, uma revisão independente ao final.

**Goal:** separar coordenação/composição/comprovantes/favoritos/repetição/diálogos sem mudar comportamento.

**Architecture:** preservar as três APIs públicas e extrair blocos sem alterar seus donos de estado ou posições de composição.

**Tech Stack:** Kotlin, Compose, Lifecycle, JUnit, Gradle, Jacoco.

**Spec:** docs/superpowers/specs/2026-10-18-bet-detail-extraction-design.md.

## Global Constraints

Checkout e alterações anteriores preservados; sem commits/worktrees/instalações globais. Único arquivo anterior de produção alterado: BetDetailRoute.kt. Criar cinco arquivos no mesmo pacote conforme spec. VM/regras/IO/componentes/recursos/deps/gates intactos.

## Review Focus

- Falha inicial/retry deve usar a aposta hidratada nos callbacks: novo teste de coordenação.
- Troca de VM deve cancelar/substituir coletor de PDF; falha não pode ser silenciosa: novo teste de recibo inválido.
- Favorita com conflito não salva antes de consentimento, permite cancelar o editor e fecha no sucesso: novo teste de favorita.
- Seleção/conversão preserva tipo e callbacks; sucesso consumido antes de atualizar carrinho: novos testes de repetição.
- Redirect consumido antes de navegar, sem repetição em recomposição; Teimosinha mantém chaves/restauração e diálogos mantêm ordem: novo teste redirect + testes existentes + equivalência estática.

### Task 1 — Caracterização

**Files:** criar BetDetailCoordinationUiTest.kt em feature/tickets/src/androidTest/java/br/gov/caixa/loterias/feature/tickets.

**Interfaces:** APIs públicas anteriores e use cases locais com Dispatchers.Main.immediate.

- [x] Snapshot/hashes, análise local com perfil fixado e baseline check/release/gates aprovados.
- [x] Seis casos novos contra original, todos Android do módulo; registrar resultados/assumptions e JVM.

### Task 2 — Extração

**Files:** modificar BetDetailRoute.kt; criar BetDetailScreen.kt, BetDetailReceipts.kt, BetDetailFavorites.kt, BetRepeatActions.kt e BetDetailDialogs.kt.

**Interfaces:** assinaturas públicas/defaults idênticos; helper de recibos recebe ViewModel, favoritos recebem os valores/callbacks existentes, diálogos de coordenação recebem estado/VM/callbacks existentes.

- [x] Extrair blocos conforme spec sem alterar condições/donos de estado/efeitos/layout.
- [x] Verificar reconstrução de tokens/literais/corpos e hashes; registrar arquitetura.

### Task 3 — Verificação final

- [x] Check/cobertura/lint/release e todos Android do módulo; JVM app/compilePRDrelease/APKPRDdebug e nove gates.
- [x] Revisão independente final, whitespace e evidências.
- [x] Pacotes/display preservados e emulador dedicado encerrado.
