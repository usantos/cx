package br.gov.caixa.loterias.feature.marketplacefilter.presentation.route

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.screen.MarketplaceFilterScreen
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.viewmodel.MarketplaceFilterViewModel
import java.math.BigDecimal

@Composable
fun MarketplaceFilterRoute(
    viewModel: MarketplaceFilterViewModel,
    onBack: () -> Unit,
    onApply: (MarketplaceFilter) -> Unit,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
    onBackReset: (MarketplaceFilter) -> Unit = {},
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val leaveFilter = {
        onBackReset(viewModel.filterAfterBack())
        onBack()
    }
    BackHandler(onBack = leaveFilter)

    LaunchedEffect(viewModel) {
        viewModel.appliedFilters.collect(onApply)
    }

    MarketplaceFilterScreen(
        state = state,
        cartTotal = cartTotal,
        cartBetCount = cartBetCount,
        onCart = onCart,
        onAction = { action ->
            if (action == MarketplaceFilterAction.Back) leaveFilter() else viewModel.onAction(action)
        },
        modifier = Modifier.uiId("filter.0039")
    )
}
