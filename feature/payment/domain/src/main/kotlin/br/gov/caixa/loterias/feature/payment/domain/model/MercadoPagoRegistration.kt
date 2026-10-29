package br.gov.caixa.loterias.feature.payment.domain.model

import br.gov.caixa.loterias.feature.payment.domain.model.localizedMessage as messageText
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/** Requisição transitória do navegador. Sua representação textual não deve revelar o token. */
class MercadoPagoRegistration private constructor(val url: String) {
    override fun toString(): String = "MercadoPagoRegistration(redacted)"

    companion object {
        fun fromToken(token: String): MercadoPagoRegistration {
            require(token.isNotBlank() && token.length <= 4096 && token.none { it.isISOControl() }) {
                messageText("token_de_cadastro_mercado_pago_i_0cf401")
            }
            val encoded = URLEncoder.encode(token, StandardCharsets.UTF_8.name())
            return MercadoPagoRegistration(
                "https://registration-br.mercadopago.com/registration-mp?mode=mp&external_registration_token=$encoded"
            )
        }
    }
}
