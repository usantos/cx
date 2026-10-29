package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import br.gov.caixa.loterias.feature.cart.presentation.route.CartAdditionRoute
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAdditionKind
import br.gov.caixa.loterias.feature.cart.presentation.state.CountdownTicker
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartAdditionViewModel

class CartAdditionCoordinationUiTest {
    @get:Rule val compose = createComposeRule()
    private val models = mutableListOf<CartAdditionViewModel>()
    private val origin = Instant.parse("2026-10-18T15:00:00Z")
    private val device get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    @After fun cleanup() { models.forEach { it.viewModelScope.cancel() } }

    @Test fun resumeUsesElapsedServerTimeAndExpirationKeepsConfirmationAndActions() {
        var now = origin
        lateinit var owner: Owner
        lateinit var model: CartAdditionViewModel
        val calls = mutableListOf<String>()
        compose.setContent {
            val lifecycleOwner = remember { Owner().also { owner = it } }
            val viewModel = remember {
                model(CartAdditionKind.POOL_SHARE, "QUINA", serverTime = ServerTimeSource { now }).also { model = it }
            }
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                CaixaDSTheme(darkTheme = false) {
                    CartAdditionRoute(viewModel, { calls += "back" }, { calls += "home" }, { calls += "cart" },
                        { id, modality -> calls += "help:$id:$modality" })
                }
            }
        }
        compose.onNodeWithText("02:00").assertIsDisplayed()
        compose.runOnIdle {
            owner.registry.currentState = Lifecycle.State.CREATED
            now = origin.plusSeconds(75)
        }
        compose.runOnIdle { owner.registry.currentState = Lifecycle.State.RESUMED }
        compose.onNodeWithText("00:45").assertIsDisplayed()
        compose.runOnIdle {
            owner.registry.currentState = Lifecycle.State.CREATED
            now = origin.plusSeconds(130)
        }
        compose.runOnIdle { owner.registry.currentState = Lifecycle.State.RESUMED }
        compose.onNodeWithText("00:00").assertIsDisplayed()
        compose.onNodeWithText("Bolão inserido no\ncarrinho com sucesso.").assertIsDisplayed()
        compose.runOnIdle { assertTrue(model.state.value.expired); assertTrue(calls.isEmpty()) }
        compose.onNodeWithText("Carrinho\nde apostas", useUnmergedTree = true).performClick()
        compose.onNodeWithContentDescription("Dúvidas").performClick()
        compose.onNodeWithText("Voltar ao início").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        device.pressBack()
        compose.runOnIdle { assertEquals(listOf("cart", "help:3:QUINA", "home", "back", "back"), calls) }
    }

    @Test fun replacingViewModelUpdatesKindHeaderMinimumAmountTypographyAndBackCallback() {
        val selected = mutableIntStateOf(0)
        val calls = mutableListOf<String>()
        compose.setContent {
            val viewModels = remember {
                listOf(model(CartAdditionKind.BET, "COMBO", contest = 0, amount = "999.99"),
                    model(CartAdditionKind.BET, "", contest = 1234, amount = "1000", minimum = "30"),
                    model(CartAdditionKind.POOL_SHARE, "MEGA_SENA", minimum = "99"))
            }
            val index = selected.intValue
            CaixaDSTheme(darkTheme = false) {
                CartAdditionRoute(viewModels[index], { calls += "back:$index" }, {}, {}, { _, _ -> })
            }
        }
        compose.onNodeWithText("Combo inserido no\ncarrinho com sucesso.").assertIsDisplayed()
        compose.onNodeWithText("Concurso").assertDoesNotExist()
        compose.onNodeWithText("Sorteio").assertDoesNotExist()
        compose.onNodeWithContentDescription("Dúvidas").assertDoesNotExist()
        compose.onNodeWithText("Lembre-se:", substring = true).assertDoesNotExist()
        assertAmountFont("999,99", 14f)
        device.pressBack()
        compose.runOnIdle { selected.intValue = 1 }
        compose.onNodeWithText("bolão").assertIsDisplayed()
        compose.onNodeWithText("1234").assertIsDisplayed()
        compose.onNodeWithText("19/10").assertIsDisplayed()
        compose.onNodeWithText("Aposta inserida no\ncarrinho com sucesso.").assertIsDisplayed()
        compose.onNodeWithText("Lembre-se: o valor mínimo", substring = true).assertIsDisplayed()
        assertAmountFont("1.000,00", 9.8f)
        compose.runOnIdle { selected.intValue = 2 }
        compose.onNodeWithText("Bolão inserido no\ncarrinho com sucesso.").assertIsDisplayed()
        compose.onNodeWithText("Aposta inserida no\ncarrinho com sucesso.").assertDoesNotExist()
        compose.onNodeWithText("Lembre-se:", substring = true).assertDoesNotExist()
        compose.onNodeWithText("02:00").assertIsDisplayed()
        device.pressBack()
        compose.runOnIdle { assertEquals(listOf("back:0", "back:2"), calls) }
    }

    @Test fun helpAliasesKeepBackendIdAndOriginalModalityAndUnknownOrAbsentHelpStaysHidden() {
        val examples = listOf("MEGA_SENA" to 2, "Mega-da-Virada" to 2,
            "QUINA" to 3, "Quina de São João" to 3, "SUPER_SETE" to 7, "SUPER-7" to 7,
            "LOTOFACIL" to 8, "Lotofácil da Independência" to 8, "+milionária" to 9, "MAIS_MILIONARIA" to 9,
            "DIA_DE_SORTE" to 11, "LOTOGOL" to 14, "LOTOMANIA" to 16, "DUPLA_SENA" to 18,
            "Dupla de Páscoa" to 18, "LOTECA" to 19, "TIMEMANIA" to 20)
        val selected = mutableIntStateOf(0)
        val helpEnabled = mutableStateOf(true)
        val calls = mutableListOf<Pair<Int, String>>()
        compose.setContent {
            val viewModels = remember { (examples.map { it.first } + "COMBO").map { model(CartAdditionKind.BET, it) } }
            CaixaDSTheme(darkTheme = false) {
                CartAdditionRoute(viewModels[selected.intValue], {}, {}, {},
                    onHelp = if (helpEnabled.value) ({ id, modality -> calls += id to modality }) else null)
            }
        }
        examples.forEachIndexed { index, _ ->
            compose.runOnIdle { selected.intValue = index }
            compose.onNodeWithContentDescription("Dúvidas").performClick()
        }
        compose.runOnIdle { assertEquals(examples.map { it.second to it.first }, calls); selected.intValue = examples.size }
        compose.onNodeWithContentDescription("Dúvidas").assertDoesNotExist()
        compose.runOnIdle { selected.intValue = 0; helpEnabled.value = false }
        compose.onNodeWithContentDescription("Dúvidas").assertDoesNotExist()
    }

    private fun assertAmountFont(value: String, size: Float) {
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(value, substring = true, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(size, layouts.single().layoutInput.style.fontSize.value, 0.01f)
    }

    private fun model(kind: CartAdditionKind, modality: String, contest: Int = 2800, amount: String = "5",
        minimum: String = "0", serverTime: ServerTimeSource = ServerTimeSource { origin },
    ) = CartAdditionViewModel(
        kind, serverTime, expiresAt = origin.plusSeconds(120).toString(),
        modality = modality, contest = contest, drawDate = "2026-10-19T20:00:00-03:00",
        amount = BigDecimal(amount), minimumCartAmount = BigDecimal(minimum),
        ticker = CountdownTicker { awaitCancellation() }
    ).also { models += it }

    private class Owner : LifecycleOwner {
        val registry = LifecycleRegistry(this).apply { currentState = Lifecycle.State.RESUMED }
        override val lifecycle: Lifecycle get() = registry
    }
}
