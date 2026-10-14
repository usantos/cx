package br.gov.caixa.loterias.apostas

/** Only the reservation confirmation returns RESULT_OK to CX Detail and closes it. */
internal fun shouldClosePoolDetailAfterChild(parentRoute: String?, contentChanged: Boolean): Boolean {
    if (!contentChanged) return false
    val path = parentRoute?.substringBefore('?') ?: return false
    return path.startsWith("betting/pool-detail/")
}

internal enum class CartNewBetReturn { Parent, Home }
internal fun cartNewBetReturn(parentRoute: String?): CartNewBetReturn =
    if (parentRoute?.substringBefore('?') == "tickets/purchase-detail") CartNewBetReturn.Home
    else CartNewBetReturn.Parent
