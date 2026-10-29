package br.gov.caixa.loterias.feature.marketplacefilter

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContestsUseCase
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability
import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParameters
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import java.math.BigDecimal
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.toDomain
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterModalityUi
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.viewmodel.MarketplaceFilterViewModel
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.retailerDisplayLabel

@OptIn(ExperimentalCoroutinesApi::class)
class MarketplaceFilterViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    private val dispatcher = UnconfinedTestDispatcher()

    @Test
    fun `loads bet limits and available modalities from both backend contracts`() = runTest {
        val viewModel = viewModel(filterParameters = MarketplaceFilterParameters(3, 6))

        assertEquals(listOf(3, 4, 5, 6), viewModel.state.value.betOptions)
        assertEquals(listOf("mega sena"), viewModel.state.value.modalities.map { it.name })
        assertNull(viewModel.state.value.configurationError)
        viewModel.onAction(MarketplaceFilterAction.SelectBetQuantity(4))
        assertEquals(4, viewModel.state.value.selectedBetQuantity)
        viewModel.onAction(MarketplaceFilterAction.SelectBetQuantity(4))
        assertNull(viewModel.state.value.selectedBetQuantity)
    }

    @Test
    fun `reports unavailable configuration when either backend contract fails`() = runTest {
        val filterFailure = viewModel(filterFails = true)
        assertEquals(emptyList<Int>(), filterFailure.state.value.betOptions)
        assertEquals("Configuração do filtro indisponível", filterFailure.state.value.configurationError)

        val bettingFailure = viewModel(bettingFails = true)
        assertEquals(emptyList<MarketplaceFilterModalityUi>(), bettingFailure.state.value.modalities)
        assertEquals("Configuração do filtro indisponível", bettingFailure.state.value.configurationError)
        bettingFailure.loadParameters()
        assertEquals("Configuração do filtro indisponível", bettingFailure.state.value.configurationError)
    }

    @Test
    fun `emits filter with selected modality identifiers and rejects invalid filter`() = runTest {
        val viewModel = viewModel(filterParameters = MarketplaceFilterParameters(1, 2))
        val mega = viewModel.state.value.modalities.last()
        val applied = async(start = CoroutineStart.UNDISPATCHED) { viewModel.appliedFilters.first() }
        viewModel.onAction(MarketplaceFilterAction.SelectModality(mega))
        viewModel.onAction(MarketplaceFilterAction.ChangeMinimumShares("2"))
        viewModel.onAction(MarketplaceFilterAction.ChangeMaximumShares("4"))
        viewModel.onAction(MarketplaceFilterAction.Apply)
        assertEquals(2, applied.await().minimumShares)
        assertEquals(2, applied.await().modalityId)

        viewModel.onAction(MarketplaceFilterAction.ChangeMinimumShares("9"))
        viewModel.onAction(MarketplaceFilterAction.ChangeMaximumShares("1"))
        viewModel.onAction(MarketplaceFilterAction.Apply)
        assertEquals("A quantidade mínima de cotas não pode ser maior que a máxima", viewModel.state.value.validationMessage)
        viewModel.onAction(MarketplaceFilterAction.Back)
    }

    @Test
    fun `searches retailers at six characters and filters cached results afterwards`() = runTest {
        val queries = mutableListOf<String>()
        val center = retailer(101, "Lotérica Centro")
        val south = retailer(102, "Lotérica Sul")
        val viewModel = viewModel(retailerSearch = { query ->
            queries += query
            AppResult.Success(listOf(center, south))
        })

        viewModel.onAction(MarketplaceFilterAction.ChangeLottery("00010"))
        assertTrue(queries.isEmpty())
        assertTrue(viewModel.state.value.retailerSuggestions.isEmpty())

        viewModel.onAction(MarketplaceFilterAction.ChangeLottery("000101"))
        assertEquals(listOf("000101"), queries)
        assertEquals(listOf(center, south), viewModel.state.value.retailerSuggestions)
        assertFalse(viewModel.state.value.retailerLoading)

        viewModel.onAction(MarketplaceFilterAction.ChangeLottery("Lotérica Centro"))
        assertEquals(listOf(center), viewModel.state.value.retailerSuggestions)
        viewModel.onAction(MarketplaceFilterAction.SelectRetailer(center))
        assertEquals(center, viewModel.state.value.selectedRetailer)
        assertTrue(viewModel.state.value.retailerSuggestions.isEmpty())
    }

    @Test
    fun `shows empty search feedback and clears loading after retailer failure`() = runTest {
        val empty = viewModel(retailerSearch = { AppResult.Success(emptyList()) })
        empty.onAction(MarketplaceFilterAction.ChangeLottery("000101"))
        assertEquals("Sua busca não retornou resultados.", empty.state.value.retailerMessage)

        val failure = viewModel(retailerSearch = { AppResult.Failure(AppError.Network) })
        failure.onAction(MarketplaceFilterAction.ChangeLottery("000101"))
        assertFalse(failure.state.value.retailerLoading)
        assertTrue(failure.state.value.retailerSuggestions.isEmpty())
    }

    @Test fun `opening restores applied filter and clearing restores the original entry modality`() = runTest {
        val applied = br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter(
            "10.50", "20.00", 2, 10, 4, 7, "", "", listOf(1, 2), listOf(3), 2, 1)
        val vm = viewModel(initial = applied, clean = br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter.forModality(2, 1))
        assertEquals(2, vm.state.value.selectedModality?.id)
        assertEquals("10,50", vm.state.value.minimumValue)
        assertEquals(4, vm.state.value.selectedBetQuantity)
        assertEquals(listOf(1, 2), vm.state.value.wantedNumbers)
        vm.onAction(MarketplaceFilterAction.Clear)
        assertEquals(2, vm.state.value.selectedModality?.id)
        assertEquals(1, vm.state.value.selectedModality?.contestType)
        assertEquals("", vm.state.value.minimumValue)
        assertNull(vm.state.value.selectedBetQuantity)
        assertEquals(emptyList<Int>(), vm.state.value.wantedNumbers)
    }

    @Test fun `retailer selection switches the modality constraint to all like CX`() = runTest {
        val vm = viewModel(initial = br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter.forModality(2, 1))
        assertFalse(vm.state.value.modalities.any { it.isAll })
        val modality = vm.state.value.modalities.first()
        vm.onAction(MarketplaceFilterAction.SelectModality(modality))
        vm.onAction(MarketplaceFilterAction.SelectRetailer(retailer(101, "Centro")))
        assertTrue(vm.state.value.modalities.first().isAll)
        assertNull(vm.state.value.selectedModality)
        assertNull(vm.state.value.toDomain().modalityId)
        assertNull(vm.state.value.toDomain().contestType)
        vm.onAction(MarketplaceFilterAction.ChangeLottery(""))
        assertFalse(vm.state.value.modalities.any { it.isAll })
    }

    @Test fun `form actions and retailer selection reset dependent modality filters`() = runTest {
        val vm = viewModel()
        val mega = vm.state.value.modalities.last()
        vm.onAction(MarketplaceFilterAction.SelectModality(mega))
        vm.onAction(MarketplaceFilterAction.SelectBetQuantity(7))
        vm.onAction(MarketplaceFilterAction.SelectNumberQuantity(10))
        vm.onAction(MarketplaceFilterAction.ChangeWantedNumbers(listOf(5, 2, 5)))
        vm.onAction(MarketplaceFilterAction.ChangeUnwantedNumbers(listOf(9, 3)))

        vm.onAction(MarketplaceFilterAction.SelectModality(mega))
        assertEquals(mega, vm.state.value.selectedModality)
        assertNull(vm.state.value.selectedBetQuantity)
        assertNull(vm.state.value.selectedNumberQuantity)
        assertEquals(emptyList<Int>(), vm.state.value.wantedNumbers)
        assertEquals(emptyList<Int>(), vm.state.value.unwantedNumbers)
        assertEquals(mega.numberOptions, vm.state.value.numberOptions)

        val retailer = retailer(101, "Centro")
        vm.onAction(MarketplaceFilterAction.SelectBetQuantity(7))
        vm.onAction(MarketplaceFilterAction.SelectNumberQuantity(10))
        vm.onAction(MarketplaceFilterAction.ChangeWantedNumbers(listOf(2, 5)))
        vm.onAction(MarketplaceFilterAction.ChangeUnwantedNumbers(listOf(3, 9)))
        vm.onAction(MarketplaceFilterAction.SelectRetailer(retailer))
        assertNull(vm.state.value.selectedModality)
        assertNull(vm.state.value.selectedBetQuantity)
        assertNull(vm.state.value.selectedNumberQuantity)
        assertTrue(vm.state.value.wantedNumbers.isEmpty())
        assertTrue(vm.state.value.unwantedNumbers.isEmpty())
        assertEquals((5..26).toList(), vm.state.value.numberOptions)
        assertEquals(retailer, vm.state.value.selectedRetailer)
        assertTrue(vm.state.value.modalities.first().isAll)
        assertEquals(emptyList<Retailer>(), vm.state.value.retailerSuggestions)

        vm.onAction(MarketplaceFilterAction.ChangeLottery("000102"))
        assertNull(vm.state.value.selectedRetailer)
        assertEquals("000102", vm.state.value.lottery)

        vm.onAction(MarketplaceFilterAction.SelectModality(vm.state.value.modalities.first()))
        assertNull(vm.state.value.selectedModality)
        assertTrue(vm.state.value.allModalitiesHighlighted)
        assertNull(vm.state.value.selectedBetQuantity)
        assertNull(vm.state.value.selectedNumberQuantity)
    }

    @Test fun `clear preserves parameter failure and disabled retailer mode`() = runTest {
        val vm = viewModel(filterFails = true, lotteryEnabled = false)
        vm.onAction(MarketplaceFilterAction.Clear)
        assertFalse(vm.state.value.lotteryEnabled)
        assertEquals("Configuração do filtro indisponível", vm.state.value.configurationError)
        assertFalse(vm.state.value.canApply)
    }

    @Test fun `opening restores the matching retailer display without accepting a different retailer`() = runTest {
        val applied = br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter.empty().withLottery("101")
        val retained = retailer(101, "Centro")
        val vm = viewModel(initial = applied, initialRetailer = retained)
        assertEquals(retained, vm.state.value.selectedRetailer)
        assertEquals(retailerDisplayLabel(retained), vm.state.value.lottery)
        assertEquals("101", vm.state.value.toDomain().lottery)
        val wrong = viewModel(initial = applied, initialRetailer = retailer(102, "Outro"))
        assertNull(wrong.state.value.selectedRetailer)
        assertEquals("101", wrong.state.value.toDomain().lottery)
        wrong.onAction(MarketplaceFilterAction.ChangeLottery("Centro"))
        assertEquals("", wrong.state.value.toDomain().lottery)
    }

    @Test fun `back without applying clears constraints using current search modality instead of edited or clean modality`() = runTest {
        val initial = br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter(
            "10", "20", 2, 10, 4, 7, "101", "", listOf(1), listOf(2), 2, 1)
        val clean = br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter.forModality(8, 2)
        val vm = viewModel(initial = initial, clean = clean)
        vm.onAction(MarketplaceFilterAction.ChangeMinimumValue("15,00"))
        vm.onAction(MarketplaceFilterAction.SelectModality(MarketplaceFilterModalityUi(8, "Lotofácil", "Lotofácil", 2, listOf(15), 25)))
        val result = vm.filterAfterBack()
        assertEquals(br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter.forModality(2, 1), result)
        assertEquals(8, vm.state.value.selectedModality?.id)
    }

    @Test fun `apply emits inverted money range unchanged while invalid shares still block it`() = runTest {
        val vm = viewModel()
        vm.onAction(MarketplaceFilterAction.ChangeMinimumValue("500,00"))
        vm.onAction(MarketplaceFilterAction.ChangeMaximumValue("10,00"))
        vm.onAction(MarketplaceFilterAction.ChangeMinimumShares("2"))
        vm.onAction(MarketplaceFilterAction.ChangeMaximumShares("100"))
        val applied = async(start = CoroutineStart.UNDISPATCHED) { vm.appliedFilters.first() }
        vm.onAction(MarketplaceFilterAction.Apply)
        assertNull("CX does not validate the money interval locally", vm.state.value.validationMessage)
        testScheduler.runCurrent()
        assertTrue("CX sends the money criteria to the backend", applied.isCompleted)
        assertEquals("500.00", applied.await().minimumValue)
        assertEquals("10.00", applied.await().maximumValue)
        vm.onAction(MarketplaceFilterAction.ChangeMinimumShares("1"))
        assertFalse(vm.state.value.canApply)
        vm.onAction(MarketplaceFilterAction.Apply)
        assertEquals("Quantidade mínima não pode ser menor que 2", vm.state.value.validationMessage)
    }

    @Test fun `erasing retailer restores previous modality and clears dependent filters`() = runTest {
        val vm = viewModel(initial = br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter.forModality(2, 1))
        vm.onAction(MarketplaceFilterAction.SelectRetailer(retailer(101, "Centro")))
        vm.onAction(MarketplaceFilterAction.SelectBetQuantity(4))
        vm.onAction(MarketplaceFilterAction.SelectNumberQuantity(10))
        vm.onAction(MarketplaceFilterAction.ChangeWantedNumbers(listOf(1)))
        vm.onAction(MarketplaceFilterAction.ChangeUnwantedNumbers(listOf(2)))

        vm.onAction(MarketplaceFilterAction.ChangeLottery(""))

        assertEquals(2, vm.state.value.toDomain().modalityId)
        assertEquals(1, vm.state.value.toDomain().contestType)
        assertEquals("", vm.state.value.toDomain().lottery)
        assertFalse(vm.state.value.modalities.any { it.isAll })
        assertNull(vm.state.value.selectedBetQuantity)
        assertNull(vm.state.value.selectedNumberQuantity)
        assertTrue(vm.state.value.wantedNumbers.isEmpty())
        assertTrue(vm.state.value.unwantedNumbers.isEmpty())
    }

    @Test fun `erasing retailer restores last selected special rather than first modality`() = runTest {
        val parameters = bettingParameters().copy(availablePoolModalities = listOf(
            PoolModalityAvailability(2, "MEGA_SENA", "NORMAL"),
            PoolModalityAvailability(2, "MEGA_SENA", "ESPECIAL"),
        ))
        val vm = viewModel(parameters = parameters,
            initial = br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter.forModality(2, 1))
        vm.onAction(MarketplaceFilterAction.SelectModality(vm.state.value.modalities.last()))
        vm.onAction(MarketplaceFilterAction.SelectRetailer(retailer(101, "Centro")))
        vm.onAction(MarketplaceFilterAction.ChangeLottery(""))
        assertEquals(2, vm.state.value.toDomain().modalityId)
        assertEquals(2, vm.state.value.toDomain().contestType)
    }

    @Test fun `oversized minimum shares blocks application instead of losing constraint`() = runTest {
        val vm = viewModel()
        vm.onAction(MarketplaceFilterAction.ChangeMinimumShares("2147483648"))
        assertEquals("Quantidade mínima não pode ser maior que a máxima", vm.state.value.minimumSharesError)
        assertFalse(vm.state.value.canApply)
        val emitted = async(start = CoroutineStart.UNDISPATCHED) { vm.appliedFilters.first() }
        vm.onAction(MarketplaceFilterAction.Apply)
        testScheduler.runCurrent()
        assertFalse(emitted.isCompleted)
        assertEquals("Quantidade mínima não pode ser maior que a máxima", vm.state.value.validationMessage)
        emitted.cancel()
    }

    @Test fun `oversized maximum shares blocks application instead of losing constraint`() = runTest {
        val vm = viewModel()
        vm.onAction(MarketplaceFilterAction.ChangeMaximumShares("99999999999999"))
        assertEquals("Quantidade máxima não pode ser maior que 100", vm.state.value.maximumSharesError)
        assertFalse(vm.state.value.canApply)
        val emitted = async(start = CoroutineStart.UNDISPATCHED) { vm.appliedFilters.first() }
        vm.onAction(MarketplaceFilterAction.Apply)
        testScheduler.runCurrent()
        assertFalse(emitted.isCompleted)
        assertEquals("Quantidade máxima não pode ser maior que 100", vm.state.value.validationMessage)
        emitted.cancel()
    }

    @Test fun `loads temporary contest configuration for available pool modality`() = runTest {
        val parameters = bettingParameters().copy(availablePoolModalities = listOf(
            PoolModalityAvailability(2, "MEGA_SENA", "ESPECIAL", contestNumber = 3000)))
        val vm = viewModel(parameters = parameters, specialContests = GetMarketplaceSpecialContestsUseCase {
            AppResult.Success(MarketplaceSpecialContests(megaThirtyYearsContest = 3000))
        })
        assertEquals("mega-sena\n30 anos", vm.state.value.modalities.single().name)
        assertNull(vm.state.value.configurationError)
    }

    @Test fun `unavailable optional special configuration preserves filter availability`() = runTest {
        val vm = viewModel(specialContests = GetMarketplaceSpecialContestsUseCase {
            AppResult.Failure(AppError.Network)
        })
        assertEquals("mega sena", vm.state.value.modalities.single().name)
        assertNull(vm.state.value.configurationError)
        assertTrue(vm.state.value.canApply)
    }

    private fun viewModel(
        filterParameters: MarketplaceFilterParameters = MarketplaceFilterParameters(1, 15),
        filterFails: Boolean = false,
        bettingFails: Boolean = false,
        parameters: BettingParameters = bettingParameters(),
        retailerSearch: suspend (String) -> AppResult<List<Retailer>> = { AppResult.Success(emptyList()) },
        initial: br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter = br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter.empty(),
        clean: br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter = br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter.forModality(initial.modalityId ?: 0, initial.contestType ?: 0),
        lotteryEnabled: Boolean = true,
        initialRetailer: Retailer? = null,
        specialContests: GetMarketplaceSpecialContestsUseCase? = null,
    ) = MarketplaceFilterViewModel(
        getFilterParameters = GetMarketplaceFilterParameters(configurationRepository {
            if (filterFails) error("offline") else filterParameters
        }),
        getBettingParameters = GetBettingParametersUseCase {
            if (bettingFails) AppResult.Failure(AppError.Network) else AppResult.Success(parameters)
        },
        searchRetailers = SearchRetailersUseCase(retailerSearch),
        dispatchers = FixedCoroutineDispatchers(dispatcher),
        initialFilter = initial,
        clearFilter = clean,
        lotteryEnabled = lotteryEnabled,
        initialRetailer = initialRetailer,
        getSpecialContests = specialContests,
    )

    private fun bettingParameters() = BettingParameters(
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        0,
        games = listOf(
            LotteryGameParameters(
                LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
                1,
                "",
                60,
                6,
                20,
                0,
                emptyList(),
                listOf(BetPrice(6, 0, BigDecimal.ONE), BetPrice(7, 0, BigDecimal.TEN)),
            ),
        ),
        availablePoolModalities = listOf(PoolModalityAvailability(2, "MEGA_SENA", "NORMAL")),
    )

    private fun retailer(id: Long, name: String) =
        Retailer(id, "0001-01", name, "", "", "", "", "", false)

    private fun configurationRepository(
        parameters: suspend () -> MarketplaceFilterParameters,
    ) = object : AppConfigurationRepository {
        override suspend fun isVersionSupported(versionName: String) = true
        override suspend fun marketplaceFilterParameters() = parameters()
    }
}
