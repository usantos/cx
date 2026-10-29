package br.gov.caixa.loterias.feature.results.navigation

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResultUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetResultByContestUseCase

/** Dependências deste grafo de destinos, fornecidas pela raiz de composição do aplicativo. */
interface ResultsNavigationDependencies : br.gov.caixa.loterias.core.navigation.fixture.VisualBetNavigationFixtures {
    val confirmedBetResults: br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase
    val dispatchers: CoroutineDispatchers
    val latestResult: GetLatestResultUseCase
    val mockBackendEnabled: Boolean
    val resultByContest: GetResultByContestUseCase

}
