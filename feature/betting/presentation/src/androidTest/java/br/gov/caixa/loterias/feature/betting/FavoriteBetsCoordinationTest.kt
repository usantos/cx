package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.assertCountEquals

import androidx.compose.ui.test.isDialog

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetCartOptions
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.presentation.route.FavoriteBetsRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.FavoriteBetsViewModel

class FavoriteBetsCoordinationTest {
    @get:Rule val compose = createComposeRule()
    private val models = mutableListOf<FavoriteBetsViewModel>()
    private val quina = LotteryModality(3, "Quina", "QUINA", "Quina Especial")
    private val mega = LotteryModality(2, "Mega-Sena", "MEGA_SENA")
    private val lotomania = LotteryModality(16, "Lotomania", "LOTOMANIA")
    private val loteca = LotteryModality(19, "Loteca", "LOTECA")

    @After fun cleanup() {
        models.forEach { it.viewModelScope.cancel() }
    }

    @Test fun filterIsAppliedExplicitlyAndEmptyModalityCreatesTheSelectedFavorite() {
        val fixture = Fixture()
        val created = mutableListOf<Int>()
        compose.setContent {
            val model = remember { fixture.create() }
            CaixaDSTheme { FavoriteBetsRoute(model, {}, {}, { created += it }) }
        }
        compose.onNodeWithContentDescription("Selecionar modalidade").performClick()
        dialogText("loteca").assertDoesNotExist()
        dialogText("mega-sena").performClick()
        dialogText("Cancelar").performClick()
        compose.runOnIdle {
            assertNull(fixture.model.state.value.selectedModalityId)
            assertEquals(listOf<Int?>(null), fixture.filters)
        }
        compose.onNodeWithContentDescription("Selecionar modalidade").performClick()
        dialogText("mega-sena").performClick()
        dialogText("Filtrar").performClick()
        compose.onNodeWithText("Você ainda não tem apostas favoritas desta modalidade. Que tal adicionar uma agora?").assertExists()
        compose.onNodeWithText("Incluir aposta favorita").performClick()
        compose.runOnIdle {
            assertEquals(listOf<Int?>(null, 2), fixture.filters)
            assertEquals(listOf(2), created)
        }
    }

    @Test fun singleNormalContestKeepsTheFirstRecurringValueAndMirrorUntilExplicitAdd() {
        val fixture = Fixture(listOf(favorite(1, lotomania)), listOf(game(lotomania, recurring = listOf(4, 2, 4))))
        compose.setContent {
            val model = remember { fixture.create() }
            CaixaDSTheme { FavoriteBetsRoute(model, {}, {}, {}) }
        }
        compose.onNodeWithContentDescription("Adicionar aposta").performClick()
        compose.onNodeWithText("Adicionar ao carrinho").assertIsEnabled()
        compose.onNodeWithText("Tipo de Concurso").assertDoesNotExist()
        compose.onNodeWithText("Quantidade de teimosinhas").performClick()
        compose.onNodeWithText("Ao selecionar esta opção", substring = true).performClick()
        compose.runOnIdle {
            assertEquals(listOf(4, 2), fixture.model.state.value.recurringOptions)
            assertEquals(4, fixture.model.state.value.recurringDraws)
            assertTrue(fixture.model.state.value.mirror)
            assertTrue(fixture.added.isEmpty())
        }
        compose.onNodeWithText("Adicionar ao carrinho").performClick()
        compose.onNodeWithText("Aposta favorita inserida com sucesso").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals(listOf(1L to FavoriteBetCartOptions(1, 4, true)), fixture.added)
            assertFalse(fixture.model.state.value.addedToCart)
        }
    }

    @Test fun openedSpecialChoicesSurviveViewModelReplacementAndUseCurrentConfirmation() {
        val first = Fixture(listOf(favorite(1, quina)), listOf(game(quina), game(quina, "ESPECIAL")))
        val second = Fixture(listOf(favorite(2, quina)), listOf(game(quina), game(quina, "ESPECIAL")))
        val selected = mutableIntStateOf(0)
        compose.setContent {
            val old = remember { first.create() }
            val current = remember {
                second.create().also {
                    it.requestAdd(second.favorites.single())
                    it.setContestType(2)
                }
            }
            CaixaDSTheme {
                FavoriteBetsRoute(
                    if (selected.intValue == 0) old else current,
                    {},
                    {},
                    {},
                    privateDataEnabled = selected.intValue == 0
                )
            }
        }
        compose.onNodeWithContentDescription("Adicionar aposta").performClick()
        compose.onNodeWithText("Adicionar ao carrinho").assertIsNotEnabled()
        compose.onNodeWithText("Tipo de Concurso").performClick()
        compose.runOnIdle { first.model.setContestType(2) }
        compose.onNodeWithText("Quina Especial").assertExists()
        compose.onNodeWithText("Adicionar ao carrinho").assertIsEnabled()
        compose.runOnIdle { selected.intValue = 1 }
        compose.onNodeWithText("Quina Especial").assertExists()
        compose.onNodeWithText("Adicionar ao carrinho").assertIsEnabled().performClick()
        compose.onNodeWithText("Aposta favorita inserida com sucesso").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertTrue(first.added.isEmpty())
            assertNotNull(first.model.state.value.addCandidate)
            assertEquals(listOf(2L to FavoriteBetCartOptions(2, 0, false)), second.added)
            assertFalse(second.model.state.value.addedToCart)
        }
    }

    @Test fun contiguousHeadersAndDeleteCancellationRemainVisibleAfterFailure() {
        val fixture = Fixture(listOf(favorite(1, quina), favorite(2, lotomania), favorite(3, quina)))
        fixture.deleteResult = AppResult.Failure(AppError.Validation("Exclusão recusada"))
        compose.setContent {
            val model = remember { fixture.create() }
            CaixaDSTheme { FavoriteBetsRoute(model, {}, {}, {}) }
        }
        compose.onAllNodesWithText("quina").assertCountEquals(2)
        compose.onAllNodesWithContentDescription("Excluir aposta")[0].performClick()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle {
            assertTrue(fixture.deleted.isEmpty())
            assertNull(fixture.model.state.value.deleteCandidate)
        }
        compose.onAllNodesWithContentDescription("Excluir aposta")[0].performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Exclusão recusada").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Favorita 1").assertExists()
        compose.runOnIdle {
            assertEquals(listOf(1L), fixture.deleted)
            assertNull(fixture.model.state.value.errorMessage)
        }
    }

    private fun dialogText(text: String) = compose.onNode(hasText(text) and hasAnyAncestor(isDialog()))

    private inner class Fixture(
        val favorites: List<FavoriteBet> = listOf(favorite(1, quina)),
        val games: List<LotteryGameParameters> = listOf(game(quina), game(mega), game(loteca)),
    ) {
        val filters = mutableListOf<Int?>()
        val added = mutableListOf<Pair<Long, FavoriteBetCartOptions>>()
        val deleted = mutableListOf<Long>()
        var deleteResult: AppResult<Unit> = AppResult.Success(Unit)
        lateinit var model: FavoriteBetsViewModel

        fun create() = FavoriteBetsViewModel(
            getFavorites = { id ->
                filters += id
                AppResult.Success(favorites.filter { id == null || it.modalityId == id })
            },
            getParameters = { AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 10, games)) },
            deleteFavorite = { id ->
                deleted += id
                deleteResult
            },
            addFavoriteToCart = { id, options ->
                added += id to options
                AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
            },
            dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        ).also {
            model = it
            models += it
        }
    }

    private fun favorite(id: Long, modality: LotteryModality) =
        FavoriteBet(id, modality.id, modality.name, "Favorita $id", listOf(listOf(1, 2, 3)))

    private fun game(modality: LotteryModality, type: String = "NORMAL", recurring: List<Int> = emptyList()) =
        LotteryGameParameters(modality, 100, "18/10/2026", 100, 5, 50, 10, recurring, emptyList(), contestType = type)
}
