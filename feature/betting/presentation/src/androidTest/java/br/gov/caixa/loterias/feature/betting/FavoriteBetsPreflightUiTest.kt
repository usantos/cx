package br.gov.caixa.loterias.feature.betting

import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.betting.presentation.route.FavoriteBetsRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.FavoriteBetsViewModel
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetCartOptions
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FavoriteBetsPreflightUiTest {
    @get:Rule val compose = createComposeRule()
    private val models = mutableListOf<FavoriteBetsViewModel>()
    private val modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA")
    private val favorite = FavoriteBet(9, 2, "Mega-Sena", "Minha Mega", listOf(listOf(1, 2, 3, 4, 5, 6)))
    private val game = LotteryGameParameters(
        modality, 2900, "", 60, 6, 20, 10, listOf(0, 2),
        listOf(BetPrice(6, 0, BigDecimal("6.00")))
    )
    private val limitMessage = "O valor máximo de apostas permitida diariamente será excedido com a inclusão dessa aposta. " +
        "Deseja prosseguir com a inclusão da aposta?"
    private val duplicateMessage = "Existe uma aposta idêntica em seu carrinho. Deseja prosseguir com a inclusão?"

    @After fun cleanup() = models.forEach { it.viewModelScope.cancel() }

    @Test fun dailyLimitAndDuplicateRequireSeparateConfirmationsBeforePicker() {
        val fixture = Fixture()
        show(fixture)
        compose.onNodeWithContentDescription("Adicionar aposta").performClick()
        compose.onNodeWithText(limitMessage).assertIsDisplayed()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText(duplicateMessage).assertIsDisplayed()
        compose.onNodeWithText("Adicionar ao carrinho").assertDoesNotExist()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertTrue(fixture.added.isEmpty()) }
        compose.onNodeWithContentDescription("Adicionar aposta").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Quantidade de teimosinhas").assertIsDisplayed()
        compose.onNodeWithText("Adicionar ao carrinho").assertIsEnabled().performClick()
        compose.onNodeWithText("Aposta favorita inserida com sucesso").assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(FavoriteBetCartOptions(1, 0, false)), fixture.added) }
    }

    @Test fun cancellingDailyLimitNeverReachesDuplicateOrSubmission() {
        val fixture = Fixture()
        show(fixture)
        compose.onNodeWithContentDescription("Adicionar aposta").performClick()
        compose.onNodeWithText(limitMessage).assertIsDisplayed()
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithText(duplicateMessage).assertDoesNotExist()
        compose.onNodeWithText("Adicionar ao carrinho").assertDoesNotExist()
        compose.runOnIdle { assertTrue(fixture.added.isEmpty()) }
    }

    @Test fun suspendedAccountShowsNoticeWithoutCartOrSubmission() {
        val fixture = Fixture(suspended = true)
        show(fixture)
        compose.onNodeWithContentDescription("Adicionar aposta").performClick()
        compose.onNodeWithText("Há uma suspensão temporária ativa", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals(0, fixture.cartReads)
            assertTrue(fixture.added.isEmpty())
            assertEquals(1, fixture.exits)
        }
    }

    @Test fun specialOnlyPostsDirectlyWithNoMirrorQuerySelection() {
        val fixture = Fixture(special = true)
        show(fixture)
        compose.onNodeWithContentDescription("Adicionar aposta").performClick()
        compose.onNodeWithText("Aposta favorita inserida com sucesso").assertIsDisplayed()
        compose.onNodeWithText("Tipo de Concurso").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf(FavoriteBetCartOptions(2, 0, null)), fixture.added) }
    }

    private fun show(fixture: Fixture) = compose.setContent {
        val model = remember { fixture.create() }
        CaixaDSTheme { FavoriteBetsRoute(model, { fixture.exits++ }, {}, {}) }
    }

    private inner class Fixture(val suspended: Boolean = false, val special: Boolean = false) {
        val added = mutableListOf<FavoriteBetCartOptions>()
        var cartReads = 0
        var exits = 0
        private val cart = ShoppingCart(
            1,
            if (special) BigDecimal.ZERO else BigDecimal("5.00"),
            if (special) {
                emptyList()
            } else {
                listOf(
                    CartItem(
                        4,
                        CartItemKind.INDIVIDUAL_BET,
                        "Mega-Sena",
                        "",
                        BigDecimal("6.00"),
                        modalityCode = "MEGA_SENA",
                        selectedNumbers = listOf(listOf(6, 5, 4, 3, 2, 1))
                    )
                )
            }
        )
        fun create() = FavoriteBetsViewModel(
            getFavorites = { AppResult.Success(listOf(favorite)) },
            getParameters = {
                AppResult.Success(
                    BettingParameters(
                        BigDecimal.TEN,
                        BigDecimal.ONE,
                        10,
                        listOf(if (special) game.copy(contestType = "ESPECIAL") else game)
                    )
                )
            },
            deleteFavorite = { AppResult.Success(Unit) },
            addFavoriteToCart = { _, options ->
                added += options
                AppResult.Success(cart)
            },
            dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
            getCurrentCart = {
                cartReads++
                AppResult.Success(cart)
            },
            verifyAccountAccess = { AppResult.Success(AccountAccess(PostLoginDestination.HOME, suspended, BigDecimal.TEN)) }
        ).also { models += it }
    }
}
