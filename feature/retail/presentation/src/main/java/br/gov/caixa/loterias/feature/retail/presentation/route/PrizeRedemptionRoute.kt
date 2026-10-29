package br.gov.caixa.loterias.feature.retail.presentation.route

import androidx.compose.ui.res.stringResource

import br.gov.caixa.loterias.feature.retail.R as LocalizedR

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSLocalTextStyle as LocalTextStyle
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSProvideTextStyle as ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeAgencyDataScreen
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeLotteryDataScreen
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeMercadoPagoDataScreen
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeOptionsScreen
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixActions
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizePixDataScreen
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizeLoadingScreen
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel
import br.gov.caixa.loterias.feature.retail.presentation.mapper.prizeScreenState
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeSuccessDataScreen

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
    onBetEntry: (br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    br.gov.caixa.loterias.core.presentation.effect.NavigationResumeEffect(viewModel::resumeAfterRedirect)
    val redirect = state.redirect
    if (redirect == br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect.ACCEPT_TERMS) {
        br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog(
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
    LaunchedEffect(state.betEntryDecision) {
        state.betEntryDecision?.let {
            viewModel.consumeBetEntryDecision()
            onBetEntry(it)
        }
    }
    var initialSelectionApplied by remember(viewModel, initialChannel) { mutableStateOf(false) }
    val controlledPreviewChannel = previewSuccessChannel.takeIf { allowControlledSuccessPreview }
    LaunchedEffect(state.stage, initialChannel, controlledPreviewChannel) {
        if (
            !initialSelectionApplied &&
            controlledPreviewChannel == null &&
            initialChannel != null &&
            state.stage == PrizeRedemptionStage.OPTIONS
        ) {
            initialSelectionApplied = true
            viewModel.select(initialChannel)
        }
    }
    val uiState = prizeScreenState(state, controlledPreviewChannel, allowControlledSuccessPreview)
    val receipts = rememberPrizeReceiptManager(viewModel)
    val handleBack = { if (viewModel.back()) onBack() }
    BackHandler(enabled = uiState.stage != PrizeRedemptionStage.SUCCESS, onBack = handleBack)

    // Os TextViews compactos do prêmio preservam o espaçamento de fonte Android; não herdam
    // a altura de linha do corpo Material3 ao medir o texto literal de 14/16/25sp.
    ProvideTextStyle(
        LocalTextStyle.current.copy(
            fontFamily = CaixaDSFontFamily,
            platformStyle = PlatformTextStyle(includeFontPadding = true),
            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified
        )
    ) {
        when (uiState.stage) {
            PrizeRedemptionStage.LOADING -> PrizeLoadingScreen(handleBack)

            PrizeRedemptionStage.OPTIONS -> PrizeOptionsScreen(
                uiState,
                handleBack,
                onHelp,
                viewModel::select,
                viewModel::betNow,
                viewModel::loadCurrentContest
            )

            PrizeRedemptionStage.LOTTERY -> PrizeLotteryDataScreen(uiState, handleBack, onHelp, viewModel::refreshLotteryCode)

            PrizeRedemptionStage.AGENCY -> PrizeAgencyDataScreen(
                state = uiState,
                onBack = handleBack,
                onHelp = onHelp,
                onLottery = { viewModel.select(PrizeRedemptionChannel.LOTTERY) },
                onDownload = {
                    val fileName = "comprovante-aposta-${uiState.details?.betId}.pdf"
                    receipts.openOrDownload(fileName, viewModel::downloadBetReceipt)
                }
            )

            PrizeRedemptionStage.PIX_ENTRY, PrizeRedemptionStage.PIX_CONFIRM -> PrizePixDataScreen(
                uiState,
                handleBack,
                onHelp,
                PrizePixActions(
                    cancelPixAndExit = viewModel::cancelPixAndExit,
                    consultPix = viewModel::consultPix,
                    confirmPix = viewModel::confirmPix,
                    selectCpfPixKey = viewModel::selectCpfPixKey,
                    selectOtherPixKey = viewModel::selectOtherPixKey,
                    changePixKey = viewModel::changePixKey,
                    togglePixOwnership = viewModel::togglePixOwnership
                )
            )

            PrizeRedemptionStage.MERCADO_PAGO -> PrizeMercadoPagoDataScreen(
                state = uiState,
                onBack = handleBack,
                onHelp = onHelp,
                onConfirm = viewModel::confirmMercadoPago,
                onEmailChange = viewModel::changeEmail
            )

            PrizeRedemptionStage.SUCCESS -> PrizeSuccessDataScreen(
                state = uiState,
                onHelp = onHelp,
                onHome = onHome,
                onMyBets = onMyBets,
                onReceipt = {
                    val fileName = "comprovante-premio-${uiState.details?.betId}.pdf"
                    receipts.openOrDownload(fileName, viewModel::downloadPrizeReceipt)
                }
            )
        }
    }
    if (uiState.loading && uiState.stage != PrizeRedemptionStage.LOADING) {
        if (uiState.stage == PrizeRedemptionStage.MERCADO_PAGO) {
            CaixaDSAuthenticationLoadingDialog()
        } else {
            Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.Black.copy(alpha = .28f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.brand)
            }
        }
    }
    state.errorMessage?.let {
        CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissError
        )
    }
    state.notice?.let { notice ->
        br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = notice.message,
            positiveLabel = notice.positiveLabel,
            negativeLabel = notice.negativeLabel,
            onPositive = {
                if (notice.destination == null) {
                    viewModel.dismissNotice(notice)
                } else {
                    viewModel.consumeNoticeAction(notice)?.let(onRedirect)
                }
            },
            onNegative = { viewModel.dismissNotice(notice) }
        )
    }
    state.registrationNotice?.let { notice ->
        br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = notice.message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onPositive = { viewModel.consumeRegistrationNotice(notice)?.let(onRegistration) },
            onNegative = { viewModel.dismissRegistrationNotice(notice) }
        )
    }
    state.successNoticeMessage?.let {
        CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissSuccessNotice
        )
    }
}
