package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.PlatformTextStyle
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSLocalTextStyle as LocalTextStyle
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSProvideTextStyle as ProvideTextStyle
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizeLoadingScreen
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeAgencyDataScreen
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeLotteryDataScreen
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeMercadoPagoDataScreen
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeOptionsScreen
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizePixDataScreen
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeSuccessDataScreen
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixActions
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState

@Composable
internal fun DeliveredPrizeRedemptionScreen(
    state: PrizeRedemptionUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onHome: () -> Unit,
    onMyBets: () -> Unit,
    actions: PrizeRedemptionScreenActions,
) {
    // Os TextViews compactos do prêmio preservam o espaçamento de fonte Android; não herdam
    // a altura de linha do corpo Material3 ao medir o texto literal de 14/16/25sp.
    ProvideTextStyle(
        LocalTextStyle.current.copy(
            fontFamily = LoteriasCaixaDSFontFamily,
            platformStyle = PlatformTextStyle(includeFontPadding = true),
            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified
        )
    ) {
        when (state.stage) {
            PrizeRedemptionStage.LOADING -> PrizeLoadingScreen(onBack)

            PrizeRedemptionStage.OPTIONS -> PrizeOptionsScreen(
                state,
                onBack,
                onHelp,
                actions.select,
                actions.betNow,
                actions.loadCurrentContest
            )

            PrizeRedemptionStage.LOTTERY -> PrizeLotteryDataScreen(state, onBack, onHelp, actions.refreshLotteryCode)

            PrizeRedemptionStage.AGENCY -> PrizeAgencyDataScreen(
                state = state,
                onBack = onBack,
                onHelp = onHelp,
                onLottery = { actions.select(PrizeRedemptionChannel.LOTTERY) },
                onDownload = actions.downloadBetReceipt
            )

            PrizeRedemptionStage.PIX_ENTRY, PrizeRedemptionStage.PIX_CONFIRM -> PrizePixDataScreen(
                state,
                onBack,
                onHelp,
                actions.pix
            )

            PrizeRedemptionStage.MERCADO_PAGO -> PrizeMercadoPagoDataScreen(
                state = state,
                onBack = onBack,
                onHelp = onHelp,
                onConfirm = actions.confirmMercadoPago,
                onEmailChange = actions.changeEmail
            )

            PrizeRedemptionStage.SUCCESS -> PrizeSuccessDataScreen(
                state = state,
                onHelp = onHelp,
                onHome = onHome,
                onMyBets = onMyBets,
                onReceipt = actions.downloadPrizeReceipt
            )
        }
    }
}
