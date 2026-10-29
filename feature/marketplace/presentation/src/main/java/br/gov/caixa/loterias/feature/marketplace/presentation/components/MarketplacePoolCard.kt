package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplacePoolExpansion
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplacePoolSummary
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplacePoolModalityLabel
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.numberLabel
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.totalFor
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceListRetailerName
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplacePrizeWords
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceRetailerName

@Composable
internal fun PoolCard(
    pool: MarketplacePool,
    expanded: Boolean,
    selectedShares: Int,
    lotteryFiltered: Boolean,
    updatingFavorite: Boolean,
    addingToCart: Boolean,
    actions: PoolCardActions,
    modalityNames: Map<Pair<String, Int>, String> = emptyMap(),
    specialContests: MarketplaceSpecialContests = MarketplaceSpecialContests(),
    showModality: Boolean = lotteryFiltered,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val color = marketplaceColors(pool.modality).listText
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White)) {
        Column {
            CaixaDSMarketplacePoolSummary(
                title = if (showModality) {
                    marketplacePoolModalityLabel(pool, modalityNames, specialContests)
                } else {
                    marketplaceListRetailerName(pool.lotteryName)
                },
                city = marketplaceRetailerName(pool.cityAndState).let { if (it.length > 14) it.take(14) + "..." else it },
                summary = uiStrings.getString(
                    LocalizedR.string.ui_restam_1_s_de_2_s_cotas_3_s_apostas_f1a609,
                    pool.availableShares.toString(),
                    pool.totalShares.toString(),
                    pool.betQuantity.toString(),
                    pool.numberQuantity.toString(),
                    pool.numberLabel()
                ),
                amount = formatBrazilianCurrency(pool.totalFor(selectedShares)),
                lotteryFiltered = showModality,
                prize = pool.estimatedPrize.toBigDecimalOrNull()?.let(::formatBrazilianCurrency).orEmpty(),
                prizeWords = pool.estimatedPrize.toBigDecimalOrNull()?.let(::marketplacePrizeWords).orEmpty(),
                modalityColor = color, expanded = expanded, favorite = pool.favoriteLottery,
                canFavorite = pool.lotteryId > 0 && !updatingFavorite,
                onFavorite = actions.onFavorite, onToggle = actions.onToggleExpanded
            )
            if (expanded) {
                val colors = marketplaceColors(pool.modality)
                CaixaDSMarketplacePoolExpansion(
                    contest = if (lotteryFiltered) "Concurso ${pool.contest}" else null,
                    prognostics = pool.prognosticLabel(), availability = pool.shareAvailability(),
                    shares = selectedShares, canDecrease = selectedShares > 1,
                    canIncrease = selectedShares < pool.availableShares, adding = addingToCart, canAdd = pool.availableShares > 0,
                    color = colors.dark, contentColor = colors.darkContent, detailColor = color,
                    onDetail = actions.onDetail,
                    onDecrease = actions.onDecrease,
                    onIncrease = actions.onIncrease,
                    onAdd = actions.onAddToCart
                )
            }
        }
    }
}

internal data class PoolCardActions(
    val onToggleExpanded: () -> Unit,
    val onDecrease: () -> Unit,
    val onIncrease: () -> Unit,
    val onFavorite: () -> Unit,
    val onDetail: () -> Unit,
    val onAddToCart: () -> Unit,
)
