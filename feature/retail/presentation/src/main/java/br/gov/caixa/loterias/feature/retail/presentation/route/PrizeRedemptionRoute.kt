package br.gov.caixa.loterias.feature.retail.presentation.route

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.delivery.RetailUiEntry
import br.gov.caixa.loterias.feature.retail.presentation.mapper.prizeScreenState
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel

@Composable
fun PrizeRedemptionRoute(
    viewModel: PrizeRedemptionViewModel,
    initialChannel: PrizeRedemptionChannel? = null,
    previewSuccessChannel: PrizeRedemptionChannel? = null,
    allowControlledSuccessPreview: Boolean = false,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onHome: () -> Unit,
    onMyBets: () -> Unit,
    onRegistration: (String) -> Unit,
    onRedirect: (br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit,
    onBetEntry: (br.gov.caixa.loterias.core.domain.contract.betting.model.BetEntryDecision) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PrizeRedemptionRedirectEffect(viewModel, state, onRedirect)
    PrizeRedemptionBetEntryEffect(viewModel, state, onBetEntry, currentState = { state })
    var initialSelectionApplied by remember(viewModel, initialChannel) { mutableStateOf(false) }
    val controlledPreviewChannel = previewSuccessChannel.takeIf { allowControlledSuccessPreview }
    PrizeRedemptionInitialSelectionEffect(
        viewModel = viewModel,
        state = state,
        initialChannel = initialChannel,
        controlledPreviewChannel = controlledPreviewChannel,
        initialSelectionApplied = { initialSelectionApplied },
        onInitialSelectionApplied = { initialSelectionApplied = true },
        currentState = { state }
    )
    val uiState = prizeScreenState(state, controlledPreviewChannel, allowControlledSuccessPreview)
    val receipts = rememberPrizeReceiptManager(viewModel)
    val handleBack = { if (viewModel.back()) onBack() }
    BackHandler(enabled = uiState.stage != PrizeRedemptionStage.SUCCESS, onBack = handleBack)

    FeatureModuleContent(
        module = AppModule.RETAIL,
        entryType = RetailUiEntry::class.java,
        onBack = handleBack,
        preserveActive = true
    ) { entry ->
        entry.PrizeRedemptionScreen(
            state = uiState,
            onBack = handleBack,
            onHelp = onHelp,
            onHome = onHome,
            onMyBets = onMyBets,
            actions = prizeRedemptionScreenActions(viewModel, uiState, receipts)
        )
        entry.PrizeRedemptionDialogs(
            state = state,
            displayState = uiState,
            onDismissError = viewModel::dismissError,
            onNoticeAction = { notice ->
                if (notice.destination == null) {
                    viewModel.dismissNotice(notice)
                } else {
                    viewModel.consumeNoticeAction(notice)?.let(onRedirect)
                }
            },
            onDismissNotice = viewModel::dismissNotice,
            onRegistrationAction = { notice -> viewModel.consumeRegistrationNotice(notice)?.let(onRegistration) },
            onDismissRegistration = viewModel::dismissRegistrationNotice,
            onDismissSuccessNotice = viewModel::dismissSuccessNotice
        )
    }
}
