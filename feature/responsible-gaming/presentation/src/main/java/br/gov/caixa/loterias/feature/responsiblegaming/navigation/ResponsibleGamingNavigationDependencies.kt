package br.gov.caixa.loterias.feature.responsiblegaming.navigation

import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SubmitSelfAssessmentUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SuspendAccountUseCase

/** Dependências deste grafo de destinos, fornecidas pela raiz de composição do aplicativo. */
interface ResponsibleGamingNavigationDependencies {
    val serverTimeSource: br.gov.caixa.loterias.core.common.time.ServerTimeSource
        get() = br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.now() }
    val currentUserIdentity: CurrentUserIdentityProvider
    val dispatchers: CoroutineDispatchers
    val selfAssessmentQuestions: GetSelfAssessmentQuestionsUseCase
    val socialTransfers: GetSocialTransfersUseCase
    val submitSelfAssessment: SubmitSelfAssessmentUseCase
    val suspendAccount: SuspendAccountUseCase
}
