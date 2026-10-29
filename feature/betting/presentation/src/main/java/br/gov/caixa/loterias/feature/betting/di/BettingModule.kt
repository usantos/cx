package br.gov.caixa.loterias.feature.betting.di

import android.content.Context
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.common.time.MonotonicServerClock
import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.core.domain.persistence.secureEnvironmentKey
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.persistence.AndroidSecureStores
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingRemoteDataSource
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.FavoriteBetRemoteDataSource
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.FavoriteBetRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.RapidBettingRemoteDataSource
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.RapidBettingRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.betting.data.recovery.SecureBettingCheckpointSession
import br.gov.caixa.loterias.feature.betting.data.repository.FavoriteBetRepositoryImpl
import br.gov.caixa.loterias.feature.betting.data.repository.RapidBettingRepositoryImpl
import br.gov.caixa.loterias.feature.betting.data.repository.ShakeSettingsRepositoryImpl
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSessionFactory
import br.gov.caixa.loterias.feature.betting.domain.repository.BettingRepository
import br.gov.caixa.loterias.feature.betting.domain.repository.FavoriteBetRepository
import br.gov.caixa.loterias.feature.betting.domain.repository.RapidBettingRepository
import br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCart
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddComboToCart
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddComboToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddFavoriteBetToCart
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddFavoriteBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.DeleteFavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.usecase.DeleteFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GenerateRapidBets
import br.gov.caixa.loterias.feature.betting.domain.usecase.GenerateRapidBetsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombos
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingSpecialContests
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingSpecialContestsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetFavoriteBets
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalities
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetRapidBetConfiguration
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntry
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingAction
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PreparePoolEntry
import br.gov.caixa.loterias.feature.betting.domain.usecase.PreparePoolEntryUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveRapidBetConfiguration
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessage
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessages
import br.gov.caixa.loterias.feature.onboarding.domain.repository.OnboardingRepository
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissed
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object BettingModule {
    @Provides
    fun recoveryMessages(@ApplicationContext context: Context): BettingRecoveryMessages = BettingRecoveryMessages { message ->
        val resource = when (message) {
            BettingRecoveryMessage.WRITE_FAILED -> R.string.betting_recovery_write_failed
            BettingRecoveryMessage.RECONCILE_REQUIRED -> R.string.betting_recovery_reconcile_required
            BettingRecoveryMessage.RULES_UNAVAILABLE -> R.string.betting_recovery_rules_unavailable
            BettingRecoveryMessage.CONTEST_CHANGED -> R.string.betting_recovery_contest_changed
            BettingRecoveryMessage.READ_FAILED -> R.string.betting_recovery_read_failed
            BettingRecoveryMessage.INTENT_FAILED -> R.string.betting_recovery_intent_failed
        }
        context.getString(resource)
    }

    @Provides @Singleton
    fun bettingCheckpoints(
        @ApplicationContext context: Context,
        tokenStore: SessionTokenStore,
        configuration: HostFeatureConfiguration,
    ): BettingCheckpointSessionFactory {
        val stores = AndroidSecureStores(context)
        fun environment() = secureEnvironmentKey(
            configuration.backendSessionNamespace,
            configuration.backendApiBaseUrl
        )
        return BettingCheckpointSessionFactory {
            val lease = tokenStore.captureLocalPersistenceLease()
            val identity = tokenStore.captureIdentity()
            val capturedEnvironment = environment()
            val scope = SecureStoreScope(
                capturedEnvironment,
                identity?.ownerKey ?: "guest",
                "betting-checkpoint-v1"
            )
            SecureBettingCheckpointSession(stores.open(scope)) {
                lease() && capturedEnvironment == environment() &&
                    (identity?.let(tokenStore::isCurrent) ?: (tokenStore.captureIdentity() == null && tokenStore.token() == null))
            }
        }
    }

    @Provides
    fun bettingDataSource(source: BettingRemoteDataSourceImpl): BettingRemoteDataSource = source

    @Provides @Singleton
    fun rapidBettingDataSource(provider: BackendApiProvider): RapidBettingRemoteDataSource = RapidBettingRemoteDataSourceImpl(provider)

    @Provides @Singleton
    fun favoriteBetDataSource(provider: BackendApiProvider): FavoriteBetRemoteDataSource = FavoriteBetRemoteDataSourceImpl(provider)

    @Provides @Singleton
    fun rapidBettingRepository(source: RapidBettingRemoteDataSource): RapidBettingRepository = RapidBettingRepositoryImpl(source)

    @Provides @Singleton
    fun favoriteBetRepository(source: FavoriteBetRemoteDataSource): FavoriteBetRepository = FavoriteBetRepositoryImpl(source)

    @Provides
    fun bettingTutorialDismissed(
        repository: OnboardingRepository,
    ): br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetBettingTutorialDismissedUseCase =
        br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetBettingTutorialDismissed(repository)

    @Provides
    fun shakeEnabled(repository: ShakeSettingsRepository): br.gov.caixa.loterias.feature.betting.domain.usecase.GetShakeEnabledUseCase =
        br.gov.caixa.loterias.feature.betting.domain.usecase.GetShakeEnabled(repository)

    @Provides
    fun setBettingTutorialDismissed(repository: OnboardingRepository): SetBettingTutorialDismissedUseCase =
        SetBettingTutorialDismissed(repository)

    @Provides
    fun preparePoolEntry(verifyAccess: VerifyAccountAccessUseCase, onboarding: OnboardingRepository): PreparePoolEntryUseCase =
        PreparePoolEntry(verifyAccess, onboarding)

    @Provides
    fun prepareHomeBettingAction(
        verifyAccess: VerifyAccountAccessUseCase,
        features: GetHomeFeatureConfigurationUseCase,
        onboarding: OnboardingRepository,
        tokenStore: SessionTokenStore,
    ): PrepareHomeBettingActionUseCase = PrepareHomeBettingAction(verifyAccess, features, onboarding) {
        tokenStore.token() !=
            null
    }

    @Provides
    fun prepareBetEntry(
        verifyAccess: VerifyAccountAccessUseCase,
        parameters: GetBettingParametersUseCase,
        features: GetHomeFeatureConfigurationUseCase,
        clock: MonotonicServerClock,
        tokenStore: SessionTokenStore,
    ): PrepareBetEntryUseCase = PrepareBetEntry(verifyAccess, parameters, features, clock) {
        tokenStore.token() !=
            null
    }

    @Provides
    fun lotteryModalities(repository: BettingRepository): GetLotteryModalitiesUseCase = GetLotteryModalities(repository)

    @Provides
    fun bettingSpecialContests(configuration: AppConfigurationRepository): GetBettingSpecialContestsUseCase =
        GetBettingSpecialContests(configuration)

    @Provides
    fun bettingParameters(repository: BettingRepository): GetBettingParametersUseCase = GetBettingParameters(repository)

    @Provides
    fun betCombos(repository: BettingRepository): GetBetCombosUseCase = GetBetCombos(repository)

    @Provides
    fun addBetToCart(repository: BettingRepository): AddBetToCartUseCase = AddBetToCart(repository)

    @Provides
    fun addComboToCart(repository: BettingRepository): AddComboToCartUseCase = AddComboToCart(repository)

    @Provides
    fun rapidBetConfiguration(repository: RapidBettingRepository): GetRapidBetConfigurationUseCase = GetRapidBetConfiguration(repository)

    @Provides
    fun saveRapidBetConfiguration(repository: RapidBettingRepository): SaveRapidBetConfigurationUseCase =
        SaveRapidBetConfiguration(repository)

    @Provides
    fun generateRapidBets(repository: RapidBettingRepository): GenerateRapidBetsUseCase = GenerateRapidBets(repository)

    @Provides
    fun favoriteBets(repository: FavoriteBetRepository): GetFavoriteBetsUseCase = GetFavoriteBets(repository)

    @Provides
    fun deleteFavoriteBet(repository: FavoriteBetRepository): DeleteFavoriteBetUseCase = DeleteFavoriteBet(repository)

    @Provides
    fun addFavoriteBetToCart(repository: FavoriteBetRepository): AddFavoriteBetToCartUseCase = AddFavoriteBetToCart(repository)

    @Provides
    fun validateFavoriteBet(repository: FavoriteBetRepository): ValidateFavoriteBetUseCase = ValidateFavoriteBet(repository)

    @Provides
    fun saveFavoriteBet(repository: FavoriteBetRepository): SaveFavoriteBetUseCase = SaveFavoriteBet(repository)

    @Provides @Singleton
    fun shakeSettings(@ApplicationContext context: Context, configuration: HostFeatureConfiguration): ShakeSettingsRepository =
        ShakeSettingsRepositoryImpl(
            context,
            environmentKey = {
                secureEnvironmentKey(
                    configuration.backendSessionNamespace,
                    configuration.backendApiBaseUrl
                )
            }
        )
}
