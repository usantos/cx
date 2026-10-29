# Marketplace extraction implementation plan

> Executar inline com superpowers:executing-plans, análise prévia e revisão final usando perfis VoltAgent autorizados.

**Goal:** separar coordenação/abas/paginação/bolões/favoritas/cartões/formatadores mantendo ViewModel e comportamento.

**Architecture:** rota e tela pública preservam declarações; listState continua no conteúdo de bolões. Extrair somente o efeito de paginação e demais corpos existentes.

**Tech Stack:** Kotlin, Compose, JUnit, Android UI, Gradle/Jacoco.

**Spec:** docs/superpowers/specs/2026-10-18-marketplace-extraction-design.md.

## Global Constraints

Preservar checkout/trabalho anterior, sem commits/worktrees. Só MarketplaceRoute existente muda na produção, com sete arquivos novos no mesmo pacote. ViewModel, contratos, carrossel, recursos, dependências e gates intactos.

## Review Focus

- Efeitos da rota e consumo antes da navegação; loading após diálogos.
- listState mesmo owner, efeito antes do when sem condições novas, mesmas chaves/capturas e callback atualizado.
- retailerView/abas/cabeçalho/toolbar e carrinho preservados literalmente.
- Erro de append mantém lista/retry e não inventa footer de esgotamento; mesmas chaves e split carrossel/lista.
- Cálculos, locale, truncamentos e apresentação de prêmio mantidos, sem unificar diferenças do carrossel/cartão.

## Task 1 — Referência

- [x] Capturar código/hashes; check/cobertura/release/três gates offline aprovados.
- [x] Análise prévia somente leitura com perfil refactoring-specialist.
- [x] Adicionar três casos MarketplaceCoordinationUiTest e executar todos os 24 Android contra original. Esperado: aprovados.

## Task 2 — Extração

- [x] Criar MarketplaceScreen.kt, MarketplaceTabs.kt, MarketplacePagination.kt, MarketplacePoolsContent.kt, MarketplaceFavoriteRetailers.kt, MarketplacePoolCard.kt e MarketplaceFormatters.kt conforme especificação.
- [x] Reconstruir corpos, conferir hashes e atualizar APP_ARCHITECTURE.md. Esperado: só MarketplaceRoute anterior alterado.

## Task 3 — Validação e entrega

- [x] Check/lint/cobertura/release, 24 Android sem filtro, JVM app/APK PRD debug e nove gates offline. Esperado: aprovados.
- [x] Revisão independente code-reviewer sem achados relevantes pendentes.
- [x] Whitespace/evidências e emulador restaurado/encerrado.
