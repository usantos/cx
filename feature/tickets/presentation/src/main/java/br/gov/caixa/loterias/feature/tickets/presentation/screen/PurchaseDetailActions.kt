package br.gov.caixa.loterias.feature.tickets.presentation.screen

import br.gov.caixa.loterias.core.domain.contract.tickets.model.Purchase

data class PurchaseDetailActions(
    val openPurchaseBetFilters: () -> Unit,
    val loadPurchase: (Purchase) -> Unit,
    val openFavoriteDialog: () -> Unit,
    val pendingPixSecondsRemaining: () -> Long,
    val checkPendingPixStatus: () -> Unit,
    val clearPurchaseBetFilters: () -> Unit,
    val openHelp: (() -> Unit)? = null,
)
