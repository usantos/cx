package br.gov.caixa.loterias.feature.betting

import android.graphics.Bitmap
import android.view.View
import android.widget.LinearLayout
import android.widget.FrameLayout
import android.widget.Space
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.betting.AddBetToCartUseCase
import br.gov.caixa.loterias.core.domain.betting.BetDraft
import br.gov.caixa.loterias.core.domain.betting.BetPrice
import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BettingGameRouteSmokeTest {
    @get:Rule val compose = createComposeRule()

    @Test fun successfulAdditionStaysOnVolanteShowsAcknowledgementAndAllowsSecondBet() {
        val addBet = AddBetFake()
        val notifications = AtomicInteger(0)
        val game = LotteryGameParameters(
            modality = LotteryModality(9, "Mais Milionária", "MAIS_MILIONARIA"),
            contestNumber = 2900, drawDate = "10/09/2026", recurringOptions = listOf(2), maximumNumber = 50, minimumSelection = 6,
            maximumSelection = 6, minimumClovers = 2, maximumClovers = 6,
            maximumCloverNumber = 6, maximumSurprises = 3,
            prices = listOf(BetPrice(6, 2, BigDecimal("18.00"))),
        )
        val model = BettingGameViewModel(9, GetBettingParametersUseCase {
            AppResult.Success(BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game)))
        }, addBet)
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            BettingGameRoute(model, onBack = {}, onAdded = { notifications.incrementAndGet() })
        } }
        compose.waitUntil(10_000) { !model.state.value.loading }
        compose.onNodeWithText("+milionária").assertIsDisplayed()
        compose.runOnIdle { (1..6).forEach(model::toggleNumber); model.primaryAction() }
        compose.onNodeWithTag("betting-clover-1").assertIsDisplayed()
        saveDeviceCapture("compose-task5-plus-clovers")
        compose.runOnIdle { model.selectMode(BetEntryMode.SURPRISE); model.addToCart() }
        compose.waitUntil(10_000) { model.state.value.addedToCart }
        compose.onNodeWithText("Aposta adicionada ao carrinho com sucesso!").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(BetEntryMode.MANUAL, model.state.value.mode)
            assertEquals(1, addBet.calls.get())
            assertEquals(1, notifications.get())
        }
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("+milionária").assertIsDisplayed()
        compose.runOnIdle { model.selectMode(BetEntryMode.SURPRISE); model.addToCart() }
        compose.waitUntil(10_000) { addBet.calls.get() == 2 && model.state.value.addedToCart }
        compose.onNodeWithText("Aposta adicionada ao carrinho com sucesso!").assertIsDisplayed()
        compose.runOnIdle { assertEquals(2, notifications.get()) }
    }

    /** Exercises the Compose header, selectors, and help action without submitting a bet. */
    @Test fun megaSenaLoadsComposeHeaderAndSelectorsWithoutSubmittingABet() {
        val addBet = AddBetFake()
        val onHelp = AtomicInteger(0)
        val game = LotteryGameParameters(
            modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
            contestNumber = 2900,
            drawDate = "10/09/2026 20:00:00",
            maximumNumber = 60,
            minimumSelection = 6,
            maximumSelection = 7,
            maximumSurprises = 3,
            recurringOptions = listOf(2, 4),
            prices = listOf(BetPrice(6, 0, BigDecimal("5.00")), BetPrice(7, 0, BigDecimal("35.00"))),
        )
        val viewModel = BettingGameViewModel(
            modalityId = 2,
            getParameters = GetBettingParametersUseCase {
                AppResult.Success(BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game)))
            },
            addBet = addBet,
        )
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                BettingGameRoute(viewModel, onBack = {}, onAdded = {}, shakeEnabled = false, onHelp = { onHelp.incrementAndGet() })
            }
        }
        compose.waitUntil(timeoutMillis = 10_000) { !viewModel.state.value.loading && viewModel.state.value.game != null }
        compose.waitForIdle()
        compose.onNodeWithText("mega-sena").assertIsDisplayed()
        compose.onNodeWithText("10/09").assertIsDisplayed()
        compose.onNodeWithText(game.drawDate).assertDoesNotExist()
        compose.onNodeWithContentDescription("Dúvidas").assertIsDisplayed()
        compose.onNodeWithText("Como jogar").assertIsDisplayed()
        saveDeviceCapture("compose-surprise-switch-fixed")
        compose.onNodeWithTag("betting-surprise-switch-control", useUnmergedTree = true)
            .assertWidthIsEqualTo(45.dp)
            .assertHeightIsEqualTo(26.dp)
        compose.onNodeWithContentDescription("Dúvidas").performTouchInput { click(center) }
        compose.runOnIdle { assertEquals(1, onHelp.get()) }
        compose.onNodeWithText("Como Jogar: Mega-Sena").assertDoesNotExist()
        compose.onNodeWithText("Surpresinha").performTouchInput { click(center) }
        compose.runOnIdle { assertEquals(BetEntryMode.SURPRISE, viewModel.state.value.mode) }
        compose.onNodeWithText("Surpresinha").performTouchInput { click(center) }
        compose.runOnIdle { assertEquals(BetEntryMode.MANUAL, viewModel.state.value.mode) }
        compose.onNodeWithText("01").assertIsDisplayed().performClick()
        compose.onNodeWithText("Selecionados: 1/6").assertIsDisplayed()
        compose.runOnIdle {
            assertTrue(1 in viewModel.state.value.selectedNumbers)
            assertEquals(0, addBet.calls.get())
        }
    }

    @Test fun specialQuinaUsesTwoLineTitleAndBlocksRecurringDraws() {
        val game = LotteryGameParameters(
            modality = LotteryModality(3, "Quina", "QUINA", specialName = "Quina de São João"),
            contestNumber = 7325,
            drawDate = "07/10/2026 20:00:00",
            maximumNumber = 80,
            minimumSelection = 5,
            maximumSelection = 15,
            maximumSurprises = 3,
            recurringOptions = emptyList(),
            prices = listOf(BetPrice(5, 0, BigDecimal("3.00"))),
            contestType = "ESPECIAL",
        )
        val vm = BettingGameViewModel(
            modalityId = 3,
            getParameters = GetBettingParametersUseCase {
                AppResult.Success(BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game)))
            },
            addBet = AddBetFake(),
        )
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, {}, {}) } }
        compose.waitUntil(timeoutMillis = 10_000) { vm.state.value.game != null }
        saveDeviceCapture("compose-special-quina-entry")
        compose.onNodeWithText("quina de \nsão joão").assertIsDisplayed()
        compose.onNodeWithText("Teimosinha").performClick()
        compose.onNodeWithText("Por ser uma modalidade especial, esse concurso não permite teimosinhas.").assertIsDisplayed()
        compose.onNodeWithText("Teimosinhas").assertDoesNotExist()
    }

    @Test fun selectorDropdownCaretsKeepTheLegacySevenByFourDpSize() {
        val game = LotteryGameParameters(
            modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
            contestNumber = 2900,
            drawDate = "10/09/2026 20:00:00",
            maximumNumber = 60,
            minimumSelection = 6,
            maximumSelection = 6,
            maximumSurprises = 3,
            recurringOptions = listOf(2, 4),
            prices = listOf(BetPrice(6, 0, BigDecimal("5.00"))),
        )
        val vm = BettingGameViewModel(
            modalityId = 2,
            getParameters = GetBettingParametersUseCase {
                AppResult.Success(BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game)))
            },
            addBet = AddBetFake(),
        )
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, {}, {}) } }
        compose.waitUntil(timeoutMillis = 10_000) { vm.state.value.game != null }
        val carets = compose.onAllNodesWithTag("betting-dropdown-icon", useUnmergedTree = true)
        assertEquals(2, carets.fetchSemanticsNodes().size)
        carets[0].assertWidthIsEqualTo(7.dp).assertHeightIsEqualTo(4.dp)
        carets[1].assertWidthIsEqualTo(7.dp).assertHeightIsEqualTo(4.dp)
    }

    @Test fun entrySelectorsAndFooterActionsKeepInstalledDimensions() {
        val vm = megaSenaViewModel()
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, {}, {}) } }
        compose.waitUntil(timeoutMillis = 10_000) { vm.state.value.game != null }
        compose.onNodeWithTag("betting-button-6 números", useUnmergedTree = true).assertWidthIsEqualTo(112.dp).assertHeightIsEqualTo(45.dp)
        compose.onNodeWithTag("betting-button-Teimosinha", useUnmergedTree = true).assertWidthIsEqualTo(112.dp).assertHeightIsEqualTo(45.dp)
        // rodape_simula.xml is responsive: screen minus two24dp outer margins,
        // then a weight1 FrameLayout,8dp Space and130dp Limpar. Measure Android's
        // layout independently;197dp was valid only on the earlier device width.
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val density = context.resources.displayMetrics.density
        val windowWidth = compose.onRoot().fetchSemanticsNode().boundsInRoot.width.toInt()
        var nativeRandomWidth = 0
        compose.runOnIdle {
            fun pixels(dp: Int) = (dp * density + .5f).toInt()
            val row = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL }
            val random = FrameLayout(context)
            row.addView(random, LinearLayout.LayoutParams(0, pixels(52), 1f))
            row.addView(Space(context), LinearLayout.LayoutParams(pixels(8), 1))
            row.addView(View(context), LinearLayout.LayoutParams(pixels(130), pixels(52)))
            row.measure(View.MeasureSpec.makeMeasureSpec(windowWidth - 2 * pixels(24), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            nativeRandomWidth = random.measuredWidth
        }
        compose.onNodeWithTag("betting-button-Aposta aleatória", useUnmergedTree = true)
            .assertWidthIsEqualTo((nativeRandomWidth / density).dp).assertHeightIsEqualTo(52.dp)
        compose.onNodeWithTag("betting-button-Limpar", useUnmergedTree = true).assertWidthIsEqualTo(130.dp).assertHeightIsEqualTo(52.dp)
        compose.onNodeWithTag("betting-button-Adicionar ao carrinho", useUnmergedTree = true).assertHeightIsEqualTo(52.dp)
    }

    @Test fun emptyCartTapShowsLegacyStyleMessageAndDoesNotNavigate() {
        val onCart = AtomicInteger(0)
        val vm = megaSenaViewModel()
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                BettingGameRoute(
                    viewModel = vm,
                    onBack = {},
                    onAdded = {},
                    cartBetCount = 0,
                    onCart = { onCart.incrementAndGet() },
                )
            }
        }
        compose.waitUntil(timeoutMillis = 10_000) { vm.state.value.game != null }
        compose.onNodeWithTag("betting-cart-footer").performClick()
        compose.onNodeWithText(EMPTY_CART_MESSAGE).assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, onCart.get()) }
    }

    @Test fun failedParameterLoadKeepsRequestedModalityInTheHeader() {
        val vm = BettingGameViewModel(
            modalityId = 8,
            getParameters = GetBettingParametersUseCase { AppResult.Failure(AppError.Network) },
            addBet = AddBetFake(),
        )
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                BettingGameRoute(vm, onBack = {}, onAdded = {}, fallbackModalityId = 8)
            }
        }
        compose.waitUntil(timeoutMillis = 10_000) { !vm.state.value.loading && vm.state.value.game == null }
        compose.onNodeWithText("lotofácil").assertIsDisplayed()
        compose.onNodeWithText("Não foi possível carregar a aposta. Verifique sua internet.").assertIsDisplayed()
    }

    @Test fun cartWithExistingBetsNavigatesWithoutEmptyCartMessage() {
        val onCart = AtomicInteger(0)
        val vm = megaSenaViewModel()
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                BettingGameRoute(
                    viewModel = vm,
                    onBack = {},
                    onAdded = {},
                    cartBetCount = 1,
                    onCart = { onCart.incrementAndGet() },
                )
            }
        }
        compose.waitUntil(timeoutMillis = 10_000) { vm.state.value.game != null }
        compose.onNodeWithTag("betting-cart-footer").performClick()
        compose.runOnIdle { assertEquals(1, onCart.get()) }
        compose.onNodeWithText(EMPTY_CART_MESSAGE).assertDoesNotExist()
    }

    private class AddBetFake : AddBetToCartUseCase {
        val calls = AtomicInteger(0)
        override suspend fun invoke(draft: BetDraft): AppResult<ShoppingCart> {
            calls.incrementAndGet()
            return AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
        }
    }

    private fun megaSenaViewModel(): BettingGameViewModel {
        val game = LotteryGameParameters(
            modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
            contestNumber = 3068,
            drawDate = "08/10/2026 21:00:00",
            maximumNumber = 60,
            minimumSelection = 6,
            maximumSelection = 20,
            maximumSurprises = 3,
            recurringOptions = listOf(0, 2, 4),
            prices = listOf(BetPrice(6, 0, BigDecimal("6.00"))),
        )
        return BettingGameViewModel(
            modalityId = 2,
            getParameters = GetBettingParametersUseCase {
                AppResult.Success(BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game)))
            },
            addBet = AddBetFake(),
        )
    }

    private companion object {
        const val EMPTY_CART_MESSAGE = "Seu carrinho está vazio."
    }

    private fun saveDeviceCapture(name: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        val directory = File(requireNotNull(instrumentation.targetContext.externalCacheDir), "codex-parity").apply { mkdirs() }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(directory, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }
}
