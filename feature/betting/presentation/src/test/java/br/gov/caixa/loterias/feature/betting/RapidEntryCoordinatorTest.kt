package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingTarget
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.RapidEntryCoordinator
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RapidEntryCoordinatorTest {
    @Test fun `suspended account never enables rapid private screen`() = runTest {
        val coordinator = RapidEntryCoordinator(
            PrepareHomeBettingActionUseCase {
                assertEquals(HomeBettingAction.RAPID, it)
                AppResult.Failure(AppError.Validation("Há uma suspensão temporária ativa para compras."))
            },
            isAuthenticated = true,
            showList = true
        )
        assertNull(coordinator.state.value)
        coordinator.prepare()
        assertTrue(coordinator.state.value is AppResult.Failure)
    }

    @Test fun `visitor selects tutorial without checking account or backend`() = runTest {
        listOf(false, true).forEach { completed ->
            val coordinator = RapidEntryCoordinator(
                PrepareHomeBettingActionUseCase { error("Private account read") },
                isAuthenticated = false,
                showList = completed
            )
            coordinator.prepare()
            assertEquals(
                AppResult.Success(
                    HomeBettingDecision.Ready(if (completed) HomeBettingTarget.RAPID_LIST else HomeBettingTarget.RAPID_CONFIGURATION)
                ),
                coordinator.state.value
            )
        }
    }

    @Test fun `successful account gate exposes tutorial decision only for current lease`() = runTest {
        var current = true
        val response = CompletableDeferred<AppResult<HomeBettingDecision>>()
        val coordinator = RapidEntryCoordinator(
            PrepareHomeBettingActionUseCase { response.await() },
            isAuthenticated = true,
            showList = false,
            isSessionCurrent = { current }
        )
        val job = launch { coordinator.prepare() }
        runCurrent()
        current = false
        response.complete(AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.RAPID_LIST)))
        job.join()
        assertNull(coordinator.state.value)
    }
}
