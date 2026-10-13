package br.gov.caixa.loterias.apostas.di

import android.content.Context
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.core.auth.AppAuthAuthenticationGateway
import br.gov.caixa.loterias.core.auth.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.OidcConfiguration
import br.gov.caixa.loterias.core.auth.SessionTokenStore
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.data.AppConfigurationRemoteDataSource
import br.gov.caixa.loterias.core.data.AccountRemoteDataSource
import br.gov.caixa.loterias.core.data.CartRemoteDataSource
import br.gov.caixa.loterias.core.data.FavoriteCartRemoteDataSource
import br.gov.caixa.loterias.core.data.BettingRemoteDataSource
import br.gov.caixa.loterias.core.data.MarketplaceRemoteDataSource
import br.gov.caixa.loterias.core.data.RemoteAppConfigurationRepository
import br.gov.caixa.loterias.core.data.RemoteAccountRepository
import br.gov.caixa.loterias.core.data.RemoteCartRepository
import br.gov.caixa.loterias.core.data.RemoteFavoriteCartRepository
import br.gov.caixa.loterias.core.data.RemoteBettingRepository
import br.gov.caixa.loterias.core.data.RemoteRapidBettingRepository
import br.gov.caixa.loterias.core.data.RapidBettingRemoteDataSource
import br.gov.caixa.loterias.core.data.RetrofitRapidBettingRemoteDataSource
import br.gov.caixa.loterias.core.data.FavoriteBetRemoteDataSource
import br.gov.caixa.loterias.core.data.RemoteFavoriteBetRepository
import br.gov.caixa.loterias.core.data.RetrofitFavoriteBetRemoteDataSource
import br.gov.caixa.loterias.core.data.PaymentRemoteDataSource
import br.gov.caixa.loterias.core.data.RemotePaymentRepository
import br.gov.caixa.loterias.core.data.RetrofitPaymentRemoteDataSource
import br.gov.caixa.loterias.core.data.RetailRemoteDataSource
import br.gov.caixa.loterias.core.data.PrizeRedemptionRemoteDataSource
import br.gov.caixa.loterias.core.data.RemoteRetailRepository
import br.gov.caixa.loterias.core.data.RetrofitRetailRemoteDataSource
import br.gov.caixa.loterias.core.data.RemoteMarketplaceRepository
import br.gov.caixa.loterias.core.data.RemoteResultsRepository
import br.gov.caixa.loterias.core.data.RemoteSupportRepository
import br.gov.caixa.loterias.core.data.RemoteResponsibleGamingRepository
import br.gov.caixa.loterias.core.data.ResponsibleGamingRemoteDataSource
import br.gov.caixa.loterias.core.data.ResultsRemoteDataSource
import br.gov.caixa.loterias.core.data.RetrofitLoteriasRemoteDataSource
import br.gov.caixa.loterias.core.data.SupportRemoteDataSource
import br.gov.caixa.loterias.core.domain.configuration.AppConfigurationRepository
import br.gov.caixa.loterias.core.domain.account.AccountRepository
import br.gov.caixa.loterias.core.domain.account.GetAccountProfile
import br.gov.caixa.loterias.core.domain.account.GetAccountProfileUseCase
import br.gov.caixa.loterias.core.domain.account.ResolveZipCode
import br.gov.caixa.loterias.core.domain.account.ResolveZipCodeUseCase
import br.gov.caixa.loterias.core.domain.account.UpdatePersonalData
import br.gov.caixa.loterias.core.domain.account.UpdatePersonalDataUseCase
import br.gov.caixa.loterias.core.domain.account.RegisterAccount
import br.gov.caixa.loterias.core.domain.account.RegisterAccountUseCase
import br.gov.caixa.loterias.core.domain.account.VerifyAccountAccess
import br.gov.caixa.loterias.core.domain.account.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.core.domain.account.RecalculateAuthorizedLimit
import br.gov.caixa.loterias.core.domain.account.RecalculateAuthorizedLimitUseCase
import br.gov.caixa.loterias.core.domain.cart.CartRepository
import br.gov.caixa.loterias.core.domain.cart.GetCurrentCart
import br.gov.caixa.loterias.core.domain.cart.GetCurrentCartUseCase
import br.gov.caixa.loterias.core.domain.cart.RemoveCartItem
import br.gov.caixa.loterias.core.domain.cart.RemoveCartItemUseCase
import br.gov.caixa.loterias.core.domain.cart.ClearCart
import br.gov.caixa.loterias.core.domain.cart.ClearCartUseCase
import br.gov.caixa.loterias.core.domain.cart.ValidateCart
import br.gov.caixa.loterias.core.domain.cart.ValidateCartUseCase
import br.gov.caixa.loterias.core.domain.cart.ValidateFavoriteCartName
import br.gov.caixa.loterias.core.domain.cart.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.core.domain.cart.SaveCurrentCartFavorite
import br.gov.caixa.loterias.core.domain.cart.SaveCurrentCartFavoriteUseCase
import br.gov.caixa.loterias.core.domain.cart.FavoriteCartRepository
import br.gov.caixa.loterias.core.domain.cart.GetFavoriteCarts
import br.gov.caixa.loterias.core.domain.cart.GetFavoriteCartsUseCase
import br.gov.caixa.loterias.core.domain.cart.GetFavoriteCartBets
import br.gov.caixa.loterias.core.domain.cart.GetFavoriteCartBetsUseCase
import br.gov.caixa.loterias.core.domain.cart.DeleteFavoriteCart
import br.gov.caixa.loterias.core.domain.cart.DeleteFavoriteCartUseCase
import br.gov.caixa.loterias.core.domain.cart.DeleteFavoriteCartBet
import br.gov.caixa.loterias.core.domain.cart.DeleteFavoriteCartBetUseCase
import br.gov.caixa.loterias.core.domain.cart.AddFavoriteCartToCurrent
import br.gov.caixa.loterias.core.domain.cart.AddFavoriteCartToCurrentUseCase
import br.gov.caixa.loterias.core.domain.cart.FavoriteCartUsageRepository
import br.gov.caixa.loterias.core.domain.cart.MarkFavoriteCartAdded
import br.gov.caixa.loterias.core.domain.cart.MarkFavoriteCartAddedUseCase
import br.gov.caixa.loterias.core.domain.cart.WasFavoriteCartAdded
import br.gov.caixa.loterias.core.domain.cart.WasFavoriteCartAddedUseCase
import br.gov.caixa.loterias.core.domain.betting.BettingRepository
import br.gov.caixa.loterias.core.domain.betting.RapidBettingRepository
import br.gov.caixa.loterias.core.domain.betting.GetRapidBetConfiguration
import br.gov.caixa.loterias.core.domain.betting.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.core.domain.betting.SaveRapidBetConfiguration
import br.gov.caixa.loterias.core.domain.betting.SaveRapidBetConfigurationUseCase
import br.gov.caixa.loterias.core.domain.betting.GenerateRapidBets
import br.gov.caixa.loterias.core.domain.betting.GenerateRapidBetsUseCase
import br.gov.caixa.loterias.core.domain.betting.FavoriteBetRepository
import br.gov.caixa.loterias.core.domain.betting.GetFavoriteBets
import br.gov.caixa.loterias.core.domain.betting.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.core.domain.betting.DeleteFavoriteBet
import br.gov.caixa.loterias.core.domain.betting.DeleteFavoriteBetUseCase
import br.gov.caixa.loterias.core.domain.betting.AddFavoriteBetToCart
import br.gov.caixa.loterias.core.domain.betting.AddFavoriteBetToCartUseCase
import br.gov.caixa.loterias.core.domain.betting.ValidateFavoriteBet
import br.gov.caixa.loterias.core.domain.betting.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.core.domain.betting.SaveFavoriteBet
import br.gov.caixa.loterias.core.domain.betting.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.core.domain.payment.PaymentRepository
import br.gov.caixa.loterias.core.domain.payment.GetPaymentMethods
import br.gov.caixa.loterias.core.domain.payment.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.core.domain.payment.GetSavedCards
import br.gov.caixa.loterias.core.domain.payment.GetSavedCardsUseCase
import br.gov.caixa.loterias.core.domain.payment.DeleteSavedCard
import br.gov.caixa.loterias.core.domain.payment.DeleteSavedCardUseCase
import br.gov.caixa.loterias.core.domain.payment.GeneratePixCharge
import br.gov.caixa.loterias.core.domain.payment.GeneratePixChargeUseCase
import br.gov.caixa.loterias.core.domain.payment.GetPurchaseProgress
import br.gov.caixa.loterias.core.domain.payment.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.core.domain.payment.ValidateDam
import br.gov.caixa.loterias.core.domain.payment.ValidateDamUseCase
import br.gov.caixa.loterias.core.domain.payment.SubmitCardPayment
import br.gov.caixa.loterias.core.domain.payment.SubmitCardPaymentUseCase
import br.gov.caixa.loterias.core.domain.betting.GetLotteryModalities
import br.gov.caixa.loterias.core.domain.betting.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.core.domain.betting.GetBettingParameters
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.GetBetCombos
import br.gov.caixa.loterias.core.domain.betting.GetBetCombosUseCase
import br.gov.caixa.loterias.core.domain.betting.AddBetToCart
import br.gov.caixa.loterias.core.domain.betting.AddBetToCartUseCase
import br.gov.caixa.loterias.core.domain.betting.AddComboToCart
import br.gov.caixa.loterias.core.domain.betting.AddComboToCartUseCase
import br.gov.caixa.loterias.core.auth.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.auth.JwtCurrentUserIdentityProvider
import br.gov.caixa.loterias.core.domain.configuration.GetMarketplaceFilterParameters
import br.gov.caixa.loterias.core.domain.configuration.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.domain.configuration.GetHomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.domain.configuration.IsVersionSupported
import br.gov.caixa.loterias.core.domain.configuration.IsVersionSupportedUseCase
import br.gov.caixa.loterias.core.domain.onboarding.CompleteIntroduction
import br.gov.caixa.loterias.core.domain.onboarding.CompleteIntroductionUseCase
import br.gov.caixa.loterias.core.domain.onboarding.OnboardingRepository
import br.gov.caixa.loterias.core.domain.onboarding.ShouldShowIntroduction
import br.gov.caixa.loterias.core.domain.onboarding.ShouldShowIntroductionUseCase
import br.gov.caixa.loterias.core.domain.onboarding.SetBettingTutorialDismissed
import br.gov.caixa.loterias.core.domain.onboarding.SetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.core.preferences.SharedPreferencesOnboardingRepository
import br.gov.caixa.loterias.core.preferences.SharedPreferencesFavoriteCartUsageRepository
import br.gov.caixa.loterias.core.domain.marketplace.GetMarketplaceDetail
import br.gov.caixa.loterias.core.domain.marketplace.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.core.domain.marketplace.AddMarketplacePoolToCart
import br.gov.caixa.loterias.core.domain.marketplace.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceDetailRepository
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePurchaseRepository
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceRepository
import br.gov.caixa.loterias.core.domain.marketplace.SearchMarketplace
import br.gov.caixa.loterias.core.domain.marketplace.SearchMarketplaceUseCase
import br.gov.caixa.loterias.core.domain.results.GetLatestResult
import br.gov.caixa.loterias.core.domain.results.GetLatestResultUseCase
import br.gov.caixa.loterias.core.domain.results.GetResultByContest
import br.gov.caixa.loterias.core.domain.results.GetResultByContestUseCase
import br.gov.caixa.loterias.core.domain.results.ResultsRepository
import br.gov.caixa.loterias.core.domain.support.AcceptTerms
import br.gov.caixa.loterias.core.domain.support.AcceptTermsUseCase
import br.gov.caixa.loterias.core.domain.support.GetSupportQuestions
import br.gov.caixa.loterias.core.domain.support.GetSupportQuestionsUseCase
import br.gov.caixa.loterias.core.domain.support.GetSupportSections
import br.gov.caixa.loterias.core.domain.support.GetSupportSectionsUseCase
import br.gov.caixa.loterias.core.domain.support.GetTerms
import br.gov.caixa.loterias.core.domain.support.GetTermsUseCase
import br.gov.caixa.loterias.core.domain.support.SupportRepository
import br.gov.caixa.loterias.core.domain.responsiblegaming.GetSelfAssessmentQuestions
import br.gov.caixa.loterias.core.domain.responsiblegaming.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.GetSocialTransfers
import br.gov.caixa.loterias.core.domain.responsiblegaming.GetSocialTransfersUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.ResponsibleGamingRepository
import br.gov.caixa.loterias.core.domain.responsiblegaming.SubmitSelfAssessment
import br.gov.caixa.loterias.core.domain.responsiblegaming.SubmitSelfAssessmentUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.SuspendAccount
import br.gov.caixa.loterias.core.domain.responsiblegaming.SuspendAccountUseCase
import br.gov.caixa.loterias.core.network.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.LegacyBackendApiProvider
import br.gov.caixa.loterias.core.network.BetsHistoryApiProvider
import br.gov.caixa.loterias.core.network.PaymentApiProvider
import br.gov.caixa.loterias.core.data.RetrofitTicketsRemoteDataSource
import br.gov.caixa.loterias.core.data.TicketsRemoteDataSource
import br.gov.caixa.loterias.core.data.RemoteTicketsRepository
import br.gov.caixa.loterias.core.data.LocalNotificationRepository
import br.gov.caixa.loterias.core.domain.tickets.TicketsRepository
import br.gov.caixa.loterias.core.domain.tickets.GetConfirmedBets
import br.gov.caixa.loterias.core.domain.tickets.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.core.domain.tickets.RecoverConfirmedBet
import br.gov.caixa.loterias.core.domain.tickets.RecoverConfirmedBetUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchases
import br.gov.caixa.loterias.core.domain.tickets.GetPurchasesUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseBets
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseDetails
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.core.domain.tickets.SavePurchaseAsFavorite
import br.gov.caixa.loterias.core.domain.tickets.SavePurchaseAsFavoriteUseCase
import br.gov.caixa.loterias.core.domain.tickets.CheckTicket
import br.gov.caixa.loterias.core.domain.tickets.CheckTicketUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetConfirmedBetsConfiguration
import br.gov.caixa.loterias.core.domain.tickets.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseMonths
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchasePaymentMethods
import br.gov.caixa.loterias.core.domain.tickets.GetPurchasePaymentMethodsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseStatuses
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseStatusesUseCase
import br.gov.caixa.loterias.core.domain.retail.RetailRepository
import br.gov.caixa.loterias.core.domain.retail.SearchRetailers
import br.gov.caixa.loterias.core.domain.retail.SearchRetailersUseCase
import br.gov.caixa.loterias.core.domain.retail.GetFavoriteRetailers
import br.gov.caixa.loterias.core.domain.retail.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.core.domain.retail.SetRetailerFavorite
import br.gov.caixa.loterias.core.domain.retail.SetRetailerFavoriteUseCase
import br.gov.caixa.loterias.core.domain.retail.PrizeRedemptionRepository
import br.gov.caixa.loterias.core.domain.retail.GetPrizeDetails
import br.gov.caixa.loterias.core.domain.retail.GetPrizeDetailsUseCase
import br.gov.caixa.loterias.core.domain.retail.GenerateLotteryRedemptionCode
import br.gov.caixa.loterias.core.domain.retail.GenerateLotteryRedemptionCodeUseCase
import br.gov.caixa.loterias.core.domain.retail.ConsultPixRedemptionKey
import br.gov.caixa.loterias.core.domain.retail.ConsultPixRedemptionKeyUseCase
import br.gov.caixa.loterias.core.domain.retail.RedeemPrize
import br.gov.caixa.loterias.core.domain.retail.RedeemPrizeUseCase
import br.gov.caixa.loterias.core.domain.retail.CancelPixRedemption
import br.gov.caixa.loterias.core.domain.retail.CancelPixRedemptionUseCase
import br.gov.caixa.loterias.core.domain.retail.DownloadBetReceipt
import br.gov.caixa.loterias.core.domain.retail.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.core.domain.retail.DownloadPrizeReceipt
import br.gov.caixa.loterias.core.domain.retail.DownloadPrizeReceiptUseCase
import br.gov.caixa.loterias.core.domain.notifications.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    fun verifyCartPaymentAccount(
        identity: CurrentUserIdentityProvider,
        verifyAccess: VerifyAccountAccessUseCase,
    ): br.gov.caixa.loterias.core.domain.cart.VerifyCartPaymentAccountUseCase =
        br.gov.caixa.loterias.core.domain.cart.VerifyCartPaymentAccount(
            isAuthenticated = { identity.currentUser() != null }, verifyAccess = verifyAccess,
        )

    @Provides @Singleton
    fun networkAvailability(@ApplicationContext context: Context): br.gov.caixa.loterias.core.common.NetworkAvailability =
        br.gov.caixa.loterias.core.common.ReadNetworkAvailability {
            context.getSystemService(android.net.ConnectivityManager::class.java)?.let {
                it.activeNetwork != null
            }
        }

    @Provides @Singleton
    fun tokenStore() = SessionTokenStore()

    @Provides @Singleton
    fun serverClock() = br.gov.caixa.loterias.core.common.MonotonicServerClock(
        elapsedMillis = { android.os.SystemClock.elapsedRealtime() },
    )

    @Provides @Singleton
    fun localClock(): java.time.Clock = java.time.Clock.systemDefaultZone()

    @Provides @Singleton
    fun apiProvider() = LoteriasApiProvider()

    @Provides @Singleton
    fun legacyApiProvider() = LegacyBackendApiProvider()

    @Provides @Singleton
    fun betsHistoryApiProvider() = BetsHistoryApiProvider()

    @Provides @Singleton
    fun paymentApiProvider() = PaymentApiProvider()

    @Provides @Singleton
    fun purchaseApiProvider() = br.gov.caixa.loterias.core.network.PurchaseBackendApiProvider()

    @Provides @Singleton
    fun prizeApiProvider() = br.gov.caixa.loterias.core.network.PrizeBackendApiProvider()

    @Provides @Singleton
    fun accountVerificationProvider() = br.gov.caixa.loterias.core.network.AccountVerificationBackendProvider()

    @Provides @Singleton
    fun notificationReadProvider() = br.gov.caixa.loterias.core.network.NotificationReadApiProvider()

    @Provides
    fun unreadNotificationRepository(provider: br.gov.caixa.loterias.core.network.NotificationReadApiProvider,
        sessions: br.gov.caixa.loterias.core.data.AccountVerificationSessionBinding): br.gov.caixa.loterias.core.domain.account.UnreadNotificationCountRepository =
        br.gov.caixa.loterias.core.data.RemoteUnreadNotificationCountRepository(provider, sessions)

    @Provides
    fun unreadNotificationCount(repository: br.gov.caixa.loterias.core.domain.account.UnreadNotificationCountRepository,
        sessions: br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentityProvider) =
        br.gov.caixa.loterias.core.domain.account.GetUnreadNotificationCountUseCase(repository, sessions)

    @Provides
    fun latestNotificationSession(tokenStore: SessionTokenStore): br.gov.caixa.loterias.core.domain.account.LatestNotificationSession = tokenStore

    @Provides @Singleton
    fun latestNotificationReadDataSource(provider: br.gov.caixa.loterias.core.network.NotificationReadApiProvider,
        sessions: br.gov.caixa.loterias.core.data.AccountVerificationSessionBinding): br.gov.caixa.loterias.core.data.LatestNotificationReadRemoteDataSource =
        br.gov.caixa.loterias.core.data.RetrofitLatestNotificationReadRemoteDataSource(provider, sessions)

    @Provides @Singleton
    fun latestNotificationReadRepository(remote: br.gov.caixa.loterias.core.data.LatestNotificationReadRemoteDataSource): br.gov.caixa.loterias.core.domain.account.LatestNotificationReadRepository =
        br.gov.caixa.loterias.core.data.RemoteLatestNotificationReadRepository(remote)

    @Provides
    fun readLatestNotification(session: br.gov.caixa.loterias.core.domain.account.LatestNotificationSession,
        remote: br.gov.caixa.loterias.core.domain.account.LatestNotificationReadRepository): br.gov.caixa.loterias.core.domain.account.ReadLatestNotificationUseCase =
        br.gov.caixa.loterias.core.domain.account.ReadLatestNotification(session, remote)

    @Provides
    fun accountVerificationSessionBinding(tokenStore: SessionTokenStore): br.gov.caixa.loterias.core.data.AccountVerificationSessionBinding =
        br.gov.caixa.loterias.core.data.AccountVerificationSessionBinding { identity ->
            br.gov.caixa.loterias.core.network.AccountVerificationSession(
                boundToken = { tokenStore.tokenFor(identity) },
                current = { tokenStore.isCurrent(identity) },
            )
        }

    @Provides @Singleton
    fun postLoginAccountDataSource(
        provider: br.gov.caixa.loterias.core.network.AccountVerificationBackendProvider,
        sessions: br.gov.caixa.loterias.core.data.AccountVerificationSessionBinding,
    ) = br.gov.caixa.loterias.core.data.RetrofitPostLoginAccountDataSource(provider, sessions)

    @Provides @Singleton
    fun onboardingRepository(@ApplicationContext context: Context): OnboardingRepository =
        SharedPreferencesOnboardingRepository.fromContext(context)

    @Provides @Singleton
    fun favoriteCartUsageRepository(@ApplicationContext context: Context): FavoriteCartUsageRepository =
        SharedPreferencesFavoriteCartUsageRepository.fromContext(context)

    @Provides @Singleton
    fun remoteDataSource(
        provider: LoteriasApiProvider,
        legacyProvider: LegacyBackendApiProvider,
    ) = RetrofitLoteriasRemoteDataSource(provider, legacyProvider)

    @Provides
    fun configurationDataSource(source: RetrofitLoteriasRemoteDataSource): AppConfigurationRemoteDataSource = source

    @Provides
    fun marketplaceDataSource(source: RetrofitLoteriasRemoteDataSource): MarketplaceRemoteDataSource = source

    @Provides
    fun resultsDataSource(source: RetrofitLoteriasRemoteDataSource): ResultsRemoteDataSource = source

    @Provides
    fun supportDataSource(source: RetrofitLoteriasRemoteDataSource): SupportRemoteDataSource = source

    @Provides
    fun responsibleGamingDataSource(source: RetrofitLoteriasRemoteDataSource): ResponsibleGamingRemoteDataSource = source

    @Provides
    fun accountDataSource(source: RetrofitLoteriasRemoteDataSource): AccountRemoteDataSource = source

    @Provides
    fun cartDataSource(source: RetrofitLoteriasRemoteDataSource): CartRemoteDataSource = source

    @Provides
    fun favoriteCartDataSource(source: RetrofitLoteriasRemoteDataSource): FavoriteCartRemoteDataSource = source

    @Provides
    fun bettingDataSource(source: RetrofitLoteriasRemoteDataSource): BettingRemoteDataSource = source

    @Provides @Singleton
    fun rapidBettingDataSource(provider: LegacyBackendApiProvider): RapidBettingRemoteDataSource =
        RetrofitRapidBettingRemoteDataSource(provider)

    @Provides @Singleton
    fun favoriteBetDataSource(provider: LegacyBackendApiProvider): FavoriteBetRemoteDataSource =
        RetrofitFavoriteBetRemoteDataSource(provider)

    @Provides @Singleton
    fun paymentDataSource(
        provider: LegacyBackendApiProvider,
        loteriasApiProvider: LoteriasApiProvider,
        paymentApiProvider: PaymentApiProvider,
        purchaseApiProvider: br.gov.caixa.loterias.core.network.PurchaseBackendApiProvider,
    ): PaymentRemoteDataSource = RetrofitPaymentRemoteDataSource(provider, loteriasApiProvider, paymentApiProvider, purchaseApiProvider)

    @Provides @Singleton
    fun ticketsRemoteDataSource(
        provider: LegacyBackendApiProvider,
        betsHistoryApiProvider: BetsHistoryApiProvider,
        configurationSource: AppConfigurationRemoteDataSource,
        currentUserIdentityProvider: CurrentUserIdentityProvider,
    ): RetrofitTicketsRemoteDataSource = RetrofitTicketsRemoteDataSource(
        apiProvider = provider,
        betsHistoryApiProvider = betsHistoryApiProvider,
        configurationSource = configurationSource,
        currentUserCpfProvider = { currentUserIdentityProvider.currentUser()?.cpf.orEmpty() },
        production = BuildConfig.FLAVOR.equals("prd", ignoreCase = true),
    )

    @Provides
    fun ticketsDataSource(source: RetrofitTicketsRemoteDataSource): TicketsRemoteDataSource = source

    @Provides @Singleton
    fun retrofitRetailDataSource(
        provider: LegacyBackendApiProvider,
        prizeProvider: br.gov.caixa.loterias.core.network.PrizeBackendApiProvider,
    ) = RetrofitRetailRemoteDataSource(provider, prizeProvider)

    @Provides
    fun retailDataSource(source: RetrofitRetailRemoteDataSource): RetailRemoteDataSource = source

    @Provides
    fun prizeRedemptionDataSource(source: RetrofitRetailRemoteDataSource): PrizeRedemptionRemoteDataSource = source

    @Provides @Singleton
    fun configurationRepository(source: AppConfigurationRemoteDataSource): AppConfigurationRepository =
        RemoteAppConfigurationRepository(source)

    @Provides @Singleton
    fun marketplaceRepository(source: MarketplaceRemoteDataSource) = RemoteMarketplaceRepository(source)

    @Provides
    fun marketplaceContract(repository: RemoteMarketplaceRepository): MarketplaceRepository = repository

    @Provides
    fun marketplaceDetailContract(repository: RemoteMarketplaceRepository): MarketplaceDetailRepository = repository

    @Provides
    fun marketplacePurchaseContract(repository: RemoteMarketplaceRepository): MarketplacePurchaseRepository = repository

    @Provides @Singleton
    fun resultsRepository(source: ResultsRemoteDataSource): ResultsRepository = RemoteResultsRepository(source)

    @Provides @Singleton
    fun supportRepository(source: SupportRemoteDataSource): SupportRepository = RemoteSupportRepository(source)

    @Provides @Singleton
    fun responsibleGamingRepository(source: ResponsibleGamingRemoteDataSource): ResponsibleGamingRepository =
        RemoteResponsibleGamingRepository(source)

    @Provides @Singleton
    fun accountRepository(source: AccountRemoteDataSource): AccountRepository = RemoteAccountRepository(source)

    @Provides @Singleton
    fun cartRepository(source: CartRemoteDataSource,
        local: br.gov.caixa.loterias.core.domain.cart.VisitorCartRepository,
        tokenStore: SessionTokenStore): CartRepository =
        br.gov.caixa.loterias.core.domain.cart.SessionCartRepository(RemoteCartRepository(source), local) { tokenStore.token() != null }

    @Provides @Singleton
    fun favoriteCartRepository(source: FavoriteCartRemoteDataSource): FavoriteCartRepository =
        RemoteFavoriteCartRepository(source)

    @Provides @Singleton
    fun bettingRepository(source: BettingRemoteDataSource,
        local: br.gov.caixa.loterias.core.domain.cart.VisitorCartRepository,
        tokenStore: SessionTokenStore): BettingRepository =
        br.gov.caixa.loterias.core.domain.cart.SessionBettingRepository(RemoteBettingRepository(source), local) { tokenStore.token() != null }

    @Provides @Singleton
    fun rapidBettingRepository(source: RapidBettingRemoteDataSource): RapidBettingRepository =
        RemoteRapidBettingRepository(source)

    @Provides @Singleton
    fun favoriteBetRepository(source: FavoriteBetRemoteDataSource): FavoriteBetRepository =
        RemoteFavoriteBetRepository(source)

    @Provides @Singleton
    fun paymentRepository(source: PaymentRemoteDataSource): PaymentRepository = RemotePaymentRepository(source)

    @Provides @Singleton
    fun ticketsRepository(source: TicketsRemoteDataSource): TicketsRepository = RemoteTicketsRepository(source)

    @Provides
    fun recoverConfirmedBet(repository: TicketsRepository): RecoverConfirmedBetUseCase = RecoverConfirmedBet(repository)

    @Provides @Singleton
    fun retailRepository(
        source: RetailRemoteDataSource,
        prizeSource: PrizeRedemptionRemoteDataSource,
    ): RemoteRetailRepository = RemoteRetailRepository(source, prizeSource)

    @Provides fun retailContract(repository: RemoteRetailRepository): RetailRepository = repository
    @Provides fun prizeRedemptionContract(repository: RemoteRetailRepository): PrizeRedemptionRepository = repository
    @Provides fun mercadoPagoRegistrationContract(repository: RemoteRetailRepository): br.gov.caixa.loterias.core.domain.payment.MercadoPagoRegistrationRepository = repository
    @Provides fun prepareMercadoPagoRegistration(repository: br.gov.caixa.loterias.core.domain.payment.MercadoPagoRegistrationRepository): br.gov.caixa.loterias.core.domain.payment.PrepareMercadoPagoRegistrationUseCase =
        br.gov.caixa.loterias.core.domain.payment.PrepareMercadoPagoRegistration(repository)

    // Local sample inbox: fresh destination instance, no remote-read mirroring of synthetic IDs.
    @Provides
    fun notificationRepository(): NotificationRepository = LocalNotificationRepository()

    @Provides
    fun isVersionSupported(repository: AppConfigurationRepository): IsVersionSupportedUseCase =
        IsVersionSupported(repository)

    @Provides
    fun filterParameters(repository: AppConfigurationRepository): GetMarketplaceFilterParametersUseCase =
        GetMarketplaceFilterParameters(repository)

    @Provides
    fun homeFeatureConfiguration(repository: AppConfigurationRepository): GetHomeFeatureConfigurationUseCase =
        GetHomeFeatureConfiguration(repository)

    @Provides
    fun shouldShowIntroduction(repository: OnboardingRepository): ShouldShowIntroductionUseCase =
        ShouldShowIntroduction(repository)

    @Provides
    fun completeIntroduction(repository: OnboardingRepository): CompleteIntroductionUseCase =
        CompleteIntroduction(repository)

    @Provides
    fun setBettingTutorialDismissed(repository: OnboardingRepository): SetBettingTutorialDismissedUseCase =
        SetBettingTutorialDismissed(repository)

    @Provides
    fun preparePoolEntry(verifyAccess: VerifyAccountAccessUseCase, onboarding: OnboardingRepository):
        br.gov.caixa.loterias.core.domain.betting.PreparePoolEntryUseCase =
        br.gov.caixa.loterias.core.domain.betting.PreparePoolEntry(verifyAccess, onboarding)

    @Provides
    fun prepareHomeBettingAction(verifyAccess: VerifyAccountAccessUseCase,
        features: br.gov.caixa.loterias.core.domain.configuration.GetHomeFeatureConfigurationUseCase,
        onboarding: OnboardingRepository, tokenStore: SessionTokenStore): br.gov.caixa.loterias.core.domain.betting.PrepareHomeBettingActionUseCase =
        br.gov.caixa.loterias.core.domain.betting.PrepareHomeBettingAction(verifyAccess, features, onboarding) { tokenStore.token() != null }

    @Provides
    fun searchMarketplace(repository: MarketplaceRepository): SearchMarketplaceUseCase = SearchMarketplace(repository)

    @Provides
    fun marketplaceDetail(repository: MarketplaceDetailRepository): GetMarketplaceDetailUseCase =
        GetMarketplaceDetail(repository)

    @Provides
    fun addMarketplacePoolToCart(
        repository: MarketplacePurchaseRepository,
    ): AddMarketplacePoolToCartUseCase = AddMarketplacePoolToCart(repository)

    @Provides
    fun latestResult(repository: ResultsRepository): GetLatestResultUseCase = GetLatestResult(repository)

    @Provides
    fun resultByContest(repository: ResultsRepository): GetResultByContestUseCase = GetResultByContest(repository)

    @Provides
    fun supportSections(repository: SupportRepository): GetSupportSectionsUseCase = GetSupportSections(repository)

    @Provides
    fun supportQuestions(repository: SupportRepository): GetSupportQuestionsUseCase = GetSupportQuestions(repository)

    @Provides
    fun terms(repository: SupportRepository): GetTermsUseCase = GetTerms(repository)

    @Provides
    fun acceptTerms(repository: SupportRepository): AcceptTermsUseCase = AcceptTerms(repository)

    @Provides
    fun selfAssessmentQuestions(repository: ResponsibleGamingRepository): GetSelfAssessmentQuestionsUseCase =
        GetSelfAssessmentQuestions(repository)

    @Provides
    fun submitSelfAssessment(repository: ResponsibleGamingRepository): SubmitSelfAssessmentUseCase =
        SubmitSelfAssessment(repository)

    @Provides
    fun socialTransfers(repository: ResponsibleGamingRepository): GetSocialTransfersUseCase =
        GetSocialTransfers(repository)

    @Provides
    fun suspendAccount(repository: ResponsibleGamingRepository): SuspendAccountUseCase =
        SuspendAccount(repository)

    @Provides
    fun accountProfile(repository: AccountRepository): GetAccountProfileUseCase = GetAccountProfile(repository)

    @Provides
    fun resolveZipCode(repository: AccountRepository): ResolveZipCodeUseCase = ResolveZipCode(repository)

    @Provides
    fun updatePersonalData(repository: AccountRepository): UpdatePersonalDataUseCase = UpdatePersonalData(repository)

    @Provides
    fun registerAccount(repository: AccountRepository): RegisterAccountUseCase = RegisterAccount(repository)

    @Provides
    fun verifyAccountAccess(repository: AccountRepository): VerifyAccountAccessUseCase = VerifyAccountAccess(repository)

    @Provides @Singleton
    fun blockedAccountVerification(provider: LegacyBackendApiProvider):
        br.gov.caixa.loterias.core.domain.account.BlockedAccountVerificationRepository =
        br.gov.caixa.loterias.core.data.RemoteBlockedAccountVerificationRepository(provider)

    @Provides @Singleton
    fun verifyPostLoginAccess(
        blocked: br.gov.caixa.loterias.core.domain.account.BlockedAccountVerificationRepository,
        account: br.gov.caixa.loterias.core.domain.account.PostLoginAccountRepository,
        sessions: br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentityProvider,
    ): br.gov.caixa.loterias.core.domain.account.VerifyPostLoginAccessUseCase =
        br.gov.caixa.loterias.core.domain.account.VerifyPostLoginAccess(blocked, account, sessions)

    @Provides @Singleton
    fun postLoginAccountRepository(remote: br.gov.caixa.loterias.core.data.RetrofitPostLoginAccountDataSource,
        content: br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionContentRepository,
        transfer: br.gov.caixa.loterias.core.domain.cart.LegacyCartTransferRepository,
        sessions: br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentityProvider):
        br.gov.caixa.loterias.core.domain.account.PostLoginAccountRepository =
        br.gov.caixa.loterias.core.data.RemotePostLoginAccountRepository(remote, content, transfer, sessions::isCurrent)

    @Provides
    fun prepareBetEntry(
        verifyAccess: VerifyAccountAccessUseCase,
        parameters: br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase,
        features: br.gov.caixa.loterias.core.domain.configuration.GetHomeFeatureConfigurationUseCase,
        clock: br.gov.caixa.loterias.core.common.MonotonicServerClock,
        tokenStore: SessionTokenStore,
    ): br.gov.caixa.loterias.core.domain.betting.PrepareBetEntryUseCase =
        br.gov.caixa.loterias.core.domain.betting.PrepareBetEntry(verifyAccess, parameters, features, clock) { tokenStore.token() != null }

    @Provides
    fun recalculateAuthorizedLimit(repository: AccountRepository): RecalculateAuthorizedLimitUseCase =
        RecalculateAuthorizedLimit(repository)

    @Provides
    fun currentCart(repository: CartRepository): GetCurrentCartUseCase = GetCurrentCart(repository)

    @Provides
    fun removeCartItem(repository: CartRepository): RemoveCartItemUseCase = RemoveCartItem(repository)

    @Provides
    fun clearCart(repository: CartRepository): ClearCartUseCase = ClearCart(repository)

    @Provides
    fun validateCart(repository: CartRepository): ValidateCartUseCase = ValidateCart(repository)

    @Provides
    fun validateFavoriteCartName(repository: CartRepository): ValidateFavoriteCartNameUseCase =
        ValidateFavoriteCartName(repository)

    @Provides
    fun saveCurrentCartFavorite(repository: CartRepository): SaveCurrentCartFavoriteUseCase =
        SaveCurrentCartFavorite(repository)

    @Provides
    fun favoriteCarts(repository: FavoriteCartRepository): GetFavoriteCartsUseCase = GetFavoriteCarts(repository)

    @Provides
    fun favoriteCartBets(repository: FavoriteCartRepository): GetFavoriteCartBetsUseCase = GetFavoriteCartBets(repository)

    @Provides
    fun deleteFavoriteCart(repository: FavoriteCartRepository): DeleteFavoriteCartUseCase =
        DeleteFavoriteCart(repository)

    @Provides
    fun deleteFavoriteCartBet(repository: FavoriteCartRepository): DeleteFavoriteCartBetUseCase =
        DeleteFavoriteCartBet(repository)

    @Provides
    fun addFavoriteCartToCurrent(repository: FavoriteCartRepository): AddFavoriteCartToCurrentUseCase =
        AddFavoriteCartToCurrent(repository)

    @Provides
    fun wasFavoriteCartAdded(repository: FavoriteCartUsageRepository): WasFavoriteCartAddedUseCase =
        WasFavoriteCartAdded(repository)

    @Provides
    fun markFavoriteCartAdded(repository: FavoriteCartUsageRepository): MarkFavoriteCartAddedUseCase =
        MarkFavoriteCartAdded(repository)

    @Provides
    fun lotteryModalities(repository: BettingRepository): GetLotteryModalitiesUseCase = GetLotteryModalities(repository)

    @Provides
    fun bettingParameters(repository: BettingRepository): GetBettingParametersUseCase = GetBettingParameters(repository)

    @Provides
    fun betCombos(repository: BettingRepository): GetBetCombosUseCase = GetBetCombos(repository)

    @Provides
    fun addBetToCart(repository: BettingRepository): AddBetToCartUseCase = AddBetToCart(repository)

    @Provides
    fun addComboToCart(repository: BettingRepository): AddComboToCartUseCase = AddComboToCart(repository)

    @Provides
    fun rapidBetConfiguration(repository: RapidBettingRepository): GetRapidBetConfigurationUseCase =
        GetRapidBetConfiguration(repository)

    @Provides
    fun saveRapidBetConfiguration(repository: RapidBettingRepository): SaveRapidBetConfigurationUseCase =
        SaveRapidBetConfiguration(repository)

    @Provides
    fun generateRapidBets(repository: RapidBettingRepository): GenerateRapidBetsUseCase =
        GenerateRapidBets(repository)

    @Provides
    fun favoriteBets(repository: FavoriteBetRepository): GetFavoriteBetsUseCase = GetFavoriteBets(repository)

    @Provides
    fun deleteFavoriteBet(repository: FavoriteBetRepository): DeleteFavoriteBetUseCase = DeleteFavoriteBet(repository)

    @Provides
    fun addFavoriteBetToCart(repository: FavoriteBetRepository): AddFavoriteBetToCartUseCase =
        AddFavoriteBetToCart(repository)

    @Provides
    fun validateFavoriteBet(repository: FavoriteBetRepository): ValidateFavoriteBetUseCase =
        ValidateFavoriteBet(repository)

    @Provides
    fun saveFavoriteBet(repository: FavoriteBetRepository): SaveFavoriteBetUseCase =
        SaveFavoriteBet(repository)

    @Provides @Singleton
    fun recentPurchaseRepository(@ApplicationContext context: Context): br.gov.caixa.loterias.core.domain.cart.RecentPurchaseRepository =
        br.gov.caixa.loterias.core.preferences.LegacyRecentPurchaseRepository.fromContext(context)

    @Provides @Singleton
    fun checkoutLocationSource(@ApplicationContext context: Context, dispatchers: CoroutineDispatchers): br.gov.caixa.loterias.core.domain.cart.CheckoutLocationSource =
        br.gov.caixa.loterias.apostas.AndroidCheckoutLocationSource(context, dispatchers)

    @Provides
    fun checkCheckoutLocation(source: br.gov.caixa.loterias.core.domain.cart.CheckoutLocationSource): br.gov.caixa.loterias.core.domain.cart.CheckCheckoutLocationUseCase =
        br.gov.caixa.loterias.core.domain.cart.CheckCheckoutLocation(source)

    @Provides
    fun recordCheckoutLocationRequest(source: br.gov.caixa.loterias.core.domain.cart.CheckoutLocationSource): br.gov.caixa.loterias.core.domain.cart.RecordCheckoutLocationRequestUseCase =
        br.gov.caixa.loterias.core.domain.cart.RecordCheckoutLocationRequest(source)

    @Provides
    fun hasRecentPurchase(repository: br.gov.caixa.loterias.core.domain.cart.RecentPurchaseRepository): br.gov.caixa.loterias.core.domain.cart.HasRecentPurchaseUseCase =
        br.gov.caixa.loterias.core.domain.cart.HasRecentPurchase(repository, java.time.Clock.systemDefaultZone())

    @Provides
    fun recordRecentPurchase(repository: br.gov.caixa.loterias.core.domain.cart.RecentPurchaseRepository): br.gov.caixa.loterias.core.domain.cart.RecordRecentPurchaseUseCase =
        br.gov.caixa.loterias.core.domain.cart.RecordRecentPurchase(repository, java.time.Clock.systemDefaultZone())

    @Provides fun paymentMethods(repository: PaymentRepository): GetPaymentMethodsUseCase = GetPaymentMethods(repository)
    @Provides fun savedCards(repository: PaymentRepository): GetSavedCardsUseCase = GetSavedCards(repository)
    @Provides fun deleteSavedCard(repository: PaymentRepository): DeleteSavedCardUseCase = DeleteSavedCard(repository)
    @Provides fun generatePixCharge(repository: PaymentRepository): GeneratePixChargeUseCase = GeneratePixCharge(repository)
    @Provides fun purchaseProgress(repository: PaymentRepository): GetPurchaseProgressUseCase = GetPurchaseProgress(repository)
    @Provides fun validateDam(repository: PaymentRepository): ValidateDamUseCase = ValidateDam(repository)
    @Provides fun submitCardPayment(repository: PaymentRepository): SubmitCardPaymentUseCase = SubmitCardPayment(repository)

    @Provides
    fun confirmedBets(repository: TicketsRepository): GetConfirmedBetsUseCase = GetConfirmedBets(repository)

    @Provides
    fun purchases(repository: TicketsRepository): GetPurchasesUseCase = GetPurchases(repository)

    @Provides
    fun purchaseBets(repository: TicketsRepository): GetPurchaseBetsUseCase = GetPurchaseBets(repository)

    @Provides
    fun purchaseDetails(repository: TicketsRepository): GetPurchaseDetailsUseCase = GetPurchaseDetails(repository)

    @Provides
    fun savePurchaseAsFavorite(repository: TicketsRepository): SavePurchaseAsFavoriteUseCase =
        SavePurchaseAsFavorite(repository)

    @Provides
    fun checkTicket(repository: TicketsRepository): CheckTicketUseCase = CheckTicket(repository)

    @Provides
    fun confirmedBetsConfiguration(repository: TicketsRepository): GetConfirmedBetsConfigurationUseCase =
        GetConfirmedBetsConfiguration(repository)

    @Provides
    fun purchaseMonths(repository: TicketsRepository): GetPurchaseMonthsUseCase = GetPurchaseMonths(repository)

    @Provides
    fun purchasePaymentMethods(repository: TicketsRepository): GetPurchasePaymentMethodsUseCase =
        GetPurchasePaymentMethods(repository)

    @Provides
    fun purchaseStatuses(repository: TicketsRepository): GetPurchaseStatusesUseCase = GetPurchaseStatuses(repository)

    @Provides fun searchRetailers(repository: RetailRepository): SearchRetailersUseCase = SearchRetailers(repository)
    @Provides fun favoriteRetailers(repository: RetailRepository): GetFavoriteRetailersUseCase = GetFavoriteRetailers(repository)
    @Provides fun setRetailerFavorite(repository: RetailRepository): SetRetailerFavoriteUseCase = SetRetailerFavorite(repository)
    @Provides fun prizeDetails(repository: PrizeRedemptionRepository): GetPrizeDetailsUseCase = GetPrizeDetails(repository)
    @Provides fun lotteryRedemptionCode(repository: PrizeRedemptionRepository): GenerateLotteryRedemptionCodeUseCase =
        GenerateLotteryRedemptionCode(repository)
    @Provides fun consultPixRedemptionKey(repository: PrizeRedemptionRepository): ConsultPixRedemptionKeyUseCase =
        ConsultPixRedemptionKey(repository)
    @Provides fun redeemPrize(repository: PrizeRedemptionRepository): RedeemPrizeUseCase = RedeemPrize(repository)
    @Provides fun cancelPixRedemption(repository: PrizeRedemptionRepository): CancelPixRedemptionUseCase =
        CancelPixRedemption(repository)
    @Provides fun downloadBetReceipt(repository: PrizeRedemptionRepository): DownloadBetReceiptUseCase =
        DownloadBetReceipt(repository)
    @Provides fun downloadPrizeReceipt(repository: PrizeRedemptionRepository): DownloadPrizeReceiptUseCase =
        DownloadPrizeReceipt(repository)

    @Provides @Singleton
    fun currentUserIdentity(tokenStore: SessionTokenStore): CurrentUserIdentityProvider =
        JwtCurrentUserIdentityProvider(tokenStore)

    @Provides @Singleton
    fun appAuthGateway(
        @ApplicationContext context: Context,
        tokenStore: SessionTokenStore,
    ): AppAuthAuthenticationGateway = AppAuthAuthenticationGateway(
        context = context,
        configuration = OidcConfiguration(
            authorizationEndpoint = BuildConfig.SSO_AUTH_URL,
            tokenEndpoint = BuildConfig.SSO_TOKEN_URL,
            clientId = BuildConfig.SSO_CLIENT_ID,
            redirectUri = BuildConfig.SSO_REDIRECT_URI,
            endSessionEndpoint = BuildConfig.SSO_LOGOUT_URL,
            postLogoutRedirectUri = BuildConfig.SSO_REDIRECT_URI,
            sessionNamespace = BuildConfig.API_BASE_URL,
        ),
        tokenStore = tokenStore,
    )

    @Provides
    fun authentication(gateway: AppAuthAuthenticationGateway): AuthenticationGateway = gateway

    @Provides
    fun sessionIdentity(tokenStore: SessionTokenStore): br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentityProvider = tokenStore

    @Provides
    fun sessionContent(tokenStore: SessionTokenStore): br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionContentRepository = tokenStore

    @Provides
    fun observeSessionContent(repository: br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionContentRepository):
        br.gov.caixa.loterias.core.domain.account.ObserveAuthenticatedSessionContentUseCase =
        br.gov.caixa.loterias.core.domain.account.ObserveAuthenticatedSessionContent(repository)

    @Provides
    fun endSession(gateway: AppAuthAuthenticationGateway): br.gov.caixa.loterias.core.auth.EndSessionGateway = gateway

    @Provides @Singleton
    fun legacyLocalSessionData(@ApplicationContext context: Context): br.gov.caixa.loterias.core.preferences.LegacyLocalSessionDataRepository =
        br.gov.caixa.loterias.core.preferences.LegacyLocalSessionDataRepository.fromContext(context)

    @Provides @Singleton
    fun shakeSettings(@ApplicationContext context: Context): br.gov.caixa.loterias.core.domain.betting.ShakeSettingsRepository =
        br.gov.caixa.loterias.core.preferences.SharedPreferencesShakeSettings(context)

    @Provides @Singleton
    fun visitorCart(@ApplicationContext context: Context): br.gov.caixa.loterias.core.domain.cart.VisitorCartRepository =
        br.gov.caixa.loterias.core.preferences.SQLiteVisitorCartRepository(context)

    @Provides @Singleton
    fun legacyCartTransfer(@ApplicationContext context: Context): br.gov.caixa.loterias.core.domain.cart.LegacyCartTransferRepository =
        br.gov.caixa.loterias.core.preferences.LegacyCartTransferLocalRepository.fromContext(context)

    @Provides
    fun localSessionData(repository: br.gov.caixa.loterias.core.preferences.LegacyLocalSessionDataRepository):
        br.gov.caixa.loterias.core.domain.account.LocalSessionDataRepository = repository

    @Provides
    fun localSessionCleanupRecovery(repository: br.gov.caixa.loterias.core.preferences.LegacyLocalSessionDataRepository):
        br.gov.caixa.loterias.core.domain.account.LocalSessionCleanupRecoveryRepository = repository

    @Provides
    fun resumeLocalSessionCleanup(repository: br.gov.caixa.loterias.core.domain.account.LocalSessionCleanupRecoveryRepository):
        br.gov.caixa.loterias.core.domain.account.ResumeLocalSessionCleanupUseCase =
        br.gov.caixa.loterias.core.domain.account.ResumeLocalSessionCleanup(repository)

    @Provides
    fun clearLocalSessionData(repository: br.gov.caixa.loterias.core.domain.account.LocalSessionDataRepository):
        br.gov.caixa.loterias.core.domain.account.ClearLocalSessionDataUseCase =
        br.gov.caixa.loterias.core.domain.account.ClearLocalSessionData(repository)

    @Provides @Singleton
    fun dispatchers(): CoroutineDispatchers = DefaultCoroutineDispatchers
}
