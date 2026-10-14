package br.gov.caixa.loterias.feature.results

import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.results.*
import br.gov.caixa.loterias.core.domain.service.*
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConfirmedBetResultsViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()
    private val bet = ConfirmedBet(7, "Mega Sena", 2800, "", "Não premiada", listOf(1,2), recurringDraws = 2)
    @Test fun `non awarded recurring bet loads all contest results`() = runTest(dispatcherRule.dispatcher) {
        val results = listOf(2800,2801).map { DrawResult("Mega Sena", it, "", "", listOf(1,2), emptyList(), emptyList(), false, "", "", "", emptyList()) }
        val model = ConfirmedBetResultsViewModel(bet, GetConfirmedBetResultsUseCase {
            assertEquals(bet, it); ServiceResult.Success(results)
        }, FixedCoroutineDispatchers(dispatcherRule.dispatcher))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(results, model.state.value.results)
        assertFalse(model.state.value.loading)
    }
    @Test fun `business redirect retains typed destination without invented results`() = runTest(dispatcherRule.dispatcher) {
        val failure = ServiceFailure.RedirectRequired(ServiceRedirect.ACCEPT_TERMS)
        val model = ConfirmedBetResultsViewModel(bet, GetConfirmedBetResultsUseCase { ServiceResult.Rejected(failure) }, FixedCoroutineDispatchers(dispatcherRule.dispatcher))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(failure, model.state.value.serviceFailure)
        assertTrue(model.state.value.results.isEmpty())
    }
    @Test fun `retry is deduplicated synchronously until current query finishes`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val pending = kotlinx.coroutines.CompletableDeferred<ServiceResult<List<DrawResult>>>()
        val model = ConfirmedBetResultsViewModel(bet, GetConfirmedBetResultsUseCase {
            calls++; pending.await()
        }, FixedCoroutineDispatchers(dispatcherRule.dispatcher))
        assertTrue(model.state.value.loading)
        model.retry(); model.retry()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, calls)
        pending.complete(ServiceResult.Success(emptyList()))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(model.state.value.loading)
        model.retry()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, calls)
    }
    @Test fun `service business notice can retry and recover without stale decision`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val notice = ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, "Ainda não disponível"))
        val model = ConfirmedBetResultsViewModel(bet, GetConfirmedBetResultsUseCase {
            if (calls++ == 0) ServiceResult.Rejected(notice) else ServiceResult.Success(emptyList())
        }, FixedCoroutineDispatchers(dispatcherRule.dispatcher))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(notice, model.state.value.serviceFailure)
        model.retry()
        assertNull(model.state.value.serviceFailure)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(model.state.value.serviceFailure)
        assertNull(model.state.value.errorMessage)
    }
    @Test fun `validation network and unexpected failures retain their distinct retry messages`() = runTest(dispatcherRule.dispatcher) {
        val cases = listOf(
            br.gov.caixa.loterias.core.common.AppError.Validation("Concurso inválido") to "Concurso inválido",
            br.gov.caixa.loterias.core.common.AppError.Network to "Não foi possível acessar o serviço. Verifique sua conexão.",
            br.gov.caixa.loterias.core.common.AppError.Unexpected to "Não foi possível consultar o resultado. Tente novamente.",
        )
        cases.forEach { (error, message) ->
            val model = ConfirmedBetResultsViewModel(bet, GetConfirmedBetResultsUseCase { ServiceResult.Failed(error) },
                FixedCoroutineDispatchers(dispatcherRule.dispatcher))
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(message, model.state.value.errorMessage)
            assertFalse(model.state.value.loading)
            assertTrue(model.state.value.results.isEmpty())
        }
    }

}
