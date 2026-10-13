# Paridade visual do fluxo logado — revisão física de 05/10/2026

Status: em revisão. Igualdade visual de 100% ainda não certificada.

Referência visual principal: APK oficial instalado no XCover7, versão1.24.0/230102400, SHA-256 `2e8ef3bd96c3db48a4391695429bf8531ccae72a821e3683a742bc61eb90dc3e`. Nas divergências prevalece esse binário; recursos/callbacks da pasta legado só complementam a interpretação. Referência de código: /Users/ubiracysantos/Projects/legado/simlo-app-android. Implementação: checkout Compose simlo-app-android, módulo :app. Comparações no XCover7, 1080×2408, density450, fontScale1. O pacote oficial foi preservado; o candidato usa backend mock e pacote paralelo .hmp.composevalidation. Valores e dados distintos não constituem pares completos. Nenhuma compra, confirmação de aposta, exclusão de cartão ou gravação de favorita foi feita no app oficial.

## Evidência desta rodada

- Capturas físicas válidas de Home/menu, Minhas apostas, Meus Cartões, Termo de uso, Carrinhos Favoritos/detalhe, Dados Pessoais e simulação Mega-Sena. Seleção local de seis números e abertura/cancelamento do diálogo de favorita no legado, sem confirmar.
- Fontes e desenhos originais reutilizados. Fonte CAIXA Regular conferida por hash. Cores, estilos e métricas rastreados em XML e nos overrides dos controladores.
- Meus Cartões: altura do cartão medida no screenshot original e no candidato:464px nos dois. O item tinha sido comprimido a447px pelo pai; corrigido. Isso verifica essa dimensão, não a tela inteira.
- Termo de uso: cabeçalho comparado em recorte comum;169280 de169560pixels idênticos. Conteúdo HTML mock é distinto e não prova paridade do documento real.
- Minhas apostas: largura de ações corrigida para93dp, pois48dp cortava a ajuda. Backend define o trevo; não substituído por uma cor fixa para simular dados de outra rota.
- Simulação: crash de InsetDrawable corrigido com renderer Android; segundo erro real de MaterialButton corrigido usando o Theme.SimulaMaterial original, separado do tema AppCompat do Rapidão.
- Teste Android BettingGameRouteSmokeTest no XCover7: primeiro vermelho por Theme.MaterialComponents; reexecução após correção OK(1test), abre a tela completa, verifica ajuda e seleciona01 sem enviar apostas. Logs: /tmp/exact-simulation-smoke-xcover.log e /tmp/exact-simulation-smoke-xcover-green.log.
- Debug APK, Kotlin Release, migração, CaixaDS e inventário passaram na rodada /tmp/exact-logged-capture-final-build.log. Testes afetados sem falha nas respectivas rodadas: betting84, account55, tickets33, support14, app48 do flavorAutomacao, data158 (rodada anterior deste turno). Esses resultados não certificam todos os layouts nem contratos reais.

Ajuda geral: Minhas apostas abre TermosUsoActivity com aba Dúvidas ativa e título Loterias Caixa. Corrigido destino para support/terms?tab=FAQ; documento sem abas permanece support/terms-view. Dúvidas da simulação passou a usar esse destino; Como jogar conserva a ajuda local.

## O que ainda falta

1. Certificar os pares físicos restantes de todas as modalidades, complementos, foco/IME, loading/erro/vazio e diálogos. A matriz de fixtures cobre as modalidades e estados listados nos relatórios; não equivale a um par oficial em cada combinação.
2. Parear as telas financeiras com uma referência controlada que forneça compra, cobrança e prêmio equivalentes. Contratos e interações usam fixtures sintéticas; não foi confirmada operação financeira real.
3. Conferir conteúdo remoto equivalente (concursos, datas, valores e HTML). Mega-Sena usa o mesmo concurso público3067, sorteio06/10/2026 e preços do instalado; outros conteúdos não foram todos pareados.
4. Exercitar restauração após morte real do processo no NavHost. SavedStateHandle cobre perda de resposta, reentrada, proprietário e cobrança inválida em testes JVM; isso ainda não prova restauração integral de navegação no aparelho.
5. Certificar raster final (fontes, bordas, sombras, ícones e insets) em cada estado. Igualdade visual integral permanece em revisão.

Capturas: build/verification/exact-layout-20261005. A imagem invalid-simulation-returned-legacy.png foi descartada como prova Compose: uma falha havia devolvido o foco ao app oficial. Capturas novas do candidato conferem foco antes do screencap. Documentos de detalhes: exact-betting-parity-2026-10-05.md, exact-account-commerce-parity-2026-10-05.md, exact-home-tickets-retail-parity-2026-10-05.md e exact-favorite-bets-parity-2026-10-05.md.

## Candidata da rodada anterior

APK mock paralelo: `build/verification/exact-layout-20261005/simlo-exact-logged-layout-mock.apk`; SHA-256 `68c5025c618e61c6cc013e730777f9fabed15dddbd6b521b844edaabcccd485b`. Build final, compilação Kotlin Release, testes app48/support14 e cinco gates passaram em `logs/exact-logged-faq-final-build.log`; rodada anterior deste conjunto também executou betting84/account55/cart76. Smoke físico ampliado OK1 inclui um único callback de Ajuda por toque, alternância Surpresinha/Manual e zero envios de aposta. Navegação Minhas apostas → Ajuda abre aba Dúvidas com título Loterias Caixa; ambas as abas recapturadas no APK final. Conteúdo remoto não pareado. Links das respostas agora replicam detecção, validação, estilo e confirmação literal do legado; cinco novos testes JVM passaram, interação física desse diálogo de URL ainda não executada.

Resumo de verificação: `build/verification/exact-layout-20261005/verification-summary.json`. Logs preservados no subdiretório `logs`. Status global permanece em revisão, não100%.

## Continuação solicitada: referência, estados e transações

Plano: docs/superpowers/plans/2026-10-05-paridade-referencia-instalada.md. APK/recurso extraído em build/verification/installed-reference-20261005; manifesto e hash em reference.json. Matrizes desta rodada: installed-betting-matrix-2026-10-05.md, installed-account-states-2026-10-05.md e installed-commerce-validation-2026-10-05.md.

Achados reproduzidos: mesma VM Pix fazia segundo POST em reload existente, pending e retorno de redirect; três testes vermelhos antes da correção. Testes Payment/RetaiI verdes após correção, sem envio financeiro real. Aliases de resgate com betId100 e preview de sucesso passam a fixtures mock; produção direciona às apostas com ID real. Prévia explícita mock não sobrepõe loading ou resultado real.

XCover7 desbloqueado e utilizado para a rodada controlada. Build Debug, Kotlin Release,481testes JVM afetados e os cinco gates passaram em installed-parity-final-validation.log. Outra rodada de build/gates passou após o calendário instalado. Pagamento Pix6, resgate2 e detalhes de compra6 passaram no XCover7, sem operação financeira real. Testes de conta/apoio/modalidades e recaptura após ajustes visuais estão sendo consolidados abaixo.

Revisão adicional corrigiu perda de resposta Pix: tentativa incompleta entra em recuperação, sem novo POST; restauração exige proprietário e cobrança válidos. Repository exige ID da compra consultada antes de reconhecer aprovação. Detalhes de compra respeitam offset temporal e ignoram falha tardia de outra compra. O alias de Pix expirado passa a mock exclusivo; backend real direciona às compras antes de criar VM.

Ajuda da simulação: causa encontrada no ScaleType.FIT_CENTER, enquanto ImageButton original usa CENTER. O arredondamento da borda1dp também precisa usar roundToInt na densidade450. Correções e recaptura final são registradas na matriz específica.

## Candidata e verificação desta continuação

APK `build/verification/installed-reference-20261005/simlo-installed-reference-mock.apk`, pacote paralelo `br.gov.caixa.loterias.apostas.hmp.composevalidation`, SHA-256 `13d2039384b3f58040a93d030a22c44d82ccfedb691223f82f86be52a6fedd2e`. Build Debug, compilação Kotlin Release e cinco gates verdes em `logs/installed-parity-delivery-build.log`. Resumo reproduzível: `verification-summary.json` no mesmo diretório. Testes JVM dos sete módulos registrados:565, sem falhas; os481de contratos financeiros/host/core e84de betting foram executados nas rodadas finais.

No XCover7 foram executados34cenários únicos controlados nesta continuação:11modalidades,2seletores,loading/retry,7etapas do Rapidão,parMega,6Pix,2resgate,6detalhescompra,2conta,1apoio e1buscaTimemania. O teste de busca abre teclado, filtra resultado/vazio e seleciona time sem envio, com4PNGs/metadados. Recapturas repetem cenários para conferir as correções visuais e não somam casos únicos.

Ajuda Mega: recorte14.300/14.300pixels iguais; quantidade1.155.990/1.155.992iguais, diferença de2pixels na borda. `physical-pair-metrics.json` registra região e bounding box. A primeira bola mede124×124px em ambos. Fonte do botão disabled foi conferida no DEX `SimulaActivity.fonteBotaoBold`: regular disabled, semi-bold enabled; candidata final recapturada com essa regra. Outros pares e conteúdo seguem as pendências acima.

Artefatos físicos em `xcover-final-betting/`, captura de matriz em `xcover-betting-matrix/`; dados sintéticos identificados nos arquivosTXT. A referência oficial permanece instalada e logada. Pergunta de disponibilidade de homologação com compras/prêmios equivalentes permanece pendente; nenhum teste local foi declarado aprovação financeira real ou certificação100%.

## Rodada das pendências solicitadas — referência instalada

Esta seção atualiza os registros históricos acima. A sessão oficial expirou durante a comparação e voltou ao Login. A referência continua sendo o APK instalado; a pasta legado complementa métricas e comportamento. O pacote oficial foi preservado. Não foram realizadas compras, pagamentos, resgates, gravação de favorita ou cadastro reais.

### Correções concluídas

- Modalidades: bolas CAIXA e trevos nativos, cor selecionada correta, aliases de IDs Quina/Lotomania, faixa/checkbox de espelho, indicadores e ações por etapa, grade Lotofácil e colunas Super Sete. Surpresinha mantém seis máscaras originais; +Milionária altera números/trevos, conserva quantidade de jogos e calcula os preços observados. Completar usa estado e cores desabilitados quando a seleção está completa.
- Diálogos: renderer Android com HtmlCompat para ajuda e avisos; literal instalado de aumento de números; confirmação de saída da aposta em edição, cancelamento preservando cartela. Teimosinha testada com gesto real, centro da wheel e um único callback. Favorita/teclado/sobrescrita/duplicada exercitados com callbacks controlados, sem gravação.
- Carregamento: simulação e preparação na Home usam o overlay original compartilhado, com ícone, animação, atraso e texto; parâmetros já verificados chegam à VM sincronamente, evitando uma segunda consulta e um frame de carregamento desnecessário. Erro e nova tentativa permanecem funcionais.
- Conta: checkbox Android, cores de consentimento/rodapé e helper CEP corrigidos. Capturas cobrem teclado, campos inferiores, erros e scroll explícito. Isso não certifica autoscroll/cursor em dados oficiais equivalentes.
- Financeiro: CVV com sobreposição original, fontes e scroll; Pix sem espaçador180dp, ações expiradas na posição original, spans CAIXA em dp, botões Android e indicador de consulta nativo. Fontes/espaçamentos dos resgates alinhados aos recursos instalados.
- Continuidade: morte real do processo comprovada em Android16 no emulador e XCover7, preservando a mesma tarefa/cobrança mock, com um POST inicial e zero POSTs após restauração. Essa prova reinstala o mesmo proprietário sintético; não comprova persistência da autenticação de produção.
- Federal: rastreada no APK e fonte como seleção de resultados; não há entrada de aposta Federal na Home instalada.

### Evidências e limites atuais

Build Debug, compilação Kotlin Release,809 testes JVM e os cinco gates passaram em `build/verification/remaining-installed-20261005/logs/remaining-delivery-green-validation.log`. Os resultados físicos finais e o hash do APK estão em `verification-summary.json` nesse diretório. Os relatórios específicos são `remaining-betting-parity-2026-10-05.md`, `remaining-account-parity-2026-10-05.md`, `remaining-financial-parity-2026-10-05.md` e `remaining-pix-process-restoration-2026-10-05.md`.

Dados públicos usados nos pares: Mega3067, Lotofácil3798, Quina7136, Timemania2451, Super Sete908, +Milionária396 e Lotomania2985. Identificadores de times e conteúdo de ajuda das fixtures são sintéticos quando não observados; metadados distinguem esses casos. Loteca1274 está fechada para aposta: sua suposta captura selecionada é um aviso negocial, portanto não foi usada como prova de seleção. Quina manual foi capturada durante transição; o header Lotofácil manual mantém um botão pressionado; corpo do espelho Lotomania está em scroll diferente. Esses recortes foram excluídos das certificações.

Ainda falta fechar paridade visual integral: pares oficiais de Dia de Sorte/Dupla Sena e Loteca aberta, recaptura estável da Quina, estados equivalentes de conta/IME/cursor, HTML completo de ajuda/termos, resultados remotos e transações financeiras equivalentes. As métricas de imagens registram diferenças residuais de raster/posicionamento e não certificam100%. A indisponibilidade da sessão e dos dados equivalentes foi registrada; teste controlado não substitui essa comparação.

Candidata mock paralela: `build/verification/remaining-installed-20261005/simlo-remaining-parity-mock.apk`, SHA-256 `97637e6fb6c02eec1889ee0f39d4c3345fe77b220b8a0f5ffbaca04eb29683da`, cenário HAPPY_PATH, logging de rede desativado. O APK da prova de processo é separado e atestado em `process-probe/`.

Execução final física:51 cenários únicos controlados passaram no XCover7 (29 de modalidades/diálogos/loading,2 conta,3 apoio HTML,11 cartão/Pix e6 resgate). O batch final de modalidades passou15/15; a matriz adicional passou14 cenários, com a asserção antiga de spinner corrigida e validada no batch final. Recapturas repetidas não foram somadas. Não são51 pares oficiais certificados.

Última recaptura após alinhamento Super Sete:6/6 passaram, sem somar os cenários repetidos. Build/JVM/gates adicionais passaram em `remaining-super-centering-validation.log`. Autenticação real pós-morte do processo permanece pendente: o SessionTokenStore atual é em memória e a prova mock reinstala explicitamente o mesmo proprietário.

Recorte final da cartela Super Sete:99,6373% de pixels iguais, conforme `physical-public-final-pairs-metrics.json`, excluindo barras do sistema. Isso não certifica o header/rodapé, estados selecionados, outras densidades ou todas as modalidades. Candidata final instalada em paralelo; configuração stay-awake restaurada para0 e emulador de validação encerrado.
