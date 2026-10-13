# Layout e diálogos de produção — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remover as diferenças comprovadas de raster e métricas dos componentes compartilhados.

**Architecture:** Comparar o binário instalado, corrigir componentes existentes e validar sem substituir contratos por mocks de produção. Plano com execução contínua já autorizada pelo usuário; status por evidência.

**Tech Stack:** Kotlin, Jetpack Compose, widgets Android programáticos, JUnit, AndroidJUnitRunner, ADB.

**Spec:** docs/exact-logged-device-status-2026-10-05.md

**Files:** core/design-system, feature/betting, feature/home.

**Interfaces:** CaixaDSFontFamily, CaixaDSLegacyHtmlDialog, CaixaDSAuthenticationLoadingDialog.

## Global Constraints
- Referência principal: APK de produção instalado no XCover7, pacote br.gov.caixa.loterias.apostas, versão1.24.0/230102400. Fonte legado apenas complementa.
- Preservar checkout e alterações anteriores. Sem reset, commit, push ou reinstalação do oficial. Candidato somente pacote paralelo/debug.
- Não confirmar compra, pagamento, resgate, favorita, cadastro ou alteração cadastral em produção; comparar telas de leitura e usar doubles locais para mutações.
- Apenas coordenador usa ADB/Gradle. Cores/fontes no CaixaDS; sem novos layouts XML de tela.
- Aprovação funcional, comparação física e conteúdo equivalente são resultados separados. Não declarar100% com recortes incompletos.

## Review Focus
- Mensagens/literais/HTML e fontes exatamente como o binário.
- Dados distintos não podem passar por pares equivalentes.
- Teclado/foco/scroll não podem esconder ações ou perder seleção.
- Nenhuma restauração pode repetir mutação financeira ou trocar proprietário.
- Gates existentes e caminhos reais de navegação permanecem íntegros.

### Task1: Medir causas dos recortes divergentes
- [ ] Comparar header, toolbar, cartela, trevos e rodapé com `physical-public-final-pairs-metrics.json`, excluindo estados inválidos.
- [x] Conferir XML/DEX instalado para fontes, dimensões arredondadas e temas herdados, especialmente btnNextStep.
- [x] Criar assertion de propriedade nativa/geométrica que exponha cada diferença e executar RED antes da correção.

### Task2: Corrigir e recapturar
- [ ] Corrigir exclusivamente a origem de cada diferença em DS/componentes, preservando callbacks.
- [ ] Executar JVM afetados, compileDebug/releaseKotlin e gates. Esperado:zero falhas.
- [ ] Executar capturas Android pareadas na mesma densidade/fontScale/estado e registrar diferenças residuais.

## Evidência desta execução

Distribuição inteira das seis colunas, texto nativo das ações, margem da cartela curta do Dia de Sorte, centralização dos meses e títulos canônicos da ajuda receberam regressões antes das correções. Fontes/cores não foram alteradas por aproximação. Os recursos de ajuda da compra nas cinco densidades são idênticos byte a byte ao APK; o ícone de filtro passa a usar o caminho original, em alias restrito à compra.

As capturas equivalentes e métricas estão em `build/verification/production-plans-20261005`. O total do carrinho, a identificação de pagamento e dados pessoais distintos ficam fora dos recortes certificados. O resíduo dos prompts foi localizado: dois paddings8dp arredondam para46px a450dpi, enquanto a margem16dp original mede45px. A imagem se torna idêntica nesse recorte ao deslocar a origem horizontal por1px; fonte e glifos permanecem iguais. A regressão física falhou antes da correção da origem. Tolerância de um canal RGB nos demais recortes não significa igualdade de100%.

Novos pares pessoais mostraram diferença de comportamento do rodapé com teclado e do botão de solicitação de limite. Esses pontos seguem o plano03, sem salvar cadastro no pacote oficial. A posição de favorito e a altura do cabeçalho da compra seguem o plano04.

Compra: Guideline `actionBarSize` é lida com `getDimensionPixelOffset` no ConstraintLayout do APK (trunca), enquanto a altura da toolbar arredonda. A450dpi, conteúdo223px/barra224px; primeiro bloco roxo720px. O scaffold oferece reprodução opt-in dessa regra somente no detalhe da compra. RED15/1 seguido GREEN15/15 em `production-tickets-final2-green-xcover.log`; três probes nativos de fontes/status/data já haviam descartado diferenças de altura dos textos.

### Fechamento da revisão — 06/10/2026

As correções de geometria identificadas por RED foram executadas e passaram nas suítes físicas finais de modalidades (21 casos combinados) e compra (15). As capturas de modalidades foram recalculadas: corpo entre98,20% e99,71% de pixels exatamente iguais; entre99,71% e99,97% dentro de1RGB. Portanto, os recortes ficaram muito próximos, mas a igualdade raster integral ainda não foi atingida. O prêmio R$7 mantém resíduo maior no toolbar/formulário, registrado no relatório final.

Os gates e o build Kotlin Debug/Release passaram. O relatório final consolidado está em `docs/paridade-producao-final-2026-10-06.md`; métricas finais em `build/verification/production-plans-20261005/public-pairs-metrics.json` e `prize-final-raster-metrics.json`. Este registro substitui a pendência de execução dos GREEN anteriores, mas mantém aberta a certificação raster completa.


## Resultado final 06/10/2026

A grade/complementos do grupo de modalidades passou a rodada Android final, junto dos prompts cujo recorte de Dia de Sorte e Lotofácil tem zero pixels diferentes em x/y esperados. A compra finalizada passou 15/15 Android depois de reproduzir a `Guideline` de 56dp truncada no APK e arredondamento da toolbar; os cinco gates de inventário, DS, arquitetura, migração e backend ficaram verdes. Os recortes resolvidos não certificam o restante das telas. No prêmio R$7, a posição do painel foi alinhada e testada; as métricas raster globais seguem abaixo de100% (ver plano04 e `prize-final-raster-metrics.json`).
