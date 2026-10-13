# Apostas: matriz contra o APK instalado — 2026-10-05

Referência primária: `build/verification/installed-reference-20261005/legacy-installed.apk`, versão official1.24.0, SHA-256 `2e8ef3bd96c3db48a4391695429bf8531ccae72a821e3683a742bc61eb90dc3e`. O checkout Java/XML é apoio para interpretar callbacks, não substitui o binário. Nesta subtarefa não se executaram Gradle nem ADB; execuções físicas e integração pertencem ao agente principal.

## Inventário por modalidade

| Modalidade / ID | Região comum | Complemento / estado específico | Caso Android controlado | Situação visual |
| --- | --- | --- | --- | --- |
| Quina / 1 | header, toolbar, números, rodapé | 5–15 números; Teimosinha e Surpresinha | `BettingModalityRenderingTest[Quina]` | Grade comum reconstruída; faltam pares instalado/moderno por estado |
| Mega-Sena / 2 | idem | 6–20 números; seleção, favorito | `BettingGameRouteSmokeTest`, matriz | Smoke existente; par físico/header ainda depende do principal |
| Lotomania / 5 | idem | 50 números/00, espelho | matriz | Espelho requer par próprio; grade comum usa 100→00 |
| Lotofácil / 8 | idem | 15–20 números | matriz | Falta par de seleção/preços e diálogos |
| Dupla Sena / 18 | idem | concursos/datas/sorteios | matriz | Falta par de conteúdo real/concurso |
| Timemania / 20 | idem | time do coração, busca vazia/resultado/escudo, teclado | matriz inclui entrada do complemento | Substituída por controles nativos: EditText16sp/min40dp/margem16dp/lupa original e cards115dp sem borda padrão, seleção com2dp/radius8dp; três colunas/ItemDecoration8px. Falta par físico/keyboard |
| Dia de Sorte / 11 | idem | mês da sorte não selecionado/selecionado | matriz inclui mês | Corrigidos 2 colunas, espaçamento14dp, margem8dp, TextView14sp/padding12dp, cinza, regular/semi-bold e fade200ms; falta par físico |
| Super Sete / 7 | cabeçalho/rodapé | sete colunas, ordem dos números/colunas, números por coluna | matriz manual/Surpresinha | DEX confirmou `SimulaFragment`→`fragment_cartela_novo`0x7f0d00eb; corrigidos header35dp/raio16/margem1, bolas44dp/Futura bold/stroke3px, spacing15dp/background80DDE89C/selectedBED730+008254. Falta par físico |
| +Milionária / 9 | idem | 2–6 trevos, quantidade de trevos Surpresinha, preços | matriz entra nos trevos | Trevos manuais corrigidos: texto literal, 36dp/60dp, grid42dp abaixo/3colunas/45dp/margem8dp; falta par/Surpresinha |
| Loteca / 19 | corpo esportivo específico | partidas, duplos/triplos, legendas | matriz manual | Retirado corpo genérico. `item_loteca_novo`/LotecaHolder reconstruídos nativamente com weighted rows/wrap height/selectorsoriginais/textos e imagens/margens, descrição escondida na seleção e translationZ2px. Toolbar `Suas apostas`/Partidas/Simples/Duplas/Triplas/Como jogar reconstruída; falta par físico |
| Lotogol / 14 | corpo esportivo específico | placares | fixture adicional, não representa disponibilidade comercial | Compatibilidade mantida; modalidade histórica não certificada ativa no APK |
| Federal | entrada/catálogo | sem escolha numérica convencional | não fabricado teste de simulação | É preciso mapear destino original; não afirmar que possuir nome no catálogo autoriza simulação |

## Revisão adicional de regras — 2026-10-08

Ao confrontar o fluxo esportivo com `LotecaFragment` e `ParametroJogoDTO` no legado, a implementação Compose não aplicava o limite de26 prognósticos nem bloqueava a inclusão de duplos/triplos sem combinação exata de preço. Também mantinha `BetDraft.numberQuantity` no mínimo mesmo quando a seleção ampliava o total de prognósticos. Essas três diferenças foram corrigidas no `BettingGameViewModel`: excedentes são recusados com a mensagem do legado, somente combinações precificadas podem ser montadas e a quantidade acompanha o maior total validamente alcançado. Testes JVM verificam o fluxo, incluindo a rejeição da 27.ª previsão.

Essa correção funcional não altera a situação visual da matriz: nesta sessão o único dispositivo conectado é o Pixel9Pro AVD e o pacote legado 1.24 nele instalado mostra `AppIndisponivelActivity`. A instrumentação Compose capturou as11modalidades com fixtures controladas, sem chamar o carrinho, mas não substitui comparação lado a lado no APK de produção1.25.

Na suíte Android completa de apostas,44/50 passaram no Pixel9Pro AVD. As seis falhas são guardas vinculadas ao XCover (1080px/450dpi e botão flexível esperado197dp), enquanto o AVD tem1280px/480dpi e mede a ação flexível em240,67dp. Não houve falha nas outras44 regressões; os testes físicos precisam ser repetidos no XCover para validar essas seis medidas.

Os limites comerciais devem vir do DTO de parâmetros corrente. Os casos usam dados determinísticos mínimos, não tentam fixar preços/concursos reais.

## Diálogos, loading, erro e foco

| Estado | Evidência empacotada / controlador | Correção ou pendência | Verificação controlada |
| --- | --- | --- | --- |
| Quantidade de números | `layout_dialog_itens.xml` ListView300dp + `item_text_popup_itens.xml` linha52dp/texto15.8sp; DialogUtils.showDialogListItens | Retirado radio-list Material3; diálogo Android85%, padding24/24/24/14, título16sp/blue_caixa, divisor1dp/top17, ListView original, rodapé16dp/Cancelar/Confirmar16sp. +Milionária mostra subtítulo literal '* Valores para apostas com 2 trevos'. Seleção atual mantida como fallback funcional de confirmação. | `BettingNativeOptionsTest.quantityUsesOriginalListAndCancelDoesNotCommit`: cancelar não confirma callback |
| Teimosinha | `layout_dialog_itens_novo.xml` RecyclerView170dp; `item_text_popup_itens_novo.xml` linha52dp, lateral15dp; WheelController/LinearSnapHelper | Native RecyclerView com snap central, seleção/fundo EB F1 F2/raio31dp, foco no título, abertura no primeiro item como instalado; descrição literal 'Escolha em quantos concursos você quer participar com este mesmo jogo:' | `recurrenceUsesOriginalWheelAndCommitsExactlyOnce`: geometria170/52dp, foco e callback único. Deslize precisa par físico |
| Como jogar | HTML AccordionModel + diálogo legado | Substituído Material3 AlertDialog por CaixaDSLegacyDialog compartilhado com AnnotatedString HTML, título literal/Entendi e85% de largura; rendering HTML/TextView e par físico ainda abertos | Smoke diferencia Dúvidas/FAQ e Como jogar; falta par do diálogo HTML |
| Favoritar | `layout_dialog_favoritar.xml`/DialogUtilsTheme | Diálogo nativo pré-existente; conserva nome/callback e foco no título; teclado/rolagem/excesso de nome precisam pares | Nenhum novo teste de envio favorito nesta subtarefa |
| Sobrescrever favorita / aposta duplicada | confirmações Sim/Não | Usa CaixaDSLegacyDialog compartilhado; requer comparação fonte do conteúdo/mensagens e par físico | Testes de reducer existentes; não equivalem ao visual |
| Warnings / erro de operação | mensagens ViewModel/DS | CaixaDSAlertDialog compartilhado; par por tipo ausente | Erro de carregar testado isoladamente, não todos avisos negociais |
| Carregamento inicial / falha / retry | requisição de parâmetros | Estado moderno próprio com spinner/retry; **não certificado como overlay original** | `BettingLoadingRecoveryTest`: Deferred suspende carga, devolve Network, retry sucesso, zero sends |
| Quantidade/preço/loading de adicionar | requisição carrinho | Não executado envio real; aparência de overlay ainda exige instalado | Não se usa checkout/pagamento em fixtures |
| Foco teclado/time/favorito | nativos/focusable/heading | Título dos seletores focusableInTouchMode + AccessibilityHeading + TYPE_VIEW_FOCUSED; input favorito existente. Time usa input Android e mantém query/callback | Teste wheel valida foco. Necessários TalkBack/teclado/fonte ampliada |

## Header Dúvidas: investigação sem compensação arbitrária

`cabecalho_simula.xml` empacotado confirma ImageButton48dp, padding8dp, margemEnd4dp. `duvidas_toolbar_icon.xml` empacotado confirma Inset5dp. Os cinco `icon_duvida2.png` são byte a byte iguais aos cinco arquivos de `feature/betting`: mdpi30px, hdpi45px, xhdpi61px, xxhdpi91px, xxxhdpi122px.

DEX `classes15.dex`, `CabecalhoSimulacaoFragment` → `VectorUtils.getShape`, e `classes10.dex`, `VectorUtils.getShape`, comprovam carregamento via `AppCompatResources.getDrawable(applicationContext, resource)`, wrapping/tint e `setImageDrawable`. A implementação passou a AppCompatImageButton e carrega com ApplicationContext; tamanho/insets/padding originais ficam intactos. O smoke registra `BettingHeaderMetrics` (densityDpi, classe, dimensão, padding, intrinsic, bounds, imageMatrix e classe drawable). Isso permite investigar a divergência medida81x83vs65x67 sem adicionar offsets inventados. Esta mudança **não prova** que a escala instalada foi alcançada: precisa nova captura e métricas pelo principal.

## Rapidão: sequência instalada comprovada

DEX `classes8.dex`, `ConfiguracaoRapidaoActivity$RapidaoPagerAdapter.<clinit>` endereço026944: `const/4 #7` → `NUM_ITEMS`. `getItem` endereço02685c: packed-switch de sete posições, Fragment1/2/3/4/5/6/8, sem Fragment7. A fonte auxiliar coincide. O moderno agora tem sete etapas; resumo é posição6; clamp do ViewModel passa a6; indicador e teste Android usam 'Etapa7de7'. Retirada somente a etapa adicional de favoritas e a linha de resumo que apontava à etapa removida. Favoritos da simulação e a rota FavoriteBets permanecem. DTO/favoritas persistidas não são apagados.

`RapidConfigurationViewModelTest` atualizou a expectativa de clamp7→6. `RapidPagerNavigationTest` passa a saltar0→6→2, mantendo swipe2→3 e evitando feedback de páginas intermediárias. Os sete XML das páginas presentes e os recursos antigos de favoritas continuam disponíveis no projeto; recurso empacotado não implica etapa ativa.

## Evidência auxiliar enviada ao principal: badge Home

DEX `classes8.dex`, `PrincipalActivity.atualizarBadgeNotificacao(int qtdNaoLidas)`, endereço041bf8: `findViewById(0x7f0a04a9)`; qtdNaoLidas>0→VISIBLE0, senãoGONE8. `PrincipalActivity$13.onResponse`, endereço040344, usa `NotificacaoResponse.payload List.size()`; null/erro→count0. `onActivityResult` lê extra `QTD_NAO_LIDAS` default0 em request2001/RESULT_OK. Assim, o badge XML visible não é sempre exibido em runtime e não se deriva de `latestNotification`.

## Checks que o principal deve executar

1. Compilar `:feature:betting:compileDebugAndroidTestKotlin` e executar `:feature:betting:testDebugUnitTest` (houve84tests/1falha na primeira integração porque o teste antigo esperava clamp7; expectativa corrigida).
2. Executar instrumentação `BettingGameRouteSmokeTest`, `BettingNativeOptionsTest`, `BettingLoadingRecoveryTest`, `BettingModalityRenderingTest`, `RapidPagerNavigationTest` no mesmo aparelho de referência. A matriz tem11casos (10modalidades convencionais/esportivaLoteca + compatibilidadeLotogol), todos com fake de carrinho e zero envios. Cada caso captura manual e seleção; nove capturam Surpresinha e três o complemento.
3. Registrar `BettingHeaderMetrics`, confrontar help bbox/intrinsic/matrix no par real e comparar manual/vazio/selecionado/Surpresinha/diálogos/complementos em mesmos dados e escalas.
4. Confirmar Rapidão7etapas e resumo sem link circular à antiga página de favoritas; navegar todas as etapas via swipe e pontos.
5. Comparar seletores ListView e wheel (incluindo scroll/seleção/confirm/cancel/foco), meses, trevos, keyboard e loading/erro em par instalado/moderno.

`git diff --check -- feature/betting` passou na subtarefa. Nenhum claim100% é suportado: SuperSete/Timemania/Loteca foram reconstruídos nesta etapa; catálogo/rendering HTML help/loading e pares físicos permanecem explicitamente abertos, e Android compile/test/capturas estão sob responsabilidade do principal.


## Complementos/esportes reconstruídos na continuação

- Super Sete usa o fragmento **real de simulação** `fragment_cartela_novo` confirmado pelo DEX `classes15.dex` (inflate0x7f0d00eb), não o fragmento de resultados e nem o antigo `fragment_super_sete`. Seletores usam `SuperSeteAdapter`:44dp/Futura bold, stroke3px físico, branco/cinza3F4B52; selecionadoBED730/008254. Header35dp com raio16 e margem1dp; fundo de coluna80DDE89C e espaçamento vertical15dp. Títulos genéricos removidos, orientação comum preservada.
- Timemania `BettingSportsContent.kt` usa AppCompatEditText (hint Nome do time, lupa original, bg_edit_favoritar,16sp/40dp/margem16dp), manutenção de query sem reiniciar focus; cards nativos115dp com escudo40dp/top16, textos14.2sp Caixa bold/regular, cinza48586C. `bg_time` original tem somente borda selecionada2dp/2D8563/raio8dp. ItemDecoration8 é **px físico**, convertido por LocalDensity. Escudo fallback `time_padrao` e paddingBottom12px seguem EscudoEquipeUtil; Base64 do DTO é decodificado sem solicitação adicional.
- Loteca utiliza os seletores originais separados esq/cen/dir copiados com nomes exclusivos. Medição nativa conserva altura wrap_content por texto/escudo, pesos1/1/1, padding8, imagem35dp/top12 ou empate18dp/top16/bottom10, fontes default do tema em campos sem fontFamily explícita. `LotecaHolder` em runtime colore seleção005CA9, não pinta texto branco; não selecionado48586C, traduçãoZ2px na seleção, UF escondida quando selecionado/indicadorSelecao. Títulos incluem sufixos feminino/sub17/sub20 pela mesma regra literal. O toolbar passa a conter Suas apostas e quatro contadores com separadores1x20dp/alpha0.3 e wrap por FlowRow. Callbacks do ViewModel não foram alterados.

Não foram introduzidas telas XML. Há17arquivos de recursos novos `installed_betting_*` (13grupos): seletores/bordasLoteca9XML, bg_time, ic_empate, ic_lupa(vetor+4PNGdensidades), time_padrao.jpg. TodosXML foram analisados com ElementTree. É necessário inventariar os recursos no gate principal.

A matriz Android salva PNG e metadata `fixture=true/networkSends=0/modalityId/state/densityDpi/fontScale` no externalCacheDir do APK de testes: `/sdcard/Android/data/br.gov.caixa.loterias.feature.betting.test/cache/codex-parity/betting-{id}-{state}.{png,txt}`. O principal usa ADB pull deste diretório; não há cópia ao Download. As capturas têm exclusivamente os dados fictícios do teste, sem login/PII/backend. Capturas em emulador comprovam rendering das fixtures, não substituem pares no XCover instalado.

A matriz agora também captura `selected` em todas11modalidades; Lotogol fixture possui5partidas. Seleção altera apenas estado do ViewModel e não invoca primaryAction de enviar: esta ação só é invocada para transitar números→complemento nos três tipos. As cores corrigidas foram promovidas pelo principal a tokens DS LegacyLotecaCounter e LegacySuperSeven*, e consumidas por betting.

O endpoint de unread foi confirmado no DEX `classes4.dex`, DadosUsuarioBO.getHistoricoNotificacaoNaoLidas050ff0: GET `/historico-notificacoes/nao-visualizadas`, queryMap vazio, header Authorization Bearer, `NotificacaoResponse.class` extendsSilceResponse no `classes5.dex`, payloadList consumido por PrincipalActivity$13. Sem POST/body, sem derivar count de última notificação.


## Par público Mega-Sena instalado

`InstalledMegaPairTest` usa somente snapshot público: concurso3067, sorteio06/10/2026, carrinho0, seleções6–20 e preço `C(n,6)*6`:6,42,168,504,1260,2772 para6–11números. Opções Teimosinha0,2,3,4,6,8,9,12 seguem a lista mencionada no conteúdo Mega/controlador, sujeita ao DTO real do APK. Fake de parâmetros/carrinho, zero envios. Captura `compose-mega-manual`, `quantity`, `recurrence`, `help` no externalCacheDir, verifica artefatos não vazios. Não confirma quantidade nem recorrência: cancela e verifica6números/0recorrências.

As referências físicas são `legacy-mega-manual.png`, `legacy-mega-quantity.png`, `legacy-mega-recurrence.png` em installed-reference-20261005. A primeira fotografia da wheel mostrou2concursos no centro durante abertura; a captura posterior após idle mostrouNenhumconcurso no centro. Não foi convertido estado transitório em regra de domínio. O teste registra centro medido por LinearSnapHelper e posições destacadas para comparar após idle.

Literais confirmados no controlador foram corrigidos: título `Teimosinhas` e opção0 `Nenhum concurso`. O principal reportou16testes AndroidOK no emulador em `/tmp/installed-parity-betting-emulator.log` e34capturas da matriz existentes no externalCacheDir. A antiga cópia shell paraDownload foi removida: UiAutomation não interpretava `&&` nessa chamada. Os testes agora exigem PNG/TXT não vazios no destino real. Esse resultado de emulador não é certificação de pares físicos.


### Physical Mega-Sena pair follow-up

The first XCover pair used public contest3067, draw06/10/2026 and C(n,6)*R$6 prices. The instrumented case passed; its selector screenshots captured native window fade before it settled, so those translucent frames cannot certify final dialog appearance. The pair test now waits native UI idle plus300ms before each capture and records header scale type/intrinsic size/bounds/matrix.

Measured white help pixels (exclusive right/bottom): installed `(961,91)-(1042,174)` =81x83; candidate `(969,99)-(1034,166)` =65x67. Android ImageButton's constructor defaults to CENTER. Our explicit FIT_CENTER shrank the entire InsetDrawable; restoring CENTER keeps the packaged PNG/inset5dp/view48dp/padding8dp unchanged. Native dimension assignments now round like XML getDimensionPixelSize:1dp border=3px at450dpi rather than truncated2px;13sp text=37px rather than36.5625px. Footer captions/amounts now use the original native wrap-content TextView layout and fonts at11sp/14sp; primary button follows the installed runtime fonteBotaoBold override: semi-bold enabled and regular disabled (the XML initial semi-bold is overridden). Quantity labels now include literal `por` and center gravity. All changes require a new physical capture; no final parity claim is made here.

## Execução física e comparação medida

No XCover7: matriz das11modalidades, seletores nativos, loading/erro/retry e Rapidão passaram15testes em installed-parity-betting-states-xcover.log. Foram preservadas34capturas de manual/Surpresinha/seleção/complementos e respectivos metadados. Lotogol é fixture histórica; Federal não foi tratada como simulação numérica.

Após correções de CENTER da ajuda, arredondamento de borda/texto, rodapé nativo e quantidade centralizada com literal “por”, InstalledMegaPairTest+BettingNativeOptionsTest passaram3testes em installed-parity-mega-final-xcover.log. PNGs em xcover-final-betting. physical-pair-metrics.json confirma ajuda14.300/14.300pixels iguais e bounding box[961,91,1043,174]. Quantity:1.155.990/1.155.992pixels iguais no recorte[82,539,998,1801]; dois pixels de borda diferentes. Isso aprova esses recortes, não a tela inteira ou todas as modalidades. Statusbar/hora diferentes foram excluídos.

Captura anterior de quantity foi descartada para comparação estática: registrava fade nativo ainda em curso. PairTest agora aguarda animação; aguardará sempre o estado estabilizado sem alterar animação de produção.

BettingTimemaniaSearchTest passou no XCover7 junto à recaptura final Mega (2testes em installed-parity-search-xcover.log). Foco e IME visíveis, filtroAlfa com1resultado, filtrozzzz vazio e seleçãoTimeAlfa-SP, zeroenvios; quatroPNGs/TXTs. O helper está no mesmo bounding box físico da referência. Disabled regular/ enabledsemi-bold do botão confirma override DEX `SimulaActivity.fonteBotaoBold`; XML isolado não prevalece sobre esse runtime.

### Loteca: consistência do rascunho e edição

A comparação da regra do legado em `AdicionarCarrinhoReducer.adicionarLoteca` mostrou que o envio exige a quantidade marcada ser exatamente igual a `palpitesLoteca`. O Compose agora bloqueia o carrinho enquanto `sum(selected outcomes) != numberQuantity`. A edição reconstitui essa quantidade dos resultados restaurados; um teste verifica que o draft mantém os três prognósticos selecionados. `LotecaAdapter` também restaura visual e seleção quando remover uma opção criaria uma combinação de duplos/triplos sem preço. Essas três regressões passaram na suíte JVM completa. Isso cobre comportamento determinístico local e não fecha os pares visuais reais de Loteca, que continuam dependentes do legado estar navegável em concurso aberto.

Na revisão cruzada adicional de regras numéricas, `toggleNumber` foi alinhado a `SimulaFragment`: completar a seleção e tocar em mais uma dezena aumenta quantidade/preço em uma unidade e mostra o aviso de custo; remover dezena recalcula a quantidade pelo total restante e mínimo. `chooseNumberQuantity` agora zera a cartela completa se a seleção exceder a quantidade escolhida, em vez de conservar as menores dezenas/colunas. No Super Sete, remover só é aceito quando a coluna atingiu o limite atual (1/2/3 por faixa de aposta), e a redução de quantidade não redistribui os prognósticos de forma arbitrária. Os testes têm estados reproduzíveis RED antes da correção e cobrem os efeitos no botão/carrinho.

Após essas correções, a suíte JVM repetida com `--rerun-tasks` passou98/98, `compileDebugAndroidTestKotlin` passou e a matriz Compose `BettingModalityRenderingTest` passou11/11 no Pixel9Pro AVD. Os testes de estado agora cobrem todas as correções acima. A matriz de renderização usa fakes e não representa comparação visual lado a lado com o APK de produção.

Integração compilada em `:app:compilePrdDebugKotlin` com sucesso.
