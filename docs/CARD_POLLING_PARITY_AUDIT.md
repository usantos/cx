# Acompanhamento de compra com cartão — 07/09/2026

Status: distinção 200/303, atualização dos parâmetros de consulta e retry de consulta implementados. **Não conclui a migração de pagamentos nem a homologação visual.**

Revisão posterior: `CARD_SUBMISSION_REVIEW_AUDIT.md` separou falhas de preparação de resultado inconclusivo na submissão e restaurou o alerta/ação Minhas compras. Os resultados abaixo registram a etapa anterior. Reconciliação persistente e tratamentos específicos de redirects/erros permanecem pendentes.

## Evidência e correção de interpretação

A referência é `utils/CompraUtil.java` (`registraApostaAsync`, `verificaCompraProcessamento`, `trataErroRequisicao`, `chamaVerificaCompraProcessamento`) e `view/activity/ConfirmarCartaoActivity.java` (`onCompraSuccess`, `onCompraError`).

`CompraUtil` decide o fim do acompanhamento do cartão pelo protocolo HTTP e pelo limite de consultas. Ele não usa `SituacaoCompraUtil.isSituacaoFINALIZADA`, empregado em `CompraPixUtil`. Aplicar a classificação de situação como condição de término do cartão, como fazia a revisão anterior, alterava a regra: antecipava a conclusão na submissão ou continuava consultando depois de um GET 200.

As propriedades `completed`/`rejected` dos modelos continuam classificando a situação da compra para os consumidores que precisam dela, incluindo Pix. Não são mais a condição de término do acompanhamento do cartão. A confirmação continua dizendo “Estamos processando sua compra.” e orienta que a aposta precisa estar **EFETIVADA** para participar dos sorteios. Não é uma declaração de aprovação de todas as apostas.

## Contrato restaurado

| Resposta | Decisão de acompanhamento do cartão |
|---|---|
| Submissão 200/303 com envelope válido e sem redirect externo ao processamento | Iniciar na posição zero, com ID, periodicidade e repetições retornados; não terminar antecipadamente pelo campo de situação |
| Limite zero | Ir à confirmação de processamento sem a consulta extra que `coerceAtLeast(1)` introduzia |
| Consulta 200 com envelope válido | Encerrar o acompanhamento; a presença de situação final ou payload não é exigida por esse ramo legado |
| Consulta 303 com payload/metadados válidos | Incrementar a posição acumulada, substituir ID/periodicidade/limite pelos dados retornados e continuar somente enquanto posição < limite |
| Erro de transporte, código de negócio, payload/metadados inválidos ou redirect não implementado | Interromper com erro; não converter em conclusão |

Um HTTP 200 não suprime a validação de código de negócio ou redirect. Resposta HTTP com corpo totalmente vazio/nulo continua sendo erro de transporte; o caso aceito sem payload é um envelope JSON válido. Intervalos negativos são imediatos e limites negativos encerram, sem transformá-los em uma tentativa obrigatória. ID inválido em consulta preserva o ID conhecido; a submissão exige ID positivo. Metadados ausentes em submissão/303 não são inventados como zero.

## Separação de responsabilidades

- `PurchaseBackendResponse` carrega envelope e sinal de processamento derivados do HTTP. O JSON não pode sobrescrever esse sinal.
- `RemotePaymentRepository` transforma o sinal em `PurchaseProgress.nextPoll`: ausente significa fim do protocolo; presente contém `PurchasePolling` com os parâmetros de continuação. O domínio não depende de Retrofit/códigos HTTP.
- `CardPurchasePolling` mantém ID, intervalo, limite e posição, aplicando a transição sem dependência de Android ou rede.
- `PaymentCardViewModel` agenda consultas e mantém a compra já submetida. Uma falha de GET conserva ID, posição e valor original do carrinho. “Tentar novamente” nesse estado repete a consulta, sem buscar novo carrinho, tokenizar cartão ou reenviar POST. O CVV é limpo ao entrar no acompanhamento.
- Ações antigas de formulário/confirmar/recarregar não podem reabrir confirmação durante o processamento. Retry repetido e retry após conclusão não iniciam outra consulta/submissão.
- Na falha de consulta conhecida, a UI mostra “Acompanhamento da compra” e o erro correspondente, em vez de afirmar “Pagamento não aprovado”. O restante dessa tela ainda requer comparação visual e revisão de dialogs conforme o legado.
- Mocks de processamento/Pix pendente usam HTTP 303; a recusa de compra usa 303 com código/mensagem de erro. Não basta variar apenas `idSituacaoCompra` em respostas sempre 200.

## Verificação executada

`build/verification/card-polling-parity-20260907/`: **330 testes JVM aprovados** (280 de estados/domínio/regras/rotas, 17 de rede, 29 de repositories e 4 de persistência em memória). Compilação isolada do host/telas/integração concluída.

Os testes cobrem atualização de ID/intervalo/limite com tempo virtual, limite reduzido e ampliado sem reiniciar posição, zero tentativas, conclusão sem status final, classificação separada para Pix, ausência de metadados, erros antes/depois da submissão, GET após falha sem segundo POST, valor original preservado e ações repetidas/fora de etapa. Também passam os testes de envelope 303, pinagem/host/expiração e integração dos mocks reais ao data source.

Cobertura JaCoCo dos escopos compilados, incluindo classes internas de corrotinas:

- regras/estados/ViewModels de pagamento: 321/326 linhas (98,47%);
- modelos/classificação/estado de polling selecionados do domínio: 43/43 (100%), incluindo `CardPurchasePolling` 15/15;
- transporte/DTOs/provider de compra: 33/33 (100%);
- repository/data source de pagamento: 184/187 (98,40%).

Esses números **não representam cobertura global**, telas Compose ou todos os módulos. O comando `./gradlew :core:domain:test :core:data:test :feature:payment:testDebugUnitTest --offline` continua impedido antes das tarefas pelo sandbox ao abrir o lock da distribuição Gradle (`Operation not permitted`). A compilação isolada não executa Hilt/KSP, empacotamento, R8 ou testes Android.

## Pendências para equivalência integral

1. Redirects de negócio tipados e suas ações reais no grafo Compose; continuam interrompendo o fluxo, sem seguir URL arbitrária.
2. Falha de resultado desconhecido durante o POST inicial: ainda exige tratamento próprio, distinto de falha de tokenização/validação, para não oferecer nova cobrança sem reconciliação.
3. Recuperação da compra após morte do processo ou saída/reentrada da rota. A proteção atual de retry pertence ao ViewModel vivo; não é uma garantia global de idempotência.
4. Polling Pix manual/periódico, prazo e lifecycle, com as regras específicas de `CompraPixUtil`.
5. Limpeza de estado local de carrinho/carrinho favorito e recuperação no momento legado correto, callbacks, mensagens e dialogs de cada erro.
6. Matriz visual/interativa, todos os flavors/Release, testes Android/TLS e cobertura global superior a 95%.
