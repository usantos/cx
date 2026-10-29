package br.gov.caixa.loterias.apostas

import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.navigation.workflow.VisitorLoginCallbacks
import br.gov.caixa.loterias.core.navigation.workflow.openVisitorDrawerDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.ObserveAuthenticatedSessionContentUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ReadLatestNotificationUseCase
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
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomeViewModel
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import java.math.BigDecimal
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class VisitorHomeEntryUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun homeV2BottomPromptsOnHomeWhileDrawerOpensPrivateDestination() {
        val vm = visitorHomeViewModel()
        lateinit var nav: NavHostController
        var request by mutableStateOf<String?>(null)
        compose.setContent {
            CaixaDSTheme {
                nav = rememberNavController()
                NavHost(nav, startDestination = "home") {
                    composable("home") {
                        // Exercita as ações inferiores da V2 independentemente da opção padrão da compilação.
                        CompositionLocalProvider(
                            br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration provides
                                br.gov.caixa.loterias.feature.home.navigation.HomeRuntimeConfiguration(homeV2 = true, versionName = "test")
                        ) {
                            HomeRoute(
                                null, NetworkAvailability { ConnectionAvailability.CONNECTED }, null,
                                onOpen = { request = it },
                                onDrawerOpen = { openVisitorDrawerDestination(it, nav) { request = it } },
                                onPoolEntry = {}, onLogout = {}, viewModel = vm, onBetEntry = {}, onOtherBetting = {}
                            )
                        }
                    }
                    composable("tickets/my-bets") { Text("Private history") }
                    composable("betting/favorites") { Text("Private favorites") }
                    composable(br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation.pattern) { Text("Login") }
                }
                request?.let { route ->
                    val callbacks = VisitorLoginCallbacks(route, nav, { request }, { request = null }, {})
                    CaixaDSDialog(
                        "Atenção",
                        "Efetuar login?",
                        "Sim",
                        callbacks::onPositive,
                        "Não",
                        callbacks::onNegative,
                        visitorPrompt = true
                    )
                }
            }
        }
        compose.onNodeWithText("Minhas\nApostas").performClick()
        compose.runOnIdle { assertEquals("home", nav.currentDestination?.route) }
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithText("Apostas\nFavoritas").performClick()
        compose.runOnIdle { assertEquals("home", nav.currentDestination?.route) }
        compose.onNodeWithText("Não").performClick()
        compose.onNode(
            SemanticsMatcher("Abrir menu") {
                it.config.getOrNull(androidx.compose.ui.semantics.SemanticsActions.OnClick)?.label == "Abrir menu"
            }
        ).performClick()
        compose.onNodeWithText("Minha área").performClick()
        compose.onNodeWithText("Minhas apostas", ignoreCase = true).performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertEquals("tickets/my-bets", nav.currentDestination?.route) }
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertEquals("home", nav.currentDestination?.route) }
    }

    private fun visitorHomeViewModel(): HomeViewModel {
        val parameters =
            BettingParameters(
                BigDecimal.TEN,
                BigDecimal.ONE,
                30,
                listOf(
                    LotteryGameParameters(
                        LotteryModality(
                            2,
                            "Mega-Sena",
                            "MEGA_SENA"
                        ),
                        2890, "14/10/2026", 60, 6, 20, 10, emptyList(), emptyList()
                    )
                )
            )
        return HomeViewModel(
            GetBettingParametersUseCase { AppResult.Success(parameters) },
            GetHomeFeatureConfigurationUseCase { AppResult.Success(HomeFeatureConfiguration()) },
            GetBetCombosUseCase { AppResult.Success(emptyList()) },
            GetSocialTransfersUseCase { AppResult.Failure(AppError.Network) },
            GetCurrentCartUseCase { AppResult.Success(ShoppingCart(0, BigDecimal.ZERO, emptyList())) },
            PrepareBetEntryUseCase { error("Unexpected bet") },
            PreparePoolEntryUseCase { error("Unexpected pool") },
            PrepareHomeBettingActionUseCase { error("Unexpected rapid") },
            ObserveAuthenticatedSessionContentUseCase { flowOf(null) },
            ReadLatestNotificationUseCase { error("Unexpected notification") },
            selectHomeResaleOffers = br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffers(
                br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.EPOCH }
            )
        )
    }
}
