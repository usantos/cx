package br.gov.caixa.loterias.feature.retail.presentation.delivery

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeRedemptionScreenActions
import br.gov.caixa.loterias.feature.retail.presentation.screen.RetailSearchActions
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateUiState
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRegistrationNotice
import br.gov.caixa.loterias.feature.retail.presentation.state.RetailSearchUiState

interface RetailUiEntry : FeatureUiEntry {
    @Composable
    fun RetailSearchScreen(state: RetailSearchUiState, actions: RetailSearchActions, onBack: () -> Unit, onHelp: () -> Unit)

    @Composable
    fun PrizeRedemptionScreen(
        state: PrizeRedemptionUiState,
        onBack: () -> Unit,
        onHelp: () -> Unit,
        onHome: () -> Unit,
        onMyBets: () -> Unit,
        actions: PrizeRedemptionScreenActions,
    )

    @Composable
    fun LocationGateScreen(state: LocationGateUiState, versionName: String, @DrawableRes lotteryIconResourceId: Int, onAction: () -> Unit)

    @Composable
    fun PrizeRedemptionDialogs(
        state: PrizeRedemptionUiState,
        displayState: PrizeRedemptionUiState,
        onDismissError: () -> Unit,
        onNoticeAction: (ServiceNoticeUiState) -> Unit,
        onDismissNotice: (ServiceNoticeUiState) -> Unit,
        onRegistrationAction: (PrizeRegistrationNotice) -> Unit,
        onDismissRegistration: (PrizeRegistrationNotice) -> Unit,
        onDismissSuccessNotice: () -> Unit,
    )
}
