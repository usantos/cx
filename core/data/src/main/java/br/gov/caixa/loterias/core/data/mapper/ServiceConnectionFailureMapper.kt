package br.gov.caixa.loterias.core.data.mapper

import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.Collections
import java.util.IdentityHashMap

/** Nenhum texto de exceção, URL de requisição ou credencial chega à apresentação. */
fun IOException.toServiceConnectionFailure(): ServiceConnectionFailure {
    var cause: Throwable? = this
    val visited = Collections.newSetFromMap(IdentityHashMap<Throwable, Boolean>())
    while (cause != null && visited.size < 32 && visited.add(cause)) {
        if (cause is SocketTimeoutException) return ServiceConnectionFailure.TIMEOUT
        cause = cause.cause
    }
    return ServiceConnectionFailure.CONNECTION
}
