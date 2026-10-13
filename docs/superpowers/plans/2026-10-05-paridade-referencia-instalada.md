# Paridade com a referência instalada Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Usar o APK oficial instalado como referência principal e comparar modalidades, diálogos/estados e compras/pagamento/resgate com dados equivalentes.
**Architecture:** Preservar o host Compose, contratos remotos e ações já implementadas. Extrair recursos públicos do APK instalado, confrontar com screenshots do mesmo aparelho e rastrear regras no checkout Java/XML. Fixtures controladas equivalentes cobrem situações que não podem ser provocadas sem movimentação real.
**Tech Stack:** Kotlin, Compose, Android Views compatíveis, Gradle, ADB, aapt2, JUnit e instrumentação Android.
**Spec:** Pedido do usuário nesta conversa e docs/exact-logged-device-status-2026-10-05.md.

## Global Constraints
- Referência visual principal: br.gov.caixa.loterias.apostas instalado no XCover7; pasta legado complementa rastreamento, sem substituir divergências do binário.
- Mesmo aparelho, density450, fontScale1, estado e dados equivalentes; nunca declarar100% pelo build.
- Não confirmar transação financeira real, apagar dados oficiais, extrair credenciais ou reproduzir PII em fixtures/logs.
- Preservar árvore existente e alterações anteriores; sem commits/reset.
- Somente o coordenador usa aparelho e Gradle. Recursos XML só no inventário explícito; cores novas no CaixaDS.

## Review Focus
- Modalidades com complementos (trevos, mês, time, colunas/esportes).
- Teclado/foco/erro alterando geometria e ações.
- Falha/retorno da compra gerando repetição financeira.
- Pagamento pendente/expirado e resgate sem elegibilidade.
- Estados controlados confundidos com respostas reais ou screenshots oficiais.

### Task1: Fixar referência e corrigir divergências
Files: docs/exact-logged-device-status-2026-10-05.md, feature/home, feature/betting, docs/LEGACY_DRAWABLE_RESOURCES.txt.
Interface: recursos públicos do APK em build/verification/installed-reference-20261005/legacy-installed.apk; imagens oficiais existentes e novas.
- [x] Extrair manifesto/layouts/dimens/drawables e registrar versão/hash.
- [x] Corrigir sino, ajuda e sequência Rapidão conforme binário, preservar fluxos remotos.
- [x] Comparar capturas físicas e instrumentação afetada.

### Task2: Modalidades e diálogos
Files: feature/betting e teste Android da simulação; relatório de matriz específica.
Interface: GetBettingParametersUseCase → BettingGameUiState; callbacks e DTOs atuais preservados.
- [x] Enumerar modalidades/estados/complementos e layouts do APK.
- [x] Corrigir divergências comprovadas e criar cenários controlados sem enviar apostas.
- [ ] Executar todas as modalidades, seletores, manual/surpresa, foco/erro/loading; manter evidência por estado.

### Task3: Conta e estados transversais
Files: feature/account, support, retail/tickets conforme relatório; coordenador integra host/DS.
Interface: ViewModels atuais e capturas privadas sem transcrever valores.
- [ ] Comparar foco/erro/loading e campos inferiores; mapear estado e screenshot correspondente.
- [x] Corrigir métricas/recursos comprovados; preservar validadores e sessão.
- [ ] Executar testes e comparar screenshots.

### Task4: Compras, pagamento e resgate
Files: feature/cart, payment, tickets e fixtures/testes dos contratos correspondentes.
Interface: APIs e estado de compra/resgate atuais; dados equivalentes anonimizados em fixture controlada, sem copiar segredo.
- [x] Rastrear regras/DTOs e construir cenários equivalentes de sucesso/pendente/erro/expiração/elegibilidade.
- [x] Testar navegação e evitar repetição da transação; corrigir achados reais.
- [ ] Validar visualmente e reportar separadamente teste controlado e operação real não executada.

### Task5: Integração e prova
- [x] Compilar Debug/Release e testes afetados, gates de arquitetura/backend/UI.
- [x] Instalar somente candidato paralelo; conferir pacote em primeiro plano antes de screenshots.
- [x] Atualizar matriz com aprovado/divergente/indisponível e artefatos reproduzíveis, sem presumir100%.

## Fechamento da rodada

Itens de comparação integral ficam abertos: foram analisados recursos instalados, executadas fixtures e comparados os recortes físicos documentados. Falta homologação financeira com dados de teste equivalentes e pares oficiais para todos os estados. Release acima significa compilação Kotlin; não foi produzido/distribuído APK de produção assinado.

## Continuação das pendências visuais

Correções,809 testes JVM, gates, capturas controladas e prova real de morte do processo foram consolidados em `docs/exact-logged-device-status-2026-10-05.md`, seção Rodada das pendências solicitadas. Itens de comparação oficial integral permanecem abertos: sessão expirou e não há transações equivalentes. Não marcar execução de fixtures como igualdade100%.
