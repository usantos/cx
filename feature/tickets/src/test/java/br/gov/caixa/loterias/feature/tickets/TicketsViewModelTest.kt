package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.tickets.CheckTicketUseCase
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsPage
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.core.domain.tickets.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchasePaymentMethodsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseStatusesUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchasesUseCase
import br.gov.caixa.loterias.core.domain.tickets.Purchase
import br.gov.caixa.loterias.core.domain.tickets.PurchaseDetails
import br.gov.caixa.loterias.core.domain.tickets.PurchaseMonth
import br.gov.caixa.loterias.core.domain.tickets.SavePurchaseAsFavoriteUseCase
import br.gov.caixa.loterias.core.domain.cart.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.core.domain.payment.DamValidation
import br.gov.caixa.loterias.core.domain.payment.ValidateDamUseCase
import br.gov.caixa.loterias.core.domain.tickets.TicketFilterOption
import br.gov.caixa.loterias.core.domain.tickets.TicketPeriod
import br.gov.caixa.loterias.core.domain.tickets.TicketQuery
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        contestTypes = listOf(TicketFilterOption(6, "Especial")),
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
        assertEquals("0", betFilters?.get("ano"))

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
        assertEquals("2", betFilters?.get("situacao"))
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
        assertEquals(0, viewModel.state.value.betFilters.activeCount)
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
        viewModel.openPurchaseFilters(); advanceUntilIdle()
        viewModel.closePurchaseFilters()
        viewModel.clearPurchaseFilters(); advanceUntilIdle()
        assertEquals(0, viewModel.state.value.purchaseFilters.activeCount)
        viewModel.retryPurchases(); advanceUntilIdle()
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
        viewModel.loadBets(); advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("filtros"))
        configurationResult = AppResult.Success(configuration)
        modalitiesResult = AppResult.Failure(AppError.Network)
        viewModel.retryBets(); advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("modalidades"))
        modalitiesResult = AppResult.Success(listOf(modality))
        betsResult = AppResult.Failure(AppError.Network)
        viewModel.retryBets(); advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("dados"))
    }

    @Test fun `reports failures from purchase catalogs filters and page`() = runTest(dispatcher) {
        val viewModel = viewModel()
        monthsResult = AppResult.Failure(AppError.Network)
        viewModel.loadPurchases(); advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("meses"))
        monthsResult = AppResult.Success(listOf(september))
        purchasesResult = AppResult.Failure(AppError.Network)
        viewModel.retryPurchases(); advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("compras"))
        purchasesResult = AppResult.Success(emptyList())
        viewModel.loadPurchases(); advanceUntilIdle()
        methodsResult = AppResult.Failure(AppError.Network)
        viewModel.openPurchaseFilters(); advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("pagamento"))
        methodsResult = AppResult.Success(listOf(option))
        statusesResult = AppResult.Failure(AppError.Network)
        viewModel.openPurchaseFilters(); advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("situações"))
        statusesResult = AppResult.Success(listOf(option))
        viewModel.openPurchaseFilters(); advanceUntilIdle()
        statusesResult = AppResult.Failure(AppError.Network)
        viewModel.selectPaymentMethod(null); advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("situações"))
    }

    @Test fun `reports purchase detail and ticket failures`() = runTest(dispatcher) {
        val viewModel = viewModel()
        purchaseBetsResult = AppResult.Failure(AppError.Validation("inválido"))
        viewModel.loadPurchase(purchase); advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("carregar"))
        ticketResult = AppResult.Failure(AppError.Network)
        viewModel.changeTicketCode("123456789012345678901")
        viewModel.checkTicket(); advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("carregar"))
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test fun `query models omit optional filters and use regular page size`() {
        val betQuery = BetFilters(period = TicketPeriod(9, 2026, "Setembro"), status = 0, ordering = 0,
            betType = 0).query()
        assertEquals("30", betQuery["size"])
        assertFalse(betQuery.containsKey("situacao"))
        val purchaseQuery = PurchaseFilters().query(3)
        assertEquals(mapOf("offset" to "3", "size" to "10"), purchaseQuery)
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
            },
        )
        viewModel.loadPurchase(purchase); advanceUntilIdle()
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
        viewModel.validateAndSaveFavorite(); advanceUntilIdle()
        assertTrue(viewModel.state.value.favoriteOverwriteConfirmation)
        viewModel.confirmFavoriteOverwrite(); advanceUntilIdle()
        assertEquals(Triple(9L, "  Meu carrinho  ", false), saved)
        assertTrue(viewModel.state.value.favoriteSaved)
        viewModel.dismissFavoriteSaved()
        viewModel.clearPurchaseBetFilters()
        viewModel.dismissPurchaseStatusDialog()
        assertEquals(0, viewModel.state.value.purchaseBetFilters.activeCount)

        validationSucceeds = true
        viewModel.openFavoriteDialog()
        viewModel.changeFavoriteName("Novo")
        viewModel.validateAndSaveFavorite(); advanceUntilIdle()
        assertEquals(Triple(9L, "Novo", true), saved)
        viewModel.cancelFavoriteOverwrite()
        viewModel.closeFavoriteDialog()
    }

    @Test fun `represa status validates dam and shows backend message`() = runTest(dispatcher) {
        val represada = purchase.copy(statusId = 9, status = "Em processamento represada")
        val viewModel = viewModel(
            getDetails = GetPurchaseDetailsUseCase { AppResult.Success(PurchaseDetails(represada, listOf(bet))) },
            dam = ValidateDamUseCase { AppResult.Success(DamValidation(true, "Compra em processamento.")) },
        )
        viewModel.loadPurchase(purchase); advanceUntilIdle()
        assertEquals("Compra em processamento.", viewModel.state.value.damMessage)
        viewModel.dismissDamMessage()
        assertNull(viewModel.state.value.damMessage)
    }

    private fun viewModel(
        getDetails: GetPurchaseDetailsUseCase? = null,
        validateName: ValidateFavoriteCartNameUseCase? = null,
        saveFavorite: SavePurchaseAsFavoriteUseCase? = null,
        dam: ValidateDamUseCase? = null,
        history: GetConfirmedBetsUseCase = GetConfirmedBetsUseCase { betFilters = it; betsResult },
    ) = TicketsViewModel(
        getConfirmedBets = history,
        getPurchases = GetPurchasesUseCase { purchaseFilters = it; purchasesResult },
        getPurchaseBets = GetPurchaseBetsUseCase { purchaseBetsResult },
        checkTicket = CheckTicketUseCase { ticketQuery = it; ticketResult },
        dispatchers = FixedCoroutineDispatchers(dispatcher),
        getBetsConfiguration = GetConfirmedBetsConfigurationUseCase { configurationResult },
        getModalities = GetLotteryModalitiesUseCase { modalitiesResult },
        getPurchaseMonths = GetPurchaseMonthsUseCase { monthsResult },
        getPaymentMethods = GetPurchasePaymentMethodsUseCase { methodsResult },
        getPurchaseStatuses = GetPurchaseStatusesUseCase { statusMethod = it; statusesResult },
        getPurchaseDetails = getDetails,
        validateFavoriteName = validateName,
        savePurchaseAsFavorite = saveFavorite,
        validateDam = dam,
    )
}
