package br.gov.caixa.loterias.apostas

import okhttp3.HttpUrl.Companion.toHttpUrl
import br.gov.caixa.loterias.core.network.CloudBackendRouting
import okhttp3.OkHttpClient
import android.content.Context
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import br.gov.caixa.loterias.core.auth.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.SessionTokenStore
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.domain.configuration.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.domain.configuration.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.domain.onboarding.CompleteIntroductionUseCase
import br.gov.caixa.loterias.core.domain.onboarding.ShouldShowIntroductionUseCase
import br.gov.caixa.loterias.core.domain.onboarding.SetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.core.domain.account.GetAccountProfileUseCase
import br.gov.caixa.loterias.core.domain.account.ResolveZipCodeUseCase
import br.gov.caixa.loterias.core.domain.account.UpdatePersonalDataUseCase
import br.gov.caixa.loterias.core.domain.account.RegisterAccountUseCase
import br.gov.caixa.loterias.core.domain.account.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.core.domain.account.RecalculateAuthorizedLimitUseCase
import br.gov.caixa.loterias.core.domain.cart.GetCurrentCartUseCase
import br.gov.caixa.loterias.core.domain.cart.RemoveCartItemUseCase
import br.gov.caixa.loterias.core.domain.cart.ClearCartUseCase
import br.gov.caixa.loterias.core.domain.cart.ValidateCartUseCase
import br.gov.caixa.loterias.core.domain.cart.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.core.domain.cart.SaveCurrentCartFavoriteUseCase
import br.gov.caixa.loterias.core.domain.cart.GetFavoriteCartsUseCase
import br.gov.caixa.loterias.core.domain.cart.GetFavoriteCartBetsUseCase
import br.gov.caixa.loterias.core.domain.cart.DeleteFavoriteCartUseCase
import br.gov.caixa.loterias.core.domain.cart.DeleteFavoriteCartBetUseCase
import br.gov.caixa.loterias.core.domain.cart.AddFavoriteCartToCurrentUseCase
import br.gov.caixa.loterias.core.domain.cart.MarkFavoriteCartAddedUseCase
import br.gov.caixa.loterias.core.domain.cart.WasFavoriteCartAddedUseCase
import br.gov.caixa.loterias.core.domain.betting.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.GetBetCombosUseCase
import br.gov.caixa.loterias.core.domain.betting.AddBetToCartUseCase
import br.gov.caixa.loterias.core.domain.betting.AddComboToCartUseCase
import br.gov.caixa.loterias.core.domain.betting.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.core.domain.betting.SaveRapidBetConfigurationUseCase
import br.gov.caixa.loterias.core.domain.betting.GenerateRapidBetsUseCase
import br.gov.caixa.loterias.core.domain.betting.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.core.domain.betting.DeleteFavoriteBetUseCase
import br.gov.caixa.loterias.core.domain.betting.AddFavoriteBetToCartUseCase
import br.gov.caixa.loterias.core.domain.betting.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.core.domain.betting.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.core.domain.payment.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.core.domain.payment.GetSavedCardsUseCase
import br.gov.caixa.loterias.core.domain.payment.DeleteSavedCardUseCase
import br.gov.caixa.loterias.core.domain.payment.GeneratePixChargeUseCase
import br.gov.caixa.loterias.core.domain.payment.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.core.domain.payment.ValidateDamUseCase
import br.gov.caixa.loterias.core.domain.payment.SubmitCardPaymentUseCase
import br.gov.caixa.loterias.core.auth.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.domain.configuration.IsVersionSupportedUseCase
import br.gov.caixa.loterias.core.domain.marketplace.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.core.domain.marketplace.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.core.domain.marketplace.SearchMarketplaceUseCase
import br.gov.caixa.loterias.core.network.NetworkClientFactory
import br.gov.caixa.loterias.core.network.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.NetworkLogSink
import br.gov.caixa.loterias.core.network.MockBackendOptions
import br.gov.caixa.loterias.core.network.MockBackendScenario
import br.gov.caixa.loterias.core.network.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.LegacyBackendApiProvider
import br.gov.caixa.loterias.core.network.BetsHistoryApiProvider
import br.gov.caixa.loterias.core.network.PaymentApiProvider
import br.gov.caixa.loterias.core.network.PaymentApis
import br.gov.caixa.loterias.core.keymanagement.HostPublicKeyPins
import br.gov.caixa.loterias.core.keymanagement.PublicKeySet
import br.gov.caixa.loterias.core.keymanagement.PublicKeySetRepository
import br.gov.caixa.loterias.core.keymanagement.SignedKeySetVerifier
import br.gov.caixa.loterias.core.keymanagement.TrustedConfigurationSigners
import br.gov.caixa.loterias.core.domain.results.GetLatestResultUseCase
import br.gov.caixa.loterias.core.domain.results.GetResultByContestUseCase
import br.gov.caixa.loterias.core.domain.support.AcceptTermsUseCase
import br.gov.caixa.loterias.core.domain.support.GetSupportQuestionsUseCase
import br.gov.caixa.loterias.core.domain.support.GetSupportSectionsUseCase
import br.gov.caixa.loterias.core.domain.support.GetTermsUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.GetSocialTransfersUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.SubmitSelfAssessmentUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.SuspendAccountUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.core.domain.tickets.RecoverConfirmedBetUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchasesUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.core.domain.tickets.SavePurchaseAsFavoriteUseCase
import br.gov.caixa.loterias.core.domain.tickets.CheckTicketUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchasePaymentMethodsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseStatusesUseCase
import br.gov.caixa.loterias.core.domain.retail.SearchRetailersUseCase
import br.gov.caixa.loterias.core.domain.retail.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.core.domain.retail.SetRetailerFavoriteUseCase
import br.gov.caixa.loterias.core.domain.retail.GetPrizeDetailsUseCase
import br.gov.caixa.loterias.core.domain.retail.GenerateLotteryRedemptionCodeUseCase
import br.gov.caixa.loterias.core.domain.retail.ConsultPixRedemptionKeyUseCase
import br.gov.caixa.loterias.core.domain.retail.RedeemPrizeUseCase
import br.gov.caixa.loterias.core.domain.retail.CancelPixRedemptionUseCase
import br.gov.caixa.loterias.core.domain.retail.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.core.domain.retail.DownloadPrizeReceiptUseCase

@Singleton
class AppContainer @Inject constructor(
    @ApplicationContext context: Context,
    val networkAvailability: br.gov.caixa.loterias.core.common.NetworkAvailability,
    private val tokenStore: SessionTokenStore,
    private val serverClock: br.gov.caixa.loterias.core.common.MonotonicServerClock,
    val localClock: java.time.Clock,
    val prepareMercadoPagoRegistration: br.gov.caixa.loterias.core.domain.payment.PrepareMercadoPagoRegistrationUseCase,
    private val apiProvider: LoteriasApiProvider,
    private val legacyApiProvider: LegacyBackendApiProvider,
    private val betsHistoryApiProvider: BetsHistoryApiProvider,
    private val paymentApiProvider: PaymentApiProvider,
    private val purchaseApiProvider: br.gov.caixa.loterias.core.network.PurchaseBackendApiProvider,
    private val prizeApiProvider: br.gov.caixa.loterias.core.network.PrizeBackendApiProvider,
    private val accountVerificationProvider: br.gov.caixa.loterias.core.network.AccountVerificationBackendProvider,
    private val notificationReadProvider: br.gov.caixa.loterias.core.network.NotificationReadApiProvider,
    val authentication: AuthenticationGateway,
    val endSession: br.gov.caixa.loterias.core.auth.EndSessionGateway,
    val clearLocalSessionData: br.gov.caixa.loterias.core.domain.account.ClearLocalSessionDataUseCase,
    val resumeLocalSessionCleanup: br.gov.caixa.loterias.core.domain.account.ResumeLocalSessionCleanupUseCase,
    val isVersionSupported: IsVersionSupportedUseCase,
    val marketplaceFilterParameters: GetMarketplaceFilterParametersUseCase,
    val homeFeatureConfiguration: GetHomeFeatureConfigurationUseCase,
    val shouldShowIntroduction: ShouldShowIntroductionUseCase,
    val completeIntroduction: CompleteIntroductionUseCase,
    val authenticatedSessionIdentity: br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentityProvider,
    val authenticatedSessionContent: br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionContentRepository,
    val legacyCartTransfer: br.gov.caixa.loterias.core.domain.cart.LegacyCartTransferRepository,
    val visitorCart: br.gov.caixa.loterias.core.domain.cart.VisitorCartRepository,
    val shakeSettings: br.gov.caixa.loterias.core.domain.betting.ShakeSettingsRepository,
    val setBettingTutorialDismissed: SetBettingTutorialDismissedUseCase,
    val searchMarketplace: SearchMarketplaceUseCase,
    val marketplaceDetail: GetMarketplaceDetailUseCase,
    val addMarketplacePoolToCart: AddMarketplacePoolToCartUseCase,
    val latestResult: GetLatestResultUseCase,
    val resultByContest: GetResultByContestUseCase,
    val supportSections: GetSupportSectionsUseCase,
    val supportQuestions: GetSupportQuestionsUseCase,
    val terms: GetTermsUseCase,
    val acceptTerms: AcceptTermsUseCase,
    val selfAssessmentQuestions: GetSelfAssessmentQuestionsUseCase,
    val submitSelfAssessment: SubmitSelfAssessmentUseCase,
    val socialTransfers: GetSocialTransfersUseCase,
    val suspendAccount: SuspendAccountUseCase,
    val accountProfile: GetAccountProfileUseCase,
    val resolveZipCode: ResolveZipCodeUseCase,
    val updatePersonalData: UpdatePersonalDataUseCase,
    val registerAccount: RegisterAccountUseCase,
    val verifyAccountAccess: VerifyAccountAccessUseCase,
    val verifyPostLoginAccess: br.gov.caixa.loterias.core.domain.account.VerifyPostLoginAccessUseCase,
    val observeSessionContent: br.gov.caixa.loterias.core.domain.account.ObserveAuthenticatedSessionContentUseCase,
    val readLatestNotification: br.gov.caixa.loterias.core.domain.account.ReadLatestNotificationUseCase,
    val unreadNotificationCount: br.gov.caixa.loterias.core.domain.account.GetUnreadNotificationCountUseCase,
    val prepareBetEntry: br.gov.caixa.loterias.core.domain.betting.PrepareBetEntryUseCase,
    val preparePoolEntry: br.gov.caixa.loterias.core.domain.betting.PreparePoolEntryUseCase,
    val prepareHomeBettingAction: br.gov.caixa.loterias.core.domain.betting.PrepareHomeBettingActionUseCase,
    val recalculateAuthorizedLimit: RecalculateAuthorizedLimitUseCase,
    val currentCart: GetCurrentCartUseCase,
    val removeCartItem: RemoveCartItemUseCase,
    val clearCart: ClearCartUseCase,
    val validateCart: ValidateCartUseCase,
    val verifyCartPaymentAccount: br.gov.caixa.loterias.core.domain.cart.VerifyCartPaymentAccountUseCase,
    val validateFavoriteCartName: ValidateFavoriteCartNameUseCase,
    val saveCurrentCartFavorite: SaveCurrentCartFavoriteUseCase,
    val favoriteCarts: GetFavoriteCartsUseCase,
    val favoriteCartBets: GetFavoriteCartBetsUseCase,
    val deleteFavoriteCart: DeleteFavoriteCartUseCase,
    val deleteFavoriteCartBet: DeleteFavoriteCartBetUseCase,
    val addFavoriteCartToCurrent: AddFavoriteCartToCurrentUseCase,
    val wasFavoriteCartAdded: WasFavoriteCartAddedUseCase,
    val markFavoriteCartAdded: MarkFavoriteCartAddedUseCase,
    val lotteryModalities: GetLotteryModalitiesUseCase,
    val bettingParameters: GetBettingParametersUseCase,
    val betCombos: GetBetCombosUseCase,
    val addBetToCart: AddBetToCartUseCase,
    val addComboToCart: AddComboToCartUseCase,
    val rapidBetConfiguration: GetRapidBetConfigurationUseCase,
    val saveRapidBetConfiguration: SaveRapidBetConfigurationUseCase,
    val generateRapidBets: GenerateRapidBetsUseCase,
    val favoriteBets: GetFavoriteBetsUseCase,
    val deleteFavoriteBet: DeleteFavoriteBetUseCase,
    val addFavoriteBetToCart: AddFavoriteBetToCartUseCase,
    val validateFavoriteBet: ValidateFavoriteBetUseCase,
    val saveFavoriteBet: SaveFavoriteBetUseCase,
    val paymentMethods: GetPaymentMethodsUseCase,
    val savedCards: GetSavedCardsUseCase,
    val deleteSavedCard: DeleteSavedCardUseCase,
    val generatePixCharge: GeneratePixChargeUseCase,
    val purchaseProgress: GetPurchaseProgressUseCase,
    val validateDam: ValidateDamUseCase,
    val submitCardPayment: SubmitCardPaymentUseCase,
    val hasRecentPurchase: br.gov.caixa.loterias.core.domain.cart.HasRecentPurchaseUseCase,
    val checkCheckoutLocation: br.gov.caixa.loterias.core.domain.cart.CheckCheckoutLocationUseCase,
    val recordCheckoutLocationRequest: br.gov.caixa.loterias.core.domain.cart.RecordCheckoutLocationRequestUseCase,
    val recordRecentPurchase: br.gov.caixa.loterias.core.domain.cart.RecordRecentPurchaseUseCase,
    val confirmedBets: GetConfirmedBetsUseCase,
    val recoverConfirmedBet: RecoverConfirmedBetUseCase,
    val purchases: GetPurchasesUseCase,
    val purchaseBets: GetPurchaseBetsUseCase,
    val purchaseDetails: GetPurchaseDetailsUseCase,
    val savePurchaseAsFavorite: SavePurchaseAsFavoriteUseCase,
    val checkTicket: CheckTicketUseCase,
    val confirmedBetsConfiguration: GetConfirmedBetsConfigurationUseCase,
    val purchaseMonths: GetPurchaseMonthsUseCase,
    val purchasePaymentMethods: GetPurchasePaymentMethodsUseCase,
    val purchaseStatuses: GetPurchaseStatusesUseCase,
    val searchRetailers: SearchRetailersUseCase,
    val favoriteRetailers: GetFavoriteRetailersUseCase,
    val setRetailerFavorite: SetRetailerFavoriteUseCase,
    val prizeDetails: GetPrizeDetailsUseCase,
    val lotteryRedemptionCode: GenerateLotteryRedemptionCodeUseCase,
    val consultPixRedemptionKey: ConsultPixRedemptionKeyUseCase,
    val redeemPrize: RedeemPrizeUseCase,
    val cancelPixRedemption: CancelPixRedemptionUseCase,
    val downloadBetReceipt: DownloadBetReceiptUseCase,
    val downloadPrizeReceipt: DownloadPrizeReceiptUseCase,
    val notificationViewModelFactory: NotificationViewModelFactory,
    val currentUserIdentity: CurrentUserIdentityProvider,
    val dispatchers: CoroutineDispatchers,
) {
    fun prepareLoggedVisualFixture(requested: Boolean) {
        if (!shouldUseLoggedVisualFixture(BuildConfig.DEBUG, BuildConfig.ENABLE_MOCK_BACKEND, requested)) return
        if (tokenStore.captureIdentity() != null) return
        br.gov.caixa.loterias.core.auth.SyntheticSessionFixture.install(tokenStore)
    }

    val sessionIdentityChanges = tokenStore.observeIdentities()
    val foregroundSession: br.gov.caixa.loterias.core.auth.ForegroundSessionAccess = tokenStore

    val serverTime: br.gov.caixa.loterias.core.common.ServerTimeSource get() = serverClock
    private val applicationContext = context.applicationContext
    // Only CAIXA clients observe the server clock, never third-party payment clients.
    private val backendGson by lazy {
        com.google.gson.GsonBuilder().registerTypeAdapterFactory(
            br.gov.caixa.loterias.core.network.ServerTimeAdapterFactory { serverClock.synchronize(it) },
        ).create()
    }
    private val cloudRouting = CloudBackendRouting(BuildConfig.API_BASE_URL.toHttpUrl(), BuildConfig.FLAVOR == "prd")

    private fun backendClientBuilder() = OkHttpClient.Builder().apply {
        if (!BuildConfig.ENABLE_MOCK_BACKEND) addInterceptor(cloudRouting)
    }

    private val localFallback = PublicKeySet(
        version = 0,
        issuedAtEpochSeconds = 0,
        expiresAtEpochSeconds = Long.MAX_VALUE,
        hosts = listOf(
            HostPublicKeyPins(
                hostname = "silce.carrinho.caixa.gov.br",
                // Leaf certificate verified against the system CA chain and hostname on 2026-09-09.
                // CN=silce.carrinho.caixa.gov.br; validity ends 2027-02-03.
                pins = listOf("sha256/0DYFpXI7bHqawgC/ukvGy585F993RbQvkwvQoJDMGC0="),
            ),
            HostPublicKeyPins(
                hostname = "mobileloterias.caixa.gov.br",
                pins = listOf(
                    "sha256/tQUfS0eZg0eN0NEBbe/7S5r3rTS/F0Rx6QSEGs6ku74=",
                    "sha256//GoM/RN9Mr8qcaA7Yg5lP5cTHAYcFhQmEKdFrCh9pQk=",
                ),
            ),
        ),
    )
    private val debugOptions = NetworkDebugOptions(
        enabled = BuildConfig.ENABLE_NETWORK_LOGGING,
        sink = NetworkLogSink { message -> Log.d(NETWORK_LOG_TAG, message) },
    )
    private val mockOptions = MockBackendOptions(
        enabled = BuildConfig.ENABLE_MOCK_BACKEND,
        scenario = MockBackendScenario.from(BuildConfig.MOCK_BACKEND_SCENARIO),
    )
    private val keySetRepository = runCatching { createKeySetRepository() }
        .onFailure { failure ->
            if (BuildConfig.ENABLE_NETWORK_LOGGING) {
                Log.d(NETWORK_LOG_TAG, "Configuração remota de pins indisponível: ${failure.javaClass.simpleName}")
            }
        }
        .getOrNull()
    init {
        apiProvider.onParametersLoaded(cloudRouting::update)
        replaceMainBackendApis(localFallback)
    }

    val networkSecurity: br.gov.caixa.loterias.core.domain.configuration.PrepareNetworkSecurityUseCase =
        NetworkSecurityPreparation { updateNetworkSecurity() }

    /** Refreshes signed pins before business APIs are used; local/cached pins remain available offline. */
    private suspend fun updateNetworkSecurity() {
        if (BuildConfig.ENABLE_MOCK_BACKEND) {
            replaceMainBackendApis(localFallback)
            return
        }
        val resolved = keySetRepository?.refresh()
        replaceMainBackendApis(resolved?.keySet ?: localFallback)
        if (BuildConfig.ENABLE_NETWORK_LOGGING) {
            Log.d(
                NETWORK_LOG_TAG,
                "Pins TLS carregados de ${resolved?.source ?: "LOCAL_FALLBACK"}, versão ${resolved?.keySet?.version ?: 0}",
            )
        }
    }

    private fun createApi(keySet: PublicKeySet) =
        NetworkClientFactory.create(
            baseUrl = BuildConfig.API_BASE_URL, clientBuilder = backendClientBuilder(),
            tokenProvider = tokenStore,
            gson = backendGson,
            publicKeySet = keySet,
            debugOptions = debugOptions,
            mockOptions = mockOptions,
        )

    private fun replaceMainBackendApis(keySet: PublicKeySet) {
        apiProvider.replace(createApi(keySet))
        notificationReadProvider.replace(NetworkClientFactory.createNotificationRead(
            baseUrl = BuildConfig.API_BASE_URL, clientBuilder = backendClientBuilder(), gson = backendGson, publicKeySet = keySet,
            debugOptions = debugOptions, mockOptions = mockOptions,
        ))
        accountVerificationProvider.replace(
            NetworkClientFactory.createAccountVerification(
                baseUrl = BuildConfig.API_BASE_URL, clientBuilder = backendClientBuilder(),
                gson = backendGson,
                publicKeySet = keySet,
                debugOptions = debugOptions,
                mockOptions = mockOptions,
            ),
        )
        prizeApiProvider.replace(
            NetworkClientFactory.createPrizeRedemption(
                baseUrl = BuildConfig.API_BASE_URL, clientBuilder = backendClientBuilder(),
                tokenProvider = tokenStore,
                gson = backendGson,
                publicKeySet = keySet,
                debugOptions = debugOptions,
                mockOptions = mockOptions,
            ),
        )
        purchaseApiProvider.replace(
            NetworkClientFactory.createPurchaseBackend(
                baseUrl = BuildConfig.API_BASE_URL, clientBuilder = backendClientBuilder(),
                tokenProvider = tokenStore,
                gson = backendGson,
                publicKeySet = keySet,
                debugOptions = debugOptions,
                mockOptions = mockOptions,
            ),
        )
        legacyApiProvider.replace(
            NetworkClientFactory.createLegacy(
                baseUrl = BuildConfig.API_BASE_URL, clientBuilder = backendClientBuilder(),
                tokenProvider = tokenStore,
                gson = backendGson,
                publicKeySet = keySet,
                debugOptions = debugOptions,
                mockOptions = mockOptions,
            ),
        )
        betsHistoryApiProvider.replace(
            NetworkClientFactory.createBetsHistory(
                baseUrl = BuildConfig.API_BASE_URL, clientBuilder = backendClientBuilder(),
                tokenProvider = tokenStore,
                gson = backendGson,
                publicKeySet = keySet,
                debugOptions = debugOptions,
                mockOptions = mockOptions,
            ),
        )
        betsHistoryApiProvider.configureDynamicClients(
            verifiedPinnedHosts = keySet.hosts.map(HostPublicKeyPins::hostname).toSet(),
        ) { baseUrl ->
            NetworkClientFactory.createBetsHistory(
                baseUrl = baseUrl,
                tokenProvider = tokenStore,
                gson = backendGson,
                publicKeySet = keySet,
                debugOptions = debugOptions,
                mockOptions = mockOptions,
            )
        }
        paymentApiProvider.replace(
            PaymentApis(
                mercadoPago = NetworkClientFactory.createMercadoPago(
                    baseUrl = BuildConfig.MERCADO_PAGO_BASE_URL,
                    publicKeySet = keySet,
                    debugOptions = debugOptions,
                    mockOptions = mockOptions,
                ),
                recargaPay = NetworkClientFactory.createRecargaPay(
                    baseUrl = BuildConfig.RECARGA_PAY_BASE_URL,
                    publicKeySet = keySet,
                    debugOptions = debugOptions,
                    mockOptions = mockOptions,
                ),
            ),
        )
    }

    private fun createKeySetRepository(): PublicKeySetRepository? {
        val signers = BuildConfig.KEYSET_TRUSTED_SIGNERS.split(',')
            .mapNotNull { item ->
                val parts = item.trim().split('=', limit = 2)
                if (parts.size == 2 && parts.all(String::isNotBlank)) parts[0] to parts[1] else null
            }.toMap()
        if (signers.size < BuildConfig.KEYSET_SIGNATURE_THRESHOLD) return null

        val verifier = SignedKeySetVerifier(
            trustedSigners = TrustedConfigurationSigners(signers, BuildConfig.KEYSET_SIGNATURE_THRESHOLD),
            allowedHosts = localFallback.hosts.map(HostPublicKeyPins::hostname).toSet() +
                setOf(
                    "*.caixa.gov.br",
                    requireNotNull(Uri.parse(BuildConfig.API_BASE_URL).host),
                    requireNotNull(Uri.parse(BuildConfig.MERCADO_PAGO_BASE_URL).host),
                    requireNotNull(Uri.parse(BuildConfig.RECARGA_PAY_BASE_URL).host),
                ),
        )
        return PublicKeySetRepository(
            remote = FirebaseRemotePublicKeySetSource.create(applicationContext),
            store = PreferencesSignedKeySetStore(applicationContext),
            verifier = verifier,
            localFallback = localFallback,
        )
    }

    private companion object {
        const val NETWORK_LOG_TAG = "LoteriasHTTP"
    }
}
