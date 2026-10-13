# Cadastro Mercado Pago — erros da consulta do token

## Referência e escopo

`JogosConfirmadosPremioMercadoPagoActivity.redirectMp()` encerra o loading e entrega erros de `ApostaSilceBO.getMercadoPagoToken` a `RedirectNetwork.checkRedirect`. O serviço é `GET /usuarios/gerar-token-cadastro-mercado-pago?email=...`. Antes desta revisão, o Compose transformava esses erros em `AppError.Unexpected`, perdendo alertas e navegação.

Foram confrontados `checkRedirect`, `trataRedict`, `treatNegotialErros`, `trataErro500` e `enviaRegistrarMp`. Este último exige um novo Sim para chamar `redirectMp` novamente. Não há nova chamada a `postResgatarAposta` nesse callback.

## Decisões migradas

- Redirect tem prioridade sobre status/código: login, termos, cadastro, indisponibilidade, carrinho e carrinhos favoritos são destinos tipados. Termos preservam o aviso antes de navegar.
- HTTP 401 sem redirect apresenta Sim/Não para login. Não fecha o formulário de resgate nem abre o cadastro Mercado Pago.
- HTTP 400 com 011010/011011/011012 oferece Apostar mais/Entendi; HTTP 404 com o mesmo código mantém o alerta genérico com diagnóstico.
- HTTP 500 preserva prioridade do CPF 2067, mensagem de tipo 1 e mensagem genérica nos demais tipos, conforme o handler compartilhado.
- 018003 nos ramos elegíveis pede novo consentimento. Sim consulta somente o token para o e-mail selecionado; Não encerra a preparação. Cada ocorrência recebe um snapshot distinto, mesmo se um repository reutilizar a mesma instância de resultado. Callbacks antigos/duplicados não autorizam a nova tentativa.
- Redirect reconhecido pelo enum mas sem ação nesse handler encerra apenas a preparação. Valor desconhecido/vazio gera falha de operação, nunca URL externa.
- HTTP 200 com código/redirect ou token inválido continua sendo rejeitado. A resposta HTTP não pode ser reinterpretada como cadastro/resgate concluído.

## Camadas e navegação

`MercadoPagoRegistrationToken` distingue token de `ServiceFailure`. `PrepareMercadoPagoRegistration` mantém validação do e-mail e construção da URL fixa somente no ramo disponível. O contrato estreito é separado de `PrizeRedemptionRepository`.

As decisões comuns foram extraídas para `core:domain/service`, e a apresentação de avisos para `core:presentation`. Os nomes específicos de resgate são aliases, evitando manter duas implementações das mesmas regras. Há um gate próprio de cobertura para os avisos extraídos, com verificação de entradas não vazias; a execução integral desse gate via Gradle continua pendente.

O token agora usa o cliente dedicado do provider de resgate, com redirects desativados, para que a resposta chegue ao handler. Detalhes, QR, consulta/cancelamento Pix e PDFs permanecem no cliente geral. O bootstrap/rotação de pins já atualiza esse provider; nenhum endpoint, host ou pin foi inventado.

`MercadoPagoRegistrationViewModel` publica aviso, redirect, novo consentimento ou término sem ação, usando relógio injetado. `MercadoPagoRegistrationOverlay` apresenta os diálogos CaixaDS e entrega somente destinos tipados ao host. A integração cobre as quatro entradas de pagamento e o destino compartilhado de resgate. Enquanto uma navegação estiver ativa, a origem permanece pausada; ao retornar, libera a pausa sem consultar token, aprovar operação ou repetir POST automaticamente.

`MercadoPagoRegistrationConsent` usa igualdade por identidade. Copiar uma `data class` com o mesmo texto não seria suficiente: se o loading for breve, o coletor de `StateFlow` poderia suprimir o estado seguinte por igualdade e deixar o diálogo com callbacks antigos. O teste de 018003 consecutivos verifica também que os estados não são iguais. O e-mail transitório é limpo antes da publicação dos estados que não permitem nova consulta.

O cast legado de `enviaRegistrarMp` para a Activity de resgate não foi reproduzido: o coordenador tipado permite que o cadastro compartilhado funcione também nas entradas de pagamento, sem depender de Activity/Fragment.

## Cenários e limites

### Evidência executada

Artefatos em `build/verification/registration-token-errors-20260907/`:

- **441 testes JVM aprovados**: 344 de domínio/estados/rotas/regras, 34 de rede, 56 de data, 4 de preferências e 3 de navegação mock do host.
- Compilação isolada de Compose, host e composition root concluída. Permanece o aviso preexistente de depreciação de `LocalClipboardManager` no pagamento.
- Lógica selecionada de pagamento: **502/507 linhas (99,01%)**, incluindo **91/91** no arquivo do coordenador/estado/consentimento de cadastro e suas corrotinas. O gate passa a exigir mais de 95% e entradas não vazias, sem retirar classes internas.
- Avisos compartilhados: **29/29** em `core:presentation` (regras **24/24**, formatação **5/5**). Lógica selecionada de resgate/lotéricas: **382/391 (97,70%)**. A redução desse denominador decorre da extração das 24 linhas compartilhadas para outro módulo, não de exclusão de cobertura; permanece um wrapper de uma linha na feature.
- Repository/data source de resgate/cadastro: **221/221**. Domínio de `retail`: **114/114**; decisões compartilhadas de serviço: **7/7**; domínio de pagamento selecionado: **78/78**, incluindo preparação/token/resultados.
- Transporte selecionado de resgate: **40/40**; compra: **57/57**; repository/data source de pagamento: **236/239 (98,74%)**.
- Relatórios: `feature-payment-coverage.xml`, `core-presentation-coverage.xml`, `feature-retail-coverage.xml`, `retail-repository-coverage.xml`, `retail-domain-coverage.xml`, `service-domain-coverage.xml`, `core-domain-payment-coverage.xml` e os relatórios de transporte/pagamento. São medições parciais, **não cobertura global**.

### Mocks e validações restantes

Foram incluídos seis cenários observáveis: `MP_TOKEN_LOGIN_REQUIRED`, `MP_TOKEN_LOGIN_REDIRECT`, `MP_TOKEN_TERMS_REQUIRED`, `MP_TOKEN_REPEAT_REGISTRATION`, `MP_TOKEN_BUSINESS_RULE` e `MP_TOKEN_UNCHANGED`. Iniciam no resgate Mercado Pago. A preparação/detalhe continua disponível, Pix não é convertido em cadastro e a falha do token só ocorre após a ação explícita correspondente. A abertura de navegador permanece simulada nos mocks.

A validação em memória não prova follow-ups HTTP/TLS reais, callbacks Android, paridade visual nem execução única no servidor. O corpo não reutilizável protege os POSTs transacionais; o GET do token não tem corpo e não recebe essa marcação. A máquina de estados impede repetição automática pela apresentação, mas isso não é uma garantia global sobre retries HTTP de GETs.

A tentativa `./gradlew :core:domain:test :core:data:test :core:presentation:testDebugUnitTest :feature:payment:testDebugUnitTest --offline` falhou antes das tarefas por `Operation not permitted` no lock `~/.gradle/wrapper/dists/gradle-9.4.1-bin/arn2x92ynaizyzdaamcbpbhtj/gradle-9.4.1-bin.zip.lck`. Não houve contorno da restrição.

A revisão posterior [Conexão e abertura de login](PRIZE_CONNECTIVITY_PARITY_AUDIT.md) preserva timeout/falha de conexão do token e do POST de resgate e verifica conectividade no clique para login. Ainda faltam as demais falhas de parsing/perfil, retorno contextual completo após autenticação, recuperação após morte do processo e erros dos outros endpoints de resgate. Hilt/KSP, todos os flavors/Release, cobertura global acima de 95% e a matriz visual completa continuam pendentes.
