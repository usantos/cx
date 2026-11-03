package br.gov.caixa.loterias.feature.responsiblegaming.di

import br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote.ResponsibleGamingRemoteDataSource
import br.gov.caixa.loterias.feature.responsiblegaming.data.repository.ResponsibleGamingRepositoryImpl
import br.gov.caixa.loterias.feature.responsiblegaming.domain.repository.ResponsibleGamingRepository
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSelfAssessmentQuestions
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SubmitSelfAssessment
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SubmitSelfAssessmentUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ResponsibleGamingModule {
    @Provides
    fun responsibleGamingDataSource(
        source: br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote.ResponsibleGamingRemoteDataSourceImpl,
    ): ResponsibleGamingRemoteDataSource = source

    @Provides @Singleton
    fun responsibleGamingRepository(source: ResponsibleGamingRemoteDataSource): ResponsibleGamingRepository =
        ResponsibleGamingRepositoryImpl(source)

    @Provides
    fun selfAssessmentQuestions(repository: ResponsibleGamingRepository): GetSelfAssessmentQuestionsUseCase =
        GetSelfAssessmentQuestions(repository)

    @Provides
    fun submitSelfAssessment(repository: ResponsibleGamingRepository): SubmitSelfAssessmentUseCase = SubmitSelfAssessment(repository)

    @Provides
    fun socialTransfers(repository: ResponsibleGamingRepository): GetSocialTransfersUseCase = GetSocialTransfers(repository)

    @Provides
    fun suspensionRepositories(
        provider: br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider,
        tokens: br.gov.caixa.loterias.core.auth.session.SessionTokenStore,
    ): br.gov.caixa.loterias.feature.responsiblegaming.domain.repository.SuspensionRepositoryFactory =
        br.gov.caixa.loterias.feature.responsiblegaming.data.repository.SessionSuspensionRepositoryFactory(provider) {
            tokens.captureIdentity()?.let { identity ->
                br.gov.caixa.loterias.core.network.api.AccountVerificationSession(
                    { tokens.tokenFor(identity) },
                    { tokens.isCurrent(identity) }
                )
            }
        }

    @Provides
    fun suspensionOperations(
        repositories: br.gov.caixa.loterias.feature.responsiblegaming.domain.repository.SuspensionRepositoryFactory,
    ): br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SuspensionUseCasesFactory =
        br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SuspensionUseCasesFactory(repositories)
}
