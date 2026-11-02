package br.gov.caixa.loterias.feature.account

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.model.PersonalDataUpdate
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.RecalculateAuthorizedLimitUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalDataUseCase
import br.gov.caixa.loterias.feature.account.presentation.screen.canUpdatePersonalData
import br.gov.caixa.loterias.feature.account.presentation.screen.dailyLimitValidationMessage
import br.gov.caixa.loterias.feature.account.presentation.screen.formatCpf
import br.gov.caixa.loterias.feature.account.presentation.screen.formatZipCode
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.PersonalDataViewModel
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.parseBrazilianCurrency
import java.math.BigDecimal
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PersonalDataSessionOwnershipTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test fun `expired session prevents queued form writes and limit recalculation`() = runTest(dispatcherRule.dispatcher) {
        var current = true
        var updates = 0
        var limits = 0
        val model = viewModel(
            update = { _, _ ->
                updates++
                AppResult.Success(Unit)
            },
            recalculate = {
                limits++
                AppResult.Success(BigDecimal.TEN)
            },
            isSessionCurrent = { current }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.submit()
        model.recalculateLimit()
        current = false
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, updates)
        assertEquals(0, limits)
        assertFalse(model.state.value.saved)
    }

    @Test fun `expired session discards a pending form write result`() = runTest(dispatcherRule.dispatcher) {
        var current = true
        val pending = CompletableDeferred<AppResult<Unit>>()
        val model = viewModel(update = { _, _ -> pending.await() }, isSessionCurrent = { current })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.submit()
        dispatcherRule.dispatcher.scheduler.runCurrent()
        current = false
        pending.complete(AppResult.Success(Unit))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(model.state.value.saved)
    }

    @Test fun `completion from old session is not delivered after replacement`() = runTest(dispatcherRule.dispatcher) {
        var current = true
        val model = viewModel(isSessionCurrent = { current })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.submit()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        current = false
        assertFalse(model.consumeCompletion())
    }

    private fun viewModel(
        update: suspend (PersonalDataUpdate, AccountProfile) -> AppResult<Unit> = { _, _ -> AppResult.Success(Unit) },
        recalculate: suspend () -> AppResult<BigDecimal> = { AppResult.Success(BigDecimal.TEN) },
        isSessionCurrent: () -> Boolean,
    ) = PersonalDataViewModel(
        GetAccountProfileUseCase {
            AppResult.Success(
                AccountProfile(
                    "12345678901", "Pessoa", "pessoa@example.test", "70000000", Gender.MALE,
                    MunicipalityId(FIXTURE_STATE_ID, 1, 0), "DF", "Brasília", BigDecimal("100"), BigDecimal.TEN, BigDecimal("200"), true
                )
            )
        },
        ResolveZipCodeUseCase { error("CEP não consultado neste cenário") },
        UpdatePersonalDataUseCase { value, original -> update(value, original) },
        FixedCoroutineDispatchers(dispatcherRule.dispatcher),
        RecalculateAuthorizedLimitUseCase { recalculate() },
        isSessionCurrent = isSessionCurrent
    )
}

private const val FIXTURE_STATE_ID = 7L
