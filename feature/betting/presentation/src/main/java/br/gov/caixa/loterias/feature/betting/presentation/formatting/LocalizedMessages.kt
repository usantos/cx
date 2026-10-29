package br.gov.caixa.loterias.feature.betting.presentation.formatting

import br.gov.caixa.loterias.core.common.text.TextCatalog

private object MessageResources

private val messageCatalog = TextCatalog(
    "br.gov.caixa.loterias.feature.betting.presentation.formatting.messages",
    requireNotNull(MessageResources::class.java.classLoader)
)

/** Resolve o texto do próprio módulo mantendo o chamador independente de Context. */
fun localizedMessage(key: String, vararg arguments: Any?): String = messageCatalog.text(key, *arguments)

/** Captura o padrão e o idioma antes do dispatch, sem consulta ambiental durante replay. */
internal fun localizedTemplate(key: String): br.gov.caixa.loterias.core.common.text.CapturedTextTemplate {
    val locale = java.util.Locale.getDefault()
    return br.gov.caixa.loterias.core.common.text.CapturedTextTemplate(messageCatalog.text(key, locale = locale), locale.toLanguageTag())
}
