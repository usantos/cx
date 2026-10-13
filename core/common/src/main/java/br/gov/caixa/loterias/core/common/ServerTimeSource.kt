package br.gov.caixa.loterias.core.common

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

    /** Backend timestamps without an offset are explicitly in São Paulo time. */
    fun parse(value: String): Instant? =
        runCatching { OffsetDateTime.parse(value).toInstant() }.getOrNull()
            ?: runCatching { LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(zone).toInstant() }.getOrNull()
            ?: runCatching { LocalDateTime.parse(value, legacy).atZone(zone).toInstant() }.getOrNull()
}

/** In-memory only. Wall time seeds startup as in the legacy app; later ticks are monotonic. */
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
        // An older response completing late must not move the reference backwards.
        if (reference.synchronized && instant <= reference.instant) return false
        reference = Reference(instant, elapsedMillis(), true)
        return true
    }
}
