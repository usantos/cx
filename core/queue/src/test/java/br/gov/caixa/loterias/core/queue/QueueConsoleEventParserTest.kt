package br.gov.caixa.loterias.core.queue

import org.junit.Assert.assertEquals
import org.junit.Test
import br.gov.caixa.loterias.core.queue.parser.QueueConsoleEvent
import br.gov.caixa.loterias.core.queue.parser.QueueConsoleEventParser

class QueueConsoleEventParserTest {
    @Test fun `does not turn an unsafe pass target into a successful queue event`() {
        for (target in listOf("javascript:alert(1)", "http://caixa.filabra.com/?qfqid=x", "https://evil.test/?qfqid=x")) {
            val event = QueueConsoleEventParser.parse(
                "REDIRECT qfpt {\"target\":\"$target\",\"type\":\"Queued\",\"when\":0}",
            )
            org.junit.Assert.assertFalse("Unsafe target must not pass", event is QueueConsoleEvent.Pass)
        }
    }

    @Test fun `ignores unrelated messages`() {
        assertEquals(QueueConsoleEvent.Ignore, QueueConsoleEventParser.parse("ready"))
        assertEquals(QueueConsoleEvent.Ignore, QueueConsoleEventParser.parse("{}"))
        assertEquals(QueueConsoleEvent.Ignore, QueueConsoleEventParser.parse("REDIRECT {}"))
    }

    @Test fun `parses join`() {
        assertEquals(QueueConsoleEvent.Join(42), QueueConsoleEventParser.parse("JOIN {\"request\":42}"))
    }

    @Test fun `parses redirect pass and defaults lifetime`() {
        assertEquals(
            QueueConsoleEvent.Pass("https://caixa.filabra.com", "Queued", 0, 9),
            QueueConsoleEventParser.parse("REDIRECT qfpt {\"target\":\"https://caixa.filabra.com\",\"type\":\"Queued\",\"when\":-2,\"pl\":9}"),
        )
        assertEquals(
            QueueConsoleEvent.Pass("https://caixa.filabra.com", "Repass", 10, -1),
            QueueConsoleEventParser.parse("REDIRECT qfpt {\"target\":\"https://caixa.filabra.com\",\"type\":\"Repass\",\"when\":10}"),
        )
    }

    @Test fun `maps malformed queue messages to safe error`() {
        assertEquals(
            QueueConsoleEvent.Error("Não foi possível processar a resposta da fila."),
            QueueConsoleEventParser.parse("JOIN {invalid}"),
        )
    }
}
