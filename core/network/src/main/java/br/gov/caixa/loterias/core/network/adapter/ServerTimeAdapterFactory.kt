package br.gov.caixa.loterias.core.network.adapter

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.feature.payment.data.dto.PurchaseEnvelopeDto

/** Observa o envelope CAIXA após a conversão normal do Gson; nunca consome o corpo duas vezes. */
class ServerTimeAdapterFactory(private val onServerTime: (String) -> Unit) : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType != ApiEnvelope::class.java && type.rawType != PurchaseEnvelopeDto::class.java) return null
        val delegate = gson.getDelegateAdapter(this, type)
        return object : TypeAdapter<T>() {
            override fun read(reader: JsonReader): T {
                val result = delegate.read(reader)
                val time = when (result) {
                    is ApiEnvelope<*> -> result.serverDateTime
                    is PurchaseEnvelopeDto -> result.serverDateTime
                    else -> null
                }
                time?.let(onServerTime)
                return result
            }

            override fun write(writer: JsonWriter, value: T) = delegate.write(writer, value)
        }
    }
}
