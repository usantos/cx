package br.gov.caixa.loterias.core.common

/** Device connectivity is a UI hint, never proof that an API or its TLS configuration is healthy. */
enum class ConnectionAvailability { CONNECTED, DISCONNECTED, UNKNOWN }

fun interface NetworkAvailability {
    fun current(): ConnectionAvailability
}

/** Reads on demand: no cached snapshot survives a user's new retry. */
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
