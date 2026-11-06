package br.gov.caixa.loterias.core.navigation.policy

/** Somente a confirmação da reserva retorna RESULT_OK ao detalhe do CX e o fecha. */
fun shouldClosePoolDetailAfterChild(parentRoute: String?, contentChanged: Boolean): Boolean {
    if (!contentChanged) return false
    val path = parentRoute?.substringBefore('?') ?: return false
    return path.startsWith("betting/pool-detail/")
}
