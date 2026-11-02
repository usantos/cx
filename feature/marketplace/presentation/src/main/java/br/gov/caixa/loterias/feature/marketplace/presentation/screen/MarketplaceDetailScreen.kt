package br.gov.caixa.loterias.feature.marketplace.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSEmptyState
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSStatusBarColor
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailBetGrid
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailDivider
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailEstimatedPrize
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailHeader
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailLotecaMatch
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailPoolInformation
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailPurchasePanel
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailRetailerData
import br.gov.caixa.loterias.feature.marketplace.presentation.components.marketplaceColors
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import java.math.BigDecimal

@Composable
fun MarketplaceDetailScreen(
    state: MarketplaceDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onAddToCart: () -> Unit,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
    onFavorite: () -> Unit = {},
    specialName: String? = null,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSStatusBarColor(LoteriasCaixaDSColorTokens.White, darkIcons = true)
    val detail = state.detail
    val colors = marketplaceColors(detail?.modality.orEmpty())
    Column(Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.White).windowInsetsPadding(WindowInsets.safeDrawing)) {
        DetailHeader(detail, colors, onBack, specialName)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = colors.dark)

                state.errorMessage != null -> LoteriasCaixaDSEmptyState(
                    title = uiStrings.getString(
                        LocalizedR.string.ui_nao_foi_possivel_carregar_o_bolao_41c8f3
                    ),
                    message = state.errorMessage,
                    actionLabel = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                    onAction = onRetry,
                    modifier = Modifier.uiId("pool.0109")
                )

                detail != null -> LazyColumn((Modifier.fillMaxSize()).uiId("pool.0110")) {
                    item {
                        DetailEstimatedPrize(detail, colors)
                        DetailDivider((Modifier.padding(top = 16.dp)).uiId("pool.0111"))
                        Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            DetailRetailerData(detail, state.favoriteLottery, state.isUpdatingFavorite, onFavorite)
                            DetailPoolInformation(detail)
                        }
                        DetailDivider(modifier = Modifier.uiId("pool.0112"))
                    }
                    if (detail.modality.contains("LOTECA", true)) {
                        itemsIndexed(detail.bets.firstOrNull()?.lotecaSelections.orEmpty()) { index, selection ->
                            DetailLotecaMatch(index, selection)
                        }
                    } else {
                        itemsIndexed(detail.bets) { index, bet ->
                            val titleColor = when {
                                detail.modality.contains("SUPER", true) -> colors.dark
                                detail.modality.contains("TIMEMANIA", true) || detail.modality.contains("DIA", true) -> colors.content
                                else -> colors.listText
                            }
                            DetailBetGrid(index, bet, titleColor, detail.modality)
                        }
                    }
                    if (!detail.modality.contains("LOTECA", true)) {
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
        if (detail != null && !state.isLoading && state.errorMessage == null) {
            DetailPurchasePanel(state, onDecrease, onIncrease, onAddToCart, cartTotal, cartBetCount, onCart)
        }
    }
}
