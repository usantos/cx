# Rapid configuration extraction implementation plan

> **For agentic workers:** Use superpowers:executing-plans inline, with one fresh-context read-only review at the end.

**Goal:** Separar coordenação, pager, etapas, resumo e formatadores sem alterar comportamento ou ViewModel.
**Architecture:** Rota mantém estado/efeitos/diálogos. Conteúdo e etapas recebem estado/callbacks atuais, mantendo estado local e sincronização em seus owners originais.
**Tech Stack:** Kotlin, Compose, coroutines, JUnit, Android UI, Gradle.
**Spec:** docs/superpowers/specs/2026-10-18-rapid-configuration-extraction-design.md.

## Global Constraints

Checkout atual codex/migracao-compose e alterações anteriores preservados. Sem commit, worktree novo, recursos/dependências/gates/VM/domínio modificados. API, efeitos/keys, sete etapas e favoritos auxiliar sem nova ligação.

## Review Focus

- Dots distantes, swipe e pedido interrompido: nunca publicar página intermediária; callback atualizado via rememberUpdatedState e chaves originais.
- Visitar resumo libera Salvar também no retorno a etapas; loading/canSave ainda controlam habilitação e saved ainda entrega onSaved.
- Edição de valor mantém remembers locais, limites/presets/parser/validação e cancelamento sem aplicação.
- Números/modalidades mantêm exclusão mútua/clear e destinos de edição do resumo; favoritos não viram nova etapa.
- Trocar VM com mesma composição liga ações atuais sem remember e preserva os owners de estado da tela.

## Task 1 — Caracterização

Consumes: fonte atual e testes existentes. Produces: snapshots e baseline verificável.

- [x] Registrar baseline JVM e três gates offline. Expected: sucesso ou falha anterior nomeada.
- [x] Adicionar três contratos RapidConfigurationFormattersTest (números, colunas, complementos), rodar e observar RED por rapidFavoriteNumbers private.
- [x] Adicionar UI da rota pública/VM real e callback atualizado do pager, executar com RapidPagerNavigationTest antes de alterar produção. Expected: mesmos cenários aprovados após extração.

## Task 2 — Extração

Consumes: snapshot/baseline. Produces: oito novos arquivos de produção da spec.

- [x] Extrair conteúdo/actions, pager, etapas, componentes, resumo e formatter; coordenação permanece na rota.
- [x] Reconstruir e comparar cada declaração/API/coordenador/Scaffold com snapshot, revertendo somente bindings/visibilidade. Expected: equivalência, VM e todos os Kotlin anteriores exceto rota byte-idênticos.
- [x] Rodar JVM/debug/release compile e três gates. Expected: GREEN dos três contratos novos e suite anterior aprovada.

## Task 3 — Validação e entrega

Consumes: extração. Produces: arquitetura/evidências/revisão.

- [x] Rodar mesma seleção Android, feature check/lint/cobertura95.01%, release compile, PRD debug build/unit e nove gates offline. Expected: sem regressões, relatar falhas preexistentes pelo nome.
- [x] Revisão independente somente leitura comparando snapshot, spec, testes e Review Focus. Expected: nenhum problema importante pendente.
- [x] Atualizar arquitetura/registro, conferir hashes/whitespace, restaurar pacotes/display do AVD dedicado e encerrá-lo.
