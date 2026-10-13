package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.feature.payment.MercadoPagoRegistrationViewModel
import br.gov.caixa.loterias.feature.payment.MercadoPagoRegistrationOverlay

@Composable
internal fun paymentRegistrationViewModel(container: AppContainer): MercadoPagoRegistrationViewModel =
    viewModel(key = "mercado-pago-registration") {
        MercadoPagoRegistrationViewModel(container.accountProfile, container.prepareMercadoPagoRegistration, container.dispatchers, container.localClock, container.networkAvailability)
    }

@Composable
internal fun PaymentRegistrationHost(
    viewModel: MercadoPagoRegistrationViewModel,
    onFinished: () -> Unit,
    onRedirect: (br.gov.caixa.loterias.core.domain.service.ServiceRedirect) -> Unit,
) {
    MercadoPagoRegistrationOverlay(viewModel, simulateBrowser = BuildConfig.ENABLE_MOCK_BACKEND, onRedirect = onRedirect, onFinished = onFinished)
}
