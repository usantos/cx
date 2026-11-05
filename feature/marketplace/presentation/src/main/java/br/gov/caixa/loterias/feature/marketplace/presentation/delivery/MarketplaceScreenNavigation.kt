package br.gov.caixa.loterias.feature.marketplace.presentation.delivery

import br.gov.caixa.loterias.core.domain.contract.retail.model.Retailer

data class MarketplaceScreenNavigation(
    val onBack: () -> Unit,
    val onFilter: () -> Unit,
    val onHelp: () -> Unit,
    val onRetailerSelected: (Retailer) -> Unit,
    val onPoolSelected: (String) -> Unit,
    val onCart: (() -> Unit)?,
    val onRetailerSearch: () -> Unit,
)
