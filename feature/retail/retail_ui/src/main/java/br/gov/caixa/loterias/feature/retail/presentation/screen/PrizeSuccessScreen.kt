package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState

@Composable
internal fun PrizeSuccessDataScreen(
    state: PrizeRedemptionUiState,
    onHelp: () -> Unit,
    onHome: () -> Unit,
    onMyBets: () -> Unit,
    onReceipt: () -> Unit,
) {
    val isPix = state.selectedChannel == PrizeRedemptionChannel.PIX
    BackHandler(onBack = if (isPix) onMyBets else onHome)
    if (isPix) {
        PrizePixSuccessScreen(onHelp, onMyBets)
    } else {
        PrizeMercadoPagoSuccessScreen(state, onHelp, onHome, onMyBets, onReceipt)
    }
}
