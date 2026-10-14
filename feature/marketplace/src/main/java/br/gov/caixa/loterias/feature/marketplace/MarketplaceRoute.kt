package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCard
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyEmptyState
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyOutlinedButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPrimaryButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePool
import br.gov.caixa.loterias.core.domain.retail.Retailer
import br.gov.caixa.loterias.core.designsystem.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSCartSummary
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MarketplaceRoute(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    onFilter: () -> Unit,
    onHelp: () -> Unit,
    onRetailerSelected: (Retailer) -> Unit,
    onPoolSelected: (String) -> Unit,
    onPoolReserved: (MarketplacePoolConfirmation) -> Unit,
    retailerView: Boolean = false,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
    refreshRevision: Int = 0,
) {
    LaunchedEffect(viewModel, refreshRevision) {
        viewModel.observeChildRevision(refreshRevision)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.confirmation) {
        state.confirmation?.let { confirmation ->
            viewModel.consumeConfirmation()
            onPoolReserved(confirmation)
        }
    }
    MarketplaceScreen(
        state,
        onBack,
        onFilter,
        onHelp,
        onRetailerSelected,
        { viewModel.onAction(MarketplaceAction.Retry) },
        { viewModel.onAction(MarketplaceAction.LoadMore) },
        onPoolSelected,
        onToggleExpanded = { viewModel.onAction(MarketplaceAction.ToggleExpanded(it)) },
        onDecrease = { viewModel.onAction(MarketplaceAction.DecreaseShares(it)) },
        onIncrease = { viewModel.onAction(MarketplaceAction.IncreaseShares(it)) },
        onFavorite = { viewModel.onAction(MarketplaceAction.RequestFavorite(it)) },
        onAddToCart = { viewModel.onAction(MarketplaceAction.AddToCart(it)) },
        onSelectTab = { viewModel.onAction(MarketplaceAction.SelectTab(it)) },
        onRemoveRetailer = { viewModel.onAction(MarketplaceAction.RequestFavoriteRetailerRemoval(it)) },
        retailerView = retailerView,
        cartTotal = cartTotal,
        cartBetCount = cartBetCount,
        onCart = onCart,
    )
    state.pendingFavoritePoolCode?.let { code ->
        val pool = state.pools.firstOrNull { it.code == code }
        if (pool != null) {
            CaixaDSLegacyDialog(
                title = "Atenção",
                message = if (pool.favoriteLottery) {
                    "Deseja excluir ${pool.lotteryName} da sua lista de favoritas?"
                } else {
                    "Deseja incluir ${pool.lotteryName} na sua lista de favoritas?"
                },
                positiveLabel = "Confirmar",
                onPositive = { viewModel.onAction(MarketplaceAction.ConfirmFavorite) },
                negativeLabel = "Cancelar",
                onNegative = { viewModel.onAction(MarketplaceAction.CancelFavorite) },
            )
        }
    }
    state.actionError?.let { message ->
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = message,
            positiveLabel = "Entendi",
            onPositive = { viewModel.onAction(MarketplaceAction.DismissActionError) },
        )
    }
    state.pendingFavoriteRetailerRemoval?.let { retailerId ->
        val retailer = state.favoriteRetailers.firstOrNull { it.id == retailerId }
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Deseja excluir ${retailer?.fantasyName.orEmpty()} da sua lista de favoritas?",
            positiveLabel = "Confirmar",
            onPositive = { viewModel.onAction(MarketplaceAction.ConfirmFavoriteRetailerRemoval) },
            negativeLabel = "Cancelar",
            onNegative = { viewModel.onAction(MarketplaceAction.CancelFavoriteRetailerRemoval) },
        )
    }
    if (state.addingPoolCodes.isNotEmpty() || state.updatingFavoriteLotteryIds.isNotEmpty()) {
        CaixaDSAuthenticationLoadingDialog()
    }
}

@Composable
fun MarketplaceScreen(
    state: MarketplaceUiState,
    onBack: () -> Unit,
    onFilter: () -> Unit,
    onHelp: () -> Unit,
    onRetailerSelected: (Retailer) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onPoolSelected: (String) -> Unit,
    onToggleExpanded: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onFavorite: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onSelectTab: (MarketplaceTab) -> Unit,
    onRemoveRetailer: (Long) -> Unit,
    retailerView: Boolean,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
) {
    CaixaDSLegacyScaffold(
        title = if (retailerView) "Lotérica favorita" else "Bolão CAIXA",
        onBack = onBack,
        gradientTopBar = false,
        topBarStartColor = CaixaDSColorTokens.LegacyCaixaBlue,
        topBarNavigationWidth = 45.dp,
        topBarTitleSize = 16.sp,
        topBarTitleContent = {
            Text(
                if (retailerView) "Lotérica favorita" else "Bolão CAIXA",
                color = CaixaDSColorTokens.White,
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = true),
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        bottomBar = { CaixaDSCartSummary(cartTotal, cartBetCount, onClick = onCart) },
        topBarActionWidth = if (retailerView) 48.dp else 96.dp,
        topBarAction = {
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                if (!retailerView) {
                    IconButton(
                        onClick = if (state.selectedTab == MarketplaceTab.POOLS) onFilter else ({}),
                        enabled = state.selectedTab == MarketplaceTab.POOLS,
                    ) {
                        Icon(
                            if (state.selectedTab == MarketplaceTab.POOLS) CaixaDSIcons.Filter else CaixaDSIcons.Search,
                            contentDescription = if (state.selectedTab == MarketplaceTab.POOLS) "Filtrar bolões" else "Pesquisar lotérica",
                            tint = if (state.selectedTab == MarketplaceTab.POOLS) CaixaDS.colors.accent else CaixaDS.colors.onBrand,
                        )
                    }
                }
                IconButton(onClick = onHelp) {
                    Icon(CaixaDSIcons.Help, contentDescription = "Ajuda", tint = CaixaDS.colors.onBrand)
                }
            }
        },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.LegacyMarketplaceCanvas),
        ) {
            if (!retailerView) {
                MarketplaceTabs(state.selectedTab, onSelectTab)
            }
            if (state.selectedTab == MarketplaceTab.POOLS && !state.lotteryFiltered) {
                state.contestHeader?.let { MarketplaceContestHeader(it) }
            }
            when {
                !retailerView && state.selectedTab == MarketplaceTab.FAVORITE_RETAILERS -> FavoriteRetailersContent(
                    state = state,
                    onOpen = onRetailerSelected,
                    onRemove = onRemoveRetailer,
                )
                else -> MarketplacePoolsContent(
                    state = state,
                    onRetry = onRetry,
                    onLoadMore = onLoadMore,
                    onPoolSelected = onPoolSelected,
                    onToggleExpanded = onToggleExpanded,
                    onDecrease = onDecrease,
                    onIncrease = onIncrease,
                    onFavorite = onFavorite,
                    onAddToCart = onAddToCart,
                )
            }
        }
    }
}

@Composable
private fun MarketplaceTabs(selected: MarketplaceTab, onSelect: (MarketplaceTab) -> Unit) {
    Row(Modifier.fillMaxWidth().height(66.dp).background(CaixaDS.colors.surface)) {
        MarketplaceTabItem("Bolões", selected == MarketplaceTab.POOLS) { onSelect(MarketplaceTab.POOLS) }
        MarketplaceTabItem("Lotéricas Favoritas", selected == MarketplaceTab.FAVORITE_RETAILERS) {
            onSelect(MarketplaceTab.FAVORITE_RETAILERS)
        }
    }
}

@Composable
private fun RowScope.MarketplaceTabItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.weight(1f).fillMaxSize().background(
            if (selected) CaixaDSColorTokens.LegacyMarketplaceSelectedTab else CaixaDS.colors.surface,
        ).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = if (selected) CaixaDS.colors.onBrand else CaixaDS.colors.legacyNeptune,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun MarketplacePoolsContent(
    state: MarketplaceUiState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onPoolSelected: (String) -> Unit,
    onToggleExpanded: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onFavorite: (String) -> Unit,
    onAddToCart: (String) -> Unit,
) {
    val listState = rememberLazyListState()
    val currentLoadMore by rememberUpdatedState(onLoadMore)
    LaunchedEffect(listState, state.canLoadMore, state.isLoadingMore) {
        snapshotFlow {
            shouldLoadMarketplacePage(
                listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0,
                listState.canScrollForward, state.canLoadMore, state.isLoadingMore,
            )
        }.distinctUntilChanged().filter { it }.collect { currentLoadMore() }
    }
    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CaixaDS.colors.legacyPrimaryButton)
        }
        state.errorMessage != null && state.pools.isEmpty() -> ErrorContent(state.errorMessage, onRetry)
        state.pools.isEmpty() -> MarketplaceEmptyState(
            "Não foram encontrados bolões disponíveis para os filtros informados. Limpe os filtros e tente novamente.",
        )
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("marketplace-pools-list"),
            state = listState,
        ) {
            item(key = "carousel") {
                MarketplaceCarousel(state, onDecrease, onIncrease, onFavorite, onPoolSelected, onAddToCart)
            }
            items(state.listPools, key = MarketplacePool::code) { pool ->
                Box(Modifier.testTag("pool-list-${pool.code}")) {
                PoolCard(
                    pool = pool,
                    expanded = pool.code in state.expandedPoolCodes,
                    selectedShares = state.selectedShares[pool.code] ?: 1,
                    lotteryFiltered = state.lotteryFiltered,
                    updatingFavorite = pool.lotteryId in state.updatingFavoriteLotteryIds,
                    addingToCart = pool.code in state.addingPoolCodes,
                    onToggleExpanded = { onToggleExpanded(pool.code) },
                    onDecrease = { onDecrease(pool.code) },
                    onIncrease = { onIncrease(pool.code) },
                    onFavorite = { onFavorite(pool.code) },
                    onDetail = { onPoolSelected(pool.code) },
                    onAddToCart = { onAddToCart(pool.code) },
                )
                }
            }
            state.errorMessage?.let { message -> item { ErrorContent(message, onRetry) } }
            if (state.isLoadingMore) {
                item { Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CaixaDSColorTokens.LegacyCaixaBlue)
                } }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun MarketplaceEmptyState(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.surface),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text("Ops!", color = CaixaDS.colors.legacyPrimaryButton, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(
                message,
                color = CaixaDS.colors.legacyPrimaryButton,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun FavoriteRetailersContent(
    state: MarketplaceUiState,
    onOpen: (Retailer) -> Unit,
    onRemove: (Long) -> Unit,
) {
    when {
        state.favoriteRetailersLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CaixaDS.colors.legacyPrimaryButton)
        }
        state.favoriteRetailersError != null -> MarketplaceEmptyState(state.favoriteRetailersError)
        state.favoriteRetailers.isEmpty() -> MarketplaceEmptyState("Você ainda não possui lotéricas favoritas.")
        else -> LazyColumn(Modifier.fillMaxSize().background(CaixaDS.colors.surface)) {
            items(state.favoriteRetailers, key = Retailer::id) { retailer ->
                Row(
                    Modifier.fillMaxWidth().border(0.5.dp, CaixaDS.colors.legacyLine).padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(retailer.fantasyName, color = CaixaDS.colors.legacyText, fontWeight = FontWeight.Bold)
                        Text("${retailer.city} - ${retailer.state}", Modifier.padding(top = 6.dp), color = CaixaDS.colors.legacyText)
                    }
                    Text(
                        "Ver Bolões",
                        Modifier.border(1.dp, CaixaDS.colors.brand, RectangleShape).clickable { onOpen(retailer) }.padding(4.dp),
                        color = CaixaDS.colors.brand,
                    )
                    IconButton(onClick = { onRemove(retailer.id) }) {
                        Icon(CaixaDSIcons.Delete, contentDescription = "Excluir ${retailer.fantasyName}", tint = CaixaDS.colors.brand)
                    }
                }
            }
        }
    }
}

@Composable
private fun PoolCard(
    pool: MarketplacePool,
    expanded: Boolean,
    selectedShares: Int,
    lotteryFiltered: Boolean,
    updatingFavorite: Boolean,
    addingToCart: Boolean,
    onToggleExpanded: () -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onFavorite: () -> Unit,
    onDetail: () -> Unit,
    onAddToCart: () -> Unit,
) {
    val color = marketplaceColors(pool.modality).light
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White)) {
        Column {
            Row(
                Modifier.fillMaxWidth().clickable(onClick = onToggleExpanded).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!lotteryFiltered) {
                    IconButton(
                        onClick = onFavorite,
                        enabled = pool.lotteryId > 0 && !updatingFavorite,
                    ) {
                        Icon(
                            if (pool.favoriteLottery) CaixaDSIcons.Favorite else CaixaDSIcons.FavoriteBorder,
                            contentDescription = if (pool.favoriteLottery) "Excluir lotérica favorita" else "Favoritar lotérica",
                            tint = CaixaDS.colors.accent,
                        )
                    }
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        if (lotteryFiltered) pool.modality.ifBlank { "Bolão" } else marketplaceRetailerName(pool.lotteryName),
                        color = if (lotteryFiltered) color else CaixaDSColorTokens.LegacyRetailSearchText,
                        style = CaixaDS.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!lotteryFiltered && pool.cityAndState.isNotBlank()) {
                        Text(marketplaceCity(pool.cityAndState), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    if (lotteryFiltered && pool.estimatedPrize.isNotBlank()) {
                        Text("Prêmio estimado: R$ ${pool.estimatedPrize}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                    if (!expanded && !lotteryFiltered) {
                        Text("Restam ${pool.availableShares} de ${pool.totalShares} cotas")
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    if (!expanded && !lotteryFiltered) Text("Valor da Cota", color = CaixaDSColorTokens.LegacySocialGraphite)
                    Text(
                        formatPoolAmount(pool.totalFor(selectedShares)),
                        color = color,
                        style = CaixaDS.typography.titleLarge,
                    )
                    Icon(
                        CaixaDSIcons.ChevronDown,
                        contentDescription = if (expanded) "Recolher bolão" else "Ver mais",
                        modifier = Modifier.rotate(if (expanded) 180f else 0f),
                        tint = CaixaDSColorTokens.Blue700,
                    )
                }
            }
            if (expanded) {
                Column(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    if (lotteryFiltered) Text("Concurso ${pool.contest}", color = color)
                    Text("${pool.availableShares} de ${pool.totalShares} cotas disponíveis")
                    Text("${pool.betQuantity} apostas • ${pool.numberQuantity} ${pool.numberLabel()}")
                    Row(
                        Modifier.fillMaxWidth().padding(top = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        IconButton(onClick = onDecrease, enabled = selectedShares > 1 && !addingToCart) { Text("−") }
                        Text(selectedShares.toString(), style = CaixaDS.typography.headlineSmall)
                        IconButton(
                            onClick = onIncrease,
                            enabled = selectedShares < pool.availableShares && !addingToCart,
                        ) { Text("+") }
                    }
                    Row(
                        Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        CaixaDSLegacyOutlinedButton("Detalhes", onDetail, Modifier.weight(1f))
                        CaixaDSLegacyPrimaryButton(
                            if (addingToCart) "Adicionando..." else "Adicionar ao carrinho",
                            onAddToCart,
                            Modifier.weight(1f),
                            enabled = pool.availableShares > 0 && !addingToCart,
                        )
                    }
                }
            }
        }
    }
}

internal fun MarketplacePool.totalFor(shares: Int) = lastSharePriceWithFeeValue +
    regularSharePriceWithFee.multiply((shares - 1).coerceAtLeast(0).toBigDecimal())

private fun MarketplacePool.numberLabel() = if (modality.contains("LOTECA", ignoreCase = true)) "palpites" else "dezenas"

private fun formatPoolAmount(amount: java.math.BigDecimal): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(amount)

private fun poolColor(modality: String): Color = when {
    modality.contains("LOTOFACIL", true) || modality.contains("LOTOFÁCIL", true) -> CaixaDSColorTokens.Lotofacil
    modality.contains("QUINA", true) -> CaixaDSColorTokens.Quina
    modality.contains("LOTOMANIA", true) -> CaixaDSColorTokens.Lotomania
    modality.contains("TIMEMANIA", true) -> CaixaDSColorTokens.Timemania
    modality.contains("DUPLA", true) -> CaixaDSColorTokens.DuplaSena
    modality.contains("DIA_DE_SORTE", true) || modality.contains("DIA DE SORTE", true) -> CaixaDSColorTokens.DiaDeSorte
    modality.contains("SUPER", true) -> CaixaDSColorTokens.SuperSete
    modality.contains("MILIONARIA", true) || modality.contains("MILIONÁRIA", true) -> CaixaDSColorTokens.MaisMilionaria
    else -> CaixaDSColorTokens.MegaSena
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    CaixaDSLegacyEmptyState(
        title = "Não foi possível carregar os bolões",
        message = message,
        actionLabel = "Tentar novamente",
        onAction = onRetry,
        modifier = modifier,
    )
}
