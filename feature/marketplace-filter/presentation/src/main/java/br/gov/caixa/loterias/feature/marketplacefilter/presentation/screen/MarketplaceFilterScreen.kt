package br.gov.caixa.loterias.feature.marketplacefilter.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.delivery.MarketplaceFilterUiEntry
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import java.math.BigDecimal

/** Conversão Compose do `activity_filtra_marketplace.xml`, preservando a geometria legada. */

@Composable
fun MarketplaceFilterScreen(
    state: MarketplaceFilterUiState,
    onAction: (MarketplaceFilterAction) -> Unit,
    modifier: Modifier = Modifier,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
) {
    FeatureModuleContent(
        module = AppModule.MARKETPLACE_FILTER,
        entryType = MarketplaceFilterUiEntry::class.java,
        onBack = { onAction(MarketplaceFilterAction.Back) },
        preserveActive = false
    ) { entry ->
        entry.MarketplaceFilterScreen(state, onAction, modifier, cartTotal, cartBetCount, onCart)
    }
}
