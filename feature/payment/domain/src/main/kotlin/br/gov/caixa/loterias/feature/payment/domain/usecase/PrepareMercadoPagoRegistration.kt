package br.gov.caixa.loterias.feature.payment.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistration
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistrationResult
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistrationToken
import br.gov.caixa.loterias.feature.payment.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.domain.repository.MercadoPagoRegistrationRepository

class PrepareMercadoPagoRegistration(private val repository: MercadoPagoRegistrationRepository) : PrepareMercadoPagoRegistrationUseCase {
    override suspend fun invoke(email: String) = appResultOf {
        val normalized = email.trim()
        require(
            normalized.length <= 254 && normalized.none {
                it.isISOControl()
            } && normalized.matches(Regex("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
        ) {
            messageText("informe_um_e_mail_valido_para_o_82d261")
        }
        when (val token = repository.mercadoPagoRegistrationToken(normalized)) {
            is MercadoPagoRegistrationToken.Available -> MercadoPagoRegistrationResult.Ready(MercadoPagoRegistration.fromToken(token.value))
            is MercadoPagoRegistrationToken.Rejected -> MercadoPagoRegistrationResult.Rejected(token.failure)
        }
    }
}
