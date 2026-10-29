package br.gov.caixa.loterias.feature.results.navigation

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResultUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetResultByContestUseCase
import javax.inject.Inject

class ResultsNavigationDependenciesAdapter @Inject constructor(
    override val confirmedBetResults: br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase,
    override val dispatchers: CoroutineDispatchers,
    override val latestResult: GetLatestResultUseCase,
    override val resultByContest: GetResultByContestUseCase,
    private val visualBetFixtures: br.gov.caixa.loterias.core.navigation.fixture.VisualBetNavigationFixtures,
    private val hostConfiguration: br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration,
) : ResultsNavigationDependencies {
    override val mockBackendEnabled: Boolean get() = hostConfiguration.mockBackendEnabled

    override fun visualConfirmedBet(
        variant: String,
        originalRecurringBet: Boolean,
        redeemedPrize: Boolean,
    ): br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet =
        visualBetFixtures.visualConfirmedBet(variant, originalRecurringBet, redeemedPrize)
}
