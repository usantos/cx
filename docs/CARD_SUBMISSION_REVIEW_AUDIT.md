# Compra com resposta inconclusiva — 07/09/2026

Status: separação de preparação/submissão e alerta de revisão implementados no fluxo Compose. **Não é uma garantia global de idempotência nem homologação completa de pagamentos.**

## Referência legada

`model/bo/RedirectNetwork.java`, métodos `checkRedirectCompraAsync`, `showAlertToRedirectMinhasCompras` e `goToMinhasCompras`: em respostas inconclusivas, apresentar alerta “Atenção” com “Entendi”; a ação abre `ListaComprasActivity` e encerra a tela de confirmação. O recurso é `verificamos_compra_demorando_ou_processando`.

O texto foi conferido com `aapt2 dump resources` no APK legado `build/outputs/apk/prd/release/app-prd-release.apk`, não apenas copiado do XML. As aspas não escapadas no XML não aparecem no recurso compilado. Texto usado no Compose:

> Verificamos que sua compra está em processamento e está demorando mais que o esperado. Aguarde alguns instantes e verifique na área Minhas compras se a compra foi processada antes de tentar novamente.

SHA-256 do APK de referência lido: `78888c9e19803d125d5679f38ed61cd08aeaa521d2ab0c035d51b31c43b0e515`. A leitura de recursos confirma o texto, não a geometria do diálogo no aplicativo moderno.

## Alteração comportamental

Antes, qualquer falha de `submitCardPayment` virava um erro genérico. “Tentar novamente” retornava à confirmação e podia reenviar o cartão, inclusive quando a resposta do POST havia sido perdida. Agora:

| Etapa/resultado | Decisão |
|---|---|
| Validação e preparação/tokenização antes da chamada de registro | Falha comum, permitindo corrigir/tentar novamente sem presumir compra submetida |
| Registro com envelope válido, ID positivo e metadados | `CardSubmissionOutcome.Submitted`; iniciar o acompanhamento existente |
| Falha ou resposta inconclusiva após iniciar a chamada de registro | `CardSubmissionOutcome.ReviewRequired`; não oferecer outro POST pela mesma tela |
| Cancelamento de corrotina | Propagar o cancelamento, sem retry automático; recuperação persistente continua pendente |

O início da chamada não comprova que o servidor recebeu bytes. Sem confirmação confiável, o comportamento é conservador: orientar a revisão, não afirmar recusa nem concluir a compra. Somente ID positivo, quando já recebido, é preservado no resultado de revisão; corpo de erro, token, cartão/CVV e exceção bruta não são carregados nesse resultado nem registrados por esse tratamento.

`CardSubmissionOutcome` pertence ao domínio. `AppResult.Success(ReviewRequired)` significa que a decisão foi obtida, **não sucesso de pagamento**; o ViewModel trata os dois resultados explicitamente. A validação/preparação permanece fora da região em que falhas são convertidas em revisão.

## Compose e navegação

- `PaymentCardViewModel` entra em `REVIEW_REQUIRED`, limpa o CVV e não grava histórico de compra concluída, não inicia polling nem muda para sucesso.
- `load`, ações de formulário, `submit` e `retry` não podem retirar esse estado e reenviar o cartão. A ação Entendi é consumida uma única vez.
- `PaymentCardFlowRoute` usa `CaixaDSLegacyDialog` não cancelável, sobre a confirmação, com título/texto/botão do legado.
- O callback obrigatório abre `tickets/purchases` com `launchSingleTop` e remove a entrada de confirmação com `popUpTo(..., inclusive = true)`. Isso foi conectado tanto à rota normal quanto às quatro entradas de compatibilidade de cartão.
- A recuperação de GET já implementada continua distinta: quando uma compra conhecida falha durante consulta, o retry consulta essa compra, sem tokenizar/submeter novamente.

## Mocks e verificação

Foi adicionado `PURCHASE_RESPONSE_LOST`: somente o POST de registro falha com timeout; preparação e consultas permanecem disponíveis. O cenário abre a confirmação do cartão para exercitar o envio e o alerta, em vez de abrir diretamente uma tela final.

O teste da cadeia completa revelou duas fixtures ausentes: `meios-pagamento/{id}` e `usuarios/gerar-token-apostador-recarga-pay`. Elas foram adicionadas. Sem isso, os mocks falhavam na preparação e não exercitavam a resposta perdida. Há teste de Retrofit/data sources reais com mocks, para os dois processadores, cartão novo/salvo e resultado normal/inconclusivo. Todas as respostas ficam em memória; não há cobrança ou conexão com provedores reais.

Evidências em `build/verification/card-submission-review-20260907/`: **343 testes JVM aprovados** (284 de estados/domínio/regras/rotas, 18 de rede, 34 de repositories, 4 de persistência em memória e 3 de navegação mock). Compilação isolada do host/telas/integração concluída.

Cobertura medida, incluindo classes internas de corrotinas nos escopos analisados:

- regras/estados/ViewModels de pagamento: 330/335 linhas (98,51%);
- modelos/classificação/polling/outcomes selecionados do domínio: 45/45 (100%);
- transporte/DTOs/provider de compra: 33/33 (100%);
- repository/data source de pagamento: 190/193 (98,45%).

Esses resultados não comprovam cobertura global. O comando `./gradlew :core:domain:test :core:network:test :core:data:test :feature:payment:testDebugUnitTest --offline` falhou antes das tarefas: o sandbox impede abrir o lock da distribuição Gradle (`Operation not permitted`). A compilação isolada não executa Hilt/KSP, APK, R8, testes Android ou comparação visual.

## Pendências obrigatórias

Revisão posterior: os quatro redirects conhecidos do handler 200/303 receberam resultados tipados, navegação e testes em `PURCHASE_REDIRECT_PARITY_AUDIT.md`. Eles não caem mais indistintamente em revisão. Os tratamentos de outros erros HTTP e a homologação de retorno permanecem pendentes.

1. Persistir/reconciliar tentativas após morte do processo ou saída/reentrada do fluxo, sem guardar dados de cartão/CVV/tokens. A proteção atual pertence ao ViewModel vivo e à retirada da confirmação da pilha; não impede toda tentativa manual posterior no aplicativo.
2. Restaurar os redirects e tratamentos específicos de negócio de `RedirectNetwork`, inclusive 401, 400/404, 500 e código 018003. Nesta etapa, respostas pós-submissão sem uma confirmação utilizável vão à revisão; isso não substitui essas decisões legadas específicas.
3. Tratar cancelamento durante submissão e recuperação de uma operação já enviada depois da destruição do escopo.
4. Concluir Pix manual/periódico, limpeza de carrinho/estado favorito e todos os callbacks do pagamento.
5. Capturar o alerta/navegação no APK atual e comparar com o legado; validar todos os flavors/Release, Android/TLS e cobertura global superior a 95%.
