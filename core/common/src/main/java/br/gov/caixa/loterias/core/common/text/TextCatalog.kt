package br.gov.caixa.loterias.core.common.text

import java.util.Locale
import java.util.ResourceBundle

/** Resolve mensagens do módulo sem vincular regras ou ViewModels às APIs de recursos do Android. */
class TextCatalog(private val baseName: String, private val loader: ClassLoader) {
    /** Consulta o idioma a cada chamada; não armazena traduções em estado global. */
    fun text(key: String, vararg arguments: Any?, locale: Locale = Locale.getDefault()): String {
        val pattern = ResourceBundle.getBundle(baseName, locale, loader).getString(key)
        return if (arguments.isEmpty()) pattern else String.format(locale, pattern, *arguments)
    }
}
