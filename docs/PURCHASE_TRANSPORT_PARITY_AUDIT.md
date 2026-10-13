# Transporte de compras — HTTP 303

Revisão posterior: `PURCHASE_HTTP_ERROR_PARITY_AUDIT.md` amplia a leitura tipada aos metadados dos demais erros HTTP, com fallback de login para 401 e destinos de carrinho. O texto abaixo descreve a implementação/evidência histórica da etapa 303, não o encerramento da paridade de erros.

Status: transporte conectado ao fluxo real; **não certifica a paridade completa do processamento de compras**.

Revisão posterior: `CARD_POLLING_PARITY_AUDIT.md` implementou a continuação tipada 200/303, os parâmetros dinâmicos e retry de GET na compra conhecida. A implementação e os números abaixo registram a etapa anterior. Redirects de navegação, Pix completo, falha incerta de submissão e recuperação após morte do processo continuam pendentes.

## Referência legada

`CompraUtil.registraApostaAsync`, `trataErroRequisicao` e `chamaVerificaCompraProcessamento`, além de `CompraPixUtil`, tratam HTTP 303 como envelope de negócio. O corpo contém `payload`, `codigo`, `redirect` e `mensagem`. O payload informa ID, periodicidade, repetições e situação. Um 303 não significa, por si só, pagamento aprovado. Um redirect diferente de `/carrinhos/verifica-compra-processamento` exige decisão de navegação.

## Implementação conectada

- `PurchaseBackendApi` usa `Response<PurchaseEnvelopeDto>` para `POST logado/carrinhos/registrar-apostas-async` e `GET logado/carrinhos/verifica-compra-processamento`. Assim o corpo 303 chega ao decoder, em vez de ser perdido pela exceção automática do contrato anterior.
- `PurchaseBackendClient` preserva o envelope de respostas 2xx e decodifica exclusivamente 303 entre as respostas não-2xx. Outros códigos continuam sendo falhas. Corpo vazio/nulo, inválido ou com mais de 65.536 bytes no decoder não vira sucesso. O corpo 303 é fechado mesmo quando a leitura/conversão falha. Esse limite é do decoder: o Retrofit já pode ter armazenado o corpo de erro em memória; não é uma garantia de limite de download.
- `NetworkClientFactory.createPurchaseBackend` desabilita `followRedirects`, `followSslRedirects` e `retryOnConnectionFailure` no cliente dedicado. Mantém bearer, configuração dos logs/cURL, mocks, `CertificatePinner` e guardas de host/expiração. Não altera a política de redirects dos clientes de uso geral.
- `AppContainer` recria o cliente com o mesmo conjunto de pins verificado ao preparar/renovar a segurança. `ApplicationModule` injeta o provider obrigatório no data source; antes da preparação ele falha explicitamente, sem criar um cliente alternativo sem pinagem.
- `RetrofitPaymentRemoteDataSource` usa esse provider nas duas operações. As demais operações e a tokenização dos processadores continuam nas interfaces existentes. As declarações antigas em `LegacyBackendApi` permanecem no inventário de endpoints, mas não são mais o caminho de execução dessas duas chamadas.
- `RemotePaymentRepository` valida código, redirect e payload antes do mapeamento. O redirect de processamento é aceito; outros não são seguidos como URLs nem convertidos em aprovação. Submissão exige ID positivo. Situação ausente não é final; periodicidade e repetições da submissão mantêm o contrato de domínio.
- `ServerTimeAdapterFactory` reconhece também o novo envelope, preservando a observação de `dataHoraServidor` em 200/303.

## Verificação

Evidências em `build/verification/purchase-303-transport-20260907/`: **317 testes JVM aprovados** (271 de estados/domínio/regras/rotas, 16 de rede, 26 de repositories e 4 de persistência em memória), mais compilação isolada do host/telas/integração.

JaCoCo dos fontes compilados nesta revisão: transporte/DTOs/provider de compra, 28/28 linhas (100%); `RemotePaymentRepository` e `RetrofitPaymentRemoteDataSource`, incluindo classes internas de corrotinas, 174/177 (98,31%). Relatórios `purchase-transport-coverage.xml` e `payment-repository-coverage.xml`. Esses escopos não medem todo o módulo de rede/data, UI nem o aplicativo inteiro. O gate Gradle de rede passou a incluir também as novas classes e corrotinas, mas sua execução completa continua impedida conforme descrito abaixo.

Os testes novos exercitam respostas Retrofit inteiramente interceptadas, sem servidor/socket nem cobrança: POST/GET, query de ID, metadados, código/redirect, relógio do servidor, cabeçalho Location, política do cliente, hosts não autorizados/expiração, fechamento do corpo, cancelamento, falhas de transporte, resposta vazia/inválida/limítrofe e troca do provider. Testes do repository validam payloads ausentes, códigos de erro, redirects não suportados, ID inválido e mapeamento sem aprovação de débito iniciado.

A compilação isolada usa dependências locais já disponíveis. O comando Gradle `./gradlew :core:network:test :core:data:test --offline` foi tentado nesta revisão e falhou antes de iniciar as tarefas: o sandbox não permite abrir o lock `~/.gradle/wrapper/dists/gradle-9.4.1-bin/arn2x92ynaizyzdaamcbpbhtj/gradle-9.4.1-bin.zip.lck` (`Operation not permitted`). Não houve remoção de lock nem mudança de permissões.

## Pendências obrigatórias

Atualização posterior: [Transporte transacional de resgate e compra](PRIZE_TRANSACTION_TRANSPORT_AUDIT.md) adicionou corpo não reutilizável aos POSTs. A política anterior de `retryOnConnectionFailure(false)` isoladamente não cobria follow-up de HTTP 503 com `Retry-After: 0`; não deve ser interpretada como garantia absoluta de ausência de reenvio. Reconciliação entre processos e comportamento TLS/runtime permanecem pendentes.

1. Representar os redirects de negócio como decisões tipadas de domínio e restaurar suas ações no grafo Compose. Atualmente, um redirect não relacionado ao processamento interrompe o fluxo com orientação de acompanhamento; isso é uma proteção intermediária, não paridade com a navegação legada.
2. Restaurar a distinção entre término HTTP 200 e continuidade HTTP 303 no acompanhamento. A periodicidade/repetições atualizadas em cada consulta ainda não são consumidas pela máquina de estados moderna. Preservar esses dados no DTO não conclui essa regra.
3. Separar falha de submissão de falha de acompanhamento e revisar a ação manual de tentar novamente, retomada e recriação para não reenviar cobrança aceita ou de resultado desconhecido. Desativar retries de transporte não resolve sozinho esse problema.
4. Restaurar todos os ramos de Pix manual/periódico, prazo, esgotamento, mensagens negativas e limpeza do carrinho no momento correto.
5. Executar Gradle/Hilt/KSP, testes Android/TLS, todos os flavors/Release/R8, cobertura global e a matriz visual/interativa. Os testes sem sockets não certificam uma negociação TLS real nem o comportamento de redirecionamento em uma conexão real.
