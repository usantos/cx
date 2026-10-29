# Betting Catalog Extraction Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: superpowers:executing-plans. Execução inline, uma revisão independente final.

**Goal:** separar coordenação/modalidades/combos/detalhes/cartões/cores mantendo ViewModel/comportamento.

**Architecture:** três APIs públicas conservam coleta/efeito e ligam estado/callbacks a Screens. Componentes/cartões/mapeador recebem corpos existentes sem novo estado ou layout.

**Tech Stack:** Kotlin, Compose, Lifecycle, JUnit, Gradle/Jacoco.

**Spec:** docs/superpowers/specs/2026-10-18-betting-catalog-extraction-design.md.

## Global Constraints

Somente BettingCatalogRoute.kt anterior muda em produção; seis arquivos novos da spec no mesmo pacote. VM/estado/regras/formatadores/recursos/deps/gates intactos. Preservar todas alterações anteriores; sem commits/worktrees/instalação global.

## Review Focus

- Modalidade fechada desabilitada, game ausente selecionável, ID/fallback/arte Lotogol preservados: novo teste de modalidades.
- Linha ímpar mantém spacer/área física de toque e navegação pelo ID; carrinho/vazio: novo teste de combos.
- Quantidade1..15, preço, checkbox !surprise e payload de compra: novo teste de controles.
- Combo nulo/loading/falha não removem rodapé e retry continua no VM atual: novo teste de recuperação.
- Troca de VM não captura métodos antigos; addedToCart não deve ganhar consumo/nova chave e cores não devem ser cacheadas: novos testes troca/sucesso e campanha.

### Task 1 — Caracterização

**Files:** criar BettingCatalogCoordinationUiTest.kt em feature/betting/src/androidTest/java/br/gov/caixa/loterias/feature/betting.

**Interfaces:** três rotas públicas originais e use cases locais em Dispatchers.Main.immediate.

- [x] Capturar original/hashes, aplicar perfil local e rodar baseline check/release/três gates.
- [x] Escrever e executar seis casos de UI contra original, registrar JVM/coverage/XML e dispositivo.

### Task 2 — Extração

**Files:** alterar BettingCatalogRoute.kt; criar LotteryModalitiesScreen.kt, BetCombosScreen.kt, BetComboDetailScreen.kt, BettingCatalogCards.kt, BettingCatalogComponents.kt, BettingCatalogColors.kt.

**Interfaces:** Screens internal recebem estado/callbacks como spec; componentes preservam assinaturas/defaults, exceto private→internal; ComboDetailEntryCard(entry: BetComboEntry) sem wrapper.

- [x] Mover composição e helpers e ligar callbacks do VM atual sem captura/novo estado/efeito/key/layout.
- [x] Reconstruir tokens/literais das rotas e helpers, conferir hashes e registrar arquitetura.

### Task 3 — Verificação final

- [x] Check/lint/cobertura/release/JVM do módulo e seis Android; app JVM/compilePRDrelease/APKPRDdebug e nove gates offline.
- [x] Revisão independente final e whitespace/evidências.
- [x] Pacotes/display preservados e emulador dedicado encerrado.
