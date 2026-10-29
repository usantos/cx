# Bolões: paridade dos cenários restantes — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** Comparar os cenários alcançáveis de Bolões com o Legado e corrigir divergências demonstradas, sem transações reais.

**Architecture:** Preservar módulos, contratos e alterações existentes. Auditorias independentes somente de leitura; implementação e Gradle/adb serializados no agente principal. Usar fronteiras de serviços controladas para erros e mutações, e consultas autenticadas de leitura no celular.

**Tech Stack:** Kotlin, Compose, Gradle, JUnit, Android instrumentation, Retrofit/Gson, adb.

**Spec:** solicitação atual do usuário; referência principal APK nativo cxv2validation e XML/fontes correspondentes em `/Users/ubiracysantos/Projects/cx`; a cópia antiga é apenas evidência histórica.

## Global Constraints

- Sem apostas, pagamentos ou reservas reais; favoritas reais também não serão alteradas.
- Backend real, Home do Legado, Chucker e logs de rede desativados no celular.
- Preservar checkout dirty e reorganização dos módulos; sem reset, commit, push ou limpeza geral.
- Sem mudanças de regra baseadas em suposição: citar fonte nativa, reproduzir diferença, corrigir e verificar.

## Review Focus

- Filtros conflitantes/limites e todas as modalidades/especiais: mensagens, seleção e query iguais ao Legado.
- Paginação regional, transição e Retry: preservar área/página e resultados, sem duplicar/ignorar solicitações.
- Resposta tardia após Retry/filtro ou troca de sessão: não sobrescrever estado atual nem permitir ação em conta anterior.
- Favoritas e detalhes: confirmação, estados vazios/erro e callbacks; mutações apenas em fixtures.
- Layout em 384dp/450dpi e padrão do emulador: textos, artes, alinhamento e ações acessíveis por rolagem.

---

### Task 1: Matriz e filtros

**Files:** `feature/marketplace-filter/**`, `feature/marketplace/api/**/MarketplaceFilterRules.kt`, testes correspondentes em `core/domain/src/test/**/marketplace` e `feature/marketplace-filter/presentation/src/{test,androidTest}`.

- [x] Enumerar diferenças e cenários equivalentes com referências do Legado; registrar na matriz de evidência.
- [x] Para cada diferença, adicionar teste com entrada e resultado extraídos do Legado e observar RED.
- [x] Corrigir minimamente filtro/query e observar GREEN nos testes afetados.

### Task 2: Consulta, sessão, favoritas e regras

**Files:** `feature/marketplace/presentation/src/main/**/coordinator`, `viewmodel`, `navigation`; `feature/marketplace/data/**`; respectivos testes.

- [x] Comparar progressão município/UF/nacional/lotérica, paginação, Retry, favoritas, detalhes e limites de cotas.
- [x] Reproduzir diferenças de estado/contrato com serviços controlados; observar RED.
- [x] Corrigir causas e observar GREEN, incluindo atraso, cancelamento e sessão.

### Task 3: Layout e estados

**Files:** `core/design-system/src/main/**/CaixaDSMarketplace*`, `feature/marketplace/presentation/src/main/**/components`, `screen`, testes Android correspondentes.

- [x] Comparar layouts nativos e capturas por estado/modalidade, incluindo vazios e falhas.
- [x] Demonstrar diferenças com métricas/capturas ou testes de interação, corrigir e revalidar.

### Task 4: Verificação integrada e celular

**Files:** relatório/matriz em `docs/pools-parity-2026-10-21.md`, evidência em `build/verification/pools-complete-parity-20261021`.

- [x] Rodar suites JVM de marketplace/filter/data/domain e gates de arquitetura, além de compilar APK real e testes Android.
- [x] Rodar cenários instrumentados controlados (incluindo erros, sessão e mutações) no emulador.
- [x] Instalar APK final no celular; autenticar pelo fluxo normal e consultar modalidades, filtros e favoritas sem alterações reais.
- [x] Conferir lista expandida, detalhes e filtros no celular; deixar uma tela útil aberta.
- [x] Revisar mudanças e evidência; registrar limites concretos de cobertura e resultados finais sem prometer paridade universal não medida.

## Ledger

- Ruling: execução contínua autorizada pelo pedido explícito de validar e corrigir; nenhuma confirmação de plano adicional é necessária.
- Ruling: trabalhar no checkout atual, que contém a migração não commitada necessária; um worktree limpo perderia esse contexto. Não commitar alterações do usuário.
- Estado inicial: 35 testes UI marketplace e 252 JVM app/marketplace passaram na rodada anterior; não substituem a verificação desta rodada.
- Ruling: o APK `br.gov.caixa.loterias.apostas.cxv2validation`, usado nas rodadas anteriores, tem prioridade como oráculo executável. A captura válida do detalhe mostrou botão full-width48dp/rodapé3col/aviso de arredondamento, coincidentes com `/Users/ubiracysantos/Projects/cx`; a cópia `/Projects/legado/simlo-app-android` contém XML antigo190×56/rodapé94dp. Todos os achados desta rodada serão reconferidos contra CX/APK antes da integração final. Não alterar os dois checkouts legados.
