package br.gov.caixa.loterias.core.domain.contract.marketplace.model.apimessages

import br.gov.caixa.loterias.core.common.text.TextCatalog

private object MessageResources

private val messageCatalog = TextCatalog(
    "br.gov.caixa.loterias.core.domain.contract.marketplace.model.apimessages.messages",
    requireNotNull(MessageResources::class.java.classLoader)
)

/** Resolve os textos do contrato público sem depender de recursos Android. */
fun localizedMessage(key: String, vararg arguments: Any?): String = messageCatalog.text(key, *arguments)
