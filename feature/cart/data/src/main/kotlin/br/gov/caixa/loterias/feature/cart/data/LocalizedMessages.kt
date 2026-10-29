package br.gov.caixa.loterias.feature.cart.data

import br.gov.caixa.loterias.core.common.text.TextCatalog

private object MessageResources

private val messageCatalog = TextCatalog(
    "br.gov.caixa.loterias.feature.cart.data.messages",
    requireNotNull(MessageResources::class.java.classLoader)
)

/** Resolve os textos do próprio módulo sem introduzir dependência de Context. */
internal fun localizedMessage(key: String, vararg arguments: Any?): String = messageCatalog.text(key, *arguments)
