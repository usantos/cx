package br.gov.caixa.loterias.feature.marketplacefilter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilter

@Composable
fun MarketplaceFilterRoute(
    viewModel: MarketplaceFilterViewModel,
    onBack: () -> Unit,
    onApply: (MarketplaceFilter) -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel) {
        viewModel.appliedFilters.collect(onApply)
    }

    MarketplaceFilterScreen(
        state = state,
        onAction = { action ->
            if (action == MarketplaceFilterAction.Back) onBack() else viewModel.onAction(action)
        },
    )
}
