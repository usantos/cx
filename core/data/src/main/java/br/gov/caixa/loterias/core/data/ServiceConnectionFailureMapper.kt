package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.service.ServiceConnectionFailure
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.Collections
import java.util.IdentityHashMap

/** No exception text, request URL or credentials cross into presentation. */
internal fun IOException.toServiceConnectionFailure(): ServiceConnectionFailure {
    var cause: Throwable? = this
    val visited = Collections.newSetFromMap(IdentityHashMap<Throwable, Boolean>())
    while (cause != null && visited.size < 32 && visited.add(cause)) {
        if (cause is SocketTimeoutException) return ServiceConnectionFailure.TIMEOUT
        cause = cause.cause
    }
    return ServiceConnectionFailure.CONNECTION
}
