# Cadastro Mercado Pago no pagamento — 07/09/2026

## Referência e limite da revisão

Fontes legadas: `RedirectNetwork.checkRedirectCompraAsync`, `treatNegotialErros`, `enviaRegistrarMp`, `DialogUtils.dialogSim`, `JogosConfirmadosPremioMercadoPagoActivity.redirectMp`, `ApostaSilceBO.getMercadoPagoToken` e `ServerMethods.TOKEN_CADASTRO_MP_PATH`. O caminho original é `/usuarios/gerar-token-cadastro-mercado-pago`; a URL externa foi conferida no recurso compilado do APK de referência.

O handler compartilhado legado faz um cast da Activity para `JogosConfirmadosPremioMercadoPagoActivity`, inclusive quando chamado pelo pagamento. Esse cast não é reproduzido. O app Compose usa um coordenador de cadastro com dependências injetadas; no pagamento, o e-mail é consultado no perfil autenticado após consentimento. A API do coordenador também aceita e-mail explícito. A integração posterior desse contrato ao resgate está documentada em `PRIZE_MP_REGISTRATION_AUDIT.md`; as medições abaixo correspondem à revisão original do pagamento.

Esta revisão implementa o ramo `018003` do pagamento; não certifica a equivalência integral do aplicativo ou de todos os handlers de erro.

## Comportamento implementado

- Erros de compra 400/404 com código `018003` apresentam a mensagem do backend, título Atenção e ações Sim/Não, sem o rodapé diagnóstico de código/data. Mensagem ausente/vazia usa o fallback de operação já adotado pelo app.
- Os redirects conhecidos continuam tendo prioridade. HTTP 401 sem redirect permanece no login; HTTP 500 preserva seu handler específico. Os demais status seguem o ramo legado de `018003`; em 408, a mensagem é `Tempo de resposta esgotou`.
- HTTP 303 com código `018003` e sem redirect também solicita cadastro. HTTP 200 com erro continua inconclusivo, não inicia cadastro automaticamente; o marcador de processamento/redirect não é interpretado como URL de cadastro.
- A ação `Não` dispensa o alerta sem consulta de perfil/token nem abertura externa. `Sim` consome a decisão uma vez e pausa as ações de pagamento enquanto o cadastro é preparado/aberto.
- A consulta de perfil precede a consulta do token. Se uma delas falhar, aparece erro dispensável, sem abertura do navegador e sem expor a mensagem técnica da exceção.
- O token é obtido pelo contrato `MercadoPagoRegistrationRepository`, implementado pelo `RemoteRetailRepository`, via `RetrofitRetailRemoteDataSource` e `LegacyBackendApi`. O composition root Hilt fornece `PrepareMercadoPagoRegistrationUseCase`.
- O loading usa `CaixaDSAuthenticationLoading`; o host abre `ACTION_VIEW` somente após consumir a solicitação transitória. Ausência de navegador e restrição de abertura recebem alerta próprio.
- O retorno do navegador apenas libera a pausa. Não comprova cadastro concluído, não aprova compra, não gera Pix e não reenvia cartão. Uma compra já identificada conserva a consulta por ID, sem outro POST automático.

## Tratamento do token

O destino é fixo: `https://registration-br.mercadopago.com/registration-mp`, com `mode=mp` e `external_registration_token`. O token é codificado como valor de query, preservando caracteres opacos; não se reproduz a remoção de caracteres especiais feita no legado, que poderia alterar o token. Não são aceitas URLs de destino recebidas do backend.

O domínio rejeita token vazio, caracteres de controle e tamanho acima de 4096; o data source rejeita envelope sem payload ou com código/redirect. A representação textual do envelope e da solicitação oculta seus valores. O token não é gravado em `SavedStateHandle` ou preferências pelo coordenador e sai do estado ao ser consumido.

Os testes com logs habilitados verificam que e-mail, bearer e token não aparecem no cURL/log HTTP ou na representação do resultado. O token **continua sendo enviado na URL do navegador**, conforme o contrato legado. Não se afirma que navegador/sistema operacional não guardam histórico, nem que expiração, uso único ou política de referrer estejam garantidos: dependem de validação do contrato externo. Isso não é uma certificação OWASP completa.

## Mocks e testes

`PURCHASE_MP_REGISTRATION_REQUIRED` e `PURCHASE_MP_REGISTRATION_TOKEN_FAILURE` abrem a confirmação de cartão. Ambos retornam erro `018003` apenas nas operações de compra; perfil, carrinho e preparação permanecem disponíveis. O segundo falha especificamente na consulta do token. A mensagem negocial é explicitamente simulada.

Com `ENABLE_MOCK_BACKEND`, o navegador real não é aberto: um diálogo local representa abertura/retorno. O cenário não cria cadastro, compra ou conexão externa.

Testes de domínio/estado cobrem ausência de trabalho antes de Sim, Não, duplicidade de consentimento, falhas, cancelamento do escopo, codificação do token, consumo único por identidade, navegador indisponível e retorno sem reenvio. Testes de data exercitam a cadeia Retrofit real com interceptor em memória e rejeição de envelopes inválidos. Testes de rede verificam fixtures e desserialização; o host verifica o destino inicial dos mocks.

## Evidência executada

Artefatos em `build/verification/mercado-pago-registration-20260907/`:

- **394 testes JVM aprovados**: 318 de domínio/estados/rotas/regras, 24 de rede, 45 de data, 4 de preferências e 3 de navegação mock do host.
- Compilação isolada dos fontes alterados de UI, host e composition root concluída. Permanece o aviso preexistente de depreciação de `LocalClipboardManager`.
- Lógica selecionada de pagamento, incluindo corrotinas: **456/461 linhas (98,92%)**. Novo coordenador: **41/41**; regras de aviso: **15/15**.
- Modelos/protocolo selecionados de domínio de pagamento: **70/70**, incluindo **13/13** do cadastro. Transporte selecionado de compra e DTO de cadastro: **57/57**. `RemotePaymentRepository`/data source de pagamento: **236/239 (98,74%)**.
- Relatórios: `feature-payment-coverage.xml`, `core-domain-payment-coverage.xml`, `purchase-transport-coverage.xml` e `payment-repository-coverage.xml`. O último não mede o `RemoteRetailRepository`; a cadeia do token nesse repository foi exercitada por testes de integração em memória, sem atribuir a ela esse percentual de pagamento.

Esses resultados são **escopos selecionados**, não cobertura global nem certificação de renderização. A tentativa `./gradlew :core:domain:test :core:data:test :feature:payment:testDebugUnitTest --offline` falhou antes das tarefas por `Operation not permitted` ao abrir `~/.gradle/wrapper/dists/gradle-9.4.1-bin/arn2x92ynaizyzdaamcbpbhtj/gradle-9.4.1-bin.zip.lck`. Nenhuma restrição foi contornada. O compilador isolado usa dependências em cache e não substitui a geração Hilt/KSP, empacotamento, R8, flavors ou callbacks Android. Nenhum APK/captura atual foi produzido nesta revisão.

## Pendências para homologação

- O código `018003` do resgate recebeu integração posterior em `PRIZE_MP_REGISTRATION_AUDIT.md`. O tratamento HTTP do token também foi migrado posteriormente em [Erros da consulta do token](REGISTRATION_TOKEN_ERROR_AUDIT.md), com avisos, redirects e novo consentimento. Falhas sem resposta HTTP, demais endpoints e homologação Android continuam pendentes.
- Comparar diálogos/loading e testar consentimento, navegador, cancelamento, rotação, retomada, acessibilidade e pilha no APK, incluindo todos os estados de cartão e Pix.
- Implementar/homologar reconciliação após morte do processo; o bloqueio atual protege o fluxo com ViewModels vivos, não fornece idempotência persistente.
- Confirmar o contrato externo de validade/uso único do token e comportamento do navegador, sem inventar garantias de segurança.
- Executar Gradle/Hilt/KSP, APKs, R8, todos os flavors/Release, matriz completa de telas e medição de cobertura global superior a 95%.
