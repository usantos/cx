package br.gov.caixa.loterias.feature.payment.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.MercadoPagoRegistrationViewModel
import br.gov.caixa.loterias.feature.payment.presentation.components.MercadoPagoRegistrationOverlay

@Composable
fun paymentRegistrationViewModel(container: MercadoPagoRegistrationDependencies): MercadoPagoRegistrationViewModel =
    viewModel(key = "mercado-pago-registration") {
        MercadoPagoRegistrationViewModel(container.accountProfile, container.prepareMercadoPagoRegistration, container.dispatchers, container.localClock, container.networkAvailability)
    }

@Composable
fun PaymentRegistrationHost(
    viewModel: MercadoPagoRegistrationViewModel,
    onFinished: () -> Unit,
    onRedirect: (br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit,
    simulateBrowser: Boolean,
) {
    MercadoPagoRegistrationOverlay(viewModel, simulateBrowser = simulateBrowser, onRedirect = onRedirect, onFinished = onFinished)
}
