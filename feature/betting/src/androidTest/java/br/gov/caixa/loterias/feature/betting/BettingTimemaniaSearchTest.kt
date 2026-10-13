package br.gov.caixa.loterias.feature.betting

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.io.File
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Busca, foco e seleção são exercitados pela semântica Compose; os serviços permanecem locais. */
class BettingTimemaniaSearchTest {
    @get:Rule val compose = createComposeRule()

    @Test fun searchWithResultWithoutResultAndSelectTeamWithoutSending() {
        val sends = AtomicInteger(0)
        val alfa = SportsTeam(1, "Time Alfa", "SP")
        val beta = SportsTeam(2, "Time Beta", "RJ")
        val game = LotteryGameParameters(
            modality = LotteryModality(20, "Timemania", "TIMEMANIA"),
            contestNumber = 2900, drawDate = "06/10/2026", maximumNumber = 80,
            minimumSelection = 10, maximumSelection = 10, maximumSurprises = 3,
            recurringOptions = listOf(0, 2), prices = listOf(BetPrice(10, 0, BigDecimal("5.00"))),
            teams = listOf(alfa, beta),
        )
        val vm = BettingGameViewModel(20, GetBettingParametersUseCase {
            AppResult.Success(BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game)))
        }, object : AddBetToCartUseCase {
            override suspend fun invoke(draft: BetDraft): AppResult<ShoppingCart> {
                sends.incrementAndGet()
                return AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
            }
        })
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, {}, {}) } }
        compose.waitUntil(10_000) { vm.state.value.game != null }
        compose.runOnIdle { (1..10).forEach(vm::toggleNumber); vm.primaryAction() }
        compose.waitForIdle()

        val search = compose.onNodeWithContentDescription("Nome do time")
        search.assertIsDisplayed().performClick().assertIsFocused()
        capture("focus", vm, sends)

        search.performTextInput("Alfa")
        compose.waitUntil(10_000) { vm.state.value.teamQuery == "Alfa" }
        compose.onNodeWithContentDescription(alfa.name).assertIsDisplayed()
        compose.onNodeWithContentDescription(beta.name).assertDoesNotExist()
        search.assertIsFocused().assertTextEquals("Alfa")
        capture("result", vm, sends)

        search.performTextClearance()
        search.performTextInput("zzzz")
        compose.waitUntil(10_000) { vm.state.value.teamQuery == "zzzz" }
        compose.onNodeWithContentDescription(alfa.name).assertDoesNotExist()
        compose.onNodeWithContentDescription(beta.name).assertDoesNotExist()
        search.assertIsFocused()
        compose.runOnIdle { assertEquals(null, vm.state.value.selectedTeam) }
        capture("empty", vm, sends)

        search.performTextClearance()
        search.performTextInput("Alfa")
        compose.waitUntil(10_000) { vm.state.value.teamQuery == "Alfa" }
        compose.onNodeWithContentDescription(alfa.name).performClick()
        compose.waitUntil(10_000) { vm.state.value.selectedTeam == alfa }
        compose.runOnIdle {
            assertEquals("Time Alfa-SP", vm.state.value.teamQuery)
            assertEquals(0, sends.get())
        }
        search.assertTextEquals("Time Alfa-SP")
        compose.onNodeWithText("Time Alfa").assertIsDisplayed()
        capture("selected", vm, sends)
        // Para neste estado: a ação principal não é enviada ao carrinho.
    }

    private fun capture(state: String, vm: BettingGameViewModel, sends: AtomicInteger) {
        compose.waitForIdle()
        android.os.SystemClock.sleep(100)
        val folder = File(
            requireNotNull(InstrumentationRegistry.getInstrumentation().targetContext.externalCacheDir),
            "codex-parity",
        ).apply { mkdirs() }
        val name = "betting-20-search-$state"
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        File(folder, "$name.png").outputStream().use {
            assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
        }
        bitmap.recycle()
        File(folder, "$name.txt").writeText(
            "fixture=true\nnetworkSends=${sends.get()}\nmodalityId=20\nstate=$state\n" +
                "query=${vm.state.value.teamQuery}\nselectedTeam=${vm.state.value.selectedTeam?.number}\n" +
                "focusVerified=true\ninteraction=compose-semantics\n",
        )
        assertTrue(File(folder, "$name.png").length() > 0)
        assertTrue(File(folder, "$name.txt").length() > 0)
        assertEquals(0, sends.get())
    }
}
