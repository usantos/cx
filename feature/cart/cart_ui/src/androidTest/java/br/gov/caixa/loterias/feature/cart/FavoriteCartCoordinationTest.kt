package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewModelScope
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.createFeatureComposeRuleV2
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.route.FavoriteCartDetailRoute
import br.gov.caixa.loterias.feature.cart.presentation.route.FavoriteCartsRoute
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.FavoriteCartsViewModel
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FavoriteCartCoordinationTest {
    @get:Rule val compose = createFeatureComposeRuleV2()
    private val models = mutableListOf<FavoriteCartsViewModel>()

    @After fun cleanup() {
        models.forEach { it.viewModelScope.cancel() }
    }

    @Test fun privateFlagHidesLoadedCartsWithoutRefetchingOrShowingEmptyMessage() {
        val fixture = Fixture()
        val privateData = mutableStateOf(false)
        var back = 0
        var opened: Long? = null
        compose.setFeatureContent {
            val model = remember { fixture.create() }
            LoteriasCaixaDSTheme {
                FavoriteCartsRoute(model, { back++ }, { opened = it }, {}, onHelp = {}, privateDataEnabled = privateData.value)
            }
        }
        compose.onNodeWithText("Aqui estão todos os seus carrinhos favoritos.").assertIsDisplayed()
        compose.onNodeWithText("Favorito").assertDoesNotExist()
        compose.onNodeWithText("Você ainda não possui carrinhos favoritos.").assertDoesNotExist()
        compose.onNodeWithContentDescription("Ajuda").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, fixture.listReads)
            privateData.value = true
        }
        compose.onNodeWithText("Favorito").assertIsDisplayed()
        compose.onNodeWithContentDescription("Detalhar carrinho").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, fixture.listReads)
            assertEquals(1L, opened)
            assertEquals(1, back)
        }
    }

    @Test fun importingConsumesFlagBeforeNavigationAndRepeatedImportNeedsConsent() {
        val fixture = Fixture()
        var ready = 0
        compose.setFeatureContent {
            val model = remember { fixture.create() }
            LoteriasCaixaDSTheme {
                FavoriteCartsRoute(model, {}, {}, {
                    assertFalse(model.state.value.imported)
                    ready++
                })
            }
        }
        compose.onNodeWithContentDescription("Transformar carrinho").performClick()
        compose.runOnIdle {
            assertEquals(1, ready)
            assertEquals(listOf(1L to 0), fixture.imports)
        }
        compose.onNodeWithContentDescription("Transformar carrinho").performClick()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle {
            assertEquals(1, ready)
            assertNull(fixture.model.state.value.pendingImportCartId)
        }
        compose.onNodeWithContentDescription("Transformar carrinho").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle {
            assertEquals(2, ready)
            assertEquals(listOf(1L to 0, 1L to 0), fixture.imports)
        }
    }

    @Test fun specialSelectionNeedsAnOptionAndCancellationResetsTheRememberedChoice() {
        val fixture = Fixture().apply { bets = listOf(normal, normal.copy(id = 11, modality = "Mega da Virada", special = true)) }
        var ready = 0
        compose.setFeatureContent {
            val model = remember { fixture.create() }
            LoteriasCaixaDSTheme { FavoriteCartsRoute(model, {}, {}, { ready++ }) }
        }
        compose.onNodeWithContentDescription("Transformar carrinho").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle {
            assertTrue(fixture.imports.isEmpty())
            assertTrue(fixture.model.state.value.specialSelectionRequired)
        }
        compose.onNodeWithText("Mega da Virada").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { assertNull(fixture.model.state.value.pendingImportCartId) }
        compose.onNodeWithContentDescription("Transformar carrinho").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle {
            assertTrue(fixture.imports.isEmpty())
            assertTrue(fixture.model.state.value.specialSelectionRequired)
        }
        compose.onNodeWithText("Ambas").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle {
            assertEquals(1, ready)
            assertEquals(listOf(1L to 0), fixture.imports)
        }
    }

    @Test fun replacingViewModelUpdatesRowActionsAndConfirmationForTheSameCartId() {
        val first = Fixture()
        val second = Fixture()
        val replace = mutableStateOf(false)
        compose.setFeatureContent {
            val old = remember { first.create() }
            val current = remember { second.create() }
            LoteriasCaixaDSTheme { FavoriteCartsRoute(if (replace.value) current else old, {}, {}, {}) }
        }
        compose.onNodeWithText("Favorito").performClick()
        compose.onNodeWithText("6 num(s)").assertIsDisplayed()
        compose.runOnIdle { replace.value = true }
        compose.waitForIdle()
        compose.onNodeWithText("6 num(s)").assertDoesNotExist()
        compose.onNodeWithText("Favorito").performClick()
        compose.onNodeWithText("6 num(s)").assertIsDisplayed()
        compose.onNodeWithContentDescription("Excluir carrinho").performClick()
        compose.runOnIdle {
            assertNull(first.model.state.value.pendingCartRemoval)
            assertEquals(1L, second.model.state.value.pendingCartRemoval)
        }
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithContentDescription("Excluir carrinho").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Você ainda não possui carrinhos favoritos.").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, first.model.state.value.carts.size)
            assertTrue(second.model.state.value.carts.isEmpty())
        }
    }

    @Test fun detailRetryHelpSystemBackAndLastBetDeletionKeepTheirOriginalDestinations() {
        val fixture = Fixture().apply { failFirstDetail = true }
        var back = 0
        var help = 0
        compose.setFeatureContent {
            val model = remember { fixture.create(detail = true) }
            LoteriasCaixaDSTheme { FavoriteCartDetailRoute(model, { back++ }, { help++ }) }
        }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("01 - 02 - 03 - 04 - 05 - 06").assertIsDisplayed()
        compose.onNodeWithContentDescription("Ajuda").performClick()
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressBack()
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, help)
            assertEquals(1, back)
        }
        compose.onNodeWithContentDescription("Excluir aposta").performClick()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertFalse(fixture.model.state.value.cartDeleted) }
        compose.onNodeWithContentDescription("Excluir aposta").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle {
            assertTrue(fixture.model.state.value.cartDeleted)
            assertTrue(fixture.model.state.value.bets.isEmpty())
            assertEquals(2, back)
        }
    }

    private inner class Fixture {
        var carts = listOf(FavoriteCart(1, "Favorito"))
        var bets = listOf(normal)
        var listReads = 0
        var failFirstDetail = false
        val imports = mutableListOf<Pair<Long, Int>>()
        lateinit var model: FavoriteCartsViewModel
        fun create(detail: Boolean = false) = FavoriteCartsViewModel(
            cartId = if (detail) 1 else null,
            getFavoriteCarts = {
                listReads++
                AppResult.Success(carts)
            },
            getFavoriteCartBets = {
                if (failFirstDetail) {
                    failFirstDetail = false
                    AppResult.Failure(AppError.Network)
                } else {
                    AppResult.Success(bets)
                }
            },
            deleteFavoriteCart = { id ->
                carts = carts.filterNot { it.id == id }
                AppResult.Success(carts)
            },
            deleteFavoriteCartBet = { _, id ->
                bets = bets.filterNot { it.id == id }
                AppResult.Success(bets)
            },
            addFavoriteCartToCurrent = { id, selection ->
                imports.add(id to selection)
                AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList()))
            },
            dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        ).also {
            model = it
            models.add(it)
        }
    }

    companion object {
        private val normal = FavoriteCartBet(10, "Mega-Sena", listOf(1, 2, 3, 4, 5, 6), modalityId = 2)
    }
}
