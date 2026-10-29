package br.gov.caixa.loterias.feature.betting.navigation

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSessionFactory
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddComboToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddFavoriteBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.DeleteFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GenerateRapidBetsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingSpecialContestsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetShakeEnabledUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessages
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import java.time.Clock
import javax.inject.Inject
// A fronteira de composição preserva a ligação explícita dos casos de uso pelo Hilt e a identidade das dependências.
@Suppress("LongParameterList")
class BettingNavigationDependenciesAdapter @Inject constructor(
    override val recoveryMessages: BettingRecoveryMessages,
    override val serverTime: ServerTimeSource,
    override val bettingCheckpoints: BettingCheckpointSessionFactory,
    override val addBetToCart: AddBetToCartUseCase,
    override val addComboToCart: AddComboToCartUseCase,
    override val addFavoriteBetToCart: AddFavoriteBetToCartUseCase,
    override val betCombos: GetBetCombosUseCase,
    override val bettingSpecialContests: GetBettingSpecialContestsUseCase,
    override val bettingParameters: GetBettingParametersUseCase,
    override val currentCart: GetCurrentCartUseCase,
    override val deleteFavoriteBet: DeleteFavoriteBetUseCase,
    override val dispatchers: CoroutineDispatchers,
    override val favoriteBets: GetFavoriteBetsUseCase,
    override val generateRapidBets: GenerateRapidBetsUseCase,
    override val localClock: Clock,
    override val lotteryModalities: GetLotteryModalitiesUseCase,
    override val networkAvailability: NetworkAvailability,
    override val prepareHomeBettingAction: PrepareHomeBettingActionUseCase,
    override val rapidBetConfiguration: GetRapidBetConfigurationUseCase,
    override val removeCartItem: RemoveCartItemUseCase,
    override val saveFavoriteBet: SaveFavoriteBetUseCase,
    override val saveRapidBetConfiguration: SaveRapidBetConfigurationUseCase,
    override val bettingTutorialDismissed: GetBettingTutorialDismissedUseCase,
    override val setBettingTutorialDismissed: SetBettingTutorialDismissedUseCase,
    override val shakeEnabled: GetShakeEnabledUseCase,
    override val validateCart: ValidateCartUseCase,
    override val validateFavoriteBet: ValidateFavoriteBetUseCase,
    override val verifyAccountAccess: VerifyAccountAccessUseCase,
) : BettingNavigationDependencies
