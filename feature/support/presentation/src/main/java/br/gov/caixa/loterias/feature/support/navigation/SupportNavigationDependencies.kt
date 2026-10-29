package br.gov.caixa.loterias.feature.support.navigation

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.AcceptTermsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportQuestionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportSectionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetTermsUseCase

/** Dependências deste grafo de destinos, fornecidas pela raiz de composição do aplicativo. */
interface SupportNavigationDependencies {
    val acceptTerms: AcceptTermsUseCase
    val bettingParameters: GetBettingParametersUseCase
    val dispatchers: CoroutineDispatchers
    val supportQuestions: GetSupportQuestionsUseCase
    val supportSections: GetSupportSectionsUseCase
    val terms: GetTermsUseCase
}
