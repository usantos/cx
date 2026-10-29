# Marketplace Detail — extração sem mudança de comportamento

Separar coordenação, composição, cabeçalho, dados da lotérica, grades, compra, diálogos e componentes mantendo ViewModel/comportamento atuais.

## Estrutura e interfaces

- MarketplaceDetailRoute.kt mantém assinatura/defaults públicos, collectAsStateWithLifecycle, LifecycleResumeEffect(viewModel), LaunchedEffect(state.confirmation) e consumo antes de onPoolReserved. Trocar apenas o trecho de diálogos por MarketplaceDetailDialogs(state, viewModel), depois da tela.
- MarketplaceDetailScreen.kt recebe a Screen pública com a mesma assinatura/defaults. Preservar cores lidas na composição, Column/insets/Box/when e LazyColumn no mesmo owner, itens/ordem/chaves/branch Loteca/só primeira aposta, condição do painel fixo. Extrair blocos visuais com os mesmos nós, sem wrappers de layout novos.
- MarketplaceDetailHeader.kt: DetailHeader(detail: MarketplacePoolDetail?, colors: MarketplaceColors, onBack: () -> Unit), corpo Row original; HeaderDatum permanece privado.
- MarketplaceDetailRetailer.kt: DetailRetailerData(detail: MarketplacePoolDetail, favoriteLottery: Boolean?, isUpdatingFavorite: Boolean, onFavorite: () -> Unit), corpo Row original de nome/código/cidade/favorita. Apenas adaptar state.favoriteLottery/isUpdatingFavorite aos parâmetros; sem novo estado.
- MarketplaceDetailGrids.kt: DetailBetGrid e DetailLotecaMatch internal; DetailSuperSevenGrid/DetailLotecaChoice/detailFavoriteTeamName/detailTeamName privados e íntegros.
- MarketplaceDetailPurchase.kt: DetailPurchasePanel internal, DetailQuantityButton/DetailFooterValue privados. Valores, quantidade, condições, badge+999, cart nulo, cores e cálculo por ViewModel inalterados.
- MarketplaceDetailDialogs.kt: MarketplaceDetailDialogs(state: MarketplaceDetailUiState, viewModel: MarketplaceDetailViewModel), mesmo pendingFavorite let, favoriteError let e loading if, nesta ordem; mensagens/defaults/method references idênticos.
- MarketplaceDetailComponents.kt: detailText e DetailDivider internal com corpos existentes; DetailEstimatedPrize(detail, colors) extrai Column original; DetailPoolInformation(detail) emite apenas os Text originais de quantidade/cotas/tarifas, sem Column novo.

## Invariantes

Só MarketplaceDetailRoute.kt anterior muda em produção, sete novos arquivos no pacote atual. Não alterar ViewModels/contratos/formatadores existentes em Contract/paleta em Carousel/recursos/dependências/gates. Nenhum estado, efeito, key, cache ou regra nova. Não adicionar rememberUpdatedState nem keys para viewModel/callbacks nos efeitos atuais. Cores/campanhas permanecem composables. Composição lazy e painel fixo mantêm hierarquia/geometria/semântica/tags; limites de quantidade e custos continuam com ViewModel. Helpers compartilhados internal; auxiliares locais privados.

## Validação

34JVM/check/lint/cobertura/release/três gates baseline;24Android existentes+3novos sem filtro antes/depois. Casos novos: substituição VM, limites de cotas, pedido/destino correto com confirmação consumida antes do callback e não repetida em recomposição; confirmação negativa de favorita, falha/dismiss/retry e estado antigo preservado; lifecycle com GET inicial único e posterior refresh/reset de cotas preservando favorita, falha de GET/retry. Manter os testes existentes de grades especiais/geometria/loading físico. Comparar tokens/literais e reconstruir blocos extraídos; hashes anteriores. Final módulo check/release/Android,134JVM app/compilePRDrelease/APKPRDdebug/nove gates. Cobertura configurada de ViewModels/estados231/231=100%, mínimo95%, filtros intactos; não equivale à cobertura da UI. Revisão independente VoltAgent code-reviewer e emulador dedicado restaurado/encerrado.
