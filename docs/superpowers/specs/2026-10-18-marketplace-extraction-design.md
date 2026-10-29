# Extração do Marketplace

Separar coordenação, abas, paginação, bolões, lotéricas favoritas, cartões e formatadores, preservando ViewModel e comportamento. Continuar inline no checkout existente, sem commits/worktrees ou instalação global de agentes.

## Organização

- MarketplaceRoute conserva a API pública/defaults, coleta lifecycle, efeito por ViewModel/refreshRevision, consumo da confirmação antes da navegação, todas as ações e a ordem dos diálogos, com loading por último.
- MarketplaceScreen.kt recebe MarketplaceScreen público integralmente, mantendo parâmetros/defaults, Scaffold, toolbar, carrinho, cabeçalho, abas e escolha condicional entre bolões/favoritas, inclusive combinações com retailerView.
- MarketplaceTabs.kt recebe abas e extensão RowScope privada.
- MarketplacePagination.kt recebe MarketplacePaginationEffect(listState: LazyListState, state: MarketplaceUiState, onLoadMore: () -> Unit). O helper conserva rememberUpdatedState e LaunchedEffect(listState,state.canLoadMore,state.isLoadingMore), snapshotFlow, distinctUntilChanged e filtro. Mantém a captura do snapshot state original.
- MarketplacePoolsContent.kt conserva o owner de rememberLazyListState antes do when e chama o efeito incondicionalmente no ponto original. Preserva prioridades/loading/erro/vazio/lista, chave carousel e códigos, carrossel com primeiros quatro itens, erro de append/retry/footer. Inclui os estados visuais vazio/erro compartilhados.
- MarketplaceFavoriteRetailers.kt recebe o conteúdo de favoritas com mesmas prioridades, chaves por id e callbacks por objeto/id.
- MarketplacePoolCard.kt recebe PoolCard com corpo idêntico: truncamentos, preço da primeira cota e das regulares, expansão, guardas e callbacks.
- MarketplaceFormatters.kt recebe totalFor, numberLabel e formatPoolAmount, mantendo cálculos, clamp, locale e textos.

Sete arquivos novos de produção; somente MarketplaceRoute existente muda. ViewModels, contratos, carrossel, apresentação compartilhada, regras, recursos, dependências e gates permanecem intactos.

## Verificação

Referência e hashes de fontes/recursos/configurações antes da extração. Check/cobertura/release e três gates como referência. Acrescentar três testes Android pela API pública para callback de paginação após recomposição, erro de append mantendo lista/retry e favoritas/retailerView. Executar todos os 24 testes Android do módulo contra original e extração.

Comparar corpos e hashes. Final: check/lint/cobertura configurada, release, todos os Android do módulo, JVM do app, APK PRD debug e nove gates. Análise prévia e revisão independente pelos perfis VoltAgent já autorizados. Restaurar pacotes/tela e encerrar apenas o emulador dedicado.
