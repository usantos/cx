# Backend mock

O aplicativo possui um backend determinístico dentro do `:core:network`. Quando habilitado, o interceptor responde em memória antes de qualquer conexão HTTP. As operações de referência estão preservadas em `LEGACY_BACKEND_BASELINE.tsv`, incluindo Mercado Pago e RecargaPay. Ter uma resposta JSON para cada rota não comprova que todos os contratos ou fluxos estejam simulados: ainda existem respostas genéricas e cenários que exigem fixtures específicas.

O modo mock existe somente em `debug`. Em qualquer variante `release`, `ENABLE_MOCK_BACKEND` é compilado como `false`, mesmo que uma propriedade `-PmockBackend=true` seja informada. O gate `verifyNetworkSecurity` protege essa regra.

## Como executar

O logout em `ENABLE_MOCK_BACKEND` encerra a sessão local e exibe aviso explícito de simulação, sem abrir o provedor SSO nem afirmar que a sessão real do navegador foi encerrada. Cancelamento/falha externa têm testes de estado separados; não são uma homologação do SSO. Ver `SSO_END_SESSION_AUDIT.md`.

Use preferencialmente o flavor TQS:

```bash
./gradlew :app:installTqsDebug \
  -PmockBackend=true \
  -PmockScenario=HAPPY_PATH
```

Para gerar apenas o APK:

```bash
./gradlew :app:assembleTqsDebug \
  -PmockBackend=true \
  -PmockScenario=EMPTY
```

O APK será criado em `app/build/outputs/apk/tqs/debug/`. A troca do cenário exige executar novamente o comando, pois a seleção fica gravada no `BuildConfig` da variante e não pode ser alterada pelo usuário final.

No mock, a FilaBRA, o Remote Config de pins e a autenticação externa no navegador são ignorados. O splash e a validação inicial continuam sendo executados com dados mockados; depois disso, o app abre a tela mais útil para observar o cenário escolhido.

## Cenários disponíveis

| Cenário | Situação reproduzida | Entrada inicial |
|---|---|---|
| `HAPPY_PATH` | Respostas padrão do catálogo mock | Início |
| `EMPTY` | Listas, carrinho e pesquisas sem resultados | Início |
| `SLOW_SUCCESS` | Resposta bem-sucedida após 2,5 segundos | Início |
| `NETWORK_ERROR` | Falha de conexão sem resposta HTTP | Indisponibilidade no startup |
| `TIMEOUT` | Tempo de conexão esgotado | Indisponibilidade no startup |
| `UNAUTHENTICATED` | HTTP 401 nos serviços funcionais | Início |
| `FORBIDDEN` | HTTP 403 nos serviços funcionais | Início |
| `VALIDATION_ERROR` | HTTP 422 com mensagem de validação | Início |
| `SERVER_ERROR` | HTTP 500 nos serviços funcionais | Início |
| `APP_UPDATE_REQUIRED` | Versão do aplicativo recusada | Atualização necessária |
| `APP_UNAVAILABLE` | Startup responde HTTP 503 | Aplicativo indisponível |
| `REGISTRATION_REQUIRED` | Cadastro do apostador pendente | Cadastro |
| `TERMS_REQUIRED` | Novo termo de uso pendente | Aceite de termos |
| `SELF_ASSESSMENT_REQUIRED` | Autoavaliação obrigatória | Autoavaliação |
| `ACCOUNT_SUSPENDED` | Conta com suspensão temporária | Alerta de conta suspensa |
| `ACCOUNT_PARTIALLY_BLOCKED` | Cadastro retorna `situacaoApostador.enum=BLOQUEADO_PARCIAL` | Fixture de integração pós-login; aviso também coberto por teste instrumentado, sem atalho de startup para SSO completo |
| `POST_LOGIN_BLOCKED_CHECK_HTTP_ERROR` | HTTP 403 somente na consulta de apostador bloqueado | Fixture de integração pós-login; sem atalho que simule autorização externa |
| `POST_LOGIN_BLOCKED_CHECK_REDIRECT` | HTTP 200 com redirect de login na consulta de bloqueio, sem falso sucesso | Fixture de integração pós-login; cadastro e demais endpoints preservados |
| `CART_EXPIRED_POOL` | Cota de bolão expirada removida do carrinho | Carrinho |
| `PURCHASE_PROCESSING` | Compra assíncrona em processamento | Confirmação de pagamento |
| `PURCHASE_APPROVED` | Compra aprovada | Sucesso de pagamento |
| `PURCHASE_REJECTED` | Compra rejeitada | Pagamento não aprovado |
| `PURCHASE_RESPONSE_LOST` | Timeout somente na resposta do POST de registro; preparação e consultas permanecem disponíveis | Confirmação de cartão; ao apostar, alerta de revisão e Entendi → Minhas compras, sem reenvio pela mesma tela |
| `PURCHASE_LOGIN_REQUIRED` | HTTP 303 com redirect de login nas operações de compra | Confirmação de cartão; envio encaminha ao login |
| `PURCHASE_TERMS_REQUIRED` | HTTP 303 com redirect de aceite de termos nas operações de compra | Confirmação de cartão; alerta Entendi → termos |
| `PURCHASE_REGISTRATION_REQUIRED` | HTTP 303 com redirect de cadastro nas operações de compra | Confirmação de cartão; envio encaminha ao cadastro |
| `PURCHASE_UNAVAILABLE` | HTTP 303 com redirect de indisponibilidade nas operações de compra | Confirmação de cartão; envio encaminha à indisponibilidade |
| `PURCHASE_HTTP_UNAUTHORIZED` | HTTP 401 sem redirect apenas nas operações de compra | Confirmação de cartão; envio solicita login |
| `PURCHASE_CART_REDIRECT` | HTTP 400 com redirect `carrinho` apenas nas operações de compra | Confirmação de cartão; envio abre carrinho |
| `PURCHASE_FAVORITE_CARTS_REDIRECT` | HTTP 404 com redirect `carrinhos-favoritos` apenas nas operações de compra | Confirmação de cartão; envio abre carrinhos favoritos |
| `PURCHASE_MORE_BETS_REQUIRED` | HTTP 400, código 011010 e mensagem simulada | Confirmação; alerta Apostar mais/Entendi |
| `PURCHASE_BUSINESS_RULE` | HTTP 404 e mensagem negocial simulada | Confirmação; alerta com código e data/hora |
| `PURCHASE_SERVER_MESSAGE` | HTTP 500, tipo 1 e mensagem simulada | Confirmação; alerta com mensagem, código e data/hora |
| `PURCHASE_SERVER_FAILURE` | HTTP 500, tipo 2 | Confirmação; alerta genérico, sem exibir a mensagem técnica do mock |
| `PURCHASE_INVALID_IDENTIFICATION` | HTTP 500, código 2067 | Confirmação; alerta específico de CPF do Mercado Pago |
| `PURCHASE_MP_REGISTRATION_REQUIRED` | HTTP 400, código 018003; token de cadastro mock disponível após Sim | Confirmação; Sim/Não, preparação e abertura/retorno simulados localmente, sem navegador real |
| `PURCHASE_MP_REGISTRATION_TOKEN_FAILURE` | Mesmo consentimento; HTTP 500 apenas ao consultar token de cadastro | Confirmação; Sim → erro dispensável, sem abertura externa nem reenvio automático |
| `PAYMENT_NO_SAVED_CARDS` | Processadores disponíveis sem cartões cadastrados | Forma de pagamento |
| `PIX_PENDING` | Cobrança Pix válida aguardando pagamento | Pix Copia e Cola |
| `PIX_EXPIRED` | Cobrança Pix expirada | Pix expirado |
| `DAM_BLOCKED` | Janela de represa ativa retornada por `parametros/valida-represa` | Alerta após confirmar a pré-compra |
| `TEIMOSINHA_RESCUED` | Aposta original de Teimosinha com resgate e aposta de troca | Detalhe com alerta obrigatório |
| `PRIZE_AVAILABLE` | Aposta premiada disponível para resgate | Resultado, opções, PIX, QR e confirmação de resgate |
| `PRIZE_MP_REGISTRATION_REQUIRED` | HTTP 400, código 018003 apenas no resgate Mercado Pago | Resgate Mercado Pago; Sim/Não, e-mail informado e abertura/retorno simulados sem novo resgate |
| `PRIZE_MP_REGISTRATION_TOKEN_FAILURE` | Mesmo consentimento; HTTP 500 ao buscar o token de cadastro | Resgate Mercado Pago; Sim → erro dispensável, sem abertura externa |
| `MP_TOKEN_LOGIN_REQUIRED` | Cadastro necessário; consulta do token responde HTTP 401 | Resgate Mercado Pago; Sim → alerta de login Sim/Não |
| `MP_TOKEN_LOGIN_REDIRECT` | Consulta do token responde redirect `login` | Resgate Mercado Pago; Sim → autorização, sem abrir cadastro Mercado Pago |
| `MP_TOKEN_TERMS_REQUIRED` | Consulta do token exige aceite de termos | Resgate Mercado Pago; Sim → aviso Entendi → termos |
| `MP_TOKEN_REPEAT_REGISTRATION` | Token responde novamente com 018003 | Resgate Mercado Pago; cada nova consulta exige outro Sim, sem repetir o resgate |
| `MP_TOKEN_BUSINESS_RULE` | Token responde com mensagem/código de negócio | Resgate Mercado Pago; alerta com diagnóstico e Entendi |
| `MP_TOKEN_UNCHANGED` | Token responde redirect conhecido sem ação (`home`) | Resgate Mercado Pago; fecha loading e mantém formulário sem navegador |
| `MP_TOKEN_RESPONSE_TIMEOUT` | Consulta do token lança timeout simulado, após consentimento | Resgate Mercado Pago; mensagem de timeout ou de internet conforme conexão atual |
| `MP_TOKEN_CONNECTION_FAILURE` | Consulta do token lança falha de conexão simulada | Resgate Mercado Pago; aviso dispensável, sem abertura externa |
| `PRIZE_RESPONSE_TIMEOUT` | POST de resgate lança timeout simulado, mantendo preparação | Resgate Mercado Pago; mantém formulário e não declara sucesso |
| `PRIZE_CONNECTION_FAILURE` | POST de resgate lança falha de conexão simulada | Resgate Mercado Pago; aviso de conexão/internet, sem nova operação automática |
| `PRIZE_LOGIN_REQUIRED` | HTTP 401 no resgate | Resgate Mercado Pago; confirmação Sim/Não de login |
| `PRIZE_PROCESSING_REDIRECT` | Marcador de processamento no handler de resgate | Resgate Mercado Pago; encerra loading e mantém formulário, sem sucesso nem nova requisição |
| `PRIZE_LOGIN_REDIRECT` | Redirect de login no resgate | Resgate Mercado Pago; inicia login sem confirmação adicional |
| `PRIZE_TERMS_REQUIRED` | Redirect de aceite de termos | Resgate Mercado Pago; alerta Entendi → termos |
| `PRIZE_ACCOUNT_REGISTRATION_REQUIRED` | Redirect de cadastro do apostador | Resgate Mercado Pago; abre cadastro CAIXA |
| `PRIZE_UNAVAILABLE` | Redirect de indisponibilidade | Resgate Mercado Pago; abre indisponibilidade |
| `PRIZE_CART_REDIRECT` | Redirect de carrinho | Resgate Mercado Pago; abre carrinho |
| `PRIZE_FAVORITE_CARTS_REDIRECT` | Redirect de carrinhos favoritos | Resgate Mercado Pago; abre carrinhos favoritos |
| `PRIZE_MORE_BETS_REQUIRED` | HTTP 400, código 011010 | Resgate Mercado Pago; Apostar mais/Entendi |
| `PRIZE_BUSINESS_RULE` | Erro negocial de resgate | Resgate Mercado Pago; mensagem/código/data local |
| `PRIZE_SERVER_MESSAGE` | HTTP 500, tipo 1 | Resgate Mercado Pago; mensagem negocial/código/data |
| `PRIZE_SERVER_FAILURE` | HTTP 500, tipo 2 | Resgate Mercado Pago; mensagem genérica, sem detalhe técnico |
| `PRIZE_INVALID_IDENTIFICATION` | HTTP 500, código 2067 | Resgate Mercado Pago; alerta de CPF |

Os erros HTTP não são aplicados às duas rotas de startup, exceto no cenário específico `APP_UNAVAILABLE`. Isso permite abrir o app e validar como cada feature trata 401, 403, 422 e 500.

## Garantias automáticas

```bash
./gradlew :core:network:test \
  :core:network:jacocoTestCoverageVerification \
  verifyLegacyBackendParity
```

Os testes percorrem por reflexão todas as interfaces Retrofit e validam que:

- existem exatamente as 99 combinações de método e path da baseline legada;
- todos os cenários que retornam corpo produzem JSON válido;
- falha de rede e timeout lançam as exceções esperadas;
- o interceptor desabilitado delega integralmente para a rede real;
- rotas críticas, como Pix expirado, cota removida, prêmio e Teimosinha resgatada, retornam os marcadores de negócio esperados.

Para adicionar uma situação, inclua o valor em `MockBackendScenario`, centralize o fixture em `MockBackendFixtures`, defina a tela inicial em `mockStartDestination` quando necessário e acrescente a expectativa aos testes. Nenhum fixture deve conter credencial, token ou dado pessoal real.
# Aviso da Home após login

O cenário `post_login_payment_not_identified` retorna carrinho, última notificação e
`pagamentoNaoIdentificado=true` em `logado/usuarios/verificar-cadastro`. Permite verificar a
prioridade do aviso de compra cancelada. Não autentica usuário nem injeta sessão artificialmente;
para a Home receber os dados é necessário o fluxo de login ou a fixture do teste Compose.
O popup agora está integrado; ver `HOME_NOTIFICATION_POPUP_AUDIT.md` para validação e limites.

`post_login_notification` retorna o aviso comum, com link e sem a flag de pagamento. As variantes
`notification_read_login_required`, `notification_read_terms_required`, `notification_read_cart_redirect`,
`notification_read_business_rule`, `notification_read_server_message`, `notification_read_timeout` e
`notification_read_connection_failure` mantêm esse pós-login e afetam somente o PUT de leitura.
Não simulam autorização OIDC: use sessão autenticada/fixture de UI. O retorno do erro e suas ações
não repetem o PUT nem reabrem o popup dispensado.
