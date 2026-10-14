package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import br.gov.caixa.loterias.core.domain.tickets.RecoverConfirmedBetUseCase
import br.gov.caixa.loterias.core.domain.retail.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.core.domain.betting.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.core.domain.betting.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.core.domain.betting.AddBetToCartUseCase
import br.gov.caixa.loterias.core.domain.betting.BetDraft
import br.gov.caixa.loterias.core.domain.betting.FavoriteBetDraft
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.BetPrice
import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.betting.LuckyMonth
import br.gov.caixa.loterias.core.domain.betting.SportsTeam
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BetDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val bet = ConfirmedBet(1, "Mega-Sena", 1, "hoje", "Ativa", listOf(1))

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `loads recovered nova api details and keeps summary on failure`() = runTest {
        val recovered = bet.copy(status = "Recuperada", numbers = listOf(1, 2, 3))
        val success = BetDetailViewModel(
            bet,
            RecoverConfirmedBetUseCase { AppResult.Success(recovered) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher),
        )
        advanceUntilIdle()
        assertEquals(recovered, success.state.value.bet)
        assertFalse(success.state.value.loading)

        val failure = BetDetailViewModel(
            bet,
            RecoverConfirmedBetUseCase { AppResult.Failure(AppError.Network) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher),
        )
        advanceUntilIdle()
        assertEquals(bet, failure.state.value.bet)
        assertFalse(failure.state.value.loading)
    }

    @Test fun `paid bet downloads prize receipt rather than bet receipt`() = runTest {
        var betReceipts = 0
        var prizeReceipts = 0
        val paid = bet.copy(statusId = 11, redeemedPrize = true)
        val model = BetDetailViewModel(paid,
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { betReceipts++; AppResult.Success(byteArrayOf(1)) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) }, cartUseCase(), FixedCoroutineDispatchers(dispatcher),
            downloadPrizeReceipt = br.gov.caixa.loterias.core.domain.retail.DownloadPrizeReceiptUseCase {
                prizeReceipts++; AppResult.Success(byteArrayOf(2))
            })
        advanceUntilIdle()
        val receipt = async { model.receipts.first() }
        model.downloadReceipt()
        advanceUntilIdle()
        assertEquals(0, betReceipts)
        assertEquals(1, prizeReceipts)
        assertEquals("comprovante-premio-1.pdf", receipt.await().fileName)
    }

    @Test fun `typed hydration rejection prevents repeating stale list numbers`() = runTest {
        var additions = 0
        val rejection = br.gov.caixa.loterias.core.domain.service.ServiceFailure.RedirectRequired(
            br.gov.caixa.loterias.core.domain.service.ServiceRedirect.LOGIN)
        val model = BetDetailViewModel(bet,
            RecoverConfirmedBetUseCase { error("legacy recovery must not run") },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            AddBetToCartUseCase { additions++; AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) },
            FixedCoroutineDispatchers(dispatcher),
            loadConfirmedBet = br.gov.caixa.loterias.core.domain.tickets.LoadConfirmedBetUseCase {
                br.gov.caixa.loterias.core.domain.service.ServiceResult.Rejected(rejection)
            })
        advanceUntilIdle()
        model.addCurrentBetToCart()
        advanceUntilIdle()
        assertEquals(rejection, model.state.value.serviceFailure)
        assertFalse(model.state.value.detailsReady)
        assertEquals(0, additions)
    }

    @Test fun `preview bets do not request recovery`() {
        val preview = bet.copy(id = -1)
        var calls = 0
        val viewModel = BetDetailViewModel(
            preview,
            RecoverConfirmedBetUseCase { calls++; AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher),
        )
        assertEquals(0, calls)
        assertEquals(preview, viewModel.state.value.bet)
    }

    @Test fun `downloads receipt and exposes file event`() = runTest {
        val bytes = byteArrayOf(1, 2, 3)
        val viewModel = BetDetailViewModel(
            bet,
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(bytes) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher),
        )
        val receipt = async { viewModel.receipts.first() }
        viewModel.downloadReceipt()
        advanceUntilIdle()
        assertEquals("comprovante-aposta-1.pdf", receipt.await().fileName)
        assertFalse(viewModel.state.value.loading)
    }

    @Test fun `shows and dismisses receipt errors`() = runTest {
        val viewModel = BetDetailViewModel(
            bet,
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Failure(AppError.Network) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher),
        )
        advanceUntilIdle()
        viewModel.downloadReceipt()
        advanceUntilIdle()
        assertEquals("Não foi possível acessar o serviço. Verifique sua conexão.", viewModel.state.value.errorMessage)
        viewModel.dismissError()
        assertEquals(null, viewModel.state.value.errorMessage)
    }

    @Test fun `validates and saves named favorite`() = runTest {
        var savedName = ""
        val viewModel = BetDetailViewModel(
            bet.copy(modalityId = 2),
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { savedName = it.name; AppResult.Success(Unit) },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher),
        )
        advanceUntilIdle()
        viewModel.toggleFavoriteEditor()
        viewModel.changeFavoriteName("Minha aposta")
        viewModel.submitFavorite()
        advanceUntilIdle()
        assertEquals("Minha aposta", savedName)
        assertEquals(true, viewModel.state.value.favoriteSaved)
        assertEquals(false, viewModel.state.value.favoriteExpanded)
    }

    @Test fun `favorite requires name and supports overwrite confirmation`() = runTest {
        var saves = 0
        val viewModel = BetDetailViewModel(
            bet.copy(modalityId = 2),
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Failure(AppError.Validation("Nome já usado")) },
            SaveFavoriteBetUseCase { saves++; AppResult.Success(Unit) },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher),
        )
        advanceUntilIdle()
        viewModel.submitFavorite()
        advanceUntilIdle()
        assertEquals("Por favor, insira o nome da aposta.", viewModel.state.value.errorMessage)
        viewModel.changeFavoriteName("Repetida")
        viewModel.submitFavorite()
        advanceUntilIdle()
        assertEquals(true, viewModel.state.value.favoriteOverwriteConfirmation)
        viewModel.confirmFavoriteOverwrite()
        advanceUntilIdle()
        assertEquals(1, saves)
        assertEquals(true, viewModel.state.value.favoriteSaved)
    }

    @Test fun `unexpected favorite validation cannot authorize overwrite`() = runTest {
        var saves = 0
        val model = BetDetailViewModel(
            bet.copy(modalityId = 2), RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Failure(AppError.Unexpected) },
            SaveFavoriteBetUseCase { saves++; AppResult.Success(Unit) }, cartUseCase(),
            FixedCoroutineDispatchers(dispatcher),
        )
        advanceUntilIdle()
        model.changeFavoriteName("Minha aposta")
        model.submitFavorite()
        advanceUntilIdle()
        assertFalse(model.state.value.favoriteOverwriteConfirmation)
        assertNotNull(model.state.value.errorMessage)
        model.confirmFavoriteOverwrite()
        advanceUntilIdle()
        assertEquals(0, saves)
    }

    @Test fun `favorite overwrite saves validated snapshot once`() = runTest {
        val saved = mutableListOf<FavoriteBetDraft>()
        val model = BetDetailViewModel(
            bet.copy(modalityId = 2), RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Failure(AppError.Validation("Nome já usado")) },
            SaveFavoriteBetUseCase { saved += it; AppResult.Success(Unit) }, cartUseCase(),
            FixedCoroutineDispatchers(dispatcher),
        )
        advanceUntilIdle()
        model.changeFavoriteName("Validado")
        model.submitFavorite()
        advanceUntilIdle()
        model.changeFavoriteName("Não validado")
        model.confirmFavoriteOverwrite()
        advanceUntilIdle()
        model.confirmFavoriteOverwrite()
        advanceUntilIdle()
        assertEquals(listOf("Validado"), saved.map { it.name })
    }

    @Test fun `adds recovered numeric bet directly to cart`() = runTest {
        var receivedNumbers = emptyList<Int>()
        val viewModel = BetDetailViewModel(
            bet.copy(modalityId = 2, numbers = listOf(1, 2, 3, 4, 5, 6)),
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            AddBetToCartUseCase {
                receivedNumbers = it.numbers
                AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
            },
            FixedCoroutineDispatchers(dispatcher),
        )
        advanceUntilIdle()
        viewModel.addCurrentBetToCart()
        advanceUntilIdle()
        assertEquals(listOf(1, 2, 3, 4, 5, 6), receivedNumbers)
        assertFalse(viewModel.state.value.loading)
        assertTrue(viewModel.state.value.cartAdditionConfirmed)
        viewModel.dismissCartAddition()
        assertFalse(viewModel.state.value.cartAdditionConfirmed)
    }

    @Test fun `failed repetition never displays success and can be retried`() = runTest {
        var fails = true
        val viewModel = BetDetailViewModel(
            bet.copy(modalityId = 2, numbers = listOf(1, 2, 3, 4, 5, 6)),
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            AddBetToCartUseCase {
                if (fails) AppResult.Failure(AppError.Network)
                else AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
            },
            FixedCoroutineDispatchers(dispatcher),
        )
        advanceUntilIdle()
        viewModel.addCurrentBetToCart()
        advanceUntilIdle()
        assertFalse(viewModel.state.value.cartAdditionConfirmed)
        assertFalse(viewModel.state.value.loading)
        assertTrue(viewModel.state.value.errorMessage != null)
        viewModel.dismissError()
        fails = false
        viewModel.addCurrentBetToCart()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.cartAdditionConfirmed)
        assertEquals(null, viewModel.state.value.errorMessage)
    }

    @Test fun `favorite and repetition preserve special complements from recovered bet`() = runTest {
        val team = SportsTeam(101, "Time Azul", "DF")
        val month = LuckyMonth(9, "Setembro", "SET")
        val special = bet.copy(
            modality = "+Milionária",
            modalityId = 9,
            contestTypeId = 2,
            numbers = listOf(1, 2, 3, 4, 5, 6),
            numberColumns = listOf(listOf(1, 2, 3, 4, 5, 6)),
            clovers = listOf(2, 5),
            favoriteTeam = team,
            luckyMonth = month,
            recurringDraws = 3,
            quantity = 2,
            mirror = true,
        )
        var favorite: FavoriteBetDraft? = null
        var repeated: BetDraft? = null
        val viewModel = BetDetailViewModel(
            special,
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { favorite = it; AppResult.Success(Unit) },
            AddBetToCartUseCase {
                repeated = it
                AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
            },
            FixedCoroutineDispatchers(dispatcher),
        )
        advanceUntilIdle()
        viewModel.changeFavoriteName("Especial")
        viewModel.submitFavorite()
        advanceUntilIdle()
        viewModel.addCurrentBetToCart()
        advanceUntilIdle()

        assertEquals(listOf(2, 5), favorite?.clovers)
        assertEquals(team, favorite?.favoriteTeam)
        assertEquals(month, favorite?.luckyMonth)
        assertEquals("ESPECIAL", repeated?.contestType)
        assertEquals(listOf(2, 5), repeated?.clovers)
        assertEquals(3, repeated?.recurringDraws)
        assertEquals(2, repeated?.quantity)
        assertEquals(true, repeated?.generateMirror)
    }

    @Test fun `repeat checks live contests and lets customer select when normal and special are open`() = runTest {
        var contestType = ""
        val normal = game("NORMAL", "Mega-Sena")
        val special = game("ESPECIAL", "Mega-Sena", "Mega da Virada")
        val viewModel = BetDetailViewModel(
            bet.copy(modalityId = 2, contestTypeId = 1, numbers = listOf(1, 2, 3, 4, 5, 6)),
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            AddBetToCartUseCase {
                contestType = it.contestType
                AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
            },
            FixedCoroutineDispatchers(dispatcher),
            getBettingParameters = GetBettingParametersUseCase {
                AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 10, listOf(normal, special)))
            },
        )
        advanceUntilIdle()
        viewModel.addCurrentBetToCart()
        advanceUntilIdle()
        assertEquals(listOf("Mega-Sena", "Mega da Virada"), viewModel.state.value.repeatContestOptions.map { it.label })
        viewModel.selectRepeatContest(1)
        viewModel.confirmRepeatContest()
        advanceUntilIdle()
        assertEquals("ESPECIAL", contestType)
        assertTrue(viewModel.state.value.repeatContestOptions.isEmpty())
    }

    @Test fun `repeat asks conversion for only alternate contest and blocks closed modality`() = runTest {
        var submissions = 0
        val specialOnly = BetDetailViewModel(
            bet.copy(modalityId = 2, contestTypeId = 1, numbers = listOf(1, 2, 3, 4, 5, 6)),
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            AddBetToCartUseCase {
                submissions++
                AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
            },
            FixedCoroutineDispatchers(dispatcher),
            getBettingParameters = GetBettingParametersUseCase {
                AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 10, listOf(game("ESPECIAL", "Mega-Sena", "Mega da Virada"))))
            },
        )
        advanceUntilIdle()
        specialOnly.addCurrentBetToCart()
        advanceUntilIdle()
        assertTrue(requireNotNull(specialOnly.state.value.repeatConversion).conversionMessage.contains("não está vigente"))
        assertEquals(0, submissions)
        specialOnly.confirmRepeatConversion()
        advanceUntilIdle()
        assertEquals(1, submissions)

        val closed = specialOnlyFor(AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 10, emptyList())))
        advanceUntilIdle()
        closed.addCurrentBetToCart()
        advanceUntilIdle()
        assertEquals("Modalidade não disponível no momento.", closed.state.value.errorMessage)
    }

    private fun specialOnlyFor(result: AppResult<BettingParameters>) = BetDetailViewModel(
        bet.copy(modalityId = 2, numbers = listOf(1, 2, 3, 4, 5, 6)),
        RecoverConfirmedBetUseCase { AppResult.Success(it) },
        DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
        ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
        SaveFavoriteBetUseCase { AppResult.Success(Unit) },
        cartUseCase(),
        FixedCoroutineDispatchers(dispatcher),
        getBettingParameters = GetBettingParametersUseCase { result },
    )

    private fun game(type: String, name: String, specialName: String = "") = LotteryGameParameters(
        modality = LotteryModality(2, name, "MEGA_SENA", specialName),
        contestNumber = 1,
        drawDate = "hoje",
        maximumNumber = 60,
        minimumSelection = 6,
        maximumSelection = 20,
        maximumSurprises = 10,
        recurringOptions = listOf(0),
        prices = listOf(BetPrice(6, 0, BigDecimal("5.00"))),
        contestType = type,
    )

    private fun cartUseCase() = AddBetToCartUseCase {
        AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
    }
}
