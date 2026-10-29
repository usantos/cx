# Tickets data routes extraction implementation plan

> Execução inline com `superpowers:executing-plans`; uma revisão independente ao final.

**Goal:** Separar coordenação, listas, detalhe, filtros e consulta manual sem alterar ViewModel/comportamento.
**Architecture:** Cinco rotas públicas mantêm efeitos e decisões; componentes consomem estado e ações tipadas. Efeitos vinculados ao item lazy/painel Pix permanecem em suas posições originais.
**Tech Stack:** Kotlin, Compose, coroutines, JUnit/Compose UI tests, Gradle.
**Spec:** `docs/superpowers/specs/2026-10-18-tickets-extraction-design.md`.

## Global Constraints

- Referência local811 linhas; preservar checkout sujo anterior, API/defaults, ViewModel e estado compartilhado.
- Sem mudanças de regras, recursos, gates, dependências ou KMP; sem commit.
- Bindings por composição, sem guardar ViewModel/closures antigos.

## Review Focus

- privateDataEnabled=false não dispara carga inicial; true já loaded não recarrega.
- Efeito da paginação só existe com o item lazy e preserva chave/predicados.
- Filtros da lista aplicam imediatamente; selecionar no diálogo/cancelar preserva comportamento de estado original.
- Avisos/status/favoritos Pix mantêm destinos/consumo/ordem; polling do painel continua condicionado à cópia/prazo.
- Consulta manual conserva máscara/validade e onResult para sucesso vazio, sem navegar em erro ou introduzir evento novo.

## Task 1 — Referência e caracterização

- [x] Salvar fonte/hashes/classes Android e iniciar baseline JVM/gates.
- [x] Criar cinco casos de coordenação pela API pública e executá-los junto aos 13 casos existentes antes da extração.

## Task 2 — Extrair

- [x] Criar os dez arquivos previstos na spec com os corpos existentes e ações locais tipadas.
- [x] Reduzir as rotas a coleta/efeitos/bindings/avisos e preservar as cinco assinaturas/defaults.
- [x] Comparar coordenação, layouts, callbacks e helpers por reconstrução estrutural.
- [x] Executar JVM/compilação debug/release e gates após a extração.

## Task 3 — Validar e entregar

- [x] Reexecutar os mesmos 18 casos Android no AVD dedicado.
- [x] Executar tickets check/cobertura/lint, release compile, PRD debug build/unit e nove gates offline.
- [x] Obter revisão independente e verificar escopo/whitespace/hash do ViewModel.
- [x] Atualizar arquitetura/evidências; restaurar estado do AVD e encerrá-lo; deixar mudanças sem commit.
