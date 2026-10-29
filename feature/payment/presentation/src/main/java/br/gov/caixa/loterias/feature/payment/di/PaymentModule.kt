package br.gov.caixa.loterias.feature.payment.di

import br.gov.caixa.loterias.feature.payment.data.datasource.remote.PaymentRemoteDataSource
import br.gov.caixa.loterias.feature.payment.data.repository.PaymentRepositoryImpl
import br.gov.caixa.loterias.feature.payment.data.datasource.remote.PaymentRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.payment.domain.usecase.DeleteSavedCard
import br.gov.caixa.loterias.feature.payment.domain.usecase.DeleteSavedCardUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GeneratePixCharge
import br.gov.caixa.loterias.feature.payment.domain.usecase.GeneratePixChargeUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethods
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgress
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCards
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.repository.PaymentRepository
import br.gov.caixa.loterias.feature.payment.domain.usecase.SubmitCardPayment
import br.gov.caixa.loterias.feature.payment.domain.usecase.SubmitCardPaymentUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDam
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.provider.PaymentApiProvider
import dagger.Provides
import javax.inject.Singleton
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {
    @Provides @Singleton
    fun paymentDataSource(
        provider: BackendApiProvider,
        loteriasApiProvider: LoteriasApiProvider,
        paymentApiProvider: PaymentApiProvider,
        purchaseApiProvider: br.gov.caixa.loterias.core.network.api.PurchaseBackendApiProvider,
    ): PaymentRemoteDataSource = PaymentRemoteDataSourceImpl(provider, loteriasApiProvider, paymentApiProvider, purchaseApiProvider)

    @Provides @Singleton
    fun paymentRepository(source: PaymentRemoteDataSource): PaymentRepository = PaymentRepositoryImpl(source)


    @Provides fun prepareMercadoPagoRegistration(repository: br.gov.caixa.loterias.feature.payment.domain.repository.MercadoPagoRegistrationRepository): br.gov.caixa.loterias.feature.payment.domain.usecase.PrepareMercadoPagoRegistrationUseCase =
        br.gov.caixa.loterias.feature.payment.domain.usecase.PrepareMercadoPagoRegistration(repository)

    @Provides fun paymentMethods(repository: PaymentRepository): GetPaymentMethodsUseCase = GetPaymentMethods(repository)

    @Provides fun savedCards(repository: PaymentRepository): GetSavedCardsUseCase = GetSavedCards(repository)

    @Provides fun deleteSavedCard(repository: PaymentRepository): DeleteSavedCardUseCase = DeleteSavedCard(repository)

    @Provides fun generatePixCharge(repository: PaymentRepository): GeneratePixChargeUseCase = GeneratePixCharge(repository)

    @Provides fun purchaseProgress(repository: PaymentRepository): GetPurchaseProgressUseCase = GetPurchaseProgress(repository)

    @Provides fun validateDam(repository: PaymentRepository): ValidateDamUseCase = ValidateDam(repository)

    @Provides fun submitCardPayment(repository: PaymentRepository): SubmitCardPaymentUseCase = SubmitCardPayment(repository)
}
