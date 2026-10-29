package br.gov.caixa.loterias.feature.tickets.domain.model

data class TicketQuery(
    val code: String,
    val qrCode: Boolean = false,
    val retailerCode: String = "",
    val preparedScan: PreparedTicketScan? = null,
)

/** A orientação do leitor é uma entrada do domínio; a configuração Android permanece na apresentação. */
enum class TicketScanOrientation { PORTRAIT, LANDSCAPE }

/** Payload interpretado pelo leitor, retido para que o codificador de transporte não o interprete novamente. */
data class PreparedTicketScan(val value: String, val qrCode: Boolean)
