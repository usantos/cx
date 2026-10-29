package br.gov.caixa.loterias.feature.results.di

import br.gov.caixa.loterias.feature.results.data.repository.ResultsRepositoryImpl
import br.gov.caixa.loterias.feature.results.data.datasource.remote.ResultsRemoteDataSource
import br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResult
import br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResultUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetResultByContest
import br.gov.caixa.loterias.feature.results.domain.usecase.GetResultByContestUseCase
import br.gov.caixa.loterias.feature.results.domain.repository.ResultsRepository
import dagger.Provides
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ResultsModule {
    @Provides
    fun resultsDataSource(source: br.gov.caixa.loterias.feature.results.data.datasource.remote.ResultsRemoteDataSourceImpl): ResultsRemoteDataSource = source

    @Provides @Singleton
    fun resultsRepository(source: ResultsRemoteDataSource): ResultsRepository = ResultsRepositoryImpl(source)

    @Provides
    fun latestResult(repository: ResultsRepository): GetLatestResultUseCase = GetLatestResult(repository)

    @Provides
    fun confirmedBetResults(repository: ResultsRepository): br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase =
        br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResults(repository)

    @Provides
    fun resultByContest(repository: ResultsRepository): GetResultByContestUseCase = GetResultByContest(repository)
}
