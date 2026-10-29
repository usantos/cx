package br.gov.caixa.loterias.feature.responsiblegaming.di

import br.gov.caixa.loterias.feature.responsiblegaming.data.repository.ResponsibleGamingRepositoryImpl
import br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote.ResponsibleGamingRemoteDataSource
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSelfAssessmentQuestions
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.repository.ResponsibleGamingRepository
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SubmitSelfAssessment
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SubmitSelfAssessmentUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SuspendAccount
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SuspendAccountUseCase
import dagger.Provides
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ResponsibleGamingModule {
    @Provides
    fun responsibleGamingDataSource(source: br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote.ResponsibleGamingRemoteDataSourceImpl): ResponsibleGamingRemoteDataSource = source

    @Provides @Singleton
    fun responsibleGamingRepository(source: ResponsibleGamingRemoteDataSource): ResponsibleGamingRepository =
        ResponsibleGamingRepositoryImpl(source)

    @Provides
    fun selfAssessmentQuestions(repository: ResponsibleGamingRepository): GetSelfAssessmentQuestionsUseCase =
        GetSelfAssessmentQuestions(repository)

    @Provides
    fun submitSelfAssessment(repository: ResponsibleGamingRepository): SubmitSelfAssessmentUseCase =
        SubmitSelfAssessment(repository)

    @Provides
    fun socialTransfers(repository: ResponsibleGamingRepository): GetSocialTransfersUseCase =
        GetSocialTransfers(repository)

    @Provides
    fun suspendAccount(repository: ResponsibleGamingRepository): SuspendAccountUseCase =
        SuspendAccount(repository)
}
