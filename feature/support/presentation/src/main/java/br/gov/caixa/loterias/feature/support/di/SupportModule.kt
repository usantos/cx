package br.gov.caixa.loterias.feature.support.di

import br.gov.caixa.loterias.feature.support.data.repository.SupportRepositoryImpl
import br.gov.caixa.loterias.feature.support.data.datasource.remote.SupportRemoteDataSource
import br.gov.caixa.loterias.feature.support.domain.usecase.AcceptTerms
import br.gov.caixa.loterias.feature.support.domain.usecase.AcceptTermsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportQuestions
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportQuestionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportSections
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportSectionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetTerms
import br.gov.caixa.loterias.feature.support.domain.usecase.GetTermsUseCase
import br.gov.caixa.loterias.feature.support.domain.repository.SupportRepository
import dagger.Provides
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SupportModule {
    @Provides
    fun supportDataSource(source: br.gov.caixa.loterias.feature.support.data.datasource.remote.SupportRemoteDataSourceImpl): SupportRemoteDataSource = source

    @Provides @Singleton
    fun supportRepository(source: SupportRemoteDataSource): SupportRepository = SupportRepositoryImpl(source)

    @Provides
    fun supportSections(repository: SupportRepository): GetSupportSectionsUseCase = GetSupportSections(repository)

    @Provides
    fun supportQuestions(repository: SupportRepository): GetSupportQuestionsUseCase = GetSupportQuestions(repository)

    @Provides
    fun terms(repository: SupportRepository): GetTermsUseCase = GetTerms(repository)

    @Provides
    fun acceptTerms(repository: SupportRepository): AcceptTermsUseCase = AcceptTerms(repository)
}
