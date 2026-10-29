# Marketplace filter extraction implementation plan

> **For agentic workers:** Use superpowers:executing-plans inline, with one fresh-context review at the end.

**Goal:** Separar formulário, modalidades, lotéricas e números sem alterar comportamento/ViewModel.
**Architecture:** Tela pública retém coordenação dos modais; formulário/sections recebem estado e ações existentes. Extração mecânica com comparação contra referência local.
**Tech Stack:** Kotlin, Compose, JUnit, Android Compose UI tests, Gradle.
**Spec:** docs/superpowers/specs/2026-10-18-marketplace-filter-extraction-design.md.

## Global Constraints

Preservar checkout codex/migracao-compose e trabalho anterior. Sem commit/worktree novo, mudanças de ViewModel/regras/reducer/resources/dependencies/gates/KMP. Seis arquivos novos de produção e uma classe Android; API/defaults/remember e layout inalterados.

## Review Focus

- Cancelar/Fechar descartam rascunho sem emitir ação; Confirmar ordena e emite uma única ação antes de fechar; remember mantém chaves originais.
- Quero/não quero bloqueiam números do outro filtro, com conflito dispensável e sem alterar seleção; quantidade única mantém seleção ao tocar novamente.
- Modalidades preservam id+contestType, destaque Todas e artes; Loteca/Super Sete ocultam apenas os seletores já ocultos anteriormente.
- Lotéricas preservam lotteryEnabled, limite de seis sugestões, loading/mensagem e encaminhamento do objeto original; campos/quantidade encaminham ações atuais.
- Scaffold conserva carrinho/Back, prioridade de erros, canApply e Clear; não adicionar validações ou limpeza automática ao mover componentes.

## Task 1 — Referência e caracterização

Consumes: tela atual e infra de testes. Produces: snapshot/hashes, baseline JVM/gates e 17 casos Android.

- [x] Salvar fonte/hashes e executar :feature:marketplace-filter:testDebugUnitTest com três gates offline. Expected: sucesso; registrar contagem.
- [x] Criar MarketplaceFilterCoordinationUiTest.kt com sete casos da spec via tela pública, estado controlado/reducer existente; manter os dez casos existentes.
- [x] Rodar os dezessete casos no AVD dedicado antes da extração. Expected: todos passam ou falha preexistente registrada, sem alterar produção.

## Task 2 — Extração

Consumes: fonte local e baseline. Produces: seis arquivos descritos na spec e coordenador público reduzido.

- [x] Extrair Scaffold e seções mantendo ordem/guards; onOpenNumberDialog apenas substitui atribuição local, sem nova decisão.
- [x] Mover helpers/preview/enum com visibilidade mínima; comparar por reconstrução todos os corpos, API, callbacks e remembers. Expected: igualdade com a referência após reverter extração.
- [x] Executar testes JVM/compile debug-release/três gates. Expected: sucesso e hashes de VM/reducer/state/mapper iguais.

## Task 3 — Validar e entregar

Consumes: extração. Produces: evidências, arquitetura atualizada e código sem commit.

- [x] Reexecutar os mesmos 17 testes Android, feature check/lint/cobertura, compile release, PRD debug build/unit e nove gates offline. Expected: sem regressões versus baseline.
- [x] Revisão independente somente leitura do escopo local com Review Focus acima. Expected: nenhum problema importante pendente.
- [x] Atualizar arquitetura/evidências, verificar hashes/whitespace, restaurar estado original do AVD e encerrá-lo.
