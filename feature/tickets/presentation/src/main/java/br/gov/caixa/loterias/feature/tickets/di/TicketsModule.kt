package br.gov.caixa.loterias.feature.tickets.di

import br.gov.caixa.loterias.feature.tickets.data.repository.TicketsRepositoryImpl
import br.gov.caixa.loterias.feature.tickets.data.datasource.remote.TicketsRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.tickets.data.datasource.remote.TicketsRemoteDataSource
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicket
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBets
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBets
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetails
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseMonths
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasePaymentMethods
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasePaymentMethodsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseStatuses
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseStatusesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchases
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.RecoverConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.usecase.RecoverConfirmedBetUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.SavePurchaseAsFavorite
import br.gov.caixa.loterias.feature.tickets.domain.usecase.SavePurchaseAsFavoriteUseCase
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository
import dagger.Provides
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TicketsModule {
    @Provides
    fun ticketsDataSource(source: TicketsRemoteDataSourceImpl): TicketsRemoteDataSource = source

    @Provides @Singleton
    fun ticketsRepository(source: TicketsRemoteDataSource): TicketsRepository = TicketsRepositoryImpl(source)

    @Provides
    fun loadConfirmedBet(repository: TicketsRepository): br.gov.caixa.loterias.feature.tickets.domain.usecase.LoadConfirmedBetUseCase =
        br.gov.caixa.loterias.feature.tickets.domain.usecase.LoadConfirmedBet(repository)

    @Provides
    fun recoverConfirmedBet(repository: TicketsRepository): RecoverConfirmedBetUseCase = RecoverConfirmedBet(repository)

    @Provides
    fun confirmedBets(repository: TicketsRepository): GetConfirmedBetsUseCase = GetConfirmedBets(repository)

    @Provides
    fun purchases(repository: TicketsRepository): GetPurchasesUseCase = GetPurchases(repository)

    @Provides
    fun purchaseBets(repository: TicketsRepository): GetPurchaseBetsUseCase = GetPurchaseBets(repository)

    @Provides
    fun purchaseDetails(repository: TicketsRepository): GetPurchaseDetailsUseCase = GetPurchaseDetails(repository)

    @Provides
    fun savePurchaseAsFavorite(repository: TicketsRepository): SavePurchaseAsFavoriteUseCase =
        SavePurchaseAsFavorite(repository)

    @Provides
    fun checkTicket(repository: TicketsRepository): CheckTicketUseCase = CheckTicket(repository)

    @Provides
    fun confirmedBetsConfiguration(repository: TicketsRepository): GetConfirmedBetsConfigurationUseCase =
        GetConfirmedBetsConfiguration(repository)

    @Provides
    fun purchaseMonths(repository: TicketsRepository): GetPurchaseMonthsUseCase = GetPurchaseMonths(repository)

    @Provides
    fun purchasePaymentMethods(repository: TicketsRepository): GetPurchasePaymentMethodsUseCase =
        GetPurchasePaymentMethods(repository)

    @Provides
    fun purchaseStatuses(repository: TicketsRepository): GetPurchaseStatusesUseCase = GetPurchaseStatuses(repository)
}
