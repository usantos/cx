# Catálogo de apostas — extração preservando comportamento

Manter BettingCatalogViewModel, BettingCatalogUiState, modelos/use cases/regras/formatadores/recursos/dependências/gates. Manter APIs/defaults públicos de LotteryModalitiesRoute, BetCombosRoute e BetComboDetailRoute. Checkout anterior preservado, sem commit/worktree/instalações globais.

## Fronteiras

- BettingCatalogRoute.kt mantém coleta e efeito LaunchedEffect(state.addedToCart), sem consumir a flag nem mudar a chave. Cada rota liga estado e callbacks diretamente ao ViewModel atual.
- LotteryModalitiesScreen.kt mantém scaffold/lista, key por ID, disponibilidade game?.open != false e fallback quando game nulo, arte especial Lotogol e divisores.
- BetCombosScreen.kt mantém scaffold/rodapé e lista de linhas chunked(2), spacer da linha ímpar, vazio, gradiente e contentPadding.
- BetComboDetailScreen.kt mantém scaffold, rodapé fixo, quantidade/total, disponibilidade de inclusão, checkbox !surprise e lista/informações. Recebe estado/callbacks; selectedCombo continua derivado a cada composição.
- BettingCatalogCards.kt recebe LegacyComboCatalogCard com corpo literal e ComboDetailEntryCard extraído do item existente, sem wrapper adicional.
- BettingCatalogComponents.kt recebe BettingCatalogContentState e CatalogCounterButton, corpos/defaults idênticos e apenas visibilidade internal.
- BettingCatalogColors.kt recebe modalityColor como Composable internal, preservando leitura dinâmica da campanha e comparação com estilo DEFAULT; sem cache.

Screens internal recebem BettingCatalogUiState e callbacks tipados existentes: onBack/onSelect/onRetry; combos recebem também cartTotal/cartBetCount/onCart; detalhe recebe onDecreaseQuantity/onIncreaseQuantity/onAddCombo/onToggleSurprise e dados/callback de carrinho. Nenhum novo estado/efeito/key/cache/layout ou modelo de ações.

## Verificação

Caracterização verde antes/depois, fase REFACTOR de comportamento existente sem fabricar RED. Seis casos novos contra APIs públicas: modalidades com loading/falha/retry/aberta/fechada/sem parâmetros; combos com linha ímpar/toque/seleção/carrinho/vazio; detalhe com limites1..15, preço e checkbox invertido; combo ausente, inclusão com loading/falha/retry e rodapé persistente; troca de ViewModel/callbacks e sucesso sem consumo/sem duplicar por recomposição; cores DEFAULT/rosa/azul recompostas dinamicamente.

Executar JVM/check/lint/cobertura/release do módulo, seis casos Android do catálogo antes/depois, app JVM PRD debug/compilePRDrelease/APKPRDdebug e nove gates. Não alegar execução da suíte Android inteira de betting, que não é afetada pela extração. Cobertura configurada de regras/ViewModels/estados não mede UI; mínimo95,01% e filtros intactos. Reconstruir corpos das três rotas, componentes, cartões e mapeador; conferir hashes e uma revisão independente somente leitura. Restaurar pacotes/display e encerrar emulador dedicado.
