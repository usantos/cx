package br.gov.caixa.loterias.feature.support.presentation.dynamic

/** Resolve somente a entrada tipada conhecida, depois da confirmação da instalação. */
fun interface SupportFaqEntryLoader {
    fun load(context: android.content.Context): SupportFaqEntry?
}
