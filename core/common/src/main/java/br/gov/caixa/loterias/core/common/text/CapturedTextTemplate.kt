package br.gov.caixa.loterias.core.common.text

import java.util.Locale

/** Padrão e idioma capturados na ação; a formatação não consulta o ambiente do processo. */
data class CapturedTextTemplate(val pattern: String, val localeTag: String) {
    fun format(vararg arguments: Any?): String = String.format(Locale.forLanguageTag(localeTag), pattern, *arguments)
}
