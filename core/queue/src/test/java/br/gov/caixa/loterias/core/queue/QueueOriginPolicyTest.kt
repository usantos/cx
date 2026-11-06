package br.gov.caixa.loterias.core.queue

import br.gov.caixa.loterias.core.queue.security.QueueOriginPolicy
import org.junit.Assert.*
import org.junit.Test

class QueueOriginPolicyTest {
    @Test fun `accepts vendor HTTPS pages and binds later navigation to the initial origin`() {
        val initial = "https://caixa.filabra.com/queue?qfqid=fixture"
        assertTrue(QueueOriginPolicy.isQueuePage(initial))
        assertTrue(QueueOriginPolicy.isSameQueueOrigin(initial, "https://CAIXA.filabra.com:443/next"))
        assertFalse(QueueOriginPolicy.isSameQueueOrigin(initial, "https://other.filabra.com/next"))
        assertTrue(QueueOriginPolicy.isPassTarget("https://mobileloterias.caixa.gov.br/?qfqid=fixture"))
    }

    @Test fun `rejects ambiguous authorities schemes host suffix tricks and nonstandard ports`() {
        for (url in listOf("http://caixa.filabra.com", "https://evil.test", "https://filabra.com.evil.test",
            "https://evilfilabra.com", "https://user@caixa.filabra.com", "https://caixa.filabra.com:444/",
            "https://caixa.filabra.com./", "https://caixa.filabra.com\\@evil.test/", "https://caixa%2efilabra.com/",
            "javascript:alert(1)", "data:text/html,test", "https:///caixa.filabra.com", "//caixa.filabra.com")) {
            assertFalse(url, QueueOriginPolicy.isQueuePage(url))
            assertFalse(url, QueueOriginPolicy.isSameQueueOrigin("https://caixa.filabra.com/", url))
        }
        assertFalse(QueueOriginPolicy.isPassTarget("https://caixa.gov.br.evil.test/?qfqid=fixture"))
    }
}
