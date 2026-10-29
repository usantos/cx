package br.gov.caixa.loterias.feature.responsiblegaming.navigation

import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SubmitSelfAssessmentUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SuspendAccountUseCase
import javax.inject.Inject

class ResponsibleGamingNavigationDependenciesAdapter @Inject constructor(
    override val currentUserIdentity: CurrentUserIdentityProvider,
    override val dispatchers: CoroutineDispatchers,
    override val selfAssessmentQuestions: GetSelfAssessmentQuestionsUseCase,
    override val socialTransfers: GetSocialTransfersUseCase,
    override val submitSelfAssessment: SubmitSelfAssessmentUseCase,
    override val suspendAccount: SuspendAccountUseCase,
    override val serverTimeSource: br.gov.caixa.loterias.core.common.time.ServerTimeSource,
) : ResponsibleGamingNavigationDependencies {

}
