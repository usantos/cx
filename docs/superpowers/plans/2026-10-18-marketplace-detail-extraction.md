# Marketplace Detail Extraction Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans. Executar inline, uma revisão independente ao final.

**Goal:** separar oito responsabilidades mantendo ViewModel/comportamento.

**Architecture:** coordenador conserva efeitos; Screen conserva owner/hierarquia da lista e painel fixo. Sete arquivos recebem corpos e blocos existentes sem novos estados/efeitos.

**Tech Stack:** Kotlin, Compose, Lifecycle, JUnit, Android UI, Gradle/Jacoco.

**Spec:** docs/superpowers/specs/2026-10-18-marketplace-detail-extraction-design.md.

## Global Constraints

Preservar checkout/trabalho anterior, sem commits/worktrees/configuração global. Só MarketplaceDetailRoute.kt anterior muda em produção, sete novos arquivos no pacote. ViewModel/Contract/Carousel/recursos/dependências/gates intactos.

## Review Focus

- Confirmation consumida antes da navegação, callbacks após troca VM e efeito sem nova key: novo teste coordenação/reserva.
- Lifecycle init GET único e refresh posterior: novo teste lifecycle/falha/retry.
- Favorita excluir/cancelar/falhar/dismiss/retry e loading depois dos diálogos: novo teste favorita e testes existentes de input físico.
- LazyColumn mesmo owner/itens, primeira aposta Loteca e grades especiais/tags: testes existentes e reconstrução literal.
- Painel fixo/limites/valores/badge/cart nulo e dados opcionais: teste novo limites e integração; corpos preservados e testes de geometria existentes.

### Task 1 — Referência

**Files:** novo MarketplaceDetailCoordinationUiTest.kt em feature/marketplace/src/androidTest/java/br/gov/caixa/loterias/feature/marketplace.

- [x] Capturar código/hashes e aplicar refactoring-specialist localmente;34JVM/check/lint/cobertura/release/três gates aprovados.
- [x] Acrescentar três casos da spec e executar todos27Android sem filtro contra original. Esperado: aprovados, hashes anteriores intactos.

### Task 2 — Extração

**Files:** MarketplaceDetailRoute.kt e sete novos arquivos da spec.

**Interfaces:** assinaturas/defaults e visibilidades conforme spec, sem novo modelo.

- [x] Mover declarações/extrair blocos mantendo efeitos/owners/hierarquia.
- [x] Comparar tokens/literais, reconstruir Screen/Route, conferir hashes e adicionar um parágrafo em APP_ARCHITECTURE.md.

### Task 3 — Verificação

- [x] Check/cobertura/release/todos27Android do módulo;134JVM app/compilePRDrelease/APKPRDdebug e nove gates offline aprovados.
- [x] Revisão independente final sem achados relevantes; whitespace/evidências.
- [x] Pacotes/display originais confirmados, emulador dedicado encerrado.
