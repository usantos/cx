package br.gov.caixa.loterias.feature.account

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
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
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.PersonalDataViewModel
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
abstract class PersonalDataViewModelFixture {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    protected fun viewModel(
        ioDispatcher: kotlinx.coroutines.CoroutineDispatcher = dispatcherRule.dispatcher,
        profileResult: suspend () -> AppResult<AccountProfile> = { AppResult.Success(profile()) },
        resolve: suspend (String) -> AppResult<ZipCodeLocation> = { AppResult.Success(location()) },
        update: suspend (PersonalDataUpdate, AccountProfile) -> AppResult<Unit> = { _, _ -> AppResult.Success(Unit) },
        recalculate: suspend () -> AppResult<BigDecimal> = { AppResult.Success(BigDecimal("200")) },
        deferInitialLoad: Boolean = false,
        isSessionCurrent: () -> Boolean = { true },
        canStartWrite: () -> Boolean = { true },
    ) = PersonalDataViewModel(
        GetAccountProfileUseCase { profileResult() },
        ResolveZipCodeUseCase { resolve(it) },
        UpdatePersonalDataUseCase { value, original -> update(value, original) },
        FixedCoroutineDispatchers(ioDispatcher),
        RecalculateAuthorizedLimitUseCase { recalculate() },
        deferInitialLoad,
        isSessionCurrent,
        canStartWrite
    )

    protected fun profile() = AccountProfile(
        "12345678901", "Pessoa", "pessoa@caixa.gov.br", "70000000", Gender.MALE,
        MunicipalityId(SYNTHETIC_STATE_ID, 1, 0), "DF", "Brasília",
        BigDecimal("100"), BigDecimal("10"), BigDecimal("200"), true
    )
    protected fun location() = ZipCodeLocation(MunicipalityId(SYNTHETIC_STATE_ID, 1, 0), "DF", "Brasília")
}

private const val SYNTHETIC_STATE_ID = 7L
