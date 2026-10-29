package br.gov.caixa.loterias.feature.results

import br.gov.caixa.loterias.feature.results.presentation.state.HomeResultsAction
import br.gov.caixa.loterias.feature.results.presentation.state.HomeResultsLoadingAction
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsAction
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import br.gov.caixa.loterias.feature.results.presentation.state.reduceHomeResultsLoadingState
import br.gov.caixa.loterias.feature.results.presentation.state.reduceHomeResultsState
import br.gov.caixa.loterias.feature.results.presentation.state.reduceResultsState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultsReducerTest {
    @Test
    fun `historical failure clears contest bounds for latest fallback`() {
        val failed = reduceResultsState(
            ResultsUiState(
                modality = "MEGA_SENA",
                contestInput = "10",
                latestContest = 20,
                isLoading = true,
                errorMessage = "old"
            ),
            ResultsAction.HistoricalLoadFailed
        )
        assertEquals("MEGA_SENA", failed.modality)
        assertEquals("", failed.contestInput)
        assertEquals(null, failed.latestContest)
        assertEquals(null, failed.errorMessage)
        assertFalse(failed.isLoading)
    }

    @Test
    fun `home loading and cache reducers retain independent contracts`() {
        assertTrue(reduceHomeResultsLoadingState(false, HomeResultsLoadingAction.LoadStarted))
        assertFalse(reduceHomeResultsLoadingState(true, HomeResultsLoadingAction.JobsChanged(false)))
        assertTrue(reduceHomeResultsState(emptyMap(), HomeResultsAction.RefreshStarted("MEGA_SENA")).isEmpty())
        val input = reduceResultsState(ResultsUiState(), ResultsAction.ContestChanged("12a3"))
        assertEquals("123", input.contestInput)
    }
}
