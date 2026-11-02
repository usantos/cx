package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.betting.presentation.components.ModalityFilterDialog
import br.gov.caixa.loterias.feature.betting.presentation.screen.FavoriteBetsContent
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.FavoriteBetsViewModel

@Composable
fun FavoriteBetsRoute(
    viewModel: FavoriteBetsViewModel,
    onBack: () -> Unit,
    onTerms: () -> Unit,
    onCreateFavorite: (Int) -> Unit,
    privateDataEnabled: Boolean = true,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showFilter = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    FavoriteBetsContent(
        state = state,
        onBack = onBack,
        onTerms = onTerms,
        onCreateFavorite = onCreateFavorite,
        privateDataEnabled = privateDataEnabled,
        onFilter = { showFilter.value = true },
        onAdd = viewModel::requestAdd,
        onDelete = viewModel::requestDelete,
        onLoadMore = viewModel::loadMore
    )

    if (showFilter.value) {
        ModalityFilterDialog(
            selected = state.selectedModalityId,
            modalities = state.modalities.map { it.id to it.name },
            onDismiss = { showFilter.value = false },
            onSelect = {
                showFilter.value = false
                viewModel.selectModality(it)
            }
        )
    }
    FavoriteBetConfirmationDialogs(state, viewModel)
    FavoriteBetAdditionDialogs(state, viewModel, onBack)
}
