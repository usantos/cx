package br.gov.caixa.loterias.feature.betting.navigation

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddComboToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddFavoriteBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.DeleteFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GenerateRapidBetsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase

/** Dependências deste grafo de destinos, fornecidas pela raiz de composição do aplicativo. */
interface BettingNavigationDependencies {
    val recoveryMessages: br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessages
        get() = br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessages.Identifiers
    val serverTime: br.gov.caixa.loterias.core.common.time.ServerTimeSource
        get() = br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.now() }
    val bettingCheckpoints: br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSessionFactory
    val addBetToCart: AddBetToCartUseCase
    val addComboToCart: AddComboToCartUseCase
    val addFavoriteBetToCart: AddFavoriteBetToCartUseCase
    val betCombos: GetBetCombosUseCase
    val bettingSpecialContests: br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingSpecialContestsUseCase
    val bettingParameters: GetBettingParametersUseCase
    val currentCart: GetCurrentCartUseCase
    val deleteFavoriteBet: DeleteFavoriteBetUseCase
    val dispatchers: CoroutineDispatchers
    val favoriteBets: GetFavoriteBetsUseCase
    val generateRapidBets: GenerateRapidBetsUseCase
    val localClock: java.time.Clock
    val lotteryModalities: GetLotteryModalitiesUseCase
    val networkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability
    val prepareHomeBettingAction: br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
    val rapidBetConfiguration: GetRapidBetConfigurationUseCase
    val removeCartItem: RemoveCartItemUseCase
    val saveFavoriteBet: SaveFavoriteBetUseCase
    val saveRapidBetConfiguration: SaveRapidBetConfigurationUseCase
    val bettingTutorialDismissed: br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetBettingTutorialDismissedUseCase
    val setBettingTutorialDismissed: SetBettingTutorialDismissedUseCase
    val shakeEnabled: br.gov.caixa.loterias.feature.betting.domain.usecase.GetShakeEnabledUseCase
    val validateCart: ValidateCartUseCase
    val validateFavoriteBet: ValidateFavoriteBetUseCase
    val verifyAccountAccess: VerifyAccountAccessUseCase
}
