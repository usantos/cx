package br.gov.caixa.loterias.feature.payment.domain.model

data class PaymentMethod(
    val id: Long,
    val purchaseName: String,
    val prizeName: String = "",
    val default: Boolean = false,
    val publicKey: String = "",
) {
    override fun toString(): String = "PaymentMethod(id=$id, default=$default)"

    val isPix: Boolean get() = id == PIX_ID
    val isSupportedCard: Boolean get() = id == MERCADO_PAGO_ID || id == RECARGA_PAY_ID
    companion object {
        const val MERCADO_PAGO_ID = 1L
        const val RECARGA_PAY_ID = 5L
        const val PIX_ID = 6L
    }
}
