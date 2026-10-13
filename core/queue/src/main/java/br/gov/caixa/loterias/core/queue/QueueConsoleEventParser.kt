package br.gov.caixa.loterias.core.queue

import com.google.gson.JsonParser

sealed interface QueueConsoleEvent {
    data object Ignore : QueueConsoleEvent
    data class Join(val request: Int) : QueueConsoleEvent
    data class Pass(val target: String, val passType: String, val delayMillis: Long, val passedLifetime: Int) : QueueConsoleEvent
    data class Error(val message: String) : QueueConsoleEvent
}

object QueueConsoleEventParser {
    fun parse(message: String): QueueConsoleEvent {
        if ('{' !in message || '}' !in message) return QueueConsoleEvent.Ignore
        if ("JOIN" !in message && "REDIRECT" !in message) return QueueConsoleEvent.Ignore
        val jsonText = message.substring(message.indexOf('{'), message.lastIndexOf('}') + 1)
        return runCatching {
            val json = JsonParser.parseString(jsonText).asJsonObject
            when {
                "JOIN" in message -> QueueConsoleEvent.Join(json.get("request").asInt)
                "qfpt" !in message -> QueueConsoleEvent.Ignore
                else -> QueueConsoleEvent.Pass(
                    target = json.get("target").asString,
                    passType = json.get("type").asString,
                    delayMillis = json.get("when").asLong.coerceAtLeast(0),
                    passedLifetime = json.get("pl")?.asInt ?: -1,
                )
            }
        }.getOrElse { QueueConsoleEvent.Error("Não foi possível processar a resposta da fila.") }
    }
}
