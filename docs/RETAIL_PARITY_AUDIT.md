# Auditoria de resgate e lotéricas — 06/09/2026

Status: correções de código verificadas por compilação isolada e testes JVM; paridade visual integral e builds de todos os flavors ainda não homologados.

Revisão posterior de 07/09: `PRIZE_MP_REGISTRATION_AUDIT.md` integra o consentimento/cadastro Mercado Pago do código `018003`, o loading modal do resgate e a pausa sem reenvio ao retornar. Contém os testes e medições mais recentes; não substitui a homologação visual/Android pendente.

## Contratos confrontados

| Comportamento | Referência legada | Alteração e evidência atual |
|---|---|---|
| Validar e-mail Mercado Pago no clique | `ValidacaoUtils.validarEmail`, `JogosConfirmadosPremioMercadoPagoActivity` | Regex original, mensagem `E-mail inválido.`, sem envio quando inválido; testes de regras e ViewModel |
| Validar e-mail/chave Pix | `ChavePixTextWatcher`, `android.util.Patterns.EMAIL_ADDRESS` | Contrato próprio, preservando `+` e hífen no domínio aceitos pelo Pix; não reutiliza o regex mais restrito do Mercado Pago |
| Voltar ou cancelar Pix | `LoteriasAppActivity.onSupportNavigateUp`, `ResgatePixActivity.handleCancelar` | Seta/hardware back retornam às opções; botão de cancelamento na confirmação chama backend antes de sair; testes do ViewModel |
| Ajuda | `LoteriasAppActivity.configMenu` | Callback para Termos de Uso ligado a lotérica, Pix e sucesso do Pix, além de agência e Mercado Pago; recaptura interativa pendente |
| Valor líquido zero | `ResultadoApostaConfirmadaActivity.setaViews` | Mantém zero e mostra `você foi premiado!`; não apresenta valor bruto como líquido; teste do repository |
| Campo e ações Mercado Pago | `content_jogos_confirmados_premio_mercado_pago.xml` | Campo sublinhado com fonte CaixaDS, botão 184×84 dp, fonte 17,7 sp, rodapés de 72/176 dp; nova comparação visual pendente |
| Contador de lotérica | `ResgatePremioLotericaActivity` | QR 200 dp; avisos em 60/10/3 segundos; expiração e regeneração; testes de limites e ViewModel |

## Verificação executada

O Gradle padrão foi tentado e não iniciou: o ambiente restringiu a escrita do lock em `~/.gradle/wrapper/dists`. A alternativa executada foi invocar diretamente o compilador Kotlin 2.4.10, JUnit 4.13.2 e JaCoCo 0.8.13 usando dependências locais, gravando os resultados dentro de `build/verification/retail-jvm-20260906/`. Nenhum mecanismo de lock do Gradle foi alterado nesta verificação.

- 22 testes aprovados: `RetailRulesTest`, `PrizeRedemptionViewModelTest`, `LocationGateViewModelTest`, `RetailSearchViewModelTest`.
- 7 testes aprovados: `RemoteRetailRepositoryTest`, compilando o repository e o domínio atuais.
- Cobertura de linhas de ViewModels/UiStates/regras: **272/282 = 96,45%**, incluindo classes internas de corrotinas. Relatório: `build/verification/retail-jvm-20260906/coverage.xml`.
- Compilação isolada da UI com o plugin Compose e dependências locais. O erro de retorno do callback `Cancelar` foi identificado e corrigido.

O relatório inclui as regras de localização extraídas de `LocationGateRoute.kt` para `LocationRules.kt`, permitindo testar as mensagens e ações sem renderizar a tela. O gate Gradle foi ampliado para corresponder a esse escopo e gerar XML/HTML; a execução do gate pelo Gradle permanece pendente.

## Pendências para homologação

1. Executar `:feature:retail:check`, `:core:data:test`, `modernCheck` e `releaseCheck` com acesso normal ao Gradle/SDK.
2. Recapturar Mercado Pago inicial/sucesso, Pix CPF/outra chave/confirmação/sucesso e lotérica ativa/urgente/expirada contra o legado com a mesma fixture.
3. Exercitar ajuda, hardware back, cancelamento com sucesso/falha e comprovante em runtime.
4. Corrigir a fixture da lotérica que encerra a Activity legada antes da captura. As imagens do launcher não são referência de layout.
5. Concluir os cabeçalhos dos concursos especiais e homologar em runtime a janela temporal de `EncerramentoUtil` (encerramento individual versus revenda de cotas, usando hora do servidor), implementada no caso de uso conforme a revisão abaixo. O rodapé e a navegação protegida de Aposte agora foram implementados, mas a comparação visual/interativa ainda impede homologação integral. O CTA individual da Home foi ligado à política em 07/09; bolões, Combo, Instantânea, Rapidão e outras entradas ainda exigem auditoria própria.
6. Ampliar a auditoria de cobertura para os demais módulos e estados de UI. Os 96,45% acima não representam cobertura global do aplicativo.

## Resultado premiado — tabelas individuais, bolões e Teimosinha

A revisão de `ViewUtils.getFaixaPremiacaoDTO`, `ItemListaFaixaPremiacao`, `ItemListaFaixaPremiacaoBolao` e seus layouts identificou dados que a primeira migração descartava. O repository agora preserva `indicadorCotaBolao`, `nome` da faixa, situação do concurso, quantidade de acertos, sorteio e concursos não premiados. Mantém a ordem legada: faixas premiadas primeiro, concursos não premiados depois. Um prêmio por cota igual a zero não é substituído pelo líquido; valores ausentes continuam ausentes e são apresentados como `-`.

O Compose usa seis colunas para apostas individuais e três para bolões, com o total líquido apenas na primeira linha. Cabeçalhos, pesos das colunas, fontes e margens foram derivados dos layouts originais. A ação `Ver detalhe da aposta` volta ao detalhe, como o `finish()` legado, e usa a lupa original convertida para `CaixaDSIcons.PrizeBetDetail`, sem XML.

Verificação JVM atualizada após o rodapé: **78 testes aprovados** (33 de retail, 6 do novo caso de uso de entrada, 28 do simulador/regras e 11 de repositories). A cobertura de ViewModels/UiStates/regras de retail é **336/349 linhas (96,28%)**, incluindo 20/20 linhas de `PrizeResultRules.kt`. O caso de uso `PrepareBetEntry` e suas decisões registram **22/22 (100%)**; o simulador e `BettingRules` registram **436/456 (95,61%)**, incluindo corrotinas. Os XMLs estão em `build/verification/retail-prize-tables-20260906/`: `coverage.xml`, `entry-coverage.xml` e `betting-coverage.xml`.

A compilação isolada da tela, regras, tokens, faixa e ícones atuais com Kotlin/Compose 2.4.10 passou (`retail-ui.jar`). Também passaram os fontes alterados de `LoteriasApp`, `HomeScreen`, `AppContainer` e `ApplicationModule` (`app-host.jar`), usando dependências locais já compiladas. Isso verifica tipos e chamadas entre os componentes alterados, mas não substitui a geração Hilt/KSP, o empacotamento, R8 ou as builds dos flavors. Esses resultados não medem cobertura global nem certificam a aparência. A recaptura pareada, as ações em runtime e as builds Gradle continuam pendentes.

## Identificação da aposta e condição de entrada na agência

`troca`, `indicadorCotaBolao`, `combo`, `espelho`, `surpresinha` e `quantidadeTeimosinhas` agora percorrem repository e domínio até a faixa de identificação. A ordem e os rótulos são os de `montaTipoApostaLinha`: Aposta de Troca, Bolão, Combo, Espelho, Surpresinha e Teimosinha. Sem indicadores, a faixa não ocupa espaço. Teimosinha depende de quantidade maior que zero, não de uma inferência pelo intervalo de concursos.

O componente reutilizável `CaixaDSBetTypeStrip` preserva quebra de linha, ícones de 20 dp, espaçamento e semibold da faixa legada. Os seis `CaixaDSBetTypeIcons` mantêm os 19 caminhos (incluindo dois caminhos de recorte do Combo) conferidos contra os XMLs originais; nenhum XML de UI foi adicionado. O token `CaixaDSLotteryStyle.darkContent` separa a cor da fonte em fundo escuro da fonte em fundo claro. O cabeçalho deixou de acrescentar um intervalo de concursos que não existe naquela posição do layout legado, e preserva o hífen do nome da modalidade.

No canal agência, `ResultadoApostaConfirmadaActivity.apresentaOrientacoesResgate(false)` exige uma resposta bem-sucedida de geração do código antes de abrir a orientação. O ViewModel agora mantém as opções em caso de falha e permite nova tentativa; evita requisições duplicadas enquanto carrega. A seleção também fica limitada aos canais efetivamente retornados no detalhe do prêmio. QR vazio bloqueia a tela de lotérica, mas não a orientação de agência, que usa o mesmo serviço sem renderizar QR. Esses ramos e os indicadores foram cobertos nos testes JVM; navegação e apresentação em runtime continuam pendentes.

## Informações do concurso atual e Aposte agora

O rodapé usa a fonte de parâmetros de simulação, não os valores da aposta histórica. `hasNextContest` preserva a existência de `proximoConcurso` sem substituir o concurso atual pelo próximo. Como no `init()` legado, o resumo usa o primeiro parâmetro da modalidade e só preenche os valores quando existe próximo concurso para ela. O fechamento é apresentado em `dd/MM | HHhmm`. Falha na consulta não remove os canais de resgate e permite nova tentativa.

O CTA consulta `PrepareBetEntryUseCase`, injetado por Hilt. A política revalida o acesso da conta, bloqueia suspensão com o texto legado, encaminha pendências de cadastro/termos/autoavaliação e só libera a modalidade solicitada quando retornada e aberta. Falhas não geram navegação. O host remove o contexto de edição, retorna à Home e abre o simulador preservando modalidade e tipo de concurso; a Home recebe a modalidade para posicionar seu carrossel. O ViewModel consome a decisão uma única vez e bloqueia cliques duplicados.

A revisão identificou e removeu também o fallback que trocava uma modalidade explícita inexistente por outro jogo aberto no simulador. A escolha automática continua apenas na entrada genérica, sem modalidade solicitada. Desaparecimento da modalidade ou do tipo de concurso após a consulta gera erro e limpa o jogo anterior. O gate de betting passou a incluir as corrotinas do simulador, exigir cobertura maior que 95% e falhar se faltarem classes ou dados de execução; sua execução pelo Gradle ainda está pendente.

## Janela de revenda e hora do servidor

O mapeamento de `modalidadesDisponiveisCotas` agora preserva `concurso`, `dataEncerramentoRevenda` e `horaEncerramentoRevenda`. `PoolResaleWindow` compara modalidade, tipo e concurso, exige a flag remota e mantém os limites inclusivos de `EncerramentoUtil`. Datas inválidas/ausentes e intervalos invertidos não geram janela artificial. Em `PrepareBetEntry`, a janela é avaliada antes da mensagem comum de concurso fechado e produz o aviso para usar “Compre seu bolão”, sem liberar navegação ao simulador.

Conferência da flag: `SplashScreenActivity` grava `ConfiguracoesEnum.BOLAO` (`isBolaoHabilitado`) com `ParametrosConfiguraveisDTO.getBolaoHabilitado()`. O DTO usa `@SerializedName("habilitaRevendaBolaoApp")`; portanto o atual `HomeFeatureConfiguration.poolSalesEnabled` é o mapeamento correto. Foi acrescentado teste que distingue o nome remoto da antiga chave local e verifica o fallback desabilitado.

O relógio em memória é fornecido pelo Hilt e avança com `SystemClock.elapsedRealtime()`. O Gson dos quatro pontos de criação de clientes CAIXA observa `ApiEnvelope.dataHoraServidor`; Mercado Pago e RecargaPay permanecem sem esse observador. O código legado possui o método de sincronização do singleton, mas a auditoria não encontrou chamada a ele; a nova ligação foi explicitamente implementada, não presumida. A hora local é apenas a referência inicial. O relógio não é utilizado para validar assinaturas ou expiração de pins.

A correção também completa o provider de `PrepareBetEntry`, que tinha ficado com duas dependências para um construtor de quatro argumentos. Verificação JVM em `build/verification/pool-resale-window-20260906/`: **92 testes distintos aprovados**, considerando a reexecução atualizada dos quatro testes do relógio. Cobertura de linhas: relógio/parser **17/17**, entrada/janela **38/38**, observador Gson **8/8**. Relatórios: `common-coverage.xml`, `domain-betting-coverage.xml`, `network-clock-coverage.xml`. O gate de rede passou a incluir `ServerTimeAdapterFactory` e suas classes internas. Essas medições não representam cobertura global.

A tentativa Gradle desta revisão (`:core:common:test :core:domain:test :core:network:test :core:data:test --offline`) não iniciou por restrição de escrita no lock do wrapper. A homologação do alerta em runtime, suas ênfases visuais e a extensão da política ao clique direto na Home continuam pendentes.

A compilação isolada atual da UI Compose concluiu e gerou `retail-ui.jar`. O executor temporário teve uma falha de leitura após essa etapa; a etapa do host foi então executada separadamente e concluiu em 07/09/2026, gerando `app-host.jar` com os fontes atuais de `AppContainer`, `ApplicationModule`, `LoteriasApp` e `HomeScreen`. Isso confirma a compatibilidade de tipos dos pontos alterados, não a geração Hilt/KSP, R8, empacotamento ou execução dos flavors.

## Entrada individual pela Home — 07/09/2026

`PrincipalActivity` verifica suspensão no listener de `buttonHomeBet` e consulta a janela de `EncerramentoUtil` em `nextActivity`. O Compose navegava diretamente apenas com modalidade. Agora `HomeViewModel` executa o caso de uso compartilhado com `BetEntryRequest(modalityId, contestType, contestNumber)`. A política não substitui um concurso explícito desaparecido por outro normal/especial. A entrada pelo resultado histórico mantém o contrato de selecionar o concurso atual da modalidade, sem exigir o número antigo.

O ViewModel ignora cliques durante carga inicial, consulta em andamento ou decisão ainda não consumida. Falhas mantêm os parâmetros na Home, exibem alerta dispensável e permitem nova tentativa. A espera é modal, impedindo acionar outro destino enquanto a decisão está pendente. O host preserva a posição atual do carrossel quando a origem é a própria Home; somente a entrada a partir do resultado retorna/reposiciona a Home antes de abrir o simulador.

`updateHomeButtonsForPosition` mantém “Aposte”/“Aposte por” e usa a cor escura da modalidade, com texto branco nas apostas individuais exceto Timemania. Esses pontos foram ajustados no CTA Compose, retirando a substituição por “Apostas encerradas”; o clique permite apresentar a mensagem contextual de encerramento/revenda.

Verificação isolada: **100 testes aprovados** (84 de domínio/estados/formatação, 3 do envelope e 13 de repositories). Home, incluindo corrotinas: **127/130 linhas (97,69%)**; entrada/revenda: **49/49 (100%)**. Relatórios em `build/verification/home-bet-entry-20260907/feature-home-coverage.xml` e `core-domain-betting-coverage.xml`. O gate Gradle da Home inclui classes internas, exige mais de 95%, gera XML/HTML e falha se não houver classes ou execução. Sua execução pelo Gradle continua pendente.

A UI de resgate e os fontes do host também compilaram isoladamente. O host foi recompilado após os últimos ajustes de cor/texto do CTA e concluiu sem erros, gerando `app-host.jar` no mesmo diretório. Essa validação usa dependências locais já compiladas: não executa Hilt/KSP, R8 nem empacota APKs.

A política no CTA individual não certifica os outros caminhos de aposta. A preservação do número na segunda consulta foi implementada na revisão seguinte, descrita abaixo. Ainda falta revisar todos os demais acessos e homologar visualmente/interativamente alerta, loading e retorno. Cobertura medida aqui não equivale à cobertura integral do app.

## Identidade do concurso no simulador e inclusão especial — 07/09/2026

`BettingSimulationRoute` acrescenta o número validado aos argumentos já existentes de modalidade/tipo; o número também integra a chave do ViewModel. `expectedContestNumber` impede que a segunda consulta ou uma nova tentativa escolha outro concurso. Se o número desaparece, o simulador fica sem jogo válido e não permite inclusão; se reaparece, a tentativa pode recuperar exatamente aquele concurso. As rotas antigas sem número continuam válidas, e a reedição não fica presa ao concurso histórico.

A revisão também identificou que `BettingGameViewModel.addToCart` omitira `contestType` ao construir `BetDraft`: mesmo em uma tela especial, o valor padrão era `NORMAL`. Agora o tipo selecionado percorre rascunho e repository até `tipoConcurso.valor = E` / `descricao = ESPECIAL`, enquanto Normal continua usando `N`. Há testes para manual/Surpresinha no ViewModel e para a serialização dos dois tipos no repository; nenhum campo remoto de número de concurso foi inventado.

Uma falha na recarga agora invalida o jogo anterior para inclusão, em vez de manter uma aposta enviável com parâmetros antigos. Concurso fechado gera a mensagem específica e nenhuma requisição de inclusão. O envio marca `loading` de forma síncrona antes da corrotina; o botão e os métodos de ação bloqueiam chamadas durante carga/envio ou depois de sucesso. O teste legado de Dia de Sorte foi ajustado para abrir uma nova entrada após sucesso, pois o fluxo real já sai do simulador nesse ponto.

Verificação final desta revisão: **106 testes aprovados** (89 de domínio/estado/formatação/rota, 3 de envelope e 14 de repositories). Simulador/regras com classes de corrotinas: **447/467 linhas (95,72%)**; construção da rota: **2/2 (100%)**. Relatórios e artefatos em `build/verification/simulator-contest-20260907/`: `feature-betting-coverage.xml`, `apostas-coverage.xml`, `retail-ui.jar` e `app-host.jar`. A compilação isolada dos fontes atuais da UI e do host concluiu sem erros. Permanecem pendentes navegação Android em runtime, Hilt/KSP, R8, todos os flavors e a matriz visual completa; os percentuais não representam o app inteiro.
