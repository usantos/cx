# Favorite bets extraction implementation plan

> Executar inline com superpowers:executing-plans; perfis VoltAgent para análise prévia e revisão final.

**Goal:** separar coordenação/lista/filtros/inclusão/seletores/modalidades preservando ViewModel e comportamento.

**Architecture:** rota mantém owners e decisões; folhas recebem estado e callbacks atuais. Extrair corpos sem corrigir capturas ou redesenhar UI.

**Tech Stack:** Kotlin, Compose, coroutines, JUnit, Android UI, Gradle/Jacoco.

**Spec:** docs/superpowers/specs/2026-10-18-favorite-bets-extraction-design.md.

## Global Constraints

Preservar checkout/trabalho anterior; sem commits/worktrees. Somente FavoriteBetsRoute existente muda na produção. Seis novos arquivos no mesmo pacote. ViewModels, regras, recursos, dependências e gates intactos.

## Review Focus

- showFilter/pending e estados de inclusão conservam owners e chaves originais, inclusive trocando candidato/VM.
- Callbacks externos atuais sem remember; roda mantém efeitos/capturas já existentes no design system.
- Visitante precede loading/lista sem suprimir diálogos; mesma ordem de sucesso/erro.
- Valores/ordem dos concursos e recorrências; habilitação inicial normal único e liberação explícita nos demais casos.
- Chaves id, agrupamento contíguo sem ordenar, política Loteca, layouts e cache Canvas intactos.

## Task 1 — Referência

- [x] Capturar código/hashes e executar check/cobertura/release/três gates offline.
- [x] Análise somente leitura pelo perfil refactoring-specialist.
- [x] Adicionar quatro casos FavoriteBetsCoordinationTest pela rota pública com VM real e executar contra original. Executar também o teste existente de visitante. Esperado: aprovados.

## Task 2 — Extração

- [x] Criar FavoriteBetsContent.kt, FavoriteBetsList.kt, FavoriteBetsFilterDialog.kt, FavoriteBetsAddDialog.kt, FavoriteBetsSelectors.kt e FavoriteBetsModalityComponents.kt conforme especificação.
- [x] Reconstruir corpos e conferir hashes. Atualizar APP_ARCHITECTURE.md. Esperado: só FavoriteBetsRoute anterior mudou.

## Task 3 — Validar e entregar

- [x] Check/lint/cobertura/release/JVM app/APK PRD debug/nove gates, quatro Android da coordenação e um de visitante. Esperado: aprovados.
- [x] Revisão independente com perfil code-reviewer, sem achados relevantes pendentes.
- [x] Whitespace/evidências e emulador restaurado/encerrado.
