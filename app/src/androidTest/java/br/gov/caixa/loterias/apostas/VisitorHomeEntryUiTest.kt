package br.gov.caixa.loterias.apostas

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.designsystem.*
import br.gov.caixa.loterias.core.domain.account.*
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.*
import br.gov.caixa.loterias.core.domain.configuration.*
import br.gov.caixa.loterias.core.domain.responsiblegaming.*
import br.gov.caixa.loterias.feature.home.HomeViewModel
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class VisitorHomeEntryUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    @Test fun actualHomeBottomPromptsOnHomeWhileDrawerOpensPrivateDestination() {
        val parameters = BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 30, listOf(
            LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 2890, "14/10/2026", 60, 6, 20, 10, emptyList(), emptyList())))
        val vm = HomeViewModel(
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
        )
        lateinit var nav: NavHostController
        var request by mutableStateOf<String?>(null)
        compose.setContent {
            CaixaDSTheme {
                nav = rememberNavController()
                NavHost(nav, startDestination = "home") {
                    composable("home") {
                        HomeScreen(null, NetworkAvailability { ConnectionAvailability.CONNECTED }, null,
                            onOpen = { request = it },
                            onDrawerOpen = { openVisitorDrawerDestination(it, nav) { request = it } },
                            onPoolEntry = {}, onLogout = {}, viewModel = vm, onBetEntry = {}, onOtherBetting = {})
                    }
                    composable("tickets/my-bets") { Text("Private history") }
                    composable("betting/favorites") { Text("Private favorites") }
                    composable("account/login?authorize={authorize}") { Text("Login") }
                }
                request?.let { route ->
                    val callbacks = VisitorLoginCallbacks(route, nav, { request }, { request = null }, {})
                    CaixaDSLegacyDialog("Atenção", "Efetuar login?", "Sim", callbacks::onPositive, "Não", callbacks::onNegative, visitorPrompt = true)
                }
            }
        }
        compose.onNodeWithText("Minhas\nApostas").performClick()
        compose.runOnIdle { assertEquals("home", nav.currentDestination?.route) }
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithText("Apostas\nFavoritas").performClick()
        compose.runOnIdle { assertEquals("home", nav.currentDestination?.route) }
        compose.onNodeWithText("Não").performClick()
        compose.onNode(SemanticsMatcher("Abrir menu") {
            it.config.getOrNull(androidx.compose.ui.semantics.SemanticsActions.OnClick)?.label == "Abrir menu"
        }).performClick()
        compose.onNodeWithText("Minha área").performClick()
        compose.onNodeWithText("Minhas apostas", ignoreCase = true).performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertEquals("tickets/my-bets", nav.currentDestination?.route) }
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertEquals("home", nav.currentDestination?.route) }
    }
}
