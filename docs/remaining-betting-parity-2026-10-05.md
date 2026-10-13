# Apostas: continuação contra referência instalada

Referência primária: official1.24.0, `build/verification/installed-reference-20261005/legacy-installed.apk`, hash registrado em `reference.json`. Novas capturas físicas do principal ficam em `build/verification/remaining-installed-20261005`. Fonte Java/XML auxiliar: checkout `/Users/ubiracysantos/Projects/legado/simlo-app-android`.

## Alterações de apresentação nesta continuação

`BettingGameRoute` conserva a cartela quando `loading=true` e já existe jogo, exibindo `CaixaDSAuthenticationLoadingDialog` não cancelável. Antes o ramo de loading substituía a cartela por spinner inline. A origem foi rastreada: DEX classes10, `SimularApostaModel.adicionaApostaNoCarrinho` endereço02245c chama `AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoCartela`; helper Java85/93/106/118/125 chama `AlertDialogUtils.show`; DEX `AlertDialogUtils.show`02348c chama `LoadingViewLoterias.show`, endereço0461e0. `SimulaActivity` recebe `ParametroJogoDTO` por extras. O host moderno agora transporta o snapshot público preparado, preservando o mínimo real do carrinho; o ViewModel aceita esse DTO somente na inicialização e somente quando modalidade/tipo/concurso correspondem. `load()` explícito continua buscando parâmetros; entrada direta ou restauração sem snapshot usa o fallback atual. No fallback sem DTO, o scaffold permanece vazio atrás do mesmo overlay DS; removido spinner Material3 inline. Esse estado inicial sem extras não foi observado na Activity instalada e fica identificado como fallback moderno.

Lotomania deixa de usar Checkbox Material3, título/explicação inventados no fim da grade. `BettingLegacyMirror` reconstrói os dois controles nativos: faixa manual antes da cartela, com padding16/12/16/16dp, checkbox20dp, texto12sp e HTML literal; Surpresinha com checkbox20dp, texto14.2sp e rótulo literal `Aposta espelho`. Visibilidade e callback de alternar espelho foram preservados. O XML empacotado `activity_simula_aposta` confirma a faixa antes da cartela, `laranjaLoto` FDF1E6/texto4E4E4E; `fragment_surpresinha_novo` confirma a linha nativa. DEX classes15 `SimulaFragment`050b94 confirma texto completo e `ViewUtils.fromHtml`. O path de `ic_check_novo` foi reconstruído em Drawable usando tokens DS e StateListDrawable. Nenhuma tela XML ou nova cor literal foi introduzida no feature.

## Fixtures públicas e limites

`ExtendedInstalledPairsTest` captura estados em `externalCacheDir/codex-parity`: `compose-installed-{id}-{contest}-{state}.{png,txt}`. Cada metadata registra JSON do snapshot, números/trevos/espelho, densidade/fonte, `fixture=true` e `networkSends=0`. `AddBetToCartUseCase` lança erro se for invocado. As seleções são aplicadas pelo ViewModel; as capturas comprovam renderização controlada e precisam comparação com estados idênticos da referência.

| Fixture pública registrada pelo principal | Estados implementados | Limite |
| --- | --- | --- |
| Lotofácil3798,06/10/2026,15nR$3,50 | manual,seleção1–15,Surpresinha,quantidade,ajuda | Quantidades adicionais usam C(n,15), não foram observadas todas na tela instalada |
| Quina7136,06/10/2026,5nR$3 | manual,seleção1–5,Surpresinha,quantidade,ajuda | Quantidades adicionais usam C(n,5) |
| Timemania2451,06/10/2026,10nR$3,50 | manual,seleção1–10,complemento,ABC-RN selecionado,Surpresinha,quantidade,ajuda | Seis nomes públicos; números de equipe são IDs sintéticos de fixture. Escudos remotos são excluídos do pareamento de imagem |
| SuperSete908,07/10/2026,7nR$3 | manual,zeros em todas7colunas,Surpresinha,quantidade,ajuda | Preços observados7–12:3/6/12/24/48/96. Fixture não certifica opções/preços13–21; não usa C(n,7) |
| +Milionária396,07/10/2026,6n/2trevosR$6 | manual,seleção1–6,trevosvazio/1–2selecionados,quantidadetrevos,números,Surpresinha,ajuda | Preços observados6–11n/2trevos;6n/2–6trevos. Quantidades12n e outrosprodutos não certificados |
| Lotomania2985,07/10/2026,50nR$3 | manual,seleção1–50,espelhoativado,Surpresinha/comespelho,quantidade,ajuda | A captura mirror-selected começa no13 recortado; comparar faixa de espelho, não parear corpo com fixture no01 |

O argumento instrumentado `publicBettingFixtures` permite uma lista JSON com snapshots adicionais observados: campos `id,name,contest,draw,maxNumber,min,max,basePrice`, opcionais `prices` (`numbers,clovers,amount`), `recurring`, `months` (`number,name,short`), `teams` (`number,name,state,badge`) e `matches` (`number,home,away`). Não preencher dados desconhecidos e classificá-los como públicos.

Loteca1274: a referência `legacy-loteca-selected.png` é o aviso **Concurso 1274 ainda não aberto para aposta. Tente novamente mais tarde.**, não seleção. Corpo disponível tem Flamengo/RJ×Fluminense/RJ e Vasco da Gama/RJ×Remo/PA; a seleção real permanece indisponível nessa sessão. Não incluída no default público. A matriz sintética existente continua separada para contadores/duplos/triplos.


## Estados e testes adicionais

`BettingRemainingDialogStatesTest.duplicateDialogCancelAndConfirmUseOnlyLocalCallbacks` captura o literal compartilhado da aposta duplicada e verifica Não/Sim com callbacks exclusivamente locais; não chama carrinho/checkout. Não é reprodução do backend instalado.

`BettingRemainingDialogStatesTest.favoriteKeyboardFullInputPendingOverlayOverwriteCancelAndBlankName` usa jogo público Mega3067, callback de validação favorito suspenso localmente, zero saves e zero carrinho. Abre favorito, pede foco e IME real, digita alfabeto26caracteres; campo conserva26 e DTO recebe25. Captura abertura/teclado/modal pendente/confirmação sobrescrever/nome vazio. Mensagem de sobrescrever é resposta sintética explicitamente identificada como `messageSource=fake-server-validation`, não conteúdo observado no backend. Cancelar não salva.

`BettingWheelSwipeTest.realTouchSwipeSnapsCenterAndConfirmsThatOptionOnce` injeta eventos touch reais na wheel nativa; após idle mede opção central por LinearSnapHelper, salva `compose-mega-recurrence-swiped` e confirma callback exatamente uma vez com essa opção. Nenhuma rede.

`BettingEditedExitTest.editedTicketRequiresCancelOrConfirmBeforeReturningHome` reproduz a pendência de comportamento: atualmente Voltar deixa a tela sem confirmação. Espera mensagem instalada **Ao voltar para a página inicial, sua aposta em edição será descartada. Deseja continuar?**, Cancelar preservando seleção e Confirmar chamando host uma vez. RED físico confirmado pelo principal em `/tmp/remaining-exit-red-xcover.log` (1teste/1falha por diálogo ausente); correção autorizada: header e BackHandler preservam complemento→números, depois exibem Cancelar/Confirmar quando existe edição pendente. Cancelar conserva a cartela; Confirmar chama o host. Fonte auxiliar: `SimulaActivity.exibirDialogConfirmacaoSaida`1964/`getPossuiAlteracoesPendentes`1982. GREEN físico confirmado pelo principal no batch12 em `/tmp/remaining-betting-green-xcover.log`; pareamento visual do diálogo segue separado.

Ajuda HTML passou a usar novo `CaixaDSLegacyHtmlDialog`, API DS em arquivo próprio que recebe temas nativos existentes do caller: `DialogUtils.dialogHtml`644→`buildAndShowDialogHtml`843 usa `HtmlCompat.fromHtml(FROM_HTML_MODE_LEGACY)` em TextView nativo16sp, com MaterialButton e `layout_dialog_padrao`. O renderer novo usa AlertDialog/HtmlCompatLegacy/TextView16sp/MaterialButton com fonte Caixa, padding24/24/24/14, divisor1dp/margem17, conteúdo16dp e ação16dp, foco/heading no título. O mesmo renderer em modo texto literal/botões opcionais também substitui os modais de saída, sobrescrever, duplicada, warnings e erros da simulação; mantém mensagens e callbacks. Nenhuma tela XML acrescentada. As assertions de InstalledMegaPairTest/ExtendedInstalledPairsTest passaram para helper de TextView nativo; o pareamento físico ainda é necessário.

`BettingCloverQuantityTest.cloverSelectorUsesTwoThroughSixWithPriceAndRequiresChosenCount`: RED físico confirmado em `/tmp/remaining-clovers-red-xcover.log` (1teste/1falha por ausência do selector2trevos). Depois de autorização, o ViewModel ganhou alvo manual de trevos derivado do DTO/preseed, preços por alvo, selector nativo2–6, clearing conforme TrevosReducer241, exato aviso de aumento e bloqueio de envio até selecionar o alvo. Shake completa o mesmo alvo; teste escolhe4/preço36 e bloqueia quinto clique. GREEN físico inicial confirmado pelo principal no batch12 em `/tmp/remaining-betting-green-xcover.log`; a conversão visual posterior para ToggleButton nativo requer nova execução.

Na +Milionária foram copiados os paths originais `ic_trevo`/`ic_trevo_selecionado` em dois recursos drawable `installed_betting_*`; cores foram substituídas por referências Android preto/transparente e tint pelo DS LegacyMaisMilionariaDark, preservando geometria. Os círculos genéricos anteriores eram incorretos. Indicadores1/2↔2/2, rótulos primários de EscolhaUtils e random/clear por etapa foram alinhados ao runtime. A orientação vazia dos trevos conserva altura TextView, como renderTextoSelecionados do original. As mudanças de apresentação exigem novas capturas.

## Federal: destino real

DEX classes5 `ModalidadeEnum` (DTO do Home, região92624–92710 de `/tmp/simlo-betting-installed/classes5.txt`) tem BOLAO/COMBO/INSTANTANEA/LOTECA/LOTOFACIL/LOTOGOL/LOTOMANIA/MAIS_MILIONARIA/MEGA_SENA/QUINA/SUPER_7/TIMEMANIA/DIA_DE_SORTE/DUPLA_SENA; não contém Federal. Federal aparece em `ModalidadeEnumSelecaoModalidades` dos filtros de resultado. `PrincipalActivity.nextActivity`0428e8 envia Instantânea a ACTION_VIEW, Combo a CombosActivity, Loteca a LotecaActivity, demais modalidades de aposta a SimulaActivity; não há branch Federal. Fonte auxiliar concorda. Não foi comprovado card Federal para aposta no Home instalado, portanto nenhum destino ou simulação foi inventado no moderno.

`BettingGameViewModelTest.prepared public parameters avoid refetch and explicit retry refreshes backend` exige zero consultas para DTO preparado, mínimo de carrinho preservado e refresh explícito: primeira tentativa falha, retry atualiza preço e mínimo. RED confirmado pelo principal em `/tmp/remaining-prefetch-vm-red.log` (30 testes, uma falha). Correção autorizada implementada; teste complementar conserva consulta backend quando snapshot é de outra modalidade, tipo ou concurso. GREEN dessa alteração aguarda execução do principal.

## Validação

O principal executou a compilação instrumentada (`/tmp/remaining-betting-build.log`: BUILD SUCCESSFUL) antes da última atualização HTML/exit. O teste JVM de warning foi endurecido para o literal empacotado e falhou como esperado em `/tmp/remaining-betting-red.log` (28testes/1falha). Após autorização, o warning passou ao literal instalado completo. A compilação/JVM seguinte passou em `/tmp/remaining-native-process-build.log`; após isso houve novas correções de trevos, recursos e fixtures. A última atualização exige nova compilação/GREEN. Nesta subtarefa não se executa Gradle nem ADB. `git diff --check -- feature/betting` não reportou erros, mas o módulo está untracked neste checkout e o comando não inspeciona esse conteúdo; não constitui verificação abrangente das edições. Compilação instrumentada, execução física RED/GREEN e novos pares pertencem ao principal. Estes registros são preparação verificável, não resultado de testes ainda não executados e não certificação100%.

## Revisão dos pares físicos após batch GREEN

SuperSete apresentava orientação depois do cabeçalho1–7 e colunas excessivamente largas. Corrigido para orientação antes dos headers, margem horizontal8dp e gap16dp entre headers/colunas, preservando y inicial das bolas. Lotofácil manual/selecionado mostram grade16dp abaixo da fixture; aplicado o marginTop16dp de cltGeral somente nessa modalidade observada. O header manual tem estado back pressed e não deve alimentar métrica integral; header selecionado é estável. Quina manual foi capturada durante transição e está excluída de certificação.

Trevos convertidos para AppCompatToggleButton nativo45dp/margin8dp/CaixaBold16sp, usando backgrounds com tint DS e callbacks existentes; o teste agora clica botões nativos1–5. A geometria vinha do item_dezena_trevo e remove diferenças de rasterização Image/Compose Text.

Surpresinha Lotomania revelou50 máscaras modernas contra seis placeholders XML fixos; corrigido para seis. Switch checked recria track/shape/presente tintados pela modalidade e toolbar com alpha26, como configurarSwitchSurpresinha/renderSwitchSurpresinha. Corpo de Lotomania mirror-selected não é pareável pois o oficial começa no13 e fixture no01. Cores erradas de selector/bolas/botão foram encaminhadas ao principal: mapping DS de ids5/16 e1/3.

Snapshot preparado agora aplicado síncronamente no constructor: RED `prepared parameters render immediately before queued IO runs` confirmado pelo principal em `/tmp/remaining-prefetch-immediate-red.log` (31 testes, uma falha). Fix autorizado separa applyParameters puro de refresh coroutine; nova execução GREEN pendente.

+Milionária Surpresinha: RED físico de caption/contador em `/tmp/remaining-surprise-red-xcover.log` (1 teste, uma falha). Correção autorizada mostra contador de números e de trevos; números6→7 mantém surpriseQuantity1/preço42. Labels oficiais e underline18sp, menos no mínimo com CaixaDS.colors.legacyLine (#D0E0E3 comprovado pelo principal) e sombra nativa. Nova execução GREEN pendente.

Bolas numéricas comuns convertidas para AppCompatToggleButton45dp, padding interno0, fonteCaixaBold14sp arredondada em pixels; FlowRow/células/margens2h4v preservadas. Semântica de texto e clique mantém SmokeTest01. Clique rejeitado restaura checked da seleção atual do ViewModel. Pares anteriores ainda não refletem essa conversão. Time do coração: nomes/UF centralizados e parent nativo match-parent; escudos são fallback e continuam excluídos.

## Último pacote estável para fechamento

Batch físico final inicial: 14 testes GREEN pelo principal em `/tmp/remaining-betting-final-xcover.log`, antes dos três ajustes abaixo. Completar em números completos já tinha isEnabled=false, mas preservava tint/texto/borda de enabled; agora usa canvasF7FAFA/texto e borda9EB2B8 comprovados por pixels, e completação de complemento preenchido também desabilita. BettingCloverQuantityTest exige enabledfalse e três cores exatas em Completar jogo antes de Escolher Trevos.

SuperSete usa TextView nativo conforme SuperSeteAdapter.getView (não ToggleButton): tamanho44dp truncado em pixels, cornerRadius inteiro size/2, stroke3 pixels físicos, FuturaBold e tamanho nativo padrão. Cabeçalhos e posições preservados. O tamanho de fonte Escolher Trevos permanece intacto: btnNextStep não declara textSize e o tema instalado precisa evidência adicional antes de mudança.

BettingLoadingRecoveryTest substitui a assertion obsoleta ProgressBarRangeInfo pela implementação real DS: root dialog + Carregando… após750ms, VM em loading, back físico não dismiss nem callback, falha e retry liberam overlay, zero envio preservado. Esses três ajustes requerem execução final do principal.

## Resultado final coordenado

Build/JVM/gates passaram em `remaining-delivery-green-validation.log`: betting87, total809 JVM incluindo DS. XCover batch final15/15 em `remaining-betting-delivery-xcover.log`, incluindo nativeballsmoke, Completar disabled com cores instaladas, contadores Surpresinha +Mil e loading original/backblocked/retry. Matriz adicional14 passaram (11modalidades,2seletores,1buscaTime); RED antigo de ProgressBar semântica foi substituído pela verificação do overlay real e passou no batch final. Capturas atualizadas em `build/verification/remaining-installed-20261005/betting-final/`. As métricas de pares ainda registram diferenças residuais e estados indisponíveis; não há certificação100%.

Alinhamento final Super Sete: o GridView original declara gravityCENTER; o renderer moderno reproduz centralização por divisão inteira em pixels para não arredondar a sobra ímpar como BiasAlignment Compose. Filhos mantêm dimensões nativas e callbacks. Build/JVM/gates adicionais passaram em `remaining-super-centering-validation.log`; recaptura dos seis pares fica em `remaining-super-centering-xcover.log`.
