# Situações de compra e processamento — 07/09/2026

Status: classificação das situações corrigida no domínio, cartão, Pix e mocks; **protocolo completo de processamento/redirects ainda não homologado**.

Correção posterior de escopo: `CARD_POLLING_PARITY_AUDIT.md` confirmou que **o cartão encerra pelo protocolo de `CompraUtil`, não pela classificação de status usada no Pix**. O cartão agora segue término 200, continuação 303 e limite acumulado. A classificação dos códigos abaixo permanece válida, mas as afirmações históricas sobre término imediato/rejeição do cartão apenas por situação foram substituídas por essa regra.

Atualização posterior: o cliente dedicado e a integração do envelope HTTP 303 ao data source foram implementados em `PURCHASE_TRANSPORT_PARITY_AUDIT.md`. A análise abaixo registra a lacuna encontrada nesta revisão anterior; redirects de navegação, polling dinâmico e retomada continuam pendentes.

## Divergência encontrada

O modelo moderno `PurchaseProgress` tratava `idSituacaoCompra = 2` como aprovação. O legado `SituacaoCompraUtil` define 2 como **débito iniciado**, ainda em andamento. Essa interpretação podia fazer o Pix sair da tela de acompanhamento e gravar histórico local antes da conclusão. A submissão de cartão também encerrava imediatamente ao receber 2.

A referência autoritativa é `SituacaoCompraUtil.isSituacaoFINALIZADA` / `isSituacaoNegativa`, confrontada com `CompraUtil`, `CompraPixUtil` e as Activities de confirmação/Pix.

| Códigos | Significado no legado | Tratamento moderno corrigido |
|---|---|---|
| 1, 2, 7, 9, 10 | Carrinho, débito iniciado, em processamento, represada, aguardando Pix | Não concluem o pagamento por classificação de status |
| 6, 8, 13, 14 | Débito realizado; finalizada; todas as apostas efetivadas; contém apostas não efetivadas | Encerram a consulta de pagamento, conforme o helper legado |
| 3, 4, 5, 11, 12 | Débito não autorizado, estornada, cancelada, aguardando devolução Pix, cancelada por prazo | Resultado negativo; não gravam compra concluída nesse ramo |
| Ausente/desconhecido | Sem comprovação de situação final | Não é convertido em aprovação |

Encerrar a consulta de pagamento **não significa que todas as apostas foram efetivadas**. Em particular, 14 contém apostas não efetivadas. A tela de conclusão continua orientando o usuário a acompanhar o processamento em Minhas Compras. O legado classifica 11 também como processamento em outro helper, mas o inclui expressamente entre situações negativas; o resultado da compra foi mantido negativo.

## Alterações

- `PurchaseStatus.kt` centraliza os códigos; `PurchaseProgress` e `PurchaseSubmission` compartilham a classificação. A propriedade incorreta `approved` foi substituída por `completed`, limitada à conclusão do acompanhamento de pagamento.
- `PaymentCardViewModel` não conclui imediatamente o débito iniciado. Consulta o andamento e trata todos os códigos finais/negativos, não somente 2/3. O comportamento existente ao esgotar tentativas permanece uma navegação para acompanhamento de processamento, **não uma confirmação de efetivação**; sua equivalência completa depende da revisão do HTTP 303 abaixo.
- `PaymentViewModel` mantém Pix iniciado/aguardando em acompanhamento, sem `pixApproved` e sem gravação de compra recente. Estados finais passam pela gravação e estados negativos não.
- Mocks `PURCHASE_PROCESSING`, `PURCHASE_APPROVED` e `PURCHASE_REJECTED` agora retornam 7/8/3 tanto na submissão quanto na consulta. `PIX_PENDING` retorna 10 na consulta; `PIX_EXPIRED`, 12. O happy path usa 8.
- A regra específica desses endpoints foi movida antes do prefixo genérico `logado/carrinhos`, que antes devolvia um carrinho em vez de um payload de processamento. Testes verificam os payloads reais da camada de mock, não somente fakes de ViewModel.

## Verificação

Evidências em `build/verification/payment-status-parity-20260907/`: 296 testes JVM aprovados (271 de estados/domínio/regras/rotas, 6 de envelope/mocks HTTP, 15 de repositories remotos e 4 de persistência com stores em memória), além de compilação isolada do host/telas/integração.

Cobertura medida: classificação e modelos de status (`PurchaseStatus`, `PurchaseProgress`, `PurchaseSubmission`), 23/23 linhas (100%); regras/ViewModels/estados de pagamento incluindo corrotinas, 300/305 (98,36%). Esses escopos não representam o domínio inteiro, renderização nem cobertura global do aplicativo.

Os casos novos exercitam todos os códigos 1–14, valores ausentes/desconhecidos/extremos, submissão e polling, débito iniciado antes de qualquer consulta, negativos imediatos/tardios, finalização parcial e ausência de gravação antecipada do Pix. Os mocks HTTP são interceptados localmente; não houve conexão com serviços reais nem cobrança.

Esses testes não certificam navegador, permissões, SQLite Android, Gradle/Hilt/KSP, APK, R8, flavors ou paridade visual global.

## HTTP 303: lacuna confirmada, ainda não corrigida nesta revisão

No legado, `CompraUtil.trataErroRequisicao` lê o envelope do HTTP 303. Redirect diferente de `/carrinhos/verifica-compra-processamento` segue para a navegação correspondente; código de erro ou payload ausente interrompe. Nos demais casos, o payload mantém ID, periodicidade e repetições para continuar o polling. `CompraPixUtil` também trata esse protocolo.

O contrato Retrofit atual retorna diretamente `ApiEnvelope<JsonElement>` para `registerCartBetsAsync` e `purchaseProcessing`. A implementação local da biblioteca Retrofit 3.0.0 (`OkHttpCall.parseResponse`, `KotlinExtensions.await`) confirma que HTTP não-2xx vira `HttpException`; o data source atual não recupera o envelope 303 e lê apenas `.payload` no sucesso. `ApiEnvelope` também não mapeia `codigo`. Corrigir somente os status não corrige essa lacuna.

Próximas exigências para paridade:

1. Preservar envelope, código, redirect e metadados de polling nas respostas 200/303, com DTO e decisão tipados; não promover qualquer 303 a sucesso.
2. Evitar redirecionamento HTTP automático de operações de compra que possa ocultar o envelope ou transformar POST em outra requisição. Não seguir URL arbitrária recebida no corpo.
3. Aplicar redirects de negócio pelo grafo do aplicativo, sem relançar submissão/cartão automaticamente. Distinguir falha na submissão de falha ao acompanhar compra já aceita.
4. Restaurar periodicidade/repetições retornadas durante o polling, comportamento manual/periódico do Pix, fim do prazo, esgotamento e retomada/recriação sem cobrança duplicada.
5. Homologar mensagem/ações para cada negativo, limpar o carrinho no momento legado correto, executar a matriz real em todos os flavors/Release e medir cobertura global.

Não considerar processamento de pagamento integralmente migrado enquanto esses itens estiverem pendentes.
