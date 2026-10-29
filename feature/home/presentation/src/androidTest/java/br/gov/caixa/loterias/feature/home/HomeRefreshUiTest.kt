package br.gov.caixa.loterias.feature.home

import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository
import br.gov.caixa.loterias.feature.betting.presentation.route.ShakeTutorialRoute
import br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository
import br.gov.caixa.loterias.feature.home.domain.usecase.HomePreferencesUseCases
import br.gov.caixa.loterias.feature.home.presentation.route.HomeRoute
import br.gov.caixa.loterias.feature.home.presentation.route.HomeV2RouteContent
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomeViewModel
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeRefreshUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun skippingShakeTutorialOpensDrawerAndTooltip() = tutorialOpensGuidance(false)

    @Test fun completingShakeTutorialOpensDrawerAndTooltip() = tutorialOpensGuidance(true)

    private fun tutorialOpensGuidance(complete: Boolean) {
        val shake = object : ShakeSettingsRepository {
            private var completed = false
            private var pending = false
            override suspend fun enabled() = false
            override suspend fun setEnabled(enabled: Boolean) {}
            override suspend fun warningHidden() = true
            override suspend fun setWarningHidden(hidden: Boolean) {}
            override suspend fun tutorialCompleted(ownerKey: String?) = completed
            override suspend fun completeTutorial(ownerKey: String?) {
                completed = true
                pending = true
            }
            override suspend fun tooltipPending(ownerKey: String?) = pending
            override suspend fun consumeTooltip(ownerKey: String?) {
                pending = false
            }
        }
        val preferences = HomePreferencesViewModel(
            HomePreferencesUseCases(
                object : HomeLayoutRepository {
                    override suspend fun loadOrder(): String? = null
                    override suspend fun saveOrder(order: String) {}
                },
                shake
            ),
            br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers(kotlinx.coroutines.Dispatchers.Main.immediate)
        )
        val parameters = BettingParameters(
            BigDecimal.TEN,
            BigDecimal.ONE,
            30,
            listOf(
                LotteryGameParameters(
                    LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 1234,
                    "21/10/2026", 60, 6, 20, 10, emptyList(), emptyList()
                )
            )
        )
        val model = model(AtomicInteger(), parameters)
        compose.setContent {
            CaixaDSTheme {
                val state = preferences.state.collectAsStateWithLifecycle()
                if (!state.value.loaded) {
                    Text("Carregando")
                } else if (!state.value.shakeTutorialCompleted) {
                    ShakeTutorialRoute { preferences.completeShakeTutorial() }
                } else {
                    HomeRoute(
                        null, { br.gov.caixa.loterias.core.common.network.ConnectionAvailability.UNKNOWN },
                        null, {}, {}, {}, model, preferencesViewModel = preferences, onBetEntry = {}, onOtherBetting = {}
                    )
                }
            }
        }
        compose.waitUntil(5_000) { preferences.state.value.loaded }
        if (complete) {
            repeat(5) { compose.onNodeWithContentDescription("Próxima etapa").performClick() }
            compose.onNodeWithText("Fechar tutorial").performClick()
        } else {
            compose.onNodeWithText("Pular tutorial").performClick()
        }
        compose.waitUntil(5_000) { preferences.state.value.shakeTutorialCompleted }
        compose.onNodeWithText("Agite o celular para preencher\nsua aposta com números aleatórios").assertIsDisplayed()
        compose.waitUntil(5_000) { !preferences.state.value.shakeTooltipPending }
        compose.onNodeWithContentDescription("Fechar menu").performClick()
        compose.onNodeWithText("Agite o celular para preencher\nsua aposta com números aleatórios").assertDoesNotExist()
        compose.runOnIdle {
            model.viewModelScope.cancel()
            preferences.viewModelScope.cancel()
        }
    }

    @Test fun reenteringV2DoesNotReloadParameters() {
        val calls = AtomicInteger()
        val model = model(calls)
        val show = mutableStateOf(false)
        compose.setContent {
            androidx.compose.runtime.CompositionLocalProvider(
                br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration provides
                    br.gov.caixa.loterias.feature.home.navigation.HomeRuntimeConfiguration(true, "test")
            ) {
                CaixaDSTheme {
                    if (show.value) {
                        HomeRoute(
                            null, { br.gov.caixa.loterias.core.common.network.ConnectionAvailability.UNKNOWN },
                            null, {}, {}, {}, model, onBetEntry = {}, onOtherBetting = {}
                        )
                    } else {
                        Text("Outra tela")
                    }
                }
            }
        }
        compose.waitUntil(5_000) { !model.state.value.loading }
        repeat(2) {
            compose.runOnIdle { show.value = true }
            compose.waitForIdle()
            compose.waitUntil(5_000) { !model.state.value.loading }
            assertEquals("Only the initial load is automatic", 1, calls.get())
            compose.runOnIdle { show.value = false }
        }
        compose.runOnIdle { model.viewModelScope.cancel() }
    }

    @Test fun classicHeaderStaysVisibleWhenReturningDuringAModalityRefresh() {
        val parameters = BettingParameters(
            BigDecimal.TEN,
            BigDecimal.ONE,
            30,
            listOf(
                LotteryGameParameters(
                    LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 1234,
                    "21/10/2026", 60, 6, 20, 10, emptyList(), emptyList()
                )
            )
        )
        val initial = kotlinx.coroutines.CompletableDeferred<AppResult<BettingParameters>>()
        val response = java.util.concurrent.atomic.AtomicReference(initial)
        val model = model(AtomicInteger(), parametersSource = { response.get().await() })
        val show = mutableStateOf(true)
        try {
            compose.setContent {
                androidx.compose.runtime.CompositionLocalProvider(
                    br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration provides
                        br.gov.caixa.loterias.feature.home.navigation.HomeRuntimeConfiguration(false, "test")
                ) {
                    CaixaDSTheme {
                        if (show.value) {
                            HomeRoute(
                                null,
                                { br.gov.caixa.loterias.core.common.network.ConnectionAvailability.UNKNOWN },
                                null, {}, {}, {}, model, onBetEntry = {}, onOtherBetting = {}
                            )
                        } else {
                            Text("Outra tela")
                        }
                    }
                }
            }
            compose.onNodeWithContentDescription("Abrir menu").assertDoesNotExist()
            compose.onNodeWithContentDescription("Notificações").assertDoesNotExist()
            initial.complete(AppResult.Success(parameters))
            compose.waitUntil(5_000) { model.state.value.headerActionsRevealed }
            compose.onNodeWithContentDescription("Abrir menu").assertIsDisplayed()
            compose.onNodeWithContentDescription("Notificações").assertIsDisplayed()

            compose.runOnIdle { show.value = false }
            compose.onNodeWithText("Outra tela").assertIsDisplayed()
            val refresh = kotlinx.coroutines.CompletableDeferred<AppResult<BettingParameters>>()
            compose.runOnIdle {
                response.set(refresh)
                model.load()
                show.value = true
            }
            compose.onNodeWithContentDescription("Abrir menu").assertIsDisplayed()
            compose.onNodeWithContentDescription("Notificações").assertIsDisplayed()
            compose.runOnIdle { org.junit.Assert.assertTrue(model.state.value.loading) }
            refresh.complete(AppResult.Success(parameters))
            compose.waitUntil(5_000) { !model.state.value.loading }
            compose.onNodeWithContentDescription("Notificações").assertIsDisplayed()
        } finally {
            compose.runOnIdle { model.viewModelScope.cancel() }
        }
    }

    @Test fun pullingV2RefreshesExistingCards() {
        val calls = AtomicInteger()
        val model = model(calls)
        val lottery = HomeLottery(
            2, "NORMAL", "megasena", "Mega-Sena", 1234,
            BigDecimal.TEN, BigDecimal.ONE, "2026-10-21T21:00:00", "", true,
            HomeLotteryKind.LOTTERY, Color.Green, Color.Black, Color.White,
            R.drawable.trevo_megasena_fundo_branco, R.drawable.ic_calendario, R.drawable.ic_calendario
        )
        compose.setContent {
            CaixaDSTheme {
                val state = model.state.collectAsStateWithLifecycle()
                HomeV2RouteContent(
                    listOf("MEGA_SENA"), { _, _ -> }, state.value, null, listOf(lottery),
                    LocalDensity.current, 0f, {}, null, model, {}
                )
            }
        }
        compose.waitUntil(5_000) { !model.state.value.loading }
        compose.onRoot().performTouchInput { swipeDown(startY = height * .18f, endY = height * .75f, durationMillis = 500) }
        compose.waitUntil(5_000) { calls.get() == 2 }
        compose.onNodeWithText("Escolha sua modalidade").assertExists()
        compose.runOnIdle { model.viewModelScope.cancel() }
    }

    private fun model(
        calls: AtomicInteger,
        parameters: BettingParameters? = null,
        parametersSource: suspend () -> AppResult<BettingParameters> = {
            parameters?.let { AppResult.Success(it) } ?: AppResult.Failure(AppError.Network)
        },
    ) = HomeViewModel(
        {
            calls.incrementAndGet()
            parametersSource()
        },
        { AppResult.Success(HomeFeatureConfiguration()) }, { AppResult.Success(emptyList()) },
        { AppResult.Failure(AppError.Network) }, { AppResult.Failure(AppError.Network) },
        { error("Unused betting action") }, { error("Unused pool action") }, { error("Unused shortcut") },
        { flowOf(null) }, { error("Unused notification") },

        selectHomeResaleOffers = br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffers(
            br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.parse("2026-10-21T18:00:00Z") }
        )
    )
}
