// Literais numéricos descrevem valores de teste de aposta e recibo recuperados.
@file:Suppress("MagicNumber")

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
class BetDetailLoadingAndReceiptViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val bet = ConfirmedBet(1, "Mega-Sena", 1, "hoje", "Ativa", listOf(1))

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `settled detail loads contest results for the native accordion`() = runTest {
        val result = br.gov.caixa.loterias.feature.results.domain.model.DrawResult(
            "LOTOFACIL", 3804, "14/10/2026", "", listOf(1, 2, 4), emptyList(), emptyList(),
            false, "", "", "", emptyList()
        )
        val model = BetDetailViewModel(
            bet.copy(statusId = 9),
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher),
            getConfirmedBetResults = br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase {
                br.gov.caixa.loterias.core.domain.service.model.ServiceResult.Success(listOf(result))
            }
        )
        advanceUntilIdle()
        assertEquals(listOf(result), model.state.value.drawResults)
        assertFalse(model.state.value.loading)
    }

    @Test fun `loads recovered nova api details and keeps summary on failure`() = runTest {
        val recovered = bet.copy(status = "Recuperada", numbers = listOf(1, 2, 3))
        val success = BetDetailViewModel(
            bet,
            RecoverConfirmedBetUseCase { AppResult.Success(recovered) },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher)
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
            FixedCoroutineDispatchers(dispatcher)
        )
        advanceUntilIdle()
        assertEquals(bet, failure.state.value.bet)
        assertFalse(failure.state.value.loading)
    }

    @Test fun `paid bet downloads prize receipt rather than bet receipt`() = runTest {
        var betReceipts = 0
        var prizeReceipts = 0
        val paid = bet.copy(statusId = 11, redeemedPrize = true)
        val model = BetDetailViewModel(
            paid,
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase {
                betReceipts++
                AppResult.Success(byteArrayOf(1))
            },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher),
            downloadPrizeReceipt = br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase {
                prizeReceipts++
                AppResult.Success(byteArrayOf(2))
            }
        )
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
        val rejection = br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RedirectRequired(
            br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect.LOGIN
        )
        val model = BetDetailViewModel(
            bet,
            RecoverConfirmedBetUseCase { error("legacy recovery must not run") },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            AddBetToCartUseCase {
                additions++
                AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
            },
            FixedCoroutineDispatchers(dispatcher),
            loadConfirmedBet = br.gov.caixa.loterias.feature.tickets.domain.usecase.LoadConfirmedBetUseCase {
                br.gov.caixa.loterias.core.domain.service.model.ServiceResult.Rejected(rejection)
            }
        )
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
            RecoverConfirmedBetUseCase {
                calls++
                AppResult.Success(it)
            },
            DownloadBetReceiptUseCase { AppResult.Success(byteArrayOf()) },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase { AppResult.Success(Unit) },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher)
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
            FixedCoroutineDispatchers(dispatcher)
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
            FixedCoroutineDispatchers(dispatcher)
        )
        advanceUntilIdle()
        viewModel.downloadReceipt()
        advanceUntilIdle()
        assertEquals("Não foi possível acessar o serviço. Verifique sua conexão.", viewModel.state.value.errorMessage)
        viewModel.dismissError()
        assertEquals(null, viewModel.state.value.errorMessage)
        viewModel.reportReceiptOpenFailure()
        assertTrue(viewModel.state.value.errorMessage!!.contains("leitor de PDF"))
        assertFalse(viewModel.state.value.loading)
    }

    private fun cartUseCase() = AddBetToCartUseCase {
        AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
    }
}
