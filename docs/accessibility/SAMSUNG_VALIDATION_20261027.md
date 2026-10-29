# Samsung - validação acessível das 14 features

**Rodada complementar de 27/10/2026, em execução; APK final instalado e sessão autenticada retomada.** Samsung SM-G556B, Android 16; app PRD existente, sem APK de testes ou limpeza de dados. TalkBack oficial Samsung ativo e fonte do sistema em 200%. A ativação foi conferida durante as capturas. A instalação final foi concluída; os retestes das últimas correções e a conferência auditiva continuam em andamento; esta página não declara aprovação integral das 14 features.

## Evidência humana e visual

O usuário respondeu literalmente **“Feito”** às conferências solicitadas de Home/menu, tutorial Shake, filtros de bolões, autoavaliação, Dados pessoais, escolha de pagamento, Dúvidas e controles desabilitados de Notificações. Nenhum defeito adicional foi relatado nessas respostas. Isso registra o roteiro solicitado, sem inferir leitura de todos os textos, todas as respostas de backend ou homologação integral da pronúncia. Os requisitos iniciais de leitura integral de carrinho e resultados não foram contados como aprovações. Após a instalação, o usuário confirmou ambos os roteiros com **“Leitura completa e correta”**: dez respostas positivas nesta rodada. O tutorial de Bolões recebeu uma resposta de falha: **“Só ficaram acessiveus o Fechar, Pular e o botão Vamos comecar”**; seu reteste permanece aberto. A confirmação anterior **“Sim, funciona no menu”** pertence à auditoria histórica.

Capturas demonstram layout e estado visível. Injeção ADB de toque/rolagem serve à navegação; não reproduz de modo confiável os gestos do TalkBack. Dumps UiAutomation foram evitados porque interferem no serviço. Nenhuma aprovação auditiva é deduzida de teste Robolectric ou screenshot.

| Feature | Percurso realizado nesta rodada | Situação que ainda exige conferência |
|---|---|---|
| Account | Login corrigido em 200%, SSO concluído pelo usuário; Dados pessoais conferidos sem salvar | Repetição no APK final; biometria e erros específicos de SSO não provocados |
| Betting | Grade pública e seletor de quantidade; tutorial Shake com confirmação humana | Retestar seletor corrigido; variantes de modalidades/roda e cabeçalho truncado |
| Cart | Carrinho autenticado e entrada/retorno de pagamento; total medido | Leitura integral confirmada pelo usuário; variantes de exclusão, expiração e reenvio não executadas |
| Home | Clássica, menu, cartão, rodapé e retorno; confirmação humana | Home V2 desativada nesta configuração; não equivale a validação física de V2 |
| Marketplace | Favoritos, busca de lotérica, bolões, carrossel e detalhe | Retestar tabs, cidade, carrossel e painel de compra corrigidos; TalkBack nesses controles |
| Marketplace Filter | Campos mínimo/máximo e ações com confirmação humana; diálogo numérico | Retestar dígitos e seções corrigidos; toolbar preservada ainda limita texto em 200% |
| Notifications | Lista, não lidas e ações em lote | Retestar ações compartilhadas corrigidas; conferência humana de leitura/expansão |
| Onboarding | Splash e continuidade da sessão | Introdução de primeiro uso não reapresentada pela sessão atual; tutorial Shake pertence a Betting |
| Payment | Confirmação de R$ 7,00 e escolha de métodos aberta, sem gerar Pix | Conferência humana respondida; ajuste visual de “Listar mais” e consentimento/processamento ainda pendentes |
| Responsible Gaming | Introdução e formulário autenticado; confirmação humana sem enviar | Retestar escala real do texto e controles de corpo corrigidos |
| Results | Entrada das modalidades e leitura pública de resultados | Leitura integral de sorteio e faixa confirmada; demais modalidades/colunas continuam com escopo parcial |
| Retail | Busca de lotéricas, campo preenchido, teclado e retorno | Resgate Pix/Mercado Pago depende de prêmio elegível; regressão sintética não equivale a Samsung |
| Support | Categorias, perguntas, resposta expandida e termos HTML | Retestar categorias corrigidas; conferência humana de links e confirmação externa |
| Tickets | Minhas apostas, período e scanner | Retestar período e entrada manual corrigidos; comprovante real depende de conteúdo disponível |

## Defeitos reproduzidos e correções

**54 novas regressões JVM passaram nesta rodada**, incluindo 17 de leitura agrupada, duas de introdução e seis do tutorial animado. O consolidado tem **3.379 aprovados, zero falhas, erros ou ignorados, em 68 módulos**. Módulos afetados foram repetidos completos; Lint, Detekt, arquitetura/CaixaDS e montagem passaram. Ktlint de 73 arquivos passou; falhas intermediárias de fixtures estão preservadas nos logs. As regressões usam fonte de sistema em 200% e os componentes reais, com dados sintéticos. Falhas RED e resultados GREEN permanecem nos logs, separados dos totais finais.

| Área | Problema reproduzido | Correção |
|---|---|---|
| Login | Corte nas ações e rodapé | Altura mínima e corpo rolável no texto ampliado |
| Results | Nomes de modalidades cortados | Uma coluna e altura natural com fonte ampliada |
| Tickets | Período cortado; ação manual existente sem entrada no scanner | Período adaptável e botão “Digitar código” ligado ao callback existente |
| Design System | Opções/preços e botões habilitados/desabilitados cortados | Lista com viewport limitado, ações alcançáveis e alturas naturais |
| Cart | Total e contador cortados | Resumo natural e largura intrínseca do dígito; scaffold/header intacto |
| Marketplace | Tabs, cidade e controles do carrossel/detalhe sobrepostos | Fluxo vertical, rolagem e controles com texto ampliado |
| Filtros | Dígitos de dois caracteres e títulos incompletos | Células/grade adaptáveis e seções de altura natural |
| Jogo responsável | Escala neutralizada na introdução e fechamento estreito | Texto em sp real e ação de corpo adaptável |
| Mercado Pago | Escala neutralizada e e-mail preenchido sem nome estável | Texto em sp e semântica do campo preenchido |
| FAQ | Categorias longas cortadas | Altura natural no texto ampliado |
| Tutorial de Bolões e +Milionária | Instruções desenhadas nas animações ausentes do TalkBack; preferência sem um alvo único nomeado | Texto equivalente por etapa ativa e checkbox agrupado; ações e animação preservadas, reteste Samsung pendente |
| Home / scanner | Negar câmera impedia chegar à entrada manual | Home navega para Tickets; a feature possui o controle da câmera e a alternativa manual |
| Introdução | Rodapé e mensagem cortados com fonte em 200% | Rodapé medido e corpo rolável, preservando o logo e a apresentação na escala padrão |
| Payment | “Listar mais” quebrado letra por letra | Ações em linhas separadas no texto ampliado; regressão passou, reteste físico pendente |

Os termos HTML foram investigados sem alterar o produto: a medição do DOM no Samsung encontrou largura de 545 para viewport de 544 CSS px, dentro do arredondamento; links mantiveram quebra nativa. O teste instrumentado de caracterização passou sem correção de CSS, com JavaScript de produto desativado e destino do link preservado.

## Integridade e limites

Apostas locais do visitante: **5/R$ 22,00** antes do login. Carrinho autenticado do servidor: **2/R$ 7,00**. São contextos distintos, registrados em `cart-contexts.json`; a diferença não foi tratada como perda de dados. Avisos de apostas locais foram fechados em “Depois”, sem reenviar, remover ou repor apostas. Sem compra, cadastro, resgate, avaliação enviada ou captura de credenciais.

Há títulos truncados em headers com fonte em 200%. Eles permanecem como limitação aberta para respeitar a instrução de não alterar headers. Correções no corpo não são apresentadas como solução desses títulos.

As 37 configurações/baselines e os dois headers protegidos têm referência SHA-256 anterior. Nenhum limiar, baseline, exclusão ou supressão adicional foi autorizado para aprovar os novos testes. Os gates finais passaram; fontes e APK instalado estão identificados em quality-code-final.json, source-fingerprint-final.json e apk-tutorial-final.json.

## Rastreabilidade

Pasta de trabalho: `build/verification/samsung-a11y-14features-20261027/`. Registros: `actions.jsonl`, `manual-confirmations.json`, `cart-contexts.json`, `changed-files.json`, logs RED/GREEN, capturas originais e `*-service.txt`. Capturas com dados pessoais não são publicadas no guia. A matriz JSON mantém critérios pendentes até haver evidência suficiente; pendente não é ignorado.

A auditoria anterior está preservada em `build/verification/a11y-full-audit-20261027/`. Seus 3.325 testes, 19 casos nativos, 26 verificações públicas e 1 confirmação TalkBack são históricos e não são somados às confirmações desta rodada.

## Leitura integral solicitada durante a conferência

O usuário solicitou que cada bloco de modalidade no carrinho e os blocos de resultados fossem lidos integralmente, por extenso em português. Foi acrescentado um formatador puro compartilhado em Core Presentation: cardinais, concordância de quantidades, reais/centavos, ordinais e datas brasileiras, ISO ou com mês abreviado. Sem biblioteca nova, API oculta, controle do motor TTS ou alteração das regras de negócio.

No carrinho, o bloco agrupa modalidade, concurso, números/colunas, trevos, complementos, repetição, seleções da Loteca, cotas/expiração ou Combo e valor, conforme os dados existentes. Identificadores conservam os zeros; números mascarados da Surpresinha continuam sem ser revelados. Excluir, detalhes e legenda mantêm foco/callback próprios.

Em Resultados, o sorteio numérico agrupa modalidade, concurso, números, segundo sorteio, trevos, seleção complementar e data. Cada faixa reúne acertos, ganhadores e valor exibido. Partidas, recompensas complementares, ganhadores por região, próximos concursos e arrecadação têm blocos próprios. A busca, navegação, rolagem e paginação continuam operáveis; o carrossel descreve somente a partida atual.

Regressões novas: PortugueseSpeechTest, CartWholeBlockSpeechTest e ResultWholeBlockSpeechTest. Os testes de paridade continuam medindo os mesmos glifos, cores, dimensões e posições na árvore não fundida. A árvore de acessibilidade agrupada possui asserções próprias de texto integral e ações independentes. Uma não substitui a outra.

## Indisponibilidade observada e recuperação

Depois da conferência de Notificações, a entrada normal passou a exibir “O aplicativo está temporariamente indisponível”; a tentativa de recuperação pela própria tela exibiu erro novamente. A causa ainda não foi estabelecida. O bloqueio foi capturado **antes de instalar o APK de leitura agrupada**, conforme os hashes do instalado e do candidato. Não foi contornado por rota de teste, sessão sintética ou desativação de proteção.

Após a atualização e reinício normais, o login retomou a sessão real e o carrinho foi novamente conferido em 2/R$7. A leitura de mensagens reais de Notificações e os retestes restantes continuam pendentes. Isso não apaga as confirmações anteriores nem transforma indisponibilidade em aprovação ou teste ignorado. TalkBack/fonte 200% e motor Google selecionado temporariamente continuam ativos para a retomada; as configurações iniciais estão registradas.

## Critérios ainda abertos

A matriz possui 56 critérios amplos: 10 aprovados no cenário registrado, 28 parciais, 8 falhos (cinco headers preservados, dois critérios de TalkBack no tutorial e a entrada do scanner), 8 não executados e 2 indisponíveis. As três últimas falhas têm correção verificada em código e instalada; continuam falhas até o reteste no aparelho. Não há critérios marcados como ignorados. Os números são de critérios, não de testes JVM nem de features homologadas integralmente. A indisponibilidade foi recuperada; os cenários ainda sem evidência continuam abertos, sem mudar retroativamente as conferências já obtidas.


## Atualização após o achado do tutorial

APK instalado: `6ab3dbd3c570edd89fd4dcd2a91c945bbcd28dcbeedac41b32975db380415f11`, idêntico ao build. Fontes: `69a565cb944594b0795b991e3702d4dd469b45151baa07f99d58e148a67bf499`, 4357 arquivos. Na entrada normal, o reinício recuperou o login após nova indisponibilidade, ocorrida antes desta instalação. A autenticação está aberta para conclusão direta pelo usuário, sem capturas. Retestes de Bolões, introdução e scanner continuam pendentes; não houve aprovação integral das 14 features. As confirmações auditivas anteriores de Cart/Results foram registradas em sua versão e escopo próprios.
