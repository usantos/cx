package br.gov.caixa.loterias.feature.support.navigation

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.AcceptTermsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportQuestionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportSectionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetTermsUseCase
import javax.inject.Inject

class SupportNavigationDependenciesAdapter @Inject constructor(
    override val acceptTerms: AcceptTermsUseCase,
    override val bettingParameters: GetBettingParametersUseCase,
    override val dispatchers: CoroutineDispatchers,
    override val supportQuestions: GetSupportQuestionsUseCase,
    override val supportSections: GetSupportSectionsUseCase,
    override val terms: GetTermsUseCase,
) : SupportNavigationDependencies
