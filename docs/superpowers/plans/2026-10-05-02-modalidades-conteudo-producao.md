# Modalidades e conteúdo de produção — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Completar a matriz das modalidades e conteúdo público nos mesmos concursos.

**Architecture:** Comparar o binário instalado, corrigir componentes existentes e validar sem substituir contratos por mocks de produção. Plano com execução contínua já autorizada pelo usuário; status por evidência.

**Tech Stack:** Kotlin, Jetpack Compose, widgets Android programáticos, JUnit, AndroidJUnitRunner, ADB.

**Spec:** docs/exact-logged-device-status-2026-10-05.md

**Files:** feature/betting, feature/results, feature/support.

**Interfaces:** BettingGameUiState, LotteryGameParameters, ExtendedInstalledPairsTest(publicBettingFixtures).

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

### Task1: Completar referência pública
- [ ] Capturar Dia de Sorte, Dupla Sena, Quina estável, Lotofácil sem ripple, espelho Lotomania no mesmo scroll e Loteca quando aberta.
- [ ] Registrar mês/time/trevos/colunas/partidas, preços+Mil12n/Super13–21 e HTML Como jogar completos somente quando observados.
- [x] Verificar disponibilidade de Federal na navegação atual de resultados: não oferecida nas10modalidades observadas; conteúdo de eventual destino permanece condicional ao backend.
- [ ] Comparar conteúdo de ajuda/termos com navegação de produção.

### Task2: Ajustar apresentação/contratos encontrados
- [ ] Reproduzir divergência de UI/DTO com teste local antes de corrigir; não inventar dados indisponíveis.
- [ ] Corrigir somente feature/betting/results/support conforme ownership do coordenador.
- [ ] Executar JVM/Android existentes e novos com zero envios; guardar screenshot, foco e snapshot público por estado.

## Auditoria de apresentação e regras — 05/10/2026

Referência estável: `physical-public-final-pairs-metrics.json` e screenshots em `build/verification/remaining-installed-20261005/betting-final`. Metadata dos pares confirma viewport1080px, densityDpi450, fontScale1.0.

- [x] Rastrear grade numérica comum: `SimulaFragment.configuraListaDezenas` (fonte auxiliar1276; DEX classes15, método104610) usa `GridLayoutManager` com6colunas; `item_dezena.xml` usa ToggleButton45dp e padding2dp horizontal/4dp vertical. O candidato conserva o widget nativo, mas `BettingGameRoute.BettingLegacyNumberGrid` divide `maxWidth/6` e usa FlowRow, perdendo a distribuição do resto inteiro. Na +Milionária396, bordas raster oficiais:77–200,233–356,389–512,546–669,702–825,858–981; candidato:77–200,233–356,389–512,545–668,701–824,857–980. Primeira coluna conserva todos os y720/869/1018/1167/1316/1465/1614. O teste `BettingInstalledGridGeometryTest.commonBallsRetainInstalledSixColumnRemainderDistribution` exige deltas oficiais0/156/312/469/625/781 e largura nativa127px, distinguindo o raster124px do drawable45dp. RED físico e correção ainda dependem do coordenador.
- [x] Rastrear fonte da ação seguinte: manifesto instalado liga `SimulaActivity` ao tema0x7f140398 `Theme.SimulaMaterial`; resource50787 herda `Theme.MaterialComponents.Light.NoActionBar`; `Widget.MaterialComponents.Button` resource54136 usa `textAppearanceButton`; `TextAppearance.MaterialComponents.Button` resource48975 herda `Base.TextAppearance.MaterialComponents.Button`, resource43620, com14sp. `rodape_simula/btnNextStep` omite tamanho; `btnAdicionarCarrinho` declara13sp. DEX classes15, `renderEstadoBotoes`40396–40595 muda enabled/literal/tint/typeface, não tamanho; `fonteBotaoBold` somente fonte. Candidato passa13sp para ambos por default. Teste `nextStepInheritsFourteenSpWhileAddRetainsExplicitThirteenSp` preparado; nenhuma correção de fonte antes do RED físico.
- [x] Investigar trevos97.1824%: recorte292/1045/791/1380 tem4710pixels distintos de167165, mas erro máximo absoluto por canalRGB é1. Formas, texto e posições coincidem. Não há evidência para mover os trevos ou trocar a fonte; registrar resíduo de rasterização/antialias separadamente e não chamar a imagem de100%.
- [x] Encontrar ajuda vazia em IDs reais: `BettingGameRoute` chama `legacyBettingHelp(game.modality.id)`; o mapa antigo tinha somente3/16/13 para Quina/Lotomania/Loteca, mas DTOs reais usam1/5/19 (`RemoteBettingRepository`, `RemoteRetailRepository` e fixtures públicas). `BettingInstalledHelpTest` falhou3/3 no RED do coordenador em `/tmp/production-plans-red-build.log`. Após autorização foram acrescentados aliases1/5/19 mantendo3/16/13 e os literais. GREEN pendente do coordenador. A captura de ajuda existente apenas provava abertura, não conteúdo; esse limite não é conclusão de paridade.

### Modalidades ainda sem par físico completo

Dia de Sorte11: números/complemento/Surpresinha e mês escolhido já têm lógica moderna. Fonte instalada/auxiliar `MesesAdapter` usa `ItemRetanguloTextView`, `item_retanglulo_texto`14sp/padding12dp, selector activated com raio4dp/borda1dp, regular→semiBold e alpha200ms; `fragment_meses` tem2colunas, horizontalSpacing14dp, verticalSpacing16dp e margens8dp. `LuckyMonthContent` reproduz estrutura básica, mas usa Row/Column Compose para distribuição/altura em vez de GridView; precisa snapshot oficial do mesmo concurso/meses/preços e captures estáveis para afirmar divergência. Não inventar fixture pública a partir de meses sintéticos.

Dupla Sena18: `RemoteBettingRepository` confirma ID; mapa HTML18 é preenchido. É grade numérica comum; a divergência de distribuição pode atingir essa modalidade. Concurso/preços/quantidades/recorrência observados e imagens manual/selecionada/Surpresinha/ajuda ainda necessários para pareamento físico. Fixture sintética não supre essa evidência.

Loteca19: renderer específico e validação de todos os jogos/ao menos duplo ou triplo já existem. Referência1274 atualmente mostra concurso não aberto; aviso e partidas públicas visíveis são auditáveis, mas seleções/transições completas permanecem indisponíveis. Não usar `legacy-loteca-selected.png` como seleção; é o aviso de indisponibilidade. Ajuda pelo ID19 foi corrigida após RED; preços da matriz/partidas adicionais/selection real ainda dependem de concurso aberto e fonte observada.

Ajuda: HTML de produção é verificável no DEX `AccordionModel` classes10, literals em torno de01d448. A renderização modal DS tem ownership do coordenador; esta tarefa conserva texto e fluxo de fechamento. Completar pares após corrigir ajuda1/5/19 e após obter mesmos concursos de Dia/Dupla; não afirmar que abertura isolada comprova texto, scroll ou raster completo.

A extração direta `aapt2 dump xmltree` do APK primário confirmou os mesmos atributos em `/tmp/production-betting-item-installed.txt` (ToggleButton45dp; padding pai2/4dp; fonte Caixa bold; nenhum tamanho de texto explícito) e `/tmp/production-betting-rodape-installed.txt` (btnAdicionarCarrinho0x7f0a00d2 com13sp; btnNextStep0x7f0a00fc sem tamanho explícito). São evidências binárias de leitura; não constituem build ou execução física.

### Correções autorizadas após RED físico

Coordenador confirmou `/tmp/production-betting-red-xcover.log`:2testes/2falhas esperadas, tamanho39px esperado/37px atual e deltas oficiais0/156/312/469/625/781 versus0/156/312/468/624/780. Após autorização, `BettingLegacyNumberGrid` passou a medir células por bordas inteiras com a mesma distribuição de resto do GridLayoutManager; widgets, drawable, fonte, semântica de clique e restauração checked após clique rejeitado permanecem no mesmo fluxo. Ação seguinte usa14sp; Adicionar usa13sp. Tamanho é atualizado na view nativa a cada recomposição, pois o mesmo AndroidView permanece vivo na transição Trevos→Adicionar. GREEN físico/recaptura pendentes do coordenador.

`ExtendedInstalledPairsTest` agora verifica trecho instalado específico da modalidade no TextView real da ajuda antes da captura, fechando a lacuna de abertura sem conteúdo. Aceita `selectedMonth` no JSON para conservar o mesmo mês do par sem reordenar a lista. Dia/Dupla usam o argumento existente `publicBettingFixtures`; devem receber concurso/data/meses e `prices` explícitos observados. Sem `prices`, a utility ainda deriva preços combinatórios e essa matriz não pode ser apresentada como todos os preços observados.

### Novo GREEN e pares Dia/Dupla

Coordenador confirmou `/tmp/production-betting-green-xcover.log`:15/15, incluindo distribuição inteira da grade, fontes Next/Add e regras de ajuda. Isso valida os testes executados; não certifica apresentação dos meses. Novas fixtures públicas equivalentes estão em `build/verification/production-plans-20261005/public-betting-fixtures.json`: Dia de Sorte11/concurso1316/data06/10/2026, meses1–12/Jan selecionado e preços7–15 observados; Dupla Sena18/concurso3018/data07/10/2026, preços6–15 e recorrência0/2/3/4/6/8/9/12 observados.

O par `legacy-dia-complement-selected.png` versus `betting-green/compose-installed-11-1316-complement-selected.png` mostra desvio concreto: rótulos dos meses ficam à esquerda no candidato e centralizados no instalado. APK primário `aapt2 dump xmltree item_retanglulo_texto` confirma TextViewMATCH_PARENT/textAlignmentCENTER4/padding12dp/14sp e não declara gravity. DEX classes10 `ItemRetanguloTextView.setLayout`0460f8 somente aplica texto/activated/typeface/animação, sem setGravity. Não atribuir gravity17 ao original sem evidência. `BettingLuckyMonthAlignmentTest.selectedAndUnselectedMonthLabelsDrawAtTheirCellCenters` exige paragraph alignmentCENTER e centro da linha de texto no centro da célula, verificando estados Janeiro→Fevereiro/preço preservado; diagnóstico inclui gravity, textAlignment, width e layoutWidth. Teste preparado antes da correção; RED físico e autorização pendentes do coordenador.

RED físico do alinhamento confirmado pelo coordenador:1/1 falha, Janeiro `gravity8388659`, `textAlignment1`, `width426`, `layoutWidth358`, paragraphALIGN_NORMAL. A inicialização AndroidView havia convertido textAlignmentCENTER4 configurado no factory em INHERIT1 antes de renderizar. Após autorização, a atribuiçãoCENTER passou ao callback update de `LuckyMonthContent`, reproduzindo o atributo XML após attachment. Não foi acrescentada gravity que o original não declara. Nova execução GREEN/captura física pendentes.

Título da ajuda também diverge no par Dia1316: original `Como Jogar: dia de sorte`, candidato `Como Jogar: Dia de Sorte`. DEXclasses10 `AccordionModel.getTituloComoJogar`01d380 concatena `Como Jogar: ` com `ModalidadeEnum.fromString`; DEXclasses5fromString04099c retorna `dia de sorte`040a24. Os nomes canônicos incluem `mega-sena` (hífen), `super sete` e `+milionária` do recurso0x7f1303f3; simplesmente lowercasing do DTO não cobre todas as variantes. Teste Android `BettingLuckyMonthAlignmentTest.diaHelpUsesTheInstalledLowercaseModalityTitle` preparado; nenhuma correção de título antes do RED.

A tentativa de aplicar textAlignmentCENTER no update não resolveu: `/tmp/production-month-title-red-xcover.log` confirmou2/2 falhas, com alinhamento aindaINHERIT1/ALIGN_NORMAL e título Dia com capitalização diferente. O wrapper AndroidView impõe INHERIT depois do callback update. Após nova autorização, `LuckyMonthContent` aplica Gravity.CENTER como adaptação compatível com o posicionamento central observado, mantendoCENTER explícito; não se afirma que gravity17 consta no XML instalado. Fonte e callbacks permanecem iguais. O título passa a `legacyBettingHelpTitle`, mapa de IDs reais→nomes exatos de ModalidadeEnum.fromString, incluindo `mega-sena` e `+milionária`. `BettingInstalledHelpTitleTest` cobre todos os nomes/variantes de DTO e fallback desconhecido. Ambos GREEN físicos e nova captura dos meses ainda pendentes do coordenador.

### Origem vertical da cartela Dia1316

Novo par físico mostrou primeira bola Dia instalada emy765 e candidata emy720; última linha31 instalada emy1510. Lotofácil3798 conserva inícioy765 em ambos. A fonte atual candidata adiciona a margem16dp de `cltGeral` somente ao ID8, omitindo Dia11. Extração do APK em `/tmp/production-betting-cartela-installed.txt` confirma `fragment_cartela_novo`: NestedScrollViewfillViewport=true, `cltGeral`marginTop16dp, RecyclerViewwrap_content. Não há gravidade/centralização vertical explícita na grade; o desvio corresponde à margem do ticket curto observado. Os testes físicos novos `BettingInstalledGridGeometryTest.diaManualGridRetainsTheInstalledShortTicketTopMargin` e `lotofacilManualGridKeepsItsAlreadyMatchingShortTicketTopMargin` verificam origem inicial/final no viewport1080/450dpi, manual e selecionado. RED Dia/controle Lotofácil e autorização de correção pendentes do coordenador; fontes e tamanhos não devem mudar nessa correção.

Coordenador confirmou RED `/tmp/production-dia-short-grid-red-xcover.log`:4testes/1falha esperada, Dia primeira bola765 esperado/720 atual; controle Lotofácil e regressões anteriores GREEN. Após autorização, a mesma margem16dp já aplicada em Lotofácil8 foi estendida a Dia11; nenhuma mudança de fonte/tamanho/grade/callback. As modalidades longas conservam origem já pareada. Produção congelada para build/GREEN completos e recapturas do coordenador.

Resíduo após análise coordenada: prompts comuns apresentam diferenças de rasterização na faixa y649–671; Quina/Lotofácil/Dupla alcançam99.733% de pixels do corpo dentro de tolerância1RGB. Esse dado é diagnóstico e não autoriza mover textos ou trocar fontes sem rastrear atributos nativos. Não apresentar tolerância, teste funcional ou matriz parcial como100%.

### GREEN completo e diagnóstico final do prompt

Coordenador confirmou `/tmp/production-betting-final-green-xcover.log`:15/15 (geometry4, month/title2, Extended8 e Smoke1), com recapturas em `build/verification/production-plans-20261005/betting-final`. `public-pairs-metrics.json`: Dia corpo89%→98.388% exato/99.706% dentro de1RGB; meses99.443% exato/99.456% dentro de1; meses selecionados99.447%/99.460%. Essas métricas delimitam os pares observados e não são certificação global.

O resíduo do prompt comum foi isolado sem alteração de fonte/renderer: crop80/610/900/710, original Dia bbox95/633/543/671 e candidato96/633/544/671. Deslocar somente a amostragem do candidato em+1pxX torna todos os pixels do recorte exatamente iguais (erro RGB total0); a diferença é origem horizontal, não antialias. Lotofácil/Quina/Dupla estáveis confirmam os mesmos bboxes. APK `activity_simula_aposta`/TextViewtextSelecionados usa uma margemStart16dp; candidato soma paddingHorizontal8dp do container a paddingStart8dp do Text. Em450dpi, round8dp=23px, duas parcelas46px, enquanto round16dp=45px. Fonte Caixa regular16sp/cor/altura e glyph raster já coincidem. O helper TextView privado de `BettingSportsContent` não é necessário para essa divergência demonstrada.

Novo RED `BettingPromptOriginTest.diaPromptStartsAtTheInstalledSingleMarginRasterOrigin` e `lotofacilPromptStartsAtTheInstalledSingleMarginRasterOrigin` verifica screenshot público isolado com bbox original e guardas viewport1080×2408/density450/fontScale1. Nenhuma correção adicional de origem antes da execução RED/autorização do coordenador; plano físico anterior15/15 continua distinguido dessa nova pendência.

Coordenador confirmou `/tmp/production-betting-prompt-red-xcover.log`:2/2 falhas esperadas, origemX95 esperado/96 atual. Após autorização, `legacyPromptInset` calcula `round(16dp)−round(8dp)` pela LocalDensity, conservando o padding8dp do container e todas as bordas da grade. Manual/números, complemento e SuperSete aplicam esse restante ao prompt, reproduzindo a única margem16dp do TextViewtextSelecionados instalado. Não houve mudança de fonte/glyph/cor/callback. Produção congelada novamente para GREEN dos15 anteriores+2prompt e recapturas; não reclassificar as métricas anteriores como resultado dessa correção ainda não executada.


### GREEN final após correção da margem do prompt

Log lido nesta revisão: `/tmp/production-betting-final2-green-xcover.log` termina em `OK (17 tests)` (geometry4, month/title2, Extended8, Smoke1 e prompt2). Os4 testes anteriores de diálogo/Wheel/Loading não tiveram código alterado e permanecem na evidência anterior do coordenador:21 testes únicos nesta rodada, sem apresentar21 como uma única execução deste log.

- [x] Corrigir aliases da ajuda nos IDs reais1/5/19 após RED JVM, conservando3/16/13 e os literais.
- [x] Corrigir distribuição inteira das6colunas após RED físico, preservando seleção e clique rejeitado.
- [x] Corrigir Next14sp/Adicionar13sp após RED físico e atualização da view reutilizada.
- [x] Centralizar meses observados de Dia1316 após RED físico, com seleção Janeiro→Fevereiro e preço preservado.
- [x] Reproduzir título canônico instalado da ajuda por modalidade após RED, incluindo hífen de mega-sena e +milionária.
- [x] Restaurar margem superior16dp da cartela curta Dia11 após RED, mantendo controle Lotofácil8.
- [x] Restaurar margem horizontal única16dp dos prompts após RED, sem alteração de fonte/glyph/cor/callback.
- [x] Executar regressões físicas finais17/17 pelo coordenador e guardar recapturas em `build/verification/production-plans-20261005/betting-final`.
- [x] Revisar o helper de margem somente por leitura: o container mantém `round(8dp)`; o restante `round(16dp)−round(8dp)` convertido de volta a Dp soma exatamente a margem nativa arredondada na densidade corrente. Manual/complemento/SuperSete compartilham esse atributo instalado e nenhuma borda da grade foi movida.

Leitura independente das recapturas finais por Pillow/numpy confirmou o crop do prompt80/610/900/710 em Dia1316 e Lotofácil3798: bbox original e candidato95/633/543/671, zero pixels diferentes e erro RGB total0. Referências: `legacy-dia-manual.png` e `legacy-lotofacil-manual-stable.png`, comparadas com os respectivos `betting-final/compose-installed-*-manual.png`. Isso encerra o desvio horizontal do prompt nesses pares; não converte o restante da tela em100%.

As checkboxes gerais de referência completa permanecem abertas: Loteca não tem seleção real disponível no concurso1274, matrizes completas Mil12n/Super13–21 e ajuda/termos/resultados ainda exigem evidências próprias. As métricas98–99% acima pertencem à recaptura anterior; as métricas globais da recaptura final devem ser citadas apenas após cálculo atualizado do coordenador. Nenhuma alteração de produção foi feita nesta revisão final.


### Disponibilidade de Federal na navegação pública atual

Nova evidência física do coordenador: `legacy-results-current.png` e `legacy-results-modalities-lower.png`, em `build/verification/production-plans-20261005`, mostram a tela `SelecaoModalidadesResultados` com10modalidades e sem Federal. Isso corresponde às10modalidades de `CandidateResultModalityCodes`; não há destino Federal oferecido pela Home/Resultados observada. A rota opcional encontrada no DEX primário demonstra código existente, mas não comprova acesso na navegação atual e não justifica acrescentar um card ao candidato.

A disponibilidade foi verificada como não oferecida neste fluxo. Comparar conteúdo de Federal passa a ser condicional: somente quando o backend/produção disponibilizar um caminho real e dados observáveis. Essa conclusão não afirma que a modalidade nunca pode ser disponibilizada. Nenhuma mudança de fonte ou código de produção decorre desta evidência.


### Fechamento da rodada física — 06/10/2026

O log final `/tmp/production-betting-final2-green-xcover.log` terminou em `OK (21 tests)`, incluindo os pares físicos selecionados, diálogos, roda e recuperação de carregamento desta rodada. Os pares e recortes não cobrem a matriz completa de modalidades. O cálculo atualizado está em `build/verification/production-plans-20261005/public-pairs-metrics.json`: corpo de Lotofácil98,92%, Quina98,20%, Dupla Sena98,20–98,45%, Dia de Sorte98,64%, e meses99,70–99,71% de pixels exatamente iguais. Pixels dentro de1RGB ficam entre99,71% e99,97%; isso não equivale a100%.

Os prompts de Dia de Sorte e Lotofácil mantêm igualdade exata no crop isolado já registrado acima. Permanecem sem certificação: Loteca selecionável em concurso aberto, espelho da Lotomania na mesma posição de scroll, todas as faixas de +Milionária e Super Sete, HTML/ajuda completa e respostas negociais de produção. Os arquivos de métricas, capturas e logs foram preservados em `build/verification/production-plans-20261005/`. Os registros anteriores de GREEN pendente ou de métricas pré-correção são históricos; os resultados acima prevalecem para esta revisão.

### Auditoria de regras de Loteca — 08/10/2026

O usuário pediu passar por todas as modalidades e igualar layout e regras ao legado. O aparelho conectado nesta sessão é somente o emulador Pixel 9 Pro; o pacote legado que está instalado nele é `br.gov.caixa.loterias.apostas.legacy`, versão1.24.0/código230102400, e abre `AppIndisponivelActivity`. Portanto, esta rodada usou as capturas/documentação de produção1.25 já guardadas e não criou novos pares visuais legado/Compose.

Ao comparar regras do Compose com `cx/app/src/main/java/br/gov/caixa/loterias/apostas/view/features/volantenovo/fragment/LotecaFragment.java`, foram encontradas duas divergências concretas. O legado bloqueia o 27.º prognóstico (`MAXIMO_PALPITES_LOTECA=26`) e não deixa selecionar duplos/triplos sem uma combinação exata na tabela de preços (`ParametroJogoDTO.isCombinacaoValidaLoteca`). Compose aceitava qualquer quantidade de resultados e deixava combinações sem preço selecionadas, embora o botão de adicionar ficasse inválido. Além disso, o `BetDraft.numberQuantity` permanecia no mínimo depois de marcar prognósticos adicionais.

`BettingGameViewModel.toggleLotecaOutcome` agora permite desmarcar livremente, bloqueia acréscimos que ultrapassam26 com a mensagem instalada, bloqueia combinações duplos/triplos sem par de preço com o literal instalado e aumenta `numberQuantity` para refletir o maior total de prognósticos alcançado. A regressão `loteca raises the priced prognosis count and caps selections at twenty six` foi observada falhar antes da correção (esperava26, recebia14) e passar depois. `loteca requires every match and sends simple double and triple outcomes` também cobre que a configuração não precificada de duplos adicionais é rejeitada sem perder a aposta válida.

A suíte JVM `:feature:betting:testDebugUnitTest`, compilação `:feature:betting:compileDebugAndroidTestKotlin` e a matriz instrumentada de renderização das11 modalidades passaram no Pixel9Pro AVD, sem envios ao carrinho. Essas fixtures provam composição e regras locais exercitadas; não provam pixels iguais aos do APK1.25. A primeira tentativa instrumental foi interrompida por uma Activity legada que ainda estava ativa no emulador; após `am force-stop` no pacote `.legacy`, a repetição passou11/11.

A suíte Android completa tem50testes; executada no mesmo AVD, passou44 e falhou6 guardas que exigem a dimensão/densidade do XCover ou a largura física da referência: uma largura de botão configurada para197dp recebeu240,67dp com o espaço flexível do AVD; três verificações de cartela esperavam1080px, mas o emulador mede1280px; dois testes de origem do prompt esperavam450dpi, mas o AVD mede480dpi. Esses resultados não substituem a execução no XCover e não justificam alterar os valores físicos de referência. Todos os outros44 testes da suíte passaram.

As demais regras existentes cobertas por testes continuam incluindo números/preços por parâmetros, complementos de trevos/time/mês, sete colunas do Super Sete, espelho Lotomania, partidas e placares esportivos, recorrência, Surpresinha, edição e restrições de carrinho. A paridade visual completa ainda exige capturas instaladas pareadas por modalidade e estado; a matriz anteriormente documentada permanece com estados físicos não certificados, sobretudo Loteca selecionável e as faixas completas de +Milionária/Super Sete.

Revisão independente posterior encontrou três casos adicionais no fluxo Loteca e eles foram corrigidos em `BettingGameViewModel`: (1) o carrinho agora só habilita se a soma de prognósticos selecionados for exatamente `numberQuantity`, como exige `AdicionarCarrinhoReducer.adicionarLoteca`; (2) ao editar, a quantidade é restaurada da seleção de partidas e o teste envia um draft com os mesmos três prognósticos; (3) retirar um resultado que deixa duplos/triplos fora da tabela de preços é revertido com o aviso do legado, conforme `LotecaAdapter`/`LotecaFragment`. Os testes reproduzem esses casos. A suíte JVM completa foi executada novamente com `--rerun-tasks` e passou. Esta correção substitui a anotação anterior que dizia que a remoção podia ocorrer livremente: agora somente uma remoção que mantém combinação precificada (ou a aposta de simples permitida) é aceita.

Na mesma revisão de regras, outros três casos de toque foram corrigidos: números comuns agora avançam a quantidade e o preço ao selecionar além do limite atual e reduzem a quantidade ao desmarcar, conforme `SimulaFragment`; ao selecionar no controle uma quantidade menor que os números marcados, a cartela inteira é limpa conforme `QuantidadeNumerosReducer`; no Super Sete, só se remove de uma coluna que alcançou o máximo da aposta atual, e a quantidade não é silenciosamente reduzida para permitir uma distribuição incompatível. Red/Green em testes de ViewModel cobre aumento/redução Mega, redução do controle em cartela comum e Super Sete, distribuição 15 `[3,2,2,2,2,2,2]` e seleção parcial após redução.

Verificação após essas correções: `:feature:betting:testDebugUnitTest --rerun-tasks` passou98/98; `:feature:betting:compileDebugAndroidTestKotlin` passou; `BettingModalityRenderingTest` passou11/11 no Pixel9Pro AVD. A matriz instrumentada usa fixtures isoladas e prova composição/renderização, não paridade de pixels com produção. A execução Android completa anteriormente registrada continua44/50 nesse AVD por seis guardas de hardware (viewport/densidade XCover). A compilação e os testes locais não eliminam essa limitação.

`:app:compilePrdDebugKotlin` também passou para integrar a mudança no app Compose de produção; a primeira tentativa sem especificar flavor foi rejeitada pelo Gradle porque `compileDebugKotlin` é ambíguo entre prd/tqs/automacao, então foi executada a tarefa explícita de produção.
