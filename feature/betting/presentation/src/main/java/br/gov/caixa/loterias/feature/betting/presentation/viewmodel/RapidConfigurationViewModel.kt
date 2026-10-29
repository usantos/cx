package br.gov.caixa.loterias.feature.betting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.RapidConfigurationAmounts
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.RapidConfigurationFavorites
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.RapidConfigurationLoading
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.RapidConfigurationNumbers
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.RapidConfigurationSubmission
import br.gov.caixa.loterias.feature.betting.presentation.mapper.rapidServiceFailurePresentation
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationAction
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceRapidConfigurationState
import java.math.BigDecimal

class RapidConfigurationViewModel(
    private val getConfiguration: GetRapidBetConfigurationUseCase,
    private val getModalities: GetLotteryModalitiesUseCase,
    private val saveConfiguration: SaveRapidBetConfigurationUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val getFavorites: GetFavoriteBetsUseCase? = null,
    private val getParameters: GetBettingParametersUseCase? = null,
    private val verifyAccess: VerifyAccountAccessUseCase? = null,
    autoLoad: Boolean = true,
    private val isSessionCurrent: () -> Boolean = { true },
    private val networkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability =
        br.gov.caixa.loterias.core.common.network.NetworkAvailability {
            br.gov.caixa.loterias.core.common.network.ConnectionAvailability.UNKNOWN
        },
    private val localClock: java.time.Clock = java.time.Clock.systemDefaultZone(),
    initialPage: Int = 0,
) : ViewModel() {
    private val mutableState = reduxStateStore<RapidConfigurationUiState, RapidConfigurationAction>(
        RapidConfigurationUiState(loading = autoLoad, page = initialPage.coerceIn(0, LAST_PAGE)),
        ::reduceRapidConfigurationState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state

    private val loading =
        RapidConfigurationLoading(
            mutableState,
            viewModelScope,
            dispatchers,
            getConfiguration,
            getModalities,
            isSessionCurrent,
            ::applyServiceFailure
        )
    private val amounts =
        RapidConfigurationAmounts(mutableState, viewModelScope, dispatchers, getParameters, verifyAccess, isSessionCurrent)
    private val favorites = RapidConfigurationFavorites(mutableState, viewModelScope, dispatchers, getFavorites, isSessionCurrent)
    private val numbers = RapidConfigurationNumbers(mutableState)
    private val submission =
        RapidConfigurationSubmission(
            mutableState,
            viewModelScope,
            dispatchers,
            saveConfiguration,
            loading::applyFailure,
            isSessionCurrent,
            ::applyServiceFailure
        )

    init {
        if (autoLoad) {
            load()
            loadFavorites()
            loadAmountLimits()
        }
    }

    fun load() = loading.load()

    fun loadAmountLimits() = amounts.loadAmountLimits()

    fun loadFavorites() = favorites.loadFavorites()

    fun toggleFavorite(id: Long) = favorites.toggleFavorite(id)

    fun setPage(page: Int) = mutableState.dispatch(RapidConfigurationAction.PageChanged(page))

    fun setMinimumPrize(value: BigDecimal) = amounts.setMinimumPrize(value)

    fun setMinimumAmount(value: BigDecimal) = amounts.setMinimumAmount(value)

    fun setMaximumAmount(value: BigDecimal) = amounts.setMaximumAmount(value)

    fun setCustomAmount(value: BigDecimal?, minimum: Boolean): Boolean = amounts.setCustomAmount(value, minimum)

    fun toggleModality(id: Int) = mutableState.dispatch(RapidConfigurationAction.ModalityToggled(id))

    fun toggleRequiredNumber(number: Int) = numbers.toggleRequiredNumber(number)

    fun toggleExcludedNumber(number: Int) = numbers.toggleExcludedNumber(number)

    fun clearRequiredNumbers() = numbers.clearRequiredNumbers()

    fun clearExcludedNumbers() = numbers.clearExcludedNumbers()

    fun dismissMessage() = mutableState.dispatch(RapidConfigurationAction.MessageDismissed)

    fun save() = submission.save()

    private fun applyServiceFailure(failure: br.gov.caixa.loterias.core.domain.service.model.ServiceFailure) = mutableState.dispatch(
        RapidConfigurationAction.ServiceFailureReceived(
            rapidServiceFailurePresentation(failure, networkAvailability, java.time.LocalDateTime.now(localClock))
        )
    )

    fun dismissServiceNotice(expected: br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState) =
        mutableState.dispatch(RapidConfigurationAction.ServiceNoticeDismissed(expected))
    fun acceptServiceNotice(expected: br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState) = mutableState.dispatch(
        RapidConfigurationAction.ServiceNoticeAccepted(
            expected,
            mutableState.value.serviceFailure.accept(expected, networkAvailability)
        )
    )
    fun consumeServiceRedirect(expected: br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent): Boolean {
        synchronized(mutableState) {
            val previous = mutableState.value.serviceFailure.redirect
            if (previous !== expected) return false
            mutableState.dispatch(RapidConfigurationAction.ServiceRedirectConsumed)
            return true
        }
    }

    private companion object {
        const val LAST_PAGE = 6
    }
}
