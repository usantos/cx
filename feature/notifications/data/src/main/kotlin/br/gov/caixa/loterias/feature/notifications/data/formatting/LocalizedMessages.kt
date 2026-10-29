package br.gov.caixa.loterias.feature.notifications.data.formatting

import br.gov.caixa.loterias.core.common.text.TextCatalog

private object MessageResources

private val messageCatalog = TextCatalog(
    "br.gov.caixa.loterias.feature.notifications.data.formatting.messages",
    requireNotNull(MessageResources::class.java.classLoader)
)

/** Resolve os rótulos locais sem introduzir recursos Android nos modelos de domínio. */
internal fun localizedMessage(key: String): String = messageCatalog.text(key)
