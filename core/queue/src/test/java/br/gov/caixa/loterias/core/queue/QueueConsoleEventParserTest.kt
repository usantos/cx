package br.gov.caixa.loterias.core.queue

import org.junit.Assert.assertEquals
import org.junit.Test

class QueueConsoleEventParserTest {
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
            QueueConsoleEvent.Pass("https://target", "Queued", 0, 9),
            QueueConsoleEventParser.parse("REDIRECT qfpt {\"target\":\"https://target\",\"type\":\"Queued\",\"when\":-2,\"pl\":9}"),
        )
        assertEquals(
            QueueConsoleEvent.Pass("https://target", "Repass", 10, -1),
            QueueConsoleEventParser.parse("REDIRECT qfpt {\"target\":\"https://target\",\"type\":\"Repass\",\"when\":10}"),
        )
    }

    @Test fun `maps malformed queue messages to safe error`() {
        assertEquals(
            QueueConsoleEvent.Error("Não foi possível processar a resposta da fila."),
            QueueConsoleEventParser.parse("JOIN {invalid}"),
        )
    }
}
