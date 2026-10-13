# Compras, pagamento e resgate — referência instalada, 05/10/2026

## Referência e método

A referência principal é `build/verification/installed-reference-20261005/legacy-installed.apk`, pacote `br.gov.caixa.loterias.apostas`, versão 1.24.0 (230102400), SHA-256 `2e8ef3bd96c3db48a4391695429bf8531ccae72a821e3683a742bc61eb90dc3e`. O inventário de recursos está em `resources.txt` no mesmo diretório. Os XML binários empacotados foram lidos com SDK build-tools 36.1.0, sem instalar, acionar cobrança ou consultar uma conta:

```sh
/Users/ubiracysantos/Library/Android/sdk/build-tools/36.1.0/aapt2 dump xmltree --file res/layout/fragment_copia_cola_pix.xml build/verification/installed-reference-20261005/legacy-installed.apk
```

As 17 árvores extraídas estão em `commerce-xml/`: `activity_carrinho`, `activity_forma_pagamento`, `fragment_meio_pagamento`, `fragment_meio_pagamento_pix`, `activity_copia_cola_pix`, `fragment_card_pix`, `fragment_copia_cola_pix`, `activity_confirmacao_pagamento`, `activity_lista_compras`, `content_lista_compras`, `activity_detalhes_compras`, `content_detalhes_compras`, `fragment_escolha_chave_pix_resgate`, `fragment_confirmacao_chave_pix_resgate`, `content_confirma_resgate_pix`, `activity_resgate_premio_loterica`, `activity_resgate_premio_loterica_agencia`.

Os controles foram rastreados no checkout legado em `CopiaColaPixActivity`, `CopiaColaPixFragment`, `CompraPixUtil` e `DetalhesComprasActivity`, e nos contratos modernos citados abaixo. Isso não equivale a verificar o código compilado do APK por decompilação. Nenhum token, payload de conta, chave real ou valor pessoal foi copiado para fixtures ou documentação.

## Contratos preservados

- Nova cobrança intencional: POST `logado/carrinhos/gerar-cobranca-pix`, com `meioPagamento=6`, `parametrosPagamento=null`, `salvarCarrinhoFavorito=false`, `nomeCarrinhoFavorito=""`. `RemotePaymentRepository` mapeia `pixCopiaECola`, `txId`, `qrcodeArrByte`, `dataHoraCriacao`, `dataHoraExpiracao`, `idCompra`, `valorCobranca`.
- Consulta de cobrança existente: GET `logado/carrinhos/verifica-compra-processamento?idCompraCarrinho=<id>`. `GetPurchaseProgressUseCase` é compartilhado pelos fluxos de pagamento e detalhes de compra. Não há POST nesta ação; aprovação exige status remoto 8.
- Cartão: `RemotePaymentRepositoryTest` cobre os contratos Mercado Pago/RecargaPay e separa preparação, envio iniciado, processamento, revisão e conclusão. Reentrada não autoriza reenviar uma compra cujo resultado foi perdido.
- Resgate: GET `apostas/{id}/detalhes-premio` fornece os canais elegíveis; consulta Pix e POST `apostas/{id}/resgatar-premio` preservam identificador, canal e chave consultada. Código Lotérica/Agência: GET `apostas/{id}/app/gerar-codigo-resgate-loterica`. Cancelamento de reserva Pix mantém seu endpoint original. `RetrofitRetailRemoteDataSource` rejeita envelopes de sucesso HTTP com erro ou redirect como prova de resgate.
- O host injeta `purchaseProgress` e relógio nos detalhes de compra, consome redirects uma vez e mantém o cadastro real Mercado Pago. Aliases de preview exigem mock autorizado; produção usa aposta real ou volta para Minhas Apostas. Preview nunca altera o estado de resgate do ViewModel.

## Correções desta rodada

`PaymentViewModel.load()` não gera outra cobrança quando já existe Pix, quando uma geração está em andamento ou ao retornar de um redirect. Falha anterior à obtenção da cobrança permite tentativa explícita; uma nova instância destinada a uma nova cobrança continua com a geração inicial. O teste antigo que esperava apagar aprovação por `load()` foi corrigido para verificar conservação da mesma cobrança/aprovação.

O preview de sucesso de resgate exige `allowControlledSuccessPreview=true` (default false). Apenas canal permitido pelo servidor pode ser escolhido. Fixtures negativas não viram sucesso por abrir uma rota.

Detalhes de compra pendente (status 10, Pix com código válido e prazo ativo) passam a oferecer a consulta real do próprio identificador. Cliques simultâneos ficam bloqueados; conclusão consulta os detalhes autoritativos da mesma compra. Redirect/aviso interrompe o fluxo até consumo; retorno não consulta nem envia automaticamente. Estados 3/4/5/8/12 e cartões não entram na consulta Pix pendente. Resposta de status com ID positivo diferente do ID consultado é rejeitada no repository antes de chegar a qualquer VM; redirect/aviso mantém a precedência original, sem aprovação. Uma falha tardia ao atualizar detalhes da compra anterior não publica erro na compra atualmente selecionada. O prazo é comparado por instante: datas UTC/Z e offsets diversos são equivalentes, e data sem offset usa São Paulo.

`CaixaDSLegacyPixPanel.kt` elimina as duas implementações divergentes do painel. Não depende de domain nem executa serviços: recebe valores, estado e callbacks. Pagamento mostra introdução/cabeçalho/orientações; detalhes de compra omitem estes elementos, como `isDetalhesCompras` no legado.

Métricas do APK aplicadas: introdução horizontal 17dp e topo efetivo 25dp; faixa esquerda 5dp; marca Pix 100×36dp; linha de cartão 68dp; marca após 42dp dentro da área posterior à faixa; valor 15sp Bold com fim 28dp; separador .2dp; painel 16dp; entrada 40dp com padding interno 16dp; copiar 60×40dp; contorno 1.2dp, raio 4dp. Expiração usa fundo cinza `767F8B` e texto branco, oculta copiar e mantém o espaço do botão invisível. Timer urgente `F60E0E`, aviso `EAAA28`, contorno `4E6178`. Os valores são tokens DS centralizados. Os botões usam texto 16sp, padding horizontal 8dp/vertical 12dp e mínimo 48dp; “Ir para as” mantém Regular e “Minhas Compras” Bold.

## Matriz de cenários equivalentes e executáveis

Todos os IDs 9002/9003, códigos `CONTROLLED-...`, beneficiários e domínios `.test` dos novos testes são construídos localmente e não pagáveis. O pareamento compara estado negocial, canal, prazo e interação; dados pessoais e identificadores reais não são necessários. Os testes Android abaixo operam a UI real com use cases controlados sem HTTP. Não são capturas de uma operação financeira no legado.

| Cenário | Entrada equivalente | Resultado exigido | Teste executável |
|---|---|---|---|
| Compra 3 Pix | Débito não autorizado | Modal literal; Gerar novo Código só após clique navega para orientação Pix; sem envio automático | `PurchaseDetailRulesTest`; Android `ControlledPurchaseStatusInteractionTest[3]` |
| Compra 3 cartão | Débito não autorizado, método não Pix | Salvar Carrinho/Ajuda/Cancelar; sem gerar Pix | `PurchaseDetailRulesTest` |
| Compra 4 | Estornada | Aviso devolução Pix; cancelar não envia favorito | Android status 4; regras JVM |
| Compra 5 Pix/cartão | Cancelada | Texto específico do método; nenhuma aprovação | Android status 5; regras JVM |
| Compra 8 | Finalizada | Sem modal negativo e sem consultar Pix pendente | Android status 8; `TicketsPixPurchaseViewModelTest` |
| Compra 10 Pix ativa | Código existente, prazo futuro | Verificar consulta mesmo ID; status 10 conserva código; status 8 atualiza detalhes sem nova cobrança | Android status 10; `TicketsPixPurchaseViewModelTest`; `ControlledPixInteractionTest` |
| Compra 12 | Pagamento não identificado | Modal de estorno; cancelar não envia favorito | Android status 12; regras JVM |
| Status desconhecido/0 | Resposta sem aprovação | Nunca concluir | `TicketsPixPurchaseViewModelTest`; `RemotePaymentRepositoryTest` |
| Geração em andamento | Dois `load()` antes da resposta | Um POST de geração | `PaymentViewModelTest` |
| Pix restaurado/aprovado | Reentrada na mesma VM | Mesmo código, uma geração; aprovação conservada | `PaymentViewModelTest`; Android `ControlledPixInteractionTest` |
| Pix expirado | Prazo passado | Código Expirado; sem copiar/Verificar; Compras só pelo clique no fluxo de pagamento | Android `ControlledPixInteractionTest`; `PurchaseDetailRulesTest` prazo local/offset |
| HTTP/redirect compra | Redirect, aviso, falha rede, resposta perdida | Sem sucesso local; consumo único; retorno sem reenviar | `RemotePaymentRepositoryTest`; `PaymentViewModelTest`; `PaymentCardViewModelTest`; `PurchaseNoticeRulesTest`; tickets JVM; Android Pix redirect |
| Cartão novo/salvo | MP ou RecargaPay | Contrato real; preparação falha não envia; perda após início exige revisão | `RemotePaymentRepositoryTest`; `PaymentCardViewModelTest` |
| Resgate inelegível | Lista de canais vazia | Nenhum canal acionável e nenhum preview de sucesso implícito | `PrizePreviewRulesTest`; `PrizeRedemptionViewModelTest`; Android `ControlledPrizeInteractionTest` |
| Resgate Pix | Canal permitido, chave consultada | Consultar não resgata; confirmar envia chave consultada uma vez; sucesso abre Minhas Apostas pelo clique | `PrizeRedemptionViewModelTest`; Android `ControlledPrizeInteractionTest` |
| Resgate MP | Único canal MP permitido | Canal não permitido recusado; envio explícito único, retorno sem reenvio | `PrizeRedemptionViewModelTest` |
| Resgate Lotérica/Agência | Canal permitido e código remoto | Código/QR e prazo reais; falta QR não fabrica Lotérica; código de Agência obrigatório | `PrizeRedemptionViewModelTest`; `PrizeResultRulesTest` |
| HTTP/redirect resgate | 401, negocial, rede, processing, cadastro MP | Formulário conservado; decisão/redirect consumido uma vez; sem sucesso nem retry automático | `PrizeRedemptionViewModelTest`; `PrizeFailureRulesTest`; `RemoteRetailRepositoryTest` |

## Evidência de execução e integração

A raiz executa Gradle, APK e aparelho. Nesta subtarefa não foram executados Gradle/ADB, nem transações.

- RED pagamento observado pela raiz: 18 testes, 3 falhas comportamentais novas (geração pendente duplicada, cobrança existente restaurada, retorno de redirect), `/tmp/parity-payment-red.log`.
- RED preview observado pela raiz: 3 testes, 1 falha no preview explicitamente autorizado. Payment+Retail JVM posteriormente verdes em `/tmp/parity-commerce-green.log`. Esse resultado precede o painel compartilhado e os novos testes Android; não valida a rodada final inteira.
- RED tickets observado pela raiz: 6 testes, 5 falhas comportamentais esperadas, `/tmp/installed-parity-ticketpix-red.log`. Após implementação, a primeira compile acusou import `AppError` ausente; corrigido. Resultado verde final deve ser registrado pela raiz após execução.
- RED revisão observado pela raiz: `RemotePaymentRepositoryTest` 29/1 (ID de aprovação diferente do consultado), `/tmp/installed-parity-mismatch-red.log`; tickets 13/2 (offset de prazo e erro tardio de outra compra), `/tmp/installed-parity-tickets-review-red.log`. As três correções foram aplicadas após esse vermelho, aguardando verde final da raiz. O teste antigo de polling foi alinhado para consultar a compra 123 que havia acabado de ser submetida.
- Android: classes `ControlledPixInteractionTest` (6 cenários), `ControlledPrizeInteractionTest` (2) e `ControlledPurchaseStatusInteractionTest` (6 parametrizados). Runner `androidx.test.runner.AndroidJUnitRunner`, dependências Compose test/manifest e runner nos três módulos. Ainda sem resultado de execução nesta evidência.

Comandos para a raiz, sem backend real:

```sh
./gradlew :feature:payment:testDebugUnitTest :feature:retail:testDebugUnitTest :feature:tickets:testDebugUnitTest
./gradlew :feature:payment:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=br.gov.caixa.loterias.feature.payment.ControlledPixInteractionTest
./gradlew :feature:retail:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=br.gov.caixa.loterias.feature.retail.ControlledPrizeInteractionTest
./gradlew :feature:tickets:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=br.gov.caixa.loterias.feature.tickets.ControlledPurchaseStatusInteractionTest
```

Não há novo XML nesta rodada. Novos arquivos de produção: painel DS e `TicketsPixNoticeRules.kt`; callbacks de status/redirect/registro foram integrados pelo host sob coordenação da raiz.

## Limites que permanecem verificáveis

O XML binário confirma atributos; não comprova raster final. Faltam pares físicos do painel Pix em pagamento/detalhes, inclusive fonte, quebra e medição de Button/progress nativos versus Compose. O legado define lineSpacingExtra 4dp no Button; a geometria final desse detalhe precisa de captura equivalente. O espaçador final de 180dp em `PixContent` não foi confirmado por um par físico ou atribuído a XML empacotado nesta rodada. Não se declara igualdade integral de layout.

`CopiaColaPixFragment.onFinish` também substitui o clipboard por “Código Expirado”; o painel compartilhado agora invalida o clipboard somente quando ele ainda contém o próprio código Pix. Texto copiado posteriormente de outra origem é preservado. Dois novos testes Android cobrem copiar→expirar e copiar→outro texto→expirar, com zero operação de pagamento. Foram escritos antes do efeito de produção, mas ainda não houve execução vermelha/verde no aparelho nesta subtarefa.

Os novos testes Android usam use cases controlados, enquanto testes de repository usam transporte mock: camadas complementares, não prova de aprovação financeira real. Lotérica/Agência/MP possuem cobertura JVM de estados e contratos; novas interações Android desta rodada concentram Pix e inelegibilidade. Capturas instaladas de telas financeiras com dados equivalentes permanecem coordenação da raiz, sem confirmar uma transação no legado.

## Resultado coordenado final

Em installed-parity-final-validation.log passaram core/data162, core/network93, payment87, tickets41, retail50 e app48:481 testes JVM, zero falhas. Debug, Kotlin Release e cinco gates passaram. As correções da revisão incluem ID consultado, offset temporal e erro tardio; o transporte mock agora retorna o próprio idCompraCarrinho consultado, sem enfraquecer validação do repository.

No XCover7 passaram ControlledPixInteractionTest6, ControlledPrizeInteractionTest2 e ControlledPurchaseStatusInteractionTest6:14 cenários de interação com use cases controlados, zero operação financeira real. A geração Pix usa SavedStateHandle para impedir novo POST após perda de resposta/recriação;8 testes JVM de restauração passaram, incluindo proprietário divergente e registro incompleto/inválido. Morte real do processo com restauração completa do NavHost ainda não foi exercitada.

O alias payment/pix-expired com backend real direciona a Minhas Compras antes de criar a VM; permanece disponível como fixture mock. pix-copy mantém o fluxo normal de geração após orientação. Dados controlados respeitam campos/estados/contratos do legado, mas não representam uma compra/prêmio remoto equivalente em homologação. Pareamento financeiro oficial continua pendente da disponibilidade desses dados.
