package br.gov.caixa.loterias.core.domain.payment

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import br.gov.caixa.loterias.core.domain.service.ServiceFailure
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun interface MercadoPagoRegistrationRepository {
    suspend fun mercadoPagoRegistrationToken(email: String): MercadoPagoRegistrationToken
}

sealed interface MercadoPagoRegistrationToken {
    class Available(val value: String) : MercadoPagoRegistrationToken {
        override fun toString() = "MercadoPagoRegistrationToken(redacted)"
    }
    data class Rejected(val failure: ServiceFailure) : MercadoPagoRegistrationToken
}

sealed interface MercadoPagoRegistrationResult {
    data class Ready(val request: MercadoPagoRegistration) : MercadoPagoRegistrationResult
    data class Rejected(val failure: ServiceFailure) : MercadoPagoRegistrationResult
}

/** Transient browser request. Its textual representation must not disclose the token. */
class MercadoPagoRegistration private constructor(val url: String) {
    override fun toString(): String = "MercadoPagoRegistration(redacted)"

    companion object {
        fun fromToken(token: String): MercadoPagoRegistration {
            require(token.isNotBlank() && token.length <= 4096 && token.none { it.isISOControl() }) {
                "Token de cadastro Mercado Pago inválido."
            }
            val encoded = URLEncoder.encode(token, StandardCharsets.UTF_8.name())
            return MercadoPagoRegistration("https://registration-br.mercadopago.com/registration-mp?mode=mp&external_registration_token=$encoded")
        }
    }
}

fun interface PrepareMercadoPagoRegistrationUseCase {
    suspend operator fun invoke(email: String): AppResult<MercadoPagoRegistrationResult>
}

class PrepareMercadoPagoRegistration(private val repository: MercadoPagoRegistrationRepository) : PrepareMercadoPagoRegistrationUseCase {
    override suspend fun invoke(email: String) = appResultOf {
        val normalized = email.trim()
        require(normalized.length <= 254 && normalized.none { it.isISOControl() } && normalized.matches(Regex("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))) {
            "Informe um e-mail válido para o cadastro Mercado Pago."
        }
        when (val token = repository.mercadoPagoRegistrationToken(normalized)) {
            is MercadoPagoRegistrationToken.Available -> MercadoPagoRegistrationResult.Ready(MercadoPagoRegistration.fromToken(token.value))
            is MercadoPagoRegistrationToken.Rejected -> MercadoPagoRegistrationResult.Rejected(token.failure)
        }
    }
}
