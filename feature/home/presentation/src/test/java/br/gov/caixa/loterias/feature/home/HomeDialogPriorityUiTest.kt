package br.gov.caixa.loterias.feature.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.Lifecycle
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.model.AccountNotification
import br.gov.caixa.loterias.feature.home.presentation.route.HomeDialogActions
import br.gov.caixa.loterias.feature.home.presentation.route.HomeRouteDialogs
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class HomeDialogPriorityUiTest {
    @get:Rule val compose = createComposeRule()
    private val calls = mutableListOf<String>()
    private var paymentAllowed = false
    private val lifecycle = mutableStateOf(Lifecycle.State.RESUMED)

    @Test fun preservedLocalBetsTakePriorityAndRequireExplicitReviewNavigation() {
        show(HomeUiState(loading = false, localCartReviewRequired = true, unidentifiedPayment = true, betEntryError = "Bet error"))
        compose.onNodeWithText("Conferir apostas").assertIsDisplayed().performClick()
        compose.onNodeWithText("Acompanhe suas compras").assertDoesNotExist()
        compose.onNodeWithText("Bet error").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("local-review", "open:cart/local-review"), calls) }
    }

    @Test fun deferringLocalReviewAcknowledgesWithoutNavigating() {
        show(HomeUiState(loading = false, localCartReviewRequired = true))
        compose.onNodeWithText("Depois").performClick()
        compose.runOnIdle { assertEquals(listOf("local-review"), calls) }
    }

    @Test fun paymentNoticeNavigatesOnlyWhenAcknowledgmentIsAccepted() {
        show(HomeUiState(loading = false, unidentifiedPayment = true))
        compose.onNodeWithText("Acompanhe suas compras").performClick()
        compose.runOnIdle {
            assertEquals(listOf("payment"), calls)
            paymentAllowed = true
        }
        compose.onNodeWithText("Acompanhe suas compras").performClick()
        compose.runOnIdle { assertEquals(listOf("payment", "payment", "open:tickets/purchases"), calls) }
    }

    @Test fun readErrorSuppressesBetErrorAndUsesItsOwnDismissalCallback() {
        show(HomeUiState(loading = false, notificationReadError = "Read error", betEntryError = "Bet error"))
        compose.onNodeWithText("Read error").assertIsDisplayed()
        compose.onNodeWithText("Bet error").assertDoesNotExist()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(listOf("read-error"), calls) }
    }

    @Test fun ordinaryBetErrorUsesEntryErrorCallback() {
        show(HomeUiState(loading = false, betEntryError = "Bet error"))
        compose.onNodeWithText("Bet error").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(listOf("bet-error"), calls) }
    }

    @Test fun notificationIsHiddenWhilePausedAndItsLinkAndCloseAreSeparateActions() {
        val state =
            HomeUiState(loading = false, latestNotification = AccountNotification(1, "News", "Message", null, "https://example.test"))
        lifecycle.value = Lifecycle.State.STARTED
        show(state)
        compose.onNodeWithText("News").assertDoesNotExist()
        compose.runOnIdle { lifecycle.value = Lifecycle.State.RESUMED }
        compose.onNodeWithText("News").assertIsDisplayed()
        compose.onNodeWithText("Abrir Link").performScrollTo().performClick()
        compose.onNodeWithText("Fechar").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(listOf("notification-link", "notification-close"), calls) }
    }

    private fun show(state: HomeUiState) {
        val actions = HomeDialogActions(
            { calls.add("local-review") },
            {
                calls.add("payment")
                paymentAllowed
            },
            { calls.add("notification-close") },
            { calls.add("read-error") },
            { calls.add("notice-accept") },
            { calls.add("notice-dismiss") },
            { calls.add("bet-error") },
            { calls.add("notification-link") }
        )
        compose.setContent {
            CaixaDSTheme {
                HomeRouteDialogs(state, lifecycle.value, { calls.add("open:$it") }, actions)
            }
        }
    }
}
