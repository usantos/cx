# Payment data extraction implementation plan

> **For agentic workers:** Use superpowers:executing-plans inline, with one fresh-context read-only review at the end.

**Goal:** Separar coordenação, métodos/cartões/Pix e formatadores sem alterar ViewModel/comportamento.
**Architecture:** Rota retém efeitos/decisões/diálogos; conteúdo e folhas recebem estado e callbacks atuais. Cinco arquivos novos de produção, aproveitando helpers existentes.
**Tech Stack:** Kotlin, Compose, coroutines, JUnit, Android Compose UI, Gradle.
**Spec:** docs/superpowers/specs/2026-10-18-payment-data-extraction-design.md.

## Global Constraints

Continuar no checkout codex/migracao-compose, preservando alterações anteriores. Sem commit/worktree novo, dependências, recursos, gates, domínio/VM/restoration/rules ou KMP. API/defaults e prioridades inalterados; callbacks atuais sem remember.

## Review Focus

- Back Pix usa prazo atual e mantém escolha ficar/início; recovery/mismatch preservam prioridade e destino compras, sem gerar outra cobrança.
- Callbacks e três bindings sempre usam o ViewModel atual; notices/redirects/aprovação e sequência dos diálogos continuam na rota.
- Métodos mantêm filtros, expansão local/primeiros dois cartões, open/delete e adição com cancelAddCard antes de onAddCard.
- Cartões mantêm privateDataEnabled, sem novo load, chave método/id e exclusão pelo objeto correto/token do Recarga.
- Pix mantém null guard, chaves expiresAt/purchaseId/copied, intervalo1s/20s e polling condicionado; formatadores mantêm spans, moeda/espaços e parsing/clamp.

## Task 1 — Referência e caracterização

Consumes: fonte atual e testes existentes. Produces: snapshot/hashes, RED unit e baseline UI.

- [x] Salvar referência e executar91 JVM/três gates offline. Expected: sucesso, registrar contagem.
- [x] Criar quatro PaymentDataFormattersTest e observar RED por private nos dois helpers entre arquivos, sem modificar produção.
- [x] Criar sete PaymentDataCoordinationUiTest pela API pública e executar com os14 casos existentes, antes da extração. Expected:21 passam ou falha preexistente registrada.

## Task 2 — Extração

Consumes: snapshot e baseline. Produces: cinco arquivos da spec e rota coordenadora.

- [x] Extrair conteúdo/folhas/formatadores e bindings atuais; manter estado/efeitos da rota e das folhas em seus locais originais.
- [x] Reconstruir/comparar API/coordenador/Scaffold/helpers/lembranças/callbacks contra a referência. Expected: igualdade após reverter extração/binding.
- [x] Rodar95 JVM, debug/release compile e três gates. Expected: GREEN dos quatro novos casos e nenhum hash protegido alterado.

## Task 3 — Validar e entregar

Consumes: extração. Produces: evidências, arquitetura atualizada e mudanças sem commit.

- [x] Reexecutar os mesmos21 Android, feature check/lint/cobertura95.01%, compile release, PRD debug build/unit e nove gates offline. Expected: sem regressões.
- [x] Uma revisão independente somente leitura do escopo local com Review Focus acima. Expected: nenhum problema importante pendente.
- [x] Atualizar arquitetura/evidências, conferir hashes/whitespace, restaurar AVD exatamente e encerrá-lo.
