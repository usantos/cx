package br.gov.caixa.loterias.feature.marketplacefilter

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.BetPrice
import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.betting.PoolModalityAvailability
import br.gov.caixa.loterias.core.domain.configuration.AppConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.GetMarketplaceFilterParameters
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilterParameters
import br.gov.caixa.loterias.core.domain.retail.Retailer
import br.gov.caixa.loterias.core.domain.retail.SearchRetailersUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class MarketplaceFilterViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    private val dispatcher = UnconfinedTestDispatcher()

    @Test
    fun `loads bet limits and available modalities from both backend contracts`() = runTest {
        val viewModel = viewModel(filterParameters = MarketplaceFilterParameters(3, 6))

        assertEquals(listOf(3, 4, 5, 6), viewModel.state.value.betOptions)
        assertEquals(listOf("Todas", "Mega-Sena"), viewModel.state.value.modalities.map { it.name })
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

    private fun viewModel(
        filterParameters: MarketplaceFilterParameters = MarketplaceFilterParameters(1, 15),
        filterFails: Boolean = false,
        bettingFails: Boolean = false,
        retailerSearch: suspend (String) -> AppResult<List<Retailer>> = { AppResult.Success(emptyList()) },
    ) = MarketplaceFilterViewModel(
        getFilterParameters = GetMarketplaceFilterParameters(configurationRepository {
            if (filterFails) error("offline") else filterParameters
        }),
        getBettingParameters = GetBettingParametersUseCase {
            if (bettingFails) AppResult.Failure(AppError.Network) else AppResult.Success(bettingParameters())
        },
        searchRetailers = SearchRetailersUseCase(retailerSearch),
        dispatchers = FixedCoroutineDispatchers(dispatcher),
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
