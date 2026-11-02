package br.gov.caixa.loterias.feature.retail.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.RetailSearchViewModel

import br.gov.caixa.loterias.feature.retail.presentation.screen.RetailSearchActions
import br.gov.caixa.loterias.feature.retail.presentation.screen.RetailSearchScreen

@Composable
fun RetailSearchRoute(viewModel: RetailSearchViewModel, onBack: () -> Unit, onHelp: () -> Unit = {}) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RetailSearchScreen(
        state,
        RetailSearchActions(
            viewModel::changeQuery,
            viewModel::searchNow,
            viewModel::requestToggleFavorite,
            viewModel::confirmToggleFavorite,
            viewModel::cancelToggleFavorite,
            viewModel::dismissError
        ),
        onBack,
        onHelp
    )
}
