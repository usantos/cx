package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BettingGameFavoriteViewModelTest {
    private var validation: AppResult<Unit> = AppResult.Success(Unit)
    private var saving: AppResult<Unit> = AppResult.Success(Unit)
    private val validated = mutableListOf<FavoriteBetDraft>()
    private val saved = mutableListOf<FavoriteBetDraft>()
    private var gate: CompletableDeferred<AppResult<Unit>>? = null
    private var game = LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 2900, "", 60,
        6, 6, 3, listOf(0, 2), listOf(BetPrice(6, 0, BigDecimal.TEN)))

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `favorite saves selected numbers only after naming and remote validation`() = runTest {
        val model = readyModel()
        model.requestFavorite()
        assertTrue(model.state.value.favoriteExpanded)
        assertTrue(validated.isEmpty())
        assertTrue(saved.isEmpty())
        model.changeFavoriteName("  Meu jogo  ")
        model.submitFavorite()
        assertFalse(model.state.value.favoriteExpanded)
        advanceUntilIdle()
        val expected = FavoriteBetDraft(2, "Mega-Sena", "Meu jogo", listOf(listOf(1, 2, 3, 4, 5, 6)))
        assertEquals(listOf(expected), validated)
        assertEquals(listOf(expected), saved)
        assertTrue(model.state.value.favoriteSaved)
        assertFalse(model.state.value.favoriteExpanded)
        assertFalse(model.state.value.addedToCart)
    }

    @Test fun `blank name does not validate and long name uses legacy trimmed twenty five characters`() = runTest {
        val model = readyModel()
        model.requestFavorite()
        model.changeFavoriteName("   ")
        model.submitFavorite()
        advanceUntilIdle()
        assertTrue(validated.isEmpty())
        assertTrue(saved.isEmpty())
        assertEquals("Favor inserir um nome para sua aposta favorita", model.state.value.errorMessage)
        assertFalse(model.state.value.favoriteExpanded)
        model.changeFavoriteName("   ABCDEFGHIJKLMNOPQRSTUVWXYZ   ")
        assertEquals("   ABCDEFGHIJKLMNOPQRSTUVWXYZ   ", model.state.value.favoriteName)
        model.submitFavorite()
        advanceUntilIdle()
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXY", saved.single().name)
    }

    @Test fun `business validation asks before saving and confirmation keeps validated name snapshot`() = runTest {
        validation = AppResult.Failure(AppError.Validation("Já existe uma aposta com esse nome. Deseja substituir?"))
        val model = readyModel()
        model.requestFavorite()
        model.changeFavoriteName("Original")
        model.submitFavorite()
        advanceUntilIdle()
        assertTrue(saved.isEmpty())
        assertTrue(model.state.value.favoriteOverwriteConfirmation)
        assertEquals("Já existe uma aposta com esse nome. Deseja substituir?", model.state.value.favoriteOverwriteMessage)
        model.changeFavoriteName("Editado depois")
        model.confirmFavoriteOverwrite()
        advanceUntilIdle()
        assertEquals("Original", saved.single().name)
        assertEquals(validated.single(), saved.single())
        assertTrue(model.state.value.favoriteSaved)
        model.confirmFavoriteOverwrite()
        advanceUntilIdle()
        assertEquals(1, saved.size)
    }

    @Test fun `editing selection invalidates pending business confirmation without saving unvalidated numbers`() = runTest {
        validation = AppResult.Failure(AppError.Validation("Confirme"))
        val model = readyModel()
        model.requestFavorite()
        model.changeFavoriteName("Jogo")
        model.submitFavorite()
        advanceUntilIdle()
        model.toggleNumber(6)
        model.toggleNumber(7)
        assertFalse(model.state.value.favoriteOverwriteConfirmation)
        model.confirmFavoriteOverwrite()
        advanceUntilIdle()
        assertTrue(saved.isEmpty())
        assertFalse(model.state.value.favoriteSaved)
    }

    @Test fun `canceling business confirmation never bypasses validation`() = runTest {
        validation = AppResult.Failure(AppError.Validation("Confirme"))
        val model = readyModel()
        model.requestFavorite()
        model.changeFavoriteName("Jogo")
        model.submitFavorite()
        advanceUntilIdle()
        model.dismissFavoriteOverwrite()
        model.confirmFavoriteOverwrite()
        advanceUntilIdle()
        assertTrue(saved.isEmpty())
        assertFalse(model.state.value.favoriteSaved)
    }

    @Test fun `network and unexpected validation failures do not offer overwrite or save`() = runTest {
        for (failure in listOf(AppError.Network, AppError.Unexpected)) {
            validation = AppResult.Failure(failure)
            val model = readyModel()
            model.requestFavorite()
            model.changeFavoriteName("Jogo")
            model.submitFavorite()
            advanceUntilIdle()
            assertFalse(model.state.value.favoriteOverwriteConfirmation)
            assertFalse(model.state.value.favoriteSaved)
            assertNotNull(model.state.value.errorMessage)
            assertFalse(model.state.value.favoriteExpanded)
        }
        assertTrue(saved.isEmpty())
    }

    @Test fun `failed save does not mark favorite and changed selection clears saved marker`() = runTest {
        saving = AppResult.Failure(AppError.Network)
        val model = readyModel()
        model.requestFavorite()
        model.changeFavoriteName("Jogo")
        model.submitFavorite()
        advanceUntilIdle()
        assertFalse(model.state.value.favoriteSaved)
        assertFalse(model.state.value.favoriteExpanded)
        saving = AppResult.Success(Unit)
        model.submitFavorite()
        advanceUntilIdle()
        assertTrue(model.state.value.favoriteSaved)
        model.clearSelection()
        assertFalse(model.state.value.favoriteSaved)
    }

    @Test fun `pending validation blocks duplicate submission and stale response cannot save edited selection`() = runTest {
        gate = CompletableDeferred()
        val model = readyModel()
        model.requestFavorite()
        model.changeFavoriteName("Jogo")
        model.submitFavorite()
        model.submitFavorite()
        assertEquals(1, validated.size)
        model.clearSelection()
        gate!!.complete(AppResult.Success(Unit))
        advanceUntilIdle()
        assertTrue(saved.isEmpty())
        assertFalse(model.state.value.favoriteSaved)
        assertFalse(model.state.value.loading)
    }

    @Test fun `favorite is unavailable for incomplete surprise and sports selections`() = runTest {
        val incomplete = model()
        advanceUntilIdle()
        incomplete.requestFavorite()
        assertFalse(incomplete.state.value.favoriteExpanded)
        val surprise = readyModel()
        surprise.selectMode(BetEntryMode.SURPRISE)
        surprise.requestFavorite()
        assertFalse(surprise.state.value.canFavorite)
        assertFalse(surprise.state.value.favoriteExpanded)
        for (id in listOf(LOTECA_ID, LOTOGOL_ID)) {
            game = game.copy(modality = LotteryModality(id, "Esporte", "ESPORTE"))
            val sports = model()
            advanceUntilIdle()
            sports.requestFavorite()
            assertFalse(sports.state.value.canFavorite)
            assertFalse(sports.state.value.favoriteExpanded)
        }
        assertTrue(validated.isEmpty())
    }

    @Test fun `favorite preserves super seven columns instead of flattening digits`() = runTest {
        game = game.copy(modality = LotteryModality(SUPER_SEVEN_ID, "Super Sete", "SUPER_SETE"),
            minimumSelection = 7, maximumSelection = 7, maximumNumber = 9,
            prices = listOf(BetPrice(7, 0, BigDecimal.TEN)))
        val model = model()
        advanceUntilIdle()
        repeat(7) { model.toggleSuperSevenDigit(it, it) }
        model.requestFavorite()
        model.changeFavoriteName("Colunas")
        model.submitFavorite()
        advanceUntilIdle()
        assertEquals(listOf(listOf(0), listOf(1), listOf(2), listOf(3), listOf(4), listOf(5), listOf(6)), saved.single().numberColumns)
    }

    @Test fun `favorite requires complementary step and preserves actual heart team`() = runTest {
        val team = SportsTeam(101, "Time Azul", "DF")
        game = game.copy(modality = LotteryModality(TIMEMANIA_ID, "Timemania", "TIMEMANIA"), teams = listOf(team))
        val model = readyModel()
        assertFalse(model.state.value.canFavorite)
        model.primaryAction()
        model.selectTeam(team)
        assertTrue(model.state.value.canFavorite)
        model.requestFavorite()
        model.changeFavoriteName("Time")
        model.submitFavorite()
        advanceUntilIdle()
        assertEquals(team, saved.single().favoriteTeam)
        model.clearCurrentStep()
        assertFalse(model.state.value.favoriteSaved)
    }

    @Test fun `favorite preserves lucky month and clovers from complementary selections`() = runTest {
        val month = LuckyMonth(3, "Março")
        game = game.copy(modality = LotteryModality(DIA_DE_SORTE_ID, "Dia de Sorte", "DIA"), luckyMonths = listOf(month))
        val monthModel = readyModel()
        monthModel.primaryAction()
        monthModel.selectLuckyMonth(month)
        monthModel.requestFavorite()
        monthModel.changeFavoriteName("Mês")
        monthModel.submitFavorite()
        advanceUntilIdle()
        assertEquals(month, saved.single().luckyMonth)
        game = game.copy(modality = LotteryModality(MAIS_MILIONARIA_ID, "+Milionária", "MILIONARIA"),
            minimumClovers = 2, maximumClovers = 2, maximumCloverNumber = 6)
        val cloverModel = readyModel()
        cloverModel.primaryAction()
        cloverModel.toggleClover(2)
        cloverModel.toggleClover(5)
        cloverModel.requestFavorite()
        cloverModel.changeFavoriteName("Trevos")
        cloverModel.submitFavorite()
        advanceUntilIdle()
        assertEquals(listOf(2, 5), saved.last().clovers)
    }

    private fun readyModel(): BettingGameViewModel = model().also { model ->
        (1..6).forEach(model::toggleNumber)
    }

    private fun model(): BettingGameViewModel {
        val dispatcher = UnconfinedTestDispatcher()
        return BettingGameViewModel(game.modality.id,
            GetBettingParametersUseCase { AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 10, listOf(game))) },
            AddBetToCartUseCase { error("Favoriting must not add to the cart") },
            FixedCoroutineDispatchers(dispatcher),
            validateFavoriteBet = ValidateFavoriteBetUseCase { draft -> validated += draft; gate?.await() ?: validation },
            saveFavoriteBet = SaveFavoriteBetUseCase { draft -> saved += draft; saving })
    }
}
