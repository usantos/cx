package br.gov.caixa.loterias.feature.retail.domain.model

enum class PrizeRedemptionChannel(val legacyId: Long) {
    MERCADO_PAGO(1),
    PIX(6),
    LOTTERY(9660),
    AGENCY(9770),
}
