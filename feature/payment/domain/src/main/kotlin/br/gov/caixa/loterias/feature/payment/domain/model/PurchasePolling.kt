package br.gov.caixa.loterias.feature.payment.domain.model

data class PurchasePolling(val intervalMillis: Long, val attemptLimit: Int)
