package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.RetailSearchViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class RetailSearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val retailer = Retailer(1, "001", "Centro", "", "Rua", "Bairro", "Cidade", "DF", false)
    private var searchResult: AppResult<List<Retailer>> = AppResult.Success(listOf(retailer))
    private var favoriteResult: AppResult<Unit> = AppResult.Success(Unit)
    private val queries = mutableListOf<String>()
    private var mutation: Pair<Long, Boolean>? = null

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `query enforces legacy minimum and debounces latest search`() = runTest(dispatcher) {
        val viewModel = viewModel()
        viewModel.changeQuery("ab")
        assertTrue(viewModel.state.value.retailers.isEmpty())
        viewModel.searchNow()
        assertTrue(viewModel.state.value.errorMessage!!.contains("3"))
        viewModel.dismissError()
        assertNull(viewModel.state.value.errorMessage)

        viewModel.changeQuery("Centro")
        advanceTimeBy(200)
        viewModel.changeQuery("Praça")
        advanceTimeBy(399)
        assertTrue(queries.isEmpty())
        advanceUntilIdle()
        assertEquals(listOf("Praça"), queries)
        assertEquals(listOf(retailer), viewModel.state.value.retailers)
        assertTrue(viewModel.state.value.searched)
        assertFalse(viewModel.state.value.loading)
    }

    @Test fun `explicit search supports success current-query guard and failure`() = runTest(dispatcher) {
        val viewModel = viewModel()
        viewModel.changeQuery("Centro")
        viewModel.searchNow()
        advanceUntilIdle()
        assertEquals(listOf("Centro"), queries)

        searchResult = AppResult.Failure(AppError.Network)
        viewModel.searchNow()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.retailers.isEmpty())
        assertTrue(viewModel.state.value.errorMessage!!.contains("buscar"))
    }

    @Test fun `favorite confirmation adds removes cancels and maps errors`() = runTest(dispatcher) {
        val viewModel = viewModel()
        viewModel.changeQuery("Centro"); viewModel.searchNow(); advanceUntilIdle()
        viewModel.confirmToggleFavorite()
        viewModel.requestToggleFavorite(retailer)
        viewModel.cancelToggleFavorite()
        assertNull(viewModel.state.value.favoriteCandidate)

        viewModel.requestToggleFavorite(retailer)
        viewModel.confirmToggleFavorite(); advanceUntilIdle()
        assertEquals(1L to true, mutation)
        assertTrue(viewModel.state.value.retailers.single().favorite)

        favoriteResult = AppResult.Failure(AppError.Unexpected)
        val favorite = viewModel.state.value.retailers.single()
        viewModel.requestToggleFavorite(favorite)
        viewModel.confirmToggleFavorite(); advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("remover"))

        viewModel.dismissError()
        viewModel.requestToggleFavorite(retailer)
        viewModel.confirmToggleFavorite(); advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.contains("favoritar"))
    }

    private fun viewModel() = RetailSearchViewModel(
        SearchRetailersUseCase { queries += it; searchResult },
        SetRetailerFavoriteUseCase { id, favorite -> mutation = id to favorite; favoriteResult },
        FixedCoroutineDispatchers(dispatcher),
    )
}
