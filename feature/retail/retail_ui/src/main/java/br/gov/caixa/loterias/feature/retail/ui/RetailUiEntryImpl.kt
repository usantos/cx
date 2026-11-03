package br.gov.caixa.loterias.feature.retail.ui

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.feature.retail.presentation.delivery.RetailUiEntry
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeRedemptionScreenActions
import br.gov.caixa.loterias.feature.retail.presentation.screen.RetailSearchActions
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateUiState
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRegistrationNotice
import br.gov.caixa.loterias.feature.retail.presentation.state.RetailSearchUiState

@Keep
class RetailUiEntryImpl : RetailUiEntry {
    @Composable
    override fun RetailSearchScreen(state: RetailSearchUiState, actions: RetailSearchActions, onBack: () -> Unit, onHelp: () -> Unit) {
        br.gov.caixa.loterias.feature.retail.presentation.screen.DeliveredRetailSearchScreen(state, actions, onBack, onHelp)
    }

    @Composable
    override fun PrizeRedemptionScreen(
        state: PrizeRedemptionUiState,
        onBack: () -> Unit,
        onHelp: () -> Unit,
        onHome: () -> Unit,
        onMyBets: () -> Unit,
        actions: PrizeRedemptionScreenActions,
    ) {
        br.gov.caixa.loterias.feature.retail.presentation.screen.DeliveredPrizeRedemptionScreen(
            state,
            onBack,
            onHelp,
            onHome,
            onMyBets,
            actions
        )
    }

    @Composable
    override fun LocationGateScreen(
        state: LocationGateUiState,
        versionName: String,
        @DrawableRes lotteryIconResourceId: Int,
        onAction: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.retail.presentation.screen.DeliveredLocationGateScreen(
            state,
            versionName,
            lotteryIconResourceId,
            onAction
        )
    }

    @Composable
    override fun PrizeRedemptionDialogs(
        state: PrizeRedemptionUiState,
        displayState: PrizeRedemptionUiState,
        onDismissError: () -> Unit,
        onNoticeAction: (ServiceNoticeUiState) -> Unit,
        onDismissNotice: (ServiceNoticeUiState) -> Unit,
        onRegistrationAction: (PrizeRegistrationNotice) -> Unit,
        onDismissRegistration: (PrizeRegistrationNotice) -> Unit,
        onDismissSuccessNotice: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.retail.presentation.components.DeliveredPrizeRedemptionDialogs(
            state,
            displayState,
            onDismissError,
            onNoticeAction,
            onDismissNotice,
            onRegistrationAction,
            onDismissRegistration,
            onDismissSuccessNotice
        )
    }
}
