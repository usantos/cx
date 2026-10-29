package br.gov.caixa.loterias.core.common.config

import java.io.Closeable
import java.util.IdentityHashMap

/** Notifica consumidores da mesma instância de configuração, sem depender do SDK de plataforma. */
object ConfigurationActivationEvents {
    private val lock = Any()
    private val observers = IdentityHashMap<Any, MutableSet<() -> Unit>>()

    fun observe(source: Any, observer: () -> Unit): Closeable {
        synchronized(lock) { observers.getOrPut(source) { linkedSetOf() }.add(observer) }
        return Closeable {
            synchronized(lock) {
                observers[source]?.let { entries ->
                    entries.remove(observer)
                    if (entries.isEmpty()) observers.remove(source)
                }
            }
        }
    }

    fun publish(source: Any) {
        val entries = synchronized(lock) { observers[source]?.toList().orEmpty() }
        entries.forEach { it() }
    }
}
