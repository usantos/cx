package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.cart.*
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

class FavoriteCartsIntegrationTest {
    @get:Rule val compose = createComposeRule()
    private val calls = mutableListOf<String>()
    private val normal = FavoriteCartBet(10, "Mega-Sena", listOf(1, 2, 3, 4, 5, 6), recurringDraws = 2, modalityId = 2)

    @Test fun listExpandsCollapsesOpensDetailAndDeletesOnlyAfterConfirmation() {
        show()
        compose.onNodeWithText("Meu favorito").performClick()
        compose.onNodeWithText("6 num(s)").assertIsDisplayed()
        compose.onNodeWithContentDescription("Teimosinha").assertIsDisplayed()
        compose.onNodeWithText("Meu favorito").performClick()
        compose.onNodeWithText("6 num(s)").assertDoesNotExist()
        compose.onNodeWithContentDescription("Detalhar carrinho").performClick()
        compose.onNodeWithContentDescription("Excluir carrinho").performClick()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertEquals(listOf("detail:1"), calls) }
        compose.onNodeWithContentDescription("Excluir carrinho").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Você ainda não possui carrinhos favoritos.").assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf("detail:1", "deleteCart:1"), calls) }
    }

    @Test fun importingPreviouslyUsedCartRequiresConfirmationAndPreservesContestSelection() {
        show(bets = listOf(normal, normal.copy(id = 11, modality = "Mega da Virada", special = true)), previouslyUsed = true)
        compose.onNodeWithContentDescription("Transformar carrinho").performClick()
        compose.onNodeWithText("Este carrinho favorito já foi adicionado ao carrinho atual. Deseja adicioná-lo novamente?").assertIsDisplayed()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertEquals(emptyList<String>(), calls) }
        compose.onNodeWithContentDescription("Transformar carrinho").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Mega da Virada").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle { assertEquals(listOf("import:1:2", "cart"), calls) }
    }

    @Test fun detailPreservesNumbersAndLastBetRemovalDeletesCartAndReturns() {
        show(detail = true)
        compose.onNodeWithText("Favoritas").assertIsDisplayed()
        compose.onNodeWithContentDescription("Termos de Uso").performClick()
        compose.onNodeWithText("01 - 02 - 03 - 04 - 05 - 06").assertIsDisplayed()
        compose.onNodeWithText("Adicionar ao carrinho").assertDoesNotExist()
        compose.onNodeWithContentDescription("Excluir aposta").performClick()
        compose.onNodeWithText("Deseja realmente excluir esta aposta?").assertIsDisplayed()
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithContentDescription("Excluir aposta").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle { assertEquals(listOf("help", "deleteCart:1", "back"), calls) }
    }

    @Test fun detailKeepsSurpriseCountsAndDeletingOneBetRetainsTheOther() {
        show(detail = true, bets = listOf(
            normal.copy(id = 10, modality = "+Milionária", modalityId = 9, numbers = emptyList(), numberCount = 6,
                cloverCount = 2, surprise = true),
            normal.copy(id = 11, modality = "Timemania", modalityId = 20, favoriteTeam = "FLAMENGO/RJ"),
        ))
        compose.onNodeWithText("XX - XX - XX - XX - XX - XX").assertIsDisplayed()
        compose.onNodeWithText("Trevos:  XX - XX").assertIsDisplayed()
        compose.onNodeWithText("FLAMENGO/RJ").assertIsDisplayed()
        compose.onAllNodesWithContentDescription("Excluir aposta").onFirst().performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("FLAMENGO/RJ").assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf("deleteBet:1:10"), calls) }
    }

    @Test fun detailKeepsSuperSevenColumnsAndLuckyMonth() {
        show(detail = true, bets = listOf(
            normal.copy(modality = "Super Sete", modalityId = 7, numberColumns = listOf(listOf(0, 3), listOf(4), listOf(6), listOf(7), listOf(8), listOf(9), listOf(2))),
            normal.copy(id = 11, modality = "Dia de Sorte", modalityId = 11, luckyMonth = "Setembro"),
            normal.copy(id = 12, modality = "+Milionária", modalityId = 9, clovers = listOf(2, 4), cloverCount = 2),
        ))
        compose.onNodeWithText("super sete").assertIsDisplayed()
        compose.onNodeWithText("0").assertIsDisplayed()
        compose.onNodeWithText("Setembro").assertIsDisplayed()
        compose.onNodeWithText("Trevos:  2 - 4").assertIsDisplayed()
    }

    @Test fun loadFailureCanBeRetriedWithoutPretendingSuccess() {
        show(failFirst = true)
        compose.onNodeWithText("Não foi possível carregar os carrinhos favoritos. Verifique sua internet.").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Meu favorito").assertIsDisplayed()
    }

    @Test fun expansionWaitsForTheRequestedCartBets() {
        val ready = kotlinx.coroutines.CompletableDeferred<Unit>()
        show(betsReady = ready)
        compose.onNodeWithText("Meu favorito").performClick()
        compose.onNodeWithText("6 num(s)").assertDoesNotExist()
        compose.runOnIdle { ready.complete(Unit) }
        compose.onNodeWithText("6 num(s)").assertIsDisplayed()
    }

    private fun show(detail: Boolean = false, bets: List<FavoriteCartBet> = listOf(normal), previouslyUsed: Boolean = false, failFirst: Boolean = false, betsReady: kotlinx.coroutines.CompletableDeferred<Unit>? = null) {
        var saved = bets
        var first = true
        compose.setContent {
            val model = remember {
                FavoriteCartsViewModel(
                    cartId = if (detail) 1 else null,
                    getFavoriteCarts = {
                        if (failFirst && first) { first = false; AppResult.Failure(AppError.Network) }
                        else AppResult.Success(listOf(FavoriteCart(1, "Meu favorito")))
                    },
                    getFavoriteCartBets = { betsReady?.await(); AppResult.Success(saved) },
                    deleteFavoriteCart = { calls.add("deleteCart:$it"); AppResult.Success(emptyList()) },
                    deleteFavoriteCartBet = { cart, bet -> calls.add("deleteBet:$cart:$bet"); saved = saved.filterNot { it.id == bet }; AppResult.Success(saved) },
                    addFavoriteCartToCurrent = { cart, selection -> calls.add("import:$cart:$selection"); AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList())) },
                    wasFavoriteCartAdded = { previouslyUsed },
                    dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                )
            }
            CaixaDSTheme(darkTheme = false) {
                if (detail) FavoriteCartDetailRoute(model, onBack = { calls.add("back") }, onHelp = { calls.add("help") })
                else FavoriteCartsRoute(model, onBack = { calls.add("back") }, onOpen = { calls.add("detail:$it") }, onCartReady = { calls.add("cart") })
            }
        }
    }
}
