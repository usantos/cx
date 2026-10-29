# Matriz de migração visual XML → Compose

Atualização visual de 15/10: tutorial de Bolão conferido com captura nativa, insets das ações/seletores de modalidades restaurados e largura dos modais flutuantes centralizada no DS. Evidências e limites: [relatório](VISUAL_ADJUSTMENTS_TUTORIAL_MODALITIES_MODALS_20261015.md).

> Atualização pareada de 15/10/2026: [Bolão e Filtros frente ao CX](MARKETPLACE_FILTER_PAIRED_PARITY_20261015.md). São 20 pares no mesmo AVD/dados, com Outubro Rosa ligada/desligada, e correções centralizadas no DS. `modernCheck releaseCheck` passou na árvore integrada: 1.699 testes JVM, 146 testes Android nos escopos executados e três Releases minificados. O bloqueio de captura das Activities internas foi resolvido pelo runner de instrumentação. A equivalência global de 100% continua pendente nos fluxos e casos históricos indicados abaixo.

> Atualização de 14/10/2026: [consolidação CaixaDS e validação integrada](DS_CONSOLIDATION_20261014.md). Os gates e os três Releases passaram; os testes adicionais cobrem componentes extraídos, com dois testes de origem raster de Dia de Sorte/Lotofácil ainda pendentes (95px esperados, 93px também na base anterior). A alteração de card encerrado/concurso e Dia de Sorte branco foi solicitada pelo produto nesta conversa. As linhas abaixo preservam evidências históricas; não homologam automaticamente a árvore atual.

`CART_CHECKOUT_ACTION_AUDIT.md` corrige o CTA do carrinho, título, fundo branco, texto do mínimo
e estado vazio. Ação em carrinho vazio volta a percorrer gates e validação remota, sem bloqueio
local por mínimo/quantidade. Dois testes de botões e um de integração foram adicionados;
capturas Android e os handlers de erro/redirect da validação ainda não estão homologados.

`CART_SUMMARY_PARITY_AUDIT.md` restaura o somador compartilhado da Home/carrinho em CaixaDS:
fundo diagonal original, valor/descrição/ícone/contador, overflow `...`, total positivo e slot
fixo no carrinho. Contagem vem da projeção plana, não dos cards. Toolbar também considera
essa projeção. Há testes Compose novos; captura pareada e homologação do restante do carrinho
continuam pendentes. Não classificar esta revisão como paridade integral.

`NOTIFICATION_INBOX_PARITY_AUDIT.md` distingue a central local do popup autenticado, remove o PUT
com IDs sintéticos e restaura retenção do item expandido em Não Lidas, ações vazias, switches
independentes e ícones/cores/prévia. Compilação/testes de lógica não homologam as capturas Android.

`HOME_NOTIFICATION_POPUP_AUDIT.md` integra o popup da última notificação em Compose, com o ícone
original vetorizado, base64, link, Fechar e avisos/redirects de leitura. A implementação não substitui
a captura pareada de imagem/texto longo, prioridade, erros e retorno do navegador. Ainda não homologado.

`POST_LOGIN_CONTENT_PARITY_AUDIT.md` restaura o aviso prioritário de pagamento não identificado
na Home, com texto legado, CaixaDS e CTA para Minhas compras. O teste Compose apenas compilou;
captura pareada/execução Android seguem obrigatórias. O popup foi integrado na revisão citada acima.

`POST_LOGIN_SESSION_INTEGRATION_AUDIT.md` conecta o cadastro ao transporte vinculado à sessão,
mantendo a ordem de bloqueio/cadastro e a superfície Compose existente. O novo erro de sessão
substituída, callbacks Android e transferência do carrinho ainda precisam de homologação.

`ACCOUNT_VERIFICATION_TRANSPORT_AUDIT.md` adiciona infraestrutura de transporte e DI, não telas
homologadas. Login/carrinho ainda precisam integrar o envio local, revisar o resultado e comparar
os estados de sucesso, redirect, sessão invalidada, resposta perdida e conflito com o legado.

Esta matriz registra a substituição dos layouts navegáveis do aplicativo anterior. Layouts de `item`, `row`, `card`, `dialog`, `content` e `fragment` são contabilizados no fluxo que os compõe; eles não viram destinos artificiais na navegação.

Ela não constitui, sozinha, certificação de igualdade pixel a pixel. O catálogo de smoke automatizado contém 80 destinos/estados Compose, com resultado final 80/80 sem crash ou novo ANR no cenário mock de Outubro Rosa em 15/10/2026; a ocorrência anterior de ANR e sua repetição controlada estão discriminadas no relatório da rodada. A certificação visual e a homologação de todas as ramificações de negócio exigem comparação de screenshots/estados com o binário legado e permanecem em andamento. Uma linha só pode ser tratada como paridade homologada quando houver evidência reproduzível de estado, ação e imagem de referência.

Legenda:

- **Compose específico**: não usa formulário/tela genérica; possui hierarquia própria na feature.
- **Estado real**: tela alimentada por `ViewModel`/use case e com estados de loading/erro/dados.
- **Paridade comportamental**: use somente quando regras de visibilidade, ações, confirmações, filtros e transições do legado tiverem teste ou evidência comparativa.
- **Componente**: antigo XML parcial absorvido pelo CaixaDS ou pela tela que o utiliza.

## Entrada e conta

A identidade OIDC adicional (`AUTHENTICATED_SESSION_IDENTITY_AUDIT.md`) passa a recusar issuer,
audience, nonce ou validade incompatíveis antes de publicar o login. Usa a superfície de erro já
existente; não foi homologada em capturas/retorno real dos flavors. O lote do carrinho ainda não
consome a nova identidade, portanto não há transferência end-to-end validada.

O protocolo de recibo do carrinho (`LEGACY_CART_TRANSFER_RECEIPT_AUDIT.md`) agora distingue lote
pendente, limpeza confirmada e conflito com alterações locais. Há provider Hilt, mas essas decisões
ainda não chegam a telas/retomadas do pós-login. Nenhum desses estados possui homologação visual
ou prova de transferência end-to-end; testes do protocolo local não homologam login/carrinho.

Carrinho herdado no pós-login: `LEGACY_CART_TRANSFER_AUDIT.md` registra leitor e contrato de upload
tipados, mas o envio ainda não está conectado à verificação de cadastro. Não homologar login/carrinho
como integralmente equivalentes apenas por renderizarem: faltam transferência, confirmação do lote,
resposta perdida e aplicação dos dados retornados, além de captura/interação com apostas locais.

Recuperação local na entrada: `SESSION_CLEANUP_RECOVERY_AUDIT.md` mantém o Splash enquanto verifica/retoma uma limpeza pendente e acrescenta aviso de falha ou recuperação local não equivalente ao SSO. O conteúdo das telas só é composto após liberação. Três testes Compose novos compilaram; transição do Splash, processo real e screenshots ainda não foram homologados.

Limpeza na saída: `LOCAL_SESSION_CLEANUP_AUDIT.md` adiciona o estado de loading durante a limpeza persistida e aviso de falha com Tentar novamente, sem liberar a continuação enquanto incompleta no processo atual. Usa CaixaDS; o teste Compose foi compilado, não executado. O texto de falha é adicional, não literal legado, e ainda exige aceite visual/interativo.

Logout: `SSO_END_SESSION_AUDIT.md` conecta menu, autossuspensão e falha pós-login ao navegador. Espera usa loading institucional; falha local, cancelamento externo e resultado não confirmado têm avisos distintos em CaixaDS. Os textos adicionais não são atribuídos ao legado. Aparência, interação, retorno SSO e limpeza da pilha Android ainda precisam de homologação.

Bloqueio parcial: `POST_LOGIN_PARTIAL_BLOCK_AUDIT.md` restaura o aviso “Usuário habilitado apenas para consultas.” e mantém a superfície técnica atrás dos avisos pós-login. Reconhecer libera a continuação uma vez, sem logout. A compilação instrumentada e os testes de estado não homologam o visual nem a pilha real de navegação.

Pós-login: `POST_LOGIN_BLOCKED_CHECK_AUDIT.md` restaura a consulta de bloqueio anterior ao cadastro e o alerta Atenção/Entendi. Testes JVM e compilação instrumentada não homologam aparência, logout SSO ou todos os ramos de bloqueio/cadastro.

Loading institucional: a revisão `LOADING_PARITY_AUDIT.md` corrige fundo/fade, curva de rotação, fonte e janela modal compartilhada do `dialog_loading.xml`. Os testes de movimento e a compilação não substituem as capturas pareadas e a validação de interação; o componente e seus consumidores ainda não estão homologados como visualmente idênticos.

| Fluxo Compose | Referência legada principal | Situação |
|---|---|---|
| Pré-splash/Splash/FilaBRA | ícone adaptativo, `activity_splash_screen.xml`, `anim_ja_pensou.json` | janela Android configurada + Compose/Lottie específico |
| `account/login` | `activity_login.xml`, `fragment_login.xml`, `alerta_carregando.xml` | Compose específico + AppAuth + verificação real de cadastro |
| `onboarding/introduction` | `activity_introducao.xml`, `fragment_introducao_*.xml`, confirmação MA003 | TQS: título da página 1 em Caixa Std Bold 20sp, títulos das páginas 2–3 em Futura Bold 18sp, corpos em Caixa Std Regular 20sp e CTA final literal “Acessar o aplicativo” conferidos contra o legado; teste instrumentado verde. A variante legada instalada já tinha a introdução dispensada, então falta captura pareada de primeira execução para homologar o visual integral. |
| `onboarding/tutorial` | `activity_introducao_tutorial.xml` | Compose específico; “Começar tutorial” abre o pager +Milionária, “Pular apresentação” segue direto à simulação e somente o primeiro caminho grava a preferência quando marcado, como no legado |
| `onboarding/age` | `activity_restrito_dezoito_anos.xml` | Compose específico; retorno à introdução e acesso ao Jogo Responsável |
| `onboarding/update` | `activity_atualizacao.xml` | Componente compartilhado com o gate, asset original, versão real, abertura da loja e bloqueio de Voltar; recaptura pendente, ver `STARTUP_STATUS_PARITY_AUDIT.md` |
| `onboarding/unavailable` | `activity_app_indisponivel_novo.xml` (CX prd 1.25) | Pareado visualmente no XCover7 em 2026-10-07: barra do sistema, cabeçalho/logo, ilustração, mensagens/divisor, botão, versão e erro. Capturas em `build/parity-production-20261007/unavailable/`; sessão do pacote oficial instalado estava no login, então o par usa a Activity prd do projeto CX. Estados offline/loading/retry e transições reais de rede ainda pendentes; ver `STARTUP_STATUS_PARITY_AUDIT.md`. |
| `account/register` | `activity_cadastrar.xml`, `content_cadastrar.xml`, `footer_cadastrar.xml` | Estado real; claims OIDC, CEP e cadastro via API; estrutura visual e estado inicial confrontados com o legado |
| `account/register-confirmation` | `activity_cadastrar_confirmacao.xml` | Estado próprio; redirecionamento único automático em 3 s |
| `account/personal-data` | `activity_dados_pessoais.xml`, `content_dados_pessoais.xml` | Estado real; consulta, CEP, limite e atualização via API |
| `account/personal-data-confirmation` | `activity_dados_pessoais_confirmacao.xml` | Estado próprio; redirecionamento único automático em 3 s |
| `account/change-login` | `activity_alterar_dados_login_caixa.xml` | Compose + navegador seguro da Conta CAIXA com `referrer` legado |
| `account/token` / retorno OAuth | `activity_token.xml`, `dialog_loading.xml`, `LoadingViewLoterias` | Superfície azul vazia compartilhada com o estado de troca/validação do login real, modal “Carregando…” sem formulário ao fundo. Testes JVM e compilação instrumentada não homologam a transição Android; ver `AUTHENTICATION_RETURN_PARITY_AUDIT.md` |

## Home e apostas

| Fluxo Compose | Referência legada principal | Situação |
|---|---|---|
| Home | `activity_principal.xml`, `content_principal.xml`, `card_modalidade.xml` | Estado real em `:feature:home`; carousel infinito centralizado de 280 dp, escala legada 1,00→0,80, expansão animada de “Último resultado”, painel superior deslocado em 500 ms e sem bullets/indicadores; parâmetros, flags de Combo/bolão, CTA de bolão por modalidade, Instantânea, repassômetro e somador do carrinho preservados |
| `betting/home` / `betting/modalities` | `activity_principal.xml`, `card_modalidade.xml` | Compose específico |
| `betting/simulation` | `activity_simular_aposta.xml`, `CartelaFragment`, `SurpresinhaFragment`, modais de quantidade/Teimosinha e rodapé | Estado real; parâmetros, preços, limites, concurso, dezenas/trevos e inclusão no carrinho via API; alternância manual/Surpresinha e modais restaurados. Os cabeçalhos de Mega da Virada, Quina de São João, Lotofácil da Independência e Dupla de Páscoa usam os assets e títulos de duas linhas do legado; concursos especiais exibem o aviso literal ao pedir Teimosinha; carets dos seletores medem 7×4 dp como o drawable XML original. Teste instrumentado no XCover7 passou 3/3; falta par raster completo para homologação pixel a pixel. |
| Seletor “Números que quero/não quero” | `activity_quero_nao_quero.xml` | Componente Compose de tela cheia integrado ao filtro e ao Rapidão; cancelar/fechar preserva valores, confirmar aplica e há bloqueio mútuo com alerta |
| `betting/rapid` / `betting/rapid-list` | `activity_configuracao_rapidao.xml`, fragments 1–8, `activity_lista_rapidao.xml` | Estado real; sete etapas, obrigatórios/proibidos mutuamente exclusivos, modalidades, valores, prêmio, geração, exclusão e validação via API |
| `betting/favorites` | `activity_favoritas.xml` e itens de favoritas | Estado real; agrupamento/filtro, vazio por modalidade, exclusão, concurso/Teimosinha/Espelho e inclusão no carrinho via API |
| `betting/pools` | `activity_bolao.xml`, cards de bolão | 10 pares nativos/Compose em duas campanhas: carrossel, lista, expansão, favoritas e concurso; estilos/componentes no DS. Ver relatório pareado de 15/10/2026. |
| `marketplace/filter` | `activity_filtra_marketplace.xml`, filtros e modais de números | 10 pares nativos/Compose em duas campanhas: inicial, rodapé, diálogo e seleção; 10 testes Android aprovados, componentes no DS. Ver relatório pareado de 15/10/2026. |
| `betting/pools`, `betting/pool-view` | lista de bolões, `item_list_bolao.xml` | Estado real; cards em acordeão, favorito por lotérica, quantidade limitada ao estoque, total tarifado, detalhe e reserva direta via API |
| `betting/pool-detail` | `activity_detalhes_bolao.xml`, itens de jogos e cotas | Estado real; quantidade limitada ao estoque e reserva confirmada pelo backend |
| `betting/combos` | `activity_combos.xml`, `item_lista_combos.xml` | Compose específico |
| `betting/combo-detail` | layouts de detalhe do combo | Compose específico |
| `betting/tutorial` / `betting/tutorial-plus` | pager de Bolão e +Milionária | Fluxo legado restaurado em Compose com os 5/7 assets Lottie, ordem, velocidades, loop, pular/fechar, CTA por página, checkbox e chaves históricas de preferência |
| `betting/tutorial-super-seven` | `activity_tutorial_super_sete.xml`, fragments 1–3 e GIFs | Fluxo legado restaurado em Compose com três páginas, GIFs, avançar/iniciar, pular e preferência histórica |

## Carrinho e pagamento

`MERCADO_PAGO_REGISTRATION_AUDIT.md` acrescenta o consentimento Sim/Não do código `018003` no pagamento, loading institucional, token por serviço legado e abertura externa sem reenvio automático. `PRIZE_MP_REGISTRATION_AUDIT.md` conecta também o resgate, preservando o e-mail digitado e distinguindo cadastro necessário de prêmio resgatado. Há mocks próprios com navegador simulado localmente. A comparação visual e callbacks Android continuam pendentes.

`PRIZE_HTTP_ERROR_PARITY_AUDIT.md` acrescenta ao POST de resgate confirmação de login, alertas negociais/500 e navegação dos redirects tipados, com data local injetada e pausa sem reenvio. A presença das rotas e os testes de estado não homologam a apresentação ou o retorno no APK. Erros dos demais endpoints de resgate/token continuam pendentes.

`PURCHASE_NOTICE_PARITY_AUDIT.md` acrescenta alertas Compose específicos para 400/404/500, Apostar mais/Entendi e texto diagnóstico com relógio injetado. A implementação e os testes de estado não substituem as capturas dos diálogos, a verificação da pilha no APK ou a homologação de todos os casos de erro.

A revisão `PURCHASE_HTTP_ERROR_PARITY_AUDIT.md` conecta 401 ao login e os redirects de erro aos carrinhos corrente/favoritos, preservando a diferença em relação ao handler 200/303. Foram adicionados mocks e testes de estado/transporte; a geometria, as transições Android e os alertas específicos de erro ainda não estão homologados.

`PURCHASE_REDIRECT_PARITY_AUDIT.md` registra os destinos login/termos/cadastro/indisponibilidade, o alerta literal de novos termos e a pausa do pagamento durante a navegação. As capturas e os callbacks Android ainda precisam ser comparados; não classificar a mera presença dessas rotas como paridade homologada.

Revisão comportamental de cartão: `CARD_POLLING_PARITY_AUDIT.md` registra o acompanhamento 200/303 e retry de consulta sem novo envio. A tela de falha de consulta distingue acompanhamento de recusa, mas sua comparação visual/diálogos continua pendente; os testes JVM não homologam esses layouts.

`CARD_SUBMISSION_REVIEW_AUDIT.md` acrescenta o alerta de resposta inconclusiva com texto conferido no APK legado, Entendi → Minhas compras e remoção da confirmação da pilha. A integração e os estados foram compilados/testados; a captura pareada desse alerta e a navegação Android ainda precisam de homologação.

| Fluxo Compose | Referência legada principal | Situação |
|---|---|---|
| `cart/current` | `activity_carrinho.xml`, linhas de apostas e somador | Estado real; consulta, exclusões e limpeza via API. Recuperação de parâmetros vazios preserva carrinho e retorna à origem; distingue falha inicial de transporte como no legado. Recaptura e erros específicos pendentes; ver `SIMULATION_RECOVERY_PARITY_AUDIT.md` |
| `cart/included` | `activity_confirmado_inclusao_carrinho.xml` | Compose específico |
| `cart/pool-included` | `activity_confirmado_inclusao_bolao_carrinho.xml` | Compose específico; aberto somente após reserva confirmada e contador baseado na expiração retornada pelo backend |
| `cart/favorite-carts` | `activity_carrinhos_favoritos.xml` | Estado real; consulta e exclusão via API |
| `cart/favorite-detail` | `activity_detalhes_carrinho_favorito.xml` | Estado real; apostas, exclusão e transformação via API |
| `payment/method` | `activity_forma_pagamento.xml`, fragments Pix/cartão | Compose específico |
| `payment/cards` | `activity_meus_cartoes.xml` | Compose específico |
| `payment/add-card` | `activity_adiciona_cartao.xml` | Estado real; entrada da máquina única de cartão, validação por bandeira/validade e envio pelo processador selecionado |
| `payment/card-preview` | `activity_apresentar_cartao.xml`, frente/verso do cartão | Estado real; recupera cartão salvo do backend e avança para CVV sem dados demonstrativos |
| `payment/cvv` / `payment/confirm-card` | `activity_digitar_cvv.xml`, `activity_confirmar_cartao.xml` | Estado real; CVV dependente da bandeira, confirmação, submissão e acompanhamento assíncrono na mesma máquina de estados |
| `payment/confirmation` / `payment/success` | `activity_confirmacao_pagamento.xml`, `ic_processamento_compra.xml` | Compose específico; texto, ênfases, ordem dos avisos, gradiente, ícone, botões e destino “Minhas Compras” confrontados com o legado e verificados no emulador |
| `payment/pix-copy` | `activity_copia_cola_pix.xml` | Compose específico |
| `payment/pix-guidance` | `activity_orientacao_pix.xml` | Estado local tipado; exige a ciência do titular antes de gerar a cobrança real |

## Acompanhamento e resultados

| Fluxo Compose | Referência legada principal | Situação |
|---|---|---|
| `tickets/my-bets` | `activity_apostas_confirmadas.xml`, filtros e itens | Estado real; catálogo de filtros/configuração e modalidades vêm do backend; Surpresinha, Teimosinha e Combos preservados |
| `tickets/bet-detail` | detalhes numérico, +Milionária, Super Sete, Timemania, Dia de Sorte, Loteca, Lotogol e bolão | Estado real; alerta Teimosinha, comprovante PDF, validação/salvamento de favorita, inclusão direta no carrinho e reedição restaurados. O domínio e o parser preservam matriz de colunas, trevos, time do coração, mês da sorte, partidas/placares, Teimosinha, Espelho, Troca, Combo e bolão; as regras de modalidade aberta e de conversão Normal/Especial também foram recuperadas. As variantes possuem fixtures determinísticas e captura Compose, mas a homologação visual comparativa de todos os estados especiais continua em andamento |
| `tickets/purchases` | `activity_lista_compras.xml` | Estado real; meses, meios e situações remotos, dependência meio/situação e paginação de 10 itens |
| `tickets/purchase-detail` | `activity_detalhes_compras.xml`, `list_group_compras.xml`, Pix e dialogs | Cabeçalho compacto e ações no toolbar como no legado; detalhe remoto preserva IDs de situação/meio, E2E, bolão e Surpresinha; filtro local por concurso/situação/modalidade, Pix pendente, favoritar e popups negativos implementados |
| `tickets/read` | `activity_ler_bilhetes.xml` | Estado real; CameraX + ML Kit, permissão em runtime e fallback manual |
| `tickets/manual-code` | `activity_insercao_manual_cod_barras.xml` | Compose específico; título, ajuda, campos sublinhados, máscaras, validação e CTA do legado |
| `tickets/read-result` | `activity_resultado_ler_bilhetes.xml` | Compose específico |
| `results/modalities` | `activity_selecao_modalidades_resultados.xml` | Estado real |
| `results/detail` | `activity_visualizar_resultados.xml` e fragments por modalidade | Estado real; consulta último/por concurso na própria toolbar como no legado, navegação anterior/próximo limitada ao concurso atual e erro/retry sem dados demonstrativos |
| `results/confirmed-bet` | `activity_resultado_aposta_confirmada.xml` | Tabelas, faixa de identificação, retorno ao detalhe e rodapé de concurso atual implementados; Aposte agora valida acesso/suspensão, modalidade e janela temporal de revenda antes de navegar. Cabeçalhos especiais, homologação interativa do alerta temporal e recaptura pareada ainda pendentes; ver `RETAIL_PARITY_AUDIT.md` |

## Serviços

| Fluxo Compose | Referência legada principal | Situação |
|---|---|---|
| `support`, `support/terms`, `support/terms-acceptance`, `support/game-help` | telas de ajuda/dúvidas/termos e conteúdo HTML | Estado real e renderização integral em Compose; `game-help` recuperou a toolbar, os metadados fixos e o conteúdo literal da `AjudaActivity` |
| `notifications/messages` | `activity_notificacao_msg.xml`, `content_lista_msgpush.xml`, `item_msg_push_swipebuttons.xml` | Estado local por destino; sem PUT com IDs sintéticos. Expansão em Não Lidas mantém conteúdo até atualizar filtro; leitura explícita/exclusão/lote atualizam lista. Ícones de swipe, prévia e título lido revisados. Capturas, gestos e back stack ainda pendentes; ver `NOTIFICATION_INBOX_PARITY_AUDIT.md` |
| `notifications/preferences` | `activity_preferencias_notificacoes.xml`, `item_preferencia_notificacao.xml` | Seis switches independentes e locais, inicialmente desligados como no holder legado, sem alegar inscrição remota. Mestre que propagava categorias foi removido. Estado por destino; captura pareada continua pendente |
| `responsible/start` | `activity_autoavaliacao_start.xml` | Compose específico |
| `responsible/form` | `activity_autoavaliacao_form.xml` | Estado real; perguntas e respostas via API |
| `responsible/suspension` | `activity_autosuspensao.xml` | Estado real; consulta do CPF, suspensão via API e logout |
| `responsible/social` | `activity_repasses_sociais.xml` | Estado real; valores do repassômetro via API |
| `retail/search` | `activity_buscar_lotericas.xml` e itens | Estado real; busca com mínimo/debounce e favoritos via API |
| `retail/location` | `activity_localizacao.xml` | Compose específico |
| `retail/prize` | opções de resgate | Estado real; opções retornadas por `detalhes-premio` |
| `retail/prize-guidance` | `activity_jogos_confirmados_premio.xml` | Compose específico |
| `retail/prize-lottery` | `activity_resgate_premio_loterica.xml` | Estado real; QR do backend, validade, contador, mudança de cor/animação/vibração, expiração e renovação; geometria recomposta a partir do XML legado |
| `retail/prize-agency` | resgate em lotérica/agência | Estado real; comprovante PDF do backend |
| `retail/prize-pix` | `activity_resgate_pix.xml`, escolha/confirmação de chave e `activity_confirma_resgate_pix.xml` | Estado real; validação imediata, consulta, confirmação, cancelamento no backend apenas pelo botão e resgate; abas, rodapé e estados recompostos a partir dos XMLs legados |
| `retail/prize-confirmation` | confirmação do resgate | Estado real; acesso às apostas e comprovante |
| `retail/prize-mercado-pago` / `retail/prize-mercado-pago-success` | `activity_jogos_confirmados_premio_mercado_pago.xml` e confirmação | Estado real; e-mail do usuário, validação no clique, ajuda, resgate via API, comprovante e ações pós-resgate; geometria recomposta a partir do XML legado |

## Componentes XML absorvidos

- Barras `app_bar_*`: `CaixaDSLegacyTopBar` e `CaixaDSLegacyScaffold`.
- Botões e rodapés: `CaixaDSLegacyPrimaryButton` e `CaixaDSLegacyOutlinedButton`.
- Campos de 52 dp: `CaixaDSLegacyLabelledField`.
- Cards, linhas e divisores: `CaixaDSLegacyCard`, `CaixaDSLegacyChoiceRow` e `CaixaDSLegacyDivider`.
- Grades de dezenas: `BettingLegacyNumberGrid` na feature Betting, usando estilos e tokens do CaixaDS.
- Dialogs: componentes de dialog do CaixaDS e dialogs específicos das features.
- Itens RecyclerView/GridView: `LazyColumn`, `LazyRow`, `LazyVerticalGrid` e `HorizontalPager` nas features.

## Validação

A revisão [Conexão e abertura de login](PRIZE_CONNECTIVITY_PARITY_AUDIT.md) restaura mensagens distintas para offline/timeout/conexão no token e POST de resgate, além da checagem de rede no clique para login. Os novos mocks e testes de estado não substituem captura pareada dos alertas nem a validação das transições reais de rede no Android.

A revisão [Erros da consulta do token](REGISTRATION_TOKEN_ERROR_AUDIT.md) conectou os avisos e redirects às entradas de pagamento/resgate e adicionou mocks de login, termos, repetição de consentimento e erro negocial. Testes JVM e compilação isolada não constituem captura pareada: os novos diálogos, loading, retomada e pilha ainda precisam ser exercitados no APK frente ao legado.

A revisão de 07/09 ligou o CTA individual do carrossel a `PrepareBetEntryUseCase`, por `HomeViewModel`, em vez de navegar diretamente ao simulador. A revalidação considera modalidade, tipo e número de concurso; erros permanecem na Home em alerta dispensável, e cliques repetidos são bloqueados. Isso não homologa os demais CTAs da Home nem a aparência do alerta/loading. A validação de todos os estados, inclusive a troca de concurso entre consultas, continua pendente.

O CTA de bolão do carrossel também recuperou a verificação de suspensão e a decisão de tutorial pela preferência legada. A conclusão abre o Marketplace filtrado por modalidade/tipo e remove o tutorial da pilha; fechar não abre o Marketplace. Esses pontos estão documentados em `POOL_ENTRY_PARITY_AUDIT.md`, ainda sem homologação visual/interativa integral.

Combo, Instantânea e o atalho de Rapidão passaram a usar decisões verificadas de entrada; Rapidão respeita a flag remota e a preferência histórica de configuração/lista. O drawer recuperou os grupos/filhos originais, expansão independente, `consultaMinhasApostas`, ícones legados, saudação, versão real e confirmações de saída/navegador. Testes de contrato e compilação isolada estão em `HOME_NAVIGATION_PARITY_AUDIT.md`; ainda faltam capturas pareadas, interação no APK e o indicador de ambiente. O menu não está homologado como visualmente equivalente.

### Comparação executável com o APK legado

- `scripts/capture-legacy-compose-parity.sh` abre a `Activity` do APK legado de automação por um launcher exclusivo do artefato de referência, abre a rota Compose correspondente no mesmo emulador, remove a barra de status da métrica e grava imagens e SSIM em `build/visual-audit/legacy-compose*/report.tsv`.
- O checkout legado e o launcher de captura ficam isolados fora da árvore do aplicativo novo; nenhum código legado, `Activity`, XML ou credencial histórica é empacotado no APK Compose.
- O executor legado isolado agora injeta a sessão e as permissões mínimas necessárias para manter `account/login` na tela antes da ida ao navegador. A captura `build/visual-audit/legacy-compose-login-v3/report.tsv` registra SSIM `0.950456`; a abertura do navegador continua sendo validada separadamente como comportamento.
- A primeira execução detectou divergências reais em `responsible/start` e `responsible/suspension`. Foram restaurados o asset original, dimensões, barra de status branca, segundo botão opcional, cantos, título, ícone, textos e estados desabilitados. O acesso obrigatório após login agora usa `responsible/start/required` e oculta “Fechar”; o acesso voluntário pelo menu mantém o botão, como no legado.
- A matriz isolada de Jogo Responsável registra `0.922479` para apresentação, `0.888367` para o formulário e `0.887236` para Repasses Sociais. O formulário recuperou o ícone e os controles compactos; Repasses recuperou o cálculo percentual, textos, cores, recortes, categorias, botão final e ajuda para Termos de Uso. O estado de suspensão já havia atingido `0.865393` antes desses últimos ajustes de controles.
- O scanner de bilhetes recuperou `mascara_loteria`, `logo_cinza`, `close_circle_outline`, `ic_qrcode_branco`, geometria, texto e navegação do legado. A comparação subiu de `0.585404` para `0.903802`; frames da câmera são dinâmicos e, por isso, a métrica dessa tela não é determinística entre capturas.
- A digitação manual de bilhetes voltou a exibir “Conferir bilhetes”, o atalho de ajuda, NSB, Lotérico, máscaras, validação e botão retangular nas mesmas posições do legado. A captura `build/visual-audit/legacy-compose-manual-ticket-v2/report.tsv` registra SSIM `0.968241` (antes do ajuste, `0.927815`). O fluxo também recuperou a montagem do payload QR e a criptografia RSA que precede a chamada ao backend.
- A captura de referência agora limpa tanto o pacote legado quanto o Compose antes de cada rota e descarta telas cobertas por alertas globais de indisponibilidade/timeout. Isso evita tratar estado persistido, teclado residual ou erro de backend como divergência visual da feature.
- O executor legado isolado passou a fornecer sessão, extras e DTOs para as 17 rotas que antes redirecionavam ou exibiam timeout. A validação posterior do foco demonstrou que algumas Activities ainda encerravam depois da abertura; por isso, a existência de screenshots dessas rodadas não comprova um par válido. Os relatórios produzidos com a revalidação pós-estabilização distinguem os estados `requires-fixture` e `redirected-after-settle` antes de calcular a métrica.
- A primeira rodada de correções das rotas com fixture elevou `results/detail` de `0.639395` para `0.927662`, `tickets/bet-detail` de aproximadamente `0.50` para `0.828591`/`0.875927` (as duas Activities legadas que convergem para o mesmo destino) e `cart/pool-included` de `0.618296` para `0.905378`. No carrinho de bolão, concurso, data, modalidade, valor e expiração agora percorrem o fluxo real de confirmação; o script também passou a escapar query strings para não perder parâmetros após `&` no `adb shell`.
- `payment/pix-copy` foi recapturada no estado ativo após corrigir a precedência do mock Retrofit, valor, validade e geometria do CTA. A evidência válida mais recente está em `build/visual-audit/legacy-compose-pix-v8/report.tsv` (`0.858421`). A tela ainda não é classificada como pixel a pixel homologada.
- `support/game-help` foi reconstruída a partir da `AjudaActivity` e de seus XMLs de referência, incluindo o cabeçalho de duas colunas, os valores fixos e o texto `Lorem ipsum` efetivamente exibidos pelo legado. A ajuda agora recebe a modalidade da aposta, aplica as cores do jogo e mantém Mega-Sena como fallback seguro da rota direta; Mega-Sena e Timemania foram exercitadas no APK `automacaoDebug`. A recaptura geométrica mais recente está em `build/visual-audit/legacy-compose-support-v4/report.tsv` (`0.598037`); conteúdo, hierarquia e alinhamento das colunas coincidem, mas as métricas de renderização do texto ainda impedem a homologação pixel a pixel.
- `retail/prize-agency` recuperou o fundo branco, a faixa sobreposta pela toolbar, enumeradores circulares, margens verticais, botão retangular branco com borda verde-azulada e o ícone de ajuda do legado. A ajuda navega para Termos de Uso e foi exercitada no APK `automacaoDebug`; a recaptura pareada está em `build/visual-audit/legacy-compose-prize-agency-v6/report.tsv`, com SSIM `0.937225` (antes da correção visual, `0.871876`).
- `results/confirmed-bet` deixou de abrir a tela genérica de comparação de dezenas e voltou ao comportamento da `ResultadoApostaConfirmadaActivity`: cabeçalho temático, concurso, valor líquido, faixas de premiação e canais de resgate são montados a partir de `apostas/{id}/detalhes-premio`; cada canal conduz ao estágio correspondente. A recaptura `build/visual-audit/legacy-compose-confirmed-bet-v6/report.tsv` registra SSIM `0.893049` (antes da correção semântica, `0.791244`). A fixture legada desse par não contém canais, enquanto o mock Compose exerce os quatro canais, portanto a região inferior não é usada como certificação pixel a pixel.
- `retail/prize-mercado-pago` foi recomposta a partir de `content_jogos_confirmados_premio_mercado_pago.xml` e de `JogosConfirmadosPremioMercadoPagoActivity`: marca, fundo branco, faixa do prêmio, campo sublinhado, botão verde-azulado sobre o rodapé cinza e ajuda seguem o legado. O botão permanece acionável com e-mail vazio e exibe `E-mail inválido.` no clique, em vez de antecipar a regra desabilitando o controle. No sucesso, a seta de retorno some, a ajuda permanece, o comprovante continua disponível e os dois botões ficam fixos sobre o rodapé de 176 dp. A evidência anterior à correção está em `build/visual-audit/legacy-compose-prize-mp-v4/report.tsv` (`0.865449`); uma nova captura pareada ainda é obrigatória antes da homologação.
- `retail/prize-lottery` recuperou o título `Lotérica`, QR de 200 dp, textos e margens do XML, timer de 25 sp, divisor, enumeradores circulares, destaque verde de `prêmio` e o botão retangular do estado expirado. O primeiro minuto fica vermelho e os últimos dez/três segundos mantêm pulso/vibração. A Activity legada encerra sem renderizar quando a fixture não consegue fornecer todos os extras e a nova proteção do executor registra corretamente `requires-fixture` em `build/visual-audit/legacy-compose-prize-lottery-v5/report.tsv`; portanto não existe ainda um par visual válido para homologação.
- `retail/prize-pix` recuperou a marca Pix completa no resumo, cores e seta das abas, campo sublinhado com erro imediato, margens dos fragments e botões de 55 dp com cantos de 4 dp. A navegação também foi separada conforme o código legado: seta/hardware back saem do canal sem cancelar a reserva, enquanto “Cancelar” na confirmação chama o endpoint de cancelamento e só retorna às opções após sucesso. Esses ramos estão cobertos no `PrizeRedemptionViewModelTest`, mas a recaptura interativa ainda é necessária.
- A revisão seguinte conectou explicitamente o hardware back à ação da seta, restaurou ajuda na lotérica e nos estados de Pix, aplicou a fonte CaixaDS aos campos básicos e separou os validadores de e-mail de Mercado Pago e Pix conforme as duas implementações legadas. Valor líquido zero não é mais substituído por bruto. A [auditoria de resgate](RETAIL_PARITY_AUDIT.md) registra 29 testes JVM aprovados e cobertura de 96,45% dos ViewModels/regras de `retail`, incluindo corrotinas; os APKs e screenshots dessa revisão ainda precisam de validação pelo Gradle/emulador.
- A auditoria comportamental de `tickets/bet-detail` encontrou três ações que ainda estavam aproximadas: comprovante, favorita e repetição da aposta. Elas agora usam, respectivamente, o PDF do backend, `logado/apostas-favoritas/validar` + `logado/apostas-favoritas` e inclusão real no carrinho; “Editar aposta” restaura modalidade, tipo de concurso e dezenas no simulador. Esses estados possuem testes unitários, mas ainda precisam de captura interativa legada/Compose antes de serem marcados como homologados.
- A segunda auditoria de `tickets/bet-detail` eliminou o achatamento dos payloads especiais: +Milionária mantém trevos; Super Sete mantém colunas; Timemania e Dia de Sorte mantêm seus complementos; Loteca e Lotogol mantêm partidas, marcações e placares. A repetição, a edição e o salvamento de favorita transportam os mesmos dados. As capturas determinísticas estão em `build/visual-audit/confirmed-bet-special/` para as variantes `plus`, `super-seven`, `timemania`, `dia-de-sorte`, `loteca`, `lotogol` e `pool`. Elas comprovam renderização e composição por modalidade, mas não substituem a comparação pareada com os respectivos estados do APK legado.
- `notifications/preferences` foi recapturada sem popup de rede. O Compose recuperou a fonte Futura original, título misto, ação de ajuda para Termos de Uso, cores, divisores e switch compacto do legado; a evidência está em `build/visual-audit/legacy-compose-notification-preferences-v6/report.tsv`, com SSIM `0.816112`, metadados do dispositivo e árvores semânticas dos dois aplicativos. A ação de ajuda também foi exercitada no APK `automacaoDebug`. Como ainda há diferença geométrica/antialiasing mensurável, a tela não é classificada como pixel a pixel homologada.
- A forma de pagamento no estado determinístico sem cartões foi comparada depois da confirmação pré-compra. O Compose passou a reproduzir o inset visual do `CardView`, as alturas de 52 dp do CTA Pix, 92 dp do cabeçalho do processador e 190 dp do estado vazio, além dos tamanhos intrínsecos distintos de Mercado Pago e RecargaPay. A evidência reproduzível está em `build/visual-audit/legacy-compose-payment-content-v11/report.tsv`, com SSIM `0.921023` (a captura anterior ao ajuste registrava `0.808777`).
- O cadastro foi recapturado no estado inicial alimentado pelas claims OIDC. Foram restaurados a barra em gradiente, o título misto, campos sublinhados, seletor de sexo animado, consulta/ajuda de CEP, consentimentos e botão no fluxo rolável. A evidência está em `build/visual-audit/legacy-compose-register-v4/report.tsv`, com SSIM `0.889181` (antes da reconstrução, `0.834539`).
- `account/personal-data` foi reconstruída a partir de `activity_dados_pessoais.xml` e `content_dados_pessoais.xml`: cabeçalho estendido em gradiente, campos sublinhados, seletor de sexo, ajuda de CEP, notificações, limite diário, diálogos e rodapé fixo seguem agora os estados do legado. A comparação sem sobreposição de erro está em `build/visual-audit/legacy-compose-personal-data-v2/report.tsv`, com SSIM `0.893980`; `legacy-compose-personal-data-v3` registra separadamente o alerta correto de dados indisponíveis no cenário vazio e, por isso, não é usado como métrica geométrica.
- As confirmações de cadastro e atualização voltaram a usar o fundo claro, `ilustra_cadastro`, divisor, tipografia mista e textos literais do legado, com redirecionamento único após três segundos. `account/personal-data-confirmation` registra SSIM `0.956668` em `build/visual-audit/legacy-compose-account-confirmations-v4/report.tsv`; a confirmação inicial de cadastro compartilha o mesmo componente e varia somente título e mensagem.
- A Home foi recapturada depois de alinhar o `ScaleTransformer` ao valor `setMinScale(0.8f)` do código legado. As evidências em `build/visual-audit/home-carousel-legacy-scale/` cobrem o card selecionado com vizinhos menores, o painel de acesso rápido aberto, frames intermediários das animações de 500 ms e “Último resultado” expandido; nenhum estado contém bullets.
- A auditoria binária entre o checkout legado e a árvore modular confirmou por SHA-256 que todos os PNG, WebP, JPG, GIF, JSON de animação e arquivos de fonte usados em runtime permanecem disponíveis. As únicas ausências por nome são configurações de build do Google e artes destinadas à publicação na Play Store; elas não compõem telas. Essa verificação comprova preservação dos arquivos, não o uso correto em cada estado visual.
- As demais telas e, principalmente, os estados que exigem sessão, DTOs ou extras do fluxo anterior continuam sendo homologados pela matriz executável. Portanto, até que todas as linhas tenham evidência, o projeto não deve ser descrito como certificado pixel a pixel em todas as telas.

- A build falha caso um XML de interface seja introduzido em `res`.
- A build falha caso seja introduzida outra `Activity` ou qualquer `Fragment`.
- Builds `prd`, `tqs` e `automacao` são verificadas por `modernCheck`.
- Em debug, `MainActivity` aceita o extra `visual_test_route`, permitindo abrir cada rota isoladamente para captura de screenshot. O extra é ignorado em release.
- `smoke-compose-routes.sh` percorre obrigatoriamente os 78 destinos do catálogo, valida conteúdo Compose em runtime e trata somente a abertura externa da Conta CAIXA como transição de navegador; `capture-compose-screens.sh` usa o mesmo catálogo e o backend mock determinístico.
- Um resultado integral do smoke significa “renderizou sem crash”; não significa “pixel a pixel homologado”.
- `verifyLegacyUiInventory` impede a remoção silenciosa da baseline de 396 layouts e de qualquer um dos 95 pontos de montagem de UI legados.
- A central de notificações foi verificada no emulador nos estados recolhido e expandido; os antigos ícones XML foram convertidos em vetores nativos do `CaixaDSIcons`.
- As 78 entradas atuais foram novamente percorridas no emulador com `automacaoDebug` e `HAPPY_PATH` (`78/78`, zero falhas). As rotas que dependem de aposta ou compra selecionada usam fixtures somente no APK debug mock; sem esse contexto, retornam ao destino anterior como no fluxo real. Formulário, prévia, CVV e confirmação de cartão foram capturados separadamente para verificar a etapa restaurada e o uso do cartão salvo retornado pelo mock Retrofit.
- Após os ajustes desta auditoria, `modernCheck` passou com testes, cobertura, lint e todos os gates arquiteturais; `releaseCheck` também passou para `automacaoRelease`, `prdRelease` e `tqsRelease`, todos com R8 e recursos reduzidos.


### Drawer institucional — alteração solicitada em 15/10/2026

O menu passa a seguir a referência fornecida pelo usuário: degradê no azul CAIXA, logo do login centralizada, botão de fechar, ícones e textos claros, sem divisores entre os itens principais e com rodapé escuro. A implementação visual reside no DS. Essa alteração é uma exceção aprovada à reprodução visual do Drawer legado; os contratos de navegação, flags, expansão dos grupos, informações institucionais e confirmação de saída permanecem.

Validação desta alteração: gates `verifyCaixaDS`/`verifyComposeMigration`, APK `prdDebug` normal (mock desativado), 111 testes JVM do app, 32 da Home e 2 testes Android do Drawer aprovados. Capturas do menu aberto e de Minha área expandida foram inspecionadas em `build/verification/drawer-20261015/`; testes e capturas usam emulador dedicado.


### Campanhas e status bar — continuação de 15/10/2026

A status bar agora segue azul CAIXA com ícones claros em todas as rotas do host, incluindo login e Drawer. Aposta Shake compartilha a tipografia dos outros itens. Seu tooltip fica abaixo do ícone de informação, com seta ancorada no centro, cantos de 20 dp e mensagem em duas linhas.

As campanhas passam a obedecer à configuração do backend, substituindo a antiga ativação local de Outubro Rosa pelo mês do aparelho. As flags `vigenciaMesOutubroRosa` e `vigenciaOutubroRosaMegaSena` foram recuperadas do legado; a paleta do Drawer pode, portanto, mudar de azul para rosa quando a campanha estiver ativa. Setembro Azul e futuras ativações por área usam a extensão opcional descrita em `COMMEMORATIVE_CAMPAIGNS.md`; essa extensão ainda depende de publicação pelo backend. As áreas existentes são Home, Drawer e cartões de Mega-Sena na Home V2.

As capturas inspecionadas estão em `build/verification/drawer-refinement-20261015/`: status bar na Home/login/suporte, tooltip no Drawer padrão, Home/Drawer com Outubro Rosa pelas flags legadas e Setembro Azul na Home antiga pelo contrato opcional. Os cenários de campanha usados nas capturas são mocks determinísticos; não constituem evidência de alteração no servidor de produção. A revisão independente também verificou a atualização ao retomar a Home antiga, a remoção do painel de Outubro Rosa após desativação e o banner de Setembro Azul nesse layout.

Verificação final desta continuação: 646 testes JVM e 7 Android aprovados, gates DS/Compose e APK `prdDebug` normal aprovados. `BuildConfig` final: `HOME_V2=true`, `ENABLE_MOCK_BACKEND=false`.
