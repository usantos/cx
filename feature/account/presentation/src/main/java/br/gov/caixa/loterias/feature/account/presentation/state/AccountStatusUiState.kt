package br.gov.caixa.loterias.feature.account.presentation.state

sealed interface AccountStatusEffect {
    data object Completed : AccountStatusEffect
}
