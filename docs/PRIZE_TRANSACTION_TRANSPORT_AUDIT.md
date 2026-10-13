# Transporte transacional de resgate e compra

Revisão posterior: [Erros da consulta do token](REGISTRATION_TOKEN_ERROR_AUDIT.md) adicionou o GET de cadastro Mercado Pago ao cliente dedicado e migrou suas decisões de erro. As afirmações e números abaixo registram o escopo anterior, que separava apenas o POST.

## Diferença encontrada

O fluxo legado `JogosConfirmadosPremioMercadoPagoActivity` encaminha erros tanto de `postResgatarAposta` quanto de `getMercadoPagoToken` para `RedirectNetwork.checkRedirect`. A migração já interpretava os erros do POST, mas o data source ainda usava o cliente HTTP geral: uma resposta de redirect podia ser seguida pelo transporte antes de chegar à decisão de apresentação. O tratamento específico dos erros do token continua pendente; esta revisão não o implementa.

O código-fonte da dependência em cache, OkHttp 5.5.0 (`RetryAndFollowUpInterceptor.kt`), também mostra que `retryOnConnectionFailure(false)` não impede sozinho o follow-up de HTTP 503 com `Retry-After: 0`. O loop rejeita um follow-up com corpo `isOneShot() == true`. A proteção abaixo usa esse contrato público, sem modificar a biblioteca.

## Implementação

- `NetworkClientFactory.createPrizeRedemption` cria um cliente dedicado com redirects HTTP/HTTPS e recuperação automática por falha de conexão desativados. Reutiliza a declaração Retrofit existente de `apostas/{id}/resgatar-premio`, sem duplicar o endpoint.
- `PrizeBackendApiProvider` é obrigatório no `RetrofitRetailRemoteDataSource`. Somente `redeemPrize` usa esse cliente. Detalhes, QR, consulta/cancelamento Pix, token e PDFs mantêm seus clientes anteriores. Não há fallback para o cliente geral se a configuração transacional estiver ausente.
- Hilt fornece a mesma instância singleton ao data source e ao `AppContainer`. O bootstrap e a atualização dos pins substituem o cliente dedicado juntamente com os demais clientes, usando o mesmo ambiente, Gson/relógio, token, logging, mock e conjunto de pins verificado. A substituição não dispara requisições; chamadas já em andamento não são reiniciadas.
- `SingleAttemptTransactionInterceptor` marca o corpo como `isOneShot`, preservando bytes, tipo, tamanho, duplex, método, URL, headers e tags. Foi aplicado ao cliente de resgate e ao de compra assíncrona. GETs de acompanhamento não recebem corpo; a periodicidade continua sendo decidida pelo caso de uso.
- Autenticação, mascaramento de logs/cURL, mocks e as duas verificações de host pinado continuam na fábrica comum. Nenhum novo host, pin, Activity ou XML foi adicionado. O contrato Retrofit continua coberto pela regra ProGuard existente de `LegacyBackendApi`.

Essa é uma proteção deliberada de segurança para operações transacionais, não uma afirmação de que a pilha Volley legada tivesse a mesma política de retries. Ela impede reenvios automáticos pelo cliente configurado; não oferece garantia global de execução única no servidor, nem substitui reconciliação após perda de resposta ou morte do processo.

## Testes e limites

`PrizeBackendTransportTest` confere flags efetivas do builder, preservação de status/redirect/código com headers `Location` e `Retry-After`, propagação de falha, bearer, logs mascarados, mocks e bloqueio de hosts não pinados/configuração expirada antes de sockets. `SingleAttemptTransactionInterceptorTest` confere integridade do corpo, preservação da requisição e distinção POST/GET no cliente de compra. `RemoteRetailRepositoryTest` verifica o roteamento entre clientes, ausência de fallback e substituição para a próxima requisição sem replay.

As respostas interceptadas em memória não atravessam a maquinaria real de follow-up do OkHttp. Por isso os testes de resposta são acompanhados de inspeção das flags/corpo configurados, mas não são apresentados como prova de rede TLS real. A cobertura do novo provider e interceptor, incluindo a classe interna do corpo, está incluída no gate de rede.

### Evidência executada

Artefatos em `build/verification/prize-transaction-transport-20260907/`:

- **429 testes JVM aprovados**: 334 de domínio/estados/rotas/regras, 34 de rede, 54 de data, 4 de preferências e 3 de navegação mock do host.
- Compilação isolada de Compose, host e composition root concluída com o novo provider obrigatório. Permanece o aviso preexistente de depreciação de `LocalClipboardManager` no pagamento.
- Lógica selecionada de `retail`: **405/414 (97,83%)**; repository/data source e auxiliares de resgate: **209/209**; domínio compilado de `retail`: **118/118**.
- Parser/DTO/provider/interceptor transacional: **40/40**. Nesse conjunto, `PrizeBackendApiProvider` tem **3/3** e `SingleAttemptTransactionInterceptor` **11/11**, incluindo o corpo interno. Esse relatório não representa toda a fábrica/módulo de rede.
- Lógica selecionada de pagamento: **452/457 (98,91%)**; repository/data source de pagamento: **236/239 (98,74%)**; transporte/DTOs selecionados de compra: **57/57**.
- Relatórios: `feature-retail-coverage.xml`, `retail-repository-coverage.xml`, `retail-domain-coverage.xml`, `prize-transport-coverage.xml`, `feature-payment-coverage.xml`, `payment-repository-coverage.xml` e `purchase-transport-coverage.xml`. São medições parciais, **não cobertura global**.

A tentativa Gradle `./gradlew :core:network:test :core:data:test :app:compileAutomacaoDebugKotlin --offline` não iniciou tarefas: `Operation not permitted` ao abrir o lock do wrapper em `~/.gradle/wrapper/dists/gradle-9.4.1-bin/arn2x92ynaizyzdaamcbpbhtj/gradle-9.4.1-bin.zip.lck`. A restrição não foi contornada. Compilação isolada não substitui Hilt/KSP, APK, R8, testes TLS, callbacks Android, todos os flavors nem a matriz visual completa.

Permanecem pendentes os erros dos outros endpoints de resgate/token, a volta contextual completa após login, reconciliação entre processos, paridade visual e cobertura global acima de 95%. O objetivo integral permanece em andamento.
