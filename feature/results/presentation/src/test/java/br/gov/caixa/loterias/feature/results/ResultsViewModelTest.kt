package br.gov.caixa.loterias.feature.results

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.repository.ResultsRepository
import br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResult
import br.gov.caixa.loterias.feature.results.domain.usecase.GetResultByContest
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import br.gov.caixa.loterias.feature.results.presentation.viewmodel.ResultsViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResultsViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test fun `failed explicit contest falls back to latest draw as native results`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<String>()
        val model = ResultsViewModel(
            {
                calls += "latest"
                br.gov.caixa.loterias.core.common.result.AppResult.Success(result(100))
            },
            { _, contest ->
                calls += "contest:$contest"
                br.gov.caixa.loterias.core.common.result.AppResult.Failure(br.gov.caixa.loterias.core.common.result.AppError.Network)
            },
            FixedCoroutineDispatchers(dispatcherRule.dispatcher)
        )
        model.selectModality("megasena")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.changeContest("77")
        model.searchContest()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("latest", "contest:77", "latest"), calls)
        assertEquals(100, model.state.value.result?.contest)
        assertEquals("100", model.state.value.contestInput)
        assertNull(model.state.value.errorMessage)
    }

    @Test fun `contest above latest uses exact native message`() = runTest(dispatcherRule.dispatcher) {
        val model = viewModel(repository(mutableListOf()))
        model.selectModality("megasena")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.changeContest("101")
        model.searchContest()
        assertEquals("O último concurso apurado é o 100", model.state.value.inputError)
    }

    @Test fun `failed home draws wait for explicit refresh instead of retrying on reentry`() = runTest(dispatcherRule.dispatcher) {
        var requests = 0
        val model = ResultsViewModel(
            {
                requests++
                br.gov.caixa.loterias.core.common.result.AppResult.Failure(br.gov.caixa.loterias.core.common.result.AppError.Network)
            },
            { _, _ -> error("No detail lookup expected") },
            FixedCoroutineDispatchers(dispatcherRule.dispatcher)
        )
        model.loadHomeResults(listOf("megasena"))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.loadHomeResults(listOf("megasena"))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, requests)
        model.loadHomeResults(listOf("megasena"), refresh = true)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, requests)
    }

    @Test
    fun `new home parameters refresh cached draws`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<String>()
        val model = viewModel(repository(calls))
        model.loadHomeResults(listOf("megasena"))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.loadHomeResults(listOf("megasena"), refresh = true)
        assertEquals(emptyMap<String, DrawResult>(), model.homeResults.value)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("latest:megasena", "latest:megasena"), calls)
        assertEquals(100, model.homeResults.value["megasena"]?.contest)
    }

    @Test
    fun `home loads separate results without changing detail selection or repeating requests`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<String>()
        val model = viewModel(repository(calls))
        model.loadHomeResults(listOf("megasena", "quina", "megasena"))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("latest:megasena", "latest:quina"), calls)
        assertEquals(setOf("megasena", "quina"), model.homeResults.value.keys)
        assertNull(model.state.value.modality)
        model.loadHomeResults(listOf("megasena", "quina"))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, calls.size)
    }

    @Test
    fun `selecting modality loads latest and sanitizes contest`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<String>()
        val viewModel = viewModel(repository(calls))

        viewModel.selectModality("megasena")
        viewModel.selectModality("quina") // Ignorado pela proteção contra requisições em andamento.
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf("latest:megasena"), calls)
        assertEquals("megasena", viewModel.state.value.modality)
        assertEquals("100", viewModel.state.value.contestInput)
        assertEquals(100, viewModel.state.value.latestContest)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessage)

        viewModel.changeContest(" 12a3 ")
        assertEquals("123", viewModel.state.value.contestInput)
        assertNull(viewModel.state.value.inputError)
    }

    @Test
    fun `search validates then requests contest`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<String>()
        val viewModel = viewModel(repository(calls))

        viewModel.searchContest()
        assertEquals("Informe uma modalidade e um concurso válido.", viewModel.state.value.inputError)
        viewModel.selectModality("quina")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.changeContest("0")
        viewModel.searchContest()
        assertTrue(viewModel.state.value.inputError != null)
        viewModel.changeContest("101")
        viewModel.searchContest()
        assertEquals("O último concurso apurado é o 100", viewModel.state.value.inputError)
        viewModel.dismissInputError()
        assertNull(viewModel.state.value.inputError)
        viewModel.changeContest("55")
        viewModel.searchContest()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf("latest:quina", "contest:quina:55"), calls)
        assertEquals(55, viewModel.state.value.result?.contest)
    }

    @Test
    fun `previous and next contests respect latest result boundary`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<String>()
        val viewModel = viewModel(repository(calls))

        viewModel.previousContest()
        viewModel.nextContest()
        viewModel.selectModality("megasena")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.nextContest()
        assertEquals(listOf("latest:megasena"), calls)

        viewModel.previousContest()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(99, viewModel.state.value.result?.contest)
        viewModel.nextContest()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(100, viewModel.state.value.result?.contest)
        assertEquals(
            listOf("latest:megasena", "contest:megasena:99", "contest:megasena:100"),
            calls
        )
    }

    @Test
    fun `previous contest never requests zero`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<String>()
        val viewModel = viewModel(object : ResultsRepository {
            override suspend fun latest(modality: String) = result(1).also { calls += "latest" }
            override suspend fun byContest(modality: String, contest: Int) = result(contest).also { calls += "contest" }
        })

        viewModel.selectModality("quina")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.previousContest()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("latest"), calls)
    }

    @Test
    fun `confirmed bet loads its exact contest and validates input`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<String>()
        val viewModel = viewModel(repository(calls))

        viewModel.loadContest("", 0)
        assertEquals("Informe uma modalidade e um concurso válido.", viewModel.state.value.inputError)
        viewModel.loadContest("lotofacil", 3779)
        viewModel.loadContest("quina", 10) // Ignorado enquanto a primeira requisição está ativa.
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf("contest:lotofacil:3779"), calls)
        assertEquals("lotofacil", viewModel.state.value.modality)
        assertEquals("3779", viewModel.state.value.contestInput)
        assertNull(viewModel.state.value.inputError)
    }

    @Test
    fun `retry handles latest and contest failures`() = runTest(dispatcherRule.dispatcher) {
        var failures = 2
        val calls = mutableListOf<String>()
        val repository = object : ResultsRepository {
            override suspend fun latest(modality: String): DrawResult {
                calls += "latest"
                if (failures-- > 0) error("offline")
                return result(100)
            }
            override suspend fun byContest(modality: String, contest: Int): DrawResult {
                calls += "contest"
                if (failures-- > 0) error("offline")
                return result(contest)
            }
        }
        val viewModel = viewModel(repository)
        viewModel.retry() // Sem modalidade: nenhuma requisição.
        viewModel.selectModality("megasena")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage != null)
        viewModel.retry()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage != null)
        viewModel.retry()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(100, viewModel.state.value.result?.contest)

        viewModel.changeContest("77")
        viewModel.retry()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(77, viewModel.state.value.result?.contest)
        assertEquals(listOf("latest", "latest", "latest", "contest"), calls)
    }

    @Test
    fun `back to modalities clears contest and errors before another selection`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = viewModel(repository(mutableListOf()))
        viewModel.selectModality("megasena")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.changeContest("0")
        viewModel.searchContest()

        viewModel.clearSelection()
        assertEquals(ResultsUiState(), viewModel.state.value)
        viewModel.selectModality("quina")
        assertNull(viewModel.state.value.result)
        assertNull(viewModel.state.value.latestContest)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("quina", viewModel.state.value.modality)
        assertEquals(100, viewModel.state.value.result?.contest)
    }

    @Test
    fun `leaving a loading result prevents its late response from replacing a new modality`() = runTest(dispatcherRule.dispatcher) {
        val oldResponse = CompletableDeferred<DrawResult>()
        val repository = object : ResultsRepository {
            override suspend fun latest(modality: String): DrawResult = if (modality == "megasena") {
                withContext(NonCancellable) { oldResponse.await() }
            } else {
                result(200)
            }
            override suspend fun byContest(modality: String, contest: Int) = result(contest)
        }
        val viewModel = viewModel(repository)
        viewModel.selectModality("megasena")
        dispatcherRule.dispatcher.scheduler.runCurrent()
        assertTrue(viewModel.state.value.isLoading)
        viewModel.clearSelection()
        assertEquals(ResultsUiState(), viewModel.state.value)
        viewModel.selectModality("quina")
        dispatcherRule.dispatcher.scheduler.runCurrent()
        assertEquals(200, viewModel.state.value.result?.contest)

        oldResponse.complete(result(100))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("quina", viewModel.state.value.modality)
        assertEquals(200, viewModel.state.value.result?.contest)
        assertFalse(viewModel.state.value.isLoading)
    }

    private fun repository(calls: MutableList<String>) = object : ResultsRepository {
        override suspend fun latest(modality: String): DrawResult {
            calls += "latest:$modality"
            return result(100)
        }
        override suspend fun byContest(modality: String, contest: Int): DrawResult {
            calls += "contest:$modality:$contest"
            return result(contest)
        }
    }

    private fun viewModel(repository: ResultsRepository) = ResultsViewModel(
        GetLatestResult(repository),
        GetResultByContest(repository),
        FixedCoroutineDispatchers(dispatcherRule.dispatcher)
    )

    private fun result(contest: Int) = DrawResult(
        modality = "Mega-Sena",
        contest = contest,
        drawDate = "01/01/2026",
        drawLocation = "Brasília - DF",
        firstDrawNumbers = listOf(1, 2, 3),
        secondDrawNumbers = emptyList(),
        clovers = emptyList(),
        accumulated = false,
        accumulatedNextContest = "0",
        totalRevenue = "100",
        notice = "",
        payouts = emptyList()
    )
}
