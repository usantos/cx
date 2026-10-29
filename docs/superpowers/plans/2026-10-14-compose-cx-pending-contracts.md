# Pendências de contratos CX / Compose

> Executar com superpowers:subagent-driven-development, uma tarefa por vez, revisão por tarefa e revisão final.

**Goal:** corrigir divergências verificadas nas fontes do CX e comparar os fluxos acessíveis por Experimente.
**Architecture:** preservar serviços, Domain/Data e Compose; corrigir os consumidores dos contratos existentes. Nenhuma transação real é necessária para reproduzir os erros com respostas controladas.
**Tech Stack:** Kotlin, Compose, Retrofit, JVM/Android tests, ADB.
**Spec:** docs/COMPOSE_CX_RUNTIME_PARITY_20261014.md e controllers/BOs atuais do checkout CX.

## Global Constraints

- Branch codex/migracao-compose, base 911d5bc31. Não editar CX ou operar emulator-5554.
- Não ler credenciais; capturas e APKs permanecem ignorados. Root opera 5556; testes usam 5558.
- Não relaxar validação de identidade, sessão ou gates; nenhum pagamento/resgate PRD.
- Gradle serializado. Commit contém a lista de todos os arquivos da tarefa.
- Evidência de fonte/teste controlado não equivale à homologação real.

## Review Focus

- Datas de nascimento aceitas pelo CX produzem yyyy-MM-dd no corpo RecargaPay.
- Teimosinha original/filha e situação paga determinam o aviso correto, sem invalidar filha.
- PDF Base64 decodifica; PDF binário compatível permanece íntegro; erro nunca vira arquivo válido.
- Relógio do servidor ancora expiração Pix; tempo decorrido é monotônico e restauração não prorroga validade.
- Erro biométrico recupera por SSO uma vez; nenhuma falha permite aceitar identidade inválida.

### Task 1: Data do titular RecargaPay

**Files:** core/data/src/main/java/br/gov/caixa/loterias/core/data/RemotePaymentRepository.kt; core/data/src/test/java/br/gov/caixa/loterias/core/data/RemotePaymentRepositoryTest.kt.
**Interfaces:** mesma criação de cartão/corpo cardHolder; sem mudança de endpoints.
- [x] Regressão do corpo real: dataNascimento Jan 1, 1990 deve emitir birthdate 1990-01-01. Preservar ISO válido e campos existentes.
- [x] Executar RED, corrigir a normalização conforme CartoesModel.java/DataUtil.java, executar GREEN e suíte core/data.
- [x] Commit detalhado e revisão da tarefa.

### Task 2: Comprovantes, Teimosinha e Pix de compras

**Files:** core/data RemoteTicketsRepository.kt/RetrofitRetailRemoteDataSource.kt, core/network LegacyBackendApi.kt, feature/tickets BetDetailRules.kt/BetDetailRoute.kt/TicketsViewModel.kt/TicketsDataRoutes.kt e testes correspondentes. Criar helper focado de clock/receipt se necessário.
**Interfaces:** mesmos endpoints PDF; PurchaseDetails.serverDateTime existente; ConfirmedBet usa campos reais quantidadeTeimosinhas/troca/situacao.
- [x] Regressões RED: Base64 JVBERi0xLjcK produz %PDF-1.7\n e Disable-Crypto:true; binário PDF válido não corrompe.
- [x] Regressões RED: quantidadeTeimosinhas 3, troca false, situação 11 aciona aviso original; troca true não. Recorrente apurada não premiada apresenta explicação do primeiro concurso; não apurada/premiada respeitam branches CX.
- [x] Regressão RED: servidor 15:00, expiração 15:10, aparelho 15:15 mantém 600 segundos iniciais e permite consulta. Avançar relógio monotônico expira; recriação não estende prazo.
- [x] Corrigir o consumidor mínimo e rodar suites afetadas Data/Network/Tickets e UI focada em 5558.
- [x] Commit detalhado e revisão da tarefa.

### Task 3: Biometria conforme CX

**Files:** feature/account LoginRoute.kt/LoginViewModel.kt, core/auth AppAuthAuthenticationGateway.kt e contratos/credenciais relacionados; testes Account/Auth JVM e AuthenticationReturnScreenTest.kt.
**Interfaces:** manter AuthenticationGateway e garantias de CAS/owner; evento de fallback SSO consumido uma única vez.
- [x] RED: erro/cancelamento do prompt e refresh revogado abrem SSO, sem voltar à origem antes da tentativa pelo navegador.
- [x] RED: ativação sem matrícula oferece diálogo/matrícula; hardware indisponível oculta linha. Desativar pede Sim/Não; Não preserva credencial e switch.
- [x] RED: refresh biométrico sem ID token só aceita access token verificado e vinculado à identidade/contexto da credencial. ID inválido presente não permite fallback.
- [x] Implementar equivalência CX, verificar suites Auth/Account e UI focada em 5558.
- [x] Commit detalhado e revisão da tarefa.

### Task 4: Seletor de Teimosinha e quantidades

**Files:** feature/betting/src/main/java/br/gov/caixa/loterias/feature/betting/BettingOptionDialog.kt; feature/betting/src/androidTest/java/br/gov/caixa/loterias/feature/betting/BettingComposeOptionsTest.kt e regressões focadas de geometria.
**Interfaces:** manter callbacks onSelect/onDismiss e parâmetros recebidos do serviço; sem alteração de preços/quantidades do Domain.
**Evidence:** capturas cx-experimente-teimosinha.png e compose-experimente-teimosinha.png em build/verification/compose-cx-runtime-20261013. CX DialogUtils.buildDialogListItensNovo/layout_dialog_itens_novo/WheelController são a referência.
- [x] RED: ao abrir, Confirmar desabilitado e nenhuma opção selecionada; tocar uma opção habilita confirmação; cancelar não altera aposta.
- [x] RED: 0/2/3 concursos inicialmente visíveis inteiros, sem padding de centralização que oculte terceira opção. Rolagem/tap seguem WheelController CX.
- [x] RED: seletores de quantidade usam a mesma altura 170dp/estilo e sem confirmação automática da seleção anterior.
- [x] Ajustar somente o picker/estilos às fontes e medidas nativas, verificar UI com input real e suíte de betting.
- [x] Recapturar Teimosinha real e valor de três concursos (R$18) no Compose, commit detalhado e revisão da tarefa.

### Task 5: Inclusão no carrinho mantém o volante

**Files:** feature/betting BettingGameViewModel.kt/BettingGameRoute.kt e testes; app LoteriasApp.kt apenas no consumidor da inclusão do simulador, se necessário.
**Interfaces:** conservar serviço AddBetToCartUseCase, preço e validação; sucesso não envia nova aposta. Resumo atualizado do carrinho confirmado recebido/reconsultado.
**Evidence:** CX AdicionarApostaCarrinhoHelper.java:299-310, SimulaActivity.java:1962-1968 e LotecaActivity.java:1354. Capturas cx-experimente-local-cart-added.png/cx-experimente-local-cart-ack.png; total1item/R$18. Source aplica ao simulador manual e Surpresinha, visitante e logado.
- [x] RED: sucesso de inclusão no simulador não chama navegação para cart/included; exibe Aposta adicionada ao carrinho com sucesso! e Entendi.
- [x] RED: reseta números/trevos/time/mês/partidas, Teimosinha e espelho como zerarApostas; resumo recebe carrinho confirmado, quantidade/preço padrão restaurados. Erro não reseta; segunda inclusão após Entendi funciona; sucesso não duplica submit.
- [x] Corrigir estado/consumidor e conferir o fluxo real visitante, preservando as rotas de combo/bolão/Rapid e consumidores externos.
- [x] RED/Correção: cabeçalho do jogo id9 mostra +milionária como o enum/controller CX, mesmo quando o backend envia Mais Milionária; concursos especiais mantêm títulos próprios. Confrontar também o deslocamento observado de 4dp no conteúdo dos trevos (capturas cx-experimente-plus-clovers/compose-experimente-plus-clovers) com XML/controller antes de ajustar.
- [x] Suíte betting e UI focada, commit detalhado e revisão.

### Task 6: Rótulos e complementos de item no carrinho

**Files:** feature/cart CurrentCartPresentation.kt/CurrentCartRoute.kt e testes CurrentCartRuntimeParityTest.kt/CartSummaryIntegrationTest.kt ou novo teste focado.
**Interfaces:** manter CartItem e serviços; alteração de apresentação do item individual.
**Evidence:** capturas cx-experimente-local-cart-final.png e compose-experimente-cart.png. CX Teimosinha top982px, na linha do valor; Compose top1050px abaixo da altura da coluna de valor. Números têm ordens distintas pela sequência manual de toque, não é divergência do mapper.
- [x] RED: Teimosinha depois das dezenas na coluna esquerda, sem deslocamento causado pela altura da coluna de preço. Demais badges/complementos seguem ApostaViewHolder/layout CX e não se sobrepõem ao valor/concurso.
- [x] Corrigir composição/estilo nativo dos indicadores/complementos: ícones DS existentes Mirror/Surprise/Recurring, drawablePadding 6dp, bold14sp resolvido pelo tema Android do CX: AppTheme.NoActionBar herda font_family_futura (Book400/Bold700), gap horizontal12dp/vertical8dp. Preservar títulos, itens sem indicadores e rodapé já comparados.
- [x] RED/Correção: toolbar visitante com apenas excluir alinha o controle ao final, sem reservar slot de favorito invisível; seguir XML/CarrinhoActivity (44dp por ação e margem final16dp). Autenticado mantém duas ações na posição nativa.
- [x] UI focada e suíte/check de cart sem relaxar cobertura; recaptura real; commit detalhado e revisão.

### Task 7: Recusar login retorna da tela de dados pessoais

**Files:** app VisitorNavigation.kt/LoteriasApp.kt e testes de navegação correspondentes.
**Interfaces:** manter deferInitialLoad de PersonalDataViewModel; nenhuma consulta privada antes de login.
**Evidence:** CX AlertDialogExperimenteLogarSingleton.java:48-62 termina DadosPessoaisActivity em Não. Compose abre formulário sob o prompt, mas onNegative apenas limpa visitorLoginRequest e deixa formulário na tela.
- [x] RED: recusar login quando o destino pessoal está aberto retorna à tela anterior; prompt iniciado da Home não fecha Home e não faz navegação adicional.
- [x] Correção mínima do callback/política; Sim preserva continuidade da autenticação e rota pretendida, sem múltiplos pops ou consulta privada.
- [x] Verificar JVM e navegação/UI focada; recapturar o fluxo real visitante, commit com arquivos e revisão.

### Task 8: Geometria do volante Super Sete e complementos

**Files:** feature/betting BettingGameRoute.kt e testes Android focados de geometria/seleção.
**Interfaces:** manter SuperSevenColumns, restrições Domain, preços e envio; apenas apresentação da matriz.
**Evidence:** comparação real com pacote conferido: cx-continuation-super7/compose-continuation-super7 em build/verification/compose-cx-runtime-20261013. CX primeira bola [89,1002][221,1134]; Compose [90,966][222,1098], 36px/12dp acima. Cabeçalho nativo TextView de coluna tem altura105px; Compose usa Text dentroBox35dp e deve comparar o Box, não confundir bounds de glyph com view. Referência real SimulaFragment.java inflates fragment_cartela_novo.xml (não fragment de resultado), adaptador SuperSeteAdapter.java.
- [x] RED: distribuição horizontal das sete colunas segue LinearLayout nativo, incluindo arredondamento residual: bolas CX x89/250/411/572/734/896/1058; Compose antigo90/252/414/575/736/897/1058, todas132px de largura. Cabeçalhos CX155px nas primeiras4 e156px nas últimas3. Usar LinearLayout Android medido como oracle, preservando floor do alinhamento da bola na coluna.
- [x] RED: primeira bola/posição e tipografia conforme XML/controller nativo; sete colunas preservadas, seleção e rolagem até dígito9 funcionam.
- [x] RED/Correção: métricas da toolbar comum conforme activity_simula_aposta.xml/cabecalho_simula.xml/controller: Native Surpresinha texto279,402–468 versusCompose270,402–474; botões quantidade/Teimosinha Nativey567–702 versusCompose570–705. Confrontar dimensões, fontes, padding/borda antes de corrigir, preservando prompt manual em750–816 e linhas já coincidentes.
- [x] RED/Correção: prompt de mês/time segue margem comum do XML (16dp menos borda1dp), como trevos corrigidos na Task5. Par real DayMonth: CX prompt750–816/Janeiro864–995, Compose738–804/Janeiro852–983; confirmar +4dp comum sem alterar espaçamentos das opções.
- [x] Confrontar XML/controller/fontes e medida nativa independente antes de ajustar margem/estilo; não mudar Domain ou copiar expectativa da própria implementação.
- [x] RED/Correção: rolagem preserva o prompt textSelecionados fixo (activity_simula_aposta.xml423–445) e cabeçalhos Super7 fixos (fragment_cartela_novo.xml16/134), com apenas a grade de dígitos rolando. Manter posição inicial/scroll até9/seleção; prompt fixo também nos outros volantes manuais conforme a mesma estrutura nativa. Não deixar esse desvio conhecido como pendência apenas documental.
- [x] Corrigir discrepância, verificar betting check/coverage e UI focada em5558, recapturar real, commit com todos arquivos e revisão.

### Task 9: Seletor e cartela da Loteca

**Files:** feature/betting BettingSportsContent.kt/BettingGameRoute.kt/BettingGameViewModel.kt e testes correspondentes.
**Interfaces:** preservar serviços/parâmetros e tabela de preços, seleções Loteca, limites e payload. Fonte atual LotecaActivity/LotecaFragment em view/features/volantenovo, fragment_cartela_novo.xml, adapter/item da partida nova.
**Evidence:** capturas cx-continuation-loteca/compose-continuation-loteca com pacote conferido. CX possui botão15palpites [99,664][435,799] e Selecionados0/15; Compose omite ambos. Primeiro Jogo nativo871 versusCompose669; cards/UF/fonte também divergem. CX popup cx-continuation-loteca-quantity tem title Quantidade de Palpites, body Escolha em quantos palpites você quer jogar:, labels15/16/17palpites sem preço; confirma inicialmente desabilitado.
- [x] RED: botão palpites abre opções15..26 conforme LotecaActivity.configuraParametrosJogo (lista independente da tabela de preços do serviço), confirmar muda alvo, cancelar não muda; contagem totalSelecionados/alvo e contadores partida/simples/duplas/triplas respondem aos toques.
- [x] RED: redução do alvo limpa seleções como LotecaActivity.onQuantidadePalpitesDialogListener; aumento preserva. Preservar aviso real de aumento, uma vez: Você aumentou a quantidade de números, o valor será alterado. Fique atento ao valor final da sua aposta. Captura cx-continuation-loteca-16-confirmed prova esse branch no CX. Inclusão usa combinação válida e preço/payload existente; erro/cancelamento preservam.
- [x] RED: diminuir de18 para17 limpa cartela e mantém alvo17, sem aviso de aumento (LotecaViewModel.handlePalpitesLotecaAtualizado compara novo versus anterior; LimparReducer.limparLoteca preserva alvo).
- [x] RED/Correção: Limpar do rodapé realmente limpa outcomes da Loteca e scores da Lotogol conforme LimparReducer, preservando quantidade/concurso; o callback atual clearCurrentStep NUMBERS só limpa números/Super7. Cobrir toque real no rodapé e continuação.
- [x] Reproduzir toolbar/seletor/margens do XML/controller; posições dos jogos/cartões, nome e UF com prefixo/ nativo, estilo/fonts/ícones conforme fonte. Não usar expectativa de geometria derivada da própria implementação.
- [x] Check/coverage betting e UI focada5558, recaptura real, commit com todos arquivos e revisão.

- [x] Conferir com captura síncrona/oracle e corrigir somente se reproduzido o estado visual inicial do picker compartilhado: nenhuma linha destacada e Confirmar cinza enquanto pending==-1, conforme capturas CX/Compose da quantidade Loteca; BettingOptionDialog.kt/testes focados autorizados, mantendo os contratos e viewport da tarefa4.

### Task 10: Entrada visitante por áreas privadas do drawer

**Files:** app VisitorNavigation.kt/LoteriasApp.kt e consumidores/ViewModels das rotas protegidas apenas quando necessário para adiar carga; testes correspondentes.
**Interfaces:** nenhum serviço privado antes de autenticação; manter guard das ações diretas Home/bottom, continuidade Sim e isolamento da identidade. Destinos atualmente requiresVisitorLogin: account/personal-data, tickets/my-bets, tickets/purchases, betting/favorites, cart/favorite-carts, payment/cards, responsible/form.
**Evidence:** CX via drawer abre MinhasApostasActivity sob prompt de login; Não termina Activity. Compose só faz isso para Dados pessoais e demais destinos pedem login sobre Home. Native AlertDialogExperimenteLogarSingleton.java:48–62 termina as Activities privadas. O fluxo direto HomeV2/bottom apresenta prompt sobre Home em ambos e deve permanecer assim.
- [x] RED: drawer visitante abre a apresentação inicial da rota protegida sob o prompt, mas zero consultas privadas são disparadas; não renderiza dados de sessão anterior. Começar pelo par comprovado tickets/my-bets e confrontar cada outro destino com controller/entrada nativa antes de estender.
- [x] RED: Não fecha apenas a rota aberta pelo drawer; prompt de ação Home/bottom preserva Home. Sim continua login/rota pretendida e carrega uma vez após autenticação da identidade atual, sem múltiplos pops/cargas e sem reaproveitar snapshot privado antigo.
- [x] Reusar telas Compose existentes com estado inicial e deferir init/load/resume quando visitante; não desenhar placeholders ad hoc nem buscar dados privados sem sessão. Preservar os contratos de carregamento dos outros consumidores.
- [x] Testes JVM/Android focados e checks dos módulos afetados, sem reduzir gates; recapturar par real drawer/Não no Experimente. Commit com lista de arquivos e revisão.

- [x] Conferir também métricas do aviso visitante: CX mensagem1336–1593/ações1641–1803, Compose1332–1620/1668–1812. Corrigir fonte/lineheight/minheight apenas com oracle Android nativo; core/design-system componente e teste focado autorizados se necessário. Capturas cx-task10-* cobrem compras/favoritos/carrinhos/cartões, Não→Home; autoavaliação apresenta welcome público e Seguir fecha welcome antes de form/login, Não→Home. BottomMybets continua guard sobreHome.

### Task 11: Completar Loteca aleatoriamente pelo rodapé

**Files:** feature/betting ShakeSelection.kt/BettingGameRoute.kt/BettingGameViewModel.kt/BettingRules.kt apenas se necessário, testes JVM/Android correspondentes.
**Interfaces:** mesmos preços, payload e inclusão; geração é local e não envia aposta. Fonte LotecaFragment.preencherPartidasAleatoriasLoteca/tentarPreencherPartidasLoteca e CompletarApostaReducer.
**Evidence:** cx-loteca-random-initial: Aposta aleatória enabled=true; compose-task9-loteca-real botão disabled porque randomEnabled exclui Loteca. Native tap→cx-loteca-random-completed: Partidas14/Simples13/Duplas1/Selecionados15/15/R4; Completar jogo disabled/Adicionar enabled, carrinho permanece24/2.
- [x] RED toque real no botão gera cartela válida, respeita alvo15..26, contadores e valor do serviço, habilita inclusão; quando completa, Completar fica desabilitado. Preservar rótulos Aposta aleatória/Completar jogo conforme seleção.
- [x] Confrontar algoritmo atual withShake, que escolhe primeiro preço sem respeitar alvo, com prioridades nativas: preservar com alvo atual, relaxar preservação por prioridades1seleção/2seleções/geral, somente depois tentar alvos maiores até26. Cobrir preservação de parcial válida, relaxação necessária, alvo não mínimo, preço esparso/impossível e aviso de aumento se alvo muda, sem loop infinito ou submit duplicado.
- [x] Refinar apenas a tipografia inteira/Flow vertical center dos contadores CAIXA14sp e Selecionados: captura final cards/grid coincidem, porém Partidas group1 top508/height58 vsNative509/59 e demais label bounds diferem1px; usar toolbarXML/NativeTextView oracle, sem compensações mágicas de posição.
- [x] Não alterar os contratos de Shake/renovar dos demais jogos; cobrir ingresso real do rodapé e segunda seleção após Limpar. Não habilitar Lotogol sem fonte/runtime de suporte.
- [x] Verificação focada/check/coverage, commit com todos arquivos, revisão e recaptura PRD.

### Task 12: Estado de indisponibilidade conforme o CX

**Files:** feature/onboarding StartupStatusScreen.kt e teste Android focado; helper de métricas somente se necessário.
**Interfaces:** preservar StartupStatusKind, classificação disponibilidade/offline/update e callback de nova tentativa; sem alteração de endpoints ou gate de disponibilidade.
**Evidence:** captura real cx-loteca-random-discard-home e compose-task9-final-login exibem LTR-503. Native AppIndisponivelActivity.setContentView usa activity_app_indisponivel_novo.xml. Versão nativa CAIXA std regular14sp, Compose usa Futura/lineHeight17sp; botão texto nativo semibold16sp sem lineHeight Material24sp. Footer acumulado move texto Tentar novamente nativo2368–2435 versusCompose2376–2448, versão2576–2635 versus2586–2637, erro2635–2694 versus2637–2694.
- [x] RED: oracle independente de TextView/LinearLayout realmente inflado/tema e fontes nativas compara fonte/altura dos textos, ícone20dp + margem16dp, botão44dp, espaço36dp antes da versão,30dp após erro. Corrigir tipografia/medidas para eliminar deslocamento acumulado sem números mágicos da captura.
- [x] Preservar textos/estado real, ação retry e casos offline/update; conferir responsividade sem regredir controles. Sem consultas extras ou relaxar acesso por indisponibilidade.
- [x] Check/coverage onboarding e UI focada, commit com todos arquivos, revisão e recaptura real.

## Integração final

- [ ] Comparar Mega-Sena/Teimosinha, demais modalidades especiais, carrinho visitante e ferramentas públicas no CX/Compose real em Experimente; registrar divergências observadas.
- [x] Revisão e validação da árvore integrada: modernCheck/releaseCheck aprovados; 80 destinos renderizados entre passagem completa e repetição dirigida de favoritos. Escopo, dois raster preexistentes pendentes e resultados em `docs/DS_CONSOLIDATION_20261014.md`; isso não homologa os estados reais da primeira linha.
- [x] Atualizar relatório e pendências reais de homologação sem afirmar equivalência de estados inacessíveis: `docs/DS_CONSOLIDATION_20261014.md`, inventário DS, status, checklist e matriz visual atualizados. O inventário de arquivos da etapa Experimente continua histórico; o relatório novo delimita as extrações posteriores.
