package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.feature.betting.domain.usecase.GenerateRapidBetsUseCase
import br.gov.caixa.loterias.feature.betting.presentation.route.RapidListRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.RapidListViewModel
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class RapidGeneratedCardsUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun generatedNumbersNormalizeHundredAndCanRevealAndHideDetailPanel() {
        val fixture = Fixture()
        show(fixture)
        compose.onNodeWithText("01 - 00").assertExists()
        compose.onNodeWithText("Conc. 123").assertExists()
        compose.onNodeWithText("6 números").assertIsNotDisplayed()
        card().performCustomAccessibilityActionWithLabel("Mostrar detalhes")
        compose.onNodeWithText("6 números").assertIsDisplayed()
        compose.onNodeWithText("3 sorteios").assertIsDisplayed()
        card().performCustomAccessibilityActionWithLabel("Fechar detalhes")
        compose.onNodeWithText("6 números").assertIsNotDisplayed()
    }

    @Test fun generatedCardAccessibilityRemovalUsesTheActualItem() {
        val fixture = Fixture()
        show(fixture)
        card().performCustomAccessibilityActionWithLabel("Excluir aposta")
        compose.onNodeWithText("Nenhuma aposta foi gerada.").assertExists()
        compose.runOnIdle { assertEquals(listOf(fixture.item), fixture.removed) }
    }

    @Test fun generatedCardLongPressOffersRemovalAndDismissesMenuAfterMutation() {
        val fixture = Fixture()
        show(fixture)
        compose.onNodeWithText("mega-sena").performTouchInput { longClick() }
        compose.onNodeWithText("Excluir aposta").performClick()
        compose.onNodeWithText("Excluir aposta").assertDoesNotExist()
        compose.onNodeWithText("Nenhuma aposta foi gerada.").assertExists()
        compose.runOnIdle { assertEquals(listOf(fixture.item), fixture.removed) }
    }

    @Test fun generatedCardSwipeOpensAndClosesItsDetails() {
        show(Fixture())
        card().performTouchInput { swipeRight() }
        compose.onNodeWithText("6 números").assertIsDisplayed()
        card().performTouchInput { swipeLeft() }
        compose.onNodeWithText("6 números").assertIsNotDisplayed()
    }

    @Test fun generationRetryReplacesEmptyStateWithReturnedBets() {
        val fixture = Fixture().apply { emptyFirst = true }
        show(fixture)
        compose.onNodeWithText("Nenhuma aposta foi gerada.").assertExists()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("O jeito mais fácil para apostar").assertExists()
        compose.onNodeWithText("mega-sena").assertExists()
        compose.runOnIdle { assertEquals(2, fixture.generated) }
    }

    @Test fun failedRemovalKeepsCardAndCanAcknowledgeServerFailure() {
        val fixture = Fixture().apply { removeFails = true }
        show(fixture)
        card().performCustomAccessibilityActionWithLabel("Excluir aposta")
        compose.onNodeWithText("Não foi possível atualizar as apostas do Rapidão. Verifique sua internet.").assertExists()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("mega-sena").assertExists()
        compose.onNodeWithText("Entendi").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf(fixture.item), fixture.removed) }
    }

    @Test fun descriptionFallbackAndSingularDetailLabelsArePreserved() {
        val fixture = Fixture().apply {
            item =
                item.copy(
                    selectedNumbers = emptyList(),
                    description = "Surpresinha",
                    numberQuantity = 1,
                    recurringDraws = 0,
                    complementaryLabel = null
                )
        }
        show(fixture)
        compose.onNodeWithText("Surpresinha").assertExists()
        card().performCustomAccessibilityActionWithLabel("Mostrar detalhes")
        compose.onNodeWithText("1 número").assertIsDisplayed()
        compose.onNodeWithText("1 sorteio").assertIsDisplayed()
    }

    private fun card() = compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.CustomActions))
    private fun show(fixture: Fixture) {
        val vm = RapidListViewModel(
            GenerateRapidBetsUseCase {
                fixture.generated++
                ServiceResult.Success(
                    if (fixture.emptyFirst &&
                        fixture.generated == 1
                    ) {
                        ShoppingCart(1, BigDecimal.ZERO, emptyList())
                    } else {
                        ShoppingCart(1, BigDecimal.TEN, listOf(fixture.item))
                    }
                )
            },
            RemoveCartItemUseCase {
                fixture.removed.add(it)
                if (fixture.removeFails) {
                    AppResult.Failure(
                        AppError.Network
                    )
                } else {
                    AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
                }
            },
            ValidateCartUseCase {
                AppResult.Success(CartValidationResult.Accepted)
            },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate),
            autoLoad = false
        )
        compose.setContent { CaixaDSTheme { RapidListRoute(vm, {}, {}, {}) } }
        compose.runOnIdle { vm.generate() }
    }
    private class Fixture {
        var item =
            CartItem(
                9, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "", BigDecimal.TEN, "2", 123, 6, 3,
                listOf(
                    listOf(1, 100)
                ),
                complementaryLabel = "Complemento"
            )
        var generated = 0
        var emptyFirst = false
        var removeFails = false
        val removed = mutableListOf<CartItem>()
    }
}
