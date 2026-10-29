package br.gov.caixa.loterias.feature.home.presentation.coordinator

/** Comandos coesos expostos pelo ViewModel da Home para o estado de resultado sob sua responsabilidade. */
interface HomeResultActions {
    fun toggle(page: Int, modality: String)
    fun load(modalities: List<String>, refresh: Boolean = false)
    fun close()
    fun dismissError()
}
