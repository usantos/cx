package br.gov.caixa.loterias.apostas

import android.content.ActivityNotFoundException
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ObserveAuthenticatedSessionContentUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ReadLatestNotificationUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PreparePoolEntryUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.home.presentation.route.HomeRoute
import br.gov.caixa.loterias.feature.home.navigation.HomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomeViewModel
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import java.math.BigDecimal
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeFalse
import org.junit.Rule
import org.junit.Test

/** Execute com -PhomeV2=false: este atalho pertence à rota da Home clássica. */
class HomeExternalLinkTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private val userName = mutableStateOf("Pessoa")

    @Test fun successfulWomenSupportOpeningUsesOriginalUrlAndClosesQuickAccessOnce() {
        val handler = RecordingUriHandler()
        show(handler)
        openWomenSupport()
        compose.onNodeWithContentDescription("Apresentar atalhos").assertIsDisplayed()
        compose.onNodeWithText(FAILURE).assertDoesNotExist()
        recomposeAndAssertAttempts(handler, 1)
    }

    @Test fun absentBrowserShowsRetryAndRetriesTheOriginalWomenSupportUrl() {
        assertFailureAndRetry(ActivityNotFoundException("No browser"))
    }

    @Test fun deniedBrowserShowsRetryAndRetriesTheOriginalWomenSupportUrl() {
        assertFailureAndRetry(SecurityException("Browser denied"))
    }

    @Test fun rejectedUriShowsRetryAndRetriesTheOriginalWomenSupportUrl() {
        assertFailureAndRetry(IllegalArgumentException("Cannot open URI"))
    }

    @Test fun dismissingOpeningFailureDoesNotRetry() {
        val handler = RecordingUriHandler(SecurityException("Browser denied"))
        show(handler)
        openWomenSupport()
        compose.onNodeWithText(FAILURE).assertIsDisplayed()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText(FAILURE).assertDoesNotExist()
        recomposeAndAssertAttempts(handler, 1)
    }

    private fun assertFailureAndRetry(failure: RuntimeException) {
        val handler = RecordingUriHandler(failure)
        show(handler)
        openWomenSupport()
        compose.onNodeWithText(FAILURE).assertIsDisplayed()
        recomposeAndAssertAttempts(handler, 1)
        compose.runOnIdle { handler.failure = null }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText(FAILURE).assertDoesNotExist()
        compose.onNodeWithContentDescription("Apresentar atalhos").assertIsDisplayed()
        recomposeAndAssertAttempts(handler, 2)
    }

    private fun recomposeAndAssertAttempts(handler: RecordingUriHandler, count: Int) {
        compose.runOnIdle { userName.value += " atualizada" }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(List(count) { URL }, handler.urls) }
    }

    private fun openWomenSupport() {
        compose.onNodeWithContentDescription("Apresentar atalhos").performClick()
        compose.onNodeWithText("Central de Atendimento à Mulher").performClick()
        compose.waitForIdle()
    }

    private fun show(handler: UriHandler) {
        assumeFalse("Classic route must be built with -PhomeV2=false", BuildConfig.HOME_V2)
        val game = LotteryGameParameters(
            modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA"), contestNumber = 4321,
            drawDate = "17/10/2026", maximumNumber = 60, minimumSelection = 6, maximumSelection = 20,
            maximumSurprises = 10, recurringOptions = emptyList(), prices = listOf(BetPrice(6, 0, BigDecimal("5.00")))
        )
        val parameters = BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 30, listOf(game))
        val model = HomeViewModel(
            GetBettingParametersUseCase { AppResult.Success(parameters) },
            GetHomeFeatureConfigurationUseCase { AppResult.Success(HomeFeatureConfiguration()) },
            GetBetCombosUseCase { AppResult.Success(emptyList()) },
            GetSocialTransfersUseCase { AppResult.Failure(AppError.Network) },
            GetCurrentCartUseCase { AppResult.Success(ShoppingCart(0, BigDecimal.ZERO, emptyList())) },
            PrepareBetEntryUseCase { error("No betting expected") },
            PreparePoolEntryUseCase { error("No pool expected") },
            PrepareHomeBettingActionUseCase { error("No betting expected") },
            ObserveAuthenticatedSessionContentUseCase { flowOf(null) },
            ReadLatestNotificationUseCase { error("No notification expected") },
            selectHomeResaleOffers = br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffers(
                br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.EPOCH }
            )
        )
        compose.setContent {
            CompositionLocalProvider(
                LocalUriHandler provides handler,
                LocalHomeRuntimeConfiguration provides HomeRuntimeConfiguration(
                    homeV2 = BuildConfig.HOME_V2,
                    versionName = BuildConfig.VERSION_NAME,
                    serverTime = br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.EPOCH }
                )
            ) {
                CaixaDSTheme(darkTheme = false) {
                    HomeRoute(
                        userName = userName.value,
                        networkAvailability = NetworkAvailability { ConnectionAvailability.CONNECTED },
                        environmentLabel = "TQS", onOpen = {}, onPoolEntry = {}, onLogout = {},
                        viewModel = model, onBetEntry = {}, onOtherBetting = {}
                    )
                }
            }
        }
        compose.waitUntil(5_000) { model.state.value.parameters != null && !model.state.value.loading }
        compose.waitForIdle()
    }

    private class RecordingUriHandler(var failure: RuntimeException? = null) : UriHandler {
        val urls = mutableListOf<String>()
        override fun openUri(uri: String) {
            urls += uri
            failure?.let { throw it }
        }
    }

    private companion object {
        const val URL = "https://api.whatsapp.com/send?phone=5561996100180&text=Disque180"
        const val FAILURE = "Não foi possível abrir o WhatsApp do canal 180. Tente novamente."
    }
}
