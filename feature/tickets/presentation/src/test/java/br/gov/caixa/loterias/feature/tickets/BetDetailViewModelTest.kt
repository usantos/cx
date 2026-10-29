package br.gov.caixa.loterias.feature.tickets

import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.usecase.RecoverConfirmedBetUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.contestType
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BetDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val bet = ConfirmedBet(1, "Mega-Sena", 1, "hoje", "Ativa", listOf(1))

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `validates and saves named favorite`() = runTest {
        var savedName = ""
        val viewModel = BetDetailViewModel(
            bet.copy(modalityId = 2),
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase {
                savedName = it.name
                AppResult.Success(Unit)
            },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher)
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
            SaveFavoriteBetUseCase {
                saves++
                AppResult.Success(Unit)
            },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher)
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
            bet.copy(modalityId = 2),
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Failure(AppError.Unexpected) },
            SaveFavoriteBetUseCase {
                saves++
                AppResult.Success(Unit)
            },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher)
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
            bet.copy(modalityId = 2),
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Failure(AppError.Validation("Nome já usado")) },
            SaveFavoriteBetUseCase {
                saved += it
                AppResult.Success(Unit)
            },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher)
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
            FixedCoroutineDispatchers(dispatcher)
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
                if (fails) {
                    AppResult.Failure(AppError.Network)
                } else {
                    AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
                }
            },
            FixedCoroutineDispatchers(dispatcher)
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
            mirror = true
        )
        var favorite: FavoriteBetDraft? = null
        var repeated: BetDraft? = null
        val viewModel = BetDetailViewModel(
            special,
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase {
                favorite = it
                AppResult.Success(Unit)
            },
            AddBetToCartUseCase {
                repeated = it
                AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
            },
            FixedCoroutineDispatchers(dispatcher)
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
            }
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
                AppResult.Success(
                    BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 10, listOf(game("ESPECIAL", "Mega-Sena", "Mega da Virada")))
                )
            }
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

    @Test fun `cancelled overwrite cannot save a stale draft and a new consent uses the new snapshot`() = runTest {
        val saved = mutableListOf<String>()
        val model = detailModel(
            validate = ValidateFavoriteBetUseCase { AppResult.Failure(AppError.Validation("Conflito")) },
            save = SaveFavoriteBetUseCase {
                saved += it.name
                AppResult.Success(Unit)
            }
        )
        advanceUntilIdle()
        model.changeFavoriteName("Anterior")
        model.submitFavorite()
        advanceUntilIdle()
        assertTrue(model.state.value.favoriteOverwriteConfirmation)
        model.dismissFavoriteOverwrite()
        model.changeFavoriteName("Nova")
        model.confirmFavoriteOverwrite()
        advanceUntilIdle()
        assertTrue(saved.isEmpty())
        assertFalse(model.state.value.favoriteOverwriteConfirmation)
        model.submitFavorite()
        advanceUntilIdle()
        model.confirmFavoriteOverwrite()
        advanceUntilIdle()
        assertEquals(listOf("Nova"), saved)
    }

    @Test fun `paid receipt without optional prize service keeps bet PDFs queued in order without replay`() = runTest {
        var downloads = 0
        val model = detailModel(
            target = bet.copy(redeemedPrize = true),
            download = DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf((++downloads).toByte())) }
        )
        advanceUntilIdle()
        model.downloadReceipt()
        advanceUntilIdle()
        model.downloadReceipt()
        advanceUntilIdle()
        assertFalse(model.state.value.loading)
        val first = async { model.receipts.first() }
        runCurrent()
        val second = async { model.receipts.first() }
        runCurrent()
        assertEquals("comprovante-aposta-1.pdf", first.await().fileName)
        assertArrayEquals(byteArrayOf(1), first.await().bytes)
        assertEquals("comprovante-aposta-1.pdf", second.await().fileName)
        assertArrayEquals(byteArrayOf(2), second.await().bytes)
        val next = async { model.receipts.first() }
        runCurrent()
        assertFalse("A consumed PDF must not replay", next.isCompleted)
        next.cancel()
    }

    @Test fun `repeat maps all super seven columns quantities and recurring flags without flattening`() = runTest {
        val columns = listOf(listOf(2, 4), listOf(2), listOf(3), listOf(4), listOf(5), listOf(6), listOf(7))
        var submitted: BetDraft? = null
        val model = detailModel(
            target = bet.copy(
                modalityId = 7,
                numbers = emptyList(),
                numberColumns = columns,
                recurringDraws = 3,
                quantity = 2,
                mirror = true
            ),
            add = AddBetToCartUseCase {
                submitted = it
                AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
            }
        )
        advanceUntilIdle()
        model.addCurrentBetToCart()
        advanceUntilIdle()
        val draft = requireNotNull(submitted)
        assertEquals(columns, draft.superSevenColumns)
        assertEquals(emptyList<Int>(), draft.numbers)
        assertEquals(8, draft.numberQuantity)
        assertEquals(3, draft.recurringDraws)
        assertEquals(2, draft.quantity)
        assertTrue(draft.generateMirror)
        assertTrue(model.state.value.cartAdditionConfirmed)
    }

    @Test fun `missing modality or selection prevents repeating incomplete bets`() = runTest {
        var additions = 0
        for (target in listOf(bet.copy(modalityId = 0), bet.copy(modalityId = 2, numbers = emptyList(), numberColumns = emptyList()))) {
            val model = detailModel(
                target = target,
                add = AddBetToCartUseCase {
                    additions++
                    AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
                }
            )
            advanceUntilIdle()
            model.addCurrentBetToCart()
            advanceUntilIdle()
            assertFalse(model.state.value.cartAdditionConfirmed)
            assertEquals("Não foi possível recuperar os dados desta aposta.", model.state.value.errorMessage)
        }
        assertEquals(0, additions)
    }

    @Test fun `preview receipt is unavailable and never reaches the download service`() = runTest {
        var downloads = 0
        val model = detailModel(
            target = bet.copy(id = -1),
            download = DownloadBetReceiptUseCase {
                downloads++
                AppResult.Success(byteArrayOf(1))
            }
        )
        model.downloadReceipt()
        advanceUntilIdle()
        assertEquals(0, downloads)
        assertFalse(model.state.value.loading)
        assertEquals("Comprovante indisponível para esta aposta.", model.state.value.errorMessage)
    }

    @Test fun `clearing viewmodel cancels pending detail hydration and receipt without late emission`() = runTest {
        val recovered = CompletableDeferred<AppResult<ConfirmedBet>>()
        val pdf = CompletableDeferred<AppResult<ByteArray>>()
        var recoveryCancelled = false
        var downloadCancelled = false
        val model = detailModel(
            recover = RecoverConfirmedBetUseCase {
                try {
                    recovered.await()
                } finally {
                    recoveryCancelled = true
                }
            },
            download = DownloadBetReceiptUseCase {
                try {
                    pdf.await()
                } finally {
                    downloadCancelled = true
                }
            }
        )
        val store = ViewModelStore().apply { put("detail", model) }
        runCurrent()
        model.downloadReceipt()
        runCurrent()
        assertTrue(model.state.value.loading)
        assertFalse(recoveryCancelled)
        assertFalse(downloadCancelled)
        val beforeClear = model.state.value
        store.clear()
        runCurrent()
        assertTrue(recoveryCancelled)
        assertTrue(downloadCancelled)
        recovered.complete(AppResult.Success(bet.copy(numbers = listOf(9))))
        pdf.complete(AppResult.Success(byteArrayOf(3)))
        runCurrent()
        assertEquals(beforeClear, model.state.value)
        val receipt = async { model.receipts.first() }
        runCurrent()
        assertFalse("A cancelled download must not emit", receipt.isCompleted)
        receipt.cancel()
    }

    private fun detailModel(
        target: ConfirmedBet = bet,
        recover: RecoverConfirmedBetUseCase = RecoverConfirmedBetUseCase { AppResult.Success(it) },
        download: DownloadBetReceiptUseCase = DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf(1)) },
        validate: ValidateFavoriteBetUseCase = ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
        save: SaveFavoriteBetUseCase = SaveFavoriteBetUseCase { AppResult.Success(Unit) },
        add: AddBetToCartUseCase = cartUseCase(),
    ) = BetDetailViewModel(target, recover, download, validate, save, add, FixedCoroutineDispatchers(dispatcher))

    private fun specialOnlyFor(result: AppResult<BettingParameters>) = BetDetailViewModel(
        bet.copy(modalityId = 2, numbers = listOf(1, 2, 3, 4, 5, 6)),
        RecoverConfirmedBetUseCase { AppResult.Success(it) },
        DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
        ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
        SaveFavoriteBetUseCase { AppResult.Success(Unit) },
        cartUseCase(),
        FixedCoroutineDispatchers(dispatcher),
        getBettingParameters = GetBettingParametersUseCase { result }
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
        contestType = type
    )

    private fun cartUseCase() = AddBetToCartUseCase {
        AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
    }
}
