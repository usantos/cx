# Favoritos, ajuda e notificações — 22/10/2026

## Favoritos

- O HTTP 400 negocial dos endpoints de validação era reduzido a `AppError.Unexpected`, impedindo os coordenadores existentes de solicitar consentimento para sobrescrever um nome repetido.
- Carrinho e aposta agora preservam a mensagem negocial como validação. Respostas de autenticação, serviço e erros sem mensagem não autorizam sobrescrita.
- Os endpoints de validação e inclusão não consomem payload: aceitam sucesso 200/204 sem corpo, como o `NetworkResponse` usado pelo Legado. O contrato anterior exigia um envelope JSON, podendo falhar mesmo após sucesso no serviço.
- Testes localhost reproduziram a falha antes da correção: `/tmp/simlo-favorites-red.log` e `/tmp/simlo-favorite-empty-red.log`. Os mesmos testes passaram depois, em `/tmp/simlo-favorites-contract-green.log`.
- Os caminhos, métodos e conteúdo dos pedidos permanecem iguais. Uma rejeição na validação não envia inclusão automaticamente; o usuário confirma a substituição.

Referências: `cx/.../CarrinhoActivity.kt:onValidarCarrinhoFavorito`, `Utils.java:isErroNegocial`, `DetalhesMinhasApostasButtonHelper.java:salvarApostaFavorita` e `DadosCorporativosNuvemBO.java`.

## Ajuda da modalidade

Comparada com `AjudaActivity.java`, `custom_toolbar_ajuda.xml` e `activity_ajuda.xml`: toolbar 56dp, trevo 22×23dp, título 20sp, fechamento 20dp, metadados em duas colunas com 157dp, margens 38dp/40dp, corpo 14,2sp e margens horizontais 36dp.

Restaurada a margem inferior de 39dp do ScrollView, além do espaço final do texto. Cores, texto e cabeçalho colorido preservados.

Limitação de equivalência de conteúdo: a `AjudaActivity` consultada preenche números/datas fixos de exemplo e Lorem ipsum. A tela Compose mantém regras e concurso reais; não foi alterada para mostrar esses exemplos. Não se declara equivalência visual pixel a pixel com o APK nativo neste trabalho.

## Notificações

Comparadas com `content_lista_msgpush.xml`, `item_msg_push_swipebuttons.xml`, `search_edittext_background.xml` e `MensagemPushViewHolder.java`.

- Preservados header Caixa atual, dimensões da busca, ações 36dp, abas 56dp, ícones 36dp, título e prévia 16sp e datas 14sp.
- Corrigidos preenchimento e contorno da busca para os recursos nativos (#EBF2F4/#929EA0), com cores pertencentes ao DS.
- Título e prévia em uma linha agora cortam o excedente como as TextViews nativas, sem reticências acrescentadas pelo Compose.
- Estados expandido/lido, abas, confirmações e ações permanecem nas features. Validação de interação usa fixtures locais, sem alterar mensagens reais.

## Verificação

- APK PRD compilado com backend real, Home clássica e logging/Chucker desabilitados.
- 321 testes unitários passaram: cart/data 58; betting/data 40; cart/presentation 108; tickets/presentation 75; support/presentation 17; notifications/presentation 23.
- 19 testes de interface passaram no emulador: notificações 10, ajuda/suporte 3, detalhe da aposta/favoritos 6. Logs `/tmp/simlo-{notifications,support,tickets}-favorites-layout-ui.log`.
- `verifyCaixaDS`, `verifyKotlinFormatting`, `verifyCleanArchitecture` e `verifyLegacyBackendParity` passaram.
- Log: `/tmp/simlo-favorites-review-final.log`.
- Nenhuma aposta, pagamento, reserva ou favorito real foi criado por esta validação.
