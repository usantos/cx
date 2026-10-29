package br.gov.caixa.loterias.apostas

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignActivation
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignArea
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaignSettings
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ObserveAuthenticatedSessionContentUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ReadLatestNotificationUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PreparePoolEntryUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.home.navigation.HomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.presentation.route.HomeRoute
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomeViewModel
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import java.io.File
import java.math.BigDecimal
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenCoordinationUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun drawerOpenStateSurvivesSavedStateRestoration() {
        val fixture = Fixture()
        val restoration = StateRestorationTester(compose)
        show(fixture, restoration = restoration)
        openMenu()
        compose.onNodeWithContentDescription("Fechar menu").assertIsDisplayed()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithContentDescription("Fechar menu").assertIsDisplayed()
    }

    @Test fun parameterRecoveryRetainsTheOpenDrawer() {
        val fixture = Fixture()
        show(fixture)
        openMenu()
        fixture.available = false
        compose.runOnIdle { fixture.viewModel.load() }
        compose.waitUntil(5_000) { fixture.viewModel.state.value.parametersUnavailable && !fixture.viewModel.state.value.loading }
        compose.onNodeWithText("Tentar novamente").assertIsDisplayed()
        fixture.available = true
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.waitUntil(5_000) { !fixture.viewModel.state.value.parametersUnavailable && !fixture.viewModel.state.value.loading }
        compose.onNodeWithContentDescription("Fechar menu").assertIsDisplayed()
    }

    @Test fun betDecisionIsDeliveredOnceAcrossRecomposition() {
        val fixture = Fixture()
        val name = mutableStateOf("Pessoa")
        val decisions = mutableListOf<BetEntryDecision>()
        show(fixture, name = { name.value }, onBetEntry = decisions::add)
        compose.onNode(hasText("Aposte", substring = true) and hasClickAction()).performClick()
        compose.waitUntil(5_000) { decisions.size == 1 }
        compose.runOnIdle { name.value = "Outra apresentação do nome" }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(listOf(BetEntryDecision.Ready(fixture.game)), decisions) }
    }

    @Test fun protectedPoolUsesTheExistingContinuationWithoutPreparingAPool() {
        val fixture = Fixture()
        val continuations = mutableListOf<String>()
        show(fixture, onProtectedAction = continuations::add)
        val pool = hasText("Compre seu bolão")
        compose.onNode(
            if (BuildConfig.HOME_V2) {
                pool and SemanticsMatcher.expectValue(
                    androidx.compose.ui.semantics.SemanticsProperties.Role,
                    androidx.compose.ui.semantics.Role.Button
                )
            } else {
                pool
            }
        ).performClick()
        compose.runOnIdle { assertEquals(listOf("home/pool/2/NORMAL"), continuations) }
    }

    @Test fun captureDeterministicHomeDrawerAndClassicPanels() {
        val fixture = Fixture()
        show(fixture)
        capture("home")
        openMenu()
        capture("drawer")
        compose.onNodeWithContentDescription("Fechar menu").performClick()
        if (!BuildConfig.HOME_V2) {
            compose.onNodeWithContentDescription("Apresentar atalhos").performClick()
            compose.waitUntil(5_000) { !fixture.viewModel.state.value.loadingSocialTransfers }
            capture("quick-access")
            compose.onNodeWithContentDescription("Ocultar atalhos").performClick()
            val results = compose.onAllNodesWithText("Último resultado")
            val center = compose.onRoot().fetchSemanticsNode().boundsInRoot.center.x
            val index = results.fetchSemanticsNodes().indices.minBy { i ->
                kotlin.math.abs(results.fetchSemanticsNodes()[i].boundsInRoot.center.x - center)
            }
            results[index].performClick()
            capture("expanded-result")
        }
    }

    @Test fun captureCampaignAndItsExpandablePanel() {
        val fixture = Fixture(campaign = true)
        show(fixture)
        capture("campaign")
        compose.onNodeWithContentDescription("Outubro Rosa").performClick()
        val titles = compose.onAllNodesWithText("Outubro Rosa")
        assertTrue("Campaign panel is visible", titles.fetchSemanticsNodes().indices.any { titles[it].isDisplayed() })
        capture("campaign-panel")
    }

    @Test fun classicPanelsRetainTheirIntermediateAnimationFrames() {
        org.junit.Assume.assumeFalse(BuildConfig.HOME_V2)
        val fixture = Fixture(campaign = true)
        show(fixture)
        compose.mainClock.autoAdvance = false
        compose.onNodeWithContentDescription("Apresentar atalhos").performClick()
        compose.waitUntil(5_000) { !fixture.viewModel.state.value.loadingSocialTransfers }
        for ((index, delta) in listOf(16L, 160L, 160L, 320L).withIndex()) {
            compose.mainClock.advanceTimeBy(delta)
            captureFrame("quick-$index")
        }
        compose.onNodeWithContentDescription("Ocultar atalhos").performClick()
        compose.mainClock.advanceTimeBy(800)
        compose.onNodeWithContentDescription("Outubro Rosa").performClick()
        for ((index, delta) in listOf(16L, 160L, 160L, 320L).withIndex()) {
            compose.mainClock.advanceTimeBy(delta)
            captureFrame("campaign-$index")
        }
        compose.mainClock.autoAdvance = true
    }

    private fun captureFrame(name: String) {
        compose.waitForIdle()
        // RippleDrawable usa o relógio da plataforma, mesmo quando o relógio do Compose está congelado.
        android.os.SystemClock.sleep(600)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val phase = InstrumentationRegistry.getArguments().getString("homePhase") ?: "verification"
        val file = File(instrumentation.targetContext.getExternalFilesDir(null), "home-refactor/$phase/classic-animation-$name.png")
        check(file.parentFile!!.isDirectory || file.parentFile!!.mkdirs())
        file.outputStream().use { stream ->
            check(compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream))
        }
    }

    private fun openMenu() {
        if (BuildConfig.HOME_V2) {
            compose.onNode(
                SemanticsMatcher("Abrir menu") {
                    it.config.getOrNull(androidx.compose.ui.semantics.SemanticsActions.OnClick)?.label == "Abrir menu"
                }
            ).performClick()
        } else {
            compose.onNodeWithContentDescription("Abrir menu").performClick()
        }
    }

    private fun show(
        fixture: Fixture,
        restoration: StateRestorationTester? = null,
        name: () -> String? = { null },
        onBetEntry: (BetEntryDecision) -> Unit = {},
        onProtectedAction: ((String) -> Unit)? = null,
    ) {
        val content: @Composable () -> Unit = {
            CompositionLocalProvider(
                LocalHomeRuntimeConfiguration provides HomeRuntimeConfiguration(
                    homeV2 = BuildConfig.HOME_V2,
                    versionName = BuildConfig.VERSION_NAME,
                    serverTime = br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.EPOCH }
                )
            ) {
                CaixaDSTheme {
                    HomeRoute(
                        userName = name(), networkAvailability = NetworkAvailability { ConnectionAvailability.CONNECTED },
                        environmentLabel = "TQS", onOpen = {}, onPoolEntry = {}, onLogout = {},
                        viewModel = fixture.viewModel, onBetEntry = onBetEntry, onOtherBetting = {},
                        onProtectedAction = onProtectedAction
                    )
                }
            }
        }
        if (restoration != null) restoration.setContent(content) else compose.setContent(content)
        compose.waitUntil(5_000) { fixture.viewModel.state.value.parameters != null && !fixture.viewModel.state.value.loading }
        compose.waitForIdle()
    }

    private fun capture(name: String) {
        compose.mainClock.advanceTimeBy(1_000)
        compose.waitForIdle()
        // RippleDrawable usa o relógio da plataforma, mesmo quando o relógio do Compose está congelado.
        android.os.SystemClock.sleep(600)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val phase = InstrumentationRegistry.getArguments().getString("homePhase") ?: "verification"
        val variant = if (BuildConfig.HOME_V2) "v2" else "classic"
        val file = File(instrumentation.targetContext.getExternalFilesDir(null), "home-refactor/$phase/$variant-$name.png")
        check(file.parentFile!!.isDirectory || file.parentFile!!.mkdirs())
        file.outputStream().use { stream ->
            check(compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream))
        }
    }

    private class Fixture(campaign: Boolean = false) {
        @Volatile var available = true
        val game = LotteryGameParameters(
            modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA"), contestNumber = 4321,
            drawDate = "17/10/2026", maximumNumber = 60, minimumSelection = 6, maximumSelection = 20,
            maximumSurprises = 10, recurringOptions = emptyList(), prices = listOf(BetPrice(6, 0, BigDecimal("5.00"))),
            estimatedPrize = BigDecimal("10000000")
        )
        private val parameters = BettingParameters(
            BigDecimal.TEN,
            BigDecimal.ONE,
            30,
            listOf(game),
            availablePoolModalities = listOf(PoolModalityAvailability(2, "MEGA_SENA", "NORMAL"))
        )
        private val features = HomeFeatureConfiguration(
            poolSalesEnabled = true,
            campaigns = CommemorativeCampaignSettings(
                if (campaign) {
                    listOf(
                        CampaignActivation(
                            CommemorativeCampaign.OUTUBRO_ROSA,
                            CampaignArea.entries.toSet()
                        )
                    )
                } else {
                    emptyList()
                }
            )
        )
        val viewModel = HomeViewModel(
            GetBettingParametersUseCase { if (available) AppResult.Success(parameters) else AppResult.Failure(AppError.Network) },
            GetHomeFeatureConfigurationUseCase { AppResult.Success(features) },
            GetBetCombosUseCase { AppResult.Success(emptyList()) },
            GetSocialTransfersUseCase { AppResult.Failure(AppError.Network) },
            GetCurrentCartUseCase { AppResult.Success(ShoppingCart(0, BigDecimal.ZERO, emptyList())) },
            PrepareBetEntryUseCase { AppResult.Success(BetEntryDecision.Ready(game)) },
            PreparePoolEntryUseCase { error("Protected continuation must not prepare a pool") },
            PrepareHomeBettingActionUseCase { error("Unexpected shortcut") },
            ObserveAuthenticatedSessionContentUseCase { flowOf(null) },
            ReadLatestNotificationUseCase { error("Unexpected notification") },
            selectHomeResaleOffers = br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffers(
                br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.EPOCH }
            )
        )
    }
}
