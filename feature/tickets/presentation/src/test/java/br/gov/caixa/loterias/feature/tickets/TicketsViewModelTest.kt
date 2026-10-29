package br.gov.caixa.loterias.feature.tickets

import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.DamValidation
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsBackend
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsPage
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseDetails
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketQuery
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketScanOrientation
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasePaymentMethodsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseStatusesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.SavePurchaseAsFavoriteUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetFilters
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseFilters
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TicketsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val bet = ConfirmedBet(1, "Mega-Sena", 2890, "01/09/2026", "Ativa", listOf(1, 2, 3))
    private val purchase = Purchase(9, "01/09/2026", "Aprovada", "Pix", BigDecimal.TEN)
    private val period = TicketPeriod(45, null, "Últimos 45 dias")
    private val september = PurchaseMonth(9, 2026, "Setembro de 2026")
    private val october = PurchaseMonth(10, 2026, "Outubro de 2026")
    private val option = TicketFilterOption(2, "Opção")
    private val configuration = ConfirmedBetsConfiguration(
        defaultOrdering = 3,
        defaultStatus = 1,
        defaultPeriod = period,
        statuses = listOf(option),
        orderings = listOf(TicketFilterOption(4, "Antigas")),
        periods = listOf(period),
        betTypes = listOf(TicketFilterOption(5, "Bolão")),
        contestTypes = listOf(TicketFilterOption(6, "Especial"))
    )
    private val modality = LotteryModality(7, "Quina", "QUINA")
    private var betsResult: AppResult<List<ConfirmedBet>> = AppResult.Success(listOf(bet))
    private var purchasesResult: AppResult<List<Purchase>> = AppResult.Success(listOf(purchase))
    private var purchaseBetsResult: AppResult<List<ConfirmedBet>> = AppResult.Success(listOf(bet))
    private var ticketResult: AppResult<List<ConfirmedBet>> = AppResult.Success(listOf(bet))
    private var configurationResult: AppResult<ConfirmedBetsConfiguration> = AppResult.Success(configuration)
    private var modalitiesResult: AppResult<List<LotteryModality>> = AppResult.Success(listOf(modality))
    private var monthsResult: AppResult<List<PurchaseMonth>> = AppResult.Success(listOf(september, october))
    private var methodsResult: AppResult<List<TicketFilterOption>> = AppResult.Success(listOf(option))
    private var statusesResult: AppResult<List<TicketFilterOption>> = AppResult.Success(listOf(option))
    private var betFilters: Map<String, String>? = null
    private var purchaseFilters: Map<String, String>? = null
    private var ticketQuery: TicketQuery? = null
    private var statusMethod: Long? = null

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `empty history page retains microservice origin for the intro clover`() = runTest(dispatcher) {
        val history = object : GetConfirmedBetsUseCase {
            override suspend fun invoke(filters: Map<String, String>): AppResult<List<ConfirmedBet>> =
                error("History loading must use the page contract to preserve backend origin")

            override suspend fun page(filters: Map<String, String>): AppResult<ConfirmedBetsPage> {
                betFilters = filters
                return AppResult.Success(ConfirmedBetsPage(emptyList(), ConfirmedBetsBackend.MICROSERVICE))
            }
        }
        val viewModel = viewModel(history = history)
        assertEquals(ConfirmedBetsBackend.SILCE, viewModel.state.value.historyBackend)

        viewModel.loadBets()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.bets.isEmpty())
        assertEquals(ConfirmedBetsBackend.MICROSERVICE, state.historyBackend)
        assertTrue(state.loaded)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals("45", betFilters?.get("mes"))
    }

    @Test fun `loads backend bet configuration and applies every legacy filter`() = runTest(dispatcher) {
        val viewModel = viewModel()
        viewModel.loadBets()
        viewModel.loadBets()
        advanceUntilIdle()
        assertEquals(listOf(bet), viewModel.state.value.bets)
        assertEquals(period, viewModel.state.value.betFilters.period)
        assertEquals("32", betFilters?.get("size"))
        assertFalse(betFilters.orEmpty().containsKey("ano"))

        viewModel.openBetFilters()
        viewModel.selectBetPeriod(period)
        viewModel.selectBetStatus(2)
        viewModel.selectBetOrdering(4)
        viewModel.selectBetType(5)
        viewModel.selectContestType(6)
        viewModel.selectModality(7)
        viewModel.toggleSurprise()
        viewModel.toggleRecurring()
        viewModel.toggleCombo()
        assertEquals(8, viewModel.state.value.betFilters.activeCount)
        viewModel.applyBetFilters()
        advanceUntilIdle()
        assertEquals("4", betFilters?.get("situacao"))
        assertEquals("4", betFilters?.get("ordenacao"))
        assertEquals("5", betFilters?.get("tipoAposta"))
        assertEquals("6", betFilters?.get("tipoConcurso"))
        assertEquals("7", betFilters?.get("modalidade"))
        assertEquals("3", betFilters?.get("surpresinha"))
        assertEquals("3", betFilters?.get("teimosinha"))
        assertEquals("3", betFilters?.get("combo"))

        viewModel.openBetFilters()
        viewModel.closeBetFilters()
        viewModel.clearBetFilters()
        advanceUntilIdle()
        assertEquals(0, viewModel.state.value.betFilters.activeCount(configuration))
        viewModel.retryBets()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.loaded)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test fun `loads months filters and paginates purchases with legacy query keys`() = runTest(dispatcher) {
        val page = (1..10).map { purchase.copy(id = it.toLong()) }
        purchasesResult = AppResult.Success(page)
        val viewModel = viewModel()
        viewModel.loadPurchases()
        viewModel.loadPurchases()
        advanceUntilIdle()
        assertEquals(september, viewModel.state.value.purchaseFilters.month)
        assertEquals("9", purchaseFilters?.get("mes"))
        assertEquals("2026", purchaseFilters?.get("ano"))
        assertEquals("10", purchaseFilters?.get("size"))
        assertTrue(viewModel.state.value.canLoadMore)

        purchasesResult = AppResult.Success(listOf(purchase.copy(id = 11)))
        viewModel.loadMorePurchases()
        viewModel.loadMorePurchases()
        advanceUntilIdle()
        assertEquals("10", purchaseFilters?.get("offset"))
        assertEquals(11, viewModel.state.value.purchases.size)
        assertFalse(viewModel.state.value.canLoadMore)
        viewModel.loadMorePurchases()

        viewModel.selectPurchaseMonth(october)
        advanceUntilIdle()
        assertEquals("10", purchaseFilters?.get("mes"))
        viewModel.openPurchaseFilters()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.showPurchaseFilters)
        viewModel.selectPaymentMethod(2)
        advanceUntilIdle()
        assertEquals(2L, statusMethod)
        viewModel.selectPurchaseStatus(2)
        viewModel.applyPurchaseFilters()
        advanceUntilIdle()
        assertEquals("2", purchaseFilters?.get("meio"))
        assertEquals("2", purchaseFilters?.get("situacao"))
        viewModel.openPurchaseFilters()
        advanceUntilIdle()
        viewModel.closePurchaseFilters()
        viewModel.clearPurchaseFilters()
        advanceUntilIdle()
        assertEquals(0, viewModel.state.value.purchaseFilters.activeCount)
        viewModel.retryPurchases()
        advanceUntilIdle()
    }

    @Test fun `loads selected purchase and sanitizes ticket input`() = runTest(dispatcher) {
        val viewModel = viewModel()
        viewModel.loadPurchase(purchase)
        viewModel.loadPurchase(purchase)
        advanceUntilIdle()
        assertEquals(purchase, viewModel.state.value.selectedPurchase)
        assertEquals(listOf(bet), viewModel.state.value.bets)
        viewModel.checkTicket()
        assertEquals("Informe o código do bilhete.", viewModel.state.value.errorMessage)
        viewModel.changeTicketCode(" ab-12 ç_" + "x".repeat(80))
        assertNull(viewModel.state.value.errorMessage)
        assertEquals(23, viewModel.state.value.ticketCode.length)
        assertTrue(viewModel.state.value.ticketCode.startsWith("AB12Ç"))
        viewModel.checkTicket(qrCode = true)
        advanceUntilIdle()
        assertEquals(TicketQuery(viewModel.state.value.ticketCode, true), ticketQuery)
        viewModel.changeTicketCode("12345678901234567890123")
        viewModel.changeTicketRetailerCode("10.000000-1")
        viewModel.checkTicket()
        advanceUntilIdle()
        assertEquals(TicketQuery("12345678901234567890123", true, "100000001"), ticketQuery)
        viewModel.submitScannedTicketCode("curto")
        assertEquals("12345678901234567890123", viewModel.state.value.ticketCode)
        viewModel.submitScannedTicketCode("APOSTA" + "x".repeat(23))
        advanceUntilIdle()
        assertEquals(TicketQuery("APOSTA" + "x".repeat(23), true), ticketQuery)
        viewModel.dismissTicketError()
    }

    @Test fun `failed append preserves the page and retries its offset before empty success ends paging`() = runTest(dispatcher) {
        val page = (1..10).map { purchase.copy(id = it.toLong()) }
        val offsets = mutableListOf<String?>()
        purchasesResult = AppResult.Success(page)
        val model = viewModel(purchasesLoader = { query ->
            offsets += query["offset"]
            purchasesResult
        })
        model.loadPurchases()
        advanceUntilIdle()

        purchasesResult = AppResult.Failure(AppError.Network)
        model.loadMorePurchases()
        advanceUntilIdle()
        assertEquals(page, model.state.value.purchases)
        assertTrue(model.state.value.canLoadMore)
        assertFalse(model.state.value.isLoadingMore)
        assertEquals("Não foi possível carregar as compras. Tente novamente.", model.state.value.errorMessage)

        purchasesResult = AppResult.Success(emptyList())
        model.loadMorePurchases()
        advanceUntilIdle()
        assertEquals(listOf("0", "10", "10"), offsets)
        assertEquals(page, model.state.value.purchases)
        assertFalse(model.state.value.canLoadMore)
        assertFalse(model.state.value.isLoadingMore)
        assertTrue(model.state.value.loaded)
        model.loadMorePurchases()
        advanceUntilIdle()
        assertEquals(3, offsets.size)
    }

    @Test fun `clearing the viewmodel cancels catalog loading before any private history request`() = runTest(dispatcher) {
        var configurationCancelled = false
        var historyReads = 0
        val model = viewModel(
            configurationLoader = {
                try {
                    awaitCancellation()
                } finally {
                    configurationCancelled = true
                }
            },
            history = GetConfirmedBetsUseCase {
                historyReads++
                betsResult
            }
        )
        model.loadBets()
        advanceUntilIdle()
        model.viewModelScope.cancel()
        advanceUntilIdle()

        assertTrue(configurationCancelled)
        assertEquals(0, historyReads)
        assertTrue(model.state.value.bets.isEmpty())
        assertFalse(model.state.value.loaded)
        assertNull(model.state.value.errorMessage)
    }

    @Test fun `ticket game choice keeps the eight legacy modalities and toggles valid cards`() = runTest(dispatcher) {
        val mega = LotteryModality(2, "Mega-Sena", "MEGA_SENA")
        val dupla = LotteryModality(6, "Dupla Sena", "DUPLA_SENA")
        val unsupported = LotteryModality(11, "+Milionária", "MAIS_MILIONARIA")
        modalitiesResult = AppResult.Success(listOf(mega, dupla, unsupported))
        val viewModel = viewModel()
        viewModel.loadTicketModalities()
        viewModel.loadTicketModalities()
        advanceUntilIdle()
        assertEquals(listOf(mega, dupla), viewModel.state.value.ticketModalities)

        viewModel.toggleTicketModality(unsupported.id)
        assertTrue(viewModel.state.value.selectedTicketModalityIds.isEmpty())
        viewModel.toggleTicketModality(mega.id)
        assertEquals(setOf(mega.id), viewModel.state.value.selectedTicketModalityIds)
        viewModel.toggleTicketModality(mega.id)
        assertTrue(viewModel.state.value.selectedTicketModalityIds.isEmpty())

        modalitiesResult = AppResult.Failure(AppError.Network)
        val failing = viewModel()
        failing.loadTicketModalities()
        advanceUntilIdle()
        assertTrue(failing.state.value.errorMessage!!.contains("modalidades"))
    }

    @Test fun `reports failures from bet catalogs requests and retry`() = runTest(dispatcher) {
        val viewModel = viewModel()
        configurationResult = AppResult.Failure(AppError.Network)
        viewModel.loadBets()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("filtros"))
        configurationResult = AppResult.Success(configuration)
        modalitiesResult = AppResult.Failure(AppError.Network)
        viewModel.retryBets()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("modalidades"))
        modalitiesResult = AppResult.Success(listOf(modality))
        betsResult = AppResult.Failure(AppError.Network)
        viewModel.retryBets()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("dados"))
    }

    @Test fun `reports failures from purchase catalogs filters and page`() = runTest(dispatcher) {
        val viewModel = viewModel()
        monthsResult = AppResult.Failure(AppError.Network)
        viewModel.loadPurchases()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("meses"))
        monthsResult = AppResult.Success(listOf(september))
        purchasesResult = AppResult.Failure(AppError.Network)
        viewModel.retryPurchases()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("compras"))
        purchasesResult = AppResult.Success(emptyList())
        viewModel.loadPurchases()
        advanceUntilIdle()
        methodsResult = AppResult.Failure(AppError.Network)
        viewModel.openPurchaseFilters()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("pagamento"))
        methodsResult = AppResult.Success(listOf(option))
        statusesResult = AppResult.Failure(AppError.Network)
        viewModel.openPurchaseFilters()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("situações"))
        statusesResult = AppResult.Success(listOf(option))
        viewModel.openPurchaseFilters()
        advanceUntilIdle()
        statusesResult = AppResult.Failure(AppError.Network)
        viewModel.selectPaymentMethod(null)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("situações"))
    }

    @Test fun `reports purchase detail and ticket failures`() = runTest(dispatcher) {
        val viewModel = viewModel()
        purchaseBetsResult = AppResult.Failure(AppError.Validation("inválido"))
        viewModel.loadPurchase(purchase)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("carregar"))
        ticketResult = AppResult.Failure(AppError.Network)
        viewModel.changeTicketCode("123456789012345678901")
        viewModel.checkTicket()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("carregar"))
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test fun `purchase detail applies filters and saves favorite with legacy overwrite flow`() = runTest(dispatcher) {
        val detailedPurchase = purchase.copy(status = "Cancelada", statusId = 5, paymentMethodId = 6)
        val detailedBet = bet.copy(modalityId = 2, statusId = 7, contest = 2900, surprise = true)
        var validationSucceeds = false
        var saved: Triple<Long, String, Boolean>? = null
        val viewModel = viewModel(
            getDetails = GetPurchaseDetailsUseCase {
                AppResult.Success(PurchaseDetails(detailedPurchase, listOf(detailedBet)))
            },
            validateName = ValidateFavoriteCartNameUseCase {
                if (validationSucceeds) AppResult.Success(Unit) else AppResult.Failure(AppError.Validation("duplicado"))
            },
            saveFavorite = SavePurchaseAsFavoriteUseCase { id, name, keep ->
                saved = Triple(id, name, keep)
                AppResult.Success(Unit)
            }
        )
        viewModel.loadPurchase(purchase)
        advanceUntilIdle()
        assertEquals(detailedPurchase, viewModel.state.value.selectedPurchase)
        assertTrue(viewModel.state.value.purchaseStatusDialog != null)
        viewModel.openPurchaseBetFilters()
        viewModel.selectPurchaseBetContest(2900)
        viewModel.selectPurchaseBetStatus(7)
        viewModel.selectPurchaseBetModality(2)
        assertEquals(3, viewModel.state.value.purchaseBetFilters.activeCount)
        viewModel.applyPurchaseBetFilters()
        viewModel.openFavoriteDialog()
        viewModel.changeFavoriteName("  Meu carrinho  ")
        viewModel.toggleKeepSurprises()
        viewModel.validateAndSaveFavorite()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.favoriteOverwriteConfirmation)
        viewModel.confirmFavoriteOverwrite()
        advanceUntilIdle()
        assertEquals(Triple(9L, "  Meu carrinho  ", false), saved)
        assertTrue(viewModel.state.value.favoriteSaved)
        viewModel.dismissFavoriteSaved()
        viewModel.clearPurchaseBetFilters()
        viewModel.dismissPurchaseStatusDialog()
        assertEquals(0, viewModel.state.value.purchaseBetFilters.activeCount)

        validationSucceeds = true
        viewModel.openFavoriteDialog()
        viewModel.changeFavoriteName("Novo")
        viewModel.validateAndSaveFavorite()
        advanceUntilIdle()
        assertEquals(Triple(9L, "Novo", true), saved)
        viewModel.cancelFavoriteOverwrite()
        viewModel.closeFavoriteDialog()
    }

    @Test fun `represa status validates dam and shows backend message`() = runTest(dispatcher) {
        val represada = purchase.copy(statusId = 9, status = "Em processamento represada")
        val viewModel = viewModel(
            getDetails = GetPurchaseDetailsUseCase { AppResult.Success(PurchaseDetails(represada, listOf(bet))) },
            dam = ValidateDamUseCase { AppResult.Success(DamValidation(true, "Compra em processamento.")) }
        )
        viewModel.loadPurchase(purchase)
        advanceUntilIdle()
        assertEquals("Compra em processamento.", viewModel.state.value.damMessage)
        viewModel.dismissDamMessage()
        assertNull(viewModel.state.value.damMessage)
    }

    @Test fun `expired lease between configuration and history prevents old query and new owner loads once`() = runTest(dispatcher) {
        var current = true
        var reads = 0
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<ConfirmedBetsConfiguration>>()
        val history = GetConfirmedBetsUseCase {
            reads++
            betsResult
        }
        val old = viewModel(isSessionCurrent = { current }, configurationLoader = { pending.await() }, history = history)
        old.loadBets()
        advanceUntilIdle()
        current = false
        pending.complete(configurationResult)
        advanceUntilIdle()
        assertEquals(0, reads)
        assertTrue(old.state.value.bets.isEmpty())
        val fresh = viewModel(history = history)
        fresh.loadBets()
        advanceUntilIdle()
        assertEquals(1, reads)
        assertEquals(listOf(bet), fresh.state.value.bets)
    }

    @Test fun `expired lease ignores pending private history response`() = runTest(dispatcher) {
        var current = true
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<List<ConfirmedBet>>>()
        val old = viewModel(isSessionCurrent = { current }, history = GetConfirmedBetsUseCase { pending.await() })
        old.loadBets()
        advanceUntilIdle()
        current = false
        pending.complete(betsResult)
        advanceUntilIdle()
        assertTrue(old.state.value.bets.isEmpty())
        assertFalse(old.state.value.loaded)
    }

    @Test fun `expired lease between purchase months and list prevents private query`() = runTest(dispatcher) {
        var current = true
        var reads = 0
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<List<PurchaseMonth>>>()
        val old =
            viewModel(isSessionCurrent = { current }, monthsLoader = { pending.await() }, purchasesLoader = {
                reads++
                purchasesResult
            })
        old.loadPurchases()
        advanceUntilIdle()
        current = false
        pending.complete(monthsResult)
        advanceUntilIdle()
        assertEquals(0, reads)
        assertTrue(old.state.value.purchases.isEmpty())
    }

    @Test fun `expired lease ignores a pending appended purchase page`() = runTest(dispatcher) {
        var current = true
        val page = (1..10).map { purchase.copy(id = it.toLong()) }
        val offsets = mutableListOf<String?>()
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<List<Purchase>>>()
        val model = viewModel(isSessionCurrent = { current }, purchasesLoader = { filters ->
            offsets += filters["offset"]
            if (filters["offset"] == "0") AppResult.Success(page) else pending.await()
        })
        model.loadPurchases()
        advanceUntilIdle()
        model.loadMorePurchases()
        model.loadMorePurchases()
        advanceUntilIdle()
        assertEquals(listOf("0", "10"), offsets)
        current = false
        pending.complete(AppResult.Success(listOf(purchase.copy(id = 11))))
        advanceUntilIdle()
        assertEquals(page, model.state.value.purchases)
        assertNull(model.state.value.errorMessage)
    }

    @Test fun `retry uses loaded catalogs and current filters after page failure`() = runTest(dispatcher) {
        var configurations = 0
        var months = 0
        val model = viewModel(
            configurationLoader = {
                configurations++
                configurationResult
            },
            monthsLoader = {
                months++
                monthsResult
            }
        )
        betsResult = AppResult.Failure(AppError.Network)
        model.loadBets()
        advanceUntilIdle()
        assertTrue(model.state.value.errorMessage!!.contains("dados"))
        model.selectBetStatus(2)
        betsResult = AppResult.Success(listOf(bet))
        model.retryBets()
        advanceUntilIdle()
        assertEquals(1, configurations)
        assertEquals("4", betFilters?.get("situacao"))
        assertEquals(listOf(bet), model.state.value.bets)
        assertNull(model.state.value.errorMessage)
        purchasesResult = AppResult.Failure(AppError.Network)
        model.loadPurchases()
        advanceUntilIdle()
        assertTrue(model.state.value.errorMessage!!.contains("compras"))
        model.selectPurchaseStatus(7)
        purchasesResult = AppResult.Success(listOf(purchase))
        model.retryPurchases()
        advanceUntilIdle()
        assertEquals(1, months)
        assertEquals("7", purchaseFilters?.get("situacao"))
        assertEquals("0", purchaseFilters?.get("offset"))
        assertEquals(listOf(purchase), model.state.value.purchases)
        assertNull(model.state.value.errorMessage)
    }

    @Test fun `queued history reloads read filters when their coroutine starts`() = runTest(dispatcher) {
        val model = viewModel()
        model.loadBets()
        advanceUntilIdle()
        model.applyBetFilters()
        model.selectBetOrdering(4)
        advanceUntilIdle()
        assertEquals("4", betFilters?.get("ordenacao"))
        model.loadPurchases()
        advanceUntilIdle()
        model.applyPurchaseFilters()
        model.selectPurchaseStatus(7)
        advanceUntilIdle()
        assertEquals("7", purchaseFilters?.get("situacao"))
        assertFalse(model.state.value.isLoading)
    }

    @Test fun `ticket result navigation is consumed once while result remains available`() = runTest(dispatcher) {
        val model = viewModel()
        assertFalse(model.consumeTicketResultNavigation())
        model.submitScannedTicketCode("12345678901234567890123")
        advanceUntilIdle()
        val result = model.state.value.ticketResult
        assertTrue(model.consumeTicketResultNavigation())
        assertFalse(model.consumeTicketResultNavigation())
        assertFalse(model.state.value.loaded)
        assertEquals(result, model.state.value.ticketResult)
        assertEquals(listOf(bet), model.state.value.bets)
        model.submitScannedTicketCode("12345678901234567890124")
        advanceUntilIdle()
        assertTrue(model.consumeTicketResultNavigation())
    }

    @Test fun `ignored scanner input keeps state idle and accepted payload reaches checker`() = runTest(dispatcher) {
        val prepared = mutableListOf<Pair<String, TicketScanOrientation>>()
        val queries = mutableListOf<TicketQuery>()
        val checker = object : CheckTicketUseCase {
            override fun prepareScannedQuery(code: String, orientation: TicketScanOrientation): TicketQuery? {
                prepared += code to orientation
                return if (code == "12345678901234567890123") null else TicketQuery(code, true)
            }
            override suspend fun invoke(query: TicketQuery): AppResult<List<ConfirmedBet>> {
                queries += query
                return AppResult.Success(listOf(bet))
            }
        }
        val model = viewModel(ticketChecker = checker)
        model.submitScannedTicketCode("12345678901234567890123", TicketScanOrientation.PORTRAIT)
        advanceUntilIdle()
        assertTrue(queries.isEmpty())
        assertFalse(model.state.value.isLoading)
        assertFalse(model.state.value.loaded)
        assertNull(model.state.value.errorMessage)
        val qr = "APOSTA{\"nsb\":\"NSB\",\"ci\":\"CI\",\"pro\":\"2\"}"
        model.submitScannedTicketCode(qr, TicketScanOrientation.PORTRAIT)
        advanceUntilIdle()
        assertEquals(listOf(TicketQuery(qr, true)), queries)
        assertEquals(
            listOf(
                "12345678901234567890123" to TicketScanOrientation.PORTRAIT,
                qr to TicketScanOrientation.PORTRAIT
            ),
            prepared
        )
        assertTrue(model.state.value.loaded)
    }

    @Test fun `scanner waits for error acknowledgement before accepting another frame`() = runTest(dispatcher) {
        var requests = 0
        val model = viewModel(
            ticketChecker = CheckTicketUseCase {
                requests++
                AppResult.Failure(AppError.Network)
            }
        )
        val first = "12345678901234567890123"
        val next = "12345678901234567890124"
        model.submitScannedTicketCode(first)
        model.submitScannedTicketCode(next)
        assertEquals(first, model.state.value.ticketCode)
        advanceUntilIdle()
        val error = model.state.value.errorMessage
        assertTrue(error != null)
        model.submitScannedTicketCode(next)
        advanceUntilIdle()
        assertEquals(1, requests)
        assertEquals(error, model.state.value.errorMessage)
        model.dismissTicketError()
        model.submitScannedTicketCode(next)
        advanceUntilIdle()
        assertEquals(2, requests)
    }

    private fun viewModel(
        isSessionCurrent: () -> Boolean = { true },
        configurationLoader: suspend () -> AppResult<ConfirmedBetsConfiguration> = { configurationResult },
        monthsLoader: suspend () -> AppResult<List<PurchaseMonth>> = { monthsResult },
        purchasesLoader: suspend (Map<String, String>) -> AppResult<List<Purchase>> = { purchasesResult },
        getDetails: GetPurchaseDetailsUseCase? = null,
        validateName: ValidateFavoriteCartNameUseCase? = null,
        saveFavorite: SavePurchaseAsFavoriteUseCase? = null,
        dam: ValidateDamUseCase? = null,
        ticketChecker: CheckTicketUseCase = CheckTicketUseCase {
            ticketQuery = it
            ticketResult
        },
        history: GetConfirmedBetsUseCase = GetConfirmedBetsUseCase {
            betFilters = it
            betsResult
        },
    ) = TicketsViewModel(
        getConfirmedBets = history,
        getPurchases = GetPurchasesUseCase {
            purchaseFilters = it
            purchasesLoader(it)
        },
        getPurchaseBets = GetPurchaseBetsUseCase { purchaseBetsResult },
        checkTicket = ticketChecker,
        dispatchers = FixedCoroutineDispatchers(dispatcher),
        getBetsConfiguration = GetConfirmedBetsConfigurationUseCase { configurationLoader() },
        getModalities = GetLotteryModalitiesUseCase { modalitiesResult },
        getPurchaseMonths = GetPurchaseMonthsUseCase { monthsLoader() },
        getPaymentMethods = GetPurchasePaymentMethodsUseCase { methodsResult },
        getPurchaseStatuses = GetPurchaseStatusesUseCase {
            statusMethod = it
            statusesResult
        },
        getPurchaseDetails = getDetails,
        validateFavoriteName = validateName,
        savePurchaseAsFavorite = saveFavorite,
        validateDam = dam,
        isSessionCurrent = isSessionCurrent
    )
}
