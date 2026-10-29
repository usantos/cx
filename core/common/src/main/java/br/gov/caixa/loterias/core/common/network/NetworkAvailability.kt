package br.gov.caixa.loterias.core.common.network

/** A conectividade do dispositivo é uma indicação para a interface, nunca prova da disponibilidade de uma API ou de sua configuração TLS. */
enum class ConnectionAvailability { CONNECTED, DISCONNECTED, UNKNOWN }

fun interface NetworkAvailability {
    fun current(): ConnectionAvailability
}

/** Lê sob demanda: nenhum retrato em cache sobrevive a uma nova tentativa do usuário. */
class ReadNetworkAvailability(private val readConnected: () -> Boolean?) : NetworkAvailability {
    override fun current(): ConnectionAvailability = try {
        when (readConnected()) {
            true -> ConnectionAvailability.CONNECTED
            false -> ConnectionAvailability.DISCONNECTED
            null -> ConnectionAvailability.UNKNOWN
        }
    } catch (_: SecurityException) {
        ConnectionAvailability.UNKNOWN
    }
}
