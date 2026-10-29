# Home V2 extraction implementation plan

> Execução inline com superpowers:executing-plans; perfis VoltAgent autorizados para análise prévia e revisão final independente.

**Goal:** separar composição, reordenação, carrossel, cartões, navegação, carregamento e mapeadores sem mudar comportamento.

**Architecture:** receiver extensions preservam acesso dinâmico à apresentação. HomeV2Content e Home continuam com as APIs atuais.

**Spec:** docs/superpowers/specs/2026-10-18-home-v2-extraction-design.md.

## Global Constraints

Preservar checkout e todo trabalho anterior. Sem commits/worktrees/instalação global. Só HomeV2Screen.kt anterior muda em produção, com oito novos arquivos no mesmo pacote. HomeViewModel e helpers de ordem/formatação permanecem intactos.

## Review Focus

Owners e chaves de estado; ordem canônica versus personalizada; callbacks atualizados sem trocar método/lambda; gesto e acessibilidade; temas, layouts, loading e navegação.

## Task 1 — Referência

- [x] Código/hashes, 134 JVM e APK/três gates iniciais; análise read-only refactoring-specialist.
- [x] Adicionar testes Android de coordenação e executar junto dos seis existentes contra o original.

## Task 2 — Extração

- [x] Criar os oito arquivos da especificação, conservar corpos/APIs e documentar arquitetura.
- [x] Comparar corpos/hashes; nenhum outro arquivo anterior de produção alterado.

## Task 3 — Validação

- [x] JVM, Android, lint, release, APK PRD debug e nove gates aprovados.
- [x] Revisão independente final e resolução de achados.
- [x] Evidências, whitespace, restauração e encerramento do emulador.
