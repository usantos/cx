package br.gov.caixa.loterias.feature.retail.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.rememberFeatureWritePermission
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.payment.navigation.PaymentRegistrationHost
import br.gov.caixa.loterias.feature.payment.navigation.paymentRegistrationViewModel
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.route.PrizeRedemptionRoute
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeReceiptUseCases
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionInput
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel

@Composable
internal fun PrizeRedemptionDestination(
    appContainer: RetailNavigationDependencies,
    betId: Long,
    keySuffix: String,
    onRedirect: (br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit,
    onBetEntry: (BetEntryDecision) -> Unit,
    initialChannel: PrizeRedemptionChannel? = null,
    previewSuccessChannel: PrizeRedemptionChannel? = null,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onHome: () -> Unit,
    onMyBets: () -> Unit,
    onForegroundHostChanged: (Boolean) -> Unit = {},
) {
    val isEntryEnabled = rememberFeatureWritePermission(AppModule.RETAIL)
    val authenticatedUser = appContainer.currentUserIdentity.currentUser()
    val prizeViewModel = viewModel(key = "prize-redemption-$keySuffix") {
        PrizeRedemptionViewModel(
            input = PrizeRedemptionInput(
                betId,
                authenticatedUser?.email
                    ?: if (appContainer.mockBackendEnabled) "mock@caixa.gov.br" else "",
                authenticatedUser?.cpf
                    ?: if (appContainer.mockBackendEnabled) "12345678901" else ""
            ),
            getDetails = appContainer.prizeDetails,
            generateLotteryCode = appContainer.lotteryRedemptionCode,
            consultPixKey = appContainer.consultPixRedemptionKey,
            redeemPrize = appContainer.redeemPrize,
            cancelPix = appContainer.cancelPixRedemption,
            receiptUseCases = PrizeReceiptUseCases(appContainer.downloadBetReceipt, appContainer.downloadPrizeReceipt),
            getBettingParameters = appContainer.bettingParameters,
            prepareBetEntry = appContainer.prepareBetEntry,
            localClock = appContainer.localClock,
            networkAvailability = appContainer.networkAvailability,
            dispatchers = appContainer.dispatchers,
            isEntryEnabled = isEntryEnabled
        )
    }
    val prizeState by prizeViewModel.state.collectAsStateWithLifecycle()
    val channelHost = legacyPrizeForegroundHost(prizeState.stage)
    DisposableEffect(channelHost) {
        onForegroundHostChanged(channelHost)
        onDispose { onForegroundHostChanged(false) }
    }
    val registrationViewModel = paymentRegistrationViewModel(appContainer)
    PrizeRedemptionRoute(
        viewModel = prizeViewModel,
        onRedirect = onRedirect,
        onBetEntry = onBetEntry,
        initialChannel = initialChannel,
        previewSuccessChannel = previewSuccessChannel,
        allowControlledSuccessPreview = appContainer.mockBackendEnabled,
        onBack = onBack,
        onHelp = onHelp,
        onHome = onHome,
        onMyBets = onMyBets,
        onRegistration = { email -> registrationViewModel.start(email) }
    )
    PaymentRegistrationHost(
        registrationViewModel,
        prizeViewModel::registrationFinished,
        onRedirect,
        simulateBrowser = appContainer.mockBackendEnabled
    )
}
