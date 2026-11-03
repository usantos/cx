package br.gov.caixa.loterias.feature.cart.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.presentation.screen.LocalCartReviewContent
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.LocalCartReviewViewModel

@Composable
fun LocalCartReviewRoute(viewModel: LocalCartReviewViewModel, onBack: () -> Unit, onRemoteCart: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    var confirm by rememberSaveable { mutableStateOf(false) }
    LocalCartReviewContent(state, onBack, onRemoteCart, viewModel::load, onConfirmDiscard = { confirm = true })
    if (state.loading) LoteriasCaixaDSAuthenticationLoadingDialog()
    if (confirm) {
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_deseja_excluir_as_apostas_locais_mos_cd1897),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onPositive = {
                confirm = false
                viewModel.discard()
            },
            onNegative = { confirm = false }
        )
    }
}
