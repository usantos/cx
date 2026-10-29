package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.data.mapper.toServiceConnectionFailure

class ServiceConnectionFailureMapperTest {
    @Test fun `timeouts remain distinguishable from connection and TLS failures without exposing causes`() {
        for (failure in listOf(SocketTimeoutException("private"), IOException("wrapper", SocketTimeoutException("private")))) {
            assertEquals(ServiceConnectionFailure.TIMEOUT, failure.toServiceConnectionFailure())
        }
        for (failure in listOf(IOException("private"), UnknownHostException("private"), ConnectException("private"),
            SSLHandshakeException("private"), SSLPeerUnverifiedException("private"))) {
            assertEquals(ServiceConnectionFailure.CONNECTION, failure.toServiceConnectionFailure())
        }
    }

    @Test fun `cyclic or excessively deep causes cannot prevent an error decision`() {
        val first = IOException("first")
        val second = IOException("second", first)
        first.initCause(second)
        assertEquals(ServiceConnectionFailure.CONNECTION, first.toServiceConnectionFailure())
        var deep: IOException = SocketTimeoutException("deep")
        repeat(40) { deep = IOException("wrapper", deep) }
        assertEquals(ServiceConnectionFailure.CONNECTION, deep.toServiceConnectionFailure())
    }
}
