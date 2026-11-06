package br.gov.caixa.loterias.core.common.time

import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

fun interface ServerTimeSource { fun now(): Instant }

object LotteryDateTimes {
    private val zone = ZoneId.of("America/Sao_Paulo")
    private val legacy = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss").withResolverStyle(ResolverStyle.STRICT)

    /** Datas e horários do servidor sem deslocamento explícito usam o fuso de São Paulo. */
    fun parse(value: String): Instant? =
        runCatching { OffsetDateTime.parse(value).toInstant() }.getOrNull()
            ?: runCatching { LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(zone).toInstant() }.getOrNull()
            ?: runCatching { LocalDateTime.parse(value, legacy).atZone(zone).toInstant() }.getOrNull()
}

/** Apenas em memória. A hora civil inicia a referência como no app legado; os avanços posteriores são monotônicos. */
class MonotonicServerClock(
    private val elapsedMillis: () -> Long,
    wallClock: Clock = Clock.systemUTC(),
) : ServerTimeSource {
    private data class Reference(val instant: Instant, val elapsed: Long, val synchronized: Boolean)
    @Volatile private var reference = Reference(wallClock.instant(), elapsedMillis(), false)

    override fun now(): Instant {
        val snapshot = reference
        return snapshot.instant.plusMillis((elapsedMillis() - snapshot.elapsed).coerceAtLeast(0))
    }

    @Synchronized
    fun synchronize(timestamp: String): Boolean {
        val instant = LotteryDateTimes.parse(timestamp) ?: return false
        // Uma resposta antiga concluída com atraso não deve fazer a referência retroceder.
        if (reference.synchronized && instant <= reference.instant) return false
        reference = Reference(instant, elapsedMillis(), true)
        return true
    }
}
