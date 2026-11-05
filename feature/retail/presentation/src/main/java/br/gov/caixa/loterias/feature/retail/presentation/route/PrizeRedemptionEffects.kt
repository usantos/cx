package br.gov.caixa.loterias.feature.retail.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import br.gov.caixa.loterias.core.domain.contract.betting.model.BetEntryDecision
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel

@Composable
internal fun PrizeRedemptionRedirectEffect(
    viewModel: PrizeRedemptionViewModel,
    state: PrizeRedemptionUiState,
    onRedirect: (ServiceRedirect) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    br.gov.caixa.loterias.core.presentation.effect.NavigationResumeEffect(viewModel::resumeAfterRedirect)
    val redirect = state.redirect
    if (redirect == br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect.ACCEPT_TERMS) {
        br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = br.gov.caixa.loterias.core.presentation.formatting.LEGACY_UPDATED_TERMS,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = { if (viewModel.consumeRedirect(redirect)) onRedirect(redirect) }
        )
    } else {
        LaunchedEffect(redirect) {
            if (redirect != null && viewModel.consumeRedirect(redirect)) onRedirect(redirect)
        }
    }
}

@Composable
internal fun PrizeRedemptionBetEntryEffect(
    viewModel: PrizeRedemptionViewModel,
    state: PrizeRedemptionUiState,
    onBetEntry: (BetEntryDecision) -> Unit,
    currentState: () -> PrizeRedemptionUiState,
) {
    LaunchedEffect(state.betEntryDecision) {
        currentState().betEntryDecision?.let {
            viewModel.consumeBetEntryDecision()
            onBetEntry(it)
        }
    }
}

@Composable
internal fun PrizeRedemptionInitialSelectionEffect(
    viewModel: PrizeRedemptionViewModel,
    state: PrizeRedemptionUiState,
    initialChannel: PrizeRedemptionChannel?,
    controlledPreviewChannel: PrizeRedemptionChannel?,
    initialSelectionApplied: () -> Boolean,
    onInitialSelectionApplied: () -> Unit,
    currentState: () -> PrizeRedemptionUiState,
) {
    LaunchedEffect(state.stage, initialChannel, controlledPreviewChannel) {
        if (
            !initialSelectionApplied() &&
            controlledPreviewChannel == null &&
            initialChannel != null &&
            currentState().stage == PrizeRedemptionStage.OPTIONS
        ) {
            onInitialSelectionApplied()
            viewModel.select(initialChannel)
        }
    }
}
