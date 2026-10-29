package br.gov.caixa.loterias.feature.payment.presentation.formatting

import br.gov.caixa.loterias.core.common.text.TextCatalog

private object MessageResources

private val messageCatalog = TextCatalog(
    "br.gov.caixa.loterias.feature.payment.presentation.formatting.messages",
    requireNotNull(MessageResources::class.java.classLoader)
)

/** Resolve o texto do próprio módulo mantendo o chamador independente de Context. */
fun localizedMessage(key: String, vararg arguments: Any?): String = messageCatalog.text(key, *arguments)
